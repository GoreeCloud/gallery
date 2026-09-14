package com.goreecloud.gallery.core

/**
 * Pure selection policy for the native Gallery browsing surfaces.
 *
 * Selection is presentation state over a caller-supplied current scope. It never creates media
 * authority: content URIs that are not in the current scope are pruned and cannot be resolved for
 * bulk actions. Sweep/range helpers preserve the same rule and are deterministic so touch gestures
 * never gain authority beyond what the current Gallery surface already presents.
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

    /** Set one item to an explicit selection state without toggle jitter. */
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
     * Apply one desired state to the inclusive presentation-order range between two authorized URIs.
     *
     * This is suitable for touch sweep/drag selection. Replaying the same range is idempotent, so
     * pointer jitter over already-visited tiles cannot repeatedly toggle them on and off.
     */
    fun applyRange(
        selectedContentUris: Set<String>,
        currentScope: List<MediaItem>,
        anchorContentUri: String,
        targetContentUri: String,
        selected: Boolean,
    ): Set<String> {
        val valid = prune(selectedContentUris, currentScope).toMutableSet()
        val anchor = currentScope.indexOfFirst { it.contentUri == anchorContentUri }
        val target = currentScope.indexOfFirst { it.contentUri == targetContentUri }
        if (anchor < 0 || target < 0) return valid

        val start = minOf(anchor, target)
        val end = maxOf(anchor, target)
        currentScope.subList(start, end + 1).forEach { item ->
            if (selected) valid.add(item.contentUri) else valid.remove(item.contentUri)
        }
        return valid
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
