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

import android.app.Activity;
import android.app.Instrumentation;
import android.support.annotation.NonNull;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import de.dreier.mytargets.R;
import de.dreier.mytargets.features.arrows.EditArrowActivity;
import de.dreier.mytargets.features.arrows.EditArrowFragment;
import de.dreier.mytargets.features.bows.EditBowActivity;
import de.dreier.mytargets.features.bows.EditBowFragment;
import de.dreier.mytargets.features.settings.SettingsActivity;
import de.dreier.mytargets.features.settings.SettingsManager;
import de.dreier.mytargets.features.statistics.StatisticsActivity;
import de.dreier.mytargets.features.timer.TimerActivity;
import de.dreier.mytargets.features.training.TrainingActivity;
import de.dreier.mytargets.features.training.edit.EditTrainingActivity;
import de.dreier.mytargets.features.training.edit.EditTrainingFragment;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.EBowType;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.db.Arrow;
import de.dreier.mytargets.shared.models.db.Bow;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.shared.streamwrapper.Stream;
import de.dreier.mytargets.shared.targets.models.WAFull;
import de.dreier.mytargets.shared.views.TargetViewBase.EInputMethod;
import de.dreier.mytargets.test.base.UITestBase;
import de.dreier.mytargets.test.utils.rules.SimpleDbTestRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static de.dreier.mytargets.base.fragments.EditableListFragmentBase.ITEM_ID;
import static de.dreier.mytargets.features.statistics.StatisticsActivity.ROUND_IDS;
import static de.dreier.mytargets.test.utils.matchers.IntentMatcher.hasClass;
import static de.dreier.mytargets.test.utils.matchers.IntentMatcher.hasLongArrayExtra;
import static de.dreier.mytargets.test.utils.matchers.RecyclerViewMatcher.withRecyclerView;
import static de.dreier.mytargets.test.utils.matchers.ViewMatcher.clickFabSpeedDialItem;
import static de.dreier.mytargets.test.utils.matchers.ViewMatcher.supportFab;
import static org.hamcrest.CoreMatchers.allOf;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest extends UITestBase {

    @NonNull
    private IntentsTestRule<MainActivity> activityTestRule = new IntentsTestRule<>(
            MainActivity.class);
    @Rule
    public final RuleChain rule = RuleChain.outerRule(new SimpleDbTestRule())
            .around(activityTestRule);

    @Before
    public void setUp() {
        SettingsManager.INSTANCE
                .setTarget(new Target(WAFull.Companion.getID(), 0, new Dimension(122, Dimension.Unit.CENTIMETER)));
        SettingsManager.INSTANCE.setDistance(new Dimension(50, Dimension.Unit.METER));
        SettingsManager.INSTANCE.setIndoor(false);
        SettingsManager.INSTANCE.setInputMethod(EInputMethod.PLOTTING);
        SettingsManager.INSTANCE.setTimerEnabled(false);
        SettingsManager.INSTANCE.setShotsPerEnd(3);

        intending(isInternal())
                .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
    }

    @Test
    public void navigation() {
        CountingIdlingResource mainActivityIdlingResource = activityTestRule.getActivity()
                .getEspressoIdlingResourceForMainActivity();

        IdlingRegistry.getInstance().register(mainActivityIdlingResource);

        // newTraining_freeTraining
        onView(withId(R.id.action_trainings)).perform(click());

        clickFabSpeedDialItem(R.id.fab1);

        intended(allOf(hasClass(EditTrainingActivity.class),
                hasAction(EditTrainingFragment.Companion.getCREATE_FREE_TRAINING_ACTION())));
        onView(withId(R.id.action_trainings)).perform(click());

        // newTraining_withStandardRound
        clickFabSpeedDialItem(R.id.fab2);
        intended(allOf(hasClass(EditTrainingActivity.class),
                hasAction(EditTrainingFragment.Companion
                        .getCREATE_TRAINING_WITH_STANDARD_ROUND_ACTION())));
        onView(withId(R.id.action_trainings)).perform(click());

        // openTraining
        onView(withRecyclerView(R.id.recyclerView).atPosition(1))
                .perform(click());

        final Training firstTraining = Stream.of(Training.Companion.getAll())
                .sorted(Collections.reverseOrder())
                .findFirstOrNull();

        intended(allOf(hasClass(TrainingActivity.class),
                hasExtra(Companion.getITEM_ID(), firstTraining.getId())));

        // openStatistics_allTrainings
        clickActionBarItem(R.id.action_statistics, R.string.statistic);
        intended(hasClass(StatisticsActivity.class));

        Set<Long> expectedRoundIds = Stream.of(Training.Companion.getAll())
                .flatMap(t -> Stream.of(t.loadRounds()))
                .map(Round::getId)
                .toSet();
        intended(allOf(hasClass(StatisticsActivity.class),
                hasLongArrayExtra(Companion.getROUND_IDS(), expectedRoundIds)));

        // openStatistics_selectedTrainings
        // Start Action mode with training 0
        onView(withRecyclerView(R.id.recyclerView).atPosition(1))
                .perform(longClick());

        // Open last month
        onView(withRecyclerView(R.id.recyclerView).atPosition(2))
                .perform(click());
        // Select training 1 and 2
        onView(withRecyclerView(R.id.recyclerView).atPosition(3))
                .perform(click());
        onView(withRecyclerView(R.id.recyclerView).atPosition(4))
                .perform(longClick());
        // Close month
        onView(withRecyclerView(R.id.recyclerView).atPosition(2))
                .perform(click());
        // Deselect training 0
        onView(withRecyclerView(R.id.recyclerView).atPosition(1))
                .perform(click());
        clickContextualActionBarItem(R.id.action_statistics, R.string.statistic);

        final List<Training> trainings = Stream.of(Training.Companion.getAll())
                .sorted(Collections.reverseOrder())
                .toList();
        expectedRoundIds = Stream.of(trainings.get(1), trainings.get(2))
                .flatMap(t -> Stream.of(t.loadRounds()))
                .map(Round::getId)
                .toSet();

        intended(allOf(hasClass(StatisticsActivity.class),
                hasLongArrayExtra(Companion.getROUND_IDS(), expectedRoundIds)));

        // newBow_recurve
        onView(withId(R.id.action_bows)).perform(click());
        clickFabSpeedDialItem(R.id.fabBowRecurve);
        intended(allOf(hasClass(EditBowActivity.class),
                hasExtra(EditBowFragment.Companion.getBOW_TYPE(), EBowType.RECURVE_BOW.name())));

        // openBow
        onView(withRecyclerView(R.id.recyclerView).atPosition(0))
                .perform(click());
        final Bow firstBow = Stream.of(Bow.Companion.getAll()).sorted().findFirstOrNull();
        intended(allOf(hasClass(EditBowActivity.class),
                hasExtra(EditBowFragment.Companion.getBOW_ID(), firstBow.getId())));

        // newArrow
        onView(withId(R.id.action_arrows)).perform(click());
        onView(supportFab()).perform(click());
        intended(hasClass(EditArrowActivity.class));

        // openArrow
        onView(withRecyclerView(R.id.recyclerView).atPosition(0))
                .perform(click());
        final Arrow firstArrow = Stream.of(Arrow.Companion.getAll()).sorted().findFirstOrNull();
        intended(allOf(hasClass(EditArrowActivity.class),
                hasExtra(EditArrowFragment.Companion.getARROW_ID(), firstArrow.getId())));

        // Open settings
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navigationView))
                .perform(NavigationViewActions.navigateTo(R.id.nav_settings));

        intended(hasClass(SettingsActivity.class));

        // Open timer
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.navigationView))
                .perform(NavigationViewActions.navigateTo(R.id.nav_timer));
        intended(hasClass(TimerActivity.class));
    }
}
