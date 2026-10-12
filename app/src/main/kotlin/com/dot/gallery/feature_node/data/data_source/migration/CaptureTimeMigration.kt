/*
 * SPDX-FileCopyrightText: 2023-2026 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package com.dot.gallery.feature_node.data.data_source.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_45_46 = object : Migration(45, 46) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE `media` ADD COLUMN `captureTimeOrigin` TEXT NOT NULL " +
                "DEFAULT 'modified_fallback'"
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `media_capture_time` (
                `mediaId` INTEGER NOT NULL,
                `captureTimestampMillis` INTEGER NOT NULL,
                `origin` TEXT NOT NULL,
                `sourceModifiedSeconds` INTEGER NOT NULL,
                `sourceTakenTimestampMillis` INTEGER,
                `sourceSize` INTEGER NOT NULL,
                `sourcePath` TEXT NOT NULL,
                `updatedAtMillis` INTEGER NOT NULL,
                PRIMARY KEY(`mediaId`)
            )
            """.trimIndent()
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_media_capture_time_origin` " +
                "ON `media_capture_time` (`origin`)"
        )
    }
}
