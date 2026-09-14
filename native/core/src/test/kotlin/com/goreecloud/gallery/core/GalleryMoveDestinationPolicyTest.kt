package com.goreecloud.gallery.core

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GalleryMoveDestinationPolicyTest {
    @Test
    fun `destinations come only from current authoritative album paths`() {
        val camera = media("1", "camera", "Camera", "DCIM/Camera/")
        val screenshots = media("2", "screens", "Screenshots", "Pictures/Screenshots/")
        val downloads = media("3", "downloads", "Download", "Download/")
        val noPath = media("4", "legacy", "Legacy", null)

        val destinations = GalleryMoveDestinationPolicy.existingDestinations(
            currentScope = listOf(camera, screenshots, downloads, noPath),
            selectedContentUris = setOf(camera.contentUri),
        )

        assertEquals(listOf("Download", "Screenshots"), destinations.map { it.displayName })
        assertEquals(listOf("Download/", "Pictures/Screenshots/"), destinations.map { it.relativePath })
    }

    @Test
    fun `current folder is removed when every selected item already resides there`() {
        val first = media("1", "camera", "Camera", "DCIM/Camera/")
        val second = media("2", "camera", "Camera", "DCIM/Camera/")
        val target = media("3", "trips", "Trips", "Pictures/Trips/")

        val destinations = GalleryMoveDestinationPolicy.existingDestinations(
            currentScope = listOf(first, second, target),
            selectedContentUris = setOf(first.contentUri, second.contentUri),
        )

        assertEquals(listOf("Trips"), destinations.map { it.displayName })
    }

    @Test
    fun `conflicting provider metadata does not become a destination`() {
        val selected = media("1", "camera", "Camera", "DCIM/Camera/")
        val conflictA = media("2", "shared", "Shared", "Pictures/One/")
        val conflictB = media("3", "shared", "Shared", "Pictures/Two/")

        val destinations = GalleryMoveDestinationPolicy.existingDestinations(
            currentScope = listOf(selected, conflictA, conflictB),
            selectedContentUris = setOf(selected.contentUri),
        )

        assertTrue(destinations.isEmpty())
    }

    @Test
    fun `foreign selections cannot manufacture move destinations`() {
        val camera = media("1", "camera", "Camera", "DCIM/Camera/")
        val trips = media("2", "trips", "Trips", "Pictures/Trips/")

        assertTrue(
            GalleryMoveDestinationPolicy.existingDestinations(
                currentScope = listOf(camera, trips),
                selectedContentUris = setOf("content://foreign/not-authorized"),
            ).isEmpty(),
        )
    }

    private fun media(
        id: String,
        albumId: String,
        albumName: String,
        relativePath: String?,
    ): MediaItem = MediaItem(
        id = id,
        contentUri = "content://media/external/images/media/$id",
        displayName = "item-$id.jpg",
        mimeType = "image/jpeg",
        capturedAt = Instant.parse("2026-09-14T08:00:00Z"),
        modifiedAt = Instant.parse("2026-09-14T08:00:00Z"),
        width = 1080,
        height = 1920,
        durationMillis = null,
        sizeBytes = 1024,
        albumId = albumId,
        albumName = albumName,
        relativePath = relativePath,
    )
}
