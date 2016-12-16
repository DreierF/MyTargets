/*
 * Copyright (C) 2016 Florian Dreier
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

package de.dreier.mytargets.activities;


import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import de.dreier.mytargets.R;
import de.dreier.mytargets.UITestBase;
import de.dreier.mytargets.fragments.EditTrainingFragment;
import de.dreier.mytargets.managers.SettingsManager;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.targets.models.WAFull;
import de.dreier.mytargets.shared.views.TargetViewBase.EInputMethod;
import de.dreier.mytargets.utils.rules.SimpleDbTestRule;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.Visibility.INVISIBLE;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static de.dreier.mytargets.OrientationChangeAction.orientationLandscape;
import static de.dreier.mytargets.OrientationChangeAction.orientationPortrait;
import static de.dreier.mytargets.PermissionGranter.allowPermissionsIfNeeded;
import static org.hamcrest.CoreMatchers.allOf;

@RunWith(AndroidJUnit4.class)
public class MainActivityNavigationTest extends UITestBase {

    private IntentsTestRule activityTestRule = new IntentsTestRule<>(MainActivity.class);

    @Rule
    public final RuleChain rule = RuleChain.outerRule(new SimpleDbTestRule()).around(
            activityTestRule);

    @Before
    public void setUp() {
        SettingsManager
                .setTarget(new Target(WAFull.ID, 0, new Dimension(122, Dimension.Unit.CENTIMETER)));
        SettingsManager.setDistance(new Dimension(50, Dimension.Unit.METER));
        SettingsManager.setIndoor(false);
        SettingsManager.setInputMethod(EInputMethod.PLOTTING);
        SettingsManager.setTimerEnabled(false);
        SettingsManager.setArrowsPerEnd(3);
    }

    @Test
    public void appDoesStartUp() {
        onView(withText(R.string.my_targets)).check(matches(isDisplayed()));
    }

    // TODO test with existing trainings, bows and arrows

    @Test
    public void openSettings() {
        clickActionBarItem(R.id.action_preferences, R.string.preferences);
        intended(hasComponent(SettingsActivity.class.getName()));
        pressBack();
    }

    @Test
    public void openStatistics() {
        clickActionBarItem(R.id.action_statistics, R.string.statistic);
        intended(hasComponent(StatisticsActivity.class.getName()));
        pressBack();
    }

    @Test
    public void openFreeTraining() {
        onView(matchFab()).perform(click());
        onView(allOf(withId(R.id.fab1), withParent(withId(R.id.fab)))).perform(click());
        intended(
                allOf(hasComponent(SimpleFragmentActivityBase.EditTrainingActivity.class.getName()),
                        hasAction(EditTrainingFragment.CREATE_FREE_TRAINING_ACTION)));
        allowPermissionsIfNeeded(activityTestRule.getActivity(), ACCESS_FINE_LOCATION);
        pressBack();
    }

    @Test
    public void openTrainingWithStandardRound() {
        onView(matchFab()).perform(click());
        onView(allOf(withId(R.id.fab2), withParent(withId(R.id.fab)))).perform(click());
        intended(allOf(
                hasComponent(SimpleFragmentActivityBase.EditTrainingActivity.class.getName()),
                hasAction(EditTrainingFragment.CREATE_TRAINING_WITH_STANDARD_ROUND_ACTION)));
        pressBack();
    }

    @Test
    public void openNewArrowScreen() {
        onView(allOf(withText(R.string.arrow), isDisplayed())).perform(click());
        onView(allOf(withId(R.id.fab), isDisplayed())).perform(click());
        intended(hasComponent(SimpleFragmentActivityBase.EditArrowActivity.class.getName()));
    }

    @Test
    public void openNewBowScreen() {
        onView(allOf(withText(R.string.bow), isDisplayed())).perform(click());
        onView(allOf(withId(R.id.fab), isDisplayed())).perform(click());
        intended(hasComponent(SimpleFragmentActivityBase.EditBowActivity.class.getName()));
        pressBack();
    }

    @Test
    public void addTraining() throws InterruptedException {
        onView(withId(R.id.fab1)).check(matches(withEffectiveVisibility(INVISIBLE)));
        onView(matchFab()).perform(click());
        onView(allOf(withId(R.id.fab1), hasSibling(matchFab()))).perform(click());
        allowPermissionsIfNeeded(activityTestRule.getActivity(), ACCESS_FINE_LOCATION);
        clickActionBarItem(R.id.action_save, R.string.save);
        onView(isRoot()).perform(orientationLandscape(activityTestRule));
        navigateUp();
        onView(isRoot()).perform(orientationPortrait(activityTestRule));
        pressBack();
        pressBack();
    }
}