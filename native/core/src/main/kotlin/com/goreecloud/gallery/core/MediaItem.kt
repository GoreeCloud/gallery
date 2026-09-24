package com.goreecloud.gallery.core

import java.time.Instant

data class MediaItem(
    val id: String,
    val contentUri: String,
    val displayName: String,
    val mimeType: String,
    val capturedAt: Instant?,
    val modifiedAt: Instant,
    val width: Int?,
    val height: Int?,
    val durationMillis: Long?,
    val sizeBytes: Long,
    val albumId: String? = null,
    val albumName: String? = null,
    /**
     * Android MediaStore RELATIVE_PATH when the provider exposes one.
     *
     * This value is provider-owned metadata, not filesystem authority. Gallery uses it only as an
     * Android-authorized destination descriptor for move workflows and never turns it into a raw
     * filesystem path.
     */
    val relativePath: String? = null,
) {
    init {
        require(id.isNotBlank())
        require(contentUri.isNotBlank())
        require(displayName.isNotBlank())
        require(mimeType.startsWith("image/") || mimeType.startsWith("video/"))
        require(sizeBytes >= 0)
        require(width == null || width > 0)
        require(height == null || height > 0)
        require(durationMillis == null || durationMillis >= 0)
        require(albumId == null || albumId.isNotBlank())
        require(albumName == null || albumName.isNotBlank())
        require((albumId == null) == (albumName == null))
        require(relativePath == null || relativePath.isNotBlank())
    }

    val kind: MediaKind
        get() = if (mimeType.startsWith("video/")) MediaKind.VIDEO else MediaKind.IMAGE
}

enum class MediaKind { IMAGE, VIDEO }
