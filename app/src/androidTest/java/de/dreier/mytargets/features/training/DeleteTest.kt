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

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.pressBack
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.longClick
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import de.dreier.mytargets.R
import de.dreier.mytargets.features.main.MainActivity
import de.dreier.mytargets.test.base.UITestBase
import de.dreier.mytargets.test.utils.assertions.RecyclerViewAssertions.itemCount
import de.dreier.mytargets.test.utils.matchers.MatcherUtils.matchToolbarTitle
import de.dreier.mytargets.test.utils.matchers.MatcherUtils.withToolbarTitle
import de.dreier.mytargets.test.utils.rules.SimpleDbTestRule
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class DeleteTest : UITestBase() {

    private val activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Rule
    val rule = RuleChain.outerRule(SimpleDbTestRule())
            .around(activityTestRule)

    @Test
    @Throws(Exception::class)
    fun testDeleteRound() {
        // Expand july
        onView(allOf(withId(R.id.recyclerView), isDisplayed())).perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(2, click()))

        // Delete and undo deletion
        onView(allOf(withId(R.id.recyclerView), isDisplayed())).perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(1, longClick()))
        onView(allOf(withId(R.id.recyclerView), isDisplayed())).perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(3, click()))
        clickContextualActionBarItem(R.id.action_delete, R.string.delete)
        onView(withId(R.id.snackbar_action)).perform(click())

        // Delete training
        onView(allOf(withId(R.id.recyclerView), isDisplayed())).perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(3, longClick()))
        clickContextualActionBarItem(R.id.action_delete, R.string.delete)
        val trainingText = activityTestRule
                .activity.resources.getQuantityString(R.plurals.training_deleted, 1, 1)
        onView(withId(R.id.snackbar_text)).check(matches(withText(trainingText)))

        // Open training
        onView(allOf(withId(R.id.recyclerView), isDisplayed())).perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        onView(allOf(withId(R.id.recyclerView), isDisplayed())).perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click()))
        onView(withText("Aug 22, 2016")).check(matches(isDisplayed()))

        onView(withId(R.id.recyclerView)).perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()))
        clickContextualActionBarItem(R.id.action_delete, R.string.delete)

        // Open round
        onView(withId(R.id.recyclerView)).perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        // Delete ends and undo
        onView(withId(R.id.recyclerView)).perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(2, longClick()))
        onView(withId(R.id.recyclerView)).perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(3, click()))
        clickContextualActionBarItem(R.id.action_delete, R.string.delete)
        val endsText = activityTestRule
                .activity.resources.getQuantityString(R.plurals.passe_deleted, 2, 2)
        onView(withId(R.id.snackbar_text)).check(matches(withText(endsText)))
        onView(withId(R.id.snackbar_action)).perform(click())

        // Delete ends
        onView(withId(R.id.recyclerView)).perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(1, longClick()))
        onView(withId(R.id.recyclerView)).perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(3, click()))
        clickContextualActionBarItem(R.id.action_delete, R.string.delete)

        onView(withId(R.id.snackbar_text)).check(matches(withText(endsText)))

        pressBack()

        onView(withId(R.id.recyclerView)).perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        onView(withId(R.id.recyclerView)).check(itemCount(`is`(4)))

        pressBack()
        onView(allOf(isAssignableFrom(Toolbar::class.java), withId(R.id.toolbar)))
                .check(ViewAssertions.matches(withToolbarTitle(`is`(getString(R.string.training)))))
        pressBack()
        matchToolbarTitle(getString(R.string.my_targets))
    }
}
