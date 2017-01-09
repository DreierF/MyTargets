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

package de.dreier.mytargets.features.main;


import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import de.dreier.mytargets.R;
import de.dreier.mytargets.features.arrows.EditArrowActivity;
import de.dreier.mytargets.features.bows.EditBowActivity;
import de.dreier.mytargets.features.bows.EditBowFragment;
import de.dreier.mytargets.features.settings.SettingsActivity;
import de.dreier.mytargets.features.settings.SettingsManager;
import de.dreier.mytargets.features.statistics.StatisticsActivity;
import de.dreier.mytargets.features.training.edit.EditTrainingActivity;
import de.dreier.mytargets.features.training.edit.EditTrainingFragment;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.EBowType;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.targets.models.WAFull;
import de.dreier.mytargets.shared.views.TargetViewBase.EInputMethod;
import de.dreier.mytargets.test.base.UITestBase;
import de.dreier.mytargets.test.utils.rules.SimpleDbTestRule;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.matcher.ViewMatchers.Visibility.INVISIBLE;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static de.dreier.mytargets.test.utils.PermissionGranter.allowPermissionsIfNeeded;
import static de.dreier.mytargets.test.utils.actions.OrientationChangeAction.orientationLandscape;
import static de.dreier.mytargets.test.utils.actions.OrientationChangeAction.orientationPortrait;
import static org.hamcrest.CoreMatchers.allOf;

@RunWith(AndroidJUnit4.class)
public class MainActivityNavigationTest extends UITestBase {

    private IntentsTestRule<MainActivity> activityTestRule = new IntentsTestRule<>(
            MainActivity.class);

    @Rule
    public final RuleChain rule = RuleChain.outerRule(new SimpleDbTestRule())
            .around(activityTestRule);

    @Before
    public void setUp() {
        SettingsManager
                .setTarget(new Target(WAFull.ID, 0, new Dimension(122, Dimension.Unit.CENTIMETER)));
        SettingsManager.setDistance(new Dimension(50, Dimension.Unit.METER));
        SettingsManager.setIndoor(false);
        SettingsManager.setInputMethod(EInputMethod.PLOTTING);
        SettingsManager.setTimerEnabled(false);
        SettingsManager.setShotsPerEnd(3);
    }

    @Test
    public void navigationTest() {
        // Do settings work
        clickActionBarItem(R.id.action_preferences, R.string.preferences);
        intended(hasComponent(SettingsActivity.class.getName()));
        pressBack();

        clickActionBarItem(R.id.action_statistics, R.string.statistic);
        intended(hasComponent(StatisticsActivity.class.getName()));
        pressBack();

        // Does new free training work
        onView(matchFab()).perform(click());
        onView(withId(R.id.fab1)).perform(click());
        intended(allOf(hasComponent(EditTrainingActivity.class.getName()),
                hasAction(EditTrainingFragment.CREATE_FREE_TRAINING_ACTION)));
        allowPermissionsIfNeeded(activityTestRule.getActivity(), ACCESS_FINE_LOCATION);
        pressBack();

        // Does new training with standard round work
        onView(matchFab()).perform(click());
        onView(withId(R.id.fab2)).perform(click());
        intended(allOf(hasComponent(EditTrainingActivity.class.getName()),
                hasAction(EditTrainingFragment.CREATE_TRAINING_WITH_STANDARD_ROUND_ACTION)));
        pressBack();

        // TODO test with existing trainings, bows and arrows

        // Does new bow work
        onView(allOf(withText(R.string.bow), isDisplayed())).perform(click());
        onView(matchFab()).perform(click());
        onView(withId(R.id.fabBowRecurve)).perform(click());
        intended(allOf(hasComponent(EditBowActivity.class.getName()),
                hasExtra(EditBowFragment.BOW_TYPE, EBowType.RECURVE_BOW.name())));
        pressBack();

        // Does new arrow work
        onView(allOf(withText(R.string.arrow), isDisplayed())).perform(click());
        onView(allOf(withId(R.id.fab), isDisplayed())).perform(click());
        intended(hasComponent(EditArrowActivity.class.getName()));
    }

    @Test
    public void addTraining() throws InterruptedException {
        onView(withId(R.id.fab1)).check(matches(withEffectiveVisibility(INVISIBLE)));
        onView(matchFab()).perform(click());
        onView(withId(R.id.fab1)).perform(click());
        allowPermissionsIfNeeded(activityTestRule.getActivity(), ACCESS_FINE_LOCATION);
        clickActionBarItem(R.id.action_save, R.string.save);
        onView(isRoot()).perform(orientationLandscape(activityTestRule));
        navigateUp();
        onView(isRoot()).perform(orientationPortrait(activityTestRule));
        pressBack();
        pressBack();
    }
}