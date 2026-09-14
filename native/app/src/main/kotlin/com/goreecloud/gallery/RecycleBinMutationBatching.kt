package com.goreecloud.gallery

/**
 * Pure ordering helper for Recycle Bin operations that exceed one Android MediaStore mutation
 * request. It does not validate or grant media authority; every emitted batch still has to pass
 * AndroidMediaMutationRequests before Gallery can launch Android's confirmation UI.
 */
internal object RecycleBinMutationBatching {
    fun plan(contentUris: List<String>, maxBatchSize: Int): List<List<String>> {
        require(contentUris.isNotEmpty()) { "at least one Recycle Bin item is required" }
        require(maxBatchSize > 0) { "mutation batch size must be positive" }
        require(contentUris.toSet().size == contentUris.size) {
            "Recycle Bin batch scope must contain unique media items"
        }

        return contentUris.chunked(maxBatchSize).map { batch -> batch.toList() }
    }
}
