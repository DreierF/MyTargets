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

import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import de.dreier.mytargets.views.speeddial.FabSpeedDial;

public class ParentViewMatcher {
    public static Matcher<View> isNestedChildOfView(Matcher<View> parentViewMatcher) {
        return new TypeSafeMatcher<View>() {
            public void describeTo(Description description) {
                description.appendText("is nested child of view ");
                parentViewMatcher.describeTo(description);
            }

            public boolean matchesSafely(View view) {
                return MatcherUtils.getMatchingParent(view, parentViewMatcher) != null;
            }
        };
    }

    public static Matcher<View> isOnForegroundFragment() {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("is on foreground fragment");
            }

            @Override
            public boolean matchesSafely(View view) {
                View content = MatcherUtils.getParentViewById(view, android.R.id.content);
                if (content != null && content instanceof ViewGroup) {
                    final View currentFragment = ((ViewGroup) content)
                            .getChildAt(((ViewGroup) content).getChildCount() - 1);
                    return MatcherUtils.isInViewHierarchy(view, currentFragment);
                }
                return false;
            }
        };
    }

    public static Matcher<View> withSpeedDialItem(Matcher<View> speedDialViewMatcher, @IdRes int id) {
        return new TypeSafeMatcher<View>() {
            public void describeTo(Description description) {
                description.appendText("with id " + id + " on speed dial ");
                speedDialViewMatcher.describeTo(description);
            }

            public boolean matchesSafely(View view) {
                View speedDialView = MatcherUtils.getMatchingParent(view, speedDialViewMatcher);
                if (speedDialView != null && speedDialView instanceof FabSpeedDial) {
                    FloatingActionButton fabFromMenuId = ((FabSpeedDial) speedDialView)
                            .getFabFromMenuId(id);
                    ViewParent parent = fabFromMenuId.getParent();
                    if (parent != null && parent instanceof ViewGroup &&
                            ((ViewGroup) parent).getChildAt(0) == view) {
                        return true;
                    }
                }
                return false;
            }
        };
    }
}
