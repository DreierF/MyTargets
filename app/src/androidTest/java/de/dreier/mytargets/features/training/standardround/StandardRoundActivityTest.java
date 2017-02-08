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

package de.dreier.mytargets.features.training.standardround;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import de.dreier.mytargets.R;
import de.dreier.mytargets.features.settings.SettingsManager;
import de.dreier.mytargets.shared.models.db.StandardRound;
import de.dreier.mytargets.test.base.UITestBase;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static de.dreier.mytargets.test.utils.matchers.RecyclerViewMatcher.withRecyclerView;

@RunWith(AndroidJUnit4.class)
public class StandardRoundActivityTest extends UITestBase {

    @Rule
    public IntentsTestRule<StandardRoundActivity> activityTestRule = new IntentsTestRule<>(
            StandardRoundActivity.class, true, false);

    @Before
    public void setUp() {
        Map<Long, Integer> map = new HashMap<>();
        map.put(32L, 3);
        map.put(31L, 2);
        SettingsManager.setStandardRoundsLastUsed(map);
    }

    @Test
    public void searchTest() {
        activityTestRule.launchActivity(
                StandardRoundListFragment.getIntent(StandardRound.get(32L)).build());

        clickActionBarItem(R.id.action_search, R.string.search);

        onView(withId(R.id.search_src_text)).perform(replaceText("wa 18"), closeSoftKeyboard());

        onView(withRecyclerView(R.id.recyclerView).atPosition(1))
                .check(matches(hasDescendant(withText(R.string.wa_18_40cm))));

        onView(withRecyclerView(R.id.recyclerView).atPosition(2))
                .check(matches(hasDescendant(withText(R.string.wa_18_60cm))));

        onView(withId(R.id.recyclerView))
                .perform(actionOnItemAtPosition(1, click()));
    }

    @Test
    public void recentlyUsedTest() {
        activityTestRule.launchActivity(
                StandardRoundListFragment.getIntent(StandardRound.get(32L)).build());

        onView(withRecyclerView(R.id.recyclerView).atPosition(0))
                .check(matches(hasDescendant(withText(R.string.recently_used))));

        onView(withRecyclerView(R.id.recyclerView).atPosition(1))
                .check(matches(hasDescendant(withText(R.string.wa_standard))));

        onView(withRecyclerView(R.id.recyclerView).atPosition(2))
                .check(matches(hasDescendant(withText(R.string.wa_cub))));

        onView(withRecyclerView(R.id.recyclerView).atPosition(4))
                .check(matches(hasDescendant(withText(R.string.adelaide))));
    }
}
