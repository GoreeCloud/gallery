package com.goreecloud.gallery.core

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class GalleryNewFolderMovePolicyTest {
    @Test
    fun `photo new folder uses Pictures instead of inheriting an unsupported source root`() {
        val first = media("1", "downloads", "Download", "Download/")
        val second = media("2", "downloads", "Download", "Download/")

        val destination = GalleryNewFolderMovePolicy.destinationForSelection(
            currentScope = listOf(first, second),
            selectedContentUris = setOf(first.contentUri, second.contentUri),
            rawFolderName = "  Trip Photos  ",
        )

        assertEquals("Trip Photos", destination.displayName)
        assertEquals("Pictures", destination.parentDisplayName)
        assertEquals("Pictures/Trip Photos/", destination.relativePath)
    }

    @Test
    fun `video new folder uses Movies`() {
        val video = media(
            id = "1",
            albumId = "downloads",
            albumName = "Download",
            relativePath = "Download/",
            mimeType = "video/mp4",
        )

        val destination = GalleryNewFolderMovePolicy.destinationForSelection(
            currentScope = listOf(video),
            selectedContentUris = setOf(video.contentUri),
            rawFolderName = "Clips",
        )

        assertEquals("Movies", destination.parentDisplayName)
        assertEquals("Movies/Clips/", destination.relativePath)
    }

    @Test
    fun `mixed photo and video selection uses DCIM when source authority is shared`() {
        val photo = media("1", "camera", "Camera", "DCIM/Camera/")
        val video = media("2", "camera", "Camera", "DCIM/Camera/", mimeType = "video/mp4")

        val destination = GalleryNewFolderMovePolicy.destinationForSelection(
            currentScope = listOf(photo, video),
            selectedContentUris = setOf(photo.contentUri, video.contentUri),
            rawFolderName = "Trip",
        )

        assertEquals("DCIM", destination.parentDisplayName)
        assertEquals("DCIM/Trip/", destination.relativePath)
    }

    @Test
    fun `mixed source folders cannot silently choose new folder authority`() {
        val download = media("1", "downloads", "Download", "Download/")
        val screenshots = media("2", "screens", "Screenshots", "Pictures/Screenshots/")

        assertNull(
            GalleryNewFolderMovePolicy.parentForSelection(
                currentScope = listOf(download, screenshots),
                selectedContentUris = setOf(download.contentUri, screenshots.contentUri),
            ),
        )
    }

    @Test
    fun `foreign selection prevents new folder authority`() {
        val download = media("1", "downloads", "Download", "Download/")

        assertNull(
            GalleryNewFolderMovePolicy.parentForSelection(
                currentScope = listOf(download),
                selectedContentUris = setOf(download.contentUri, "content://foreign/not-authorized"),
            ),
        )
    }

    @Test
    fun `unsafe folder names fail closed`() {
        listOf("", "   ", ".", "..", "Trips/2026", "Trips\\2026", "Trips:2026", "Trips.", "Bad\u0000Name").forEach { name ->
            assertFailsWith<IllegalArgumentException> {
                GalleryNewFolderMovePolicy.normalizeFolderName(name)
            }
        }
    }

    @Test
    fun `known visible destination collision fails closed`() {
        val selected = media("1", "downloads", "Download", "Download/")
        val existing = media("2", "trips", "Trip Photos", "Pictures/Trip Photos/")

        assertFailsWith<IllegalArgumentException> {
            GalleryNewFolderMovePolicy.destinationForSelection(
                currentScope = listOf(selected, existing),
                selectedContentUris = setOf(selected.contentUri),
                rawFolderName = "trip photos",
            )
        }
    }

    private fun media(
        id: String,
        albumId: String,
        albumName: String,
        relativePath: String?,
        mimeType: String = "image/jpeg",
    ): MediaItem = MediaItem(
        id = id,
        contentUri = if (mimeType.startsWith("video/")) {
            "content://media/external/video/media/$id"
        } else {
            "content://media/external/images/media/$id"
        },
        displayName = if (mimeType.startsWith("video/")) "item-$id.mp4" else "item-$id.jpg",
        mimeType = mimeType,
        capturedAt = Instant.parse("2026-09-14T08:00:00Z"),
        modifiedAt = Instant.parse("2026-09-14T08:00:00Z"),
        width = 1080,
        height = 1920,
        durationMillis = if (mimeType.startsWith("video/")) 1_000 else null,
        sizeBytes = 1024,
        albumId = albumId,
        albumName = albumName,
        relativePath = relativePath,
    )
}
