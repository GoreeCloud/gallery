package com.dot.gallery.cloud.sync

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class CloudAlbumCopyRequestPersistenceTest {
    @get:Rule val temporaryFolder = TemporaryFolder()

    @Test
    fun unsupportedAtomicMoveReplacesOnlyAfterSuccessfulFallback() {
        val target = fixture("request.json", "prior snapshot")
        val staging = fixture("request.tmp", "updated snapshot")
        var attempts = 0

        replaceCloudCopyRequestFile(staging, target) { from, to, options ->
            attempts++
            assertEquals("prior snapshot", text(to))
            if (attempts == 1) {
                assertTrue(options.contains(StandardCopyOption.ATOMIC_MOVE))
                throw AtomicMoveNotSupportedException(from.toString(), to.toString(), "test")
            }
            assertFalse(options.contains(StandardCopyOption.ATOMIC_MOVE))
            assertTrue(options.contains(StandardCopyOption.REPLACE_EXISTING))
            Files.move(from, to, *options)
        }

        assertEquals(2, attempts)
        assertEquals("updated snapshot", text(target))
        assertFalse(Files.exists(staging))
    }

    @Test
    fun failedFallbackPreservesBothOldSnapshotAndStagedUpdate() {
        val target = fixture("request.json", "prior snapshot")
        val staging = fixture("request.tmp", "updated snapshot")
        var attempts = 0

        try {
            replaceCloudCopyRequestFile(staging, target) { from, to, _ ->
                attempts++
                if (attempts == 1) {
                    throw AtomicMoveNotSupportedException(from.toString(), to.toString(), "test")
                }
                throw IOException("simulated fallback failure")
            }
            fail("Fallback failure must be surfaced")
        } catch (expected: IOException) {
            assertEquals("simulated fallback failure", expected.message)
        }

        assertEquals(2, attempts)
        assertEquals("prior snapshot", text(target))
        assertEquals("updated snapshot", text(staging))
    }

    @Test
    fun otherFilesystemErrorsDoNotTriggerAnUnsafeRetry() {
        val target = fixture("request.json", "prior snapshot")
        val staging = fixture("request.tmp", "updated snapshot")
        var attempts = 0

        try {
            replaceCloudCopyRequestFile(staging, target) { _, _, _ ->
                attempts++
                throw IOException("simulated permission failure")
            }
            fail("Unexpected filesystem error must fail closed")
        } catch (expected: IOException) {
            assertEquals("simulated permission failure", expected.message)
        }

        assertEquals(1, attempts)
        assertEquals("prior snapshot", text(target))
        assertEquals("updated snapshot", text(staging))
    }

    private fun fixture(name: String, contents: String) =
        temporaryFolder.newFile(name).toPath().also {
            Files.write(it, contents.toByteArray(StandardCharsets.UTF_8))
        }

    private fun text(path: java.nio.file.Path) =
        String(Files.readAllBytes(path), StandardCharsets.UTF_8)
}
