package com.appcuesreactnative

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.appcues.AppcuesFrameView
import com.facebook.react.bridge.Arguments
import com.facebook.react.uimanager.StateWrapper
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.PixelUtil
import com.facebook.react.uimanager.UIManagerModule

internal class AppcuesWrapperFragment : Fragment() {
    private var frameID: String? = null
    private var frameView: AppcuesFrameView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        frameView = AppcuesFrameView(requireContext())
        return frameView!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        frameID?.let { id ->
            frameView?.let { view ->
                AppcuesReactNativeModule.implementation?.registerEmbed(id, view)
            }
        }
    }

    fun setFrameID(frameID: String) {
        this.frameID = frameID
        frameView?.let { view ->
            AppcuesReactNativeModule.implementation?.registerEmbed(frameID, view)
        }
    }
}

class AppcuesFrameWrapperView(context: Context) : FrameLayout(context) {
    private var wrapperFragment: AppcuesWrapperFragment? = null
    private var stateWrapper: StateWrapper? = null
    private var frameID: String? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        post(addFragment)
    }

    override fun onDetachedFromWindow() {
        // cancel any callbacks still sitting on the handler queue. If a posted
        // measureAndLayout runs after this view has left the window, measuring the child
        // ComposeView throws from AbstractComposeView.onMeasure:
        //   IllegalStateException: Cannot locate windowRecomposer; View ... is not attached to a window
        removeCallbacks(measureAndLayout)
        removeCallbacks(addFragment)
        super.onDetachedFromWindow()
    }

    private val addFragment = Runnable {
        // the view may have been detached between the post and this callback running
        if (!isAttachedToWindow) return@Runnable

        try {
          // if the fragment exists and its view is still our child there is nothing to do.
          // When a host screen is detached and later re-attached the fragment survives but its
          // view is no longer parented here, so the transaction needs to run again - otherwise
          // the frame stays permanently empty.
          if (wrapperFragment != null && childCount > 0) return@Runnable

          if (wrapperFragment == null) {
            wrapperFragment = AppcuesWrapperFragment()
            frameID?.let { wrapperFragment?.setFrameID(it) }
          }

          val activity = (context as? ThemedReactContext)?.currentActivity as? FragmentActivity
            ?: return@Runnable

          activity.supportFragmentManager
            .beginTransaction()
            // the id value here is the react native view id that
            // has been assigned by the view manager system for this view instance
            .replace(id, wrapperFragment!!, id.toString())
            // allowing state loss here as the transaction can land after onSaveInstanceState,
            // where commitNow would throw. Skipping an embed is preferable to crashing.
            .commitNowAllowingStateLoss()
        } catch (_: Exception) {
          // should not get any exceptions here, but in case the transaction fails to put the fragment in place
          // we rather exit silent instead of crashing the app.
        }
    }

    override fun requestLayout() {
        super.requestLayout()
        // nothing to measure while detached, and requestLayout can be called several times per
        // frame - remove any pending callback first so only a single pass is queued.
        if (!isAttachedToWindow) return
        removeCallbacks(measureAndLayout)
        post(measureAndLayout)
    }

    private val measureAndLayout = Runnable {
        // the view may have been detached between the post and this callback running
        if (!isAttachedToWindow) return@Runnable

        measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        )
        layout(left, top, right, bottom)
    }

    fun setStateWrapper(stateWrapper: StateWrapper?) {
        this.stateWrapper = stateWrapper
    }

    fun setFrameID(frameID: String) {
        this.frameID = frameID
        wrapperFragment?.setFrameID(frameID)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // wait until the fragment has been embedded into the view and the
        // children are ready to measure - else it will give a (0,0) size and
        // not layout correctly.
        //
        // Also in case fragment is not created, as a safe-guard, we should
        // skip the proper measuring of the view.
        //
        // The same applies while detached - measuring a child ComposeView that is not attached
        // to a window throws, and the fragment and children are both still present at that
        // point, so those checks alone do not cover it.
        if (children.count() == 0 || wrapperFragment == null || !isAttachedToWindow) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }

        var maxWidth = 0
        var maxHeight = 0

        children.forEach {
            it.measure(widthMeasureSpec, MeasureSpec.UNSPECIFIED)
            maxWidth = maxWidth.coerceAtLeast(it.measuredWidth)
            maxHeight = maxHeight.coerceAtLeast(it.measuredHeight)
        }

        val finalWidth = maxWidth.coerceAtLeast(suggestedMinimumWidth)
        val finalHeight = maxHeight.coerceAtLeast(suggestedMinimumHeight)
        setMeasuredDimension(finalWidth, finalHeight)

        stateWrapper?.let {
            // New Arch
            it.updateState(Arguments.createMap().apply {
                putDouble("frameWidth", finalWidth.pxToDp())
                putDouble("frameHeight", finalHeight.pxToDp())
            })
        }
        ?: run {
            // Old Arch
            (context as? ThemedReactContext)?.let { themedReactContext ->
                themedReactContext.runOnNativeModulesQueueThread {
                    themedReactContext.getNativeModule(UIManagerModule::class.java)
                    ?.updateNodeSize(id, finalWidth, finalHeight)
                }
            }
        }
    }

    // PixelUtil.pxToDp is only available in RN 0.76+
    private fun Int.pxToDp(): Double {
        return PixelUtil.toDIPFromPixel(this.toFloat()).toDouble()
    }
}
