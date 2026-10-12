/*
 * SPDX-FileCopyrightText: 2023-2026 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package com.dot.gallery.cloud

import com.dot.gallery.cloud.core.CloudUri
import com.dot.gallery.cloud.core.ProviderRegistry
import com.dot.gallery.cloud.core.ProviderType
import com.dot.gallery.cloud.core.capabilities.RemoteMediaProvider
import com.dot.gallery.cloud.core.resolveRemote
import com.dot.gallery.cloud.image.firstAvailableVideoFrame
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertNull
import org.junit.Test
import java.lang.reflect.Proxy

/**
 * Pure-JVM tests for [CloudUri.parse] and [CloudUri.effectiveSize].
 *
 * These guard the `cloud://` grammar shared by every image/video pipeline resolver
 * (Sketch, Glide, ExoPlayer, ZoomImage). The manual-parse (rather than Uri.pathSegments)
 * exists specifically because path-based providers (WebDAV/ownCloud/Nextcloud/SMB/NFS)
 * carry slashes inside the remoteId; truncating it produced wrong stream URLs and colliding
 * cache keys — the root cause of the cloud-video-not-playing bug.
 */
class CloudUriTest {

    @Test
    fun parsesMinimalUriWithDefaults() {
        val uri = CloudUri.parse("cloud://IMMICH/abc-123")!!
        assertEquals(ProviderType.IMMICH, uri.providerType)
        assertEquals("abc-123", uri.remoteId)
        assertEquals("preview", uri.size) // default when no size param
        assertNull(uri.typeParam)
        assertNull(uri.fileId)
        assertEquals(-1L, uri.configId) // default when no cfg param
    }

    @Test
    fun parsesAllQueryParams() {
        val uri = CloudUri.parse(
            "cloud://IMMICH/asset-1?size=original&type=person&fileId=42&cfg=7"
        )!!
        assertEquals("original", uri.size)
        assertEquals("person", uri.typeParam)
        assertEquals("42", uri.fileId)
        assertEquals(7L, uri.configId)
    }

    @Test
    fun preservesSlashesInRemoteIdForPathProviders() {
        // WebDAV/SMB/NFS remote ids are full paths with slashes; they must survive intact.
        val uri = CloudUri.parse("cloud://WEBDAV/Photos/2024/Trip/IMG_0001.jpg?size=preview&cfg=3")!!
        assertEquals(ProviderType.WEBDAV, uri.providerType)
        assertEquals("Photos/2024/Trip/IMG_0001.jpg", uri.remoteId)
        assertEquals("preview", uri.size)
        assertEquals(3L, uri.configId)
    }

    @Test
    fun remoteIdWithSlashesAndNoQuery() {
        val uri = CloudUri.parse("cloud://SMB/gallery/videos/clip.mp4")!!
        assertEquals("gallery/videos/clip.mp4", uri.remoteId)
        assertEquals("preview", uri.size)
    }

    @Test
    fun rejectsNonCloudScheme() {
        assertNull(CloudUri.parse("https://example.com/x"))
        assertNull(CloudUri.parse("content://media/external/images/1"))
    }

    @Test
    fun rejectsUnknownProviderType() {
        assertNull(CloudUri.parse("cloud://NOTAPROVIDER/x"))
    }

    @Test
    fun rejectsMissingRemoteId() {
        assertNull(CloudUri.parse("cloud://IMMICH/"))
        assertNull(CloudUri.parse("cloud://IMMICH/?size=preview"))
    }

    @Test
    fun rejectsMissingAuthoritySeparator() {
        assertNull(CloudUri.parse("cloud://IMMICH"))
    }

    @Test
    fun invalidExplicitAccountIdsFailClosed() {
        assertNull(CloudUri.parse("cloud://IMMICH/x?cfg=notanumber"))
        assertNull(CloudUri.parse("cloud://IMMICH/x?cfg=0"))
        assertNull(CloudUri.parse("cloud://IMMICH/x?cfg=-9"))
        assertNull(CloudUri.parse("cloud://IMMICH/x?cfg="))
        assertNull(CloudUri.parse("cloud://IMMICH/x?cfg=9223372036854775808"))
        assertEquals(-1L, CloudUri.parse("cloud://IMMICH/x")!!.configId)
    }

