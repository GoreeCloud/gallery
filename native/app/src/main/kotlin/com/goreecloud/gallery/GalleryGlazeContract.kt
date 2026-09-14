package com.goreecloud.gallery

/**
 * Repository-local native mapping of the current consumer-eligible GLAZE UI Stable contract.
 *
 * Canonical authority lives in GoreeCloud/goreecloud-glaze-ui. Gallery maps the shared contract onto
 * Android-native surfaces instead of copying browser CSS. The app remains responsible for proving its
 * own rendered/device acceptance; targeting the current Stable version never auto-promotes Gallery.
 *
 * GLAZE UI V1.4 Optical Intelligence requires accessibility and semantic clarity to outrank decorative
 * optical effects. Gallery therefore keeps media visually dominant, uses protected semantic surfaces
 * for controls and selection chrome, and fails to solid/readability-first presentation whenever a
 * platform or accessibility state cannot safely support optical treatment.
 */
object GalleryGlazeContract {
    const val VERSION = "1.4.0"
    const val CANONICAL_REPOSITORY = "GoreeCloud/goreecloud-glaze-ui"
    const val RELEASE_LABEL = "GLAZE UI V1.4 — Optical Intelligence"

    const val GENERAL_TARGET_DP = 48
    const val MAX_RENDERED_MEDIA_ROWS = 100
    const val MIN_GRID_TILE_DP = 78
    const val MIN_ALBUM_TILE_DP = 132

    // V1.4 native optical/semantic mapping. Android remains platform-native and local-only.
    const val SEMANTIC_SURFACE_MIN_ALPHA = 0.88f
    const val MUTED_SURFACE_LIGHT_ALPHA = 0.92f
    const val MUTED_SURFACE_DARK_ALPHA = 0.90f
    const val SELECTION_OVERLAY_ALPHA = 0.22f
    const val MEMORY_TINT_MAX_INFLUENCE = 0.08f

    const val NAVIGATION_HEIGHT_DP = 58
    const val NAVIGATION_RADIUS_DP = 29
    const val NAVIGATION_SIDE_MARGIN_DP = 16
    const val NAVIGATION_BOTTOM_MARGIN_DP = 10
    const val NAVIGATION_ELEVATION_DP = 6
    const val NAVIGATION_RESERVED_SPACE_DP = 82
    const val CONTENT_BOTTOM_INSET_DP = 30

    fun horizontalGutterDp(widthDp: Int): Int = when {
        widthDp >= 1200 -> 40
        widthDp >= 840 -> 32
        widthDp >= 600 -> 24
        else -> 16
    }

    fun gridColumns(widthDp: Int): Int = when {
        widthDp >= 1200 -> 7
        widthDp >= 840 -> 6
        widthDp >= 600 -> 5
        widthDp >= 360 -> 4
        else -> 3
    }

    fun albumGridColumns(widthDp: Int): Int = when {
        widthDp >= 1200 -> 5
        widthDp >= 840 -> 4
        widthDp >= 600 -> 3
        else -> 2
    }
}
