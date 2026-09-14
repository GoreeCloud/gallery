package com.goreecloud.gallery.core

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class GalleryBulkActionPolicyTest {
    @Test
    fun `share plan rejects a selection outside current scope`() {
        assertNull(
            GalleryBulkActionPolicy.sharePlan(
                currentScope = listOf(media("1", "image/jpeg")),
                selectedContentUris = setOf("content://media/external/file/999"),
            ),
        )
    }

    @Test
    fun `share plan preserves current scope order and exact common mime type`() {
        val first = media("1", "image/jpeg")
        val second = media("2", "image/jpeg")

        val plan = GalleryBulkActionPolicy.sharePlan(
            currentScope = listOf(second, first),
            selectedContentUris = setOf(first.contentUri, second.contentUri),
        )

        assertEquals("image/jpeg", plan?.mimeType)
        assertEquals(listOf(second.contentUri, first.contentUri), plan?.contentUris)
    }

    @Test
    fun `share plan broadens only to image wildcard for mixed image mime types`() {
        val jpeg = media("1", "image/jpeg")
        val png = media("2", "image/png")

        assertEquals(
            "image/*",
            GalleryBulkActionPolicy.sharePlan(
                currentScope = listOf(jpeg, png),
                selectedContentUris = setOf(jpeg.contentUri, png.contentUri),
            )?.mimeType,
        )
    }

    @Test
    fun `share plan uses generic wildcard for mixed image and video selection`() {
        val image = media("1", "image/jpeg")
        val video = media("2", "video/mp4")

        assertEquals(
            "*/*",
            GalleryBulkActionPolicy.sharePlan(
                currentScope = listOf(image, video),
                selectedContentUris = setOf(image.contentUri, video.contentUri),
            )?.mimeType,
        )
    }

    @Test
    fun `favorite action adds unless every selected item is already a favorite`() {
        val first = media("1", "image/jpeg")
        val second = media("2", "image/jpeg")
        val scope = listOf(first, second)
        val selected = setOf(first.contentUri, second.contentUri)

        assertEquals(
            GalleryFavoriteBulkAction.ADD,
            GalleryBulkActionPolicy.favoriteAction(scope, selected, setOf(first.contentUri)),
        )
        assertEquals(
            GalleryFavoriteBulkAction.REMOVE,
            GalleryBulkActionPolicy.favoriteAction(scope, selected, selected),
        )
    }

    @Test
    fun `move plan is bounded to authorized selection and excludes destination residents`() {
        val cameraItem = media("1", "image/jpeg", albumId = "camera", albumName = "Camera")
        val downloadItem = media("2", "image/jpeg", albumId = "downloads", albumName = "Downloads")
        val destination = album("downloads", "Downloads", downloadItem.id)

        val plan = GalleryBulkActionPolicy.movePlan(
            currentScope = listOf(cameraItem, downloadItem),
            selectedContentUris = setOf(cameraItem.contentUri, downloadItem.contentUri, "content://foreign/999"),
            destination = destination,
        )

        assertEquals("downloads", plan?.destinationAlbumId)
        assertEquals("Downloads", plan?.destinationAlbumName)
        assertEquals(listOf(cameraItem.contentUri), plan?.contentUris)
    }

    @Test
    fun `move plan is null when every selected item already belongs to destination`() {
        val item = media("1", "image/jpeg", albumId = "camera", albumName = "Camera")
        assertNull(
            GalleryBulkActionPolicy.movePlan(
                currentScope = listOf(item),
                selectedContentUris = setOf(item.contentUri),
                destination = album("camera", "Camera", item.id),
            ),
        )
    }

    @Test
    fun `move plan enforces Android mutation item bound`() {
        val items = (1..101).map { media(it.toString(), "image/jpeg", albumId = "camera", albumName = "Camera") }
        assertFailsWith<IllegalArgumentException> {
            GalleryBulkActionPolicy.movePlan(
                currentScope = items,
                selectedContentUris = items.mapTo(linkedSetOf()) { it.contentUri },
                destination = album("downloads", "Downloads", "cover"),
            )
        }
    }

    private fun album(id: String, name: String, coverId: String): MediaAlbum = MediaAlbum(
        id = id,
        displayName = name,
        itemCount = 1,
        coverItemId = coverId,
        newestAt = Instant.parse("2026-08-31T12:00:00Z"),
    )

    private fun media(
        id: String,
        mimeType: String,
        albumId: String = "camera",
        albumName: String = "Camera",
    ): MediaItem = MediaItem(
        id = id,
        contentUri = "content://media/external/file/$id",
        displayName = "item-$id",
        mimeType = mimeType,
        capturedAt = Instant.parse("2026-08-31T12:00:00Z"),
        modifiedAt = Instant.parse("2026-08-31T12:00:00Z"),
        width = 1080,
        height = 1920,
        durationMillis = if (mimeType.startsWith("video/")) 5_000 else null,
        sizeBytes = 1024,
        albumId = albumId,
        albumName = albumName,
    )
}
