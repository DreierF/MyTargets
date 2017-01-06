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

package de.dreier.mytargets.activities;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.dreier.mytargets.R;
import de.dreier.mytargets.UITestBase;
import de.dreier.mytargets.managers.SettingsManager;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class IntroActivityTest extends UITestBase {

    @Rule
    public IntentsTestRule<MainActivity> activityTestRule = new IntentsTestRule<>(MainActivity.class);

    @BeforeClass
    public static void setUp() {
        SettingsManager.setShouldShowIntroActivity(true);
    }

    @Test
    public void introActivityTest() {
        onView(allOf(withId(R.id.txt_title_slide), isDisplayed()))
                .check(matches(withText(R.string.intro_title_track_training_progress)));
        onView(withId(R.id.button_next)).perform(click());

        onView(allOf(withId(R.id.txt_title_slide), isDisplayed()))
                .check(matches(withText(R.string.intro_title_everything_in_one_place)));
        onView(withId(R.id.button_next)).perform(click());

        onView(withId(R.id.toolbar)).check(matches(hasDescendant(withText(R.string.my_targets))));
    }
}
