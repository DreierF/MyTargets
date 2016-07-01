package de.dreier.mytargets.activities;


import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.dreier.mytargets.R;
import de.dreier.mytargets.UITestBase;
import de.dreier.mytargets.activities.SimpleFragmentActivityBase.EditTrainingActivity;
import de.dreier.mytargets.managers.SettingsManager;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.targets.WAFull;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static de.dreier.mytargets.PermissionGranter.allowPermissionsIfNeeded;
import static de.dreier.mytargets.fragments.EditTrainingFragment.FREE_TRAINING;
import static de.dreier.mytargets.fragments.EditTrainingFragment.TRAINING_TYPE;
import static de.dreier.mytargets.shared.models.Dimension.Unit.CENTIMETER;
import static de.dreier.mytargets.shared.models.Dimension.Unit.METER;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;

@RunWith(AndroidJUnit4.class)
public class EditTrainingActivityTest extends UITestBase {

    @Rule
    public IntentsTestRule<MainActivity> mActivityTestRule = new IntentsTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        SettingsManager
                .setTarget(new Target(WAFull.ID, 0, new Dimension(122, CENTIMETER)));
        SettingsManager.setDistance(new Dimension(50, METER));
        SettingsManager.setIndoor(false);
        SettingsManager.setInputMode(false);
        SettingsManager.setTimerEnabled(false);
        SettingsManager.setArrowsPerPasse(3);
        SettingsManager.setDistance(new Dimension(10, METER));
    }

    @Test
    public void editTrainingActivityTest() {
        onView(matchFab()).perform(click());
        onView(allOf(withId(R.id.fab1), withParent(withId(R.id.fab)))).perform(click());

        intended(allOf(hasComponent(EditTrainingActivity.class.getName()),
                hasExtra(TRAINING_TYPE, FREE_TRAINING)));
        allowPermissionsIfNeeded(mActivityTestRule.getActivity(), ACCESS_FINE_LOCATION);

        // Select distance 20m
        onView(withId(R.id.distanceSpinner)).perform(nestedScrollTo(), click());
        onView(allOf(withId(R.id.recyclerView), isDisplayed()))
                .perform(actionOnItemAtPosition(4, click()));
        onView(withId(R.id.distance)).check(matches(withText("20m")));

        // Change distance to 23yd as custom distance
        onView(withId(R.id.distanceSpinner)).perform(nestedScrollTo(), click());
        onView(withText(R.string.imperial)).perform(click());
        onView(allOf(withId(R.id.fab), isDisplayed())).perform(click());
        onView(withId(R.id.shot_comment)).perform(replaceText("23"));
        onView(withText(android.R.string.ok)).perform(click());
        onView(withId(R.id.distance)).check(matches(withText("23yd")));

        // Change target to vertical 3 spot
        onView(withId(R.id.targetSpinner)).perform(nestedScrollTo(), click());
        onView(withId(R.id.recyclerView)).perform(actionOnItemAtPosition(5, click()));
        onView(withId(R.id.scoring_style)).perform(click());
        onView(withText("10, 9, 8, 7, 6")).perform(click());
        onView(withId(R.id.target_size)).perform(click());
        onView(withText("40cm")).perform(click());
        onView(withId(R.id.action_save)).perform(click());
        onView(allOf(withId(R.id.name),
                withParent(withParent(withParent(withParent(withId(R.id.targetSpinner)))))))
                .check(matches(withText(containsString(
                        mActivityTestRule.getActivity().getString(R.string.vertical_3_spot)))))
                .check(matches(withText(containsString("40cm"))));
        onView(allOf(withId(R.id.details),
                withParent(withParent(withParent(withParent(withId(R.id.targetSpinner)))))))
                .check(matches(withText("10, 9, 8, 7, 6")));

        // Change environment
        onView(withId(R.id.environmentSpinner)).perform(nestedScrollTo(), click());
        onView(withId(R.id.rain)).perform(click());
        onView(withId(R.id.windSpeed)).perform(click());
        onView(withId(R.id.recyclerView)).perform(actionOnItemAtPosition(9, click()));
        onView(withId(R.id.windDirection)).perform(click());
        onView(withId(R.id.recyclerView)).perform(actionOnItemAtPosition(7, click()));
        onView(withId(R.id.location)).perform(nestedScrollTo(), replaceText("My location"));
        onView(withId(R.id.action_save)).perform(click());
        onView(allOf(withId(R.id.name),
                withParent(withParent(withParent(withParent(withId(R.id.environmentSpinner)))))))
                .check(matches(withText(R.string.rain)));
        onView(allOf(withId(R.id.details),
                withParent(withParent(withParent(withParent(withId(R.id.environmentSpinner)))))))
                .check(matches(withText(containsString("9 Btf"))))
                .check(matches(withText(containsString("My location"))));

        onView(withId(R.id.trainingDate)).perform(click());
        //TODO change date
        pressBack();

        onView(withId(R.id.action_save)).perform(click());
        pressBack();
        pressBack();
    }
}