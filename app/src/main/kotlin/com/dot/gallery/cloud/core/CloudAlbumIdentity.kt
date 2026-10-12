/*
 * SPDX-FileCopyrightText: 2023-2026 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package com.dot.gallery.cloud.core

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class CloudAlbumIdentity(
    val providerType: ProviderType,
    val serverConfigId: Long,
    val remoteId: String
) : Parcelable {
    fun matches(albumId: Long): Boolean =
        serverConfigId > 0L && remoteId.isNotBlank() &&
            cloudAlbumId(providerType, serverConfigId, remoteId) == albumId
}
