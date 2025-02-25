package com.appcuesreactnative

import com.facebook.react.BaseReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.model.ReactModuleInfo
import com.facebook.react.module.model.ReactModuleInfoProvider
import com.facebook.react.uimanager.ViewManager

class AppcuesReactNativePackage : BaseReactPackage() {

    override fun getModule(name: String, reactContext: ReactApplicationContext): NativeModule? {
        return if (name == AppcuesReactNativeModule.NAME) {
            AppcuesReactNativeModule(reactContext)
        } else {
            null
        }
    }

    override fun getReactModuleInfoProvider() = ReactModuleInfoProvider {
        mapOf(
            AppcuesReactNativeModule.NAME to ReactModuleInfo(
                AppcuesReactNativeModule.NAME,
                AppcuesReactNativeModule.NAME,
                false,
                false,
                false,
                BuildConfig.IS_NEW_ARCHITECTURE_ENABLED
            )
        )
    }

    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
        return listOf(AppcuesFrameViewManager(reactContext))
    }
}
