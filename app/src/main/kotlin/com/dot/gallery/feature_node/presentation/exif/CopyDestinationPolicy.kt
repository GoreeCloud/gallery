/*
 * SPDX-FileCopyrightText: 2023-2026 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package com.dot.gallery.feature_node.presentation.exif

import com.dot.gallery.cloud.core.CloudAlbumIdentity

enum class CloudCopyDestinationStatus {
    READY,
    MISSING_IDENTITY,
    SOURCE_UNSUPPORTED,
    MOVE_UNSUPPORTED,
    ACCOUNT_UNAVAILABLE,
    PROVIDER_UNSUPPORTED,
    READ_ONLY,
    OFFLINE
}

fun cloudCopyDestinationStatus(
    albumId: Long,
    identity: CloudAlbumIdentity?,
    sourcesSupported: Boolean,
    accountAvailable: Boolean,
    supportsAlbumWrite: Boolean,
    readOnly: Boolean,
    offline: Boolean
): CloudCopyDestinationStatus = when {
    identity?.matches(albumId) != true -> CloudCopyDestinationStatus.MISSING_IDENTITY
    !sourcesSupported -> CloudCopyDestinationStatus.SOURCE_UNSUPPORTED
    !accountAvailable -> CloudCopyDestinationStatus.ACCOUNT_UNAVAILABLE
    !supportsAlbumWrite -> CloudCopyDestinationStatus.PROVIDER_UNSUPPORTED
    readOnly -> CloudCopyDestinationStatus.READ_ONLY
    offline -> CloudCopyDestinationStatus.OFFLINE
    else -> CloudCopyDestinationStatus.READY
}
