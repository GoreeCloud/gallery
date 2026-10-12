package com.dot.gallery.core

import com.dot.gallery.cloud.core.SyncState
import com.dot.gallery.feature_node.domain.model.AlbumState
import com.dot.gallery.feature_node.domain.model.CollectionWithCount
import com.dot.gallery.feature_node.domain.model.GeoMedia
import com.dot.gallery.feature_node.domain.model.IgnoredAlbum
import com.dot.gallery.feature_node.domain.model.ImageEmbedding
import com.dot.gallery.feature_node.domain.model.Media
import com.dot.gallery.feature_node.domain.model.LocationMedia
import com.dot.gallery.feature_node.domain.util.MediaGroupType
import com.dot.gallery.feature_node.domain.model.MediaMetadataState
import com.dot.gallery.feature_node.domain.model.MediaState
import com.dot.gallery.feature_node.domain.model.LockedAlbum
import com.dot.gallery.feature_node.domain.model.MergedSubfolderAlbum
import com.dot.gallery.feature_node.domain.model.PinnedAlbum
import com.dot.gallery.feature_node.domain.model.TimelineSettings
import com.dot.gallery.feature_node.domain.model.Vault
import com.dot.gallery.feature_node.domain.model.VaultState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

enum class AlbumMediaLoadMode(val skipBatching: Boolean) {
    Progressive(false),
    Complete(true)
}

/**
 * Which media views a pending removal applies to. The scope mirrors the mutation: a trashed
 * item leaves the regular views but is arriving in the trash view, a restore only leaves the
 * trash view, and a permanent delete leaves every view.
 */
enum class PendingRemovalScope {
    /** Item is gone from every view (permanent delete). */
    EVERYWHERE,

    /** Item leaves regular views but is arriving in the trash view (trash op). */
    NON_TRASH,

    /** Item only leaves the trash view (restore out of trash). */
    TRASH_ONLY
}

interface MediaDistributor {

    /**
     * Pull-to-refresh
     */
    val isRefreshing: StateFlow<Boolean>
    suspend fun invalidate()

    /**
     * Common
     */
    val hasPermission: MutableStateFlow<Boolean>
    val dateFormatsFlow: StateFlow<Triple<String, String, String>>
    var groupByMonth: Boolean
    var groupByYear: Boolean
    val groupSimilarMedia: StateFlow<Boolean>
    val enabledGroupTypes: StateFlow<Set<MediaGroupType>>
    val mergeAlbumsByName: StateFlow<Boolean>

    /**
     * Settings
     */
    val settingsFlow: StateFlow<TimelineSettings?>

    /**
     * Albums
     */
    val albumsFlow: StateFlow<AlbumState>
    val blacklistedAlbumsFlow: StateFlow<List<IgnoredAlbum>>
    val pinnedAlbumsFlow: StateFlow<List<PinnedAlbum>>
    val lockedAlbumsFlow: StateFlow<List<LockedAlbum>>
    val mergedSubfolderAlbumsFlow: StateFlow<List<MergedSubfolderAlbum>>

    /**
     * Media
     */
    val timelineMediaFlow: SharedFlow<MediaState<Media.UriMedia>>
    fun albumTimelineMediaFlow(
        albumId: Long,
        loadMode: AlbumMediaLoadMode = AlbumMediaLoadMode.Progressive
    ): Flow<MediaState<Media.UriMedia>>
    val favoritesMediaFlow: SharedFlow<MediaState<Media.UriMedia>>
    val trashMediaFlow: SharedFlow<MediaState<Media.UriMedia>>

    /**
     * Optimistic mutations: media ids confirmed for trash/delete/restore are hidden from the
     * media flows until the underlying source catches up, and favorite toggles are applied
     * over the source the same way. Both self-heal — a pending id that reappears in a source
     * after being observed absent (e.g. a trash restore) is un-marked, overrides the source
     * already reflects are dropped, and surviving entries expire after a short TTL.
     */
    val pendingRemovalIds: StateFlow<Set<Long>>
    val favoriteOverrides: StateFlow<Map<Long, Boolean>>
    fun markPendingRemoval(
        ids: Collection<Long>,
        scope: PendingRemovalScope = PendingRemovalScope.EVERYWHERE
    )
    fun unmarkPendingRemoval(ids: Collection<Long>)
    fun setFavoriteOverride(mediaId: Long, favorite: Boolean)
    fun clearFavoriteOverride(mediaId: Long)

    /**
     * Cloud Sync States (media id → SyncState)
     */
    val cloudSyncStates: StateFlow<Map<Long, SyncState>>

    /**
     * Media Metadata
     */
    val metadataFlow: Flow<MediaMetadataState>
    val locationsMediaFlow: Flow<List<LocationMedia>>
    val geoMediaFlow: Flow<List<GeoMedia>>

    /**
     * Vault
     */
    val vaultsMediaFlow: StateFlow<VaultState>
    fun vaultMediaFlow(vault: Vault?): StateFlow<MediaState<Media.UriMedia>>

    /**
     * Collections
     */
    val collectionsFlow: StateFlow<List<CollectionWithCount>>
    val collectionAlbumIdsFlow: StateFlow<Set<Long>>
    fun collectionAlbumIdsInCollection(collectionId: Long): Flow<List<Long>>
    fun collectionMediaFlow(collectionId: Long): StateFlow<MediaState<Media.UriMedia>>

    /**
     * Search
     */
    val imageEmbeddingsFlow: StateFlow<List<ImageEmbedding>>


    fun locationBasedMedia(
        gpsLocationNameCity: String,
        gpsLocationNameCountry: String,
        latitude: Double?,
        longitude: Double?,
        additionalMediaIds: Flow<Set<Long>>,
    ): Flow<MediaState<Media.UriMedia>>
}

internal fun MediaDistributor.restorableAlbumTimelineMediaFlow(
    albumId: Long
): Flow<MediaState<Media.UriMedia>> = albumTimelineMediaFlow(
    albumId = albumId,
    loadMode = AlbumMediaLoadMode.Complete
)