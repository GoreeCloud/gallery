package com.goreecloud.gallery

import android.graphics.Bitmap
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GalleryRenderedScreenshotCaptureTest {
    @Test
    fun captureRecycleBinOnlyWhenExplicitlyRequested() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val arguments = InstrumentationRegistry.getArguments()

        assumeTrue(arguments.getString("galleryRenderedCapture") == "recycle-bin")

        val fileName = arguments.getString("galleryScreenshotFile")
            ?: error("galleryScreenshotFile instrumentation argument is required")
        require(fileName.matches(Regex("[a-z0-9-]+\\.png"))) {
            "galleryScreenshotFile must be a bounded PNG file name"
        }

        ActivityScenario.launch(RecycleBinActivity::class.java).use {
            instrumentation.waitForIdleSync()
            Thread.sleep(500)

            val screenshot = instrumentation.uiAutomation.takeScreenshot()
            val output = File(instrumentation.targetContext.filesDir, fileName)
            output.outputStream().use { stream ->
                assertTrue(
                    "Rendered screenshot must encode as PNG",
                    screenshot.compress(Bitmap.CompressFormat.PNG, 100, stream),
                )
            }
            assertTrue("Rendered screenshot must not be empty", output.length() > 0L)
        }
    }
}
