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


import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.runner.AndroidJUnit4
import de.dreier.mytargets.features.scoreboard.ScoreboardActivity
import de.dreier.mytargets.shared.models.db.Training
import de.dreier.mytargets.test.base.UITestBase
import de.dreier.mytargets.test.utils.rules.SimpleDbTestRule
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import java.util.*

@Ignore
@RunWith(AndroidJUnit4::class)
class ScoreboardActivityTest : UITestBase() {

    private val activityTestRule = IntentsTestRule(
            ScoreboardActivity::class.java, true, false)
    @get:Rule
    val rule = RuleChain.outerRule(SimpleDbTestRule())
            .around(activityTestRule)

    @Test
    fun navigation() {
        val trainings = Training.all
        Collections.sort(trainings, Collections.reverseOrder())
        val (id) = trainings[0]
        activityTestRule.launchActivity(ScoreboardActivity.getIntent(id).build())

//        intending(anyOf(not(isInternal()), isInternal()))
//                .respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))
//
//        onView(withContentDescription(containsString("415/720")))
//                .check(matches(isDisplayed()))
//
//        clickActionBarItem(R.id.action_share, R.string.share)
//        intended(hasAction(Intent.ACTION_CHOOSER))
//
//        clickActionBarItem(R.id.action_settings, R.string.preferences)
//        intended(allOf(hasClass(SettingsActivity::class.java),
//                hasExtra(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, ESettingsScreens.SCOREBOARD.key)))
    }
}
