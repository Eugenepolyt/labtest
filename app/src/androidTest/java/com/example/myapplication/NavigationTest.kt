package com.example.myapplication
import android.content.pm.ActivityInfo
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class NavigationTest {
    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)


    private fun dne(viewId: Int) {
        onView(withId(viewId)).check(doesNotExist())
    }

    private fun onScreen (viewId: Int) {
        onView(withId(viewId)).check(matches(isDisplayed()))
    }

    private fun clickById(viewId: Int) {
        onView(withId(viewId)).perform(click())
    }

    private fun up(){
        onView(
            withContentDescription(R.string.nav_app_bar_navigate_up_description)
        ).perform(click())
    }

    private fun changeOrientationToLand() {
        activityScenarioRule.scenario.onActivity { activity ->
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        sleep(500)
    }

    private fun changeOrientationToPort() {
        activityScenarioRule.scenario.onActivity { activity ->
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        sleep(500)
    }

    @Test
    fun testAbout() {
        launchActivity<MainActivity>()
        openAbout()
        onView(withId(R.id.activity_about))
            .check(matches(isDisplayed()))
    }

    // ---------------------------- Check backstack ----------------------------

    @Test
    fun backStackTestFirst() {
        onScreen(R.id.fragment1)
        openAbout()
        dne(R.id.fragment1)
        onScreen(R.id.activity_about)
        pressBackUnconditionally()
        onScreen(R.id.fragment1)
        dne(R.id.activity_about)
        pressBackUnconditionally()
        assertTrue(activityScenarioRule.scenario.state == Lifecycle.State.DESTROYED)
    }

    @Test
    fun backStackTestSecond() {
        onScreen(R.id.fragment1)
        openAbout()
        dne(R.id.fragment1)
        onScreen(R.id.activity_about)
        up()
        onScreen(R.id.fragment1)
        dne(R.id.activity_about)
        pressBackUnconditionally()
        assertTrue(activityScenarioRule.scenario.state == Lifecycle.State.DESTROYED)
    }

    @Test
    fun backStackTestThird() {
        clickById(R.id.bnToSecond)
        dne(R.id.fragment1)
        onScreen(R.id.fragment2)
        up()
        dne(R.id.fragment2)
        onScreen(R.id.fragment1)
        clickById(R.id.bnToSecond)
        pressBackUnconditionally()
        pressBackUnconditionally()
        assertTrue(activityScenarioRule.scenario.state == Lifecycle.State.DESTROYED)
    }

    @Test
    fun backStackTestFourthBack() {
        clickById(R.id.bnToSecond)
        clickById(R.id.bnToThird)
        dne(R.id.fragment1)
        dne(R.id.fragment2)
        onScreen(R.id.fragment3)
        pressBackUnconditionally()
        dne(R.id.fragment3)
        onScreen(R.id.fragment2)
        pressBackUnconditionally()
        dne(R.id.fragment2)
        onScreen(R.id.fragment1)
        pressBackUnconditionally()
        assertTrue(activityScenarioRule.scenario.state == Lifecycle.State.DESTROYED)
    }

    @Test
    fun backStackTestFourthUp() {
        clickById(R.id.bnToSecond)
        clickById(R.id.bnToThird)
        clickById(R.id.bnToSecond)
        up()
        onScreen(R.id.fragment1)
        dne(R.id.fragment2)
        dne(R.id.fragment3)
        clickById(R.id.bnToSecond)
        clickById(R.id.bnToThird)
        up()
        up()
        onScreen(R.id.fragment1)
        dne(R.id.fragment2)
        dne(R.id.fragment3)
    }

    @Test
    fun backStackTestFifth() {
        clickById(R.id.bnToSecond)
        openAbout()
        onScreen(R.id.activity_about)
        dne(R.id.fragment1)
        dne(R.id.fragment2)

        pressBackUnconditionally()
        up()
        onScreen(R.id.fragment1)
        dne(R.id.activity_about)
        dne(R.id.fragment2)

        clickById(R.id.bnToSecond)
        clickById(R.id.bnToThird)

        openAbout()

        onScreen(R.id.activity_about)
        pressBackUnconditionally()
        onScreen(R.id.fragment3)

        openAbout()

        up()
        onScreen(R.id.fragment3)
        dne(R.id.activity_about)
        up()
        onScreen(R.id.fragment2)

        openAbout()

        pressBackUnconditionally()
        onScreen(R.id.fragment2)
        dne(R.id.activity_about)
        pressBackUnconditionally()
        pressBackUnconditionally()

        assertTrue(activityScenarioRule.scenario.state == Lifecycle.State.DESTROYED)
    }

    // ---------------------------- Recreating ----------------------------

    @Test
    fun recreatingTestFirst() {
        onScreen(R.id.bnToSecond)
        onScreen(R.id.fragment1)
        onScreen(R.id.nav_view)

        changeOrientationToLand()

        onScreen(R.id.fragment1)
        onScreen(R.id.bnToSecond)
        onScreen(R.id.nav_view)

        openAbout()

        onScreen(R.id.activity_about)
        dne(R.id.nav_view)
        dne(R.id.fragment1)
        dne(R.id.bnToSecond)

        changeOrientationToPort()

        onScreen(R.id.activity_about)
        dne(R.id.nav_view)
        dne(R.id.fragment1)
        dne(R.id.bnToSecond)

        up()

        onScreen(R.id.fragment1)
        dne(R.id.activity_about)
    }

    @Test
    fun recreatingTestSecond() {
        clickById(R.id.bnToSecond)
        onScreen(R.id.nav_view)
        onScreen(R.id.fragment2)
        onScreen(R.id.bnToFirst)
        onScreen(R.id.bnToThird)

        changeOrientationToLand()

        dne(R.id.fragment1)
        dne(R.id.bnToSecond)
        onScreen(R.id.nav_view)
        onScreen(R.id.fragment2)
        onScreen(R.id.bnToFirst)
        onScreen(R.id.bnToThird)

        changeOrientationToPort()

        dne(R.id.fragment1)
        dne(R.id.bnToSecond)
        onScreen(R.id.nav_view)
        onScreen(R.id.fragment2)
        onScreen(R.id.bnToFirst)
        onScreen(R.id.bnToThird)

        changeOrientationToLand()
        clickById(R.id.bnToFirst)

        dne(R.id.fragment2)
        dne(R.id.bnToThird)
        dne(R.id.bnToFirst)
        onScreen(R.id.fragment1)
        onScreen(R.id.bnToSecond)

        pressBackUnconditionally()
        assertTrue(activityScenarioRule.scenario.state == Lifecycle.State.DESTROYED)

    }

    @Test
    fun recreatingTestThird() {
        clickById(R.id.bnToSecond)
        clickById(R.id.bnToThird)

        onScreen(R.id.nav_view)
        onScreen(R.id.bnToSecond)
        onScreen(R.id.bnToFirst)
        onScreen(R.id.fragment3)

        changeOrientationToLand()

        onScreen(R.id.nav_view)
        onScreen(R.id.bnToSecond)
        onScreen(R.id.bnToFirst)
        onScreen(R.id.fragment3)

        clickById(R.id.bnToFirst)

        onScreen(R.id.nav_view)
        onScreen(R.id.bnToSecond)
        dne(R.id.bnToFirst)
        dne(R.id.fragment3)

    }

}