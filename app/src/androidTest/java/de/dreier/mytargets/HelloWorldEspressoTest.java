package de.dreier.mytargets;

import android.os.SystemClock;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.dreier.mytargets.activities.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.Visibility.INVISIBLE;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static de.dreier.mytargets.OrientationChangeAction.orientationLandscape;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class HelloWorldEspressoTest extends UITestBase {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void appDoesStartUp() {
        onView(withText(R.string.my_targets)).check(matches(isDisplayed()));
    }

    @Test
    public void addTraining() throws InterruptedException {
        onView(withId(R.id.fab1)).check(matches(withEffectiveVisibility(INVISIBLE)));
        onView(withId(R.id.fab)).perform(click()).check(matches(isDisplayed()));
        SystemClock.sleep(500);
        onView(withId(R.id.fab1Label)).perform(click());
        onView(withId(R.id.action_save)).perform(click());
        onView(isRoot()).perform(orientationLandscape(mActivityRule));
        navigateUp();
        onView(withId(R.id.detail_score)).check(matches(withText(CoreMatchers.containsString("0/30")))); //FIXME 0/0 vs 0/30?
    }
}