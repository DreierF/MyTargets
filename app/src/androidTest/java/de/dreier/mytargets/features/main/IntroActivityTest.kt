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

package de.dreier.mytargets.features.main

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import de.dreier.mytargets.R
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.test.base.UITestBase
import de.dreier.mytargets.test.utils.matchers.ViewMatcher
import de.dreier.mytargets.test.utils.rules.EmptyDbTestRule
import org.hamcrest.Matchers.allOf
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IntroActivityTest : UITestBase() {

    private val activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Rule
    val rule = RuleChain.outerRule(EmptyDbTestRule())
            .around(activityTestRule)

    @Test
    fun introActivityTest() {
        onView(allOf(withId(R.id.txt_title_slide), isDisplayed()))
                .check(matches(withText(R.string.intro_title_track_training_progress)))
        onView(withId(R.id.button_next)).perform(click())

        onView(allOf(withId(R.id.txt_title_slide), isDisplayed()))
                .check(matches(withText(R.string.intro_title_everything_in_one_place)))
        onView(withId(R.id.button_next)).perform(click())

        onView(withId(R.id.toolbar)).check(matches(hasDescendant(withText(R.string.my_targets))))

        onView(ViewMatcher.supportFab()).perform(click())
        //allowPermissionsIfNeeded(activityTestRule.getActivity(), ACCESS_FINE_LOCATION);
    }

    companion object {

        @BeforeClass
        @JvmStatic
        fun setUp() {
            SettingsManager.shouldShowIntroActivity = true
        }
    }
}
