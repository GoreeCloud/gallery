package com.goreecloud.gallery.core

import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GallerySelectionPolicyTest {
    @Test
    fun `toggle only admits items from current scope`() {
        val first = media("1")
        val second = media("2")
        val outside = media("3")

        val selected = GallerySelectionPolicy.toggle(emptySet(), outside, listOf(first, second))

        assertTrue(selected.isEmpty())
    }

    @Test
    fun `toggle adds and removes an authorized item`() {
        val first = media("1")
        val scope = listOf(first)

        val selected = GallerySelectionPolicy.toggle(emptySet(), first, scope)
        assertEquals(setOf(first.contentUri), selected)

        val cleared = GallerySelectionPolicy.toggle(selected, first, scope)
        assertTrue(cleared.isEmpty())
    }

    @Test
    fun `explicit selection state is idempotent for drag revisits`() {
        val first = media("1")
        val scope = listOf(first)

        val selected = GallerySelectionPolicy.setSelected(emptySet(), first, scope, selected = true)
        val revisited = GallerySelectionPolicy.setSelected(selected, first, scope, selected = true)

        assertEquals(setOf(first.contentUri), revisited)

        val cleared = GallerySelectionPolicy.setSelected(revisited, first, scope, selected = false)
        val revisitedClear = GallerySelectionPolicy.setSelected(cleared, first, scope, selected = false)

        assertTrue(revisitedClear.isEmpty())
    }

    @Test
    fun `explicit selection state rejects foreign items`() {
        val first = media("1")
        val outside = media("2")

        assertTrue(
            GallerySelectionPolicy.setSelected(
                selectedContentUris = emptySet(),
                item = outside,
                currentScope = listOf(first),
                selected = true,
            ).isEmpty(),
        )
    }

    @Test
    fun `range selection changes only items inside current scope`() {
        val first = media("1")
        val second = media("2")
        val third = media("3")
        val outside = media("4")
        val scope = listOf(first, second, third)

        val selected = GallerySelectionPolicy.setSelectedRange(
            selectedContentUris = setOf(first.contentUri),
            items = listOf(second, third, outside),
            currentScope = scope,
            selected = true,
        )

        assertEquals(
            linkedSetOf(first.contentUri, second.contentUri, third.contentUri),
            selected,
        )

        val deselected = GallerySelectionPolicy.setSelectedRange(
            selectedContentUris = selected,
            items = listOf(first, third, outside),
            currentScope = scope,
            selected = false,
        )

        assertEquals(setOf(second.contentUri), deselected)
    }

    @Test
    fun `prune removes stale selection when presentation scope changes`() {
        val first = media("1")
        val second = media("2")

        val pruned = GallerySelectionPolicy.prune(
            setOf(first.contentUri, second.contentUri, "content://media/external/file/999"),
            listOf(second),
        )

        assertEquals(setOf(second.contentUri), pruned)
    }

    @Test
    fun `resolve preserves current presentation order and ignores foreign uris`() {
        val first = media("1")
        val second = media("2")
        val third = media("3")
        val scope = listOf(third, first, second)

        val resolved = GallerySelectionPolicy.resolve(
            scope,
            setOf(first.contentUri, third.contentUri, "content://foreign/not-authorized"),
        )

        assertEquals(listOf(third, first), resolved)
    }

    @Test
    fun `select all is bounded to the supplied current scope`() {
        val first = media("1")
        val second = media("2")

        assertEquals(
            linkedSetOf(first.contentUri, second.contentUri),
            GallerySelectionPolicy.selectAll(listOf(first, second)),
        )
    }

    private fun media(id: String): MediaItem = MediaItem(
        id = id,
        contentUri = "content://media/external/file/$id",
        displayName = "item-$id.jpg",
        mimeType = "image/jpeg",
        capturedAt = Instant.parse("2026-08-31T12:00:00Z"),
        modifiedAt = Instant.parse("2026-08-31T12:00:00Z"),
        width = 1080,
        height = 1920,
        durationMillis = null,
        sizeBytes = 1024,
        albumId = "camera",
        albumName = "Camera",
    )
}
