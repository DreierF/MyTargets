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

package de.dreier.mytargets.features.training


import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers.hasDescendant
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.runner.AndroidJUnit4
import de.dreier.mytargets.R
import de.dreier.mytargets.features.training.details.TrainingFragment
import de.dreier.mytargets.shared.models.db.Training
import de.dreier.mytargets.test.base.UITestBase
import de.dreier.mytargets.test.utils.matchers.RecyclerViewMatcher.Companion.withRecyclerView
import de.dreier.mytargets.test.utils.rules.SimpleDbTestRule
import org.hamcrest.Matchers.containsString
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import java.util.*

@Ignore
@RunWith(AndroidJUnit4::class)
class TrainingActivityTest : UITestBase() {

    private val activityTestRule = IntentsTestRule(
            TrainingActivity::class.java, true, false)
    @get:Rule
    val rule = RuleChain.outerRule(SimpleDbTestRule())
            .around(activityTestRule)

    @Test
    fun navigation() {
        val trainings = Training.all
        Collections.sort(trainings, Collections.reverseOrder())
        val training = trainings[0]
        activityTestRule.launchActivity(TrainingFragment.getIntent(training).build())

        //        intending(isInternal())
        //                .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));

        onView(withRecyclerView(R.id.recyclerView).atPosition(0))
                .check(matches(hasDescendant(withText(containsString("50m")))))
                .check(matches(hasDescendant(withText("212/360"))))

        onView(withRecyclerView(R.id.recyclerView).atPosition(1))
                .check(matches(hasDescendant(withText(containsString("30m")))))
                .check(matches(hasDescendant(withText("203/360"))))

        //        onView(withRecyclerView(R.id.recyclerView).atPosition(1))
        //                .perform(click());
        //        intended(allOf(hasClass(RoundActivity.class),
        //                hasExtra(RoundFragment.ROUND_ID, training.loadRounds().get(1).getId())));
        //
        //        clickActionBarItem(R.id.action_statistics, R.string.statistic);
        //        intended(allOf(hasClass(StatisticsActivity.class),
        //                hasLongArrayExtra(StatisticsActivity.ROUND_IDS, training.loadRounds()
        //                        .map(Round::getId)
        //                        .toSet())));

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