    @Test
    fun rejectsAmbiguousOrIncompleteAccountQueryFields() {
        assertNull(CloudUri.parse("cloud://IMMICH/x?cfg"))
        assertNull(CloudUri.parse("cloud://IMMICH/x?cfg&size=preview"))
        assertNull(CloudUri.parse("cloud://IMMICH/x?cfg=7&cfg=8"))
        assertNull(CloudUri.parse("cloud://IMMICH/x?cfg=7&cfg=7"))
        assertNull(CloudUri.parse("cloud://IMMICH/x?CFG=7"))
        assertNull(CloudUri.parse("cloud://IMMICH/x?cfg=7&Cfg=9"))
        assertEquals(7L, CloudUri.parse("cloud://IMMICH/x?size=preview&cfg=7")!!.configId)
    }

    @Test
    fun explicitCloudAccountIsNeverResolvedViaAnotherAccount() {
        val registry = ProviderRegistry()
        val first = remoteStub(ProviderType.IMMICH)
        val second = remoteStub(ProviderType.IMMICH)
        registry._providers[11L] = first
        registry._providers[22L] = second

        assertSame(second, registry.resolveRemote(ProviderType.IMMICH, 22L))
        assertNull(registry.resolveRemote(ProviderType.IMMICH, 33L))
        assertNull(registry.resolveRemote(ProviderType.WEBDAV, 11L))
    }

    @Test
    fun legacyCloudUriFallsBackOnlyForExactlyOneMatchingAccount() {
        val registry = ProviderRegistry()
        val first = remoteStub(ProviderType.IMMICH)
        registry._providers[11L] = first
        assertSame(first, registry.resolveRemote(ProviderType.IMMICH, -1L))
        assertNull(registry.resolveRemote(ProviderType.IMMICH, 0L))
        assertNull(registry.resolveRemote(ProviderType.IMMICH, -2L))
        registry._providers[22L] = remoteStub(ProviderType.IMMICH)
        assertNull(registry.resolveRemote(ProviderType.IMMICH, -1L))
        assertNull(registry.resolveRemote(ProviderType.SMB, -1L))
    }

    private fun remoteStub(type: ProviderType): RemoteMediaProvider =
        Proxy.newProxyInstance(
            RemoteMediaProvider::class.java.classLoader,
            arrayOf(RemoteMediaProvider::class.java)
        ) { _, method, _ ->
            when (method.name) {
                "getProviderType" -> type
                else -> throw UnsupportedOperationException(method.name)
            }
        } as RemoteMediaProvider

    @Test
    fun effectiveSizeDowngradesPreviewForGridCells() {
        val uri = CloudUri.parse("cloud://SMB/gallery/img.jpg?size=preview")!!
        // A small (grid) target downgrades to the cheap thumbnail.
        assertEquals("thumbnail", uri.effectiveSize(300))
        assertEquals("thumbnail", uri.effectiveSize(CloudUri.GRID_THUMBNAIL_MAX_PX))
    }

    @Test
    fun effectiveSizeKeepsPreviewForLargeTargets() {
        val uri = CloudUri.parse("cloud://SMB/gallery/img.jpg?size=preview")!!
        assertEquals("preview", uri.effectiveSize(CloudUri.GRID_THUMBNAIL_MAX_PX + 1))
        assertEquals("preview", uri.effectiveSize(2000))
    }

    @Test
    fun effectiveSizeNeverDowngradesOriginal() {
        val uri = CloudUri.parse("cloud://IMMICH/x?size=original")!!
        assertEquals("original", uri.effectiveSize(100))
    }

    @Test
    fun effectiveSizeNeverDowngradesPersonThumbnails() {
        val uri = CloudUri.parse("cloud://IMMICH/face-1?size=preview&type=person")!!
        assertEquals("preview", uri.effectiveSize(100))
    }

    @Test
    fun effectiveSizeIgnoresNonPositiveTargets() {
        val uri = CloudUri.parse("cloud://SMB/gallery/img.jpg?size=preview")!!
        assertEquals("preview", uri.effectiveSize(0))
        assertEquals("preview", uri.effectiveSize(-1))
    }

    @Test
    fun videoFrameFallsBackFromRepresentativeAndStartToLaterTimestamps() {
        val requestedTimes = mutableListOf<Long>()

        val frame = firstAvailableVideoFrame(durationMillis = 10_000L) { timeUs ->
            requestedTimes += timeUs
            "frame".takeIf { timeUs == 5_000_000L }
        }

        assertEquals("frame", frame)
        assertEquals(listOf(-1L, 0L, 1_000_000L, 5_000_000L), requestedTimes)
    }
}
