package com.goreecloud.gallery

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GalleryRenderedAcceptanceTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(GalleryActivity::class.java)

    @Test
    fun primaryNavigationAndHeaderActionsAreRenderedAndTouchSized() {
        listOf(
            "Photos, selected",
            "Albums",
            "Videos",
            "Settings",
            "Search the current Gallery destination",
            "Change Gallery sort order",
        ).forEach { description ->
            onView(withContentDescription(description))
                .check(matches(isDisplayed()))
                .check(matches(isClickable()))
                .check(matches(hasMinimumTouchSizeDp(48f)))
        }
    }

    @Test
    fun destinationNavigationUpdatesRenderedSelectionState() {
        onView(withContentDescription("Albums"))
            .perform(click())
        onView(withContentDescription("Albums, selected"))
            .check(matches(isDisplayed()))
            .check(matches(hasMinimumTouchSizeDp(48f)))

        onView(withContentDescription("Settings"))
            .perform(click())
        onView(withContentDescription("Settings, selected"))
            .check(matches(isDisplayed()))
            .check(matches(hasMinimumTouchSizeDp(48f)))

        onView(withContentDescription("Photos"))
            .perform(click())
        onView(withContentDescription("Photos, selected"))
            .check(matches(isDisplayed()))
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
