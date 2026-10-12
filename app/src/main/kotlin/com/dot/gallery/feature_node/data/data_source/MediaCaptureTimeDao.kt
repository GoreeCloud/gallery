/*
 * SPDX-FileCopyrightText: 2023-2026 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package com.dot.gallery.feature_node.data.data_source

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert
import com.dot.gallery.feature_node.domain.model.CaptureTimeOrigin
import com.dot.gallery.feature_node.domain.model.Media
import com.dot.gallery.feature_node.domain.model.ResolvedCaptureTime
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = "media_capture_time",
    indices = [Index(value = ["origin"])]
)
data class MediaCaptureTimeEntity(
    @PrimaryKey val mediaId: Long,
    val captureTimestampMillis: Long,
    val origin: CaptureTimeOrigin,
    val sourceModifiedSeconds: Long,
    val sourceTakenTimestampMillis: Long?,
    val sourceSize: Long,
    val sourcePath: String,
    val updatedAtMillis: Long
) {
    fun isFreshFor(media: Media.UriMedia): Boolean =
        sourceModifiedSeconds == media.timestamp &&
            sourceTakenTimestampMillis == media.takenTimestamp &&
            sourceSize == media.size &&
            sourcePath == media.path
}

fun MediaCaptureTimeEntity.applyTo(media: Media.UriMedia): Media.UriMedia {
    if (!isFreshFor(media)) return media
    val takenTimestamp = captureTimestampMillis.takeUnless {
        origin == CaptureTimeOrigin.MODIFIED_FALLBACK
    }
    return media.copy(
        takenTimestamp = takenTimestamp,
        captureTimeOrigin = origin
    )
}

fun ResolvedCaptureTime.toEntity(
    media: Media.UriMedia,
    updatedAtMillis: Long
): MediaCaptureTimeEntity = MediaCaptureTimeEntity(
    mediaId = media.id,
    captureTimestampMillis = timestampMillis,
    origin = origin,
    sourceModifiedSeconds = media.timestamp,
    sourceTakenTimestampMillis = media.takenTimestamp,
    sourceSize = media.size,
    sourcePath = media.path,
    updatedAtMillis = updatedAtMillis
)

@Dao
interface MediaCaptureTimeDao {
    @Query("SELECT * FROM media_capture_time")
    fun observeAll(): Flow<List<MediaCaptureTimeEntity>>

    @Query("SELECT * FROM media_capture_time")
    suspend fun getAll(): List<MediaCaptureTimeEntity>

    @Upsert
    suspend fun upsertAll(entries: List<MediaCaptureTimeEntity>)

    @Query("DELETE FROM media_capture_time WHERE mediaId IN (:mediaIds)")
    suspend fun deleteByIds(mediaIds: List<Long>)

    @Query("DELETE FROM media_capture_time")
    suspend fun deleteAll()
}
