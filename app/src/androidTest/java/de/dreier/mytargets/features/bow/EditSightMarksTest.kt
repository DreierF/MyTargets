/*
 * Copyright (C) 2018 Florian Dreier
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

package de.dreier.mytargets.features.bow


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.recyclerview.widget.RecyclerView
import androidx.test.rule.ActivityTestRule
import de.dreier.mytargets.R
import de.dreier.mytargets.features.main.MainActivity
import de.dreier.mytargets.test.base.UITestBase
import de.dreier.mytargets.test.utils.matchers.MatcherUtils.containsStringRes
import de.dreier.mytargets.test.utils.matchers.RecyclerViewMatcher.Companion.withNestedRecyclerView
import de.dreier.mytargets.test.utils.matchers.RecyclerViewMatcher.Companion.withRecyclerView
import de.dreier.mytargets.test.utils.matchers.ViewMatcher.clickFabSpeedDialItem
import de.dreier.mytargets.test.utils.rules.EmptyDbTestRule
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditSightMarksTest : UITestBase() {

    private val activityTestRule = ActivityTestRule(
            MainActivity::class.java)

    @get:Rule
    val rule = RuleChain.outerRule(EmptyDbTestRule())
            .around(activityTestRule)

    @Test
    fun editSightMarksTest() {
        onView(allOf(withText(R.string.bow), isDisplayed())).perform(click())

        clickFabSpeedDialItem(R.id.fabBowRecurve)

        // Set initial sight mark to 18m: 1
        onView(withNestedRecyclerView(R.id.sightMarks).atPositionOnView(0, R.id.sightSetting))
                .perform(nestedScrollTo(), replaceText("1"))

        // Add sight mark 10m: 2
        onView(withId(R.id.addButton))
                .perform(nestedScrollTo(), click())
        onView(withNestedRecyclerView(R.id.sightMarks).atPositionOnView(1, R.id.sightSetting))
                .perform(nestedScrollTo(), replaceText("2"))
        onView(withNestedRecyclerView(R.id.sightMarks).atPositionOnView(1, R.id.distance))
                .perform(nestedScrollTo(), click())
        onView(allOf(withId(R.id.recyclerView), isDisplayed()))
                .perform(actionOnItem<RecyclerView.ViewHolder>(hasDescendant(withText("10m")), click()))

        save()

        Thread.sleep(500)

        // Make sure changes have been saved and are sorted correctly
        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(0, R.id.details))
                .check(matches(allOf(containsStringRes(R.string.recurve_bow),
                        withText(containsString("10m: 2\n18m: 1")))))

        // Open bow again
        onView(withRecyclerView(R.id.recyclerView).atPosition(0))
                .perform(click())

        // Add sight mark 15m: 3
        onView(withId(R.id.addButton))
                .perform(nestedScrollTo(), click())
        onView(withNestedRecyclerView(R.id.sightMarks).atPositionOnView(2, R.id.sightSetting))
                .perform(nestedScrollTo(), replaceText("3"), closeSoftKeyboard())
        onView(withNestedRecyclerView(R.id.sightMarks).atPositionOnView(2, R.id.distance))
                .perform(nestedScrollTo(), click())
        onView(allOf(withId(R.id.recyclerView), isDisplayed()))
                .perform(actionOnItem<RecyclerView.ViewHolder>(hasDescendant(withText("15m")), click()))

        // "Accidentally delete" a sight mark and undo it
        onView(withNestedRecyclerView(R.id.sightMarks).atPositionOnView(0, R.id.removeSightSetting))
                .perform(nestedScrollTo(), click())
        onView(withId(R.id.snackbar_text))
                .check(matches(withText(R.string.sight_setting_removed)))
        onView(withId(R.id.snackbar_action)).perform(click())

        save()

        Thread.sleep(500)

        // Make sure changes have been saved and are sorted correctly
        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(0, R.id.details))
                .check(matches(allOf(containsStringRes(R.string.recurve_bow),
                        withText(containsString("10m: 2\n15m: 3\n18m: 1")))))

        // Open bow for edit via CAB
        onView(withRecyclerView(R.id.recyclerView).atPosition(0))
                .perform(longClick())
        clickContextualActionBarItem(R.id.action_edit, R.string.edit)

        onView(withNestedRecyclerView(R.id.sightMarks).atPositionOnView(0, R.id.removeSightSetting))
                .perform(nestedScrollTo(), click())

        onView(withId(R.id.snackbar_text))
                .check(matches(withText(R.string.sight_setting_removed)))

        save()

        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(0, R.id.details))
                .check(matches(allOf(containsStringRes(R.string.recurve_bow),
                        withText(containsString("15m: 3\n18m: 1")))))
                .check(matches(not(withText(containsString("10m")))))
    }
}
