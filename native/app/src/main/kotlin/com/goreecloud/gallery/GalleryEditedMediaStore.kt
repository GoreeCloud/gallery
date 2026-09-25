package com.goreecloud.gallery

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.IOException

class GalleryEditedMediaStore(
    private val contentResolver: ContentResolver,
) {
    data class SavedCopy(
        val uri: Uri,
        val displayName: String,
        val mimeType: String,
    )

    fun saveCopy(
        sourceUri: Uri,
        sourceDisplayName: String,
        sourceMimeType: String,
        bitmap: Bitmap,
    ): SavedCopy {
        require(bitmap.width > 0 && bitmap.height > 0)

        val output = outputFormat(sourceMimeType)
        val sourceMetadata = readSourceMetadata(sourceUri)
        val displayName = editedDisplayName(sourceDisplayName, output.extension)
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
            put(MediaStore.Images.Media.MIME_TYPE, output.mimeType)
            put(MediaStore.Images.Media.WIDTH, bitmap.width)
            put(MediaStore.Images.Media.HEIGHT, bitmap.height)
            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                sourceMetadata.relativePath ?: "${Environment.DIRECTORY_PICTURES}/GoreeCloud Gallery",
            )
            sourceMetadata.dateTakenMillis?.let { put(MediaStore.Images.Media.DATE_TAKEN, it) }
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        val destination = contentResolver.insert(collection, values)
            ?: throw IOException("Android MediaStore did not create the edited copy")

        try {
            val stream = contentResolver.openOutputStream(destination, "w")
                ?: throw IOException("Android MediaStore did not open the edited copy")
            stream.use {
                if (!bitmap.compress(output.compressFormat, output.quality, it)) {
                    throw IOException("Bitmap encoder did not write the edited copy")
                }
            }

            val publishValues = ContentValues().apply { put(MediaStore.Images.Media.IS_PENDING, 0) }
            if (contentResolver.update(destination, publishValues, null, null) <= 0) {
                throw IOException("Android MediaStore did not publish the edited copy")
            }
            return SavedCopy(destination, displayName, output.mimeType)
        } catch (throwable: Throwable) {
            runCatching { contentResolver.delete(destination, null, null) }
            throw throwable
        }
    }

    private fun readSourceMetadata(uri: Uri): SourceMetadata {
        return try {
            contentResolver.query(
                uri,
                arrayOf(MediaStore.Images.Media.RELATIVE_PATH, MediaStore.Images.Media.DATE_TAKEN),
                null,
                null,
                null,
            )?.use { cursor ->
                if (!cursor.moveToFirst()) return@use SourceMetadata(null, null)
                val relativePath = cursor.getColumnIndex(MediaStore.Images.Media.RELATIVE_PATH)
                    .takeIf { it >= 0 }
                    ?.let { cursor.getString(it) }
                    ?.takeIf { it.isNotBlank() }
                val dateTaken = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN)
                    .takeIf { it >= 0 && !cursor.isNull(it) }
                    ?.let { cursor.getLong(it) }
                    ?.takeIf { it > 0L }
                SourceMetadata(relativePath, dateTaken)
            } ?: SourceMetadata(null, null)
        } catch (_: SecurityException) {
            SourceMetadata(null, null)
        } catch (_: RuntimeException) {
            SourceMetadata(null, null)
        }
    }

    private fun editedDisplayName(sourceDisplayName: String, extension: String): String {
        val sourceBase = sourceDisplayName.substringBeforeLast('.', sourceDisplayName)
        val sanitized = sourceBase
            .replace(Regex("[\\p{Cntrl}/\\\\]+"), "_")
            .trim()
            .take(120)
            .ifBlank { "Photo" }
        return "${sanitized}_edited_${System.currentTimeMillis()}.$extension"
    }

    private fun outputFormat(sourceMimeType: String): OutputFormat = when (sourceMimeType.lowercase()) {
        "image/png" -> OutputFormat("image/png", "png", Bitmap.CompressFormat.PNG, 100)
        else -> OutputFormat("image/jpeg", "jpg", Bitmap.CompressFormat.JPEG, 95)
    }

    private data class SourceMetadata(
        val relativePath: String?,
        val dateTakenMillis: Long?,
    )

    private data class OutputFormat(
        val mimeType: String,
        val extension: String,
        val compressFormat: Bitmap.CompressFormat,
        val quality: Int,
    )
}
