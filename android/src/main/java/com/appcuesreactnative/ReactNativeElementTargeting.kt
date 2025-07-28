package com.appcuesreactnative

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.view.inspector.WindowInspector
import androidx.core.view.children
import com.appcues.ElementSelector
import com.appcues.ElementTargetingStrategy
import com.appcues.ViewElement
import com.appcues.isAppcuesView
import com.facebook.react.R
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.json.JSONArray
import java.lang.reflect.Method

internal data class ReactNativeViewSelector(
  var appcuesId: String?, var nativeId: String?, var testId: String?, var tag: String?
): ElementSelector {

    val isValid: Boolean
        get() = appcuesId != null || nativeId != null || testId != null || tag != null

    val displayName: String?
        get() = when {
            appcuesId != null -> appcuesId
            nativeId != null -> nativeId
            testId != null -> testId
            tag != null -> "(tag $tag)"
            else -> null
        }

    override fun toMap(): Map<String, String> =
        mapOf(
            "appcuesID" to appcuesId,
            "nativeID" to nativeId,
            "testID" to testId,
            "tag" to tag
        ).filterValues { it != null }.mapValues { it.value as String }

    override fun evaluateMatch(target: ElementSelector): Int {
        var weight = 0

        (target as? ReactNativeViewSelector)?.let {
            if (!it.appcuesId.isNullOrEmpty() && it.appcuesId == appcuesId) {
                weight += 1_000
            }

            if (!it.nativeId.isNullOrEmpty() && it.nativeId == nativeId) {
                weight += 1_000
            }

            if (!it.testId.isNullOrEmpty() && it.testId == testId) {
                weight += 1_000
            }

            if (it.tag != null && it.tag == tag) {
              weight += 100
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

    override suspend fun captureLayout(): ViewElement? {
        return module.activity?.getParentView()?.let {
            val screenBounds = Rect()
            it.getGlobalVisibleRect(screenBounds)
            it.asCaptureView(screenBounds)
        }
    }

    override fun inflateSelectorFrom(properties: Map<String, String>): ElementSelector? {
        return ReactNativeViewSelector(
            appcuesId = properties["appcuesID"],
            nativeId = properties["nativeID"],
            testId = properties["testID"],
            tag = properties["tag"]
        ).let { if (it.isValid) it else null }
    }
}

private suspend fun View.asCaptureView(screenBounds: Rect): ViewElement? {
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

    val children: MutableList<ViewElement> = mutableListOf()
    val rectDp = globalVisibleRect.toDp(density)

    if (this is ViewGroup) {
        coroutineScope {
            val deferred = this@asCaptureView.children.map {
                async {
                    if (!it.isShown) {
                        // discard hidden views and subviews within
                        null
                    } else {
                        it.asCaptureView(screenBounds)
                    }
                }
            }.toList()

            val viewChildren = deferred.awaitAll().filterNotNull()
            children.addAll(viewChildren)
        }
    }

  if (this is WebView) {
     children.addAll(this.children(rectDp))
  }

  val selector = selector()

    return ViewElement(
        x = rectDp.left,
        y = rectDp.top,
        width = rectDp.width(),
        height = rectDp.height(),
        selector = selector,
        displayName = selector?.displayName,
        type = this.javaClass.name,
        children = children.ifEmpty { null },
    )
}

internal fun View.selector(): ReactNativeViewSelector? {
    return ReactNativeViewSelector(
        appcuesId = null,
        // captures "nativeID" property set on a react native View
        nativeId = getTag(R.id.view_tag_native_id)?.toString()?.ifEmpty { null },
        // captures "testID" property set on a react native View
        testId = getTag(R.id.react_test_id)?.toString()?.ifEmpty { null },
        tag = null
    ).let { if (it.isValid) it else null }
}


private suspend fun WebView.children(rectDp: Rect): List<ViewElement> {
  val js = """
        [...document.querySelectorAll('[id], [data-appcues-id]')].reduce((result, el) => {
            const { x, y, width, height } = el.getBoundingClientRect();
            const tag = el.id ? `#${'$'}{el.id}` : null;
            const appcuesID = el.getAttribute('data-appcues-id')
            if (height !== 0 && width !== 0) {
                result.push({
                    x,
                    y,
                    width,
                    height,
                    tag,
                    appcuesID
                });
            }
            return result;
        }, []);
        """

  val result = evaluateJavascript(js)
  return result.mapNotNull { el ->
    val x = (el["x"] as Double).toInt()
    val y = (el["y"] as Double).toInt()
    val width = (el["width"] as Double).toInt()
    val height = (el["height"] as Double).toInt()

    val appcuesId = el["appcuesID"] as? String
    val tag = el["tag"] as? String

    // considering it eligible for targeting if the center point is visible
    val centerX = (x + (width / 2.0)).toInt()
    val centerY = (y + (height / 2.0)).toInt()

    if (centerX in 0..rectDp.width() && centerY in 0..rectDp.height())
      ViewElement(
        x = rectDp.left + x,
        y = rectDp.top + y,
        width = width,
        height = height,
        displayName = appcuesId ?: tag,
        selector = ReactNativeViewSelector(
          appcuesId = appcuesId,
          nativeId = null,
          testId = null,
          tag = el["tag"] as String?
        ),
        type = "HTMLNode",
        children = null,
      )
    else null
  }
}

private suspend fun WebView.evaluateJavascript(script: String): List<Map<String, Any>> {
    val completion = CompletableDeferred<List<Map<String, Any>>>()
    evaluateJavascript(script) { result ->
        try {
            val jsonArray = JSONArray(result)
            val parsedList = (0 until jsonArray.length()).map { i ->
                val jsonObject = jsonArray.getJSONObject(i)
                jsonObject.keys().asSequence().associateWith { jsonObject[it] }
            }
            completion.complete(parsedList)
        } catch (e: Exception) {
            completion.completeExceptionally(e)
        }
    }
    return completion.await()
}

private fun Int.toDp(density: Float) =
    (this / density).toInt()

private fun Rect.toDp(density: Float) = Rect(
    (this.left / density).toInt(),
    (this.top / density).toInt(),
    (this.right / density).toInt(),
    (this.bottom / density).toInt()
)
