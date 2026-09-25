package com.goreecloud.gallery

/**
 * Process-recreation state for the first-party photo editor.
 *
 * This snapshot intentionally contains only transform scalars. The source URI remains owned by
 * the Activity intent and Android media authorization; bitmap bytes and provider metadata are not
 * persisted here.
 */
data class GalleryPhotoEditStateSnapshot(
    val rotationQuarterTurns: Int,
    val flipHorizontal: Boolean,
    val cropLeft: Float,
    val cropTop: Float,
    val cropRight: Float,
    val cropBottom: Float,
)

object GalleryPhotoEditStatePolicy {
    fun snapshot(plan: GalleryPhotoEditPlan): GalleryPhotoEditStateSnapshot =
        GalleryPhotoEditStateSnapshot(
            rotationQuarterTurns = plan.rotationQuarterTurns,
            flipHorizontal = plan.flipHorizontal,
            cropLeft = plan.crop.left,
            cropTop = plan.crop.top,
            cropRight = plan.crop.right,
            cropBottom = plan.crop.bottom,
        )

    fun restore(snapshot: GalleryPhotoEditStateSnapshot?): GalleryPhotoEditPlan? {
        snapshot ?: return null
        return runCatching {
            val crop = GalleryNormalizedCrop(
                left = snapshot.cropLeft,
                top = snapshot.cropTop,
                right = snapshot.cropRight,
                bottom = snapshot.cropBottom,
            )
            require(crop.width >= GalleryPhotoEditPolicy.MIN_NORMALIZED_CROP_SIZE)
            require(crop.height >= GalleryPhotoEditPolicy.MIN_NORMALIZED_CROP_SIZE)

            GalleryPhotoEditPlan(
                rotationQuarterTurns = snapshot.rotationQuarterTurns,
                flipHorizontal = snapshot.flipHorizontal,
                crop = crop,
            )
        }.getOrNull()
    }
}
