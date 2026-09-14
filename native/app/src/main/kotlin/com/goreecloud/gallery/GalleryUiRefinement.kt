package com.goreecloud.gallery

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.TypedValue
import android.view.Gravity
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
 * GalleryActivity and RecycleBinActivity retain all navigation/media authority. This helper only
 * refines already-rendered first-party controls. It never changes Android permissions, media scope,
 * mutation authority, destination semantics, or Activity-owned navigation accessibility identity.
 * Repeated layout work is intentionally bounded to direct bottom/viewer chrome plus the four
 * primary navigation controls; large media grids are not repeatedly traversed.
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

    private val persistentControlDescriptions = setOf(
        "Back to Albums",
        "Search the current Gallery destination",
        "Change Gallery sort order",
        "Close search",
        "Gallery media access action",
        "Back to GoreeCloud Gallery",
        "Refresh Recycle Bin",
    )

    private val primaryPersistentControlDescriptions = setOf(
        "Gallery media access action",
    )

    private val recycleBinActionDescriptions = setOf(
        "Select all currently loaded trashed media",
        "Restore selected media through Android confirmation",
        "Permanently delete selected media through Android confirmation",
        "Clear Recycle Bin selection",
    )

    private val recycleBinViewerDescriptions = setOf(
        "Close Recycle Bin viewer",
        "Previous trashed media",
        "Next trashed media",
        "Restore this media through Android confirmation",
        "Permanently delete this media through Android confirmation",
        "Show details for this trashed media",
    )

    private val albumTileDescription = Regex("^.+, (?:1 item|[0-9]+ items)$")
    private val collectionSubtitle = Regex("^[0-9]+ collections?(?: · (?:Newest|Oldest) first)?$")

    fun install(activity: Activity) {
        if (
            activity !is GalleryActivity &&
            activity !is RecycleBinActivity
        ) return
        if (installations.containsKey(activity)) return

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

    private fun refine(activity: Activity, root: FrameLayout) {
        if (root.getTag(R.id.gallery_ui_refinement_tag) != ROOT_REFINED_MARKER) {
            refinePersistentControls(activity, root)
            root.setTag(R.id.gallery_ui_refinement_tag, ROOT_REFINED_MARKER)
        }

        when (activity) {
            is GalleryActivity -> {
                findNavigationCapsule(root)?.let { refineNavigation(activity, it) }
                refineAlbumCollectionSubtitle(root)
            }
            is RecycleBinActivity -> refineRecycleBinChrome(activity, root)
        }
    }

    private fun refinePersistentControls(activity: Activity, root: FrameLayout) {
        walk(root) { view ->
            val description = view.contentDescription?.toString() ?: return@walk
            if (description !in persistentControlDescriptions) return@walk
            val primary = description in primaryPersistentControlDescriptions
            styleControl(
                activity = activity,
                view = view,
                marker = "control:$description",
                role = if (primary) GalleryGlazeSurfaces.Role.CONTROL else GalleryGlazeSurfaces.Role.RAISED,
                elevationDp = if (primary) 2 else 1,
            )
        }
    }

    private fun refineNavigation(activity: GalleryActivity, capsule: LinearLayout) {
        val capsuleMarker = "navigation-capsule-v2"
        if (capsule.getTag(R.id.gallery_navigation_surface_tag) != capsuleMarker) {
            // Keep the outer bar optically quieter than the selected item so state is not conveyed
            // by color alone and the active CONTROL surface remains the strongest navigation cue.
            capsule.background = GalleryGlazeSurfaces.drawable(
                activity,
                GalleryGlazeSurfaces.Role.OVERLAY,
                GalleryGlazeContract.NAVIGATION_RADIUS_DP,
            )
            capsule.elevation = dp(activity, GalleryGlazeContract.NAVIGATION_ELEVATION_DP).toFloat()
            capsule.setTag(R.id.gallery_navigation_surface_tag, capsuleMarker)
        }

        for (index in 0 until capsule.childCount) {
            val item = capsule.getChildAt(index) as? TextView ?: continue
            val label = item.text?.toString() ?: continue
            val icon = navigationIcons[label] ?: continue
            // GalleryActivity owns the navigation selection and accessibility identity. Read that
            // identity rather than replacing it from presentation-only refinement state.
            val activityDescription = item.contentDescription?.toString().orEmpty()
            val selected = activityDescription == "$label, selected" || item.isSelected
            val marker = "navigation:$label:$selected:v3"
            if (item.getTag(R.id.gallery_ui_refinement_tag) == marker) continue

            val foreground = if (selected) activityAccent(activity) else activityPrimaryText(activity)
            item.setTextSize(TypedValue.COMPLEX_UNIT_SP, GalleryGlazeContract.NAVIGATION_LABEL_SP)
            item.setTextColor(foreground)
            item.setTypeface(null, if (selected) Typeface.BOLD else Typeface.NORMAL)
            val drawable = activity.getDrawable(icon)?.mutate()
            val iconPx = dp(activity, GalleryGlazeContract.NAVIGATION_ICON_DP)
            drawable?.setBounds(0, 0, iconPx, iconPx)
            item.setCompoundDrawables(null, drawable, null, null)
            item.compoundDrawableTintList = ColorStateList.valueOf(foreground)
            item.compoundDrawablePadding = dp(activity, 2)
            item.setPadding(dp(activity, 4), dp(activity, 2), dp(activity, 4), dp(activity, 2))
            item.background = if (selected) {
                GalleryGlazeSurfaces.drawable(
                    activity,
                    GalleryGlazeSurfaces.Role.CONTROL,
                    GalleryGlazeContract.NAVIGATION_ITEM_RADIUS_DP,
                )
            } else {
                ColorDrawable(Color.TRANSPARENT)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                item.stateDescription = if (selected) "Selected" else null
            }
            item.setTag(R.id.gallery_ui_refinement_tag, marker)
        }
    }

    /**
     * The Recovery row is intentionally separate from Collections. GalleryActivity historically
     * counted the Recycle Bin capability in the Albums subtitle even though it renders under the
     * Recovery heading. Correct the rendered summary to match the collection tiles the user can see
     * without changing MediaStore authority or album membership.
     */
    private fun refineAlbumCollectionSubtitle(root: FrameLayout) {
        var collectionCount = 0
        val subtitleCandidates = mutableListOf<TextView>()

        walk(root) { view ->
            if (
                view is LinearLayout &&
                view.orientation == LinearLayout.VERTICAL &&
                view.isClickable &&
                albumTileDescription.matches(view.contentDescription?.toString().orEmpty())
            ) {
                collectionCount += 1
            }
            if (view is TextView && collectionSubtitle.matches(view.text?.toString().orEmpty())) {
                subtitleCandidates += view
            }
        }

        if (subtitleCandidates.size != 1) return
        val subtitle = subtitleCandidates.single()
        val current = subtitle.text?.toString().orEmpty()
        val sortSuffix = current.substringAfter(" · ", missingDelimiterValue = "")
        val countLabel = if (collectionCount == 1) "1 collection" else "$collectionCount collections"
        val corrected = if (sortSuffix.isBlank()) countLabel else "$countLabel · $sortSuffix"
        if (current != corrected) subtitle.text = corrected
    }

    private fun findNavigationCapsule(root: FrameLayout): LinearLayout? {
        for (index in 0 until root.childCount) {
            val child = root.getChildAt(index) as? LinearLayout ?: continue
            if (child.childCount != navigationIcons.size) continue
            val labels = (0 until child.childCount).mapNotNull { childIndex ->
                (child.getChildAt(childIndex) as? TextView)?.text?.toString()
            }
            if (labels.size == navigationIcons.size && labels.toSet() == navigationIcons.keys) {
                return child
            }
        }
        return null
    }

    private fun refineRecycleBinChrome(activity: RecycleBinActivity, root: FrameLayout) {
        for (index in 0 until root.childCount) {
            when (val child = root.getChildAt(index)) {
                is LinearLayout -> refineRecycleBinActionBar(activity, child)
                is FrameLayout -> refineRecycleBinViewerOverlay(activity, child)
            }
        }
    }

    private fun refineRecycleBinActionBar(activity: RecycleBinActivity, child: LinearLayout) {
        val params = child.layoutParams as? FrameLayout.LayoutParams ?: return
        if (
            params.gravity == -1 ||
            (params.gravity and Gravity.VERTICAL_GRAVITY_MASK) != Gravity.BOTTOM ||
            child.childCount != RECYCLE_BIN_ACTION_COUNT
        ) return

        val barMarker = "recycle-action-bar:${child.childCount}"
        if (child.getTag(R.id.gallery_ui_refinement_tag) != barMarker) {
            child.background = GalleryGlazeSurfaces.drawable(
                activity,
                GalleryGlazeSurfaces.Role.CHROME,
                GalleryGlazeContract.SHAPE_CAPSULE_DP,
            )
            child.elevation = dp(activity, GalleryGlazeContract.NAVIGATION_ELEVATION_DP).toFloat()
            child.setTag(R.id.gallery_ui_refinement_tag, barMarker)
        }

        for (index in 0 until child.childCount) {
            val control = child.getChildAt(index)
            val description = control.contentDescription?.toString() ?: continue
            if (description !in recycleBinActionDescriptions) continue
            styleControl(activity, control, "recycle-action:$description")
        }
    }

    private fun refineRecycleBinViewerOverlay(activity: RecycleBinActivity, overlay: FrameLayout) {
        if (!containsDescription(overlay, "Close Recycle Bin viewer")) return

        for (index in 0 until overlay.childCount) {
            val child = overlay.getChildAt(index)
            if (child is LinearLayout) {
                val params = child.layoutParams as? FrameLayout.LayoutParams
                val verticalGravity = params?.gravity?.and(Gravity.VERTICAL_GRAVITY_MASK)
                if (verticalGravity == Gravity.TOP || verticalGravity == Gravity.BOTTOM) {
                    val marker = "recycle-viewer-chrome:$verticalGravity"
                    if (child.getTag(R.id.gallery_ui_refinement_tag) != marker) {
                        child.background = GalleryGlazeSurfaces.drawable(
                            activity,
                            GalleryGlazeSurfaces.Role.OVERLAY,
                            GalleryGlazeContract.SHAPE_ROUNDED_DP,
                        )
                        child.setTag(R.id.gallery_ui_refinement_tag, marker)
                    }
                }
            }
        }

        walk(overlay) { view ->
            val description = view.contentDescription?.toString() ?: return@walk
            if (description !in recycleBinViewerDescriptions) return@walk
            styleControl(activity, view, "recycle-viewer:$description")
        }
    }

    private fun containsDescription(root: View, expected: String): Boolean {
        if (root.contentDescription?.toString() == expected) return true
        if (root !is ViewGroup) return false
        for (index in 0 until root.childCount) {
            if (containsDescription(root.getChildAt(index), expected)) return true
        }
        return false
    }

    private fun styleControl(
        activity: Activity,
        view: View,
        marker: String,
        role: GalleryGlazeSurfaces.Role = GalleryGlazeSurfaces.Role.CONTROL,
        elevationDp: Int = 2,
    ) {
        if (view.getTag(R.id.gallery_ui_refinement_tag) == marker) return
        view.background = GalleryGlazeSurfaces.drawable(
            activity,
            role,
            GalleryGlazeContract.SHAPE_CONTROL_DP,
        )
        view.elevation = dp(activity, elevationDp).toFloat()
        view.setTag(R.id.gallery_ui_refinement_tag, marker)
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

    private const val ROOT_REFINED_MARKER = "gallery-ui-refinement-root-v3"
    private const val RECYCLE_BIN_ACTION_COUNT = 4
}
