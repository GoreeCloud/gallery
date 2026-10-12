/*
 * SPDX-FileCopyrightText: 2023-2026 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package com.dot.gallery

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dot.gallery.feature_node.data.data_source.InternalDatabase
import com.dot.gallery.feature_node.data.data_source.migration.MIGRATION_46_47
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CloudTagMigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        InternalDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migrate46To47CreatesTagTables() {
        helper.createDatabase(TEST_DB, 46)

        helper.runMigrationsAndValidate(TEST_DB, 47, true, MIGRATION_46_47).use { database ->
            database.execSQL(
                """
                INSERT INTO cloud_tag
                    (serverConfigId, providerType, tagId, name, value, color, lastSyncedAt)
                VALUES (7, 'IMMICH', 'tag-1', 'Nature', 'Nature', '#12ab34', 100000)
                """.trimIndent()
            )
            database.execSQL(
                """
                INSERT INTO cloud_media_tag
                    (serverConfigId, providerType, tagId, remoteId)
                VALUES (7, 'IMMICH', 'tag-1', 'asset-1')
                """.trimIndent()
            )
            database.query("SELECT tagId, name, color FROM cloud_tag").use { cursor ->
                assertEquals(1, cursor.count)
                cursor.moveToFirst()
                assertEquals("tag-1", cursor.getString(0))
                assertEquals("Nature", cursor.getString(1))
                assertEquals("#12ab34", cursor.getString(2))
            }
            database.query(
                "SELECT remoteId FROM cloud_media_tag WHERE tagId = 'tag-1'"
            ).use { cursor ->
                assertEquals(1, cursor.count)
                cursor.moveToFirst()
                assertEquals("asset-1", cursor.getString(0))
            }
            // The (serverConfigId, tagId) primary key must actually reject duplicates.
            var duplicateRejected = false
            try {
                database.execSQL(
                    """
                    INSERT INTO cloud_tag
                        (serverConfigId, providerType, tagId, name, value, color, lastSyncedAt)
                    VALUES (7, 'IMMICH', 'tag-1', 'Dup', 'Dup', NULL, 100001)
                    """.trimIndent()
                )
            } catch (e: android.database.sqlite.SQLiteConstraintException) {
                duplicateRejected = true
            }
            assertTrue("cloud_tag primary key must reject duplicate (serverConfigId, tagId)", duplicateRejected)
        }
    }

    private companion object {
        const val TEST_DB = "cloud-tag-migration-test"
    }
}
