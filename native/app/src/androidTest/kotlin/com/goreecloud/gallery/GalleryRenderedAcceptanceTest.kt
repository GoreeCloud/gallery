package com.goreecloud.gallery

import android.graphics.Rect
import android.os.Build
import android.os.SystemClock
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.Visibility.GONE
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GalleryRenderedAcceptanceTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(GalleryActivity::class.java)

    @Test
    fun primaryNavigationAndPermissionSurfaceAreRenderedAndTouchSized() {
        listOf(
            "Photos, selected",
            "Albums",
            "Videos",
            "Settings",
        ).forEach { description ->
            onView(withContentDescription(description))
                .check(matches(isDisplayed()))
                .check(matches(isClickable()))
                .check(matches(hasMinimumTouchSizeDp(48f)))
                .check(matches(hasTopCompoundDrawable()))
        }

        onView(withContentDescription("Photos, selected"))
            .check(matches(hasSelectedStateDescription()))

        onView(withContentDescription("Gallery media access action"))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
            .check(matches(hasMinimumTouchSizeDp(48f)))
    }

    @Test
    fun selectedNavigationSurfaceIsContainedByOuterCapsule() {
        activityRule.scenario.onActivity { activity ->
            val androidContent = activity.findViewById<ViewGroup>(android.R.id.content)
            val root = androidContent.getChildAt(0) as FrameLayout
            val capsule = (0 until root.childCount)
                .map(root::getChildAt)
                .filterIsInstance<LinearLayout>()
                .first { candidate ->
                    val labels = (0 until candidate.childCount).mapNotNull { index ->
                        (candidate.getChildAt(index) as? TextView)?.text?.toString()
                    }
                    labels.toSet() == setOf("Photos", "Albums", "Videos", "Settings")
                }

            assertTrue("Navigation capsule must clip child material to its rounded outline", capsule.clipToOutline)

            val selected = (0 until capsule.childCount)
                .map(capsule::getChildAt)
                .filterIsInstance<TextView>()
                .single { it.isSelected }
            val capsuleRect = Rect().also { capsule.getGlobalVisibleRect(it) }
            val selectedRect = Rect().also { selected.getGlobalVisibleRect(it) }

            assertTrue(
                "Selected navigation material must remain inside the outer capsule bounds",
                selectedRect.left >= capsuleRect.left &&
                    selectedRect.top >= capsuleRect.top &&
                    selectedRect.right <= capsuleRect.right &&
                    selectedRect.bottom <= capsuleRect.bottom,
            )
        }
    }

    @Test
    fun renderedTapPathUpdatesSelectionState() {
        assertNavigationControlsRespectSystemBarSafeArea()
        tapNavigationControlWithEspresso("Albums")
        assertSelectedNavigationState("Albums", "rendered Espresso tap: Photos -> Albums")
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
            activateNavigationControl("Albums")
            assertSelectedNavigationState("Albums", "round ${round + 1}: Photos -> Albums")
            onView(selectedNavigationLabel("Albums"))
                .check(matches(isDisplayed()))
                .check(matches(hasMinimumTouchSizeDp(48f)))
                .check(matches(hasTopCompoundDrawable()))
                .check(matches(hasSelectedStateDescription()))

            activateNavigationControl("Settings")
            assertSelectedNavigationState("Settings", "round ${round + 1}: Albums -> Settings")
            onView(selectedNavigationLabel("Settings"))
                .check(matches(isDisplayed()))
                .check(matches(hasMinimumTouchSizeDp(48f)))
                .check(matches(hasTopCompoundDrawable()))
                .check(matches(hasSelectedStateDescription()))

            activateNavigationControl("Photos")
            assertSelectedNavigationState("Photos", "round ${round + 1}: Settings -> Photos")
            onView(selectedNavigationLabel("Photos"))
                .check(matches(isDisplayed()))
                .check(matches(hasMinimumTouchSizeDp(48f)))
                .check(matches(hasTopCompoundDrawable()))
                .check(matches(hasSelectedStateDescription()))
        }
    }

    @Test
    fun mainGalleryChromeStaysInsideSystemBarAndGestureSafeAreas() {
        activityRule.scenario.onActivity { activity ->
            val androidContent = activity.findViewById<ViewGroup>(android.R.id.content)
            val root = androidContent.getChildAt(0) as FrameLayout
            val insets = root.rootWindowInsets
            assertNotNull("Gallery root must receive Android window insets", insets)

            val safe = currentSafeInsets(insets!!)
            val decor = activity.window.decorView
            val decorRect = Rect().also { decor.getGlobalVisibleRect(it) }

            val libraryScroll = (0 until root.childCount)
                .map(root::getChildAt)
                .filterIsInstance<ScrollView>()
                .single()
            val scrollRect = Rect().also { libraryScroll.getGlobalVisibleRect(it) }

            assertTrue(
                "Gallery content must start below the status-bar/cutout safe edge",
                scrollRect.top >= decorRect.top + safe.top,
            )
            assertTrue(
                "Gallery content must stay inside the physical left safe edge",
                scrollRect.left >= decorRect.left + safe.left,
            )
            assertTrue(
                "Gallery content must stay inside the physical right safe edge",
                scrollRect.right <= decorRect.right - safe.right,
            )

            val visibleBottomChrome = (0 until root.childCount)
                .map(root::getChildAt)
                .filter { child ->
                    val params = child.layoutParams as? FrameLayout.LayoutParams ?: return@filter false
                    child.visibility == View.VISIBLE &&
                        child is LinearLayout &&
                        params.gravity != -1 &&
                        (params.gravity and Gravity.VERTICAL_GRAVITY_MASK) == Gravity.BOTTOM
                }

            assertTrue("Gallery must render visible bottom Glaze chrome", visibleBottomChrome.isNotEmpty())
            visibleBottomChrome.forEach { chrome ->
                val chromeRect = Rect().also { chrome.getGlobalVisibleRect(it) }
                assertTrue(
                    "Bottom Glaze chrome must remain above the navigation/gesture safe edge",
                    chromeRect.bottom <= decorRect.bottom - safe.bottom,
                )
                assertTrue(
                    "Bottom Glaze chrome must stay inside the physical left safe edge",
                    chromeRect.left >= decorRect.left + safe.left,
                )
                assertTrue(
                    "Bottom Glaze chrome must stay inside the physical right safe edge",
                    chromeRect.right <= decorRect.right - safe.right,
                )
            }
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

    private fun tapNavigationControlWithEspresso(label: String) {
        onView(
            allOf(
                withText(label),
                withContentDescription(label),
                isDisplayed(),
                isClickable(),
            ),
        ).perform(click())
    }

    private fun activateNavigationControl(label: String) {
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

            val control = (0 until capsules.single().childCount)
                .map(capsules.single()::getChildAt)
                .filterIsInstance<TextView>()
                .singleOrNull { it.text?.toString() == label }

            assertTrue(
                "Expected visible clickable $label navigation control",
                control != null && control.isShown && control.isClickable,
            )
            assertTrue(
                "Expected $label navigation listener to accept one activation",
                control!!.performClick(),
            )
        }
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

    private fun currentSafeInsets(insets: WindowInsets): Rect {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val resolved = insets.getInsets(
                WindowInsets.Type.systemBars() or WindowInsets.Type.displayCutout(),
            )
            return Rect(resolved.left, resolved.top, resolved.right, resolved.bottom)
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

    private fun selectedNavigationLabel(expectedLabel: String) = object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description) {
            description.appendText(
                "is the selected Gallery navigation label $expectedLabel with non-color selected semantics",
            )
        }

        override fun matchesSafely(view: View): Boolean {
            if (view !is TextView || view.text?.toString() != expectedLabel || !view.isSelected) return false
            return view.contentDescription?.toString() == "$expectedLabel, selected"
        }
    }

    private fun hasSelectedStateDescription() = object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description) {
            description.appendText("exposes Android selected state description")
        }

        override fun matchesSafely(view: View): Boolean =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.R ||
                view.stateDescription?.toString() == "Selected"
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

    private fun hasTopCompoundDrawable() = object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description) {
            description.appendText("is a Gallery navigation label with a rendered top icon")
        }

        override fun matchesSafely(view: View): Boolean =
            view is TextView && view.compoundDrawables[1] != null
    }
}
