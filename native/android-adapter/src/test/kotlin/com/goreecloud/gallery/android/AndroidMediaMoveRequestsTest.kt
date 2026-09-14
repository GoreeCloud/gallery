package com.goreecloud.gallery.android

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AndroidMediaMoveRequestsTest {
    @Test
    fun `destination relative path is canonicalized without becoming filesystem authority`() {
        assertEquals(
            "DCIM/Camera/",
            AndroidMediaMoveRequests.normalizeDestinationRelativePath(" DCIM\\Camera "),
        )
        assertEquals(
            "Pictures/GoreeCloud/",
            AndroidMediaMoveRequests.normalizeDestinationRelativePath("Pictures/GoreeCloud/"),
        )
    }

    @Test
    fun `unsafe destination paths fail closed`() {
        listOf(
            "",
            "   ",
            "/sdcard/DCIM/Camera",
            "content://media/external/images/media",
            "../Camera",
            "DCIM/../Camera",
            "DCIM/./Camera",
        ).forEach { value ->
            assertFailsWith<IllegalArgumentException> {
                AndroidMediaMoveRequests.normalizeDestinationRelativePath(value)
            }
        }
    }

    @Test
    fun `pending move state restores only exact canonical uri and destination state`() {
        val state = AndroidMediaMoveRequests.restore(
            contentUris = listOf(
                "content://media/external/images/media/10",
                "content://media/external/video/media/11",
            ),
            destinationRelativePath = "Pictures/Trips/",
        )

        assertNotNull(state)
        assertEquals("Pictures/Trips/", state.destinationRelativePath)
        assertEquals(2, state.contentUris.size)
    }

    @Test
    fun `pending move state rejects altered or noncanonical state`() {
        assertNull(
            AndroidMediaMoveRequests.restore(
                contentUris = listOf(" content://media/external/images/media/10 "),
                destinationRelativePath = "Pictures/Trips/",
            ),
        )
        assertNull(
            AndroidMediaMoveRequests.restore(
                contentUris = listOf("content://media/external/images/media/10"),
                destinationRelativePath = "Pictures//Trips",
            ),
        )
        assertNull(
            AndroidMediaMoveRequests.restore(
                contentUris = listOf("content://media/external/images/media/10"),
                destinationRelativePath = "../Trips/",
            ),
        )
    }
}
