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


import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import java.util.Random;

import de.dreier.mytargets.R;
import de.dreier.mytargets.UITestBase;
import de.dreier.mytargets.managers.SettingsManager;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.models.Training;
import de.dreier.mytargets.shared.views.TargetViewBase;
import de.dreier.mytargets.utils.rules.DbTestRuleBase;

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
            StandardRound standardRound = standardRoundDataSource.get(32);

            Training training = insertDefaultTraining(standardRound, generator);

            round1 = new Round();
            round1.trainingId = training.getId();
            round1.info = standardRound.rounds.get(0);
            round1.info.target = round1.info.targetTemplate;
            round1.comment = "";
            roundDataSource.update(round1);

            Round round2 = new Round();
            round2.trainingId = training.getId();
            round2.info = standardRound.rounds.get(1);
            round2.info.target = round2.info.targetTemplate;
            round2.comment = "";
            roundDataSource.update(round2);
        }
    }).around(activityTestRule);
    private Round round1;

    @Before
    public void setUp() {
        SettingsManager.setInputMethod(TargetViewBase.EInputMethod.PLOTTING);
    }

    @Test
    public void inputActivityTest() {
        Intent i = new Intent();
        i.putExtra(InputActivity.TRAINING_ID, round1.trainingId);
        i.putExtra(InputActivity.ROUND_ID, round1.getId());
        i.putExtra(InputActivity.END_INDEX, 0);
        activityTestRule.launchActivity(i);

        onView(allOf(withContentDescription("X"), withId(R.id.targetView)))
                .check(doesNotExist());

        clickActionBarItem(R.id.action_show_sidebar, R.string.keyboard);

        //onView(withContentDescription("X"))
        //        .check(matches(isDisplayed()));
    }
}
