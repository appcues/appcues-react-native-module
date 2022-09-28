package com.appcuesreactnative

import android.content.Intent
import android.net.Uri
import com.appcues.AnalyticType
import com.appcues.AnalyticsListener
import com.appcues.Appcues
import com.appcues.LoggingLevel
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule
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

                this.analyticsListener = object: AnalyticsListener {
                    override fun trackedAnalytic(type: AnalyticType, value: String?, properties: Map<String, Any>?, isInternal: Boolean) {
                        val params = Arguments.createMap().apply {
                            putString("analytic", type.name)
                            putString("value", value ?: "")
                            putMap("properties", writableMapOf(properties ?: emptyMap<String, Any>()))
                            putBoolean("isInternal", isInternal)
                        }

                        reactApplicationContext
                            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                            .emit("analytics", params)
                    }
                }
            }
        }
    }

    private fun writableMapOf(values: Map<*, *>): WritableMap {
        val map = Arguments.createMap()
        for ((anyKey, value) in values) {
            val key = anyKey as? String ?: continue
            when (value) {
                null -> map.putNull(key)
                is Boolean -> map.putBoolean(key, value)
                is Double -> map.putDouble(key, value)
                // The Android SDK passes dates as a Long Unix timestamp
                is Long -> map.putDouble(key, value.toDouble())
                is Int -> map.putInt(key, value)
                is String -> map.putString(key, value)
                is WritableMap -> map.putMap(key, value)
                is WritableArray -> map.putArray(key, value)
                is Map<*, *> -> map.putMap(key, writableMapOf(value))
                is List<*> -> map.putArray(key, writableArrayOf(value))
                else -> map.putNull(key)
            }
        }
        return map
    }

    private fun writableArrayOf(values: List<*>): WritableArray {
        val array = Arguments.createArray()
        for (value in values) {
            when (value) {
                null -> array.pushNull()
                is Boolean -> array.pushBoolean(value)
                is Double -> array.pushDouble(value)
                // The Android SDK passes dates as a Long Unix timestamp
                is Long -> array.pushDouble(value.toDouble())
                is Int -> array.pushInt(value)
                is String -> array.pushString(value)
                is WritableMap -> array.pushMap(value)
                is WritableArray -> array.pushArray(value)
                is Map<*, *> -> array.pushMap(writableMapOf(value))
                is List<*> -> array.pushArray(writableArrayOf(value))
                else -> array.pushNull()
            }
        }
        return array
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
    fun group(groupID: String?, properties: ReadableMap? = null) {
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
    fun show(experienceID: String, promise: Promise) {
        mainScope.launch {
            val success = implementation.show(experienceID)
            if (success) {
                promise.resolve(null)
            } else {
                promise.reject("show-experience-failure", "unable to show experience $experienceID")
            }
        }
    }

    @ReactMethod
    fun debug() {
        currentActivity?.let {
            implementation.debug(it)
        }
    }

    @ReactMethod
    fun didHandleURL(url: String, promise: Promise) {
        val activity = currentActivity
        val uri = Uri.parse(url)
        if (activity != null) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = uri
            promise.resolve(implementation.onNewIntent(activity, intent))
        } else {
            promise.reject("no-activity", "unable to handle the URL, no current running Activity found")
        }
    }

    @ReactMethod
    fun addListener(eventName: String) {
        // Required for RN built in Event Emitter Calls.
    }

    @ReactMethod
    fun removeListeners(count: Int) {
        // Required for RN built in Event Emitter Calls.
    }
}
