package com.appcuesreactnative

import android.content.Context
import android.widget.FrameLayout
import androidx.core.view.children
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

internal class AppcuesWrapperView(context: Context) : FrameLayout(context) {
    val contentView: AppcuesFrameView = AppcuesFrameView(context)

    init {
        addView(contentView)
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
        var maxWidth = 0
        var maxHeight = 0
        children.forEach {
            if (it.visibility != GONE) {
                it.measure(widthMeasureSpec, MeasureSpec.UNSPECIFIED)
                maxWidth = maxWidth.coerceAtLeast(it.measuredWidth)
                maxHeight = maxHeight.coerceAtLeast(it.measuredHeight)
            }
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