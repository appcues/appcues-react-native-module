package com.appcuesreactnativesdk

import com.appcues.Appcues
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap

class AppcuesReactNativeSdkModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    private lateinit var implementation: Appcues

    override fun getName(): String {
        return "AppcuesReactNativeSdk"
    }

    @ReactMethod
    fun setup(accountID: String, applicationID: String) {
        val context = reactApplicationContextIfActiveOrWarn
        val activity = currentActivity
        if (context != null && activity != null) {
          implementation = Appcues.Builder(context, accountID, applicationID)
            .activity(activity)
            .logging(Appcues.LoggingLevel.BASIC).build()
        }
    }

    @ReactMethod
    fun identify(userID: String, properties: ReadableMap? = null) {
        implementation.identify(userID, properties?.toHashMap())
    }

    @ReactMethod
    fun screen(title: String, properties: ReadableMap? = null) {
        implementation.screen(title, properties?.toHashMap())
    }

    @ReactMethod
    fun track(name: String, properties: ReadableMap? = null) {
        implementation.track(name, properties?.toHashMap())
    }

    @ReactMethod
    fun show(experienceID: String) {
        currentActivity?.runOnUiThread {
            implementation.show(experienceID)
        }
    }

    @ReactMethod
    fun debug() {
        currentActivity?.runOnUiThread {
            implementation.debug()
        }
    }
}
