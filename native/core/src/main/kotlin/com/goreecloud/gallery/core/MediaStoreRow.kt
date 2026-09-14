package com.goreecloud.gallery.core

import java.time.Instant

/**
 * Android MediaStore projection owned by the native Gallery adapter.
 *
 * This core contract intentionally has no android.* dependency so MediaStore normalization remains
 * deterministic and unit-testable. Column literals mirror Android MediaStore constants used by the
 * compiled adapter; DATE_TAKEN is the Android `datetaken` column and is measured in epoch millis.
 */
object MediaStoreProjection {
    const val ID = "_id"
    const val DISPLAY_NAME = "_display_name"
    const val MIME_TYPE = "mime_type"
    const val DATE_TAKEN = "datetaken"
    const val DATE_MODIFIED = "date_modified"
    const val WIDTH = "width"
    const val HEIGHT = "height"
    const val DURATION = "duration"
    const val SIZE = "_size"
    const val BUCKET_ID = "bucket_id"
    const val BUCKET_DISPLAY_NAME = "bucket_display_name"
    const val RELATIVE_PATH = "relative_path"

    val columns: List<String> = listOf(
        ID,
        DISPLAY_NAME,
        MIME_TYPE,
        DATE_TAKEN,
        DATE_MODIFIED,
        WIDTH,
        HEIGHT,
        DURATION,
        SIZE,
        BUCKET_ID,
        BUCKET_DISPLAY_NAME,
        RELATIVE_PATH,
    )
}

/**
 * Provider-neutral representation of one ContentResolver row from MediaStore.
 * DATE_TAKEN is milliseconds since epoch; DATE_MODIFIED is seconds since epoch, matching
 * Android MediaStore contracts. Unsupported or structurally invalid rows fail closed.
 */
data class MediaStoreRow(
    val collectionUri: String,
    val id: Long,
    val displayName: String,
    val mimeType: String,
    val dateTakenEpochMillis: Long?,
    val dateModifiedEpochSeconds: Long,
    val width: Int?,
    val height: Int?,
    val durationMillis: Long?,
    val sizeBytes: Long,
    val bucketId: String?,
    val bucketDisplayName: String?,
    val relativePath: String? = null,
) {
    init {
        val normalizedMimeType = mimeType.lowercase()
        require(collectionUri.startsWith("content://")) { "MediaStore collection URI must use content://" }
        require(id >= 0) { "MediaStore id must be non-negative" }
        require(displayName.isNotBlank()) { "MediaStore display name is required" }
        require(normalizedMimeType.startsWith("image/") || normalizedMimeType.startsWith("video/")) {
            "MediaStore row must be image or video content"
        }
        require(dateTakenEpochMillis == null || dateTakenEpochMillis >= 0) {
            "MediaStore date taken must be non-negative"
        }
        require(dateModifiedEpochSeconds >= 0) { "MediaStore date modified must be non-negative" }
        require(width == null || width > 0) { "MediaStore width must be positive when present" }
        require(height == null || height > 0) { "MediaStore height must be positive when present" }
        require(durationMillis == null || durationMillis >= 0) {
            "MediaStore duration must be non-negative when present"
        }
        require(sizeBytes >= 0) { "MediaStore size must be non-negative" }
    }

    fun toMediaItem(): MediaItem {
        val normalizedCollection = collectionUri.trimEnd('/')
        val normalizedMimeType = mimeType.lowercase()
        val normalizedBucketId = bucketId?.trim()?.takeIf { it.isNotEmpty() }
        val normalizedBucketName = bucketDisplayName?.trim()?.takeIf { it.isNotEmpty() }
        val normalizedRelativePath = relativePath
            ?.trim()
            ?.replace('\\', '/')
            ?.takeIf { it.isNotEmpty() }
            ?.let { if (it.endsWith('/')) it else "$it/" }
        val hasCompleteAlbum = normalizedBucketId != null && normalizedBucketName != null

        return MediaItem(
            id = id.toString(),
            contentUri = "$normalizedCollection/$id",
            displayName = displayName.trim(),
            mimeType = normalizedMimeType,
            capturedAt = dateTakenEpochMillis?.let(Instant::ofEpochMilli),
            modifiedAt = Instant.ofEpochSecond(dateModifiedEpochSeconds),
            width = width,
            height = height,
            durationMillis = if (normalizedMimeType.startsWith("video/")) durationMillis else null,
            sizeBytes = sizeBytes,
            albumId = if (hasCompleteAlbum) normalizedBucketId else null,
            albumName = if (hasCompleteAlbum) normalizedBucketName else null,
            relativePath = normalizedRelativePath,
        )
    }
}
