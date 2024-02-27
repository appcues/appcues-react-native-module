package com.appcuesreactnative

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Rect
import android.os.Build
import android.util.Log
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

@Suppress("UNCHECKED_CAST")
@SuppressLint("PrivateApi")
internal fun Activity.getParentView(): ViewGroup {
    // try to find the most applicable decorView to inject Appcues content into. Typically there is just a single
    // decorView on the Activity window. However, if something like a dialog modal has been shown, this can add another
    // window with another decorView on top of the Activity. If we want to support showing content above that layer, we need
    // to find the top most decorView like below.
    val decorView = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // this is the preferred method on API 29+ with the new WindowInspector function
        // in case of multiple views, get the one that is hosting android.R.id.content
        // we get the last one because sometimes stacking activities might be listed in this method,
        // and we always want the one that is on top
        WindowInspector.getGlobalWindowViews().findTopMost() ?: window.decorView
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
            rootViews.findTopMost() ?: window.decorView
          } catch (ex: Exception) {
            Log.e("Appcues", "error getting decorView, ${ex.message}")
            // if all else fails, use the decorView on the window, which is typically the only one
            window.decorView
          }
    }

    return decorView.rootView as ViewGroup
}

private fun List<View>.findTopMost() = lastOrNull { it.findViewById<View?>(android.R.id.content) != null }

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

    // the coordinates of the non-clipped area of this view in the coordinate space of the view's root view
    val globalVisibleRect = Rect()

    if (
    // ignore the Appcues SDK content that has been injected into the view hierarchy
        this.isAppcuesView() ||
        // if getGlobalVisibleRect returns false, that indicates that none of the view is
        // visible within the root view, and we will not include it in our capture
        getGlobalVisibleRect(globalVisibleRect).not() ||
        // if the view is not currently in the screenshot image (scrolled away), ignore
        // (this is possibly a redundant check to item above, but keeping for now)
        Rect.intersects(globalVisibleRect, screenBounds).not()
    ) {
        // if any of these conditions failed, this view is not captured
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
        x = globalVisibleRect.left.toDp(density),
        y = globalVisibleRect.top.toDp(density),
        width = globalVisibleRect.width().toDp(density),
        height = globalVisibleRect.height().toDp(density),
        selector = selector,
        displayName = selector?.displayName,
        type = this.javaClass.name,
        children = children,
    )
}

internal fun View.selector(): ReactNativeViewSelector? {
    return ReactNativeViewSelector(
        // captures "nativeID" property set on a react native View
        nativeId = getTag(R.id.view_tag_native_id)?.toString()?.ifEmpty { null },
        // captures "testID" property set on a react native View
        testId = getTag(R.id.react_test_id)?.toString()?.ifEmpty { null },
    ).let { if (it.isValid) it else null }
}

private fun Int.toDp(density: Float) =
    (this / density).toInt()
