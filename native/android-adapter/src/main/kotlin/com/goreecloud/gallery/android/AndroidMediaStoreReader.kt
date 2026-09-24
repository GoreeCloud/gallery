package com.goreecloud.gallery.android

import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore
import com.goreecloud.gallery.core.MediaItem
import com.goreecloud.gallery.core.MediaStoreProjection
import com.goreecloud.gallery.core.MediaStoreRow

/**
 * Android column contract paired with the provider-neutral native core projection.
 * Keeping this list explicit makes drift between Android framework constants and the core adapter
 * contract detectable in unit tests instead of at device runtime.
 */
object AndroidMediaStoreProjection {
    val columns: List<String> = listOf(
        MediaStore.MediaColumns._ID,
        MediaStore.MediaColumns.DISPLAY_NAME,
        MediaStore.MediaColumns.MIME_TYPE,
        MediaStore.MediaColumns.DATE_TAKEN,
        MediaStore.MediaColumns.DATE_MODIFIED,
        MediaStore.MediaColumns.WIDTH,
        MediaStore.MediaColumns.HEIGHT,
        MediaStore.MediaColumns.DURATION,
        MediaStore.MediaColumns.SIZE,
        MediaStore.MediaColumns.BUCKET_ID,
        MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
        MediaStore.MediaColumns.RELATIVE_PATH,
    )

    init {
        check(columns == MediaStoreProjection.columns) {
            "Android MediaStore columns drifted from the native core projection"
        }
    }
}

/**
 * Projects rows discovered through MediaStore.Files onto the media-specific item collections that
 * Android's write/trash/delete request APIs accept. Browsing can remain a single bounded Files
 * query, while MediaItem content URIs retain their canonical image/video identity.
 *
 * The projection itself is deliberately pure so local JVM tests can validate URI identity without
 * depending on Android framework method execution. Runtime provider access remains Android-owned.
 */
internal object AndroidMediaStoreItemUris {
    fun collectionUriForMimeType(volumeName: String, mimeType: String): String {
        val normalizedVolume = volumeName.trim()
        require(normalizedVolume.isNotEmpty()) { "MediaStore volume name is required" }
        require('/' !in normalizedVolume) { "MediaStore volume name must not contain a path separator" }

        val normalizedMimeType = mimeType.trim().lowercase()
        val collectionPath = when {
            normalizedMimeType.startsWith("image/") -> "images/media"
            normalizedMimeType.startsWith("video/") -> "video/media"
            else -> throw IllegalArgumentException("MediaStore row must be image or video content")
        }
        return "content://${MediaStore.AUTHORITY}/$normalizedVolume/$collectionPath"
    }
}

data class AndroidMediaStoreReadResult(
    val items: List<MediaItem>,
    val rejectedRowCount: Int,
)

class MediaStoreQueryUnavailableException(message: String) : IllegalStateException(message)

/**
 * First-party Android bridge from ContentResolver/MediaStore into the native Gallery core model.
 *
 * The query is owner-device local and selects only MediaStore image/video rows. The caller supplies
 * a strict maximum number of provider rows to inspect; malformed individual media rows are rejected
 * instead of being fabricated into Gallery state. A missing provider cursor fails the read instead
 * of being treated as an authoritative empty library.
 */
