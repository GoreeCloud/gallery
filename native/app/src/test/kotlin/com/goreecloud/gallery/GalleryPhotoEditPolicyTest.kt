package com.goreecloud.gallery

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GalleryPhotoEditPolicyTest {
    @Test
    fun `quarter turn rotation wraps and resets crop`() {
        val cropped = GalleryPhotoEditPlan(
            rotationQuarterTurns = 3,
            crop = GalleryNormalizedCrop(0.1f, 0.2f, 0.9f, 0.8f),
        )

        val right = GalleryPhotoEditPolicy.rotateRight(cropped)
        assertEquals(0, right.rotationQuarterTurns)
        assertEquals(GalleryNormalizedCrop.FULL, right.crop)

        val left = GalleryPhotoEditPolicy.rotateLeft(GalleryPhotoEditPlan())
        assertEquals(3, left.rotationQuarterTurns)
        assertEquals(GalleryNormalizedCrop.FULL, left.crop)
    }

    @Test
    fun `horizontal flip is reversible`() {
        val flipped = GalleryPhotoEditPolicy.flipHorizontal(GalleryPhotoEditPlan())
        assertTrue(flipped.flipHorizontal)
        assertFalse(GalleryPhotoEditPolicy.flipHorizontal(flipped).flipHorizontal)
    }

    @Test
    fun `aspect presets remain centered and inside the image`() {
        val squareFromLandscape = GalleryPhotoEditPolicy.centerCropForAspect(4000, 3000, 1f)
        assertEquals(0f, squareFromLandscape.top)
        assertEquals(1f, squareFromLandscape.bottom)
        assertTrue(squareFromLandscape.left > 0f)
        assertTrue(squareFromLandscape.right < 1f)
        assertTrue(abs(squareFromLandscape.left - (1f - squareFromLandscape.right)) < 0.0001f)

        val sixteenNineFromPortrait = GalleryPhotoEditPolicy.centerCropForAspect(3000, 4000, 16f / 9f)
        assertEquals(0f, sixteenNineFromPortrait.left)
        assertEquals(1f, sixteenNineFromPortrait.right)
        assertTrue(sixteenNineFromPortrait.top > 0f)
        assertTrue(sixteenNineFromPortrait.bottom < 1f)
    }

    @Test
    fun `moving crop preserves size and clamps to bounds`() {
        val crop = GalleryNormalizedCrop(0.2f, 0.2f, 0.7f, 0.8f)
        val moved = GalleryPhotoEditPolicy.moveCrop(crop, 0.8f, -0.8f)

        assertEquals(crop.width, moved.width)
        assertEquals(crop.height, moved.height)
        assertEquals(0.5f, moved.left)
        assertEquals(1f, moved.right)
        assertEquals(0f, moved.top)
        assertEquals(0.6f, moved.bottom)
    }

    @Test
    fun `resizing crop enforces minimum dimensions`() {
        val crop = GalleryNormalizedCrop(0.1f, 0.1f, 0.9f, 0.9f)
        val resized = GalleryPhotoEditPolicy.resizeCrop(
            crop = crop,
            handle = GalleryCropHandle.TOP_LEFT,
            deltaX = 0.95f,
            deltaY = 0.95f,
        )

        assertTrue(resized.width >= GalleryPhotoEditPolicy.MIN_NORMALIZED_CROP_SIZE - 0.0001f)
        assertTrue(resized.height >= GalleryPhotoEditPolicy.MIN_NORMALIZED_CROP_SIZE - 0.0001f)
        assertEquals(0.9f, resized.right)
        assertEquals(0.9f, resized.bottom)
    }

    @Test
    fun `pixel crop is bounded and nonempty`() {
        val pixels = GalleryPhotoEditPolicy.toPixelCrop(
            crop = GalleryNormalizedCrop(0.25f, 0.25f, 0.75f, 0.75f),
            imageWidth = 4000,
            imageHeight = 3000,
        )

        assertEquals(GalleryPixelCrop(left = 1000, top = 750, width = 2000, height = 1500), pixels)
    }

    @Test
    fun `reset clears every transform`() {
        val reset = GalleryPhotoEditPolicy.reset()
        assertEquals(0, reset.rotationQuarterTurns)
        assertFalse(reset.flipHorizontal)
        assertEquals(GalleryNormalizedCrop.FULL, reset.crop)
    }
}
