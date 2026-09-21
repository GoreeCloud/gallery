package com.goreecloud.gallery

import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.FrameLayout
import android.widget.ScrollView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GallerySecondarySurfacesRenderedAcceptanceTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(RecycleBinActivity::class.java)

    @Test
    fun recycleBinPersistentControlsUseGlazeAndRemainTouchSized() {
        listOf(
            "Back to GoreeCloud Gallery",
            "Refresh Recycle Bin",
        ).forEach { description ->
            onView(withContentDescription(description))
                .check(matches(isDisplayed()))
                .check(matches(isClickable()))
                .check(matches(hasMinimumTouchSizeDp(48f)))
                .check(matches(hasRefinementTag("control:$description")))
        }
    }

    @Test
    fun recycleBinContentStaysInsideSystemBarAndGestureSafeAreas() {
        activityRule.scenario.onActivity { activity ->
            val androidContent = activity.findViewById<ViewGroup>(android.R.id.content)
            val root = androidContent.getChildAt(0) as FrameLayout
            val insets = root.rootWindowInsets
            assertNotNull("Recycle Bin root must receive Android window insets", insets)

            val safe = currentSafeInsets(insets!!)
            val decorRect = Rect().also { activity.window.decorView.getGlobalVisibleRect(it) }
            val scroll = (0 until root.childCount)
                .map(root::getChildAt)
                .filterIsInstance<ScrollView>()
                .single()
            val scrollRect = Rect().also { scroll.getGlobalVisibleRect(it) }

            assertTrue(
                "Recycle Bin content must start below the status-bar/cutout safe edge",
                scrollRect.top >= decorRect.top + safe.top,
            )
            assertTrue(
                "Recycle Bin content must stay inside the physical left safe edge",
                scrollRect.left >= decorRect.left + safe.left,
            )
            assertTrue(
                "Recycle Bin content must stay inside the physical right safe edge",
                scrollRect.right <= decorRect.right - safe.right,
            )
            assertTrue(
                "Recycle Bin content must stay above the navigation/gesture safe edge",
                scrollRect.bottom <= decorRect.bottom - safe.bottom,
            )
        }
    }

    private fun currentSafeInsets(insets: WindowInsets): Rect {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val barsAndCutout = insets.getInsets(
                WindowInsets.Type.systemBars() or WindowInsets.Type.displayCutout(),
            )
            val gestures = insets.getInsets(WindowInsets.Type.mandatorySystemGestures())
            return Rect(
                maxOf(barsAndCutout.left, gestures.left),
                maxOf(barsAndCutout.top, gestures.top),
                maxOf(barsAndCutout.right, gestures.right),
                maxOf(barsAndCutout.bottom, gestures.bottom),
            )
        }

        @Suppress("DEPRECATION")
        val cutout = insets.displayCutout
        @Suppress("DEPRECATION")
        return Rect(
            maxOf(insets.systemWindowInsetLeft, cutout?.safeInsetLeft ?: 0),
            maxOf(insets.systemWindowInsetTop, cutout?.safeInsetTop ?: 0),
            maxOf(insets.systemWindowInsetRight, cutout?.safeInsetRight ?: 0),
            maxOf(insets.systemWindowInsetBottom, cutout?.safeInsetBottom ?: 0),
        )
    }

    private fun hasMinimumTouchSizeDp(minimumDp: Float) = object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description) {
            description.appendText("has rendered width and height of at least $minimumDp dp")
        }

        override fun matchesSafely(view: View): Boolean {
            val minimumPx = minimumDp * view.resources.displayMetrics.density
            return view.width >= minimumPx && view.height >= minimumPx
        }
    }

    private fun hasRefinementTag(expected: String) = object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description) {
            description.appendText("has Gallery Glaze refinement tag $expected")
        }

        override fun matchesSafely(view: View): Boolean =
            view.getTag(R.id.gallery_ui_refinement_tag) == expected
    }
}
