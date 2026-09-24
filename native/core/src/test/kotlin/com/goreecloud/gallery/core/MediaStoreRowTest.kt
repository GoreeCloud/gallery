package com.goreecloud.gallery.core

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class MediaStoreRowTest {
    @Test
    fun `projection keeps authoritative Android MediaStore columns explicit`() {
        assertEquals(
            listOf(
                "_id",
                "_display_name",
                "mime_type",
                "datetaken",
                "date_modified",
                "width",
                "height",
                "duration",
                "_size",
                "bucket_id",
                "bucket_display_name",
                "relative_path",
            ),
            MediaStoreProjection.columns,
        )
    }

    @Test
    fun `row maps Android time units bucket metadata and relative path into native item`() {
        val item = MediaStoreRow(
            collectionUri = "content://media/external/images/media/",
            id = 42,
            displayName = "  sunrise.jpg  ",
            mimeType = "IMAGE/JPEG",
            dateTakenEpochMillis = 1_700_000_000_123,
            dateModifiedEpochSeconds = 1_700_000_010,
            width = 4032,
            height = 3024,
            durationMillis = 999,
            sizeBytes = 4_096,
            bucketId = "  camera  ",
            bucketDisplayName = "  Camera  ",
            relativePath = " DCIM\\Camera ",
        ).toMediaItem()

        assertEquals("42", item.id)
        assertEquals("content://media/external/images/media/42", item.contentUri)
        assertEquals("sunrise.jpg", item.displayName)
        assertEquals("image/jpeg", item.mimeType)
        assertEquals(Instant.ofEpochMilli(1_700_000_000_123), item.capturedAt)
        assertEquals(Instant.ofEpochSecond(1_700_000_010), item.modifiedAt)
        assertEquals("camera", item.albumId)
        assertEquals("Camera", item.albumName)
        assertEquals("DCIM/Camera/", item.relativePath)
        assertNull(item.durationMillis, "image rows must not leak a provider duration into the core model")
    }

    @Test
    fun `video row preserves duration`() {
        val item = row(mimeType = "video/mp4", durationMillis = 1_234).toMediaItem()
        assertEquals(MediaKind.VIDEO, item.kind)
        assertEquals(1_234, item.durationMillis)
    }

    @Test
    fun `incomplete bucket metadata remains ungrouped rather than fabricating album state`() {
        val onlyId = row(bucketId = "camera", bucketDisplayName = null).toMediaItem()
        val onlyName = row(bucketId = null, bucketDisplayName = "Camera").toMediaItem()

        assertNull(onlyId.albumId)
        assertNull(onlyId.albumName)
        assertNull(onlyName.albumId)
        assertNull(onlyName.albumName)
    }

    @Test
    fun `missing relative path remains absent rather than fabricating a move destination`() {
        assertNull(row(relativePath = null).toMediaItem().relativePath)
        assertNull(row(relativePath = "  ").toMediaItem().relativePath)
    }

    @Test
    fun `invalid provider rows fail closed`() {
        assertFailsWith<IllegalArgumentException> { row(collectionUri = "https://example.test/media") }
        assertFailsWith<IllegalArgumentException> { row(id = -1) }
        assertFailsWith<IllegalArgumentException> { row(displayName = " ") }
        assertFailsWith<IllegalArgumentException> { row(mimeType = "application/pdf") }
        assertFailsWith<IllegalArgumentException> { row(dateModifiedEpochSeconds = -1) }
        assertFailsWith<IllegalArgumentException> { row(sizeBytes = -1) }
    }

    private fun row(
        collectionUri: String = "content://media/external/video/media",
        id: Long = 7,
        displayName: String = "clip.mp4",
        mimeType: String = "video/mp4",
        dateTakenEpochMillis: Long? = null,
        dateModifiedEpochSeconds: Long = 1_700_000_000,
        width: Int? = 1920,
        height: Int? = 1080,
        durationMillis: Long? = 5_000,
        sizeBytes: Long = 123,
        bucketId: String? = null,
        bucketDisplayName: String? = null,
        relativePath: String? = null,
    ) = MediaStoreRow(
        collectionUri = collectionUri,
        id = id,
        displayName = displayName,
        mimeType = mimeType,
        dateTakenEpochMillis = dateTakenEpochMillis,
        dateModifiedEpochSeconds = dateModifiedEpochSeconds,
        width = width,
        height = height,
        durationMillis = durationMillis,
        sizeBytes = sizeBytes,
        bucketId = bucketId,
        bucketDisplayName = bucketDisplayName,
        relativePath = relativePath,
    )
}
