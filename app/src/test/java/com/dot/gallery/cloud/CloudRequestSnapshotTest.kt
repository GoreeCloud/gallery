/*
 * SPDX-FileCopyrightText: 2026 GoreeCloud
 * SPDX-License-Identifier: Apache-2.0
 */

package com.dot.gallery.cloud

import com.dot.gallery.cloud.sync.replaceCloudRequestSnapshot
import java.io.IOException
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.CopyOption
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CloudRequestSnapshotTest {
    @Test
    fun unsupportedAtomicMoveReplacesCompleteSnapshotWithoutPredelete() = withFiles { temporary, target ->
        val optionsSeen = mutableListOf<List<CopyOption>>()
        replaceCloudRequestSnapshot(temporary, target) { source, destination, options ->
            optionsSeen.add(options.toList())
            if (options.contains(StandardCopyOption.ATOMIC_MOVE)) {
                throw AtomicMoveNotSupportedException(source.toString(), destination.toString(), "injected")
            }
            // The last known-good record is present when replacement begins.
            assertEquals("old", Files.readString(destination))
            Files.move(source, destination, *options)
        }

        assertEquals("new", Files.readString(target))
        assertFalse(Files.exists(temporary))
        assertEquals(
            listOf(
                listOf(StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING),
                listOf(StandardCopyOption.REPLACE_EXISTING)
            ),
            optionsSeen
        )
    }

    @Test
    fun failedFallbackPreservesPreviousSnapshotAndPropagatesFailure() = withFiles { temporary, target ->
        var attempts = 0
        val failure = runCatching {
            replaceCloudRequestSnapshot(temporary, target) { source, destination, options ->
                attempts++
                if (options.contains(StandardCopyOption.ATOMIC_MOVE)) {
                    throw AtomicMoveNotSupportedException(source.toString(), destination.toString(), "injected")
                }
                assertEquals("old", Files.readString(destination))
                throw IOException("injected replacement failure")
            }
        }.exceptionOrNull()

        assertTrue(failure is IOException)
        assertEquals("injected replacement failure", failure?.message)
        assertEquals(2, attempts)
        assertEquals("old", Files.readString(target))
        assertEquals("new", Files.readString(temporary))
    }

    @Test
    fun unrelatedAtomicWriteFailureDoesNotTriggerFallbackOrEraseRecovery() = withFiles { temporary, target ->
        var attempts = 0
        val failure = runCatching {
            replaceCloudRequestSnapshot(temporary, target) { _, _, _ ->
                attempts++
                throw IOException("permission denied")
            }
        }.exceptionOrNull()

        assertTrue(failure is IOException)
        assertEquals("permission denied", failure?.message)
        assertEquals(1, attempts)
        assertEquals("old", Files.readString(target))
        assertEquals("new", Files.readString(temporary))
    }

    private fun withFiles(test: (Path, Path) -> Unit) {
        val directory = Files.createTempDirectory("gallery-cloud-request-")
        val temporary = directory.resolve("request.tmp")
        val target = directory.resolve("request.json")
        try {
            Files.writeString(temporary, "new")
            Files.writeString(target, "old")
            test(temporary, target)
        } finally {
            Files.deleteIfExists(temporary)
            Files.deleteIfExists(target)
            Files.deleteIfExists(directory)
        }
    }
}
