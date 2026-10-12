/*
 * SPDX-FileCopyrightText: 2023-2026 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package com.dot.gallery.cloud.ui.people

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PersonRemove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dot.gallery.R
import com.dot.gallery.core.LocalMediaSelector
import com.dot.gallery.core.presentation.components.SelectionBarColumn
import com.dot.gallery.core.presentation.components.SelectionSheet
import com.dot.gallery.feature_node.domain.model.Media
import com.dot.gallery.feature_node.domain.model.MediaState
import com.dot.gallery.feature_node.presentation.util.rememberAppBottomSheetState
import com.dot.gallery.feature_node.presentation.util.selectedMedia
import com.dot.gallery.feature_node.presentation.vault.components.ConfirmationSheet
import kotlinx.coroutines.launch

/**
 * Selection sheet shown on a local person's detail grid — the standard selection actions
 * plus a "Remove from person" action that un-assigns the selected media's faces from this
 * person (without deleting the media).
 */
@Composable
fun BoxScope.PersonSelectionSheet(
    allMedia: MediaState<Media.UriMedia>,
    personName: String,
    onRemoveFromPerson: (List<Long>) -> Unit,
) {
    val selector = LocalMediaSelector.current
    val selectedMedia = selector.selectedMedia.collectAsStateWithLifecycle()
    val selectedMediaList = allMedia.media.selectedMedia(selectedSet = selectedMedia)
    val scope = rememberCoroutineScope()
    val removeConfirmState = rememberAppBottomSheetState()

    SelectionSheet(
        modifier = Modifier.align(Alignment.BottomEnd),
        allMedia = allMedia,
        selectedMedia = selectedMediaList,
        extraBottomActions = {
            SelectionBarColumn(
                imageVector = Icons.Outlined.PersonRemove,
                title = stringResource(R.string.cloud_person_remove_media)
            ) {
                scope.launch { removeConfirmState.show() }
            }
        }
    )

    ConfirmationSheet(
        state = removeConfirmState,
        title = pluralStringResource(
            R.plurals.cloud_person_remove_media_title,
            selectedMediaList.size,
            selectedMediaList.size,
            personName
        ),
        summary = stringResource(R.string.cloud_person_remove_media_summary),
        confirmText = stringResource(R.string.cloud_person_remove_media_confirm),
        onConfirm = {
            onRemoveFromPerson(selectedMediaList.map { it.id })
        }
    )
}
