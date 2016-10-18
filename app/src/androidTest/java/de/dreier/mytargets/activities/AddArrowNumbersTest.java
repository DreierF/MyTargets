package de.dreier.mytargets.activities;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.dreier.mytargets.R;
import de.dreier.mytargets.UITestBase;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class AddArrowNumbersTest extends UITestBase {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(
            MainActivity.class);

    @Test
    public void addArrowNumbersTest() {
        onView(allOf(withText(R.string.arrow), isDisplayed())).perform(click());
        onView(allOf(withId(R.id.fab), isDisplayed())).perform(click());
        onView(withText(R.string.add)).perform(nestedScrollTo(), click());

        onView(allOf(withId(R.id.arrowNumber), isDisplayed()))
                .perform(click(), replaceText("1"), closeSoftKeyboard());

        onView(allOf(withText(R.string.add), isDisplayed()))
                .perform(click());

        onView(allOf(withId(R.id.arrowNumber),
                withParent(childAtPosition(withId(R.id.arrowNumbers), 1))))
                .perform(replaceText("2"), closeSoftKeyboard());

        onView(allOf(withId(R.id.action_save), withContentDescription(R.string.save),
                isDisplayed())).perform(click());

        onView(allOf(withId(R.id.recyclerView), isDisplayed()))
                .perform(actionOnItemAtPosition(0, click()));

        onView(allOf(withId(R.id.arrowNumber),
                withParent(childAtPosition(withId(R.id.arrowNumbers), 0))))
                .perform(nestedScrollTo()).check(matches(withText("1")));

        onView(allOf(withId(R.id.arrowNumber),
                withParent(childAtPosition(withId(R.id.arrowNumbers), 1))))
                .perform(nestedScrollTo()).check(matches(withText("2")));
    }
}
