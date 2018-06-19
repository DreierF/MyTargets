package de.dreier.mytargets.screengrab

import android.os.SystemClock
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.pressBack
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.filters.SdkSuppress
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import android.view.View
import de.dreier.mytargets.R
import de.dreier.mytargets.features.main.MainActivity
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.targets.models.WAFull
import de.dreier.mytargets.test.base.UITestBase
import de.dreier.mytargets.test.utils.actions.TargetViewActions
import de.dreier.mytargets.test.utils.matchers.ViewMatcher
import de.dreier.mytargets.test.utils.rules.SimpleDbTestRule
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import tools.fastlane.screengrab.Screengrab
import tools.fastlane.screengrab.UiAutomatorScreenshotStrategy
import tools.fastlane.screengrab.locale.LocaleTestRule

@SdkSuppress(minSdkVersion = 18)
@LargeTest
@RunWith(AndroidJUnit4::class)
class ScreenshotTest : UITestBase() {

    private val activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Rule
    @JvmField
    val rule = RuleChain.outerRule(SimpleDbTestRule()).around(activityTestRule)

    @Before
    fun setUp() {
        Screengrab.setDefaultScreenshotStrategy(UiAutomatorScreenshotStrategy())
        SettingsManager.arrowNumbersEnabled = false
        SettingsManager.shotsPerEnd = 6
        SettingsManager.target = Target(
            WAFull.ID, 2,
            Dimension(60f, Dimension.Unit.CENTIMETER)
        )
        SettingsManager.timerEnabled = true
    }

    @Test
    fun takeScreenshots() {
        onView(Matchers.allOf<View>(withId(R.id.recyclerView), isDisplayed())).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(2, click())
        )
        Screengrab.screenshot("3_trainings_overview")
        onView(withId(R.id.action_bows)).perform(click())
        onView(withText("PSE Fever")).perform(click())
        Screengrab.screenshot("7_bow")
        navigateUp()
        onView(withId(R.id.action_trainings)).perform(click())
        onView(withText("631/720")).perform(click())
        Screengrab.screenshot("4_training_overview")
        clickActionBarItem(R.id.action_scoreboard, R.string.scoreboard)
        Screengrab.screenshot("5_scoreboard")
        navigateUp()
        clickActionBarItem(R.id.action_statistics, R.string.statistic)
        SystemClock.sleep(100)
        Screengrab.screenshot("6_statistics")
        navigateUp()
        navigateUp()
        onView(ViewMatcher.supportFab()).perform(click())
        onView(withId(R.id.fab1)).perform(click())
        Screengrab.screenshot("2_enter_training")
        save()
        onView(isRoot()).perform(click())
        onView(isRoot()).perform(click())
        Screengrab.screenshot("8_timer")
        pressBack()
        onView(withId(R.id.targetViewContainer)).perform(
            TargetViewActions.clickTarget(0.1f, 0.05f),
            TargetViewActions.clickTarget(-0.15f, 0.3f),
            TargetViewActions.clickTarget(-0.24f, -0.06f),
            TargetViewActions.holdTapTarget(0.0f, 0.0f)
        )
        Screengrab.screenshot("1_enter_end")
        onView(withId(R.id.targetViewContainer))
            .perform(TargetViewActions.releaseTapTarget(0.0f, 0.0f))
    }

    companion object {
        @ClassRule
        @JvmField
        public val classRule: TestRule = LocaleTestRule()
    }
}
