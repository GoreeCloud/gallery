package com.goreecloud.gallery

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import java.util.WeakHashMap

/**
 * Physical-device visual refinement for Gallery's native Glaze chrome.
 *
 * GalleryActivity intentionally owns navigation authority and destination changes. This helper only
 * refines already-rendered first-party controls: it does not create media authority, change Android
 * permission state, or alter destination semantics. The listener exists because the current
 * Development shell rebuilds navigation children when destination state changes; applying the
 * refinement after hierarchy/layout updates keeps the visual treatment consistent without moving
 * operational authority out of GalleryActivity.
 */
object GalleryUiRefinement {
    private data class Installation(
        val root: FrameLayout,
        val listener: ViewTreeObserver.OnGlobalLayoutListener,
    )

    private val installations = WeakHashMap<Activity, Installation>()

    private val navigationIcons = mapOf(
        "Photos" to R.drawable.ic_gallery_nav_photos,
        "Albums" to R.drawable.ic_gallery_nav_albums,
        "Videos" to R.drawable.ic_gallery_nav_videos,
        "Settings" to R.drawable.ic_gallery_nav_settings,
    )

    private val headerControlDescriptions = setOf(
        "Back to Albums",
        "Search the current Gallery destination",
        "Change Gallery sort order",
        "Close search",
        "Gallery media access action",
    )

    fun install(activity: Activity) {
        if (activity !is GalleryActivity || installations.containsKey(activity)) return
        val androidContent = activity.findViewById<ViewGroup>(android.R.id.content) ?: return
        val root = androidContent.getChildAt(0) as? FrameLayout ?: return
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            refine(activity, root)
        }
        root.viewTreeObserver.addOnGlobalLayoutListener(listener)
        installations[activity] = Installation(root, listener)
        root.post { refine(activity, root) }
    }

    fun uninstall(activity: Activity) {
        val installation = installations.remove(activity) ?: return
        if (installation.root.viewTreeObserver.isAlive) {
            installation.root.viewTreeObserver.removeOnGlobalLayoutListener(installation.listener)
        }
    }

    private fun refine(activity: GalleryActivity, root: FrameLayout) {
        findNavigationCapsule(root)?.let { refineNavigation(activity, it) }
        walk(root) { view ->
            val description = view.contentDescription?.toString() ?: return@walk
            if (description !in headerControlDescriptions) return@walk
            val marker = "control:$description"
            if (view.getTag(R.id.gallery_ui_refinement_tag) == marker) return@walk
            view.background = GalleryGlazeSurfaces.drawable(
                activity,
                GalleryGlazeSurfaces.Role.CONTROL,
                GalleryGlazeContract.SHAPE_CONTROL_DP,
            )
            view.elevation = dp(activity, 2).toFloat()
            view.setTag(R.id.gallery_ui_refinement_tag, marker)
        }
    }

    private fun refineNavigation(activity: GalleryActivity, capsule: LinearLayout) {
        for (index in 0 until capsule.childCount) {
            val item = capsule.getChildAt(index) as? TextView ?: continue
            val label = item.text?.toString() ?: continue
            val icon = navigationIcons[label] ?: continue
            val selected = item.isSelected
            val marker = "navigation:$label:$selected"
            if (item.getTag(R.id.gallery_ui_refinement_tag) == marker) continue

            val foreground = if (selected) activityAccent(activity) else activityPrimaryText(activity)
            item.setTextSize(TypedValue.COMPLEX_UNIT_SP, GalleryGlazeContract.NAVIGATION_LABEL_SP)
            item.setTextColor(foreground)
            item.setTypeface(null, if (selected) Typeface.BOLD else Typeface.NORMAL)
            item.setCompoundDrawablesWithIntrinsicBounds(0, icon, 0, 0)
            item.compoundDrawableTintList = ColorStateList.valueOf(foreground)
            item.compoundDrawablePadding = dp(activity, 1)
            item.setPadding(dp(activity, 4), dp(activity, 3), dp(activity, 4), dp(activity, 3))
            item.background = if (selected) {
                GalleryGlazeSurfaces.drawable(
                    activity,
                    GalleryGlazeSurfaces.Role.CONTROL,
                    GalleryGlazeContract.NAVIGATION_ITEM_RADIUS_DP,
                )
            } else {
                ColorDrawable(Color.TRANSPARENT)
            }
            item.contentDescription = "$label${if (selected) ", selected" else ""}"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                item.stateDescription = if (selected) "Selected" else null
            }
            item.setTag(R.id.gallery_ui_refinement_tag, marker)
        }
    }

    private fun findNavigationCapsule(root: ViewGroup): LinearLayout? {
        for (index in 0 until root.childCount) {
            val child = root.getChildAt(index)
            if (child is LinearLayout && child.childCount == navigationIcons.size) {
                val labels = (0 until child.childCount).mapNotNull { childIndex ->
                    (child.getChildAt(childIndex) as? TextView)?.text?.toString()
                }
                if (labels.size == navigationIcons.size && labels.toSet() == navigationIcons.keys) {
                    return child
                }
            }
            if (child is ViewGroup) {
                findNavigationCapsule(child)?.let { return it }
            }
        }
        return null
    }

    private fun walk(root: View, visitor: (View) -> Unit) {
        visitor(root)
        if (root !is ViewGroup) return
        for (index in 0 until root.childCount) {
            walk(root.getChildAt(index), visitor)
        }
    }

    private fun activityAccent(activity: Activity): Int = themeColor(
        activity,
        android.R.attr.colorAccent,
        0xff2e7d6f.toInt(),
    )

    private fun activityPrimaryText(activity: Activity): Int = themeColor(
        activity,
        android.R.attr.textColorPrimary,
        0xff1d1d1f.toInt(),
    )

    private fun themeColor(activity: Activity, attribute: Int, fallback: Int): Int {
        val attributes = activity.obtainStyledAttributes(intArrayOf(attribute))
        return try {
            attributes.getColor(0, fallback)
        } finally {
            attributes.recycle()
        }
    }

    private fun dp(activity: Activity, value: Int): Int =
        (value * activity.resources.displayMetrics.density).toInt()
}
