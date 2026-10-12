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
import com.dot.gallery.feature_node.data.data_source.migration.MIGRATION_45_46
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CaptureTimeMigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        InternalDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migrate45To46AddsCaptureOriginAndIndexTable() {
        helper.createDatabase(TEST_DB, 45).close()

        helper.runMigrationsAndValidate(TEST_DB, 46, true, MIGRATION_45_46).use { database ->
            database.query("PRAGMA table_info(`media`)").use { cursor ->
                val nameIndex = cursor.getColumnIndexOrThrow("name")
                val columns = buildSet {
                    while (cursor.moveToNext()) add(cursor.getString(nameIndex))
                }
                assertTrue("captureTimeOrigin" in columns)
            }

            database.execSQL(
                """
                INSERT INTO media_capture_time (
                    mediaId, captureTimestampMillis, origin, sourceModifiedSeconds,
                    sourceTakenTimestampMillis, sourceSize, sourcePath, updatedAtMillis
                ) VALUES (7, 1000, 'embedded_image', 2, NULL, 3, '/photo.jpg', 4)
                """.trimIndent()
            )
            database.query(
                "SELECT captureTimestampMillis, origin FROM media_capture_time WHERE mediaId = 7"
            ).use { cursor ->
                cursor.moveToFirst()
                assertEquals(1000L, cursor.getLong(0))
                assertEquals("embedded_image", cursor.getString(1))
            }
        }
    }

    private companion object {
        const val TEST_DB = "capture-time-migration-test"
    }
}
