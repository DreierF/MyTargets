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

package de.dreier.mytargets.test.utils.matchers;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import de.dreier.mytargets.R;

import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.endsWith;

public class ViewMatcher {
    public static Matcher<View> androidHomeMatcher() {
        return allOf(withParent(withClassName(is(Toolbar.class.getName()))),
                withClassName(containsString("ImageButton"))
        );
    }

    @NonNull
    public static Matcher<View> matchFabMenu() {
        return Matchers.allOf(withParent(ViewMatchers.withId(R.id.fab)),
                withClassName(endsWith("ImageView")),
                isDisplayed());
    }

    @NonNull
    public static Matcher<View> supportFab() {
        return allOf(withId(R.id.fab), isDisplayed(), instanceOf(FloatingActionButton.class));
    }
}
