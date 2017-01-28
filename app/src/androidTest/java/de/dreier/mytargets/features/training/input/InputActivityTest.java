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


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

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
import de.dreier.mytargets.test.utils.rules.DbTestRuleBase;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class InputActivityTest extends UITestBase {

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
        SettingsManager.setInputMethod(TargetViewBase.EInputMethod.PLOTTING);
    }

    @Test
    public void inputActivityTest() {
        activityTestRule.launchActivity(InputActivity.getIntent(round1, 0).build());

        onView(allOf(withContentDescription("X"), withId(R.id.targetView)))
                .check(doesNotExist());

        //clickActionBarItem(R.id.action_keyboard, R.string.keyboard);

        //onView(withContentDescription("X")).check(matches(isDisplayed()));
    }
}
