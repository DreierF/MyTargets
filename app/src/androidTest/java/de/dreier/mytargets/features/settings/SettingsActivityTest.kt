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


import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
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

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static de.dreier.mytargets.test.utils.assertions.RecyclerViewAssertions.itemHasSummary;
import static de.dreier.mytargets.test.utils.matchers.MatcherUtils.matchToolbarTitle;
import static de.dreier.mytargets.test.utils.matchers.ParentViewMatcher.isOnForegroundFragment;
import static de.dreier.mytargets.test.utils.matchers.ViewMatcher.clickOnPreference;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.startsWith;

@RunWith(AndroidJUnit4.class)
public class SettingsActivityTest extends UITestBase {

    @NonNull
    @Rule
    public ActivityTestRule<SettingsActivity> activityTestRule = new ActivityTestRule<>(
            SettingsActivity.class);

    @Before
    public void setUp() {
        ApplicationInstance.Companion.getSharedPreferences()
                .edit()
                .clear()
                .apply();
        SettingsManager.INSTANCE.setInputTargetZoom(3.0f);
        SettingsManager.INSTANCE.setInputArrowDiameterScale(1.0f);
        SettingsManager.INSTANCE.setBackupLocation(EBackupLocation.INTERNAL_STORAGE);
    }


    @Test
    public void settingsActivityTest() {
        matchToolbarTitle(getActivity().getString(R.string.preferences));

        clickOnPreference(R.string.profile);
        matchToolbarTitle(getActivity().getString(R.string.profile));

        clickOnPreference(R.string.first_name);
        enterText("Joe");
        matchPreferenceSummary(R.string.first_name, "Joe");
        assertEquals(SettingsManager.INSTANCE.getProfileFirstName(), "Joe");

        clickOnPreference(R.string.last_name);
        enterText("Doe");
        matchPreferenceSummary(R.string.last_name, "Doe");
        assertEquals(SettingsManager.INSTANCE.getProfileLastName(), "Doe");

        clickOnPreference(R.string.birthday);
        enterDate(1990, 2, 11);
        matchPreferenceSummary(R.string.birthday, DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                .format(LocalDate.of(1990, 2, 11)));

        clickOnPreference(R.string.club);
        enterText("Archery Club");
        matchPreferenceSummary(R.string.club, "Archery Club");
        assertEquals(SettingsManager.INSTANCE.getProfileClub(), "Archery Club");
        pressBack();

        clickOnPreference(R.string.overview);

        matchToolbarTitle(getActivity().getString(R.string.overview));
        pressBack();

        clickOnPreference(R.string.input);

        matchToolbarTitle(getActivity().getString(R.string.input));

        matchPreferenceSummary(R.string.target_zoom, "3.0x");
        clickOnPreference(R.string.target_zoom);
        selectFromList("5.0x");
        matchPreferenceSummary(R.string.target_zoom, "5.0x");
        assertEquals(SettingsManager.INSTANCE.getInputTargetZoom(), 5.0f);

        matchPreferenceSummary(R.string.arrow_diameter_scale, "1.0x");
        clickOnPreference(R.string.arrow_diameter_scale);
        selectFromList("3.5x");
        matchPreferenceSummary(R.string.arrow_diameter_scale, "3.5x");
        assertEquals(SettingsManager.INSTANCE.getInputArrowDiameterScale(), 3.5f);

        pressBack();
        matchToolbarTitle(getActivity().getString(R.string.preferences));

        clickOnPreference(R.string.scoreboard);
        matchToolbarTitle(getActivity().getString(R.string.scoreboard));

        pressBack();
        matchToolbarTitle(getActivity().getString(R.string.preferences));

        clickOnPreference(R.string.timer);
        matchToolbarTitle(getActivity().getString(R.string.timer));

        matchPreferenceSummary(R.string.timer_waiting_time, getActivity()
                .getResources().getQuantityString(R.plurals.second, 20, 20));

        matchPreferenceSummary(R.string.timer_shooting_time, getActivity()
                .getResources().getQuantityString(R.plurals.second, 120, 120));

        matchPreferenceSummary(R.string.timer_warning_time, getActivity()
                .getResources().getQuantityString(R.plurals.second, 30, 30));

        pressBack();

        // FIXME allowPermissionsIfNeeded does not seem to work/Is not called
//        clickOnPreference(4);
//        allowPermissionsIfNeeded(getActivity(), READ_EXTERNAL_STORAGE);
//        matchToolbarTitle(getActivity().getString(R.string.backup_action));
//        pressBack();

        clickOnPreference(R.string.language);
        selectFromList("Spanish (Español)");
        matchToolbarTitle("Opciones");

        clickOnPreference(R.string.language);
        selectFromList("Standard (recommended)");
        matchToolbarTitle("Options");
    }

    @After
    public void tearDown() {
        ApplicationInstance.Companion.getSharedPreferences()
                .edit()
                .clear()
                .apply();
    }

    private SettingsActivity getActivity() {
        return activityTestRule.getActivity();
    }

    private void enterText(@NonNull String text) {
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

    private void matchPreferenceSummary(@StringRes int text, String expectedSummary) {
        onView(allOf(withId(R.id.list), isOnForegroundFragment()))
                .perform(scrollTo(hasDescendant(withText(text))))
                .check(itemHasSummary(text, expectedSummary));
    }
}
