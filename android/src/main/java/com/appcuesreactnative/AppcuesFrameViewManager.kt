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
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.UIManagerModule
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.annotations.ReactProp


internal class AppcuesFrameViewManager: ViewGroupManager<AppcuesWrapperView>() {

    override fun getName() = REACT_CLASS

    companion object {
        const val REACT_CLASS = "AppcuesFrameView"
    }

    override fun createViewInstance(context: ThemedReactContext): AppcuesWrapperView {
        return AppcuesWrapperView(context)
    }

    @ReactProp(name = "frameID")
    fun setFrameId(view: AppcuesWrapperView, frameId: String) {
        AppcuesReactNativeModule.implementation?.registerEmbed(frameId, view.contentView)
    }
}

internal class AppcuesWrapperFragment(private var frame: AppcuesFrameView) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return frame
    }
}

internal class AppcuesWrapperView(context: Context) : FrameLayout(context) {
    val contentView: AppcuesFrameView = AppcuesFrameView(context)

    private var fragmentCreated = false

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        post(addFragment)
    }

    private val addFragment = Runnable {
        if (!fragmentCreated) {
          try {
            val wrapperFragment = AppcuesWrapperFragment(contentView)
            val activity = (context as? ThemedReactContext)?.currentActivity as FragmentActivity
            activity.supportFragmentManager
              .beginTransaction()
              // the id value here is the react native view id that
              // has been assigned by the view manager system for this view instance
              .replace(id, wrapperFragment, id.toString())
              .commitNow()

            fragmentCreated = true
          } catch (_: Exception) {
            // should not get any exceptions here, but in case the transaction fails to put the fragment in place
            // we rather exit silent instead of crashing the app.
          }
        }
    }

    override fun requestLayout() {
        super.requestLayout()
        post(measureAndLayout)
    }

    private val measureAndLayout = Runnable {
        measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        )
        layout(left, top, right, bottom)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        // wait until the fragment has been embedded into the view and the
        // children are ready to measure - else it will give a (0,0) size and
        // not layout correctly
        if (children.count() == 0) {
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
        (context as? ThemedReactContext)?.let { themedReactContext ->
            themedReactContext.runOnNativeModulesQueueThread {
                themedReactContext.getNativeModule(UIManagerModule::class.java)
                    ?.updateNodeSize(id, finalWidth, finalHeight)
            }
        }
    }
}
