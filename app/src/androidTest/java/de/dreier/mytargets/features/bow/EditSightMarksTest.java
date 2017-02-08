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

package de.dreier.mytargets.features.bow;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import de.dreier.mytargets.R;
import de.dreier.mytargets.features.main.MainActivity;
import de.dreier.mytargets.test.base.UITestBase;
import de.dreier.mytargets.test.utils.rules.EmptyDbTestRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItem;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static de.dreier.mytargets.test.utils.matchers.MatcherUtils.containsStringRes;
import static de.dreier.mytargets.test.utils.matchers.RecyclerViewMatcher.withNestedRecyclerView;
import static de.dreier.mytargets.test.utils.matchers.RecyclerViewMatcher.withRecyclerView;
import static de.dreier.mytargets.test.utils.matchers.ViewMatcher.matchFabMenu;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class EditSightMarksTest extends UITestBase {

    private ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(
            MainActivity.class);

    @Rule
    public final RuleChain rule = RuleChain.outerRule(new EmptyDbTestRule())
            .around(activityTestRule);

    @Test
    public void editSightMarksTest() {
        onView(allOf(withText(R.string.bow), isDisplayed())).perform(click());

        onView(matchFabMenu()).perform(click());

        onView(withId(R.id.fabBowRecurve)).perform(click());

        // Set initial sight mark to 18m: 1
        onView(withNestedRecyclerView(R.id.sightMarks).atPositionOnView(0, R.id.sightSetting))
                .perform(nestedScrollTo(), replaceText("1"));

        // Add sight mark 10m: 2
        onView(withId(R.id.addButton))
                .perform(nestedScrollTo(), click());
        onView(withNestedRecyclerView(R.id.sightMarks).atPositionOnView(1, R.id.sightSetting))
                .perform(nestedScrollTo(), replaceText("2"));
        onView(withNestedRecyclerView(R.id.sightMarks).atPositionOnView(1, R.id.distance))
                .perform(nestedScrollTo(), click());
        onView(allOf(withId(R.id.recyclerView), isDisplayed()))
                .perform(actionOnItem(hasDescendant(withText("10m")), click()));

        save();

        // Make sure changes have been saved and are sorted correctly
        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(0, R.id.details))
                .check(matches(allOf(containsStringRes(R.string.recurve_bow),
                        withText(containsString("10m: 2\n18m: 1")))));

        // Open bow again
        onView(withRecyclerView(R.id.recyclerView).atPosition(0))
                .perform(click());

        // Add sight mark 15m: 3
        onView(withId(R.id.addButton))
                .perform(nestedScrollTo(), click());
        onView(withNestedRecyclerView(R.id.sightMarks).atPositionOnView(2, R.id.sightSetting))
                .perform(nestedScrollTo(), replaceText("3"), closeSoftKeyboard());
        onView(withNestedRecyclerView(R.id.sightMarks).atPositionOnView(2, R.id.distance))
                .perform(nestedScrollTo(), click());
        onView(allOf(withId(R.id.recyclerView), isDisplayed()))
                .perform(actionOnItem(hasDescendant(withText("15m")), click()));

        // "Accidentally delete" a sight mark and undo it
        onView(withNestedRecyclerView(R.id.sightMarks).atPositionOnView(0, R.id.removeSightSetting))
                .perform(nestedScrollTo(), click());
        onView(withId(R.id.snackbar_text))
                .check(matches(withText(R.string.sight_setting_removed)));
        onView(withId(R.id.snackbar_action)).perform(click());

        save();

        // Make sure changes have been saved and are sorted correctly
        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(0, R.id.details))
                .check(matches(allOf(containsStringRes(R.string.recurve_bow),
                        withText(containsString("10m: 2\n15m: 3\n18m: 1")))));

        // Open bow for edit via CAB
        onView(withRecyclerView(R.id.recyclerView).atPosition(0))
                .perform(longClick());
        clickContextualActionBarItem(R.id.action_edit, R.string.edit);

        onView(withNestedRecyclerView(R.id.sightMarks).atPositionOnView(0, R.id.removeSightSetting))
                .perform(nestedScrollTo(), click());

        onView(withId(R.id.snackbar_text))
                .check(matches(withText(R.string.sight_setting_removed)));

        save();

        onView(withRecyclerView(R.id.recyclerView).atPositionOnView(0, R.id.details))
                .check(matches(allOf(containsStringRes(R.string.recurve_bow),
                        withText(containsString("15m: 3\n18m: 1")))))
                .check(matches(not(withText(containsString("10m")))));
    }
}
