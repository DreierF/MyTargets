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

package de.dreier.mytargets.features.arrow;


import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import de.dreier.mytargets.R;
import de.dreier.mytargets.features.arrows.ArrowActivity;
import de.dreier.mytargets.features.arrows.EditArrowActivity;
import de.dreier.mytargets.features.training.edit.EditTrainingActivity;
import de.dreier.mytargets.features.training.edit.EditTrainingFragment;
import de.dreier.mytargets.test.base.UITestBase;
import de.dreier.mytargets.test.utils.rules.EmptyDbTestRule;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static de.dreier.mytargets.test.utils.PermissionGranter.allowPermissionsIfNeeded;
import static de.dreier.mytargets.test.utils.matchers.ParentViewMatcher.isNestedChildOfView;
import static de.dreier.mytargets.test.utils.matchers.RecyclerViewMatcher.withRecyclerView;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class ArrowSelectorTest extends UITestBase {

    private IntentsTestRule<EditTrainingActivity> activityTestRule = new IntentsTestRule<>(
            EditTrainingActivity.class, true, false);

    @Rule
    public final RuleChain rule = RuleChain.outerRule(new EmptyDbTestRule())
            .around(activityTestRule);

    @Test
    public void freeTrainingArrowSelectionTest() {
        arrowSelectionTest(EditTrainingFragment.CREATE_FREE_TRAINING_ACTION);
    }

    @Test
    public void standardRoundArrowSelectionTest() {
        arrowSelectionTest(EditTrainingFragment.CREATE_TRAINING_WITH_STANDARD_ROUND_ACTION);
    }

    private void arrowSelectionTest(String type) {
        Intent intent = new Intent();
        intent.setAction(type);
        activityTestRule.launchActivity(intent);
        //allowPermissionsIfNeeded(activityTestRule.getActivity(), ACCESS_FINE_LOCATION);

        onView(withText(R.string.add_arrow)).perform(nestedScrollTo(), click());
        intended(hasComponent(EditArrowActivity.class.getName()));
        save();

        onView(allOf(withId(R.id.name), isNestedChildOfView(withId(R.id.arrow)), isDisplayed()))
                .check(matches(withText(R.string.my_arrow)));

        // Check if arrow selection opens
        onView(withId(R.id.arrow)).perform(nestedScrollTo(), click());
        intended(hasComponent(ArrowActivity.class.getName()));
        onView(withRecyclerView(R.id.recyclerView).atPosition(0))
                .check(matches(hasDescendant(withText(R.string.my_arrow))));
        navigateUp();
    }
}
