/*
 * SPDX-FileCopyrightText: 2023-2026 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package com.dot.gallery.feature_node.presentation.exif

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dot.gallery.R
import com.dot.gallery.cloud.core.ProviderType
import com.dot.gallery.cloud.sync.CloudAlbumTransferMode
import com.dot.gallery.cloud.ui.descriptor.ProviderBrandIcon
import com.dot.gallery.core.presentation.components.LocalMediaImageRenderer
import com.dot.gallery.feature_node.domain.model.Album
import com.dot.gallery.feature_node.domain.model.Media
import com.dot.gallery.feature_node.domain.util.getUri

internal enum class TransferOpenTargetType {
    MEDIA,
    ALBUM
}

internal data class TransferOpenTarget(
    val type: TransferOpenTargetType,
    val albumId: Long,
    val albumLabel: String,
    val mediaId: Long? = null
)

internal fun resolveTransferOpenTarget(
    itemCount: Int,
    targetMediaId: Long?,
    destinationAlbumId: Long?,
    destinationLabel: String
): TransferOpenTarget? = when {
    itemCount == 1 && targetMediaId != null -> TransferOpenTarget(
        type = TransferOpenTargetType.MEDIA,
        albumId = destinationAlbumId ?: -1L,
        albumLabel = destinationLabel,
        mediaId = targetMediaId
    )
    destinationAlbumId != null -> TransferOpenTarget(
        type = TransferOpenTargetType.ALBUM,
        albumId = destinationAlbumId,
        albumLabel = destinationLabel
    )
    else -> null
}

internal data class TransferDestinationSection(
    val key: String,
    val title: String,
    val subtitle: String,
    val providerType: ProviderType?,
    val unavailable: Boolean,
    val albums: List<Album>
)

internal fun buildTransferDestinationSections(
    albums: List<Album>,
    accounts: Map<Long, CloudDestinationAccount>,
    localTitle: String,
    unavailableTitle: String
): List<TransferDestinationSection> {
    val local = albums.filter {
        it.cloudIdentity == null && it.uri.scheme != "cloud" && !it.relativePath.startsWith("cloud/")
    }
    val cloud = albums.mapNotNull { album -> album.cloudIdentity?.let { it to album } }
        .groupBy { it.first.serverConfigId }
    val unavailable = albums.filter {
        it.cloudIdentity == null && (it.uri.scheme == "cloud" || it.relativePath.startsWith("cloud/"))
    }
    return buildList {
        if (local.isNotEmpty()) add(
            TransferDestinationSection(
                key = "local",
                title = localTitle,
                subtitle = "",
                providerType = null,
                unavailable = false,
                albums = local
            )
        )
        cloud.entries.sortedBy {
            "${accounts[it.key]?.providerType?.displayName.orEmpty()}/" +
                "${accounts[it.key]?.title.orEmpty()}/${it.key}"
        }.forEach { (configId, entries) ->
            val account = accounts[configId]
            val providerType = entries.first().first.providerType
            add(
                TransferDestinationSection(
                    key = "cloud-$configId",
                    title = account?.title ?: providerType.displayName,
                    subtitle = account?.subtitle?.takeIf(String::isNotBlank)
                        ?: providerType.displayName,
                    providerType = providerType,
                    unavailable = false,
                    albums = entries.map { it.second }
                )
            )
        }
        if (unavailable.isNotEmpty()) add(
            TransferDestinationSection(
                key = "unavailable-cloud",
                title = unavailableTitle,
                subtitle = "",
                providerType = null,
                unavailable = true,
                albums = unavailable
            )
        )
    }
}

@Composable
internal fun TransferDestinationSectionHeader(
    section: TransferDestinationSection,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 6.dp, start = 8.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when {
            section.providerType != null -> ProviderBrandIcon(
                providerType = section.providerType,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            section.unavailable -> Icon(
                imageVector = Icons.Outlined.CloudOff,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            else -> Icon(
                imageVector = Icons.Outlined.PhotoLibrary,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text(
                text = section.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            if (section.subtitle.isNotBlank()) {
                Text(
                    text = section.subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
internal fun TransferJourneyPreview(
    media: List<Media>,
    destination: Album?,
    destinationLabel: String,
    mode: CloudAlbumTransferMode,
    progress: Float,
    modifier: Modifier = Modifier,
    showProgress: Boolean = true,
    showStatus: Boolean = true
) {
    val animatedProgress by animateFloatAsState(progress.coerceIn(0f, 1f), label = "transferProgress")
    val sourceLabel = media.singleOrNull()?.label
        ?: stringResource(R.string.category_media_count, media.size)
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TransferThumbnail(
                    model = media.firstOrNull()?.let { runCatching { it.getUri() }.getOrNull() },
                    label = sourceLabel,
                    count = media.size.takeIf { it > 1 },
                    fallbackProvider = null
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                TransferThumbnail(
                    model = destination?.uri,
                    label = destinationLabel,
                    count = null,
                    fallbackProvider = destination?.cloudIdentity?.providerType
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = sourceLabel,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = destinationLabel,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (showProgress) {
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
                )
            }
            if (showStatus) {
                Text(
                    text = stringResource(
                        if (mode == CloudAlbumTransferMode.MOVE) R.string.transfer_moving
                        else R.string.transfer_copying
                    ),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TransferThumbnail(
    model: Any?,
    label: String,
    count: Int?,
    fallbackProvider: ProviderType?
) {
    Box(
        modifier = Modifier
            .size(88.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHighest),
        contentAlignment = Alignment.Center
    ) {
        if (model != null && model.toString().isNotBlank()) {
            LocalMediaImageRenderer.current.RenderImage(
                modifier = Modifier.matchParentSize(),
                model = model,
                contentScale = ContentScale.Crop,
                contentDescription = label,
                signature = model
            )
        } else if (fallbackProvider != null) {
            ProviderBrandIcon(
                providerType = fallbackProvider,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.PhotoLibrary,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (count != null) {
            Text(
                text = count.toString(),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
internal fun TransferCompletionPanel(
    media: List<Media>,
    destination: Album?,
    destinationLabel: String,
    mode: CloudAlbumTransferMode,
    sourceRetained: Boolean,
    openLabel: String,
    onOpen: (() -> Unit)?,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(44.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = stringResource(
                when {
                    sourceRetained -> R.string.transfer_original_retained
                    mode == CloudAlbumTransferMode.MOVE -> R.string.transfer_move_complete
                    else -> R.string.transfer_copy_complete
                }
            ),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Text(
            text = if (media.size == 1) destinationLabel else stringResource(
                R.string.transfer_complete_summary,
                media.size,
                destinationLabel
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        TransferJourneyPreview(
            media = media,
            destination = destination,
            destinationLabel = destinationLabel,
            mode = mode,
            progress = 1f,
            showProgress = false,
            showStatus = false
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
        ) {
            TextButton(onClick = onDone) {
                Text(stringResource(R.string.done))
            }
            if (onOpen != null) {
                Button(onClick = onOpen) {
                    Text(openLabel)
                }
            }
        }
    }
}
