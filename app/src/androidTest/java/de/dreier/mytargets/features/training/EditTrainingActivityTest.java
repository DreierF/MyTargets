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


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import de.dreier.mytargets.R;
import de.dreier.mytargets.features.settings.SettingsManager;
import de.dreier.mytargets.features.training.edit.EditTrainingActivity;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.targets.models.WAFull;
import de.dreier.mytargets.shared.views.TargetViewBase.EInputMethod;
import de.dreier.mytargets.test.base.UITestBase;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItem;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static de.dreier.mytargets.features.training.edit.EditTrainingFragment.CREATE_FREE_TRAINING_ACTION;
import static de.dreier.mytargets.features.training.edit.EditTrainingFragment.CREATE_TRAINING_WITH_STANDARD_ROUND_ACTION;
import static de.dreier.mytargets.shared.models.Dimension.Unit.CENTIMETER;
import static de.dreier.mytargets.shared.models.Dimension.Unit.METER;
import static de.dreier.mytargets.test.utils.assertions.RecyclerViewAssertions.itemCount;
import static de.dreier.mytargets.test.utils.matchers.MatcherUtils.containsStringRes;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;

@RunWith(AndroidJUnit4.class)
public class EditTrainingActivityTest extends UITestBase {

    @NonNull
    @Rule
    public IntentsTestRule<EditTrainingActivity> activityTestRule = new IntentsTestRule<>(
            EditTrainingActivity.class, true, false);

    @Before
    public void setUp() {
        SettingsManager.INSTANCE.setStandardRound(93L);
        SettingsManager.INSTANCE.setTarget(new Target(WAFull.ID, 0, new Dimension(122, CENTIMETER)));
        SettingsManager.INSTANCE.setDistance(new Dimension(50, METER));
        SettingsManager.INSTANCE.setIndoor(false);
        SettingsManager.INSTANCE.setInputMethod(EInputMethod.PLOTTING);
        SettingsManager.INSTANCE.setTimerEnabled(false);
        SettingsManager.INSTANCE.setShotsPerEnd(3);
        SettingsManager.INSTANCE.setDistance(new Dimension(10, METER));
    }

    @Test
    public void createFreeTraining() {
        Intent intent = new Intent();
        intent.setAction(CREATE_FREE_TRAINING_ACTION);
        activityTestRule.launchActivity(intent);

        //allowPermissionsIfNeeded(activityTestRule.getActivity(), ACCESS_FINE_LOCATION);

        // Select distance 20m
        onView(withId(R.id.distance)).perform(nestedScrollTo(), click());
        onView(allOf(withId(R.id.recyclerView), isDisplayed()))
                .perform(actionOnItemAtPosition(4, click()));
        onView(withId(R.id.distanceValue)).check(matches(withText("20m")));

        // Change distance to 23yd as custom distance
        onView(withId(R.id.distance)).perform(nestedScrollTo(), click());
        onView(withText(R.string.imperial)).perform(click());
        onView(allOf(withId(R.id.fab), isDisplayed())).perform(click());
        onView(withId(R.id.shot_comment)).perform(replaceText("23"));
        onView(withText(android.R.string.ok)).perform(click());
        onView(withId(R.id.distanceValue)).check(matches(withText("23yd")));

        // Change target to vertical 3 spot
        onView(withId(R.id.target)).perform(nestedScrollTo(), click());
        onView(withId(R.id.recyclerView)).perform(actionOnItemAtPosition(5, click()));
        onView(withId(R.id.scoring_style)).perform(click());
        onData(instanceOf(String.class)).atPosition(2).perform(click());
        onView(withId(R.id.target_size)).perform(click());
        onView(withText("40cm")).perform(click());
        pressBack();
        onView(withId(R.id.target))
                .check(matches(hasDescendant(withText(containsString(activityTestRule.getActivity()
                        .getString(R.string.vertical_3_spot))))))
                .check(matches(hasDescendant(withText(containsString("40cm")))))
                .check(matches(hasDescendant(withText(containsString("Compound")))));

        // Change environment
        onView(withId(R.id.environment)).perform(nestedScrollTo(), click());
        onView(withId(R.id.rain)).perform(click());
        onView(withId(R.id.windSpeed)).perform(click());
        onView(withId(R.id.recyclerView)).perform(actionOnItemAtPosition(9, click()));
        onView(withId(R.id.windDirection)).perform(click());
        onView(withId(R.id.recyclerView)).perform(actionOnItemAtPosition(7, click()));
        onView(withId(R.id.location)).perform(replaceText("My location"));
        navigateUp();
        onView(withId(R.id.environment))
                .check(matches(hasDescendant(withText(R.string.rain))));
        onView(allOf(withId(R.id.details),
                withParent(withParent(withParent(withParent(withId(R.id.environment)))))))
                .check(matches(withText(containsString("9 Bft"))))
                .check(matches(withText(containsString("My location"))));

        onView(withId(R.id.trainingDate)).perform(click());
        enterDate(2016, 8, 10);
        final String formattedDate = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                .format(LocalDate.of(2016, 8, 10));
        onView(withId(R.id.trainingDate)).check(matches(withText(formattedDate)));

        save();
        pressBack();
        pressBack();
    }

    @Test
    public void createTrainingWithStandardRound() {
        Intent intent = new Intent();
        intent.setAction(CREATE_TRAINING_WITH_STANDARD_ROUND_ACTION);
        activityTestRule.launchActivity(intent);

        //allowPermissionsIfNeeded(activityTestRule.getActivity(), ACCESS_FINE_LOCATION);

        // Has last used standard round been restored
        onView(withId(R.id.standardRound))
                .check(matches(hasDescendant(withText(R.string.warwick))));

        // Change standard round
        onView(withId(R.id.standardRound)).perform(nestedScrollTo(), click());
        onView(withId(R.id.recyclerView))
                .perform(actionOnItem(hasDescendant(withText(R.string.wa_standard)), click()));
        onView(withId(R.id.recyclerView))
                .perform(actionOnItem(hasDescendant(withText(R.string.wa_standard)), click()));
        onView(withId(R.id.standardRound))
                .check(matches(hasDescendant(withText(R.string.wa_standard))));

        onView(withText(R.string.change_target_face)).perform(nestedScrollTo(), click());

        onView(withId(R.id.recyclerView))
                .check(matches(hasDescendant(withText(R.string.wa_full))))
                .check(matches(hasDescendant(withText(R.string.wa_3_ring))))
                .check(itemCount(is(5)));

        onView(allOf(withId(R.id.recyclerView), isDisplayed()))
                .perform(actionOnItemAtPosition(4, click()));
        navigateUp();

        save();

        navigateUp();
        navigateUp();

        onView(withId(R.id.detail_round_info))
                .check(matches(allOf(containsStringRes(R.string.wa_standard),
                        containsStringRes(R.string.wa_3_ring))));
    }
}
