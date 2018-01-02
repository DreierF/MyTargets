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

package de.dreier.mytargets.features.training.standardround


import android.content.Intent
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.scrollTo
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
import de.dreier.mytargets.features.training.edit.EditTrainingFragment.Companion.CREATE_TRAINING_WITH_STANDARD_ROUND_ACTION
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.Dimension.Unit.CENTIMETER
import de.dreier.mytargets.shared.models.Dimension.Unit.METER
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.targets.models.WAFull
import de.dreier.mytargets.test.base.UITestBase
import de.dreier.mytargets.test.utils.matchers.ParentViewMatcher.isNestedChildOfView
import de.dreier.mytargets.test.utils.matchers.RecyclerViewMatcher.Companion.withRecyclerView
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@Ignore
@RunWith(AndroidJUnit4::class)
class EditStandardRoundActivityTest : UITestBase() {

    @get:Rule
    var activityTestRule = IntentsTestRule(
            EditTrainingActivity::class.java, true, false)

    @Before
    fun setUp() {
        SettingsManager.target = Target(WAFull.ID, 0, Dimension(122f, CENTIMETER))
        SettingsManager.distance = Dimension(50f, METER)
        SettingsManager.timerEnabled = false
        SettingsManager.shotsPerEnd = 3
        SettingsManager.endCount = 10
        SettingsManager.distance = Dimension(10f, METER)
    }

    @Test
    fun editStandardRoundActivity() {
        val intent = Intent()
        intent.action = CREATE_TRAINING_WITH_STANDARD_ROUND_ACTION
        activityTestRule.launchActivity(intent)

        //allowPermissionsIfNeeded(activityTestRule.getActivity(), ACCESS_FINE_LOCATION);

        onView(withId(R.id.standardRound)).perform(scrollTo(), click())

        onView(withId(R.id.fab)).perform(click())

        onView(withId(R.id.distance)).perform(nestedScrollTo(), click())

        onView(allOf(withId(R.id.recyclerView), isDisplayed()))
                .perform(actionOnItem<RecyclerView.ViewHolder>(hasDescendant(withText("20m")), click()))

        onView(withId(R.id.target)).perform(nestedScrollTo(), click())

        onView(withId(R.id.recyclerView))
                .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(9, click()))

        navigateUp()

        onView(withId(R.id.addButton)).perform(nestedScrollTo(), click())

        onView(withRecyclerView(R.id.rounds).atPositionOnView(1, R.id.distance))
                .perform(nestedScrollTo(), click())

        onView(allOf(withId(R.id.recyclerView), isDisplayed()))
                .perform(actionOnItem<RecyclerView.ViewHolder>(hasDescendant(withText("15m")), click()))

        onView(allOf(withId(R.id.number_increment), isNestedChildOfView(withId(R.id.shotCount)),
                isNestedChildOfView(withRecyclerView(R.id.rounds).atPosition(1))))
                .perform(nestedScrollTo(), click(), click(), click())

        onView(allOf(withId(R.id.number_decrement), isNestedChildOfView(withId(R.id.endCount)),
                isNestedChildOfView(withRecyclerView(R.id.rounds).atPosition(1))))
                .perform(nestedScrollTo(), click(), click(), click(), click(), click())

        save()

        onView(withId(R.id.standardRound))
                .check(matches(hasDescendant(withText(R.string.custom_round))))

        onView(withId(R.id.standardRound))
                .check(matches(hasDescendant(withText(
                        allOf(startsWith("20m: 10 × 3"), containsString("15m: 5 × 6"))))))

        activityTestRule.activity.finish()
    }
}
