package com.dot.gallery.feature_node.presentation.exif

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.dot.gallery.cloud.core.CloudAccountRuntimeSettings
import com.dot.gallery.cloud.core.CloudAlbumIdentity
import com.dot.gallery.cloud.core.CloudRuntimeSettings
import com.dot.gallery.cloud.core.CloudUri
import com.dot.gallery.cloud.core.ConnectionState
import com.dot.gallery.cloud.core.ProviderRegistry
import com.dot.gallery.cloud.core.ProviderType
import com.dot.gallery.cloud.core.cloudMediaId
import com.dot.gallery.cloud.core.isUnsupportedSameAccountCloudMove
import com.dot.gallery.cloud.core.capabilities.RemoteAlbumWriteProvider
import com.dot.gallery.cloud.core.capabilities.RemoteMediaProvider
import com.dot.gallery.cloud.data.dao.CloudServerConfigDao
import com.dot.gallery.cloud.offline.OfflineModeManager
import com.dot.gallery.cloud.sync.CloudAlbumCopyRequestStore
import com.dot.gallery.cloud.sync.CloudAlbumCopyWorker
import com.dot.gallery.cloud.sync.CloudAlbumTransferMode
import com.dot.gallery.cloud.sync.enqueueCloudAlbumCopy
import com.dot.gallery.core.workers.MediaCopyWorker
import com.dot.gallery.core.workers.copyMedia
import com.dot.gallery.feature_node.domain.model.Album
import com.dot.gallery.feature_node.domain.model.Media
import com.dot.gallery.feature_node.domain.util.getUri
import com.dot.gallery.feature_node.domain.util.isCloud
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class CloudDestinationAccount(
    val providerType: ProviderType,
    val title: String,
    val subtitle: String
)

data class CloudCopyEnvironment(
    val connectionStates: Map<Long, ConnectionState> = emptyMap(),
    val settingsByConfigId: Map<Long, CloudAccountRuntimeSettings> = emptyMap(),
    val accountsByConfigId: Map<Long, CloudDestinationAccount> = emptyMap(),
    val offline: Boolean = false
)

data class LocalCopyUiState(
    val workIds: List<UUID> = emptyList(),
    val destinationAlbumId: Long? = null,
    val destinationLabel: String = "",
    val total: Int = 0,
    val completed: Int = 0,
    val progress: Float = 0f,
    val active: Boolean = false,
    val finished: Boolean = false,
    val succeeded: Boolean = false,
    val targetMediaId: Long? = null,
    val message: String = ""
)

data class CloudCopyUiState(
    val requestId: String? = null,
    val workId: UUID? = null,
    val mode: CloudAlbumTransferMode = CloudAlbumTransferMode.COPY,
    val destination: CloudAlbumIdentity? = null,
    val destinationAlbumId: Long? = null,
    val destinationLabel: String = "",
    val targetRemoteId: String? = null,
    val total: Int = 0,
    val completed: Int = 0,
    val alreadyPresent: Int = 0,
    val failed: Int = 0,
    val current: Int = 0,
    val active: Boolean = false,
    val finished: Boolean = false,
    val succeeded: Boolean = false,
    val retryable: Boolean = false,
    val message: String = ""
) {
    val progress: Float
        get() = if (total <= 0) 0f else (current.toFloat() / total).coerceIn(0f, 1f)
    val canRetry: Boolean
        get() = finished && !succeeded && retryable && requestId != null
    val targetMediaId: Long?
        get() = destination?.let { identity ->
            targetRemoteId?.takeIf(String::isNotBlank)?.let {
                cloudMediaId(identity.providerType, identity.serverConfigId, it)
            }
        }
}

