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

package de.dreier.mytargets.features.training


import android.content.Intent
import android.support.test.espresso.Espresso.*
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.replaceText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.actionOnItem
import android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import de.dreier.mytargets.R
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.features.training.edit.EditTrainingActivity
import de.dreier.mytargets.features.training.edit.EditTrainingFragment.Companion.CREATE_FREE_TRAINING_ACTION
import de.dreier.mytargets.features.training.edit.EditTrainingFragment.Companion.CREATE_TRAINING_WITH_STANDARD_ROUND_ACTION
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.Dimension.Unit.CENTIMETER
import de.dreier.mytargets.shared.models.Dimension.Unit.METER
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.targets.models.WAFull
import de.dreier.mytargets.shared.views.TargetViewBase.EInputMethod
import de.dreier.mytargets.test.base.UITestBase
import de.dreier.mytargets.test.utils.assertions.RecyclerViewAssertions.itemCount
import de.dreier.mytargets.test.utils.matchers.MatcherUtils.containsStringRes
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

@RunWith(AndroidJUnit4::class)
class EditTrainingActivityTest : UITestBase() {

    @Rule
    var activityTestRule = IntentsTestRule(
            EditTrainingActivity::class.java, true, false)

    @Before
    fun setUp() {
        SettingsManager.standardRound = 93L
        SettingsManager.target = Target(WAFull.ID, 0, Dimension(122f, CENTIMETER))
        SettingsManager.distance = Dimension(50f, METER)
        SettingsManager.indoor = false
        SettingsManager.inputMethod = EInputMethod.PLOTTING
        SettingsManager.timerEnabled = false
        SettingsManager.shotsPerEnd = 3
        SettingsManager.distance = Dimension(10f, METER)
    }

    @Test
    fun createFreeTraining() {
        val intent = Intent()
        intent.action = CREATE_FREE_TRAINING_ACTION
        activityTestRule.launchActivity(intent)

        //allowPermissionsIfNeeded(activityTestRule.getActivity(), ACCESS_FINE_LOCATION);

        // Select distance 20m
        onView(withId(R.id.distance)).perform(nestedScrollTo(), click())
        onView(allOf(withId(R.id.recyclerView), isDisplayed()))
                .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(4, click()))
        onView(withId(R.id.distanceValue)).check(matches(withText("20m")))

        // Change distance to 23yd as custom distance
        onView(withId(R.id.distance)).perform(nestedScrollTo(), click())
        onView(withText(R.string.imperial)).perform(click())
        onView(allOf(withId(R.id.fab), isDisplayed())).perform(click())
        onView(withId(R.id.shot_comment)).perform(replaceText("23"))
        onView(withText(android.R.string.ok)).perform(click())
        onView(withId(R.id.distanceValue)).check(matches(withText("23yd")))

        // Change target to vertical 3 spot
        onView(withId(R.id.target)).perform(nestedScrollTo(), click())
        onView(withId(R.id.recyclerView)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(5, click()))
        onView(withId(R.id.scoring_style)).perform(click())
        onData(instanceOf(String::class.java)).atPosition(2).perform(click())
        onView(withId(R.id.target_size)).perform(click())
        onView(withText("40cm")).perform(click())
        pressBack()
        onView(withId(R.id.target))
                .check(matches(hasDescendant(withText(containsString(activityTestRule.activity
                        .getString(R.string.vertical_3_spot))))))
                .check(matches(hasDescendant(withText(containsString("40cm")))))
                .check(matches(hasDescendant(withText(containsString("Compound")))))

        // Change environment
        onView(withId(R.id.environment)).perform(nestedScrollTo(), click())
        onView(withId(R.id.rain)).perform(click())
        onView(withId(R.id.windSpeed)).perform(click())
        onView(withId(R.id.recyclerView)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(9, click()))
        onView(withId(R.id.windDirection)).perform(click())
        onView(withId(R.id.recyclerView)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(7, click()))
        onView(withId(R.id.location)).perform(replaceText("My location"))
        navigateUp()
        onView(withId(R.id.environment))
                .check(matches(hasDescendant(withText(R.string.rain))))
        onView(allOf(withId(R.id.details),
                withParent(withParent(withParent(withParent(withId(R.id.environment)))))))
                .check(matches(withText(containsString("9 Bft"))))
                .check(matches(withText(containsString("My location"))))

        onView(withId(R.id.trainingDate)).perform(click())
        enterDate(2016, 8, 10)
        val formattedDate = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                .format(LocalDate.of(2016, 8, 10))
        onView(withId(R.id.trainingDate)).check(matches(withText(formattedDate)))

        save()
        pressBack()
        pressBack()
    }

    @Test
    fun createTrainingWithStandardRound() {
        val intent = Intent()
        intent.action = CREATE_TRAINING_WITH_STANDARD_ROUND_ACTION
        activityTestRule.launchActivity(intent)

        //allowPermissionsIfNeeded(activityTestRule.getActivity(), ACCESS_FINE_LOCATION);

        // Has last used standard round been restored
        onView(withId(R.id.standardRound))
                .check(matches(hasDescendant(withText(R.string.warwick))))

        // Change standard round
        onView(withId(R.id.standardRound)).perform(nestedScrollTo(), click())
        onView(withId(R.id.recyclerView))
                .perform(actionOnItem<RecyclerView.ViewHolder>(hasDescendant(withText(R.string.wa_standard)), click()))
        onView(withId(R.id.recyclerView))
                .perform(actionOnItem<RecyclerView.ViewHolder>(hasDescendant(withText(R.string.wa_standard)), click()))
        onView(withId(R.id.standardRound))
                .check(matches(hasDescendant(withText(R.string.wa_standard))))

        onView(withText(R.string.change_target_face)).perform(nestedScrollTo(), click())

        onView(withId(R.id.recyclerView))
                .check(matches(hasDescendant(withText(R.string.wa_full))))
                .check(matches(hasDescendant(withText(R.string.wa_3_ring))))
                .check(itemCount(`is`(5)))

        onView(allOf(withId(R.id.recyclerView), isDisplayed()))
                .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(4, click()))
        navigateUp()

        save()

        navigateUp()
        navigateUp()

        onView(withId(R.id.detail_round_info))
                .check(matches(allOf(containsStringRes(R.string.wa_standard),
                        containsStringRes(R.string.wa_3_ring))))
    }
}
