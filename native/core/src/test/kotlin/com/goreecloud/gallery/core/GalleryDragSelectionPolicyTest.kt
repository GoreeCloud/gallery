package com.goreecloud.gallery.core

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GalleryDragSelectionPolicyTest {
    @Test
    fun `drag beginning on unselected item enters select mode`() {
        val first = media("1")
        val second = media("2")
        val scope = listOf(first, second)

        val result = assertNotNull(
            GalleryDragSelectionPolicy.begin(
                selectedContentUris = emptySet(),
                item = first,
                currentScope = scope,
            ),
        )

        assertTrue(result.session.selecting)
        assertEquals(setOf(first.contentUri), result.selectedContentUris)
        assertEquals(setOf(first.contentUri), result.session.visitedContentUris)
    }

    @Test
    fun `drag beginning on selected item enters deselect mode`() {
        val first = media("1")
        val second = media("2")
        val scope = listOf(first, second)

        val result = assertNotNull(
            GalleryDragSelectionPolicy.begin(
                selectedContentUris = setOf(first.contentUri, second.contentUri),
                item = first,
                currentScope = scope,
            ),
        )

        assertFalse(result.session.selecting)
        assertEquals(setOf(second.contentUri), result.selectedContentUris)
    }

    @Test
    fun `drag applies target state once per visited item`() {
        val first = media("1")
        val second = media("2")
        val scope = listOf(first, second)
        val start = assertNotNull(
            GalleryDragSelectionPolicy.begin(emptySet(), first, scope),
        )

        val crossed = GalleryDragSelectionPolicy.apply(
            selectedContentUris = start.selectedContentUris,
            session = start.session,
            item = second,
            currentScope = scope,
        )
        val revisited = GalleryDragSelectionPolicy.apply(
            selectedContentUris = crossed.selectedContentUris,
            session = crossed.session,
            item = second,
            currentScope = scope,
        )

        assertEquals(setOf(first.contentUri, second.contentUri), crossed.selectedContentUris)
        assertEquals(crossed, revisited)
    }

    @Test
    fun `drag never admits item outside current scope`() {
        val first = media("1")
        val outside = media("2")
        val scope = listOf(first)
        val start = assertNotNull(
            GalleryDragSelectionPolicy.begin(emptySet(), first, scope),
        )

        val result = GalleryDragSelectionPolicy.apply(
            selectedContentUris = start.selectedContentUris,
            session = start.session,
            item = outside,
            currentScope = scope,
        )

        assertEquals(setOf(first.contentUri), result.selectedContentUris)
        assertEquals(start.session, result.session)
    }

    @Test
    fun `drag cannot begin outside current scope`() {
        val first = media("1")
        val outside = media("2")

        assertNull(
            GalleryDragSelectionPolicy.begin(
                selectedContentUris = emptySet(),
                item = outside,
                currentScope = listOf(first),
            ),
        )
    }

    @Test
    fun `stale selected uris are pruned while drag continues`() {
        val first = media("1")
        val second = media("2")
        val stale = media("3")
        val scope = listOf(first, second)
        val start = assertNotNull(
            GalleryDragSelectionPolicy.begin(
                selectedContentUris = setOf(stale.contentUri),
                item = first,
                currentScope = scope,
            ),
        )

        val result = GalleryDragSelectionPolicy.apply(
            selectedContentUris = start.selectedContentUris + stale.contentUri,
            session = start.session,
            item = second,
            currentScope = scope,
        )

        assertEquals(setOf(first.contentUri, second.contentUri), result.selectedContentUris)
    }

    private fun media(id: String): MediaItem = MediaItem(
        id = id,
        contentUri = "content://media/external/images/media/$id",
        displayName = "item-$id.jpg",
        mimeType = "image/jpeg",
        capturedAt = Instant.parse("2026-09-14T08:00:00Z"),
        modifiedAt = Instant.parse("2026-09-14T08:00:00Z"),
        width = 1080,
        height = 1920,
        durationMillis = null,
        sizeBytes = 2048,
        albumId = "camera",
        albumName = "Camera",
    )
}
