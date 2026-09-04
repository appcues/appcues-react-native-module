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

    fun dispose() {
        val fragment = wrapperFragment
        wrapperFragment = null

        if (fragment?.isAdded != true) return

        try {
            val fragmentManager = fragment.parentFragmentManager
            if (!fragmentManager.isDestroyed) {
                fragmentManager.beginTransaction()
                    .remove(fragment)
                    .commitNowAllowingStateLoss()
            }
        } catch (_: Exception) {
            // The host may already be tearing down. Do not let native-view disposal crash it.
        }
    }

    private val addFragment = Runnable {
        // the view may have been detached between the post and this callback running
        if (!isAttachedToWindow) return@Runnable

        // Retain the fragment only while its view remains parented here. FragmentManager
        // optimizes a replace with the same added fragment away, so it cannot restore a
        // destroyed child view. A fresh fragment makes this transaction recreate the view.
        if (wrapperFragment != null && childCount > 0) return@Runnable

        val activity = (context as? ThemedReactContext)?.currentActivity as? FragmentActivity
            ?: return@Runnable

        // isAttachedToWindow is not enough. FragmentStateManager resolves the container with
        // findViewById on the activity, so a view attached to a different window - an RN Modal
        // renders into its own Dialog window - is never found. Committing anyway adds the
        // fragment to the store and then throws, and the next state pass over the store throws
        // again from createView(), killing the activity.
        if (activity.findViewById<View?>(id) !== this) return@Runnable

        val fragment = AppcuesWrapperFragment()
        frameID?.let { fragment.setFrameID(it) }
        val fragmentManager = activity.supportFragmentManager

        try {
            fragmentManager
                .beginTransaction()
                // the id value here is the react native view id that
                // has been assigned by the view manager system for this view instance
                .replace(id, fragment, id.toString())
                // allowing state loss here as the transaction can land after onSaveInstanceState,
                // where commitNow would throw. Skipping an embed is preferable to crashing.
                .commitNowAllowingStateLoss()
            wrapperFragment = fragment
        } catch (_: Exception) {
            // commitNow adds the fragment to the store before it can fail, so failing here
            // without removing it leaves a fragment that every later state pass tries to
            // create a view for. Exiting silently is only safe once it is gone.
            wrapperFragment = null
            try {
                fragmentManager.beginTransaction()
                    .remove(fragment)
                    .commitNowAllowingStateLoss()
            } catch (_: Exception) {
            }
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
        // Wait until the fragment has been embedded into the view and the children are ready
        // to measure; otherwise it reports a (0, 0) size and does not lay out correctly.
        if (children.count() == 0 || wrapperFragment == null) {
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
