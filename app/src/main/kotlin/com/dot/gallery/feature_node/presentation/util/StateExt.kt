/*
 * SPDX-FileCopyrightText: 2023-2026 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package com.dot.gallery.feature_node.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastMap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.dot.gallery.core.Constants
import com.dot.gallery.core.Resource
import com.dot.gallery.feature_node.domain.model.Media
import com.dot.gallery.feature_node.domain.model.Media.UriMedia
import com.dot.gallery.feature_node.domain.model.MediaItem
import com.dot.gallery.feature_node.domain.model.MediaState
import com.dot.gallery.feature_node.domain.model.TimelineDateSource
import com.dot.gallery.feature_node.domain.model.timestampFor
import com.dot.gallery.feature_node.domain.repository.MediaRepository
import com.dot.gallery.feature_node.domain.util.MediaOrder
import com.dot.gallery.feature_node.domain.util.MediaGroupType
import com.dot.gallery.feature_node.domain.util.classifyGroupType
import com.dot.gallery.feature_node.domain.util.groupKey
import com.dot.gallery.feature_node.domain.util.selectRepresentative
import com.dot.gallery.feature_node.domain.util.sortedByRepresentative
import com.dot.gallery.feature_node.presentation.mediaview.rememberedDerivedState
import com.dot.gallery.feature_node.presentation.picker.AllowedMedia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun <T: Media> selectedMedia(
    media: List<T>,
    selectedSet: State<Set<Long>>
): State<SnapshotStateList<T>> = rememberedDerivedState(media, selectedSet.value) {
    media.fastFilter { selectedSet.value.contains(it.id) }.toMutableStateList()
}

@Composable
fun <T: Media> List<T>.selectedMedia(selectedSet: State<Set<Long>>) =
    remember(this, selectedSet.value) { filter { selectedSet.value.contains(it.id) }.toMutableStateList() }

val <T> MutableState<Set<T>>.size get() = value.size

fun <T> MutableState<Set<T>>.clear() {
    value = emptySet()
}

fun <T> MutableState<Set<T>>.add(item: T) {
    value = value.plus(item)
}

fun <T> MutableState<Set<T>>.add(items: Array<out T>) {
    value = value.plus(items)
}

fun <T> MutableState<Set<T>>.add(items: Collection<T>) {
    value = value.plus(items.toSet())
}

fun <T> MutableState<Set<T>>.remove(item: T) {
    value = value.minus(item)
}

fun <T> MutableState<Set<T>>.remove(items: Array<out T>) {
    value = value.minus(items.toSet())
}

fun <T> MutableState<Set<T>>.remove(items: Collection<T>) {
    value = value.minus(items.toSet())
}

@Composable
fun RepeatOnResume(action: () -> Unit) {
    val owner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        owner.lifecycleScope.launch {
            owner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                action()
            }
        }
    }
}

fun <T> MutableState<T>.update(newState: T) {
    value = newState
}

fun MediaRepository.mediaFlowWithType(
    albumId: Long,
    allowedMedia: AllowedMedia
): Flow<Resource<List<UriMedia>>> =
    (if (albumId != -1L) {
        getMediaByAlbumIdWithType(albumId, allowedMedia)
    } else {
        getMediaByType(allowedMedia)
    }).flowOn(Dispatchers.IO).conflate()

fun MediaRepository.mediaFlow(albumId: Long, target: String?): Flow<Resource<List<UriMedia>>> =
    (if (albumId != -1L) {
        getMediaByAlbumId(albumId)
    } else if (!target.isNullOrEmpty()) {
        when (target) {
            Constants.Target.TARGET_FAVORITES -> getFavorites(mediaOrder = MediaOrder.Default)
            Constants.Target.TARGET_TRASH -> getTrashed()
            else -> getMedia()
        }
    } else {
        getMedia()
    })

fun <T : Media> Flow<Resource<List<T>>>.mapMedia(
    albumId: Long,
    groupByMonth: Boolean = false,
    groupByYear: Boolean = false,
    withMonthHeader: Boolean = true,
    groupSimilarMedia: Boolean = false,
    enabledGroupTypes: Set<MediaGroupType> = MediaGroupType.entries.toSet(),
    updateDatabase: () -> Unit,
    defaultDateFormat: String,
    extendedDateFormat: String,
    weeklyDateFormat: String,
    dateSource: TimelineDateSource = TimelineDateSource.CAPTURE_TIME
) = map {
    updateDatabase()
    mapMediaToItem(
        data = it.data ?: emptyList(),
        error = it.message ?: "",
        albumId = albumId,
        groupByMonth = groupByMonth,
        groupByYear = groupByYear,
        withMonthHeader = withMonthHeader,
        groupSimilarMedia = groupSimilarMedia,
        enabledGroupTypes = enabledGroupTypes,
        defaultDateFormat = defaultDateFormat,
        extendedDateFormat = extendedDateFormat,
        weeklyDateFormat = weeklyDateFormat,
        dateSource = dateSource
    )
}

suspend fun <T : Media> MutableStateFlow<MediaState<T>>.collectMedia(
    data: List<T>,
    error: String,
    albumId: Long,
    groupByMonth: Boolean = false,
    groupByYear: Boolean = false,
    withMonthHeader: Boolean = true,
    groupSimilarMedia: Boolean = false,
    enabledGroupTypes: Set<MediaGroupType> = MediaGroupType.entries.toSet(),
    cloudGroupKeyOverrides: Map<Long, String> = emptyMap(),
    defaultDateFormat: String,
    extendedDateFormat: String,
    weeklyDateFormat: String,
    dateSource: TimelineDateSource = TimelineDateSource.CAPTURE_TIME
) = withContext(Dispatchers.IO) {
    emit(
        mapMediaToItem(
            data = data,
            error = error,
            albumId = albumId,
            groupByMonth = groupByMonth,
            groupByYear = groupByYear,
            withMonthHeader = withMonthHeader,
            groupSimilarMedia = groupSimilarMedia,
            enabledGroupTypes = enabledGroupTypes,
            cloudGroupKeyOverrides = cloudGroupKeyOverrides,
            defaultDateFormat = defaultDateFormat,
            extendedDateFormat = extendedDateFormat,
            weeklyDateFormat = weeklyDateFormat,
            dateSource = dateSource
        )
    )
}

suspend fun <T : Media> mapMediaToItem(
    data: List<T>,
    error: String,
    albumId: Long,
    groupByMonth: Boolean = false,
    groupByYear: Boolean = false,
    withMonthHeader: Boolean = true,
    groupSimilarMedia: Boolean = false,
    enabledGroupTypes: Set<MediaGroupType> = MediaGroupType.entries.toSet(),
    cloudGroupKeyOverrides: Map<Long, String> = emptyMap(),
    cloudBackups: Map<Long, List<Media.UriMedia>> = emptyMap(),
    defaultDateFormat: String,
    extendedDateFormat: String,
    weeklyDateFormat: String,
    dateSource: TimelineDateSource = TimelineDateSource.CAPTURE_TIME
) = withContext(Dispatchers.IO) {
    val estimatedSize = data.size + (data.size / 20) // ~1 header per 20 items
    val mappedData = ArrayList<MediaItem<T>>(estimatedSize)
    val mappedDataWithMonthly = if (withMonthHeader) ArrayList<MediaItem<T>>(estimatedSize) else mutableListOf()
    val mappedDataWithYearly = if (withMonthHeader) ArrayList<MediaItem<T>>(estimatedSize) else mutableListOf()
    val monthHeaderList = HashSet<String>()
    val yearHeaderList = HashSet<String>()
    val headers = ArrayList<MediaItem.Header<T>>(estimatedSize / 20 + 1)
    val pagerMediaList = if (groupSimilarMedia) ArrayList<T>(data.size) else mutableListOf()
    val mediaGroupsMap = if (groupSimilarMedia) HashMap<Long, List<T>>() else mutableMapOf()

    // DateGrouper pre-computes locale, todayStartMillis, and currentYear once,
    // then reuses a single Calendar per item instead of allocating 4 new ones.
    val dateGrouper = if (!groupByMonth && !groupByYear) DateGrouper(
        format = defaultDateFormat,
        weeklyFormat = weeklyDateFormat,
        extendedFormat = extendedDateFormat,
        /** Localized in composition */
        stringToday = "Today",
        stringYesterday = "Yesterday"
    ) else null
    val groupedData = data.groupBy {
        val timestamp = it.timestampFor(dateSource)
        when {
            groupByYear -> timestamp.getYear()
            groupByMonth -> timestamp.getMonth()
            else -> dateGrouper!!.classify(timestamp)
        }
    }
    val hasCloudOverrides = cloudGroupKeyOverrides.isNotEmpty()
    groupedData.forEach { (date, data) ->
        val dateHeader = MediaItem.Header<T>("header_$date", date, data.mapTo(HashSet(data.size)) { it.id })
        headers.add(dateHeader)
        val groupedMedia = if (groupSimilarMedia) {
            // Use pre-computed override keys for cloud items so they group with local counterparts
            val groups = if (hasCloudOverrides) {
                data.groupBy { cloudGroupKeyOverrides[it.id] ?: it.groupKey }
            } else {
                data.groupBy { it.groupKey }
            }
            groups.values.flatMap { group ->
                if (group.size > 1) {
                    val groupType = group.classifyGroupType()
                    if (groupType in enabledGroupTypes) {
                        val representative = group.selectRepresentative()
                        pagerMediaList.add(representative)
                        // Store members ordered so the representative (JPG over its RAW/DNG
                        // sibling) is first — keeps the opened stack and multi-file share aligned
                        // with the grid cover (#995).
                        mediaGroupsMap[representative.id] = group.sortedByRepresentative()
                        return@flatMap listOf(
                            MediaItem.MediaViewItem(
                                key = "media_${representative.id}",
                                media = representative,
                                stackCount = group.size,
                                isCloudGroup = groupType == MediaGroupType.CLOUD_LOCAL
                            )
                        )
                    }
                }
                group.fastMap { media ->
                    pagerMediaList.add(media)
                    MediaItem.MediaViewItem("media_${media.id}", media)
                }
            }
        } else {
            data.fastMap {
                MediaItem.MediaViewItem("media_${it.id}", it)
            }
        }
        if (groupByYear) {
            mappedData.add(dateHeader)
            mappedData.addAll(groupedMedia)
            mappedDataWithYearly.add(dateHeader)
            mappedDataWithYearly.addAll(groupedMedia)
        } else if (groupByMonth) {
            mappedData.add(dateHeader)
            mappedData.addAll(groupedMedia)
            mappedDataWithMonthly.add(dateHeader)
            mappedDataWithMonthly.addAll(groupedMedia)
        } else {
            val month = getMonth(
                defaultFormat = defaultDateFormat,
                extendedFormat = extendedDateFormat,
                date = date
            )
            if (month.isNotEmpty() && !monthHeaderList.contains(month)) {
                monthHeaderList.add(month)
                val bigMonthHeader = MediaItem.Header<T>(
                    "header_big_${month}_${data.size}",
                    month,
                    dateHeader.data
                )
                if (mappedData.isNotEmpty()) {
                    mappedData.add(bigMonthHeader)
                }
                if (withMonthHeader && mappedDataWithMonthly.isNotEmpty()) {
                    mappedDataWithMonthly.add(bigMonthHeader)
                }
            }
            mappedData.add(dateHeader)
            if (withMonthHeader) {
                mappedDataWithMonthly.add(dateHeader)
            }
            mappedData.addAll(groupedMedia)
            if (withMonthHeader) {
                mappedDataWithMonthly.addAll(groupedMedia)
            }
        }
        if (!groupByYear && withMonthHeader) {
            val year = data.firstOrNull()?.timestampFor(dateSource)?.getYear() ?: ""
            if (year.isNotEmpty() && !yearHeaderList.contains(year)) {
                yearHeaderList.add(year)
                if (mappedDataWithYearly.isNotEmpty()) {
                    mappedDataWithYearly.add(
                        MediaItem.Header(
                            "header_big_${year}_${data.size}",
                            year,
                            dateHeader.data
                        )
                    )
                }
            }
            mappedDataWithYearly.add(dateHeader)
            mappedDataWithYearly.addAll(groupedMedia)
        }
    }
    MediaState(
        isLoading = false,
        error = error,
        media = data,
        pagerMedia = (if (groupSimilarMedia) pagerMediaList else data).distinctBy { it.id },
        mediaGroups = mediaGroupsMap,
        headers = headers,
        mappedMedia = mappedData,
        mappedMediaWithMonthly = if (withMonthHeader) mappedDataWithMonthly else emptyList(),
        mappedMediaWithYearly = if (withMonthHeader) mappedDataWithYearly else emptyList(),
        cloudBackups = cloudBackups,
        dateHeader = data.dateHeader(albumId, dateSource),
        dateSource = dateSource
    )
}