@HiltViewModel
class CopyMediaViewModel @Inject constructor(
    private val workManager: WorkManager,
    private val requestStore: CloudAlbumCopyRequestStore,
    private val registry: ProviderRegistry,
    private val configDao: CloudServerConfigDao,
    private val offlineModeManager: OfflineModeManager,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val workInfosFlow = workManager.getWorkInfosByTagFlow("MediaCopyWorker")
    private val _cloudCopyState = MutableStateFlow(CloudCopyUiState())
    val cloudCopyState: StateFlow<CloudCopyUiState> = _cloudCopyState.asStateFlow()
    private val _localCopyState = MutableStateFlow(LocalCopyUiState())
    val localCopyState: StateFlow<LocalCopyUiState> = _localCopyState.asStateFlow()
    private var cloudWorkJob: Job? = null
    private var localWorkJob: Job? = null

    val cloudEnvironment: StateFlow<CloudCopyEnvironment> = combine(
        registry.connectionStates,
        CloudRuntimeSettings.settingsByConfigId,
        configDao.getActive(),
        offlineModeManager.effectiveOffline
    ) { connectionStates, settings, configs, offline ->
        val accounts = configs.associate { config ->
            val host = runCatching { Uri.parse(config.serverUrl).host.orEmpty() }.getOrDefault("")
            val title = config.displayName.ifBlank {
                host.ifBlank { config.providerType.displayName }
            }
            val subtitle = listOf(
                config.providerType.displayName,
                config.username.orEmpty(),
                host.takeUnless { it == title }.orEmpty()
            ).filter(String::isNotBlank).distinct().joinToString(" · ")
            config.id to CloudDestinationAccount(
                providerType = config.providerType,
                title = title,
                subtitle = subtitle
            )
        }
        CloudCopyEnvironment(connectionStates, settings, accounts, offline)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CloudCopyEnvironment())

    val isActive: StateFlow<Boolean> = workInfosFlow
        .map { list -> list.any { it.state == WorkInfo.State.RUNNING || it.state == WorkInfo.State.ENQUEUED } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val progress: StateFlow<Float> = workInfosFlow
        .map { list ->
            // Only consider work that is currently running or enqueued
            val activeWork = list.filter { it.state == WorkInfo.State.RUNNING || it.state == WorkInfo.State.ENQUEUED }
            
            // If no active work, return 0 to show the album selection UI
            if (activeWork.isEmpty()) return@map 0f
            
            val progressValues = activeWork.map { it.progress.getInt("progress", 0) }
            val avg = if (progressValues.isNotEmpty()) progressValues.sum() / progressValues.size else 0
            avg.coerceIn(0, 100) / 100f
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0f)

    init {
        savedStateHandle.get<String>(KEY_CLOUD_WORK_ID)
            ?.let { runCatching { UUID.fromString(it) }.getOrNull() }
            ?.let(::observeCloudWork)
    }

    fun cloudDestinationStatus(
        album: Album,
        media: List<Media>,
        environment: CloudCopyEnvironment
    ): CloudCopyDestinationStatus? {
        val identity = album.cloudIdentity ?: return null
        val provider = registry.getByConfigId(identity.serverConfigId)
            ?.takeIf { it.providerType == identity.providerType }
        val sourcesSupported = media.isNotEmpty() && media.all { it.isCloudTransferSource() }
        return cloudCopyDestinationStatus(
            albumId = album.id,
            identity = identity,
            sourcesSupported = sourcesSupported,
            accountAvailable = provider?.isAvailable == true &&
                environment.connectionStates[identity.serverConfigId].let {
                    it == ConnectionState.CONNECTED || it == ConnectionState.SYNCING
                },
            supportsAlbumWrite = provider is RemoteAlbumWriteProvider,
            readOnly = environment.settingsByConfigId[identity.serverConfigId]?.readOnlyMode == true,
            offline = environment.offline
        )
    }

    fun cloudMoveSourceStatus(
        media: List<Media>,
        environment: CloudCopyEnvironment
    ): CloudCopyDestinationStatus {
        if (media.isEmpty() || !media.all { it.isCloudTransferSource() }) {
            return CloudCopyDestinationStatus.SOURCE_UNSUPPORTED
        }
        val cloudSources = media.filter { it.isCloud }
        if (cloudSources.isNotEmpty() && cloudSources.size != media.size) {
            return CloudCopyDestinationStatus.MOVE_UNSUPPORTED
        }
        for (sourceMedia in cloudSources) {
            val source = CloudUri.parse(sourceMedia.getUri().toString())
                ?: return CloudCopyDestinationStatus.SOURCE_UNSUPPORTED
            val sourceProvider = registry.getByConfigId(source.configId)
                ?.takeIf { it.providerType == source.providerType } as? RemoteMediaProvider
                ?: return CloudCopyDestinationStatus.ACCOUNT_UNAVAILABLE
            if (!sourceProvider.isAvailable) return CloudCopyDestinationStatus.ACCOUNT_UNAVAILABLE
            if (environment.settingsByConfigId[source.configId]?.readOnlyMode == true) {
                return CloudCopyDestinationStatus.READ_ONLY
            }
        }
        return CloudCopyDestinationStatus.READY
    }

    fun cloudMoveDestinationStatus(
        album: Album,
        media: List<Media>,
        environment: CloudCopyEnvironment
    ): CloudCopyDestinationStatus? {
        val base = cloudDestinationStatus(album, media, environment) ?: return null
        if (base != CloudCopyDestinationStatus.READY) return base
        val sourceStatus = cloudMoveSourceStatus(media, environment)
        if (sourceStatus != CloudCopyDestinationStatus.READY) return sourceStatus
        for (sourceMedia in media.filter { it.isCloud }) {
            val source = CloudUri.parse(sourceMedia.getUri().toString())
                ?: return CloudCopyDestinationStatus.SOURCE_UNSUPPORTED
            if (isUnsupportedSameAccountCloudMove(
                    source,
                    requireNotNull(album.cloudIdentity),
                    sourceMedia.label
                )
            ) return CloudCopyDestinationStatus.MOVE_UNSUPPORTED
        }
        return CloudCopyDestinationStatus.READY
    }

    fun enqueueCloudCopy(
        media: List<Media>,
        album: Album,
        mode: CloudAlbumTransferMode = CloudAlbumTransferMode.COPY
    ) {
        val identity = album.cloudIdentity ?: return
        if (!identity.matches(album.id) || media.isEmpty() || !media.all { it.isCloudTransferSource() }) return
        if (mode == CloudAlbumTransferMode.MOVE &&
            cloudMoveDestinationStatus(album, media, cloudEnvironment.value) != CloudCopyDestinationStatus.READY
        ) return
        _cloudCopyState.value = CloudCopyUiState(
            mode = mode,
            destination = identity,
            destinationAlbumId = album.id,
            destinationLabel = album.label,
            total = media.size,
            active = true
        )
        viewModelScope.launch {
            runCatching {
                requestStore.create(album.id, identity, album.label, media, mode)
            }.onSuccess { request ->
                val workId = workManager.enqueueCloudAlbumCopy(request.id)
                savedStateHandle[KEY_CLOUD_REQUEST_ID] = request.id
                savedStateHandle[KEY_CLOUD_WORK_ID] = workId.toString()
                _cloudCopyState.value = _cloudCopyState.value.copy(
                    requestId = request.id,
                    workId = workId
                )
                observeCloudWork(workId)
            }.onFailure { error ->
                _cloudCopyState.value = _cloudCopyState.value.copy(
                    active = false,
                    finished = true,
                    message = error.message ?: "Could not start cloud copy"
                )
            }
        }
    }

    fun retryCloudCopy() {
        val requestId = _cloudCopyState.value.requestId
            ?: savedStateHandle.get<String>(KEY_CLOUD_REQUEST_ID)
            ?: return
        val workId = workManager.enqueueCloudAlbumCopy(requestId)
        savedStateHandle[KEY_CLOUD_WORK_ID] = workId.toString()
        _cloudCopyState.value = _cloudCopyState.value.copy(
            workId = workId,
            active = true,
            finished = false,
            succeeded = false,
            retryable = false,
            message = ""
        )
        observeCloudWork(workId)
    }

    fun clearCloudCopyResult() {
        val requestId = _cloudCopyState.value.requestId
            ?: savedStateHandle.get<String>(KEY_CLOUD_REQUEST_ID)
        if (requestId != null) viewModelScope.launch { requestStore.delete(requestId) }
        cloudWorkJob?.cancel()
        cloudWorkJob = null
        savedStateHandle.remove<String>(KEY_CLOUD_REQUEST_ID)
        savedStateHandle.remove<String>(KEY_CLOUD_WORK_ID)
        _cloudCopyState.value = CloudCopyUiState()
    }

    private fun observeCloudWork(workId: UUID) {
        cloudWorkJob?.cancel()
        cloudWorkJob = viewModelScope.launch {
            workManager.getWorkInfoByIdFlow(workId).filterNotNull().collect { info ->
                val current = _cloudCopyState.value
                val data = if (info.state.isFinished) info.outputData else info.progress
                val active = info.state == WorkInfo.State.RUNNING ||
                    info.state == WorkInfo.State.ENQUEUED ||
                    info.state == WorkInfo.State.BLOCKED
                val succeeded = info.state == WorkInfo.State.SUCCEEDED
                val providerType = data.getString(CloudAlbumCopyWorker.KEY_DESTINATION_PROVIDER)
                    ?.let { runCatching { ProviderType.valueOf(it) }.getOrNull() }
                val configId = data.getLong(
                    CloudAlbumCopyWorker.KEY_DESTINATION_CONFIG_ID,
                    current.destination?.serverConfigId ?: 0L
                )
                val remoteAlbumId = data.getString(
                    CloudAlbumCopyWorker.KEY_DESTINATION_REMOTE_ALBUM_ID
                ) ?: current.destination?.remoteId.orEmpty()
                val destination = if (providerType != null && configId > 0L && remoteAlbumId.isNotBlank()) {
                    CloudAlbumIdentity(providerType, configId, remoteAlbumId)
                } else current.destination
                val destinationAlbumId = data.getLong(
                    CloudAlbumCopyWorker.KEY_DESTINATION_ALBUM_ID,
                    current.destinationAlbumId ?: 0L
                ).takeIf { it != 0L }
                _cloudCopyState.value = current.copy(
                    requestId = data.getString(CloudAlbumCopyWorker.KEY_REQUEST_ID) ?: current.requestId
                        ?: savedStateHandle.get<String>(KEY_CLOUD_REQUEST_ID),
                    workId = workId,
                    mode = data.getString(CloudAlbumCopyWorker.KEY_MODE)
                        ?.let { runCatching { CloudAlbumTransferMode.valueOf(it) }.getOrNull() }
                        ?: current.mode,
                    destination = destination,
                    destinationAlbumId = destinationAlbumId,
                    destinationLabel = data.getString(CloudAlbumCopyWorker.KEY_DESTINATION_LABEL)
                        ?: current.destinationLabel,
                    targetRemoteId = data.getString(CloudAlbumCopyWorker.KEY_TARGET_REMOTE_ID)
                        ?.takeIf(String::isNotBlank) ?: current.targetRemoteId,
                    total = data.getInt(CloudAlbumCopyWorker.KEY_TOTAL, current.total),
                    completed = data.getInt(CloudAlbumCopyWorker.KEY_COMPLETED, current.completed),
                    alreadyPresent = data.getInt(
                        CloudAlbumCopyWorker.KEY_ALREADY_PRESENT,
                        current.alreadyPresent
                    ),
                    failed = data.getInt(CloudAlbumCopyWorker.KEY_FAILED, current.failed),
                    current = data.getInt(CloudAlbumCopyWorker.KEY_CURRENT, current.current),
                    active = active,
                    finished = info.state.isFinished,
                    succeeded = succeeded,
                    retryable = data.getBoolean(CloudAlbumCopyWorker.KEY_RETRYABLE, false),
                    message = data.getString(CloudAlbumCopyWorker.KEY_MESSAGE).orEmpty()
                )
            }
        }
    }

    fun <T : Media> enqueueLocalCopy(
        media: List<T>,
        destination: Album,
        onStarted: () -> Unit = {}
    ) {
        if (media.isEmpty()) return
        val workIds = workManager.copyMedia(
            *media.map { it to destination.absolutePath }.toTypedArray()
        )
        _localCopyState.value = LocalCopyUiState(
            workIds = workIds,
            destinationAlbumId = destination.id,
            destinationLabel = destination.label,
            total = media.size,
            active = true
        )
        observeLocalCopy(workIds)
        onStarted()
    }

    fun clearLocalCopyResult() {
        localWorkJob?.cancel()
        localWorkJob = null
        _localCopyState.value = LocalCopyUiState()
    }

    private fun observeLocalCopy(workIds: List<UUID>) {
        localWorkJob?.cancel()
        if (workIds.isEmpty()) return
        localWorkJob = viewModelScope.launch {
            combine(workIds.map { workManager.getWorkInfoByIdFlow(it).filterNotNull() }) { infos ->
                val active = infos.any {
                    it.state == WorkInfo.State.RUNNING ||
                        it.state == WorkInfo.State.ENQUEUED ||
                        it.state == WorkInfo.State.BLOCKED
                }
                val finished = infos.all { it.state.isFinished }
                val succeeded = finished && infos.all { it.state == WorkInfo.State.SUCCEEDED }
                val completed = infos.count { it.state == WorkInfo.State.SUCCEEDED }
                val progress = when {
                    succeeded -> 1f
                    infos.isEmpty() -> 0f
                    else -> infos.map { info -> info.progress.getInt("progress", 0) }
                        .average().toFloat().div(100f).coerceIn(0f, 1f)
                }
                val targetMediaId = if (_localCopyState.value.total == 1) {
                    infos.singleOrNull()?.outputData
                        ?.getString(MediaCopyWorker.KEY_TARGET_URI)
                        ?.let(Uri::parse)?.lastPathSegment?.toLongOrNull()
                } else null
                _localCopyState.value.copy(
                    completed = completed,
                    progress = progress,
                    active = active,
                    finished = finished,
                    succeeded = succeeded,
                    targetMediaId = targetMediaId,
                    message = ""
                )
            }.collect { _localCopyState.value = it }
        }
    }

    fun <T: Media> enqueueCopy(vararg sets: Pair<T, String>, onStarted: () -> Unit = {}) {
        if (sets.isEmpty()) return
        workManager.copyMedia(*sets)
        onStarted()
    }

    private fun Media.isCloudTransferSource(): Boolean {
        val uri = runCatching { getUri() }.getOrNull() ?: return false
        if (!isCloud) return uri.scheme == "content" || uri.scheme == "file"
        val source = CloudUri.parse(uri.toString()) ?: return false
        if (source.configId <= 0L) return false
        return (registry.getByConfigId(source.configId) as? RemoteMediaProvider)
            ?.takeIf { it.providerType == source.providerType }
            ?.isAvailable == true
    }

    companion object {
        private const val KEY_CLOUD_REQUEST_ID = "cloud_copy_request_id"
        private const val KEY_CLOUD_WORK_ID = "cloud_copy_work_id"
    }
}
