package com.goreecloud.gallery

import android.app.Activity
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.FrameLayout
import android.widget.ScrollView
import java.util.WeakHashMap

/**
 * Applies Android system-bar, display-cutout, and gesture-safe areas to first-party Gallery chrome.
 *
 * Android 15+ enforces edge-to-edge for current target SDKs. Gallery therefore cannot rely on the
 * decor view to keep interactive chrome out of status, navigation, gesture, or cutout regions.
 * Full-screen media/stage surfaces may remain edge-to-edge, while direct top/bottom chrome and
 * scroll surfaces receive safe margins derived from the current WindowInsets snapshot.
 */
object GallerySystemBars {
    private data class MarginBaseline(
        val start: Int,
        val top: Int,
        val end: Int,
        val bottom: Int,
    )

    private val marginBaselines = WeakHashMap<View, MarginBaseline>()

    fun install(activity: Activity) {
        val androidContent = activity.findViewById<ViewGroup>(android.R.id.content) ?: return
        val root = androidContent.getChildAt(0) as? FrameLayout ?: return

        root.setOnApplyWindowInsetsListener { _, insets ->
            applyInsets(root, safeInsets(insets))
            insets
        }

        if (root.isAttachedToWindow) {
            root.requestApplyInsets()
        } else {
            root.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(view: View) {
                    view.removeOnAttachStateChangeListener(this)
                    view.requestApplyInsets()
                }

                override fun onViewDetachedFromWindow(view: View) = Unit
            })
        }
    }

    private fun applyInsets(root: FrameLayout, safe: SafeInsets) {
        for (index in 0 until root.childCount) {
            val child = root.getChildAt(index)
            val params = child.layoutParams as? FrameLayout.LayoutParams ?: continue
            val verticalGravity = resolvedVerticalGravity(params)

            when {
                child is ScrollView -> applySafeMargins(
                    view = child,
                    params = params,
                    safe = safe,
                    includeTop = true,
                    includeBottom = true,
                    layoutDirection = root.layoutDirection,
                )

                verticalGravity == Gravity.TOP -> applySafeMargins(
                    view = child,
                    params = params,
                    safe = safe,
                    includeTop = true,
                    includeBottom = false,
                    layoutDirection = root.layoutDirection,
                )

                verticalGravity == Gravity.BOTTOM -> applySafeMargins(
                    view = child,
                    params = params,
                    safe = safe,
                    includeTop = false,
                    includeBottom = true,
                    layoutDirection = root.layoutDirection,
                )
            }
        }
        root.requestLayout()
    }

    private fun applySafeMargins(
        view: View,
        params: FrameLayout.LayoutParams,
        safe: SafeInsets,
        includeTop: Boolean,
        includeBottom: Boolean,
        layoutDirection: Int,
    ) {
        val baseline = marginBaselines.getOrPut(view) {
            MarginBaseline(
                start = params.marginStart,
                top = params.topMargin,
                end = params.marginEnd,
                bottom = params.bottomMargin,
            )
        }

        val safeStart = if (layoutDirection == View.LAYOUT_DIRECTION_RTL) safe.right else safe.left
        val safeEnd = if (layoutDirection == View.LAYOUT_DIRECTION_RTL) safe.left else safe.right

        params.marginStart = baseline.start + safeStart
        params.marginEnd = baseline.end + safeEnd
        params.topMargin = baseline.top + if (includeTop) safe.top else 0
        params.bottomMargin = baseline.bottom + if (includeBottom) safe.bottom else 0
        view.layoutParams = params
    }

    private fun resolvedVerticalGravity(params: FrameLayout.LayoutParams): Int {
        if (params.gravity == -1) return -1
        return params.gravity and Gravity.VERTICAL_GRAVITY_MASK
    }

    private fun safeInsets(insets: WindowInsets): SafeInsets {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val barsAndCutout = insets.getInsets(
                WindowInsets.Type.systemBars() or WindowInsets.Type.displayCutout(),
            )
            val gestures = insets.getInsets(WindowInsets.Type.mandatorySystemGestures())
            return SafeInsets(
                left = maxOf(barsAndCutout.left, gestures.left),
                top = maxOf(barsAndCutout.top, gestures.top),
                right = maxOf(barsAndCutout.right, gestures.right),
                bottom = maxOf(barsAndCutout.bottom, gestures.bottom),
            )
        }

        @Suppress("DEPRECATION")
        val cutout = insets.displayCutout
        @Suppress("DEPRECATION")
        return SafeInsets(
            left = maxOf(insets.systemWindowInsetLeft, cutout?.safeInsetLeft ?: 0),
            top = maxOf(insets.systemWindowInsetTop, cutout?.safeInsetTop ?: 0),
            right = maxOf(insets.systemWindowInsetRight, cutout?.safeInsetRight ?: 0),
            bottom = maxOf(insets.systemWindowInsetBottom, cutout?.safeInsetBottom ?: 0),
        )
    }

    private data class SafeInsets(
        val left: Int,
        val top: Int,
        val right: Int,
        val bottom: Int,
    )
}
