package com.goreecloud.gallery.core

/**
 * Pure selection policy for the native Gallery browsing surfaces.
 *
 * Selection is presentation state over a caller-supplied current scope. It never creates media
 * authority: content URIs that are not in the current scope are pruned and cannot be resolved for
 * bulk actions.
 */
object GallerySelectionPolicy {
    fun toggle(
        selectedContentUris: Set<String>,
        item: MediaItem,
        currentScope: List<MediaItem>,
    ): Set<String> = setSelected(
        selectedContentUris = selectedContentUris,
        item = item,
        currentScope = currentScope,
        selected = item.contentUri !in prune(selectedContentUris, currentScope),
    )

    /**
     * Applies an explicit selected/unselected state while preserving the current authorized scope.
     *
     * Drag-to-select uses explicit state instead of repeated toggle semantics so revisiting a tile
     * during one gesture cannot accidentally invert it a second time.
     */
    fun setSelected(
        selectedContentUris: Set<String>,
        item: MediaItem,
        currentScope: List<MediaItem>,
        selected: Boolean,
    ): Set<String> {
        val valid = prune(selectedContentUris, currentScope).toMutableSet()
        if (currentScope.none { it.contentUri == item.contentUri }) return valid

        if (selected) valid.add(item.contentUri) else valid.remove(item.contentUri)
        return valid
    }

    /**
     * Applies one explicit state to an ordered collection of current-scope items.
     *
     * This is intentionally scope-bounded and is useful for contiguous drag selection, keyboard
     * range selection, and future desktop/tablet pointer selection without expanding media access.
     */
    fun setSelectedRange(
        selectedContentUris: Set<String>,
        items: Collection<MediaItem>,
        currentScope: List<MediaItem>,
        selected: Boolean,
    ): Set<String> {
        val allowed = currentScope.asSequence().map { it.contentUri }.toHashSet()
        val updated = prune(selectedContentUris, currentScope).toMutableSet()
        items.asSequence()
            .map { it.contentUri }
            .filter { it in allowed }
            .forEach { contentUri ->
                if (selected) updated.add(contentUri) else updated.remove(contentUri)
            }
        return updated
    }

    fun selectAll(currentScope: List<MediaItem>): Set<String> =
        currentScope.mapTo(linkedSetOf()) { it.contentUri }

    fun prune(selectedContentUris: Set<String>, currentScope: List<MediaItem>): Set<String> {
        if (selectedContentUris.isEmpty() || currentScope.isEmpty()) return emptySet()
        val allowed = currentScope.asSequence().map { it.contentUri }.toHashSet()
        return selectedContentUris.filterTo(linkedSetOf()) { it in allowed }
    }

    fun resolve(currentScope: List<MediaItem>, selectedContentUris: Set<String>): List<MediaItem> {
        if (selectedContentUris.isEmpty()) return emptyList()
        return currentScope.filter { it.contentUri in selectedContentUris }
    }
}
