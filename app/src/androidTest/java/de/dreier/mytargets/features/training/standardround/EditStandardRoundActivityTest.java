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

package de.dreier.mytargets.features.training.standardround;


import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.dreier.mytargets.R;
import de.dreier.mytargets.features.settings.SettingsManager;
import de.dreier.mytargets.features.training.edit.EditTrainingActivity;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.targets.models.WAFull;
import de.dreier.mytargets.test.base.UITestBase;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItem;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static de.dreier.mytargets.features.training.edit.EditTrainingFragment.CREATE_TRAINING_WITH_STANDARD_ROUND_ACTION;
import static de.dreier.mytargets.shared.models.Dimension.Unit.CENTIMETER;
import static de.dreier.mytargets.shared.models.Dimension.Unit.METER;
import static de.dreier.mytargets.test.utils.PermissionGranter.allowPermissionsIfNeeded;
import static de.dreier.mytargets.test.utils.matchers.MatcherUtils.withRecyclerView;
import static de.dreier.mytargets.test.utils.matchers.ParentViewMatcher.isNestedChildOfView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;

@RunWith(AndroidJUnit4.class)
public class EditStandardRoundActivityTest extends UITestBase {

    private static final String TAG = "EditStandardRoundActivi";
    @Rule
    public IntentsTestRule<EditTrainingActivity> activityTestRule = new IntentsTestRule<>(
            EditTrainingActivity.class, true, false);

    @Before
    public void setUp() {
        SettingsManager.setTarget(new Target(WAFull.ID, 0, new Dimension(122, CENTIMETER)));
        SettingsManager.setDistance(new Dimension(50, METER));
        SettingsManager.setTimerEnabled(false);
        SettingsManager.setShotsPerEnd(3);
        SettingsManager.setEndCount(10);
        SettingsManager.setDistance(new Dimension(10, METER));
    }

    @Test
    public void editStandardRoundActivity() {
        Intent intent = new Intent();
        intent.setAction(CREATE_TRAINING_WITH_STANDARD_ROUND_ACTION);
        activityTestRule.launchActivity(intent);
        int i = 0;
        allowPermissionsIfNeeded(activityTestRule.getActivity(), ACCESS_FINE_LOCATION);
        Log.d(TAG, "editStandardRoundActivity: " + (i++));

        onView(withId(R.id.standardRound)).perform(scrollTo(), click());
        Log.d(TAG, "editStandardRoundActivity: " + (i++));

        onView(withId(R.id.fab)).perform(click());
        Log.d(TAG, "editStandardRoundActivity: " + (i++));

        onView(withId(R.id.distance)).perform(nestedScrollTo(), click());
        Log.d(TAG, "editStandardRoundActivity: " + (i++));
        onView(allOf(withId(R.id.recyclerView), isDisplayed()))
                .perform(actionOnItem(hasDescendant(withText("20m")), click()));
        Log.d(TAG, "editStandardRoundActivity: " + (i++));

        onView(withId(R.id.target)).perform(nestedScrollTo(), click());
        Log.d(TAG, "editStandardRoundActivity: " + (i++));
        onView(withId(R.id.recyclerView))
                .perform(actionOnItem(hasDescendant(withText(R.string.wa_danage_6_spot)), click()));
        Log.d(TAG, "editStandardRoundActivity: " + (i++));
        navigateUp();

        Log.d(TAG, "editStandardRoundActivity: " + (i++));
        onView(withId(R.id.addButton)).perform(nestedScrollTo(), click());
        Log.d(TAG, "editStandardRoundActivity: " + (i++));

        onView(withRecyclerView(R.id.rounds).atPositionOnView(1, R.id.distance))
                .perform(nestedScrollTo(), click());
        Log.d(TAG, "editStandardRoundActivity: " + (i++));
        onView(allOf(withId(R.id.recyclerView), isDisplayed()))
                .perform(actionOnItem(hasDescendant(withText("15m")), click()));
        Log.d(TAG, "editStandardRoundActivity: " + (i++));

        onView(allOf(withId(R.id.number_increment), isNestedChildOfView(withId(R.id.shotCount)),
                isNestedChildOfView(withRecyclerView(R.id.rounds).atPosition(1))))
                .perform(nestedScrollTo(), click(), click(), click());
        Log.d(TAG, "editStandardRoundActivity: " + (i++));

        onView(allOf(withId(R.id.number_decrement), isNestedChildOfView(withId(R.id.endCount)),
                isNestedChildOfView(withRecyclerView(R.id.rounds).atPosition(1))))
                .perform(nestedScrollTo(), click(), click(), click(), click(), click());
        Log.d(TAG, "editStandardRoundActivity: " + (i++));

        save();
        Log.d(TAG, "editStandardRoundActivity: " + (i++));

        onView(withId(R.id.standardRound))
                .check(matches(hasDescendant(withText(R.string.custom_round))));
        Log.d(TAG, "editStandardRoundActivity: " + (i++));

        onView(withId(R.id.standardRound))
                .check(matches(hasDescendant(withText(
                        allOf(startsWith("20m: 10 × 3"), containsString("15m: 5 × 6"))))));
        Log.d(TAG, "editStandardRoundActivity: " + (i++));
    }
}
