package com.appcuesreactnative

import com.appcues.Appcues
import com.appcues.LoggingLevel
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppcuesReactNativeModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    private lateinit var implementation: Appcues

    private val mainScope = CoroutineScope(Dispatchers.Main)

    override fun getName(): String {
        return "AppcuesReactNative"
    }

    @ReactMethod
    fun setup(accountID: String, applicationID: String, options: ReadableMap? = null) {
        val context = reactApplicationContextIfActiveOrWarn
        val activity = currentActivity
        if (context != null && activity != null) {
            implementation = Appcues(context, accountID, applicationID) {
                options?.toHashMap()?.let {

                    val logging = it["logging"] as? Boolean
                    if (logging != null) {
                        this.loggingLevel = if (logging) LoggingLevel.INFO else LoggingLevel.NONE
                    }

                    val apiHost = it["apiHost"] as? String
                    if (apiHost != null) {
                        this.apiBasePath = apiHost
                    }

                    val sessionTimeout = it["sessionTimeout"] as? Double
                    if (sessionTimeout != null) {
                        this.sessionTimeout = sessionTimeout.toInt()
                    }

                    val activityStorageMaxSize = it["activityStorageMaxSize"] as? Double
                    if (activityStorageMaxSize != null) {
                        this.activityStorageMaxSize = activityStorageMaxSize.toInt()
                    }

                    val activityStorageMaxAge = it["activityStorageMaxAge"] as? Double
                    if (activityStorageMaxAge != null) {
                        this.activityStorageMaxAge = activityStorageMaxAge.toInt()
                    }
                }
            }
        }
    }

    @ReactMethod
    fun identify(userID: String, properties: ReadableMap? = null) {
        implementation.identify(userID, properties?.toHashMap())
    }

    @ReactMethod
    fun reset() {
        implementation.reset()
    }

    @ReactMethod
    fun anonymous() {
        implementation.anonymous()
    }

    @ReactMethod
    fun group(groupID: String, properties: ReadableMap? = null) {
        implementation.group(groupID, properties?.toHashMap())
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
        mainScope.launch {
          implementation.show(experienceID)
        }
    }

    @ReactMethod
    fun debug() {
        currentActivity?.let {
          implementation.debug(it)
        }
    }
}
