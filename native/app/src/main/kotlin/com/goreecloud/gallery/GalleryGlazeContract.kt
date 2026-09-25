package com.goreecloud.gallery

/**
 * Repository-local GLAZE UI V1.6 authority baseline consumed by the first-party native Gallery shell.
 *
 * The exact shared Stable release source and qualification anchor are pinned here. Gallery-local
 * spacing, shape, motion, optical, and composition constants below are bounded native adapter values;
 * they are not a claim that Android-specific heuristics are canonical Glaze tokens. This contract
 * governs presentation only and cannot manufacture permission, authorization, privacy/security
 * truth, consequential execution, downstream consumer acceptance, Release Candidate, or Stable state.
 */
object GalleryGlazeContract {
    const val VERSION = "1.6.0"
    const val ACCEPTED_RELEASE_SOURCE = "a7180679ea851389e0f3004515f9a25f420e716d"
    const val SOURCE_QUALIFICATION_ANCHOR = "c7509c79256b04b0aa67cb9dd0737d7588e0ae4a"
    const val STABLE_RUNTIME_ENTRYPOINT = "js/glaze-v1.6.0.mjs"
    const val ROLLBACK_BASELINE = "1.5.1"

    const val PRESENTATION_ONLY = true
    const val PERMISSION_REQUEST_AUTOMATIC = false
    const val AUTHORIZATION_INFERRED = false
    const val CONSEQUENTIAL_EXECUTION_AUTOMATIC = false
    const val DOWNSTREAM_CONSUMER_ACCEPTANCE_AUTOMATIC = false

    enum class MaterialRole {
        CANVAS,
        SOLID,
        RAISED,
        FUNCTIONAL_GLASS,
        CLEAR_GLASS,
        OVERLAY,
    }

    // Gallery-local native adapter values used by the current Development presentation layer.
    const val SPACE_MICRO_DP = 2
    const val SPACE_HAIRLINE_DP = 4
    const val SPACE_CONTROL_DP = 8
    const val SPACE_COMPACT_CLUSTER_DP = 12
    const val SPACE_STANDARD_CLUSTER_DP = 16
    const val SPACE_CONTENT_DP = 24
    const val SPACE_SECTION_DP = 32
    const val SPACE_REGION_DP = 48

    const val SHAPE_QUIET_DP = 10
    const val SHAPE_CONTROL_DP = 12
    const val SHAPE_CONTAINER_DP = 20
    const val SHAPE_ROUNDED_DP = 24
    const val SHAPE_OVERLAY_DP = 28
    const val SHAPE_CAPSULE_DP = 999

    const val MOTION_MICRO_MS = 160L
    const val MOTION_STANDARD_MS = 240L
    const val MOTION_CONNECTED_MS = 360L
    const val MOTION_SPATIAL_MS = 480L
    const val MOTION_REDUCED_STANDARD_MS = 180L
    const val MOTION_MINIMAL_MS = 0L

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

    // Current V1.6 Gallery navigation baseline plus local icon/label adapter details.
    const val NAVIGATION_HEIGHT_DP = 54
    const val NAVIGATION_RADIUS_DP = 26
    const val NAVIGATION_SIDE_MARGIN_DP = 24
    const val NAVIGATION_BOTTOM_MARGIN_DP = 10
    const val NAVIGATION_ELEVATION_DP = 4
    const val NAVIGATION_RESERVED_SPACE_DP = 76
    const val CONTENT_BOTTOM_INSET_DP = 28
    const val NAVIGATION_ICON_DP = 20
    const val NAVIGATION_LABEL_SP = 10.5f
    const val NAVIGATION_ITEM_RADIUS_DP = SHAPE_CAPSULE_DP

    fun horizontalGutterDp(widthDp: Int): Int = when {
        widthDp >= 1200 -> SPACE_REGION_DP
        widthDp >= 840 -> SPACE_SECTION_DP
        widthDp >= 600 -> SPACE_CONTENT_DP
        else -> SPACE_STANDARD_CLUSTER_DP
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
