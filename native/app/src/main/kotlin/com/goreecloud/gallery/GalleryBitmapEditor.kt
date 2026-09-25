package com.goreecloud.gallery

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri

class GalleryPhotoTooLargeException(
    val pixelCount: Long,
    val maximumPixelCount: Long,
) : IllegalArgumentException("Photo has $pixelCount pixels; editor limit is $maximumPixelCount")

object GalleryBitmapEditor {
    const val MAX_EDITABLE_PIXELS = 24_000_000L

    fun decodeAuthorizedPhoto(
        contentResolver: ContentResolver,
        uri: Uri,
        maximumPixelCount: Long = MAX_EDITABLE_PIXELS,
    ): Bitmap {
        require(maximumPixelCount > 0L)
        val source = ImageDecoder.createSource(contentResolver, uri)
        return ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
            val pixelCount = info.size.width.toLong() * info.size.height.toLong()
            if (pixelCount <= 0L || pixelCount > maximumPixelCount) {
                throw GalleryPhotoTooLargeException(pixelCount, maximumPixelCount)
            }
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
        }
    }

    fun transform(source: Bitmap, plan: GalleryPhotoEditPlan): Bitmap {
        if (plan.rotationQuarterTurns == 0 && !plan.flipHorizontal) return source

        val matrix = Matrix().apply {
            if (plan.flipHorizontal) postScale(-1f, 1f)
            if (plan.rotationQuarterTurns != 0) postRotate(plan.rotationQuarterTurns * 90f)
        }
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    fun crop(source: Bitmap, crop: GalleryNormalizedCrop): Bitmap {
        if (crop == GalleryNormalizedCrop.FULL) return source
        val pixels = GalleryPhotoEditPolicy.toPixelCrop(crop, source.width, source.height)
        return Bitmap.createBitmap(source, pixels.left, pixels.top, pixels.width, pixels.height)
    }
}
