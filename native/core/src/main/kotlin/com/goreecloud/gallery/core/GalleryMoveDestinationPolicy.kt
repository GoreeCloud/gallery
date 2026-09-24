package com.goreecloud.gallery.core

/**
 * Builds existing Move destinations only from authoritative folder metadata already present in the
 * current authorized Gallery snapshot. Album ids/names are presentation metadata; RELATIVE_PATH is
 * the Android MediaStore destination descriptor later revalidated by the Android adapter.
 */
data class GalleryMoveDestination(
    val albumId: String,
    val displayName: String,
    val relativePath: String,
    val itemCount: Int,
) {
    init {
        require(albumId.isNotBlank())
        require(displayName.isNotBlank())
        require(relativePath.isNotBlank())
        require(itemCount > 0)
    }
}

object GalleryMoveDestinationPolicy {
    fun existingDestinations(
        currentScope: List<MediaItem>,
        selectedContentUris: Set<String>,
    ): List<GalleryMoveDestination> {
        if (currentScope.isEmpty() || selectedContentUris.isEmpty()) return emptyList()

        val selectedItems = GallerySelectionPolicy.resolve(currentScope, selectedContentUris)
        if (selectedItems.isEmpty() || selectedItems.size != selectedContentUris.size) return emptyList()

        return currentScope.asSequence()
            .filter { it.albumId != null && it.albumName != null && it.relativePath != null }
            .groupBy { checkNotNull(it.albumId) }
            .mapNotNull { (albumId, items) ->
                val names = items.map { checkNotNull(it.albumName) }.distinct()
                val paths = items.map { checkNotNull(it.relativePath) }.distinct()
                if (names.size != 1 || paths.size != 1) return@mapNotNull null

                val relativePath = paths.single()
                if (selectedItems.all { it.relativePath == relativePath }) return@mapNotNull null

                GalleryMoveDestination(
                    albumId = albumId,
                    displayName = names.single(),
                    relativePath = relativePath,
                    itemCount = items.size,
                )
            }
            .sortedWith(
                compareBy<GalleryMoveDestination> { it.displayName.lowercase() }
                    .thenBy { it.albumId },
            )
    }
}
