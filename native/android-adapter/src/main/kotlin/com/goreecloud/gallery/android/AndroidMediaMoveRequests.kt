package com.goreecloud.gallery.android

import android.app.PendingIntent
import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.util.Collections

/**
 * Android-owned authorization and execution boundary for moving existing MediaStore items between
 * provider-known relative paths.
 *
 * A Gallery album/bucket is not treated as filesystem authority. The destination must come from an
 * authoritative MediaStore RELATIVE_PATH already present in the current authorized snapshot, and
 * Android must approve write access to the exact bounded item URIs before Gallery updates metadata.
 */
class AndroidMediaMoveRequest internal constructor(
    contentUris: List<String>,
    val destinationRelativePath: String,
    val pendingIntent: PendingIntent,
) {
    val contentUris: List<String> = Collections.unmodifiableList(ArrayList(contentUris))
}

class AndroidMediaMovePendingState internal constructor(
    contentUris: List<String>,
    val destinationRelativePath: String,
) {
    val contentUris: List<String> = Collections.unmodifiableList(ArrayList(contentUris))
}

data class AndroidMediaMoveResult(
    val movedCount: Int,
    val failedCount: Int,
) {
    init {
        require(movedCount >= 0)
        require(failedCount >= 0)
    }

    val totalCount: Int
        get() = movedCount + failedCount

    val complete: Boolean
        get() = movedCount > 0 && failedCount == 0
}

object AndroidMediaMoveRequests {
    const val MIN_SUPPORTED_API = Build.VERSION_CODES.R
    const val MAX_DESTINATION_PATH_CHARACTERS = 1024

    fun isSupported(apiLevel: Int = Build.VERSION.SDK_INT): Boolean = apiLevel >= MIN_SUPPORTED_API

    fun normalizeDestinationRelativePath(raw: String): String {
        val value = raw.trim().replace('\\', '/')
        require(value.isNotEmpty()) { "move destination path is required" }
        require(value.length <= MAX_DESTINATION_PATH_CHARACTERS) { "move destination path is too long" }
        require(!value.startsWith('/')) { "move destination must remain relative to MediaStore storage" }
        require("://" !in value) { "move destination must not be a URI" }
        require('\u0000' !in value) { "move destination contains an invalid character" }

        val segments = value.split('/').filter { it.isNotEmpty() }
        require(segments.isNotEmpty()) { "move destination must contain a folder" }
        require(segments.none { it == "." || it == ".." }) { "move destination cannot traverse storage" }

        return segments.joinToString(separator = "/", postfix = "/")
    }

    fun create(
        contentResolver: ContentResolver,
        contentUris: Collection<String>,
        destinationRelativePath: String,
    ): AndroidMediaMoveRequest {
        check(isSupported()) { "Android-authorized media move requires Android 11 or newer" }
        val canonicalUris = AndroidMediaMutationRequests.normalizeMediaStoreUris(contentUris)
        val canonicalDestination = normalizeDestinationRelativePath(destinationRelativePath)
        val pendingIntent = MediaStore.createWriteRequest(
            contentResolver,
            canonicalUris.map(Uri::parse),
        )
        return AndroidMediaMoveRequest(
            contentUris = canonicalUris,
            destinationRelativePath = canonicalDestination,
            pendingIntent = pendingIntent,
        )
    }

    fun capture(request: AndroidMediaMoveRequest): AndroidMediaMovePendingState =
        AndroidMediaMovePendingState(
            contentUris = request.contentUris,
            destinationRelativePath = request.destinationRelativePath,
        )

    fun restore(
        contentUris: Collection<String>?,
        destinationRelativePath: String?,
    ): AndroidMediaMovePendingState? {
        if (contentUris.isNullOrEmpty() || destinationRelativePath.isNullOrBlank()) return null
        val suppliedUris = contentUris.toList()
        val canonicalUris = try {
            AndroidMediaMutationRequests.normalizeMediaStoreUris(suppliedUris)
        } catch (_: IllegalArgumentException) {
            return null
        }
        if (suppliedUris != canonicalUris) return null

        val canonicalDestination = try {
            normalizeDestinationRelativePath(destinationRelativePath)
        } catch (_: IllegalArgumentException) {
            return null
        }
        if (destinationRelativePath != canonicalDestination) return null

        return AndroidMediaMovePendingState(
            contentUris = canonicalUris,
            destinationRelativePath = canonicalDestination,
        )
    }

    fun contentUriValues(state: AndroidMediaMovePendingState): Array<String> = state.contentUris.toTypedArray()

    fun execute(
        contentResolver: ContentResolver,
        state: AndroidMediaMovePendingState,
    ): AndroidMediaMoveResult {
        check(isSupported()) { "Android-authorized media move requires Android 11 or newer" }
        val destination = normalizeDestinationRelativePath(state.destinationRelativePath)
        var moved = 0
        var failed = 0

        state.contentUris.forEach { contentUri ->
            val updated = try {
                contentResolver.update(
                    Uri.parse(contentUri),
                    ContentValues().apply {
                        put(MediaStore.MediaColumns.RELATIVE_PATH, destination)
                    },
                    null,
                    null,
                )
            } catch (_: SecurityException) {
                0
            } catch (_: RuntimeException) {
                0
            }
            if (updated > 0) moved += 1 else failed += 1
        }

        return AndroidMediaMoveResult(movedCount = moved, failedCount = failed)
    }
}
