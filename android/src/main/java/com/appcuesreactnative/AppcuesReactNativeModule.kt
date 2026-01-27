package com.appcuesreactnative

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.appcues.AnalyticType
import com.appcues.AnalyticsListener
import com.appcues.Appcues
import com.appcues.LoggingLevel
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppcuesReactNativeModule internal constructor(private val reactContext: ReactApplicationContext) :
    AppcuesReactNativeSpec(reactContext) {

    companion object {
        const val NAME = "AppcuesReactNative"
        var implementation: Appcues? = null
    }

    private val mainScope = CoroutineScope(Dispatchers.Main)

    val activity: Activity?
      get() = currentActivity

    override fun getName(): String {
        return NAME
    }

    @ReactMethod
    override fun setup(
        accountID: String,
        applicationID: String,
        options: ReadableMap?,
        promise: Promise
    ) {
        val context = reactApplicationContextIfActiveOrWarn
        if (context == null) {
            promise.reject("no-context", "unable to initialize the SDK, no Application Context found")
            return
        }
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

                val settingsHost = it["settingsHost"] as? String
                if (settingsHost != null) {
                    this.apiSettingsPath = settingsHost
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

                val autoPropsFromOptions = it["additionalAutoProperties"] as? HashMap<String, Any>
                if (autoPropsFromOptions != null) {
                    this.additionalAutoProperties = autoPropsFromOptions
                }
            }

            this.analyticsListener = object: AnalyticsListener {
                override fun trackedAnalytic(
                  type: AnalyticType,
                  value: String?,
                  properties: Map<String, Any>?,
                  isInternal: Boolean
                ) {
                    val params = Arguments.createMap().apply {
                        putString("analytic", type.name)
                        putString("value", value ?: "")
                        putMap("properties", readableMapOf(properties ?: emptyMap<String, Any>()))
                        putBoolean("isInternal", isInternal)
                    }

                    reactApplicationContext
                        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                        .emit("analytics", params)
                }
            }
        }

        Appcues.elementTargeting = ReactNativeViewTargeting(this@AppcuesReactNativeModule)

        // since a native module makes native calls asynchronously, we use a Promise here to allow callers to
        // be able to reliably know when initialization is complete and subsequent SDK calls can continue.
        promise.resolve(null)
    }

    private fun readableMapOf(values: Map<*, *>): ReadableMap {
        val map = Arguments.createMap()
        for ((anyKey, value) in values) {
            val key = anyKey as? String ?: continue
            when (value) {
                null -> map.putNull(key)
                is Boolean -> map.putBoolean(key, value)
                is Double -> map.putDouble(key, value)
                is Int -> map.putInt(key, value)
                is String -> map.putString(key, value)
                is WritableMap -> map.putMap(key, value)
                is WritableArray -> map.putArray(key, value)
                is Map<*, *> -> map.putMap(key, readableMapOf(value))
                is List<*> -> map.putArray(key, readableArrayOf(value))
                else -> map.putNull(key)
            }
        }
        return map
    }

    private fun readableArrayOf(values: List<*>): ReadableArray {
        val array = Arguments.createArray()
        for (value in values) {
            when (value) {
                null -> array.pushNull()
                is Boolean -> array.pushBoolean(value)
                is Double -> array.pushDouble(value)
                is Int -> array.pushInt(value)
                is String -> array.pushString(value)
                is WritableMap -> array.pushMap(value)
                is WritableArray -> array.pushArray(value)
                is Map<*, *> -> array.pushMap(readableMapOf(value))
                is List<*> -> array.pushArray(readableArrayOf(value))
                else -> array.pushNull()
            }
        }
        return array
    }

    @ReactMethod
    override fun identify(userID: String, properties: ReadableMap?) {
        implementation?.identify(userID, toMap(properties))
    }

    @ReactMethod
    override fun reset() {
        implementation?.reset()
    }

    @ReactMethod
    override fun anonymous() {
        implementation?.anonymous()
    }

    @ReactMethod
    override fun group(groupID: String?, properties: ReadableMap?) {
        implementation?.group(groupID, toMap(properties))
    }

    @ReactMethod
    override fun screen(title: String, properties: ReadableMap?) {
        implementation?.screen(title, toMap(properties))
    }

    @ReactMethod
    override fun track(name: String, properties: ReadableMap?) {
        implementation?.track(name, toMap(properties))
    }

    @ReactMethod
    override fun show(experienceID: String, promise: Promise) {
        mainScope.launch {
            val success = implementation?.show(experienceID) ?: false
            if (success) {
                promise.resolve(null)
            } else {
                promise.reject("show-experience-failure", "unable to show experience $experienceID")
            }
        }
    }

    @ReactMethod
    override fun debug() {
        currentActivity?.let {
            implementation?.debug(it)
        }
    }

    @ReactMethod
    override fun didHandleURL(url: String, promise: Promise) {
        val activity = currentActivity
        val uri = Uri.parse(url)
        if (activity != null) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = uri
            promise.resolve(implementation?.onNewIntent(activity, intent) ?: false)
        } else {
            promise.reject("no-activity", "unable to handle the URL, no current running Activity found")
        }
    }

    @ReactMethod
    override fun addListener(eventName: String) {
        // Required for RN built in Event Emitter Calls.
    }

    @ReactMethod
    override fun removeListeners(count: Double) {
        // Required for RN built in Event Emitter Calls.
    }

    private fun toMap(readableMap: ReadableMap?): Map<String, Any>? {
        // RN 0.77 .toHashMap() now returns HashMap<String, Any?>
        return readableMap
            ?.toHashMap()
            ?.filterValues { it != null }
            ?.mapValues { it.value as Any }
    }
}
