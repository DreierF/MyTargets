/*
 * Copyright (C) 2017 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.features.settings;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import de.dreier.mytargets.R;
import de.dreier.mytargets.app.ApplicationInstance;
import de.dreier.mytargets.features.settings.backup.provider.EBackupLocation;
import de.dreier.mytargets.test.base.UITestBase;
import de.dreier.mytargets.test.utils.matchers.RecyclerViewMatcher;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static de.dreier.mytargets.test.utils.matchers.MatcherUtils.matchToolbarTitle;
import static de.dreier.mytargets.test.utils.matchers.ParentViewMatcher.isOnForegroundFragment;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.startsWith;

@RunWith(AndroidJUnit4.class)
public class SettingsActivityTest extends UITestBase {

    @Rule
    public ActivityTestRule<SettingsActivity> activityTestRule = new ActivityTestRule<>(
            SettingsActivity.class);

    @Before
    public void setUp() {
        ApplicationInstance.getSharedPreferences()
                .edit()
                .clear()
                .apply();
        SettingsManager.setInputTargetZoom(3.0f);
        SettingsManager.setInputArrowDiameterScale(1.0f);
        SettingsManager.setBackupLocation(EBackupLocation.INTERNAL_STORAGE);
    }


    @Test
    public void settingsActivityTest() {
        matchToolbarTitle(getActivity().getString(R.string.preferences));

        clickOnPreference(0);

        matchToolbarTitle(getActivity().getString(R.string.overview));
        pressBack();

        clickOnPreference(1);

        matchToolbarTitle(getActivity().getString(R.string.input));

        matchPreferenceSummary(9, "3.0x");
        clickOnPreference(9);
        selectFromList("5.0x");
        matchPreferenceSummary(9, "5.0x");
        assertEquals(SettingsManager.getInputTargetZoom(), 5.0f);

        matchPreferenceSummary(10, "1.0x");
        clickOnPreference(10);
        selectFromList("3.5x");
        matchPreferenceSummary(10, "3.5x");
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
        matchPreferenceSummary(3, DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).format(LocalDate.of(1990, 2, 11)));

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

        // FIXME allowPermissionsIfNeeded does not seem to work/Is not called
//        clickOnPreference(4);
//        allowPermissionsIfNeeded(getActivity(), READ_EXTERNAL_STORAGE);
//        matchToolbarTitle(getActivity().getString(R.string.backup_action));
//        pressBack();

        clickOnPreference(5);
        selectFromList("Spanish (Español)");
        matchToolbarTitle("Opciones");
    }

    @After
    public void tearDown() {
        ApplicationInstance.getSharedPreferences()
                .edit()
                .clear()
                .apply();
    }

    private SettingsActivity getActivity() {
        return activityTestRule.getActivity();
    }

    private void enterText(String text) {
        onView(withId(android.R.id.edit))
                .perform(replaceText(text), closeSoftKeyboard());

        onView(allOf(withId(android.R.id.button1), withText(android.R.string.ok),
                isDisplayed())).perform(click());
    }

    private void selectFromList(String text) {
        onData(hasToString(startsWith(text)))
                .inAdapterView(withId(R.id.select_dialog_listview))
                .perform(click());
    }

    private void matchPreferenceSummary(int position, String expectedSummary) {
        onView(allOf(withId(R.id.list), isOnForegroundFragment()))
                .perform(scrollToPosition(position));
        onView(RecyclerViewMatcher.withRecyclerView(allOf(withId(R.id.list),
                isOnForegroundFragment())).atPositionOnView(position, android.R.id.summary))
                .check(matches(withText(expectedSummary)));
    }

    private void clickOnPreference(int position) {
        onView(allOf(withId(R.id.list), isOnForegroundFragment()))
                .perform(scrollToPosition(position));
        onView(allOf(withId(R.id.list), isOnForegroundFragment()))
                .perform(actionOnItemAtPosition(position, click()));
    }
}
