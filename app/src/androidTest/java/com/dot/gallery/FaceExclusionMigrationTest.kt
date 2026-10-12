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
import com.dot.gallery.feature_node.data.data_source.InternalDatabase_AutoMigration_49_50_Impl
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Covers the v49 -> v50 AutoMigration that creates the `face_exclusions` table —
 * the durable "this media does not contain this person" record used when removing
 * wrongly grouped photos from an on-device person.
 */
@RunWith(AndroidJUnit4::class)
class FaceExclusionMigrationTest {

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        InternalDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migrate49To50CreatesFaceExclusionsAndKeepsExistingFaces() {
        helper.createDatabase(TEST_DB, 49).use { db ->
            db.execSQL(
                """
                INSERT INTO people (
                    id, name, providerType, thumbnailMediaId, thumbnailUrl,
                    faceCount, lastUpdated, hidden
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """.trimIndent(),
                arrayOf<Any>("local_person", "Person", "LOCAL_PEOPLE", 7L, "thumb", 1, 13L, 0)
            )
            db.execSQL(
                """
                INSERT INTO detected_faces (
                    mediaId, personId, embedding, left, top, right, bottom,
                    confidence, timestamp, resultRevision
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """.trimIndent(),
                arrayOf<Any>(7L, "local_person", byteArrayOf(1, 2), 0.1, 0.1, 0.8, 0.8, 0.9, 100L, "face-v2:model")
            )
        }

        helper.runMigrationsAndValidate(
            TEST_DB,
            50,
            true,
            InternalDatabase_AutoMigration_49_50_Impl()
        ).use { db ->
            db.query(
                """
                SELECT COUNT(*) FROM sqlite_master
                WHERE type = 'table' AND name = 'face_exclusions'
                """.trimIndent()
            ).use { cursor ->
                cursor.moveToFirst()
                assertEquals(1, cursor.getInt(0))
            }
            // Existing person/face rows survive untouched.
            db.query("SELECT personId FROM detected_faces WHERE mediaId = 7").use { cursor ->
                cursor.moveToFirst()
                assertEquals("local_person", cursor.getString(0))
            }
            // The new table accepts rows and enforces the person FK.
            db.execSQL(
                "INSERT INTO face_exclusions (mediaId, personId, createdAt) VALUES (7, 'local_person', 1)"
            )
            db.query("SELECT mediaId, personId FROM face_exclusions").use { cursor ->
                cursor.moveToFirst()
                assertEquals(7L, cursor.getLong(0))
                assertEquals("local_person", cursor.getString(1))
            }
            db.query("PRAGMA foreign_key_check").use { cursor ->
                assertEquals(0, cursor.count)
            }
        }
    }

    private companion object {
        const val TEST_DB = "face-exclusion-migration-test"
    }
}
