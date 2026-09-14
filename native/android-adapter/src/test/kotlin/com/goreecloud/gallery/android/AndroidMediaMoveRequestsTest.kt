package com.goreecloud.gallery.android

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AndroidMediaMoveRequestsTest {
    @Test
    fun `relative path normalization preserves a bounded MediaStore folder`() {
        assertEquals("DCIM/Camera/", AndroidMediaMoveRequests.normalizeRelativePath("  DCIM/Camera  "))
        assertEquals("Pictures/Screenshots/", AndroidMediaMoveRequests.normalizeRelativePath("Pictures//Screenshots/"))
    }

    @Test
    fun `relative path normalization rejects absolute traversal and unsupported paths`() {
        assertFailsWith<IllegalArgumentException> { AndroidMediaMoveRequests.normalizeRelativePath("") }
        assertFailsWith<IllegalArgumentException> { AndroidMediaMoveRequests.normalizeRelativePath("/DCIM/Camera") }
        assertFailsWith<IllegalArgumentException> { AndroidMediaMoveRequests.normalizeRelativePath("DCIM/../Secrets") }
        assertFailsWith<IllegalArgumentException> { AndroidMediaMoveRequests.normalizeRelativePath("DCIM\\Camera") }
        assertFailsWith<IllegalArgumentException> { AndroidMediaMoveRequests.normalizeRelativePath("DCIM/\u0000Camera") }
    }

    @Test
    fun `pending move state keeps exact canonical item scope and destination`() {
        val destination = AndroidMediaMoveDestination(
            albumId = "camera",
            displayName = "Camera",
            relativePath = "DCIM/Camera/",
        )
        val pending = AndroidMediaMovePendingState(
            contentUris = listOf(
                "content://media/external/images/media/1",
                "content://media/external/video/media/2",
            ),
            destination = destination,
        )

        assertEquals(2, pending.contentUris.size)
        assertEquals(destination, pending.destination)
    }

    @Test
    fun `pending move state rejects non canonical media uris`() {
        assertFailsWith<IllegalArgumentException> {
            AndroidMediaMovePendingState(
                contentUris = listOf("content://media/external/file/1"),
                destination = AndroidMediaMoveDestination("camera", "Camera", "DCIM/Camera/"),
            )
        }
    }

    @Test
    fun `move support begins at Android 11`() {
        assertEquals(false, AndroidMediaMoveRequests.isSupported(29))
        assertEquals(true, AndroidMediaMoveRequests.isSupported(30))
        assertEquals(true, AndroidMediaMoveRequests.isSupported(36))
    }
}
