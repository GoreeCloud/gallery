package com.goreecloud.gallery

import kotlin.math.abs

enum class GalleryViewerSwipeAction {
    PREVIOUS,
    NEXT,
    NONE,
}

object GalleryViewerSwipePolicy {
    fun resolve(
        deltaX: Float,
        deltaY: Float,
        minimumDistancePx: Float,
        canGoPrevious: Boolean,
        canGoNext: Boolean,
    ): GalleryViewerSwipeAction {
        if (minimumDistancePx <= 0f) return GalleryViewerSwipeAction.NONE
        if (abs(deltaX) < minimumDistancePx || abs(deltaX) <= abs(deltaY)) {
            return GalleryViewerSwipeAction.NONE
        }

        return when {
            deltaX > 0f && canGoPrevious -> GalleryViewerSwipeAction.PREVIOUS
            deltaX < 0f && canGoNext -> GalleryViewerSwipeAction.NEXT
            else -> GalleryViewerSwipeAction.NONE
        }
    }
}
