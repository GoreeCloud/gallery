package com.dot.gallery.feature_node.presentation.albumtimeline

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dot.gallery.core.MediaDistributor
import com.dot.gallery.core.metrics.StartupTracer
import com.dot.gallery.core.restorableAlbumTimelineMediaFlow
import com.dot.gallery.feature_node.domain.model.Media
import com.dot.gallery.feature_node.domain.model.MediaState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AlbumTimelineViewModel internal constructor(
    savedStateHandle: SavedStateHandle,
    distributor: MediaDistributor,
    trace: (String) -> Unit,
) : ViewModel() {
    @Inject constructor(savedStateHandle: SavedStateHandle, distributor: MediaDistributor) : this(
        savedStateHandle,
        distributor,
        { label -> StartupTracer.trace(label) {} },
    )
    val albumId: Long = checkNotNull(savedStateHandle["albumId"])
    val mediaState: StateFlow<MediaState<Media.UriMedia>> =
        distributor.restorableAlbumTimelineMediaFlow(albumId)
            .onStart { trace("AlbumSession.start($albumId)") }
            .onCompletion { trace("AlbumSession.stop($albumId)") }
            .stateIn(viewModelScope, SharingStarted.Eagerly, MediaState())
}
