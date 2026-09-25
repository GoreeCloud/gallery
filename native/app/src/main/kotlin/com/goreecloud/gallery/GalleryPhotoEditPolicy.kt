package com.goreecloud.gallery

import kotlin.math.ceil
import kotlin.math.floor

/**
 * Normalized crop rectangle in the currently rendered, post-transform photo space.
 * Coordinates are inclusive at the leading edge and exclusive at the trailing edge.
 */
data class GalleryNormalizedCrop(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
) {
    init {
        require(left.isFinite() && top.isFinite() && right.isFinite() && bottom.isFinite())
        require(left in 0f..1f && top in 0f..1f && right in 0f..1f && bottom in 0f..1f)
        require(right > left)
        require(bottom > top)
    }

    val width: Float get() = right - left
    val height: Float get() = bottom - top

    companion object {
        val FULL = GalleryNormalizedCrop(0f, 0f, 1f, 1f)
    }
}

enum class GalleryCropHandle {
    TOP_LEFT,
    TOP_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_RIGHT,
}

data class GalleryPhotoEditPlan(
    val rotationQuarterTurns: Int = 0,
    val flipHorizontal: Boolean = false,
    val crop: GalleryNormalizedCrop = GalleryNormalizedCrop.FULL,
) {
    init {
        require(rotationQuarterTurns in 0..3)
    }
}

data class GalleryPixelCrop(
    val left: Int,
    val top: Int,
    val width: Int,
    val height: Int,
)

object GalleryPhotoEditPolicy {
    const val MIN_NORMALIZED_CROP_SIZE = 0.10f

    fun rotateLeft(plan: GalleryPhotoEditPlan): GalleryPhotoEditPlan =
        plan.copy(
            rotationQuarterTurns = (plan.rotationQuarterTurns + 3) % 4,
            crop = GalleryNormalizedCrop.FULL,
        )

    fun rotateRight(plan: GalleryPhotoEditPlan): GalleryPhotoEditPlan =
        plan.copy(
            rotationQuarterTurns = (plan.rotationQuarterTurns + 1) % 4,
            crop = GalleryNormalizedCrop.FULL,
        )

    fun flipHorizontal(plan: GalleryPhotoEditPlan): GalleryPhotoEditPlan =
        plan.copy(flipHorizontal = !plan.flipHorizontal)

    fun reset(): GalleryPhotoEditPlan = GalleryPhotoEditPlan()

    fun centerCropForAspect(
        imageWidth: Int,
        imageHeight: Int,
        targetAspect: Float,
    ): GalleryNormalizedCrop {
        require(imageWidth > 0 && imageHeight > 0)
        require(targetAspect.isFinite() && targetAspect > 0f)

        val sourceAspect = imageWidth.toFloat() / imageHeight.toFloat()
        return if (sourceAspect > targetAspect) {
            val normalizedWidth = (targetAspect / sourceAspect).coerceIn(MIN_NORMALIZED_CROP_SIZE, 1f)
            val left = (1f - normalizedWidth) / 2f
            GalleryNormalizedCrop(left, 0f, left + normalizedWidth, 1f)
        } else {
            val normalizedHeight = (sourceAspect / targetAspect).coerceIn(MIN_NORMALIZED_CROP_SIZE, 1f)
            val top = (1f - normalizedHeight) / 2f
            GalleryNormalizedCrop(0f, top, 1f, top + normalizedHeight)
        }
    }

    fun moveCrop(
        crop: GalleryNormalizedCrop,
        deltaX: Float,
        deltaY: Float,
    ): GalleryNormalizedCrop {
        if (!deltaX.isFinite() || !deltaY.isFinite()) return crop

        val left = (crop.left + deltaX).coerceIn(0f, 1f - crop.width)
        val top = (crop.top + deltaY).coerceIn(0f, 1f - crop.height)
        return GalleryNormalizedCrop(left, top, left + crop.width, top + crop.height)
    }

    fun resizeCrop(
        crop: GalleryNormalizedCrop,
        handle: GalleryCropHandle,
        deltaX: Float,
        deltaY: Float,
    ): GalleryNormalizedCrop {
        if (!deltaX.isFinite() || !deltaY.isFinite()) return crop

        var left = crop.left
        var top = crop.top
        var right = crop.right
        var bottom = crop.bottom

        when (handle) {
            GalleryCropHandle.TOP_LEFT -> {
                left = (left + deltaX).coerceIn(0f, right - MIN_NORMALIZED_CROP_SIZE)
                top = (top + deltaY).coerceIn(0f, bottom - MIN_NORMALIZED_CROP_SIZE)
            }
            GalleryCropHandle.TOP_RIGHT -> {
                right = (right + deltaX).coerceIn(left + MIN_NORMALIZED_CROP_SIZE, 1f)
                top = (top + deltaY).coerceIn(0f, bottom - MIN_NORMALIZED_CROP_SIZE)
            }
            GalleryCropHandle.BOTTOM_LEFT -> {
                left = (left + deltaX).coerceIn(0f, right - MIN_NORMALIZED_CROP_SIZE)
                bottom = (bottom + deltaY).coerceIn(top + MIN_NORMALIZED_CROP_SIZE, 1f)
            }
            GalleryCropHandle.BOTTOM_RIGHT -> {
                right = (right + deltaX).coerceIn(left + MIN_NORMALIZED_CROP_SIZE, 1f)
                bottom = (bottom + deltaY).coerceIn(top + MIN_NORMALIZED_CROP_SIZE, 1f)
            }
        }

        return GalleryNormalizedCrop(left, top, right, bottom)
    }

    fun toPixelCrop(
        crop: GalleryNormalizedCrop,
        imageWidth: Int,
        imageHeight: Int,
    ): GalleryPixelCrop {
        require(imageWidth > 0 && imageHeight > 0)

        val left = floor(crop.left * imageWidth).toInt().coerceIn(0, imageWidth - 1)
        val top = floor(crop.top * imageHeight).toInt().coerceIn(0, imageHeight - 1)
        val right = ceil(crop.right * imageWidth).toInt().coerceIn(left + 1, imageWidth)
        val bottom = ceil(crop.bottom * imageHeight).toInt().coerceIn(top + 1, imageHeight)

        return GalleryPixelCrop(
            left = left,
            top = top,
            width = right - left,
            height = bottom - top,
        )
    }
}
