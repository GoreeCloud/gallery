package com.goreecloud.gallery.android

import android.app.PendingIntent
import android.content.ContentProviderOperation
import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.util.Collections

/**
 * Android-owned authorization boundary for moving already-authorized Gallery media between existing
 * MediaStore folders/albums.
 *
 * Gallery supplies only canonical image/video item URIs from its current selection scope and an
 * album id already discovered through MediaStore. The adapter resolves that album's RELATIVE_PATH
 * locally, asks Android for write authority over the exact item URIs, and applies the path change
 * only after the caller receives RESULT_OK for that PendingIntent.
 */
data class AndroidMediaMoveDestination(
    val albumId: String,
    val displayName: String,
    val relativePath: String,
) {
    init {
        require(albumId.isNotBlank())
        require(displayName.isNotBlank())
        require(relativePath == AndroidMediaMoveRequests.normalizeRelativePath(relativePath))
    }
}

class AndroidMediaMoveRequest internal constructor(
    contentUris: List<String>,
    val destination: AndroidMediaMoveDestination,
    val pendingIntent: PendingIntent,
) {
    val contentUris: List<String> = Collections.unmodifiableList(ArrayList(contentUris))
}

/** Persistable non-authority state describing the exact write request while Android owns consent UI. */
class AndroidMediaMovePendingState(
    contentUris: Collection<String>,
    val destination: AndroidMediaMoveDestination,
) {
    val contentUris: List<String> = Collections.unmodifiableList(
        ArrayList(AndroidMediaMutationRequests.normalizeMediaStoreUris(contentUris)),
    )

    init {
        require(contentUris.isNotEmpty())
        require(contentUris.size <= AndroidMediaMoveRequests.MAX_MOVE_ITEMS)
    }
}

object AndroidMediaMoveRequests {
    const val MIN_SUPPORTED_API = Build.VERSION_CODES.R
    const val MAX_MOVE_ITEMS = AndroidMediaMutationRequests.MAX_MUTATION_ITEMS
    const val MAX_RELATIVE_PATH_CHARACTERS = 1024

    fun isSupported(apiLevel: Int = Build.VERSION.SDK_INT): Boolean = apiLevel >= MIN_SUPPORTED_API

    fun resolveDestination(
        contentResolver: ContentResolver,
        albumId: String,
        expectedDisplayName: String,
    ): AndroidMediaMoveDestination? {
        check(isSupported()) { "MediaStore move authorization requires Android 11 or newer" }
        val normalizedAlbumId = albumId.trim()
        val normalizedName = expectedDisplayName.trim()
        require(normalizedAlbumId.isNotEmpty()) { "destination album id is required" }
        require(normalizedName.isNotEmpty()) { "destination album name is required" }

        val collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
        val projection = arrayOf(
            MediaStore.MediaColumns.BUCKET_ID,
            MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.RELATIVE_PATH,
        )
        val selection = "${MediaStore.MediaColumns.BUCKET_ID} = ? AND ${MediaStore.Files.FileColumns.MEDIA_TYPE} IN (?, ?)"
        val selectionArgs = arrayOf(
            normalizedAlbumId,
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString(),
        )

        val cursor = contentResolver.query(collection, projection, selection, selectionArgs, null) ?: return null
        cursor.use {
            val idIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_ID)
            val nameIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME)
            val pathIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns.RELATIVE_PATH)
            while (it.moveToNext()) {
                if (it.isNull(pathIndex) || it.isNull(idIndex) || it.isNull(nameIndex)) continue
                val resolvedId = it.getString(idIndex)?.trim().orEmpty()
                val resolvedName = it.getString(nameIndex)?.trim().orEmpty()
                if (resolvedId != normalizedAlbumId || resolvedName != normalizedName) continue
                val path = try {
                    normalizeRelativePath(it.getString(pathIndex))
                } catch (_: IllegalArgumentException) {
                    continue
                }
                return AndroidMediaMoveDestination(
                    albumId = resolvedId,
                    displayName = resolvedName,
                    relativePath = path,
                )
            }
        }
        return null
    }

    fun create(
        contentResolver: ContentResolver,
        contentUris: Collection<String>,
        destination: AndroidMediaMoveDestination,
    ): AndroidMediaMoveRequest {
        check(isSupported()) { "MediaStore move authorization requires Android 11 or newer" }
        val normalizedUris = AndroidMediaMutationRequests.normalizeMediaStoreUris(contentUris)
        require(normalizedUris.size <= MAX_MOVE_ITEMS)
        val pendingIntent = MediaStore.createWriteRequest(
            contentResolver,
            normalizedUris.map(Uri::parse),
        )
        return AndroidMediaMoveRequest(
            contentUris = normalizedUris,
            destination = destination,
            pendingIntent = pendingIntent,
        )
    }

    fun capturePendingState(request: AndroidMediaMoveRequest): AndroidMediaMovePendingState =
        AndroidMediaMovePendingState(request.contentUris, request.destination)

    /** Apply an already-authorized move. Call only after Android returns RESULT_OK for [create]. */
    fun applyAuthorizedMove(
        contentResolver: ContentResolver,
        pending: AndroidMediaMovePendingState,
    ) {
        val destinationPath = normalizeRelativePath(pending.destination.relativePath)
        val operations = pending.contentUris.map { rawUri ->
            ContentProviderOperation.newUpdate(Uri.parse(rawUri))
                .withValue(MediaStore.MediaColumns.RELATIVE_PATH, destinationPath)
                .withExpectedCount(1)
                .build()
        }
        require(operations.isNotEmpty()) { "at least one move operation is required" }
        contentResolver.applyBatch(MediaStore.AUTHORITY, ArrayList(operations))
    }

    internal fun normalizeRelativePath(raw: String?): String {
        val value = raw?.trim().orEmpty()
        require(value.isNotEmpty()) { "destination relative path is required" }
        require(value.length <= MAX_RELATIVE_PATH_CHARACTERS) { "destination relative path is too long" }
        require(!value.startsWith('/')) { "destination relative path must not be absolute" }
        require('\u0000' !in value && '\\' !in value) { "destination relative path contains unsupported characters" }

        val segments = value.split('/').filter { it.isNotEmpty() }
        require(segments.isNotEmpty()) { "destination relative path must contain a folder" }
        require(segments.none { it == "." || it == ".." }) { "destination relative path may not traverse directories" }
        require(segments.all { it.isNotBlank() }) { "destination relative path contains an empty folder" }
        return segments.joinToString(separator = "/", postfix = "/")
    }
}
