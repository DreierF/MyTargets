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

package de.dreier.mytargets.features.settings


import android.support.annotation.StringRes
import android.support.test.espresso.Espresso.onData
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.pressBack
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.closeSoftKeyboard
import android.support.test.espresso.action.ViewActions.replaceText
import android.support.test.espresso.contrib.RecyclerViewActions.scrollTo
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import de.dreier.mytargets.R
import de.dreier.mytargets.features.settings.backup.provider.EBackupLocation
import de.dreier.mytargets.shared.SharedApplicationInstance
import de.dreier.mytargets.test.base.UITestBase
import de.dreier.mytargets.test.utils.assertions.RecyclerViewAssertions.itemHasSummary
import de.dreier.mytargets.test.utils.matchers.MatcherUtils.matchToolbarTitle
import de.dreier.mytargets.test.utils.matchers.ParentViewMatcher.isOnForegroundFragment
import de.dreier.mytargets.test.utils.matchers.ViewMatcher.clickOnPreference
import junit.framework.Assert.assertEquals
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

@RunWith(AndroidJUnit4::class)
class SettingsActivityTest : UITestBase() {

    @get:Rule
    var activityTestRule = ActivityTestRule(
            SettingsActivity::class.java)

    private val activity: SettingsActivity
        get() = activityTestRule.activity

    @Before
    fun setUp() {
        SharedApplicationInstance.sharedPreferences
                .edit()
                .clear()
                .apply()
        SettingsManager.inputTargetZoom = 3.0f
        SettingsManager.inputArrowDiameterScale = 1.0f
        SettingsManager.backupLocation = EBackupLocation.INTERNAL_STORAGE
    }


    @Test
    fun settingsActivityTest() {
        matchToolbarTitle(activity.getString(R.string.preferences))

        clickOnPreference(R.string.profile)
        matchToolbarTitle(activity.getString(R.string.profile))

        clickOnPreference(R.string.first_name)
        enterText("Joe")
        matchPreferenceSummary(R.string.first_name, "Joe")
        assertEquals(SettingsManager.profileFirstName, "Joe")

        clickOnPreference(R.string.last_name)
        enterText("Doe")
        matchPreferenceSummary(R.string.last_name, "Doe")
        assertEquals(SettingsManager.profileLastName, "Doe")

        clickOnPreference(R.string.birthday)
        enterDate(1990, 2, 11)
        matchPreferenceSummary(R.string.birthday, DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                .format(LocalDate.of(1990, 2, 11)))

        clickOnPreference(R.string.club)
        enterText("Archery Club")
        matchPreferenceSummary(R.string.club, "Archery Club")
        assertEquals(SettingsManager.profileClub, "Archery Club")
        pressBack()

        clickOnPreference(R.string.overview)

        matchToolbarTitle(activity.getString(R.string.overview))
        pressBack()

        clickOnPreference(R.string.input)

        matchToolbarTitle(activity.getString(R.string.input))

        matchPreferenceSummary(R.string.target_zoom, "3.0x")
        clickOnPreference(R.string.target_zoom)
        selectFromList("5.0x")
        matchPreferenceSummary(R.string.target_zoom, "5.0x")
        assertEquals(SettingsManager.inputTargetZoom, 5.0f)

        matchPreferenceSummary(R.string.arrow_diameter_scale, "1.0x")
        clickOnPreference(R.string.arrow_diameter_scale)
        selectFromList("3.5x")
        matchPreferenceSummary(R.string.arrow_diameter_scale, "3.5x")
        assertEquals(SettingsManager.inputArrowDiameterScale, 3.5f)

        pressBack()
        matchToolbarTitle(activity.getString(R.string.preferences))

        clickOnPreference(R.string.scoreboard)
        matchToolbarTitle(activity.getString(R.string.scoreboard))

        pressBack()
        matchToolbarTitle(activity.getString(R.string.preferences))

        clickOnPreference(R.string.timer)
        matchToolbarTitle(activity.getString(R.string.timer))

        matchPreferenceSummary(R.string.timer_waiting_time, activity
                .resources.getQuantityString(R.plurals.second, 20, 20))

        matchPreferenceSummary(R.string.timer_shooting_time, activity
                .resources.getQuantityString(R.plurals.second, 120, 120))

        matchPreferenceSummary(R.string.timer_warning_time, activity
                .resources.getQuantityString(R.plurals.second, 30, 30))

        pressBack()

        // FIXME allowPermissionsIfNeeded does not seem to work/Is not called
        //        clickOnPreference(4);
        //        allowPermissionsIfNeeded(getActivity(), READ_EXTERNAL_STORAGE);
        //        matchToolbarTitle(getActivity().getString(R.string.backup_action));
        //        pressBack();

        clickOnPreference(R.string.language)
        selectFromList("Spanish (Español)")
        matchToolbarTitle("Opciones")

        clickOnPreference(R.string.language)
        selectFromList("Standard (recommended)")
        matchToolbarTitle("Options")
    }

    @After
    fun tearDown() {
        SharedApplicationInstance.sharedPreferences
                .edit()
                .clear()
                .apply()
    }

    private fun enterText(text: String) {
        onView(withId(android.R.id.edit))
                .perform(replaceText(text), closeSoftKeyboard())

        onView(allOf(withId(android.R.id.button1), withText(android.R.string.ok),
                isDisplayed())).perform(click())
    }

    private fun selectFromList(text: String) {
        onData(hasToString(startsWith(text)))
                .inAdapterView(withId(R.id.select_dialog_listview))
                .perform(click())
    }

    private fun matchPreferenceSummary(@StringRes text: Int, expectedSummary: String) {
        onView(allOf(withId(R.id.list), isOnForegroundFragment()))
                .perform(scrollTo<RecyclerView.ViewHolder>(hasDescendant(withText(text))))
                .check(itemHasSummary(text, expectedSummary))
    }
}
