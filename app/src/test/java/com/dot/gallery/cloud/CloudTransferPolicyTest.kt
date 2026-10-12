/*
 * SPDX-FileCopyrightText: 2023-2026 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package com.dot.gallery.cloud

import com.dot.gallery.cloud.core.CloudAlbumIdentity
import com.dot.gallery.cloud.core.CloudUri
import com.dot.gallery.cloud.core.ProviderType
import com.dot.gallery.cloud.core.isUnsupportedSameAccountCloudMove
import com.dot.gallery.cloud.core.shouldDeleteCloudSourceAfterTransfer
import com.dot.gallery.cloud.sync.CloudAlbumCopyItemState
import com.dot.gallery.cloud.sync.CloudAlbumTransferMode
import com.dot.gallery.cloud.sync.isTransferFailure
import com.dot.gallery.cloud.sync.shouldBeginCloudMoveCleanup
import com.dot.gallery.cloud.sync.shouldPruneCloudTransferTempFile
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CloudTransferPolicyTest {

    @Test
    fun pathProviderRejectsItsCurrentFolderButAllowsAnotherFolder() {
        val source = cloudUri(ProviderType.SMB, 11L, "Camera/photo.jpg")

        assertTrue(
            isUnsupportedSameAccountCloudMove(
                source,
                CloudAlbumIdentity(ProviderType.SMB, 11L, "Camera"),
                "photo.jpg"
            )
        )
        assertFalse(
            isUnsupportedSameAccountCloudMove(
                source,
                CloudAlbumIdentity(ProviderType.SMB, 11L, "Archive"),
                "photo.jpg"
            )
        )
    }

    @Test
    fun immichMoveWithinOneAccountIsRejectedButCrossAccountIsAllowed() {
        val source = cloudUri(ProviderType.IMMICH, 11L, "asset-1")

        assertTrue(
            isUnsupportedSameAccountCloudMove(
                source,
                CloudAlbumIdentity(ProviderType.IMMICH, 11L, "album-2"),
                "photo.jpg"
            )
        )
        assertFalse(
            isUnsupportedSameAccountCloudMove(
                source,
                CloudAlbumIdentity(ProviderType.IMMICH, 22L, "album-2"),
                "photo.jpg"
            )
        )
    }

    @Test
    fun sourceDeletionRequiresADistinctVerifiedDestinationAsset() {
        val source = cloudUri(ProviderType.SMB, 11L, "Camera/photo.jpg")
        val sameAccount = CloudAlbumIdentity(ProviderType.SMB, 11L, "Camera")

        assertFalse(shouldDeleteCloudSourceAfterTransfer(source, sameAccount, null))
        // A successful transfer must provide a real destination identity
        // before a MOVE operation may delete its original source.
        assertFalse(shouldDeleteCloudSourceAfterTransfer(source, sameAccount, ""))
        assertFalse(shouldDeleteCloudSourceAfterTransfer(source, sameAccount, "  "))
        assertFalse(shouldDeleteCloudSourceAfterTransfer(source, sameAccount, "\n"))
        assertFalse(
            shouldDeleteCloudSourceAfterTransfer(
                source,
                sameAccount,
                "Camera/photo.jpg"
            )
        )
        assertTrue(
            shouldDeleteCloudSourceAfterTransfer(
                source,
                CloudAlbumIdentity(ProviderType.SMB, 11L, "Archive"),
                "Archive/photo.jpg"
            )
        )
        assertTrue(
            shouldDeleteCloudSourceAfterTransfer(
                source,
                CloudAlbumIdentity(ProviderType.SMB, 22L, "Camera"),
                "Camera/photo.jpg"
            )
        )
    }

    @Test
    fun moveCleanupWaitsForEveryDestinationCopyAndTreatsRetainedSourcesAsFailure() {
        assertFalse(
            shouldBeginCloudMoveCleanup(
                CloudAlbumTransferMode.COPY,
                listOf(CloudAlbumCopyItemState.COPIED)
            )
        )
        assertFalse(
            shouldBeginCloudMoveCleanup(
                CloudAlbumTransferMode.MOVE,
                listOf(CloudAlbumCopyItemState.COPIED, CloudAlbumCopyItemState.FAILED)
            )
        )
        assertTrue(
            shouldBeginCloudMoveCleanup(
                CloudAlbumTransferMode.MOVE,
                listOf(CloudAlbumCopyItemState.COPIED, CloudAlbumCopyItemState.ALREADY_PRESENT)
            )
        )
        assertFalse(shouldBeginCloudMoveCleanup(CloudAlbumTransferMode.MOVE, emptyList()))
        assertFalse(
            shouldBeginCloudMoveCleanup(
                CloudAlbumTransferMode.MOVE,
                listOf(CloudAlbumCopyItemState.COPIED, CloudAlbumCopyItemState.PENDING)
            )
        )
        assertFalse(
            shouldBeginCloudMoveCleanup(
                CloudAlbumTransferMode.MOVE,
                listOf(CloudAlbumCopyItemState.ALREADY_PRESENT, CloudAlbumCopyItemState.ATTACH_PENDING)
            )
        )
        assertTrue(
            shouldBeginCloudMoveCleanup(
                CloudAlbumTransferMode.MOVE,
                listOf(CloudAlbumCopyItemState.MOVED, CloudAlbumCopyItemState.SOURCE_DELETE_PENDING)
            )
        )
        assertTrue(CloudAlbumCopyItemState.SOURCE_DELETE_PENDING.isTransferFailure())
        assertTrue(CloudAlbumCopyItemState.SOURCE_RETAINED.isTransferFailure())
        assertFalse(CloudAlbumCopyItemState.MOVED.isTransferFailure())
    }

    @Test
    fun unfinishedCloudTransferRecordsAreNeverAgePruned() {
        val uuid = "123e4567-e89b-12d3-a456-426614174000"
        val now = 20L * 24 * 60 * 60 * 1000
        val old = now - 10L * 24 * 60 * 60 * 1000
        // Completed requests are explicitly deleted; even old failed/pending
        // durable JSON records must remain for recovery and retries.
        assertFalse(shouldPruneCloudTransferTempFile("$uuid.json", old, now))
        assertFalse(shouldPruneCloudTransferTempFile(".$uuid.json", old, now))
        assertFalse(shouldPruneCloudTransferTempFile("other.json", old, now))
    }

    @Test
    fun onlyOldOwnedTransferStagingFilesMayBeAgePruned() {
        val uuid = "123e4567-e89b-12d3-a456-426614174000"
        val name = ".$uuid.tmp"
        val dayMs = 24L * 60 * 60 * 1000
        val now = 20L * dayMs
        assertTrue(shouldPruneCloudTransferTempFile(name, now - 8L * dayMs, now))
        assertFalse(shouldPruneCloudTransferTempFile(name, now - 7L * dayMs, now))
        assertFalse(shouldPruneCloudTransferTempFile(name, now - dayMs, now))
        assertFalse(shouldPruneCloudTransferTempFile(name, now + dayMs, now))
        assertFalse(shouldPruneCloudTransferTempFile(name, 0L, now))
        assertFalse(shouldPruneCloudTransferTempFile("unrelated.tmp", now - 8L * dayMs, now))
    }

    private fun cloudUri(type: ProviderType, configId: Long, remoteId: String) = CloudUri(
        providerType = type,
        remoteId = remoteId,
        size = "original",
        typeParam = null,
        fileId = null,
        configId = configId
    )
}
