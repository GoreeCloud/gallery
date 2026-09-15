package com.goreecloud.gallery

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GalleryGlazeContractTest {
    @Test
    fun `native shell maps current Stable Gallery Glaze source contract at exact authority revision`() {
        assertEquals("1.4.1", GalleryGlazeContract.VERSION)
        assertEquals(
            "4fab9da0fad2e5c974e0e66ec88632c61745751c",
            GalleryGlazeContract.AUTHORITY_REVISION,
        )
        assertTrue(GalleryGlazeContract.GENERAL_TARGET_DP >= 48)
        assertTrue(GalleryGlazeContract.NAVIGATION_HEIGHT_DP >= GalleryGlazeContract.GENERAL_TARGET_DP)
    }

    @Test
    fun `v1_4_1 optical intelligence remains bounded and accessibility safe`() {
        assertTrue(GalleryGlazeContract.OPTICAL_CONTENT_AWARE_FROST_ENABLED)
        assertTrue(GalleryGlazeContract.OPTICAL_SEMANTIC_BLUR_PROTECTION_ENABLED)
        assertTrue(GalleryGlazeContract.OPTICAL_REDUCED_TRANSPARENCY_FALLBACK_REQUIRED)
        assertTrue(GalleryGlazeContract.OPTICAL_INCREASED_CONTRAST_FALLBACK_REQUIRED)
        assertTrue(GalleryGlazeContract.OPTICAL_MEMORY_TINT_MAX_FRACTION in 0f..0.08f)
        assertFalse(GalleryGlazeContract.OPTICAL_ENVIRONMENT_TINT_MAY_OVERRIDE_SEMANTIC_STATE)
    }

    @Test
    fun `shared v1_4_1 qualification does not fabricate Gallery acceptance`() {
        assertFalse(GalleryGlazeContract.GALLERY_PHYSICAL_DEVICE_ACCEPTANCE_ESTABLISHED)
        assertFalse(GalleryGlazeContract.GALLERY_MANUAL_ASSISTIVE_TECH_ACCEPTANCE_ESTABLISHED)
        assertFalse(GalleryGlazeContract.GALLERY_HUMAN_VISUAL_EXCELLENCE_ACCEPTED)
    }

    @Test
    fun `semantic spacing values follow governed Glaze spatial baseline`() {
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
    fun `semantic shape values follow governed Glaze role mapping`() {
        assertEquals(10, GalleryGlazeContract.SHAPE_QUIET_DP)
        assertEquals(12, GalleryGlazeContract.SHAPE_CONTROL_DP)
        assertEquals(20, GalleryGlazeContract.SHAPE_CONTAINER_DP)
        assertEquals(24, GalleryGlazeContract.SHAPE_ROUNDED_DP)
        assertEquals(28, GalleryGlazeContract.SHAPE_OVERLAY_DP)
        assertEquals(999, GalleryGlazeContract.SHAPE_CAPSULE_DP)
        assertEquals(GalleryGlazeContract.SHAPE_CAPSULE_DP, GalleryGlazeContract.NAVIGATION_RADIUS_DP)
    }

    @Test
    fun `semantic motion values follow governed Glaze motion baseline`() {
        assertEquals(160L, GalleryGlazeContract.MOTION_MICRO_MS)
        assertEquals(240L, GalleryGlazeContract.MOTION_STANDARD_MS)
        assertEquals(360L, GalleryGlazeContract.MOTION_CONNECTED_MS)
        assertEquals(480L, GalleryGlazeContract.MOTION_SPATIAL_MS)
        assertEquals(180L, GalleryGlazeContract.MOTION_REDUCED_STANDARD_MS)
        assertEquals(0L, GalleryGlazeContract.MOTION_MINIMAL_MS)
    }

    @Test
    fun `adaptive gutters consume governed spatial roles without treating widths as Glaze device identities`() {
        assertEquals(GalleryGlazeContract.SPACE_STANDARD_CLUSTER_DP, GalleryGlazeContract.horizontalGutterDp(390))
        assertEquals(GalleryGlazeContract.SPACE_CONTENT_DP, GalleryGlazeContract.horizontalGutterDp(820))
        assertEquals(GalleryGlazeContract.SPACE_SECTION_DP, GalleryGlazeContract.horizontalGutterDp(900))
        assertEquals(GalleryGlazeContract.SPACE_REGION_DP, GalleryGlazeContract.horizontalGutterDp(1280))
    }

    @Test
    fun `photo grid remains product specific and media dense`() {
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
    fun `navigation composition consumes semantic spacing and capsule roles`() {
        assertEquals(56, GalleryGlazeContract.NAVIGATION_HEIGHT_DP)
        assertEquals(999, GalleryGlazeContract.NAVIGATION_RADIUS_DP)
        assertEquals(20, GalleryGlazeContract.NAVIGATION_SIDE_MARGIN_DP)
        assertEquals(10, GalleryGlazeContract.NAVIGATION_BOTTOM_MARGIN_DP)
        assertEquals(6, GalleryGlazeContract.NAVIGATION_ELEVATION_DP)
        assertEquals(24, GalleryGlazeContract.CONTENT_BOTTOM_INSET_DP)
        assertTrue(
            GalleryGlazeContract.NAVIGATION_RESERVED_SPACE_DP >=
                GalleryGlazeContract.NAVIGATION_HEIGHT_DP + GalleryGlazeContract.NAVIGATION_BOTTOM_MARGIN_DP,
        )
        assertTrue(GalleryGlazeContract.CONTENT_BOTTOM_INSET_DP < GalleryGlazeContract.NAVIGATION_RESERVED_SPACE_DP)
    }

    @Test
    fun `rendered local library stays bounded`() {
        assertEquals(100, GalleryGlazeContract.MAX_RENDERED_MEDIA_ROWS)
    }
}
