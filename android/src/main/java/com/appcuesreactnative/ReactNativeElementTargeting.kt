package com.appcuesreactnative

import android.app.Activity
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.inspector.WindowInspector
import androidx.core.view.children
import com.appcues.ElementSelector
import com.appcues.ElementTargetingStrategy
import com.appcues.ViewElement
import com.appcues.isAppcuesView
import com.facebook.react.R
import java.lang.reflect.Method

internal data class ReactNativeViewSelector(var nativeId: String?, var testId: String?): ElementSelector {

    val isValid: Boolean
        get() = nativeId != null || testId != null

    val displayName: String?
        get() = when {
            nativeId != null -> nativeId
            testId != null -> testId
            else -> null
        }

    override fun toMap(): Map<String, String> =
        mapOf(
            "nativeID" to nativeId,
            "testID" to testId
        ).filterValues { it != null }.mapValues { it.value as String }

    override fun evaluateMatch(target: ElementSelector): Int {
        var weight = 0

        (target as? ReactNativeViewSelector)?.let {
            if (!it.nativeId.isNullOrEmpty() && it.nativeId == nativeId) {
                weight += 1_000
            }

            if (!it.testId.isNullOrEmpty() && it.testId == testId) {
                weight += 1_000
            }
        }

        return weight
    }
}

internal fun Activity.getParentView(): ViewGroup {

    // try to find the most applicable topmost decorView to capture layout. Typically there is just a single
    // decorView on the Activity window. However, if something like a dialog modal has been shown, this can add another
    // window with another decorView on top of the Activity. If we want to support showing content above that layer, we need
    // to find the topmost decorView like below.

    val decorView = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // this is the preferred method on API 29+ with the new WindowInspector function
        WindowInspector.getGlobalWindowViews().last()
    } else {
        @Suppress("SwallowedException", "TooGenericExceptionCaught")
        try {
            // this is the less desirable method for API 21-28, using reflection to try to get the root views
            val windowManagerClass = Class.forName("android.view.WindowManagerGlobal")
            val windowManager = windowManagerClass.getMethod("getInstance").invoke(null)
            val getViewRootNames: Method = windowManagerClass.getMethod("getViewRootNames")
            val getRootView: Method = windowManagerClass.getMethod("getRootView", String::class.java)
            val rootViewNames = getViewRootNames.invoke(windowManager) as Array<Any?>
            val rootViews = rootViewNames.map { getRootView(windowManager, it) as View }
            rootViews.last()
        } catch (_: Exception) {
            // if all else fails, use the decorView on the window, which is typically the only one
            window.decorView
        }
    }

    return decorView.rootView as ViewGroup
}

internal class ReactNativeViewTargeting(
    private val module: AppcuesReactNativeModule
): ElementTargetingStrategy {

    override fun captureLayout(): ViewElement? {
        return module.activity?.getParentView()?.let {
            val screenBounds = Rect()
            it.getGlobalVisibleRect(screenBounds)
            it.asCaptureView(screenBounds)
        }
    }

    override fun inflateSelectorFrom(properties: Map<String, String>): ElementSelector? {
        return ReactNativeViewSelector(
            nativeId = properties["nativeID"],
            testId = properties["testID"]
        ).let { if (it.isValid) it else null }
    }
}

private fun View.asCaptureView(screenBounds: Rect): ViewElement? {
    val displayMetrics = context.resources.displayMetrics
    val density = displayMetrics.density

    // this is the position of the view relative to the entire screen
    val actualPosition = Rect()
    getGlobalVisibleRect(actualPosition)

    // if the view is not currently in the screenshot image (scrolled away), ignore
    if (Rect.intersects(actualPosition, screenBounds).not()) {
        return null
    }

    // ignore the Appcues SDK content that has been injected into the view hierarchy
    if (this.isAppcuesView()) {
        return null
    }

    var children = (this as? ViewGroup)?.children?.mapNotNull {
        if (!it.isShown) {
            // discard hidden views and subviews within
            null
        } else {
            it.asCaptureView(screenBounds)
        }
    }?.toList()

    if (children?.isEmpty() == true) {
        children = null
    }

    val selector = selector()

    return ViewElement(
        x = actualPosition.left.toDp(density),
        y = actualPosition.top.toDp(density),
        width = actualPosition.width().toDp(density),
        height = actualPosition.height().toDp(density),
        selector = selector,
        displayName = selector?.displayName,
        type = this.javaClass.name,
        children = children,
    )
}

internal fun View.selector(): ReactNativeViewSelector? {
    return ReactNativeViewSelector(
        nativeId = getTag(R.id.view_tag_native_id)?.toString(),
        // a "testID" set on a react native view will come in to the Android View in the
        // tag property
        testId = tag as? String
    ).let { if (it.isValid) it else null }
}

private fun Int.toDp(density: Float) =
    (this / density).toInt()