class AndroidMediaStoreReader(
    private val contentResolver: ContentResolver,
) {
    fun readLatest(maxRows: Int = DEFAULT_MAX_ROWS): AndroidMediaStoreReadResult {
        require(maxRows in 1..MAX_ROWS) { "maxRows must be between 1 and $MAX_ROWS" }

        val collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
        val volumeName = MediaStore.getVolumeName(collection)
        val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE} IN (?, ?)"
        val selectionArgs = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString(),
        )
        val sortOrder = listOf(
            "${MediaStore.MediaColumns.DATE_TAKEN} DESC",
            "${MediaStore.MediaColumns.DATE_MODIFIED} DESC",
            "${MediaStore.MediaColumns._ID} DESC",
        ).joinToString(", ")

        val cursor = contentResolver.query(
            collection,
            AndroidMediaStoreProjection.columns.toTypedArray(),
            selection,
            selectionArgs,
            sortOrder,
        ) ?: throw MediaStoreQueryUnavailableException("MediaStore query returned no cursor")

        cursor.use {
            val indices = ColumnIndices.from(it)
            val items = ArrayList<MediaItem>(minOf(maxRows, 64))
            var rejected = 0
            var inspected = 0
            while (inspected < maxRows && it.moveToNext()) {
                inspected += 1
                try {
                    val row = indices.readRow(it, collection.toString())
                    val itemCollectionUri = AndroidMediaStoreItemUris.collectionUriForMimeType(
                        volumeName = volumeName,
                        mimeType = row.mimeType,
                    )
                    items += row.copy(collectionUri = itemCollectionUri).toMediaItem()
                } catch (_: IllegalArgumentException) {
                    rejected += 1
                }
            }
            return AndroidMediaStoreReadResult(
                items = items.toList(),
                rejectedRowCount = rejected,
            )
        }
    }

    private data class ColumnIndices(
        val id: Int,
        val displayName: Int,
        val mimeType: Int,
        val dateTaken: Int,
        val dateModified: Int,
        val width: Int,
        val height: Int,
        val duration: Int,
        val size: Int,
        val bucketId: Int,
        val bucketDisplayName: Int,
        val relativePath: Int,
    ) {
        fun readRow(cursor: Cursor, collectionUri: String): MediaStoreRow = MediaStoreRow(
            collectionUri = collectionUri,
            id = cursor.getRequiredLong(id, MediaStoreProjection.ID),
            displayName = cursor.getRequiredString(displayName, MediaStoreProjection.DISPLAY_NAME),
            mimeType = cursor.getRequiredString(mimeType, MediaStoreProjection.MIME_TYPE),
            dateTakenEpochMillis = cursor.getNullableLong(dateTaken),
            dateModifiedEpochSeconds = cursor.getRequiredLong(dateModified, MediaStoreProjection.DATE_MODIFIED),
            width = cursor.getPositiveNullableInt(width),
            height = cursor.getPositiveNullableInt(height),
            durationMillis = cursor.getNullableLong(duration),
            sizeBytes = cursor.getRequiredLong(size, MediaStoreProjection.SIZE),
            bucketId = cursor.getNullableString(bucketId),
            bucketDisplayName = cursor.getNullableString(bucketDisplayName),
            relativePath = cursor.getNullableString(relativePath),
        )

        companion object {
            fun from(cursor: Cursor): ColumnIndices = ColumnIndices(
                id = cursor.getColumnIndexOrThrow(MediaStoreProjection.ID),
                displayName = cursor.getColumnIndexOrThrow(MediaStoreProjection.DISPLAY_NAME),
                mimeType = cursor.getColumnIndexOrThrow(MediaStoreProjection.MIME_TYPE),
                dateTaken = cursor.getColumnIndexOrThrow(MediaStoreProjection.DATE_TAKEN),
                dateModified = cursor.getColumnIndexOrThrow(MediaStoreProjection.DATE_MODIFIED),
                width = cursor.getColumnIndexOrThrow(MediaStoreProjection.WIDTH),
                height = cursor.getColumnIndexOrThrow(MediaStoreProjection.HEIGHT),
                duration = cursor.getColumnIndexOrThrow(MediaStoreProjection.DURATION),
                size = cursor.getColumnIndexOrThrow(MediaStoreProjection.SIZE),
                bucketId = cursor.getColumnIndexOrThrow(MediaStoreProjection.BUCKET_ID),
                bucketDisplayName = cursor.getColumnIndexOrThrow(MediaStoreProjection.BUCKET_DISPLAY_NAME),
                relativePath = cursor.getColumnIndexOrThrow(MediaStoreProjection.RELATIVE_PATH),
            )
        }
    }

    private companion object {
        const val DEFAULT_MAX_ROWS = 250
        const val MAX_ROWS = 500
    }
}

private fun Cursor.getRequiredLong(index: Int, column: String): Long {
    require(!isNull(index)) { "$column is required" }
    return getLong(index)
}

private fun Cursor.getRequiredString(index: Int, column: String): String {
    require(!isNull(index)) { "$column is required" }
    return getString(index)
}

private fun Cursor.getNullableLong(index: Int): Long? = if (isNull(index)) null else getLong(index)

private fun Cursor.getNullableString(index: Int): String? = if (isNull(index)) null else getString(index)

private fun Cursor.getPositiveNullableInt(index: Int): Int? {
    if (isNull(index)) return null
    return getInt(index).takeIf { it > 0 }
}
