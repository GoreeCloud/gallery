package com.goreecloud.gallery.core

/**
 * Pure gesture policy for Gallery drag-to-select.
 *
 * A drag gesture captures one target state at gesture start: starting on an unselected item selects
 * everything crossed by the gesture, while starting on a selected item deselects everything crossed.
 * Re-entering the same item during one gesture is idempotent and never toggles it twice.
 *
 * This policy never expands media authority. Every mutation is delegated through
 * [GallerySelectionPolicy] against the caller-supplied current presentation scope.
 */
data class GalleryDragSelectionSession(
    val selecting: Boolean,
    val visitedContentUris: Set<String>,
) {
    init {
        require(visitedContentUris.none { it.isBlank() })
    }
}

data class GalleryDragSelectionResult(
    val selectedContentUris: Set<String>,
    val session: GalleryDragSelectionSession,
)

object GalleryDragSelectionPolicy {
    fun begin(
        selectedContentUris: Set<String>,
        item: MediaItem,
        currentScope: List<MediaItem>,
    ): GalleryDragSelectionResult? {
        if (currentScope.none { it.contentUri == item.contentUri }) return null

        val bounded = GallerySelectionPolicy.prune(selectedContentUris, currentScope)
        val selecting = item.contentUri !in bounded
        val updated = GallerySelectionPolicy.setSelected(
            selectedContentUris = bounded,
            item = item,
            currentScope = currentScope,
            selected = selecting,
        )
        return GalleryDragSelectionResult(
            selectedContentUris = updated,
            session = GalleryDragSelectionSession(
                selecting = selecting,
                visitedContentUris = linkedSetOf(item.contentUri),
            ),
        )
    }

    fun apply(
        selectedContentUris: Set<String>,
        session: GalleryDragSelectionSession,
        item: MediaItem,
        currentScope: List<MediaItem>,
    ): GalleryDragSelectionResult {
        val bounded = GallerySelectionPolicy.prune(selectedContentUris, currentScope)
        if (
            item.contentUri in session.visitedContentUris ||
            currentScope.none { it.contentUri == item.contentUri }
        ) {
            return GalleryDragSelectionResult(bounded, session)
        }

        val updated = GallerySelectionPolicy.setSelected(
            selectedContentUris = bounded,
            item = item,
            currentScope = currentScope,
            selected = session.selecting,
        )
        return GalleryDragSelectionResult(
            selectedContentUris = updated,
            session = session.copy(
                visitedContentUris = LinkedHashSet(session.visitedContentUris).apply {
                    add(item.contentUri)
                },
            ),
        )
    }
}
