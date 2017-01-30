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


import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.test.base.UITestBase;
import de.dreier.mytargets.test.utils.rules.SimpleDbTestRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static de.dreier.mytargets.test.utils.matchers.MatcherUtils.withRecyclerView;
import static org.hamcrest.Matchers.containsString;

@RunWith(AndroidJUnit4.class)
public class TrainingActivityTest extends UITestBase {

    private IntentsTestRule<TrainingActivity> activityTestRule = new IntentsTestRule<>(
            TrainingActivity.class, true, false);
    @Rule
    public final RuleChain rule = RuleChain.outerRule(new SimpleDbTestRule())
            .around(activityTestRule);

    @Test
    public void navigation() {
        final List<Training> trainings = Training.getAll();
        Collections.sort(trainings, Collections.reverseOrder());
        Training training = trainings.get(0);
        activityTestRule.launchActivity(TrainingFragment.getIntent(training).build());

//        intending(isInternal())
//                .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));

        onView(withRecyclerView(R.id.recyclerView).atPosition(0))
                .check(matches(hasDescendant(withText(containsString("50m")))))
                .check(matches(hasDescendant(withText("212/360"))));

        onView(withRecyclerView(R.id.recyclerView).atPosition(1))
                .check(matches(hasDescendant(withText(containsString("30m")))))
                .check(matches(hasDescendant(withText("203/360"))));

//        onView(withRecyclerView(R.id.recyclerView).atPosition(1))
//                .perform(click());
//        intended(allOf(hasClass(RoundActivity.class),
//                hasExtra(RoundFragment.ROUND_ID, training.getRounds().get(1).getId())));
//
//        clickActionBarItem(R.id.action_statistics, R.string.statistic);
//        intended(allOf(hasClass(StatisticsActivity.class),
//                hasLongArrayExtra(StatisticsActivity.ROUND_IDS, Stream.of(training.getRounds())
//                        .map(Round::getId)
//                        .collect(Collectors.toSet()))));

        // TODO investigate why this crashes on travis
//        clickActionBarItem(R.id.action_scoreboard, R.string.scoreboard);
//        intended(allOf(hasClass(ScoreboardActivity.class),
//                hasExtra(ScoreboardActivity.TRAINING_ID, training.getId()),
//                hasExtra(ScoreboardActivity.ROUND_ID, -1L)));

//        onView(supportFab()).perform(click());
//        intended(allOf(hasClass(EditRoundActivity.class),
//                hasExtra(ITEM_ID, training.getId())));
    }
}
