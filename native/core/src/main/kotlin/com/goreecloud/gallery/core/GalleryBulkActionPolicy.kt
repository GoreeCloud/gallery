package com.goreecloud.gallery.core

/**
 * Framework-independent planning for non-destructive multi-selection actions.
 *
 * Callers must supply the current authorized/presented scope. Every plan resolves selections through
 * [GallerySelectionPolicy], so stale or foreign content URIs cannot enter a bulk action. A move plan
 * names an already-discovered MediaStore album/bucket but grants no filesystem or provider authority;
 * the Android adapter must independently resolve and authorize the destination before writing.
 */
object GalleryBulkActionPolicy {
    fun sharePlan(
        currentScope: List<MediaItem>,
        selectedContentUris: Set<String>,
    ): GallerySharePlan? {
        val items = GallerySelectionPolicy.resolve(currentScope, selectedContentUris)
        if (items.isEmpty()) return null

        val mimeType = when {
            items.map { it.mimeType }.distinct().size == 1 -> items.first().mimeType
            items.all { it.mimeType.startsWith("image/") } -> "image/*"
            items.all { it.mimeType.startsWith("video/") } -> "video/*"
            else -> "*/*"
        }
        return GallerySharePlan(
            mimeType = mimeType,
            contentUris = items.map { it.contentUri },
        )
    }

    fun favoriteAction(
        currentScope: List<MediaItem>,
        selectedContentUris: Set<String>,
        favoriteContentUris: Set<String>,
    ): GalleryFavoriteBulkAction? {
        val items = GallerySelectionPolicy.resolve(currentScope, selectedContentUris)
        if (items.isEmpty()) return null
        return if (items.all { it.contentUri in favoriteContentUris }) {
            GalleryFavoriteBulkAction.REMOVE
        } else {
            GalleryFavoriteBulkAction.ADD
        }
    }

    /**
     * Plan a move into one existing authorized MediaStore album/bucket.
     *
     * Items already in the destination are excluded so an all-same-folder request is a no-op. The
     * plan is intentionally limited to 100 items to match Gallery's Android mutation authority bound.
     */
    fun movePlan(
        currentScope: List<MediaItem>,
        selectedContentUris: Set<String>,
        destination: MediaAlbum,
        maxItems: Int = 100,
    ): GalleryMovePlan? {
        require(maxItems in 1..100) { "maxItems must be between 1 and 100" }
        val items = GallerySelectionPolicy.resolve(currentScope, selectedContentUris)
            .filterNot { it.albumId == destination.id }
        if (items.isEmpty()) return null
        require(items.size <= maxItems) { "a single Gallery move is limited to $maxItems items" }
        return GalleryMovePlan(
            destinationAlbumId = destination.id,
            destinationAlbumName = destination.displayName,
            contentUris = items.map { it.contentUri },
        )
    }
}

data class GallerySharePlan(
    val mimeType: String,
    val contentUris: List<String>,
)

data class GalleryMovePlan(
    val destinationAlbumId: String,
    val destinationAlbumName: String,
    val contentUris: List<String>,
) {
    init {
        require(destinationAlbumId.isNotBlank())
        require(destinationAlbumName.isNotBlank())
        require(contentUris.isNotEmpty())
        require(contentUris.size <= 100)
        require(contentUris.distinct().size == contentUris.size)
    }
}

enum class GalleryFavoriteBulkAction {
    ADD,
    REMOVE,
}
