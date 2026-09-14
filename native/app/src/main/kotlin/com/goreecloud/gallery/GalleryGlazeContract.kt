package com.goreecloud.gallery

/**
 * Repository-local GLAZE UI V1.4 source mapping for the first-party native Gallery shell.
 *
 * The current repository Development line maps shared Glaze semantics through native Android
 * primitives rather than copying web CSS or requiring a web runtime. VERSION records the source
 * contract mapped by this native implementation; it does not establish Gallery application
 * conformance, rendered acceptance, production eligibility, Release Candidate, or Stable status.
 */
object GalleryGlazeContract {
    const val VERSION = "1.4.0"
    const val AUTHORITY_REVISION = "ee057ce9e729296aeaeda182d01db89f52bd66f3"

    // Shared spatial baseline used by the current Development mapping.
    const val SPACE_MICRO_DP = 2
    const val SPACE_HAIRLINE_DP = 4
    const val SPACE_CONTROL_DP = 8
    const val SPACE_COMPACT_CLUSTER_DP = 12
    const val SPACE_STANDARD_CLUSTER_DP = 16
    const val SPACE_CONTENT_DP = 24
    const val SPACE_SECTION_DP = 32
    const val SPACE_REGION_DP = 48

    // Semantic shape roles.
    const val SHAPE_QUIET_DP = 10
    const val SHAPE_CONTROL_DP = 12
    const val SHAPE_CONTAINER_DP = 20
    const val SHAPE_ROUNDED_DP = 24
    const val SHAPE_OVERLAY_DP = 28
    const val SHAPE_CAPSULE_DP = 999

    // Semantic motion roles.
    const val MOTION_MICRO_MS = 160L
    const val MOTION_STANDARD_MS = 240L
    const val MOTION_CONNECTED_MS = 360L
    const val MOTION_SPATIAL_MS = 480L
    const val MOTION_REDUCED_STANDARD_MS = 180L
    const val MOTION_MINIMAL_MS = 0L

    // Optical boundaries used by native Gallery adapters.
    const val OPTICAL_MEMORY_TINT_MAX_FRACTION = 0.08f
    const val OPTICAL_CONTENT_AWARE_FROST_ENABLED = true
    const val OPTICAL_SEMANTIC_BLUR_PROTECTION_ENABLED = true
    const val OPTICAL_REDUCED_TRANSPARENCY_FALLBACK_REQUIRED = true
    const val OPTICAL_INCREASED_CONTRAST_FALLBACK_REQUIRED = true
    const val OPTICAL_ENVIRONMENT_TINT_MAY_OVERRIDE_SEMANTIC_STATE = false

    const val GENERAL_TARGET_DP = 48

    const val MAX_RENDERED_MEDIA_ROWS = 100
    const val MIN_GRID_TILE_DP = 78
    const val MIN_ALBUM_TILE_DP = 132

    // Compact floating bottom-navigation composition. Insets are added at runtime by GallerySystemBars.
    const val NAVIGATION_HEIGHT_DP = 56
    const val NAVIGATION_RADIUS_DP = SHAPE_CAPSULE_DP
    const val NAVIGATION_SIDE_MARGIN_DP = 20
    const val NAVIGATION_BOTTOM_MARGIN_DP = 10
    const val NAVIGATION_ELEVATION_DP = 6
    const val NAVIGATION_RESERVED_SPACE_DP = 82
    const val CONTENT_BOTTOM_INSET_DP = SPACE_CONTENT_DP

    /**
     * Android Gallery capability adapter for current composition widths.
     *
     * The returned gutters are governed Glaze spatial values. The width thresholds are local Android
     * composition heuristics, not canonical Glaze device identities or universal breakpoints.
     */
    fun horizontalGutterDp(widthDp: Int): Int = when {
        widthDp >= 1200 -> SPACE_REGION_DP
        widthDp >= 840 -> SPACE_SECTION_DP
        widthDp >= 600 -> SPACE_CONTENT_DP
        else -> SPACE_STANDARD_CLUSTER_DP
    }

    /** Product-specific media density, not the Glaze foundational layout-grid column token. */
    fun gridColumns(widthDp: Int): Int = when {
        widthDp >= 1200 -> 7
        widthDp >= 840 -> 6
        widthDp >= 600 -> 5
        widthDp >= 360 -> 4
        else -> 3
    }

    /** Product-specific album-card density, not the Glaze foundational layout-grid column token. */
    fun albumGridColumns(widthDp: Int): Int = when {
        widthDp >= 1200 -> 5
        widthDp >= 840 -> 4
        widthDp >= 600 -> 3
        else -> 2
    }
}