private fun List<Media>.dateHeader(
    albumId: Long,
    dateSource: TimelineDateSource
): String =
    if (albumId != -1L && isNotEmpty()) {
        val startDate: DateExt = last().timestampFor(dateSource).getDateExt()
        val endDate: DateExt = first().timestampFor(dateSource).getDateExt()
        getDateHeader(startDate, endDate)
    } else ""

/**
 * Applies optimistic mutations — pending removals and favorite overrides — to an already
 * mapped [MediaState] without re-running [mapMediaToItem]. Removed group representatives
 * promote their first surviving member so grouped stacks stay consistent, and headers whose
 * section loses every item are dropped.
 */
internal fun MediaState<Media.UriMedia>.applyOptimisticMutations(
    pendingRemovalIds: Set<Long>,
    favoriteOverrides: Map<Long, Boolean>,
    dropUnfavorited: Boolean = false,
): MediaState<Media.UriMedia> {
    if (pendingRemovalIds.isEmpty() && favoriteOverrides.isEmpty()) return this

    fun Media.UriMedia.mutated(): Media.UriMedia? {
        if (id in pendingRemovalIds) return null
        val effective = favoriteOverrides[id]?.let { fav ->
            if ((favorite == 1) != fav) copy(favorite = if (fav) 1 else 0) else this
        } ?: this
        return if (dropUnfavorited && effective.favorite != 1) null else effective
    }

    // Mutate groups first — representative promotion depends on surviving members.
    // promoted maps a removed representative's id to its replacement media.
    val promoted = HashMap<Long, Media.UriMedia>()
    val newGroups = HashMap<Long, List<Media.UriMedia>>(mediaGroups.size)
    mediaGroups.forEach { (repId, members) ->
        val surviving = members.mapNotNull { it.mutated() }
        if (surviving.isNotEmpty()) {
            val rep = if (surviving.any { it.id == repId }) {
                surviving.first { it.id == repId }
            } else {
                surviving.first().also { promoted[repId] = it }
            }
            if (surviving.size > 1) newGroups[rep.id] = surviving
        }
    }

    fun List<MediaItem<Media.UriMedia>>.mutatedItems(): List<MediaItem<Media.UriMedia>> {
        val out = ArrayList<MediaItem<Media.UriMedia>>(size)
        var heldHeaders: MutableList<MediaItem.Header<Media.UriMedia>>? = null
        for (item in this) {
            when (item) {
                is MediaItem.Header -> {
                    if (heldHeaders == null) heldHeaders = ArrayList(2)
                    heldHeaders.add(
                        if (pendingRemovalIds.isEmpty()) item
                        else item.copy(data = item.data - pendingRemovalIds)
                    )
                }
                is MediaItem.MediaViewItem -> {
                    val replacement = item.media.mutated() ?: promoted[item.media.id]
                    if (replacement != null) {
                        heldHeaders?.let { out.addAll(it); heldHeaders = null }
                        val group = newGroups[replacement.id]
                        out.add(
                            item.copy(
                                media = replacement,
                                stackCount = group?.size ?: 1,
                                isCloudGroup = item.isCloudGroup && group != null
                            )
                        )
                    }
                }
            }
        }
        return out
    }

    val newMedia = media.mapNotNull { it.mutated() }
    val newPagerMedia = pagerMedia.mapNotNull { it.mutated() ?: promoted[it.id] }
    val newHeaders = headers.mapNotNull { header ->
        header.copy(data = header.data - pendingRemovalIds)
            .takeIf { it.data.isNotEmpty() }
    }
    val newCloudBackups = cloudBackups.mapNotNull { (localId, copies) ->
        if (localId in pendingRemovalIds) return@mapNotNull null
        val mutatedCopies = copies.mapNotNull { it.mutated() }
        if (mutatedCopies.isEmpty()) null else localId to mutatedCopies
    }.toMap()

    return copy(
        media = newMedia,
        pagerMedia = newPagerMedia,
        mediaGroups = newGroups,
        mappedMedia = mappedMedia.mutatedItems(),
        mappedMediaWithMonthly = mappedMediaWithMonthly.mutatedItems(),
        mappedMediaWithYearly = mappedMediaWithYearly.mutatedItems(),
        headers = newHeaders,
        cloudBackups = newCloudBackups
    )
}
