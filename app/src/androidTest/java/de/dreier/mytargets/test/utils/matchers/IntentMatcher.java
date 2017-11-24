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

import android.content.Intent;
import android.support.annotation.NonNull;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Set;
import java.util.TreeSet;

import de.dreier.mytargets.shared.utils.LongUtils;

import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasMyPackageName;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static org.hamcrest.CoreMatchers.allOf;

public class IntentMatcher {
    public static Matcher<? super Intent> hasLongArrayExtra(String key, Set<Long> values) {
        return new TypeSafeMatcher<Intent>() {
            @Override
            public void describeTo(Description description) {

            }

            @Override
            protected boolean matchesSafely(@NonNull Intent intent) {
                long[] items = intent.getLongArrayExtra(key);
                return items != null && new TreeSet(LongUtils.toList(items)).equals(values);
            }
        };
    }

    public static Matcher<Intent> hasClass(@NonNull Class<?> clazz) {
        return hasComponent(allOf(hasClassName(clazz.getName()), hasMyPackageName()));
    }
}
