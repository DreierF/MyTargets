package de.dreier.mytargets.activities;


import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.text.format.DateFormat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import de.dreier.mytargets.R;
import de.dreier.mytargets.UITestBase;
import de.dreier.mytargets.managers.SettingsManager;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static de.dreier.mytargets.utils.matchers.MatcherUtils.matchToolbarTitle;
import static de.dreier.mytargets.utils.matchers.MatcherUtils.withRecyclerView;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class SettingsActivityTest extends UITestBase {

    @Rule
    public ActivityTestRule<SettingsActivity> mActivityTestRule = new ActivityTestRule<>(
            SettingsActivity.class);

    @Before
    public void setUp() {
        SettingsManager.setInputTargetZoom(3.0f);
        SettingsManager.setInputArrowDiameterScale(1.0f);
        //TODO reset settings
    }

    @Test
    public void settingsActivityTest() {
        matchToolbarTitle(getActivity().getString(R.string.preferences));

        clickOnPreference(1);

        matchToolbarTitle(getActivity().getString(R.string.input));

        matchPreferenceSummary(0, "3.0x");
        clickOnPreference(0);
        selectFromList("5.0x");
        matchPreferenceSummary(0, "5.0x");
        assertEquals(SettingsManager.getInputTargetZoom(), 5.0f);

        matchPreferenceSummary(1, "1.0x");
        clickOnPreference(1);
        selectFromList("3.5x");
        matchPreferenceSummary(1, "3.5x");
        assertEquals(SettingsManager.getInputArrowDiameterScale(), 3.5f);

        pressBack();
        matchToolbarTitle(getActivity().getString(R.string.preferences));

        clickOnPreference(2);
        matchToolbarTitle(getActivity().getString(R.string.scoreboard));

        clickOnPreference(1);
        enterText("Joe");
        matchPreferenceSummary(1, "Joe");
        assertEquals(SettingsManager.getProfileFirstName(), "Joe");

        clickOnPreference(2);
        enterText("Doe");
        matchPreferenceSummary(2, "Doe");
        assertEquals(SettingsManager.getProfileLastName(), "Doe");

        clickOnPreference(3);
        enterDate(1990, 2, 11);
        matchPreferenceSummary(3, DateFormat.getDateFormat(
                InstrumentationRegistry.getTargetContext()).format(new Date(90, 1, 11)));

        clickOnPreference(4);
        enterText("Archery Club");
        matchPreferenceSummary(4, "Archery Club");
        assertEquals(SettingsManager.getProfileClub(), "Archery Club");

        clickOnPreference(6);

        clickOnPreference(6);

        clickOnPreference(12);

        clickOnPreference(14);

        clickOnPreference(20);

        clickOnPreference(22);

        clickOnPreference(28);

        pressBack();
        matchToolbarTitle(getActivity().getString(R.string.preferences));

        clickOnPreference(3);
        matchToolbarTitle(getActivity().getString(R.string.timer));

        matchPreferenceSummary(0, getActivity()
                .getResources().getQuantityString(R.plurals.second, 20, 20));

        matchPreferenceSummary(1, getActivity()
                .getResources().getQuantityString(R.plurals.second, 120, 120));

        matchPreferenceSummary(2, getActivity()
                .getResources().getQuantityString(R.plurals.second, 30, 30));

        pressBack();

        clickOnPreference(7);
        matchToolbarTitle(getActivity().getString(R.string.about));
        pressBack();

        clickOnPreference(8);
        matchToolbarTitle(getActivity().getString(R.string.licences));
        pressBack();
    }

    private SettingsActivity getActivity() {
        return mActivityTestRule.getActivity();
    }

    private void enterText(String text) {
        onView(withId(android.R.id.edit))
                .perform(scrollTo(), replaceText(text), closeSoftKeyboard());

        onView(allOf(withId(android.R.id.button1), withText(android.R.string.ok),
                withParent(allOf(withId(R.id.buttonPanel),
                        withParent(withId(R.id.parentPanel)))),
                isDisplayed())).perform(click());
    }

    private void selectFromList(String text1) {
        onView(allOf(withText(text1), withParent(withId(R.id.select_dialog_listview)),
                isDisplayed())).perform(click());
    }

    private void matchPreferenceSummary(int index, String expectedSummary) {
        onView(allOf(withRecyclerView(R.id.list).atPositionOnView(index, android.R.id.summary),
                isDisplayed())).check(matches(withText(expectedSummary)));
    }

    private void clickOnPreference(int position) {
        onView(allOf(withId(R.id.list), isOnForegroundFragment(), isDisplayed()))
                .perform(actionOnItemAtPosition(position, click()));
    }
}
