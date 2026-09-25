package com.goreecloud.gallery

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GalleryPhotoEditStatePolicyTest {
    @Test
    fun `snapshot round trip preserves bounded edit plan`() {
        val plan = GalleryPhotoEditPlan(
            rotationQuarterTurns = 3,
            flipHorizontal = true,
            crop = GalleryNormalizedCrop(0.12f, 0.2f, 0.88f, 0.92f),
        )

        val restored = GalleryPhotoEditStatePolicy.restore(
            GalleryPhotoEditStatePolicy.snapshot(plan),
        )

        assertEquals(plan, restored)
    }

    @Test
    fun `missing snapshot remains missing`() {
        assertNull(GalleryPhotoEditStatePolicy.restore(null))
    }

    @Test
    fun `invalid rotation fails closed`() {
        assertNull(
            GalleryPhotoEditStatePolicy.restore(
                GalleryPhotoEditStateSnapshot(
                    rotationQuarterTurns = 4,
                    flipHorizontal = false,
                    cropLeft = 0f,
                    cropTop = 0f,
                    cropRight = 1f,
                    cropBottom = 1f,
                ),
            ),
        )
    }

    @Test
    fun `invalid crop fails closed`() {
        assertNull(
            GalleryPhotoEditStatePolicy.restore(
                GalleryPhotoEditStateSnapshot(
                    rotationQuarterTurns = 1,
                    flipHorizontal = true,
                    cropLeft = 0.8f,
                    cropTop = 0f,
                    cropRight = 0.2f,
                    cropBottom = 1f,
                ),
            ),
        )
    }

    @Test
    fun `sub-minimum restored crop fails closed`() {
        assertNull(
            GalleryPhotoEditStatePolicy.restore(
                GalleryPhotoEditStateSnapshot(
                    rotationQuarterTurns = 0,
                    flipHorizontal = false,
                    cropLeft = 0f,
                    cropTop = 0f,
                    cropRight = GalleryPhotoEditPolicy.MIN_NORMALIZED_CROP_SIZE / 2f,
                    cropBottom = 1f,
                ),
            ),
        )
        assertNull(
            GalleryPhotoEditStatePolicy.restore(
                GalleryPhotoEditStateSnapshot(
                    rotationQuarterTurns = 0,
                    flipHorizontal = false,
                    cropLeft = 0f,
                    cropTop = 0f,
                    cropRight = 1f,
                    cropBottom = GalleryPhotoEditPolicy.MIN_NORMALIZED_CROP_SIZE / 2f,
                ),
            ),
        )
    }

    @Test
    fun `nonfinite crop fails closed`() {
        assertNull(
            GalleryPhotoEditStatePolicy.restore(
                GalleryPhotoEditStateSnapshot(
                    rotationQuarterTurns = 0,
                    flipHorizontal = false,
                    cropLeft = Float.NaN,
                    cropTop = 0f,
                    cropRight = 1f,
                    cropBottom = 1f,
                ),
            ),
        )
    }
}
