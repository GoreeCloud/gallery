package com.goreecloud.gallery.core

/**
 * A new-folder Move remains rooted in a provider-owned RELATIVE_PATH that belongs to the current
 * authorized and presented selection. Gallery never accepts an arbitrary filesystem path here.
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

    /**
     * New-folder creation is intentionally narrower than existing-folder Move: every selected item
     * must belong to exactly one currently authorized provider path. This prevents a mixed selection
     * from silently choosing one source folder as creation authority.
     */
    fun parentForSelection(
        currentScope: List<MediaItem>,
        selectedContentUris: Set<String>,
    ): GalleryNewFolderMoveParent? {
        if (currentScope.isEmpty() || selectedContentUris.isEmpty()) return null

        val selectedItems = GallerySelectionPolicy.resolve(currentScope, selectedContentUris)
        if (selectedItems.size != selectedContentUris.size || selectedItems.isEmpty()) return null

        val paths = selectedItems.mapNotNull { canonicalProviderPath(it.relativePath) }.distinct()
        if (paths.size != 1 || selectedItems.any { canonicalProviderPath(it.relativePath) == null }) return null
        val parentPath = paths.single()

        val albumNames = selectedItems.mapNotNull { it.albumName?.trim()?.takeIf(String::isNotBlank) }.distinct()
        val displayName = albumNames.singleOrNull()
            ?: parentPath.trimEnd('/').substringAfterLast('/').ifBlank { "Current folder" }

        return GalleryNewFolderMoveParent(
            displayName = displayName,
            relativePath = parentPath,
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
