package com.appcuesreactnative

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import com.appcues.ElementSelector
import com.appcues.ElementTargetingStrategy
import com.appcues.ViewElement
import com.facebook.react.R

internal data class ReactNativeViewSelector(var nativeId: String): ElementSelector {

    override fun toMap(): Map<String, String> =
        mapOf("nativeID" to nativeId)

    override fun evaluateMatch(target: ElementSelector): Int {
        return (target as? ReactNativeViewSelector)?.let {
            if (it.nativeId == nativeId) 10_000 else 0
        } ?: 0
    }
}

internal class ReactNativeViewTargeting(
    private val module: AppcuesReactNativeModule
): ElementTargetingStrategy {

    override fun captureLayout(): ViewElement? {
        return module.activity?.window?.decorView?.rootView?.asCaptureView()
    }

    override fun inflateSelectorFrom(properties: Map<String, String>): ElementSelector? {
        return properties["nativeID"]?.let { ReactNativeViewSelector(it) }
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
    // NOTE: revisit this when Android SDK is updated to expose this function publicly
    // if (this.isAppcuesView()) {
    //     return null
    // }

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

    return ViewElement(
        x = actualPosition.left.toDp(density),
        y = actualPosition.top.toDp(density),
        width = actualPosition.width().toDp(density),
        height = actualPosition.height().toDp(density),
        selector = selector(),
        type = this.javaClass.name,
        children = children,
    )
}

internal fun View.selector(): ElementSelector? {
    return getTag(R.id.view_tag_native_id)?.toString()?.let { ReactNativeViewSelector(it) }
}

private fun Int.toDp(density: Float) =
    (this / density).toInt()
