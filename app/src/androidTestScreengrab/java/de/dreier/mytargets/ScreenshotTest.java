package de.dreier.mytargets.screengrab;

import android.os.SystemClock;
import android.support.test.filters.LargeTest;
import android.support.test.filters.SdkSuppress;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import de.dreier.mytargets.R;
import de.dreier.mytargets.UITestBase;
import de.dreier.mytargets.activities.MainActivity;
import de.dreier.mytargets.managers.dao.SimpleDbTestRule;
import tools.fastlane.screengrab.locale.LocaleTestRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

@SdkSuppress(minSdkVersion = 18)
@LargeTest
@RunWith(AndroidJUnit4.class)
public class ScreenshotTest extends UITestBase {
    @ClassRule
    public static final TestRule classRule = new LocaleTestRule();

    @Rule
    public final RuleChain rule = RuleChain.outerRule(new SimpleDbTestRule())
            .around(new ActivityTestRule<>(MainActivity.class));

    @Test
    public void takeScreenshots() throws Exception {
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.fab)).perform(click());
        SystemScreengrab.screenshot("3_trainings_overview");
        onView(withText(R.string.bow)).perform(click());
        onView(withText("PSE Fever")).perform(click());
        SystemScreengrab.screenshot("7_bow");
        navigateUp();
        onView(allOf(withText(R.string.training), withParent(withParent(withParent(withId(R.id.slidingTabs)))))).perform(click());
        onView(withText("631/720")).perform(click());
        SystemScreengrab.screenshot("4_training_overview");
        clickActionBarItem(R.id.action_scoreboard, R.string.scoreboard);
        SystemScreengrab.screenshot("5_scoreboard");
        navigateUp();
        clickActionBarItem(R.id.action_statistics, R.string.statistic);
        SystemClock.sleep(1000);
        SystemScreengrab.screenshot("6_statistics");
        navigateUp();
        navigateUp();
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.fab1Label)).perform(click());
        SystemScreengrab.screenshot("2_enter_training");
        onView(withContentDescription(R.string.save)).perform(click());
        onView(isRoot()).perform(click());
        onView(isRoot()).perform(click());
        SystemScreengrab.screenshot("8_timer");
        getUiDevice().pressBack();
        onView(withId(R.id.targetView)).perform(
                clickTarget(0.1f, 0.05f),
                clickTarget(-0.45f, 0.5f),
                clickTarget(-0.5f, -0.6f),
                holdTapTarget(0.5f, 0.4f));
        SystemScreengrab.screenshot("1_enter_passe");
        SystemClock.sleep(1000);
        onView(withId(R.id.targetView)).perform(releaseTapTarget(0.5f, 0.4f));
    }

}