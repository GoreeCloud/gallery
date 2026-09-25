package com.goreecloud.gallery

object GalleryPhotoEditorContract {
    const val EXTRA_CONTENT_URI = "com.goreecloud.gallery.extra.EDIT_CONTENT_URI"
    const val EXTRA_DISPLAY_NAME = "com.goreecloud.gallery.extra.EDIT_DISPLAY_NAME"
    const val EXTRA_MIME_TYPE = "com.goreecloud.gallery.extra.EDIT_MIME_TYPE"

    private val supportedMimeTypes = setOf(
        "image/jpeg",
        "image/png",
        "image/webp",
        "image/heic",
        "image/heif",
        "image/avif",
    )

    fun isSupportedMimeType(mimeType: String): Boolean =
        mimeType.lowercase() in supportedMimeTypes
}
