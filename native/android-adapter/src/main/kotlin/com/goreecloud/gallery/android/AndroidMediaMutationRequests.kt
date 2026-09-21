package com.goreecloud.gallery.android

import android.app.PendingIntent
import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.net.URI
import java.util.Collections

/**
 * Creates Android-owned confirmation requests for MediaStore trash/recovery/destructive operations.
 *
 * GoreeCloud Gallery never turns a rendered/selected item into direct filesystem authority.
 * Only bounded MediaStore image/video item URIs from the current authorized presentation scope may
 * reach this boundary, and Android remains responsible for the final confirmation UI.
 */
enum class AndroidMediaMutationMode {
    TRASH,
    RESTORE,
    DELETE,
}

/**
 * Exact request state paired with the Android-owned PendingIntent produced for that same mode/scope.
 *
 * This is deliberately not a data class. A generated copy() method could pair an already-created
 * Android PendingIntent with a different mode or URI list and make Gallery retain pending state that
 * no longer describes the actual Android confirmation request. The URI list is defensively copied
 * and unmodifiable for the same reason.
 */
class AndroidMediaMutationRequest internal constructor(
    val mode: AndroidMediaMutationMode,
    contentUris: List<String>,
    val pendingIntent: PendingIntent,
) {
    val contentUris: List<String> = Collections.unmodifiableList(ArrayList(contentUris))
}

object AndroidMediaMutationRequests {
    const val MIN_SUPPORTED_API = Build.VERSION_CODES.R
    // Keep one Android-owned confirmation bounded to the largest Recycle Bin page Gallery exposes.
    // Aggregate URI size remains independently capped below to protect saved-state/Binder scope.
    const val MAX_MUTATION_ITEMS = 250
    const val MAX_CONTENT_URI_CHARACTERS = 1024
    const val MAX_TOTAL_CONTENT_URI_CHARACTERS = 64 * 1024

    private val canonicalMediaStoreItemPath =
        Regex("^/([A-Za-z0-9_-]+)/(images|video)/media/([1-9][0-9]*)$")

    fun isSupported(apiLevel: Int = Build.VERSION.SDK_INT): Boolean =
        apiLevel >= MIN_SUPPORTED_API

    fun create(
        contentResolver: ContentResolver,
        contentUris: Collection<String>,
        mode: AndroidMediaMutationMode,
    ): AndroidMediaMutationRequest {
        check(isSupported()) {
            "Android-authorized trash/restore/delete requests require Android 11 or newer"
        }

        val normalizedUris = normalizeMediaStoreUris(contentUris)
        val androidUris = normalizedUris.map(Uri::parse)
        val pendingIntent = when (mode) {
            AndroidMediaMutationMode.TRASH ->
                MediaStore.createTrashRequest(contentResolver, androidUris, true)
            AndroidMediaMutationMode.RESTORE ->
                MediaStore.createTrashRequest(contentResolver, androidUris, false)
            AndroidMediaMutationMode.DELETE ->
                MediaStore.createDeleteRequest(contentResolver, androidUris)
        }
        return AndroidMediaMutationRequest(
            mode = mode,
            contentUris = normalizedUris,
            pendingIntent = pendingIntent,
        )
    }

    internal fun normalizeMediaStoreUris(contentUris: Collection<String>): List<String> {
        require(contentUris.isNotEmpty()) { "at least one media content URI is required" }
        require(contentUris.size <= MAX_MUTATION_ITEMS) {
            "a single media mutation is limited to $MAX_MUTATION_ITEMS items"
        }

        val normalized = ArrayList<String>(contentUris.size)
        val seen = HashSet<String>(contentUris.size)
        var totalUriCharacters = 0
        contentUris.forEach { raw ->
            val value = raw.trim()
            require(value.isNotEmpty()) { "media content URIs must not be blank" }
            require(value == raw) {
                "MediaStore mutation URIs must already use their exact canonical form"
            }
            require(value.length <= MAX_CONTENT_URI_CHARACTERS) {
                "MediaStore mutation URI exceeds the supported size bound"
            }
            totalUriCharacters += value.length
            require(totalUriCharacters <= MAX_TOTAL_CONTENT_URI_CHARACTERS) {
                "MediaStore mutation URI scope exceeds the supported saved-state size bound"
            }
            require(seen.add(value)) {
                "MediaStore mutation URIs must be unique"
            }
            val parsed = try {
                URI(value)
            } catch (error: Exception) {
                throw IllegalArgumentException("invalid media content URI", error)
            }
            require(parsed.scheme == "content") { "only content URIs may be mutated" }
            require(parsed.authority == MediaStore.AUTHORITY) {
                "only Android MediaStore URIs may be mutated"
            }
            require(parsed.rawQuery == null && parsed.rawFragment == null) {
                "MediaStore mutation URIs must not include query parameters or fragments"
            }
            require(parsed.userInfo == null && parsed.port == -1) {
                "MediaStore mutation URIs must use the canonical content authority form"
            }
            require(parsed.rawPath == parsed.path && '\\' !in parsed.rawPath.orEmpty()) {
                "MediaStore mutation paths must not use encoded or backslash path controls"
            }
            requireSpecificImageOrVideoItem(parsed)
            normalized += value
        }

        return normalized
    }

    private fun requireSpecificImageOrVideoItem(uri: URI) {
        val path = uri.rawPath.orEmpty()
        val match = canonicalMediaStoreItemPath.matchEntire(path)
        require(match != null) {
            "MediaStore mutation URI must use /<volume>/(images|video)/media/<positive canonical id>"
        }
        require(match.groupValues[3].toLongOrNull() != null) {
            "MediaStore mutation URI item ID exceeds the supported numeric range"
        }
    }
}
