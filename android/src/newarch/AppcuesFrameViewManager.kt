package com.appcuesreactnative

import android.graphics.Color
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.uimanager.ReactStylesDiffMap
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.StateWrapper
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewManagerDelegate
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.viewmanagers.AppcuesFrameViewManagerInterface
import com.facebook.react.viewmanagers.AppcuesFrameViewManagerDelegate

@ReactModule(name = AppcuesFrameViewManager.NAME)
class AppcuesFrameViewManager(reactContext: ReactApplicationContext) : SimpleViewManager<AppcuesFrameWrapperView>(),
 AppcuesFrameViewManagerInterface<AppcuesFrameWrapperView> {
  private val mDelegate: ViewManagerDelegate<AppcuesFrameWrapperView> =
    AppcuesFrameViewManagerDelegate(this)

  override fun getDelegate(): ViewManagerDelegate<AppcuesFrameWrapperView> {
    return mDelegate
  }

  override fun getName(): String {
    return AppcuesFrameViewManager.NAME
  }

  public override fun createViewInstance(context: ThemedReactContext): AppcuesFrameWrapperView {
    return AppcuesFrameWrapperView(context)
  }

  override fun updateState(view: AppcuesFrameWrapperView, props: ReactStylesDiffMap?, stateWrapper: StateWrapper?): Any? {
    view.setStateWrapper(stateWrapper)
    return super.updateState(view, props, stateWrapper)
  }

  @ReactProp(name = "frameID")
  override fun setFrameID(view: AppcuesFrameWrapperView?, frameID: String?) {
    if (view == null || frameID == null) { return }

    view.setFrameID(frameID)
  }

  companion object {
    const val NAME = "AppcuesFrameView"
  }
}
