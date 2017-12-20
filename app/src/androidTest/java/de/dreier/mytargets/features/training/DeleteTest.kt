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

package de.dreier.mytargets.features.training;

import android.support.annotation.NonNull;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import de.dreier.mytargets.R;
import de.dreier.mytargets.features.main.MainActivity;
import de.dreier.mytargets.test.base.UITestBase;
import de.dreier.mytargets.test.utils.rules.SimpleDbTestRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static de.dreier.mytargets.test.utils.assertions.RecyclerViewAssertions.itemCount;
import static de.dreier.mytargets.test.utils.matchers.MatcherUtils.matchToolbarTitle;
import static de.dreier.mytargets.test.utils.matchers.MatcherUtils.withToolbarTitle;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class DeleteTest extends UITestBase {

    @NonNull
    private ActivityTestRule activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public final RuleChain rule = RuleChain.outerRule(new SimpleDbTestRule())
            .around(activityTestRule);

    @Test
    public void testDeleteRound() throws Exception {
        // Expand july
        onView(allOf(withId(R.id.recyclerView), isDisplayed())).perform(
                actionOnItemAtPosition(2, click()));

        // Delete and undo deletion
        onView(allOf(withId(R.id.recyclerView), isDisplayed())).perform(
                actionOnItemAtPosition(1, longClick()));
        onView(allOf(withId(R.id.recyclerView), isDisplayed())).perform(
                actionOnItemAtPosition(3, click()));
        clickContextualActionBarItem(R.id.action_delete, R.string.delete);
        onView(withId(R.id.snackbar_action)).perform(click());

        // Delete training
        onView(allOf(withId(R.id.recyclerView), isDisplayed())).perform(
                actionOnItemAtPosition(3, longClick()));
        clickContextualActionBarItem(R.id.action_delete, R.string.delete);
        final String trainingText = activityTestRule
                .getActivity().getResources().getQuantityString(R.plurals.training_deleted, 1, 1);
        onView(withId(R.id.snackbar_text)).check(matches(withText(trainingText)));

        // Open training
        onView(allOf(withId(R.id.recyclerView), isDisplayed())).perform(
                actionOnItemAtPosition(0, click()));
        onView(allOf(withId(R.id.recyclerView), isDisplayed())).perform(
                actionOnItemAtPosition(1, click()));
        onView(withText("Aug 22, 2016")).check(matches(isDisplayed()));

        onView(withId(R.id.recyclerView)).perform(
                actionOnItemAtPosition(0, longClick()));
        clickContextualActionBarItem(R.id.action_delete, R.string.delete);

        // Open round
        onView(withId(R.id.recyclerView)).perform(
                actionOnItemAtPosition(0, click()));

        // Delete ends and undo
        onView(withId(R.id.recyclerView)).perform(
                actionOnItemAtPosition(2, longClick()));
        onView(withId(R.id.recyclerView)).perform(
                actionOnItemAtPosition(3, click()));
        clickContextualActionBarItem(R.id.action_delete, R.string.delete);
        final String endsText = activityTestRule
                .getActivity().getResources().getQuantityString(R.plurals.passe_deleted, 2, 2);
        onView(withId(R.id.snackbar_text)).check(matches(withText(endsText)));
        onView(withId(R.id.snackbar_action)).perform(click());

        // Delete ends
        onView(withId(R.id.recyclerView)).perform(
                actionOnItemAtPosition(1, longClick()));
        onView(withId(R.id.recyclerView)).perform(actionOnItemAtPosition(3, click()));
        clickContextualActionBarItem(R.id.action_delete, R.string.delete);

        onView(withId(R.id.snackbar_text)).check(matches(withText(endsText)));

        pressBack();

        onView(withId(R.id.recyclerView)).perform(
                actionOnItemAtPosition(0, click()));

        onView(withId(R.id.recyclerView)).check(itemCount(is(4)));

        pressBack();
        onView(allOf(isAssignableFrom(Toolbar.class), withId(R.id.toolbar)))
                .check(ViewAssertions.matches(withToolbarTitle(is(getString(R.string.training)))));
        pressBack();
        matchToolbarTitle(getString(R.string.my_targets));
    }
}
