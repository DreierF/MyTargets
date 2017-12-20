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

package de.dreier.mytargets.features.main

import android.app.Activity
import android.app.Instrumentation
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.longClick
import android.support.test.espresso.contrib.DrawerActions
import android.support.test.espresso.contrib.NavigationViewActions
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.Intents.intending
import android.support.test.espresso.intent.matcher.IntentMatchers.*
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.runner.AndroidJUnit4
import de.dreier.mytargets.R
import de.dreier.mytargets.base.fragments.EditableListFragmentBase.Companion.ITEM_ID
import de.dreier.mytargets.features.arrows.EditArrowActivity
import de.dreier.mytargets.features.arrows.EditArrowFragment
import de.dreier.mytargets.features.bows.EditBowActivity
import de.dreier.mytargets.features.bows.EditBowFragment
import de.dreier.mytargets.features.settings.SettingsActivity
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.features.statistics.StatisticsActivity
import de.dreier.mytargets.features.statistics.StatisticsActivity.Companion.ROUND_IDS
import de.dreier.mytargets.features.timer.TimerActivity
import de.dreier.mytargets.features.training.TrainingActivity
import de.dreier.mytargets.features.training.edit.EditTrainingActivity
import de.dreier.mytargets.features.training.edit.EditTrainingFragment
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.EBowType
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.models.db.Arrow
import de.dreier.mytargets.shared.models.db.Bow
import de.dreier.mytargets.shared.models.db.Training
import de.dreier.mytargets.shared.targets.models.WAFull
import de.dreier.mytargets.shared.views.TargetViewBase.EInputMethod
import de.dreier.mytargets.test.base.UITestBase
import de.dreier.mytargets.test.utils.matchers.IntentMatcher.hasClass
import de.dreier.mytargets.test.utils.matchers.IntentMatcher.hasLongArrayExtra
import de.dreier.mytargets.test.utils.matchers.RecyclerViewMatcher.Companion.withRecyclerView
import de.dreier.mytargets.test.utils.matchers.ViewMatcher.clickFabSpeedDialItem
import de.dreier.mytargets.test.utils.matchers.ViewMatcher.supportFab
import de.dreier.mytargets.test.utils.rules.SimpleDbTestRule
import org.hamcrest.CoreMatchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class MainActivityTest : UITestBase() {

    private val activityTestRule = IntentsTestRule(
            MainActivity::class.java)
    @Rule
    val rule = RuleChain.outerRule(SimpleDbTestRule())
            .around(activityTestRule)

    @Before
    fun setUp() {
        SettingsManager
                .target = Target(WAFull.ID, 0, Dimension(122f, Dimension.Unit.CENTIMETER))
        SettingsManager.distance = Dimension(50f, Dimension.Unit.METER)
        SettingsManager.indoor = false
        SettingsManager.inputMethod = EInputMethod.PLOTTING
        SettingsManager.timerEnabled = false
        SettingsManager.shotsPerEnd = 3

        intending(isInternal())
                .respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))
    }

    @Test
    fun navigation() {
        val mainActivityIdlingResource = activityTestRule.activity
                .espressoIdlingResourceForMainActivity

        IdlingRegistry.getInstance().register(mainActivityIdlingResource)

        // newTraining_freeTraining
        onView(withId(R.id.action_trainings)).perform(click())

        clickFabSpeedDialItem(R.id.fab1)

        intended(allOf(hasClass(EditTrainingActivity::class.java),
                hasAction(EditTrainingFragment.CREATE_FREE_TRAINING_ACTION)))
        onView(withId(R.id.action_trainings)).perform(click())

        // newTraining_withStandardRound
        clickFabSpeedDialItem(R.id.fab2)
        intended(allOf(hasClass(EditTrainingActivity::class.java),
                hasAction(EditTrainingFragment
                        .CREATE_TRAINING_WITH_STANDARD_ROUND_ACTION)))
        onView(withId(R.id.action_trainings)).perform(click())

        // openTraining
        onView(withRecyclerView(R.id.recyclerView).atPosition(1))
                .perform(click())

        val firstTraining = Training.all
                .sortedWith(Collections.reverseOrder())
                .firstOrNull()

        intended(allOf(hasClass(TrainingActivity::class.java),
                hasExtra(ITEM_ID, firstTraining!!.id)))

        // openStatistics_allTrainings
        clickActionBarItem(R.id.action_statistics, R.string.statistic)
        intended(hasClass(StatisticsActivity::class.java))

        var expectedRoundIds = Training.all
                .flatMap { t -> t.loadRounds()!! }
                .map { it.id!! }
                .toSet()
        intended(allOf(hasClass(StatisticsActivity::class.java),
                hasLongArrayExtra(ROUND_IDS, expectedRoundIds)))

        // openStatistics_selectedTrainings
        // Start Action mode with training 0
        onView(withRecyclerView(R.id.recyclerView).atPosition(1))
                .perform(longClick())

        // Open last month
        onView(withRecyclerView(R.id.recyclerView).atPosition(2))
                .perform(click())
        // Select training 1 and 2
        onView(withRecyclerView(R.id.recyclerView).atPosition(3))
                .perform(click())
        onView(withRecyclerView(R.id.recyclerView).atPosition(4))
                .perform(longClick())
        // Close month
        onView(withRecyclerView(R.id.recyclerView).atPosition(2))
                .perform(click())
        // Deselect training 0
        onView(withRecyclerView(R.id.recyclerView).atPosition(1))
                .perform(click())
        clickContextualActionBarItem(R.id.action_statistics, R.string.statistic)

        val trainings = Training.all
                .sortedWith(Collections.reverseOrder())
        expectedRoundIds = listOf(trainings[1], trainings[2])
                .flatMap { t -> t.loadRounds()!! }
                .map { it.id!! }
                .toSet()

        intended(allOf(hasClass(StatisticsActivity::class.java),
                hasLongArrayExtra(ROUND_IDS, expectedRoundIds)))

        // newBow_recurve
        onView(withId(R.id.action_bows)).perform(click())
        clickFabSpeedDialItem(R.id.fabBowRecurve)
        intended(allOf(hasClass(EditBowActivity::class.java),
                hasExtra(EditBowFragment.BOW_TYPE, EBowType.RECURVE_BOW.name)))

        // openBow
        onView(withRecyclerView(R.id.recyclerView).atPosition(0))
                .perform(click())
        val firstBow = Bow.all.sorted().firstOrNull()
        intended(allOf(hasClass(EditBowActivity::class.java),
                hasExtra<Long>(EditBowFragment.BOW_ID, firstBow!!.id)))

        // newArrow
        onView(withId(R.id.action_arrows)).perform(click())
        onView(supportFab()).perform(click())
        intended(hasClass(EditArrowActivity::class.java))

        // openArrow
        onView(withRecyclerView(R.id.recyclerView).atPosition(0))
                .perform(click())
        val firstArrow = Arrow.all.sorted().firstOrNull()
        intended(allOf(hasClass(EditArrowActivity::class.java),
                hasExtra<Long>(EditArrowFragment.ARROW_ID, firstArrow!!.id)))

        // Open settings
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.navigationView))
                .perform(NavigationViewActions.navigateTo(R.id.nav_settings))

        intended(hasClass(SettingsActivity::class.java))

        // Open timer
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.navigationView))
                .perform(NavigationViewActions.navigateTo(R.id.nav_timer))
        intended(hasClass(TimerActivity::class.java))
    }
}
