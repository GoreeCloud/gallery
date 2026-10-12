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
import com.dot.gallery.feature_node.data.data_source.InternalDatabase_AutoMigration_48_49_Impl
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Covers the v48 -> v49 AutoMigration that adds per-account upload destinations
 * (`cloud_server_config.uploadBasePath` / `uploadVideosPath`) and the per-album
 * override (`cloud_upload_pref.customPath`). Existing rows must keep their data
 * and receive "" defaults so the resolver falls back to prior behaviour.
 */
@RunWith(AndroidJUnit4::class)
class CloudUploadPathMigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        InternalDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migrate48To49AddsUploadPathColumnsWithEmptyDefaults() {
        helper.createDatabase(TEST_DB, 48).use { database ->
            database.execSQL(
                """
                INSERT INTO cloud_server_config
                    (id, providerType, serverUrl, displayName, isActive, lastConnected,
                     syncEnabled, wifiOnly, syncIntervalMinutes, syncFolders)
                VALUES (7, 'WEBDAV', 'https://dav.example', 'NAS', 1, 0, 1, 1, 360, '')
                """.trimIndent()
            )
            database.execSQL(
                """
                INSERT INTO cloud_upload_pref
                    (serverConfigId, albumId, providerType, albumLabel, uploadEnabled,
                     deleteLocalAfterUpload)
                VALUES (7, 10, 'WEBDAV', 'Camera', 1, 0)
                """.trimIndent()
            )
        }

        helper.runMigrationsAndValidate(
            TEST_DB,
            49,
            true,
            InternalDatabase_AutoMigration_48_49_Impl()
        ).use { database ->
            database.query(
                """
                SELECT displayName, uploadBasePath, uploadVideosPath
                FROM cloud_server_config WHERE id = 7
                """.trimIndent()
            ).use { cursor ->
                assertEquals(1, cursor.count)
                cursor.moveToFirst()
                assertEquals("NAS", cursor.getString(0))
                assertEquals("", cursor.getString(1))
                assertEquals("", cursor.getString(2))
            }
            database.query(
                """
                SELECT albumLabel, customPath
                FROM cloud_upload_pref WHERE serverConfigId = 7 AND albumId = 10
                """.trimIndent()
            ).use { cursor ->
                assertEquals(1, cursor.count)
                cursor.moveToFirst()
                assertEquals("Camera", cursor.getString(0))
                assertEquals("", cursor.getString(1))
            }
        }
    }

    private companion object {
        const val TEST_DB = "cloud-upload-path-migration-test"
    }
}
