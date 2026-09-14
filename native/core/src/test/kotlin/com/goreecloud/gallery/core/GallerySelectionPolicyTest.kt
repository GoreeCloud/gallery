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
    fun `explicit selection state is idempotent for sweep gestures`() {
        val first = media("1")
        val scope = listOf(first)

        val selected = GallerySelectionPolicy.setSelected(emptySet(), first, scope, selected = true)
        val replayed = GallerySelectionPolicy.setSelected(selected, first, scope, selected = true)
        assertEquals(selected, replayed)

        val cleared = GallerySelectionPolicy.setSelected(replayed, first, scope, selected = false)
        val replayedClear = GallerySelectionPolicy.setSelected(cleared, first, scope, selected = false)
        assertEquals(cleared, replayedClear)
    }

    @Test
    fun `range selection follows current presentation order inclusively`() {
        val items = (1..6).map { media(it.toString()) }
        val selected = GallerySelectionPolicy.applyRange(
            selectedContentUris = setOf(items[0].contentUri),
            currentScope = items,
            anchorContentUri = items[1].contentUri,
            targetContentUri = items[4].contentUri,
            selected = true,
        )

        assertEquals(
            linkedSetOf(
                items[0].contentUri,
                items[1].contentUri,
                items[2].contentUri,
                items[3].contentUri,
                items[4].contentUri,
            ),
            selected,
        )
    }

    @Test
    fun `range deselection removes range without changing authorized selections outside it`() {
        val items = (1..6).map { media(it.toString()) }
        val selected = GallerySelectionPolicy.selectAll(items)
        val updated = GallerySelectionPolicy.applyRange(
            selectedContentUris = selected,
            currentScope = items,
            anchorContentUri = items[1].contentUri,
            targetContentUri = items[3].contentUri,
            selected = false,
        )

        assertEquals(linkedSetOf(items[0].contentUri, items[4].contentUri, items[5].contentUri), updated)
    }

    @Test
    fun `range selection fails closed when either endpoint is outside current scope`() {
        val first = media("1")
        val second = media("2")
        val outside = media("3")
        val existing = setOf(first.contentUri)

        assertEquals(
            existing,
            GallerySelectionPolicy.applyRange(
                selectedContentUris = existing,
                currentScope = listOf(first, second),
                anchorContentUri = first.contentUri,
                targetContentUri = outside.contentUri,
                selected = true,
            ),
        )
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
