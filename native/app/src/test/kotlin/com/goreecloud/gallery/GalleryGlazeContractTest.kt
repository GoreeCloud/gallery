package com.goreecloud.gallery

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GalleryGlazeContractTest {
    @Test
    fun `native shell pins exact current Stable Glaze V1_6 authority`() {
        assertEquals("1.6.0", GalleryGlazeContract.VERSION)
        assertEquals(
            "a7180679ea851389e0f3004515f9a25f420e716d",
            GalleryGlazeContract.ACCEPTED_RELEASE_SOURCE,
        )
        assertEquals(
            "c7509c79256b04b0aa67cb9dd0737d7588e0ae4a",
            GalleryGlazeContract.SOURCE_QUALIFICATION_ANCHOR,
        )
        assertEquals("js/glaze-v1.6.0.mjs", GalleryGlazeContract.STABLE_RUNTIME_ENTRYPOINT)
        assertEquals("1.5.1", GalleryGlazeContract.ROLLBACK_BASELINE)
    }

    @Test
    fun `Glaze authority remains presentation only and fail closed`() {
        assertTrue(GalleryGlazeContract.PRESENTATION_ONLY)
        assertFalse(GalleryGlazeContract.PERMISSION_REQUEST_AUTOMATIC)
        assertFalse(GalleryGlazeContract.AUTHORIZATION_INFERRED)
        assertFalse(GalleryGlazeContract.CONSEQUENTIAL_EXECUTION_AUTOMATIC)
        assertFalse(GalleryGlazeContract.DOWNSTREAM_CONSUMER_ACCEPTANCE_AUTOMATIC)
    }

    @Test
    fun `Gallery local optical adapter remains bounded and accessibility safe`() {
        assertTrue(GalleryGlazeContract.OPTICAL_CONTENT_AWARE_FROST_ENABLED)
        assertTrue(GalleryGlazeContract.OPTICAL_SEMANTIC_BLUR_PROTECTION_ENABLED)
        assertTrue(GalleryGlazeContract.OPTICAL_REDUCED_TRANSPARENCY_FALLBACK_REQUIRED)
        assertTrue(GalleryGlazeContract.OPTICAL_INCREASED_CONTRAST_FALLBACK_REQUIRED)
        assertTrue(GalleryGlazeContract.OPTICAL_MEMORY_TINT_MAX_FRACTION in 0f..0.08f)
        assertFalse(GalleryGlazeContract.OPTICAL_ENVIRONMENT_TINT_MAY_OVERRIDE_SEMANTIC_STATE)
    }

    @Test
    fun `Gallery local spatial adapter stays internally consistent`() {
        assertEquals(2, GalleryGlazeContract.SPACE_MICRO_DP)
        assertEquals(4, GalleryGlazeContract.SPACE_HAIRLINE_DP)
        assertEquals(8, GalleryGlazeContract.SPACE_CONTROL_DP)
        assertEquals(12, GalleryGlazeContract.SPACE_COMPACT_CLUSTER_DP)
        assertEquals(16, GalleryGlazeContract.SPACE_STANDARD_CLUSTER_DP)
        assertEquals(24, GalleryGlazeContract.SPACE_CONTENT_DP)
        assertEquals(32, GalleryGlazeContract.SPACE_SECTION_DP)
        assertEquals(48, GalleryGlazeContract.SPACE_REGION_DP)
    }

    @Test
    fun `Gallery local shape and motion adapters remain bounded`() {
        assertEquals(10, GalleryGlazeContract.SHAPE_QUIET_DP)
        assertEquals(12, GalleryGlazeContract.SHAPE_CONTROL_DP)
        assertEquals(20, GalleryGlazeContract.SHAPE_CONTAINER_DP)
        assertEquals(24, GalleryGlazeContract.SHAPE_ROUNDED_DP)
        assertEquals(28, GalleryGlazeContract.SHAPE_OVERLAY_DP)
        assertEquals(999, GalleryGlazeContract.SHAPE_CAPSULE_DP)
        assertEquals(160L, GalleryGlazeContract.MOTION_MICRO_MS)
        assertEquals(240L, GalleryGlazeContract.MOTION_STANDARD_MS)
        assertEquals(360L, GalleryGlazeContract.MOTION_CONNECTED_MS)
        assertEquals(480L, GalleryGlazeContract.MOTION_SPATIAL_MS)
        assertEquals(180L, GalleryGlazeContract.MOTION_REDUCED_STANDARD_MS)
        assertEquals(0L, GalleryGlazeContract.MOTION_MINIMAL_MS)
    }

    @Test
    fun `adaptive gutters follow the current Stable compact through large screen hierarchy`() {
        assertEquals(16, GalleryGlazeContract.horizontalGutterDp(390))
        assertEquals(24, GalleryGlazeContract.horizontalGutterDp(820))
        assertEquals(32, GalleryGlazeContract.horizontalGutterDp(900))
        assertEquals(48, GalleryGlazeContract.horizontalGutterDp(1280))
    }

    @Test
    fun `photo grid is media dense on phones and adapts across wider classes`() {
        assertEquals(3, GalleryGlazeContract.gridColumns(320))
        assertEquals(4, GalleryGlazeContract.gridColumns(360))
        assertEquals(4, GalleryGlazeContract.gridColumns(390))
        assertEquals(5, GalleryGlazeContract.gridColumns(820))
        assertEquals(6, GalleryGlazeContract.gridColumns(900))
        assertEquals(7, GalleryGlazeContract.gridColumns(1280))
        assertTrue(GalleryGlazeContract.MIN_GRID_TILE_DP >= 78)
    }

    @Test
    fun `album grid stays more spacious than the media timeline`() {
        assertEquals(2, GalleryGlazeContract.albumGridColumns(390))
        assertEquals(3, GalleryGlazeContract.albumGridColumns(820))
        assertEquals(4, GalleryGlazeContract.albumGridColumns(900))
        assertEquals(5, GalleryGlazeContract.albumGridColumns(1280))
        assertTrue(GalleryGlazeContract.MIN_ALBUM_TILE_DP > GalleryGlazeContract.MIN_GRID_TILE_DP)
    }

    @Test
    fun `navigation capsule preserves current V1_6 baseline and local icon semantics`() {
        assertEquals(54, GalleryGlazeContract.NAVIGATION_HEIGHT_DP)
        assertEquals(26, GalleryGlazeContract.NAVIGATION_RADIUS_DP)
        assertEquals(24, GalleryGlazeContract.NAVIGATION_SIDE_MARGIN_DP)
        assertEquals(10, GalleryGlazeContract.NAVIGATION_BOTTOM_MARGIN_DP)
        assertEquals(4, GalleryGlazeContract.NAVIGATION_ELEVATION_DP)
        assertEquals(76, GalleryGlazeContract.NAVIGATION_RESERVED_SPACE_DP)
        assertEquals(28, GalleryGlazeContract.CONTENT_BOTTOM_INSET_DP)
        assertEquals(20, GalleryGlazeContract.NAVIGATION_ICON_DP)
        assertTrue(
            GalleryGlazeContract.NAVIGATION_RESERVED_SPACE_DP >=
                GalleryGlazeContract.NAVIGATION_HEIGHT_DP + GalleryGlazeContract.NAVIGATION_BOTTOM_MARGIN_DP,
        )
        assertTrue(GalleryGlazeContract.CONTENT_BOTTOM_INSET_DP < GalleryGlazeContract.NAVIGATION_RESERVED_SPACE_DP)
        assertTrue(GalleryGlazeContract.MaterialRole.entries.contains(GalleryGlazeContract.MaterialRole.CANVAS))
        assertTrue(GalleryGlazeContract.MaterialRole.entries.contains(GalleryGlazeContract.MaterialRole.FUNCTIONAL_GLASS))
    }

    @Test
    fun `rendered local library stays bounded`() {
        assertEquals(100, GalleryGlazeContract.MAX_RENDERED_MEDIA_ROWS)
    }
}
