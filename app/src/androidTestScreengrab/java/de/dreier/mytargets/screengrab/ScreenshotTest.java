package de.dreier.mytargets.screengrab;

import android.os.SystemClock;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.filters.SdkSuppress;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import de.dreier.mytargets.R;
import de.dreier.mytargets.features.main.MainActivity;
import de.dreier.mytargets.features.settings.SettingsManager;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.targets.models.WAFull;
import de.dreier.mytargets.test.base.UITestBase;
import de.dreier.mytargets.test.utils.rules.SimpleDbTestRule;
import tools.fastlane.screengrab.Screengrab;
import tools.fastlane.screengrab.UiAutomatorScreenshotStrategy;
import tools.fastlane.screengrab.locale.LocaleTestRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static de.dreier.mytargets.test.utils.actions.TargetViewActions.clickTarget;
import static de.dreier.mytargets.test.utils.actions.TargetViewActions.holdTapTarget;
import static de.dreier.mytargets.test.utils.actions.TargetViewActions.releaseTapTarget;
import static de.dreier.mytargets.test.utils.matchers.ViewMatcher.matchFabMenu;
import static org.hamcrest.core.AllOf.allOf;

@SdkSuppress(minSdkVersion = 18)
@LargeTest
@RunWith(AndroidJUnit4.class)
public class ScreenshotTest extends UITestBase {
    @ClassRule
    public static final TestRule classRule = new LocaleTestRule();

    private ActivityTestRule activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public final RuleChain rule = RuleChain.outerRule(new SimpleDbTestRule())
            .around(activityTestRule);

    @Before
    public void setUp() {
        Screengrab.setDefaultScreenshotStrategy(new UiAutomatorScreenshotStrategy());
        SettingsManager.INSTANCE.setArrowNumbersEnabled(false);
        SettingsManager.INSTANCE.setShotsPerEnd(6);
        SettingsManager.INSTANCE.setTarget(new Target(WAFull.ID, 2,
                new Dimension(60, Dimension.Unit.CENTIMETER)));
        SettingsManager.INSTANCE.setTimerEnabled(true);
    }

    @Test
    public void takeScreenshots() {
        onView(Matchers.allOf(withId(R.id.recyclerView), isDisplayed())).perform(
                RecyclerViewActions.actionOnItemAtPosition(2, click()));
        Screengrab.screenshot("3_trainings_overview");
        onView(withText(R.string.bow)).perform(click());
        onView(withText("PSE Fever")).perform(click());
        Screengrab.screenshot("7_bow");
        navigateUp();
        onView(allOf(withText(R.string.training),
                withParent(withParent(withParent(withId(R.id.slidingTabs)))))).perform(click());
        onView(withText("631/720")).perform(click());
        Screengrab.screenshot("4_training_overview");
        clickActionBarItem(R.id.action_scoreboard, R.string.scoreboard);
        Screengrab.screenshot("5_scoreboard");
        navigateUp();
        clickActionBarItem(R.id.action_statistics, R.string.statistic);
        SystemClock.sleep(100);
        Screengrab.screenshot("6_statistics");
        navigateUp();
        navigateUp();
        onView(matchFabMenu()).perform(click());
        onView(withId(R.id.fab1)).perform(click());
        Screengrab.screenshot("2_enter_training");
        save();
        onView(isRoot()).perform(click());
        onView(isRoot()).perform(click());
        Screengrab.screenshot("8_timer");
        pressBack();
        onView(withId(R.id.targetViewContainer)).perform(
                clickTarget(0.1f, 0.05f),
                clickTarget(-0.15f, 0.3f),
                clickTarget(-0.24f, -0.06f),
                holdTapTarget(0.0f, 0.0f));
        Screengrab.screenshot("1_enter_end");
        onView(withId(R.id.targetViewContainer)).perform(releaseTapTarget(0.0f, 0.0f));
    }
}
