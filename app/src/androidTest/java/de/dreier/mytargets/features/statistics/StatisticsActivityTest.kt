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

package de.dreier.mytargets.features.statistics

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.pressBack
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withParent
import android.support.test.runner.AndroidJUnit4
import de.dreier.mytargets.R
import de.dreier.mytargets.shared.models.db.Training
import de.dreier.mytargets.test.base.UITestBase
import de.dreier.mytargets.test.utils.rules.SimpleDbTestRule
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StatisticsActivityTest : UITestBase() {

    private val activityTestRule = IntentsTestRule(
            StatisticsActivity::class.java, true, false)

    @get:Rule
    val rule = RuleChain.outerRule(SimpleDbTestRule())
            .around(activityTestRule)

    @Test
    fun navigationTest() {
        // Add round ids
        val roundIds = Training.all
                .flatMap { t -> t.loadRounds() }
                .map { it.id }
                .toList()
        activityTestRule.launchActivity(StatisticsActivity.getIntent(roundIds).build())

        onView(allOf(withId(R.id.dispersionViewOverlay),
                withParent(withId(R.id.dispersionPatternLayout))))
                .perform(nestedScrollTo(), click())

        pressBack()

        onView(withId(R.id.action_filter)).perform(click())
        onView(withId(R.id.action_filter)).perform(click())

        //TODO Test csv export
        //clickActionBarItem(R.id.action_export, R.string.exports_as_csv);
    }

}
