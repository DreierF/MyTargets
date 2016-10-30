package de.dreier.mytargets.selectors;


import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.RelativeLayout;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import de.dreier.mytargets.R;
import de.dreier.mytargets.UITestBase;
import de.dreier.mytargets.activities.ItemSelectActivity;
import de.dreier.mytargets.activities.SimpleFragmentActivityBase;
import de.dreier.mytargets.fragments.EditTrainingFragment;
import de.dreier.mytargets.utils.rules.EmptyDbTestRule;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static de.dreier.mytargets.PermissionGranter.allowPermissionsIfNeeded;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class BowSelectorTest extends UITestBase {

    private IntentsTestRule activityTestRule = new IntentsTestRule<>(
            SimpleFragmentActivityBase.EditTrainingActivity.class, true, false);

    @Rule
    public final RuleChain rule = RuleChain.outerRule(new EmptyDbTestRule())
            .around(activityTestRule);

    @Test
    public void freeTrainingBowSelectionTest() {
        bowSelectionTest(EditTrainingFragment.CREATE_FREE_TRAINING_ACTION);
    }

    @Test
    public void standardRoundBowSelectionTest() {
        bowSelectionTest(EditTrainingFragment.CREATE_TRAINING_WITH_STANDARD_ROUND_ACTION);
    }

    private void bowSelectionTest(String type) {
        Intent intent = new Intent();
        intent.setAction(type);
        activityTestRule.launchActivity(intent);
        allowPermissionsIfNeeded(activityTestRule.getActivity(), ACCESS_FINE_LOCATION);

        onView(withText(R.string.add_bow)).perform(nestedScrollTo(), click());
        intended(hasComponent(SimpleFragmentActivityBase.EditBowActivity.class.getName()));
        onView(allOf(withId(R.id.action_save), isDisplayed())).perform(click());

        onView(allOf(withId(R.id.name),
                withParent(withParent(withParent(withParent(withId(R.id.bow))))), isDisplayed()))
                .check(matches(withText(R.string.my_bow)));

        // Check if bow selection opens
        onView(withId(R.id.bow)).perform(nestedScrollTo(), click());
        intended(hasComponent(ItemSelectActivity.BowActivity.class.getName()));

        onView(allOf(withId(R.id.name), childAtPosition(childAtPosition(
                IsInstanceOf.instanceOf(RelativeLayout.class), 1), 0),
                isDisplayed())).check(matches(withText(R.string.my_bow)));
        navigateUp();
    }
}
