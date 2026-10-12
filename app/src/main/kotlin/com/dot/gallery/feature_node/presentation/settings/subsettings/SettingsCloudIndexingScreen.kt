/*
 * SPDX-FileCopyrightText: 2023-2026 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package com.dot.gallery.feature_node.presentation.settings.subsettings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dot.gallery.R
import com.dot.gallery.cloud.core.ProviderCapability
import com.dot.gallery.core.Position
import com.dot.gallery.core.SettingsEntity
import com.dot.gallery.feature_node.presentation.settings.components.ChooserPreferenceDetailScreen
import com.dot.gallery.feature_node.presentation.settings.components.SettingsItem

@Composable
fun SettingsCloudIndexingScreen(
    viewModel: SmartFeaturesViewModel = hiltViewModel()
) {
    val configuredCloudProviders by viewModel.configuredCloudProviders.collectAsStateWithLifecycle()
    val indexOnDeviceProviders by viewModel.indexOnDeviceProviders.collectAsStateWithLifecycle()
    val providerSmartSearch by viewModel.providerSmartSearch.collectAsStateWithLifecycle()

    ChooserPreferenceDetailScreen<Unit>(
        title = stringResource(R.string.smart_features_cloud_section),
        description = stringResource(R.string.smart_features_cloud_indexing_description),
        customContent = {
            Column(modifier = Modifier.fillMaxWidth()) {
                SettingsItem(
                    item = SettingsEntity.SwitchPreference(
                        title = stringResource(R.string.smart_features_provider_smart_search),
                        summary = stringResource(R.string.smart_features_provider_smart_search_summary),
                        isChecked = providerSmartSearch,
                        onCheck = viewModel::setProviderSmartSearch,
                        screenPosition = Position.Alone
                    )
                )
                if (configuredCloudProviders.isNotEmpty()) {
                    SettingsItem(
                        item = SettingsEntity.Header(
                            title = stringResource(R.string.smart_features_index_on_device_section)
                        )
                    )
                    configuredCloudProviders.forEachIndexed { index, type ->
                        val delegates =
                            ProviderCapability.SMART_SEARCH in viewModel.capabilitiesOf(type)
                        SettingsItem(
                            item = SettingsEntity.SwitchPreference(
                                title = stringResource(
                                    R.string.smart_features_index_on_device,
                                    type.displayName
                                ),
                                summary = if (delegates) {
                                    stringResource(
                                        R.string.smart_features_index_on_device_delegated_summary,
                                        type.displayName
                                    )
                                } else {
                                    stringResource(R.string.smart_features_index_on_device_summary)
                                },
                                isChecked = type.name in indexOnDeviceProviders,
                                onCheck = {
                                    viewModel.setIndexOnDeviceProvider(type, it)
                                },
                                screenPosition = when {
                                    configuredCloudProviders.size == 1 -> Position.Alone
                                    index == 0 -> Position.Top
                                    index == configuredCloudProviders.lastIndex -> Position.Bottom
                                    else -> Position.Middle
                                }
                            )
                        )
                    }
                }
            }
        }
    )
}
