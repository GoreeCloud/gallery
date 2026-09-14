package com.goreecloud.gallery.android

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AndroidMediaMoveRequestsTest {
    @Test
    fun `relative path normalization preserves a bounded MediaStore folder`() {
        assertEquals("DCIM/Camera/", AndroidMediaMoveRequests.normalizeRelativePath("  DCIM/Camera  "))
        assertEquals("Pictures/Screenshots/", AndroidMediaMoveRequests.normalizeRelativePath("Pictures//Screenshots/"))
    }

    @Test
    fun `relative path normalization rejects absolute traversal and unsupported paths`() {
        assertFailsWith<IllegalArgumentException> { AndroidMediaMoveRequests.normalizeRelativePath("") }
        assertFailsWith<IllegalArgumentException> { AndroidMediaMoveRequests.normalizeRelativePath("/DCIM/Camera") }
        assertFailsWith<IllegalArgumentException> { AndroidMediaMoveRequests.normalizeRelativePath("DCIM/../Secrets") }
        assertFailsWith<IllegalArgumentException> { AndroidMediaMoveRequests.normalizeRelativePath("DCIM\\Camera") }
        assertFailsWith<IllegalArgumentException> { AndroidMediaMoveRequests.normalizeRelativePath("DCIM/\u0000Camera") }
    }

    @Test
    fun `move support begins at Android 11`() {
        assertEquals(false, AndroidMediaMoveRequests.isSupported(29))
        assertEquals(true, AndroidMediaMoveRequests.isSupported(30))
        assertEquals(true, AndroidMediaMoveRequests.isSupported(36))
    }
}
