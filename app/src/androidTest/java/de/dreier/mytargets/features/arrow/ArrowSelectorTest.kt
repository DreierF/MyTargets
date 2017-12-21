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

package de.dreier.mytargets.features.arrow


import android.content.Intent
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.runner.AndroidJUnit4
import de.dreier.mytargets.R
import de.dreier.mytargets.features.arrows.ArrowListActivity
import de.dreier.mytargets.features.arrows.EditArrowActivity
import de.dreier.mytargets.features.training.edit.EditTrainingActivity
import de.dreier.mytargets.features.training.edit.EditTrainingFragment
import de.dreier.mytargets.test.base.UITestBase
import de.dreier.mytargets.test.utils.matchers.ParentViewMatcher.isNestedChildOfView
import de.dreier.mytargets.test.utils.matchers.RecyclerViewMatcher.Companion.withRecyclerView
import de.dreier.mytargets.test.utils.rules.EmptyDbTestRule
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ArrowSelectorTest : UITestBase() {

    private val activityTestRule = IntentsTestRule(
            EditTrainingActivity::class.java, true, false)

    @get:Rule
    val rule = RuleChain.outerRule(EmptyDbTestRule())
            .around(activityTestRule)

    @Test
    fun freeTrainingArrowSelectionTest() {
        arrowSelectionTest(EditTrainingFragment.CREATE_FREE_TRAINING_ACTION)
    }

    @Test
    fun standardRoundArrowSelectionTest() {
        arrowSelectionTest(EditTrainingFragment
                .CREATE_TRAINING_WITH_STANDARD_ROUND_ACTION)
    }

    private fun arrowSelectionTest(type: String) {
        val intent = Intent()
        intent.action = type
        activityTestRule.launchActivity(intent)
        //allowPermissionsIfNeeded(activityTestRule.getActivity(), ACCESS_FINE_LOCATION);

        onView(withText(R.string.add_arrow)).perform(nestedScrollTo(), click())
        intended(hasComponent(EditArrowActivity::class.java.name))
        save()

        onView(allOf(withId(R.id.name), isNestedChildOfView(withId(R.id.arrow)), isDisplayed()))
                .check(matches(withText(R.string.my_arrow)))

        // Check if arrow selection opens
        onView(withId(R.id.arrow)).perform(nestedScrollTo(), click())
        intended(hasComponent(ArrowListActivity::class.java.name))
        onView(withRecyclerView(R.id.recyclerView).atPosition(0))
                .check(matches(hasDescendant(withText(R.string.my_arrow))))
        navigateUp()
    }
}
