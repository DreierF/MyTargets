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
package de.dreier.mytargets.features.training.input;


import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiObjectNotFoundException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import java.util.Random;

import de.dreier.mytargets.R;
import de.dreier.mytargets.features.settings.SettingsManager;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.StandardRound;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.shared.views.TargetViewBase;
import de.dreier.mytargets.test.base.UITestBase;
import de.dreier.mytargets.test.utils.actions.TargetViewActions;
import de.dreier.mytargets.test.utils.rules.DbTestRuleBase;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static de.dreier.mytargets.test.utils.VirtualViewUtil.assertVirtualViewExists;
import static de.dreier.mytargets.test.utils.VirtualViewUtil.assertVirtualViewNotExists;
import static de.dreier.mytargets.test.utils.VirtualViewUtil.clickVirtualView;
import static de.dreier.mytargets.test.utils.matchers.ViewMatcher.clickOnPreference;

@RunWith(AndroidJUnit4.class)
public class InputActivityTest extends UITestBase {

    @NonNull
    private ActivityTestRule<InputActivity> activityTestRule = new ActivityTestRule<>(
            InputActivity.class, true, false);

    @Rule
    public final RuleChain rule = RuleChain.outerRule(new DbTestRuleBase() {
        @Override
        protected void addDatabaseContent() {
            Random generator = new Random(3435);
            StandardRound standardRound = StandardRound.get(32L);

            Training training = saveDefaultTraining(standardRound.getId(), generator);

            round1 = new Round(standardRound.getRounds().get(0));
            round1.trainingId = training.getId();
            round1.comment = "";
            round1.save();

            Round round2 = new Round(standardRound.getRounds().get(1));
            round2.trainingId = training.getId();
            round2.comment = "";
            round2.save();
        }
    }).around(activityTestRule);

    private Round round1;

    @Before
    public void setUp() {
        SettingsManager.setInputMethod(TargetViewBase.EInputMethod.KEYBOARD);
        SettingsManager.setTimerEnabled(false);
    }

    @Test
    public void inputActivityTest() throws UiObjectNotFoundException {
        activityTestRule.launchActivity(InputActivity.getIntent(round1, 0).build());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        clickVirtualView("10");
        assertVirtualViewExists("Shot 1: 10");

        clickActionBarItem(R.id.action_settings, R.string.preferences);
        clickOnPreference(R.string.keyboard_enabled);
        pressBack();
        // Wait for keyboard animation to finish
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //assertVirtualViewNotExists("10");

        onView(withId(R.id.targetViewContainer))
                .perform(TargetViewActions.clickTarget(0, 0));
        assertVirtualViewExists("Shot 2: X");

        clickVirtualView("Backspace");
        assertVirtualViewNotExists("Shot 2: X");

        onView(withId(R.id.targetViewContainer))
                .perform(TargetViewActions.clickTarget(0.1f, 0.2f));
        assertVirtualViewExists("Shot 2: 8");

        onView(withId(R.id.targetViewContainer))
                .perform(TargetViewActions.clickTarget(0, 0));
        assertVirtualViewExists("Shot 3: X");

        onView(withId(R.id.targetViewContainer))
                .perform(TargetViewActions.clickTarget(-0.9f, -0.9f));
        assertVirtualViewExists("Shot 4: M");

        onView(withId(R.id.targetViewContainer))
                .perform(TargetViewActions.clickTarget(0, 0));
        assertVirtualViewExists("Shot 5: X");

        onView(withId(R.id.targetViewContainer))
                .perform(TargetViewActions.clickTarget(0, 0));
        assertVirtualViewExists("Shot 6: X");

        onView(withId(R.id.next)).perform(click());
    }
}
