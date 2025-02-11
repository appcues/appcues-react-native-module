package com.appcuesreactnative

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.Promise

abstract class AppcuesReactNativeSpec internal constructor(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {
  abstract fun setup(accountID: String, applicationID: String, options: ReadableMap?, promise: Promise)
  abstract fun identify(userID: String, properties: ReadableMap?)
  abstract fun reset()
  abstract fun anonymous()
  abstract fun group(groupID: String?, properties: ReadableMap?)
  abstract fun screen(title: String, properties: ReadableMap?)
  abstract fun track(name: String, properties: ReadableMap?)
  abstract fun show(experienceID: String, promise: Promise)
  abstract fun debug()
  abstract fun didHandleURL(url: String, promise: Promise)

  abstract fun addListener(eventName: String)
  abstract fun removeListeners(count: Double)
}
