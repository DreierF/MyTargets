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

package de.dreier.mytargets.features.arrow


import android.support.test.espresso.Espresso.onData
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import de.dreier.mytargets.R
import de.dreier.mytargets.features.main.MainActivity
import de.dreier.mytargets.test.base.UITestBase
import de.dreier.mytargets.test.utils.matchers.RecyclerViewMatcher.Companion.withRecyclerView
import de.dreier.mytargets.test.utils.matchers.ViewMatcher.supportFab
import de.dreier.mytargets.test.utils.rules.EmptyDbTestRule
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditArrowTest : UITestBase() {

    private val activityTestRule = ActivityTestRule(
            MainActivity::class.java)

    @get:Rule
    val rule = RuleChain.outerRule(EmptyDbTestRule())
            .around(activityTestRule)

    @Test
    fun editArrowTest() {
        onView(withId(R.id.action_arrows)).perform(click())

        // Add new arrow and change some properties
        onView(supportFab()).perform(click())
        onView(withId(R.id.name))
                .perform(nestedScrollTo(), replaceText("Arrow"), closeSoftKeyboard())
        onView(withText(R.string.more_fields))
                .perform(nestedScrollTo(), click())
        onView(withId(R.id.length))
                .perform(nestedScrollTo(), replaceText("Length"))
        onView(withId(R.id.diameter))
                .perform(nestedScrollTo(), replaceText("680"), closeSoftKeyboard())

        // Attempt to save and check if error is shown
        save()
        onView(withId(R.id.diameterTextInputLayout))
                .check(matches(hasDescendant(withText(getString(R.string.not_within_expected_range_mm)))))

        // Fix input
        onView(withId(R.id.diameter))
                .perform(nestedScrollTo(), replaceText("6.8"), closeSoftKeyboard())
        save()

        // Check if arrow has been saved
        onView(withRecyclerView(R.id.recyclerView).atPosition(0))
                .check(matches(hasDescendant(withText("Arrow"))))

        // Open arrow again via CAB
        onView(withRecyclerView(R.id.recyclerView).atPosition(0))
                .perform(longClick())
        clickContextualActionBarItem(R.id.action_edit, R.string.edit)

        // Check if properties have been saved and are shown
        onView(withId(R.id.length))
                .check(matches(withText("Length")))
        onView(withId(R.id.diameter))
                .check(matches(withText("6.8")))
        onView(withId(R.id.diameterUnit))
                .check(matches(withSpinnerText("mm")))

        // Change unit to inch
        onView(withId(R.id.diameterUnit)).perform(nestedScrollTo(), click())
        onData(allOf(`is`(instanceOf<Any>(String::class.java)), `is`("inch"))).perform(click())
        save()

        // Correct value and save
        onView(withId(R.id.diameterTextInputLayout))
                .check(matches(hasDescendant(withText(R.string.not_within_expected_range_inch))))
        onView(withId(R.id.diameter))
                .perform(nestedScrollTo(), replaceText("0.5"), closeSoftKeyboard())

        // TODO Fix camera test
        //        onView(withId(R.id.coordinatorLayout)).perform(swipeDown());
        //        onView(supportFab()).perform(click());
        //        intendingImageCapture(getInstrumentation().getContext(),
        //                de.dreier.mytargets.debug.test.R.raw.mocked_image_capture);
        //        onView(withText(R.string.take_picture))
        //                .perform(click());
        //
        //        allowPermissionsIfNeeded(activityTestRule.getActivity(), CAMERA);
        //
        //        intendedImageCapture();

        save()

        onView(withRecyclerView(R.id.recyclerView).atPosition(0))
                .perform(click())

        onView(withId(R.id.diameter)).check(matches(withText("0.5")))
        onView(withId(R.id.diameterUnit))
                .check(matches(withSpinnerText("inch")))

        // Change name and discard it
        onView(withId(R.id.name))
                .perform(nestedScrollTo(), replaceText("Arrrr"), closeSoftKeyboard())
        navigateUp()

        // Check if arrow has not been saved
        onView(withRecyclerView(R.id.recyclerView).atPosition(0))
                .check(matches(hasDescendant(withText("Arrow"))))
    }
}
