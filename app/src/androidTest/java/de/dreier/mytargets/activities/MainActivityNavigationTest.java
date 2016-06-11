package de.dreier.mytargets.activities;


import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.dreier.mytargets.R;
import de.dreier.mytargets.UITestBase;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static de.dreier.mytargets.PermissionGranter.allowPermissionsIfNeeded;
import static de.dreier.mytargets.fragments.EditTrainingFragment.FREE_TRAINING;
import static de.dreier.mytargets.fragments.EditTrainingFragment.TRAINING_TYPE;
import static de.dreier.mytargets.fragments.EditTrainingFragment.TRAINING_WITH_STANDARD_ROUND;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityNavigationTest extends UITestBase {

    @Rule
    public IntentsTestRule<MainActivity> mActivityTestRule = new IntentsTestRule<>(
            MainActivity.class);

    @Test
    public void mainActivityNavigationTest() {
        // Do settings work
        onView(allOf(withId(R.id.action_preferences), isDisplayed())).perform(click());
        intended(hasComponent(SimpleFragmentActivity.SettingsActivity.class.getName()));
        pressBack();

        // Does new free training work
        onView(allOf(withId(R.id.fab), isDisplayed())).perform(click());
        onView(allOf(withId(R.id.fab1Label), isDisplayed()))
                .perform(click()); // TODO fix fabmenu fab1 not visible
        intended(allOf(hasComponent(SimpleFragmentActivity.EditTrainingActivity.class.getName()),
                hasExtra(TRAINING_TYPE, FREE_TRAINING)));
        allowPermissionsIfNeeded(mActivityTestRule.getActivity(), ACCESS_FINE_LOCATION);
        pressBack();

        // Does new training with standard round work
        onView(allOf(withId(R.id.fab), isDisplayed())).perform(click());
        onView(allOf(withId(R.id.fab2Label), isDisplayed())).perform(click());
        intended(allOf(
                hasComponent(SimpleFragmentActivity.EditTrainingActivity.class.getName()),
                hasExtra(TRAINING_TYPE, TRAINING_WITH_STANDARD_ROUND)));
        pressBack();

        // TODO test with existing trainings, bows and arrows

        // Does new bow work
        onView(allOf(withText(R.string.bow), isDisplayed())).perform(click());
        onView(allOf(withId(R.id.fab), isDisplayed())).perform(click());
        intended(hasComponent(SimpleFragmentActivity.EditBowActivity.class.getName()));
        pressBack();

        // Does new arrow work
        onView(allOf(withText(R.string.arrow), isDisplayed())).perform(click());
        onView(allOf(withId(R.id.fab), isDisplayed())).perform(click());
        intended(hasComponent(SimpleFragmentActivity.EditArrowActivity.class.getName()));
        pressBack();
    }
}
