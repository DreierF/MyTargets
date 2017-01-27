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


import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.preference.PreferenceFragmentCompat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.features.scoreboard.ScoreboardActivity;
import de.dreier.mytargets.features.settings.ESettingsScreens;
import de.dreier.mytargets.features.settings.SettingsActivity;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.test.base.UITestBase;
import de.dreier.mytargets.test.utils.rules.SimpleDbTestRule;

import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.not;

@RunWith(AndroidJUnit4.class)
public class ScoreboardActivityTest extends UITestBase {

    private IntentsTestRule<ScoreboardActivity> activityTestRule = new IntentsTestRule<>(
            ScoreboardActivity.class, true, false);
    @Rule
    public final RuleChain rule = RuleChain.outerRule(new SimpleDbTestRule())
            .around(activityTestRule);

    @Test
    public void navigation() {
        final List<Training> trainings = Training.getAll();
        Collections.sort(trainings, Collections.reverseOrder());
        Training training = trainings.get(0);
        activityTestRule.launchActivity(ScoreboardActivity.getIntent(training.getId()).build());

        intending(anyOf(not(isInternal()), isInternal()))
                .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));

//        onView(withContentDescription(containsString("415/720")))
//                .check(matches(isDisplayed()));

        clickActionBarItem(R.id.action_share, R.string.share);
        intended(hasAction(Intent.ACTION_CHOOSER));

        clickActionBarItem(R.id.action_settings, R.string.preferences);
        intended(allOf(hasClass(SettingsActivity.class),
                hasExtra(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, ESettingsScreens.SCOREBOARD.getKey())));
    }
}
