package com.appcuesreactnative

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp

internal class AppcuesFrameViewManager(var context: ReactApplicationContext): SimpleViewManager<AppcuesFrameWrapperView>() {

    override fun getName(): String {
        return AppcuesFrameViewManager.NAME
    }

    override fun createViewInstance(context: ThemedReactContext): AppcuesFrameWrapperView {
        return AppcuesFrameWrapperView(context)
    }

    @ReactProp(name = "frameID")
    fun setFrameId(view: AppcuesFrameWrapperView, frameId: String) {
        view.setFrameID(frameId)
    }

    companion object {
        const val NAME = "AppcuesFrameView"
    }
}
