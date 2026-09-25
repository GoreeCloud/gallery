package com.goreecloud.gallery

import kotlin.test.Test
import kotlin.test.assertEquals

class GalleryViewerSwipePolicyTest {
    @Test
    fun `left swipe advances when next item exists`() {
        assertEquals(
            GalleryViewerSwipeAction.NEXT,
            GalleryViewerSwipePolicy.resolve(
                deltaX = -180f,
                deltaY = 24f,
                minimumDistancePx = 72f,
                canGoPrevious = true,
                canGoNext = true,
            ),
        )
    }

    @Test
    fun `right swipe goes back when previous item exists`() {
        assertEquals(
            GalleryViewerSwipeAction.PREVIOUS,
            GalleryViewerSwipePolicy.resolve(
                deltaX = 180f,
                deltaY = 24f,
                minimumDistancePx = 72f,
                canGoPrevious = true,
                canGoNext = true,
            ),
        )
    }

    @Test
    fun `vertical or short movement is not navigation`() {
        assertEquals(
            GalleryViewerSwipeAction.NONE,
            GalleryViewerSwipePolicy.resolve(
                deltaX = 40f,
                deltaY = 6f,
                minimumDistancePx = 72f,
                canGoPrevious = true,
                canGoNext = true,
            ),
        )
        assertEquals(
            GalleryViewerSwipeAction.NONE,
            GalleryViewerSwipePolicy.resolve(
                deltaX = 90f,
                deltaY = 130f,
                minimumDistancePx = 72f,
                canGoPrevious = true,
                canGoNext = true,
            ),
        )
    }

    @Test
    fun `swipe never escapes collection bounds`() {
        assertEquals(
            GalleryViewerSwipeAction.NONE,
            GalleryViewerSwipePolicy.resolve(
                deltaX = 180f,
                deltaY = 0f,
                minimumDistancePx = 72f,
                canGoPrevious = false,
                canGoNext = true,
            ),
        )
        assertEquals(
            GalleryViewerSwipeAction.NONE,
            GalleryViewerSwipePolicy.resolve(
                deltaX = -180f,
                deltaY = 0f,
                minimumDistancePx = 72f,
                canGoPrevious = true,
                canGoNext = false,
            ),
        )
    }
}
