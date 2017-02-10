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

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static org.hamcrest.CoreMatchers.allOf;

public class RecyclerViewMatcher {
    private final Matcher<View> recyclerViewMatcher;

    public RecyclerViewMatcher(Matcher<View> recyclerViewMatcher) {
        this.recyclerViewMatcher = recyclerViewMatcher;
    }

    public static RecyclerViewMatcher withRecyclerView(Matcher<View> recyclerViewMatcher) {
        return new RecyclerViewMatcher(recyclerViewMatcher);
    }

    public static RecyclerViewMatcher withRecyclerView(int recyclerViewId) {
        return new RecyclerViewMatcher(allOf(withId(recyclerViewId), isDisplayed()));
    }

    public static RecyclerViewMatcher withNestedRecyclerView(int recyclerViewId) {
        return new RecyclerViewMatcher(allOf(withId(recyclerViewId),
                withParent(withParent(isDisplayed()))));
    }

    public Matcher<View> atPosition(final int position) {
        return atPositionOnView(position, -1);
    }

    public Matcher<View> atPositionOnView(final int position, final int targetViewId) {
        return new TypeSafeMatcher<View>() {
            Resources resources = null;
            View childView;

            public void describeTo(Description description) {
                recyclerViewMatcher.describeTo(description);
                description.appendText(" at position " + position);
                if (targetViewId != -1) {
                    String idDescription = Integer.toString(targetViewId);
                    if (resources != null) {
                        try {
                            idDescription = resources.getResourceName(targetViewId);
                        } catch (Resources.NotFoundException var4) {
                            idDescription = targetViewId + " (resource name not found)";
                        }
                    }
                    description.appendText(" on view with id " + idDescription);
                }
            }

            public boolean matchesSafely(View view) {
                resources = view.getResources();

                if (childView == null) {
                    View parent = MatcherUtils.getMatchingParent(view, recyclerViewMatcher);
                    if (parent == null || !(parent instanceof RecyclerView)) {
                        return false;
                    }
                    RecyclerView recyclerView = (RecyclerView) parent;
                    RecyclerView.ViewHolder viewHolder = recyclerView
                            .findViewHolderForAdapterPosition(position);
                    if (viewHolder == null) {
                        return false;
                    }
                    childView = viewHolder.itemView;
                }

                if (targetViewId == -1) {
                    return view == childView;
                } else {
                    View targetView = childView.findViewById(targetViewId);
                    return view == targetView;
                }

            }
        };
    }
}