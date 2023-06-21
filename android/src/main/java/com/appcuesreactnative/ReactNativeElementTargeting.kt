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
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        return WindowInspector.getGlobalWindowViews().last().rootView as ViewGroup
    }

    return window.decorView.rootView as ViewGroup
}

internal class ReactNativeViewTargeting(
    private val module: AppcuesReactNativeModule
): ElementTargetingStrategy {

    override fun captureLayout(): ViewElement? {
        return module.activity?.getParentView()?.asCaptureView()
    }

    override fun inflateSelectorFrom(properties: Map<String, String>): ElementSelector? {
        return ReactNativeViewSelector(
            nativeId = properties["nativeID"],
            testId = properties["testID"]
        ).let { if (it.isValid) it else null }
    }
}

private fun View.asCaptureView(): ViewElement? {
    val displayMetrics = context.resources.displayMetrics
    val density = displayMetrics.density

    // this is the position of the view relative to the entire screen
    val actualPosition = Rect()
    getGlobalVisibleRect(actualPosition)

    // the bounds of the screen
    val screenRect = Rect(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)

    // if the view is not currently in the screenshot image (scrolled away), ignore
    if (Rect.intersects(actualPosition, screenRect).not()) {
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
            it.asCaptureView()
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
