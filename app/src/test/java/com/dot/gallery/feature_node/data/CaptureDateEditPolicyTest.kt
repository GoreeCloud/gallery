/*
 * SPDX-FileCopyrightText: 2023-2026 IacobIacob01
 * SPDX-License-Identifier: Apache-2.0
 */

package com.dot.gallery.feature_node.data.repository

import com.dot.gallery.feature_node.domain.repository.CaptureDateEditCapability
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CaptureDateEditPolicyTest {

    @Test
    fun writableMetadata_editsOriginal() {
        assertEquals(
            CaptureDateEditCapability.DIRECT_WRITE,
            captureDateEditCapability(true, true, "image/jpeg")
        )
    }

    @Test
    fun bitmapContainer_allowsSafeCopyAndTrashOffer() {
        val capability = captureDateEditCapability(true, false, "image/bmp")

        assertEquals(CaptureDateEditCapability.SAFE_COPY, capability)
        assertTrue(canTrashOriginalAfterDatedCopy(capability))
    }

    @Test
    fun rawOrAnimatedContainer_neverOffersTrashAfterFlattening() {
        listOf("image/x-adobe-dng", "image/gif", "image/heic-sequence").forEach { mime ->
            val capability = captureDateEditCapability(true, false, mime)

            assertEquals(CaptureDateEditCapability.COPY_ONLY, capability)
            assertFalse(canTrashOriginalAfterDatedCopy(capability))
        }
    }

    @Test
    fun nonLocalOrNonImageMedia_isUnsupported() {
        assertEquals(
            CaptureDateEditCapability.UNSUPPORTED,
            captureDateEditCapability(false, true, "video/mp4")
        )
    }
}
