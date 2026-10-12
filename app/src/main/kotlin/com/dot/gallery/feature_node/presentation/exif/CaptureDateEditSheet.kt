/*
 * SPDX-FileCopyrightText: 2023-2026 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package com.dot.gallery.feature_node.presentation.exif

import android.text.format.DateFormat
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dot.gallery.R
import com.dot.gallery.core.LocalMediaHandler
import com.dot.gallery.core.presentation.components.ModalSheet
import com.dot.gallery.core.presentation.components.SetupButton
import com.dot.gallery.core.util.SdkCompat
import com.dot.gallery.feature_node.domain.model.CaptureTimeOrigin
import com.dot.gallery.feature_node.domain.model.Media
import com.dot.gallery.feature_node.domain.model.resolvedCaptureTimeOrigin
import com.dot.gallery.feature_node.domain.repository.CaptureDateEditCapability
import com.dot.gallery.feature_node.domain.repository.CaptureDateEditResult
import com.dot.gallery.feature_node.domain.util.getUri
import com.dot.gallery.feature_node.presentation.util.AppBottomSheetState
import com.dot.gallery.feature_node.presentation.util.launchWriteRequest
import com.dot.gallery.feature_node.presentation.util.rememberActivityResult
import com.dot.gallery.feature_node.presentation.util.trashRequest
import com.dot.gallery.feature_node.presentation.util.writeRequest
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaptureDateEditSheet(
    state: AppBottomSheetState,
    media: Media,
) {
    val context = LocalContext.current
    val handler = LocalMediaHandler.current
    val scope = rememberCoroutineScope()
    val zoneId = remember { ZoneId.systemDefault() }
    val initialTimestamp = media.definedTimestamp * 1000L
    var selectedTimestamp by rememberSaveable(media.id) { mutableLongStateOf(initialTimestamp) }
    var busy by remember(media.id) { mutableStateOf(false) }
    val needsEmbeddedCaptureDate = media.resolvedCaptureTimeOrigin != CaptureTimeOrigin.EMBEDDED_IMAGE
    var showDatePicker by rememberSaveable(media.id) { mutableStateOf(false) }
    var showTimePicker by rememberSaveable(media.id) { mutableStateOf(false) }
    var copyCapability by remember { mutableStateOf<CaptureDateEditCapability?>(null) }
    var copyCreated by remember { mutableStateOf<CaptureDateEditResult.CopyCreated?>(null) }

    val performUpdate: () -> Unit = {
        busy = true
        scope.launch {
            when (val result = handler.updateMediaCaptureDate(media, selectedTimestamp)) {
                CaptureDateEditResult.Updated -> {
                    Toast.makeText(context, R.string.capture_date_saved, Toast.LENGTH_SHORT).show()
                    state.hide()
                }
                is CaptureDateEditResult.NeedsCopy -> copyCapability = result.capability
                is CaptureDateEditResult.Failed ->
                    Toast.makeText(context, result.reason, Toast.LENGTH_LONG).show()
                is CaptureDateEditResult.CopyCreated -> copyCreated = result
            }
            busy = false
        }
    }
    val writePermission = rememberActivityResult(onResultOk = performUpdate)
    val trashPermission = rememberActivityResult(
        onResultOk = { scope.launch { state.hide() } }
    )

    ModalSheet(
        sheetState = state,
        title = stringResource(
            if (needsEmbeddedCaptureDate) R.string.set_capture_date else R.string.edit_capture_date
        ),
        subtitle = stringResource(R.string.capture_date_may_move),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        content = {
        val dateTime = remember(selectedTimestamp, zoneId) {
            Instant.ofEpochMilli(selectedTimestamp).atZone(zoneId)
        }
        CaptureDateValueRow(
            title = stringResource(R.string.capture_date_date),
            value = dateTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
            onClick = { showDatePicker = true }
        )
        CaptureDateValueRow(
            title = stringResource(R.string.capture_date_time),
            value = dateTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)),
            onClick = { showTimePicker = true }
        )
        Text(
            text = dateTime.offset.id,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SetupButton(
                modifier = Modifier.weight(1f),
                applyHorizontalPadding = false,
                applyBottomPadding = false,
                applyInsets = false,
                text = stringResource(R.string.action_cancel),
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                contentColor = MaterialTheme.colorScheme.onSurface,
                onClick = { scope.launch { state.hide() } }
            )
            SetupButton(
                modifier = Modifier.weight(1f),
                applyHorizontalPadding = false,
                applyBottomPadding = false,
                applyInsets = false,
                enabled = !busy && (selectedTimestamp != initialTimestamp || needsEmbeddedCaptureDate),
                text = stringResource(R.string.save_date),
                onClick = {
                    scope.launch {
                        busy = true
                        when (val capability = handler.probeCaptureDateEdit(media)) {
                            CaptureDateEditCapability.DIRECT_WRITE -> {
                                busy = false
                                writePermission.launchWriteRequest(
                                    media.writeRequest(context.contentResolver),
                                    performUpdate
                                )
                            }
                            CaptureDateEditCapability.SAFE_COPY,
                            CaptureDateEditCapability.COPY_ONLY -> {
                                copyCapability = capability
                                busy = false
                            }
                            CaptureDateEditCapability.UNSUPPORTED -> {
                                busy = false
                                Toast.makeText(
                                    context,
                                    R.string.capture_date_failed,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            )
        }
    })

    if (showDatePicker) {
        val selectedDate = Instant.ofEpochMilli(selectedTimestamp).atZone(zoneId).toLocalDate()
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { pickerMillis ->
                        val date = Instant.ofEpochMilli(pickerMillis).atZone(ZoneOffset.UTC).toLocalDate()
                        val time = Instant.ofEpochMilli(selectedTimestamp).atZone(zoneId).toLocalTime()
                        selectedTimestamp = date.atTime(time).atZone(zoneId).toInstant().toEpochMilli()
                    }
                    showDatePicker = false
                }) { Text(stringResource(R.string.action_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        ) { DatePicker(state = datePickerState) }
    }

    if (showTimePicker) {
        val current = Instant.ofEpochMilli(selectedTimestamp).atZone(zoneId)
        val timePickerState = rememberTimePickerState(
            initialHour = current.hour,
            initialMinute = current.minute,
            is24Hour = DateFormat.is24HourFormat(context)
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text(stringResource(R.string.capture_date_time)) },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(onClick = {
                    val date = current.toLocalDate()
                    val time = LocalTime.of(timePickerState.hour, timePickerState.minute, current.second)
                    selectedTimestamp = date.atTime(time).atZone(zoneId).toInstant().toEpochMilli()
                    showTimePicker = false
                }) { Text(stringResource(R.string.action_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    copyCapability?.let { capability ->
        AlertDialog(
            onDismissRequest = { copyCapability = null },
            title = { Text(stringResource(R.string.dated_copy_required)) },
            text = {
                Text(
                    stringResource(
                        if (capability == CaptureDateEditCapability.SAFE_COPY) {
                            R.string.dated_copy_required_summary
                        } else {
                            R.string.dated_copy_loss_summary
                        }
                    )
                )
            },
            confirmButton = {
                TextButton(
                    enabled = !busy,
                    onClick = {
                        busy = true
                        scope.launch {
                            when (val result = handler.createDatedCopy(media, selectedTimestamp)) {
                                is CaptureDateEditResult.CopyCreated -> {
                                    copyCreated = result
                                    copyCapability = null
                                }
                                is CaptureDateEditResult.Failed ->
                                    Toast.makeText(context, result.reason, Toast.LENGTH_LONG).show()
                                else -> Unit
                            }
                            busy = false
                        }
                    }
                ) { Text(stringResource(R.string.create_copy)) }
            },
            dismissButton = {
                TextButton(onClick = { copyCapability = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    copyCreated?.let { result ->
        AlertDialog(
            onDismissRequest = {
                copyCreated = null
                scope.launch { state.hide() }
            },
            title = { Text(stringResource(R.string.dated_copy_created)) },
            text = { Text(stringResource(R.string.dated_copy_created_summary)) },
            confirmButton = {
                if (result.canTrashOriginal && SdkCompat.supportsTrash) {
                    TextButton(onClick = {
                        val request = media.getUri().trashRequest(context.contentResolver)
                        if (request != null) trashPermission.launch(request)
                        else scope.launch { state.hide() }
                        copyCreated = null
                    }) { Text(stringResource(R.string.move_original_to_trash)) }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    copyCreated = null
                    scope.launch { state.hide() }
                }) { Text(stringResource(R.string.keep_both)) }
            }
        )
    }
}

@Composable
private fun CaptureDateValueRow(
    title: String,
    value: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleSmall)
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
