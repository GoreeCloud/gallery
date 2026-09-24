package com.goreecloud.gallery.core

/**
 * New-folder Move never accepts an arbitrary filesystem path.
 *
 * The selected rows must still belong to one provider-owned current source path so Gallery cannot
 * manufacture move authority from a mixed/foreign selection. The new destination itself is rooted
 * in Android's media-type-appropriate shared-media directory instead of blindly nesting beneath the
 * source RELATIVE_PATH. This matters for rows discovered in places such as Download/: Android allows
 * RELATIVE_PATH moves, but the destination top-level directory must remain relevant to the media
 * type. Exact row mutation still requires Android-owned write confirmation in the adapter.
 */
data class GalleryNewFolderMoveParent(
    val displayName: String,
    val relativePath: String,
) {
    init {
        require(displayName.isNotBlank())
        require(relativePath.isNotBlank())
    }
}

data class GalleryNewFolderMoveDestination(
    val displayName: String,
    val parentDisplayName: String,
    val relativePath: String,
) {
    init {
        require(displayName.isNotBlank())
        require(parentDisplayName.isNotBlank())
        require(relativePath.isNotBlank())
    }
}

object GalleryNewFolderMovePolicy {
    const val MAX_FOLDER_NAME_CHARACTERS = 96

    private const val PICTURES_ROOT = "Pictures/"
    private const val MOVIES_ROOT = "Movies/"
    private const val DCIM_ROOT = "DCIM/"

    /**
     * Every selected item must resolve inside the current authorized scope and come from exactly one
     * current provider path. The destination parent is then chosen from the media kinds being moved:
     * photos -> Pictures/, videos -> Movies/, mixed photo/video -> DCIM/.
     */
    fun parentForSelection(
        currentScope: List<MediaItem>,
        selectedContentUris: Set<String>,
    ): GalleryNewFolderMoveParent? {
        if (currentScope.isEmpty() || selectedContentUris.isEmpty()) return null

        val selectedItems = GallerySelectionPolicy.resolve(currentScope, selectedContentUris)
        if (selectedItems.size != selectedContentUris.size || selectedItems.isEmpty()) return null

        val sourcePaths = selectedItems.mapNotNull { canonicalProviderPath(it.relativePath) }.distinct()
        if (sourcePaths.size != 1 || selectedItems.any { canonicalProviderPath(it.relativePath) == null }) return null

        val hasImages = selectedItems.any { it.mimeType.startsWith("image/") }
        val hasVideos = selectedItems.any { it.mimeType.startsWith("video/") }
        val destinationRoot = when {
            hasImages && hasVideos -> DCIM_ROOT
            hasVideos -> MOVIES_ROOT
            hasImages -> PICTURES_ROOT
            else -> return null
        }

        return GalleryNewFolderMoveParent(
            displayName = destinationRoot.trimEnd('/'),
            relativePath = destinationRoot,
        )
    }

    fun destinationForSelection(
        currentScope: List<MediaItem>,
        selectedContentUris: Set<String>,
        rawFolderName: String,
    ): GalleryNewFolderMoveDestination {
        val parent = requireNotNull(parentForSelection(currentScope, selectedContentUris)) {
            "New folder requires selected media from one current authorized folder"
        }
        val folderName = normalizeFolderName(rawFolderName)
        val destinationPath = parent.relativePath + folderName + "/"

        val collision = currentScope.any { item ->
            canonicalProviderPath(item.relativePath)?.equals(destinationPath, ignoreCase = true) == true
        }
        require(!collision) { "A visible media folder with this name already exists here" }

        return GalleryNewFolderMoveDestination(
            displayName = folderName,
            parentDisplayName = parent.displayName,
            relativePath = destinationPath,
        )
    }

    fun normalizeFolderName(raw: String): String {
        val value = raw.trim()
        require(value.isNotEmpty()) { "Folder name is required" }
        require(value.length <= MAX_FOLDER_NAME_CHARACTERS) { "Folder name is too long" }
        require(value != "." && value != "..") { "Folder name is invalid" }
        require('/' !in value && '\\' !in value) { "Folder name cannot contain path separators" }
        require(':' !in value) { "Folder name cannot contain a colon" }
        require('\u0000' !in value && value.none(Char::isISOControl)) { "Folder name contains an invalid character" }
        require(!value.endsWith('.') && !value.endsWith(' ')) { "Folder name cannot end with a period or space" }
        return value
    }

    private fun canonicalProviderPath(raw: String?): String? {
        val value = raw?.trim()?.replace('\\', '/') ?: return null
        if (value.isEmpty() || value.startsWith('/') || "://" in value || '\u0000' in value) return null
        val segments = value.split('/').filter(String::isNotEmpty)
        if (segments.isEmpty() || segments.any { it == "." || it == ".." }) return null
        return segments.joinToString(separator = "/", postfix = "/")
    }
}
