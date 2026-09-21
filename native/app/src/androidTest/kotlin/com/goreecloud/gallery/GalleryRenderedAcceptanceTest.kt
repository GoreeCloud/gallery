package com.goreecloud.gallery

import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.Visibility.GONE
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GalleryRenderedAcceptanceTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(GalleryActivity::class.java)

    @Test
    fun photosNavigationControlIsRenderedAndTouchSized() {
        assertRenderedTouchControl("Photos, selected")
    }

    @Test
    fun albumsNavigationControlIsRenderedAndTouchSized() {
        assertRenderedTouchControl("Albums")
    }

    @Test
    fun videosNavigationControlIsRenderedAndTouchSized() {
        assertRenderedTouchControl("Videos")
    }

    @Test
    fun settingsNavigationControlIsRenderedAndTouchSized() {
        assertRenderedTouchControl("Settings")
    }

    @Test
    fun mediaOnlyHeaderControlsStayHiddenWithoutReadableMedia() {
        onView(withContentDescription("Search the current Gallery destination"))
            .check(matches(withEffectiveVisibility(GONE)))
        onView(withContentDescription(containsString("Sort order:")))
            .check(matches(withEffectiveVisibility(GONE)))
    }

    @Test
    fun destinationNavigationUpdatesRenderedSelectionState() {
        repeat(2) {
            onView(withContentDescription("Albums"))
                .perform(click())
            assertSelectedNavigationState("Albums")
            onView(allOf(withText("Albums"), isClickable()))
                .check(matches(isDisplayed()))
                .check(matches(hasMinimumTouchSizeDp(48f)))

            onView(withContentDescription("Settings"))
                .perform(click())
            assertSelectedNavigationState("Settings")
            onView(allOf(withText("Settings"), isClickable()))
                .check(matches(isDisplayed()))
                .check(matches(hasMinimumTouchSizeDp(48f)))

            onView(withContentDescription("Photos"))
                .perform(click())
            assertSelectedNavigationState("Photos")
            onView(allOf(withText("Photos"), isClickable()))
                .check(matches(isDisplayed()))
                .check(matches(hasMinimumTouchSizeDp(48f)))
        }
    }

    private fun assertSelectedNavigationState(expectedLabel: String) {
        activityRule.scenario.onActivity { activity ->
            val androidContent = activity.findViewById<ViewGroup>(android.R.id.content)
            val root = androidContent.getChildAt(0) as FrameLayout
            val capsules = (0 until root.childCount)
                .map(root::getChildAt)
                .filterIsInstance<LinearLayout>()
                .filter { candidate ->
                    val labels = (0 until candidate.childCount).mapNotNull { index ->
                        (candidate.getChildAt(index) as? TextView)?.text?.toString()
                    }
                    labels.toSet() == setOf("Photos", "Albums", "Videos", "Settings")
                }

            assertTrue(
                "Expected one primary Gallery navigation capsule but found ${capsules.size}",
                capsules.size == 1,
            )

            val controls = (0 until capsules.single().childCount)
                .map(capsules.single()::getChildAt)
                .filterIsInstance<TextView>()
            val actual = controls.joinToString(separator = " | ") { control ->
                buildString {
                    append(control.text)
                    append(":selected=")
                    append(control.isSelected)
                    append(",contentDescription=")
                    append(control.contentDescription)
                    append(",stateDescription=")
                    append(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) control.stateDescription else "n/a")
                    append(",visibility=")
                    append(control.visibility)
                    append(",attached=")
                    append(control.isAttachedToWindow)
                }
            }
            val selected = controls.singleOrNull { it.text?.toString() == expectedLabel }
            assertTrue(
                "Expected $expectedLabel selected navigation semantics. Actual controls: $actual",
                selected != null &&
                    selected.isSelected &&
                    selected.contentDescription?.toString() == "$expectedLabel, selected" &&
                    (Build.VERSION.SDK_INT < Build.VERSION_CODES.R ||
                        selected.stateDescription?.toString() == "Selected"),
            )
        }
    }

    private fun assertRenderedTouchControl(description: String) {
        onView(withContentDescription(description))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
            .check(matches(hasMinimumTouchSizeDp(48f)))
    }

    private fun hasMinimumTouchSizeDp(minimumDp: Float) = object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description) {
            description.appendText("has rendered width and height of at least $minimumDp dp")
        }

        override fun matchesSafely(view: View): Boolean {
            val minimumPx = minimumDp * view.resources.displayMetrics.density
            return view.width >= minimumPx && view.height >= minimumPx
        }

        override fun describeMismatchSafely(view: View, mismatchDescription: Description) {
            val density = view.resources.displayMetrics.density
            val widthDp = view.width / density
            val heightDp = view.height / density
            mismatchDescription.appendText("rendered ${widthDp}dp x ${heightDp}dp")
        }
    }
}
