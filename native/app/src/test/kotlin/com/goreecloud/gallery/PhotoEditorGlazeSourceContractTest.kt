package com.goreecloud.gallery

import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PhotoEditorGlazeSourceContractTest {
    private fun sourceText(): String {
        val candidates = listOf(
            Path.of("src/main/kotlin/com/goreecloud/gallery/PhotoEditorActivity.kt"),
            Path.of("native/app/src/main/kotlin/com/goreecloud/gallery/PhotoEditorActivity.kt"),
        )
        val source = candidates.firstOrNull { Files.exists(it) }
            ?: error("PhotoEditorActivity.kt source was not found from the native app or repository root")
        return source.toFile().readText()
    }

    @Test
    fun `photo editor consumes current semantic target spacing and shape roles`() {
        val source = sourceText()

        assertTrue(source.contains("GalleryGlazeContract.GENERAL_TARGET_DP"))
        assertTrue(source.contains("GalleryGlazeContract.SPACE_CONTROL_DP"))
        assertTrue(source.contains("GalleryGlazeContract.SPACE_COMPACT_CLUSTER_DP"))
        assertTrue(source.contains("GalleryGlazeContract.SPACE_HAIRLINE_DP"))
        assertTrue(source.contains("GalleryGlazeContract.SHAPE_ROUNDED_DP"))
        assertTrue(source.contains("GalleryGlazeContract.SHAPE_CONTROL_DP"))
    }

    @Test
    fun `photo editor does not restore superseded local target or hand selected control radii`() {
        val source = sourceText()

        assertFalse(source.contains("const val TARGET_DP"))
        assertFalse(source.contains("roundedSurface(0xe61a1a1d.toInt(), 22)"))
        assertFalse(source.contains("roundedSurface(0x2effffff, 16)"))
    }
}
