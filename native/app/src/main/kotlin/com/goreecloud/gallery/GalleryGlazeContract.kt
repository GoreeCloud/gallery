package com.goreecloud.gallery

/**
 * Repository-local GLAZE UI V1.3 source mapping for the first-party native Gallery shell.
 *
 * The current published GoreeCloud design-system authority is GLAZE UI V1.3 / 1.3.0 — Adaptive
 * Resonance. Native Android maps the shared semantics through platform primitives rather than
 * copying web CSS or requiring a web runtime.
 *
 * VERSION records the source contract mapped by this native implementation. It does not establish
 * Gallery application conformance, rendered acceptance, production eligibility, Release Candidate,
 * or Stable status. Gallery remains fail-closed on those claims until its application-specific
 * migration, accessibility, representative-device, and release gates are completed and accepted.
 */
object GalleryGlazeContract {
    const val VERSION = "1.3.0"
    const val AUTHORITY_REVISION = "ff34f232f295c9dcb07e4c681f66d4104d0b9323"
    const val AUTHORITY_TAG = "v1.3.0"

    // Shared spatial baseline consumed by the Gallery V1.3 native adapter.
    const val SPACE_MICRO_DP = 2
    const val SPACE_HAIRLINE_DP = 4
    const val SPACE_CONTROL_DP = 8
    const val SPACE_COMPACT_CLUSTER_DP = 12
    const val SPACE_STANDARD_CLUSTER_DP = 16
    const val SPACE_CONTENT_DP = 24
    const val SPACE_SECTION_DP = 32
    const val SPACE_REGION_DP = 48

    // Semantic shape roles used by Gallery's V1.3 platform mapping.
    const val SHAPE_QUIET_DP = 10
    const val SHAPE_CONTROL_DP = 12
    const val SHAPE_CONTAINER_DP = 20
    const val SHAPE_ROUNDED_DP = 24
    const val SHAPE_OVERLAY_DP = 28
    const val SHAPE_CAPSULE_DP = 999

    // Semantic motion roles used by Gallery's V1.3 platform mapping.
    const val MOTION_MICRO_MS = 160L
    const val MOTION_STANDARD_MS = 240L
    const val MOTION_CONNECTED_MS = 360L
    const val MOTION_SPATIAL_MS = 480L
    const val MOTION_REDUCED_STANDARD_MS = 180L
    const val MOTION_MINIMAL_MS = 0L

    // Gallery-local Adaptive Resonance boundaries. Environmental expression remains non-semantic
    // and must yield to legibility, task completion, protected states, and accessibility.
    const val ENVIRONMENT_TINT_MAX_FRACTION = 0.08f
    const val ADAPTIVE_EXPRESSION_ENABLED = true
    const val LIVING_MATERIAL_2_ENABLED = true
    const val REDUCED_TRANSPARENCY_FALLBACK_REQUIRED = true
    const val INCREASED_CONTRAST_FALLBACK_REQUIRED = true
    const val ENVIRONMENT_TINT_MAY_OVERRIDE_SEMANTIC_STATE = false

    // Current Android interaction floor used by this Gallery mapping.
    const val GENERAL_TARGET_DP = 48

    const val MAX_RENDERED_MEDIA_ROWS = 100
    const val MIN_GRID_TILE_DP = 78
    const val MIN_ALBUM_TILE_DP = 132

    // Gallery-specific bottom navigation composition mapped onto semantic V1.3 roles.
    const val NAVIGATION_HEIGHT_DP = 60
    const val NAVIGATION_RADIUS_DP = SHAPE_CAPSULE_DP
    const val NAVIGATION_SIDE_MARGIN_DP = SPACE_STANDARD_CLUSTER_DP
    const val NAVIGATION_BOTTOM_MARGIN_DP = SPACE_COMPACT_CLUSTER_DP
    const val NAVIGATION_ELEVATION_DP = 8
    const val NAVIGATION_RESERVED_SPACE_DP = 88
    const val CONTENT_BOTTOM_INSET_DP = SPACE_SECTION_DP

    /**
     * Android Gallery capability adapter for current composition widths.
     *
     * The returned gutters are governed spatial values. Width thresholds are local Android
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
