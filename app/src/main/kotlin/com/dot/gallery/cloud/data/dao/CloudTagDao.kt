/*
 * SPDX-FileCopyrightText: 2023-2026 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package com.dot.gallery.cloud.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.dot.gallery.cloud.data.entity.CloudMediaTagEntity
import com.dot.gallery.cloud.data.entity.CloudTagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CloudTagDao {

    @Query("SELECT * FROM cloud_tag WHERE serverConfigId = :configId")
    fun getTagsForAccount(configId: Long): Flow<List<CloudTagEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTags(tags: List<CloudTagEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLinks(links: List<CloudMediaTagEntity>)

    @Query("DELETE FROM cloud_tag WHERE serverConfigId = :configId")
    suspend fun deleteTagsForAccount(configId: Long)

    @Query("DELETE FROM cloud_media_tag WHERE serverConfigId = :configId")
    suspend fun deleteLinksForAccount(configId: Long)

    @Transaction
    suspend fun replaceForAccount(
        configId: Long,
        tags: List<CloudTagEntity>,
        links: List<CloudMediaTagEntity>
    ) {
        deleteLinksForAccount(configId)
        deleteTagsForAccount(configId)
        upsertTags(tags)
        upsertLinks(links)
    }

    @Transaction
    suspend fun deleteForAccount(configId: Long) {
        deleteLinksForAccount(configId)
        deleteTagsForAccount(configId)
    }

    /**
     * Tag-link rows whose tag name or value contains [query]. SQLite LIKE is
     * case-insensitive for ASCII, matching how metadata search treats tag text.
     */
    @Query(
        "SELECT DISTINCT m.* FROM cloud_media_tag m " +
            "INNER JOIN cloud_tag t " +
            "ON t.serverConfigId = m.serverConfigId AND t.tagId = m.tagId " +
            "WHERE t.name LIKE '%' || :query || '%' OR t.value LIKE '%' || :query || '%'"
    )
    suspend fun findLinksForTagQuery(query: String): List<CloudMediaTagEntity>
}
