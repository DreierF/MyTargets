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
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

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
import de.dreier.mytargets.shared.targets.models.WAFull;
import de.dreier.mytargets.shared.views.TargetViewBase.EInputMethod;
import de.dreier.mytargets.test.base.UITestBase;
import de.dreier.mytargets.test.utils.matchers.MatcherUtils;
import de.dreier.mytargets.test.utils.rules.SimpleDbTestRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static de.dreier.mytargets.base.fragments.EditableListFragmentBase.ITEM_ID;
import static de.dreier.mytargets.features.statistics.StatisticsActivity.ROUND_IDS;
import static de.dreier.mytargets.test.utils.matchers.MatcherUtils.withRecyclerView;
import static de.dreier.mytargets.test.utils.matchers.ParentViewMatcher.isNestedChildOfView;
import static org.hamcrest.CoreMatchers.allOf;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest extends UITestBase {

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

        intending(isInternal())
                .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
    }

    @Test
    public void navigation() {
        // newTraining_freeTraining
        switchToTab(R.string.training);
        onView(matchFabMenu()).perform(click());
        onView(withId(R.id.fab1)).perform(click());
        intended(allOf(hasClass(EditTrainingActivity.class),
                hasAction(EditTrainingFragment.CREATE_FREE_TRAINING_ACTION)));
        switchToTab(R.string.training);

        // newTraining_withStandardRound
        onView(matchFabMenu()).perform(click());
        onView(withId(R.id.fab2)).perform(click());
        intended(allOf(hasClass(EditTrainingActivity.class),
                hasAction(EditTrainingFragment.CREATE_TRAINING_WITH_STANDARD_ROUND_ACTION)));
        switchToTab(R.string.training);

        // openTraining
        onView(withRecyclerView(R.id.recyclerView).atPosition(1))
                .perform(click());

        final Training firstTraining = Stream.of(Training.getAll())
                .sorted(Collections.reverseOrder())
                .findFirst().get();

        intended(allOf(hasClass(TrainingActivity.class),
                hasExtra(ITEM_ID, firstTraining.getId())));

        // openStatistics_allTrainings
        clickActionBarItem(R.id.action_statistics, R.string.statistic);
        intended(hasClass(StatisticsActivity.class));

        Set<Long> expectedRoundIds = Stream.of(Training.getAll())
                .flatMap(t -> Stream.of(t.getRounds()))
                .map(Round::getId)
                .collect(Collectors.toSet());
        intended(allOf(hasClass(StatisticsActivity.class),
                MatcherUtils.hasLongArrayExtra(ROUND_IDS, expectedRoundIds)));

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

        final List<Training> trainings = Stream.of(Training.getAll())
                .sorted(Collections.reverseOrder())
                .collect(Collectors.toList());
        expectedRoundIds = Stream.of(trainings.get(1), trainings.get(2))
                .flatMap(t -> Stream.of(t.getRounds()))
                .map(Round::getId)
                .collect(Collectors.toSet());

        intended(allOf(hasClass(StatisticsActivity.class),
                MatcherUtils.hasLongArrayExtra(ROUND_IDS, expectedRoundIds)));

        // newBow_recurve
        switchToTab(R.string.bow);
        onView(matchFabMenu()).perform(click());
        onView(withId(R.id.fabBowRecurve)).perform(click());
        intended(allOf(hasClass(EditBowActivity.class),
                hasExtra(EditBowFragment.BOW_TYPE, EBowType.RECURVE_BOW.name())));

        // openBow
        onView(withRecyclerView(R.id.recyclerView).atPosition(0))
                .perform(click());
        final Bow firstBow = Stream.of(Bow.getAll()).sorted().findFirst().get();
        intended(allOf(hasClass(EditBowActivity.class),
                hasExtra(EditBowFragment.BOW_ID, firstBow.getId())));

        // newArrow
        switchToTab(R.string.arrow);
        onView(allOf(withId(R.id.fab), isDisplayed())).perform(click());
        intended(hasClass(EditArrowActivity.class));

        // openArrow
        onView(withRecyclerView(R.id.recyclerView).atPosition(0))
                .perform(click());
        final Arrow firstArrow = Stream.of(Arrow.getAll()).sorted().findFirst().get();
        final Class<?> clazz = EditArrowActivity.class;
        intended(allOf(hasClass(clazz), hasExtra(EditArrowFragment.ARROW_ID, firstArrow.getId())));

        // openSettings
        clickActionBarItem(R.id.action_preferences, R.string.preferences);
        intended(hasClass(SettingsActivity.class));
    }

    private void switchToTab(int title) {
        onView(allOf(withText(title), isNestedChildOfView(withId(R.id.slidingTabs)), isDisplayed()))
                .perform(click());
    }
}