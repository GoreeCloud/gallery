package com.goreecloud.gallery

/**
 * Repository-local GLAZE UI V1.6 source baseline consumed by the first-party native Gallery shell.
 *
 * The exact shared Stable release source and qualification anchor are pinned here. This contract
 * governs presentation only; it cannot manufacture permission, authorization, privacy/security
 * truth, automatic consequential execution, or downstream consumer acceptance.
 *
 * Rendered accessibility, representative-device, performance, platform-system, release, and
 * production acceptance remain separate Gallery gates.
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

    const val GENERAL_TARGET_DP = 48
    const val MAX_RENDERED_MEDIA_ROWS = 100
    const val MIN_GRID_TILE_DP = 78
    const val MIN_ALBUM_TILE_DP = 132
    const val NAVIGATION_HEIGHT_DP = 54
    const val NAVIGATION_RADIUS_DP = 26
    const val NAVIGATION_SIDE_MARGIN_DP = 24
    const val NAVIGATION_BOTTOM_MARGIN_DP = 10
    const val NAVIGATION_ELEVATION_DP = 4
    const val NAVIGATION_RESERVED_SPACE_DP = 76
    const val CONTENT_BOTTOM_INSET_DP = 28

    fun horizontalGutterDp(widthDp: Int): Int = when {
        widthDp >= 1200 -> 48
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
