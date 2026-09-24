package com.goreecloud.gallery

import android.os.Build
import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
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
        assertNavigationControlsRespectSystemBarSafeArea()
        repeat(2) { round ->
            tapNavigationControl("Albums")
            assertSelectedNavigationState("Albums", "round ${round + 1}: Photos -> Albums")
            onView(allOf(withText("Albums"), isClickable()))
                .check(matches(isDisplayed()))
                .check(matches(hasMinimumTouchSizeDp(48f)))

            tapNavigationControl("Settings")
            assertSelectedNavigationState("Settings", "round ${round + 1}: Albums -> Settings")
            onView(allOf(withText("Settings"), isClickable()))
                .check(matches(isDisplayed()))
                .check(matches(hasMinimumTouchSizeDp(48f)))

            tapNavigationControl("Photos")
            assertSelectedNavigationState("Photos", "round ${round + 1}: Settings -> Photos")
            onView(allOf(withText("Photos"), isClickable()))
                .check(matches(isDisplayed()))
                .check(matches(hasMinimumTouchSizeDp(48f)))
        }
    }

    private fun assertNavigationControlsRespectSystemBarSafeArea() {
        activityRule.scenario.onActivity { activity ->
            val androidContent = activity.findViewById<ViewGroup>(android.R.id.content)
            val root = androidContent.getChildAt(0) as FrameLayout
            val capsule = (0 until root.childCount)
                .map(root::getChildAt)
                .filterIsInstance<LinearLayout>()
                .single { candidate ->
                    val labels = (0 until candidate.childCount).mapNotNull { index ->
                        (candidate.getChildAt(index) as? TextView)?.text?.toString()
                    }
                    labels.toSet() == setOf("Photos", "Albums", "Videos", "Settings")
                }

            val decor = activity.window.decorView
            val decorLocation = IntArray(2)
            decor.getLocationOnScreen(decorLocation)
            val insets = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                decor.rootWindowInsets?.getInsets(WindowInsets.Type.systemBars())
            } else {
                null
            }
            val safeTop = decorLocation[1] + (insets?.top ?: 0)
            val safeBottom = decorLocation[1] + decor.height - (insets?.bottom ?: 0)

            val controls = (0 until capsule.childCount)
                .map(capsule::getChildAt)
                .filterIsInstance<TextView>()
            val geometry = controls.joinToString(separator = " | ") { control ->
                val location = IntArray(2)
                control.getLocationOnScreen(location)
                val top = location[1]
                val bottom = top + control.height
                "${control.text}:top=$top,bottom=$bottom,height=${control.height}"
            }
            val allControlsSafe = controls.all { control ->
                val location = IntArray(2)
                control.getLocationOnScreen(location)
                val top = location[1]
                val bottom = top + control.height
                top >= safeTop && bottom <= safeBottom
            }

            assertTrue(
                "Expected navigation controls inside system-bar safe bounds " +
                    "[$safeTop,$safeBottom]. rootPadding=${root.paddingTop}/${root.paddingBottom}. " +
                    "Controls: $geometry",
                allControlsSafe,
            )
        }
    }
    private fun tapNavigationControl(label: String) {
        onView(
            allOf(
                withText(label),
                withContentDescription(label),
                isDisplayed(),
                isClickable(),
            )
        ).perform(click())
    }

    private fun assertSelectedNavigationState(expectedLabel: String, transition: String) {
        val deadline = SystemClock.uptimeMillis() + NAVIGATION_SETTLE_TIMEOUT_MILLIS
        var lastObservation = "navigation capsule not yet observed"

        while (true) {
            var settled = false
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

                if (capsules.size != 1) {
                    lastObservation = "expected one primary Gallery navigation capsule but found ${capsules.size}"
                    return@onActivity
                }

                val controls = (0 until capsules.single().childCount)
                    .map(capsules.single()::getChildAt)
                    .filterIsInstance<TextView>()
                lastObservation = controls.joinToString(separator = " | ") { control ->
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
                settled =
                    selected != null &&
                        selected.isSelected &&
                        selected.contentDescription?.toString() == "$expectedLabel, selected" &&
                        (Build.VERSION.SDK_INT < Build.VERSION_CODES.R ||
                            selected.stateDescription?.toString() == "Selected")
            }

            if (settled) return

            if (SystemClock.uptimeMillis() >= deadline) {
                assertTrue(
                    "Expected $expectedLabel selected navigation semantics after bounded UI settling " +
                        "($transition). Last observation: $lastObservation",
                    false,
                )
            }
            SystemClock.sleep(NAVIGATION_SETTLE_POLL_MILLIS)
        }
    }

    private fun assertRenderedTouchControl(description: String) {
        onView(withContentDescription(description))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
            .check(matches(hasMinimumTouchSizeDp(48f)))
    }

    private companion object {
        const val NAVIGATION_SETTLE_TIMEOUT_MILLIS = 2_000L
        const val NAVIGATION_SETTLE_POLL_MILLIS = 25L
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
