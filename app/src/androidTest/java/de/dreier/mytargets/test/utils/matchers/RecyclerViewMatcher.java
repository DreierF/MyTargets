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

public class RecyclerViewMatcher {
    private final int recyclerViewId;

    public RecyclerViewMatcher(int recyclerViewId) {
        this.recyclerViewId = recyclerViewId;
    }

    public Matcher<View> atPosition(final int position) {
        return atPositionOnView(position, -1);
    }

    public Matcher<View> atPositionOnView(final int position, final int targetViewId) {

        return new TypeSafeMatcher<View>() {
            Resources resources = null;

            public void describeTo(Description description) {
                String idDescription = Integer.toString(recyclerViewId);
                if (resources != null) {
                    try {
                        idDescription = resources.getResourceName(recyclerViewId);
                    } catch (Resources.NotFoundException var4) {
                        idDescription = recyclerViewId + " (resource name not found)";
                    }
                }

                description.appendText("with id: " + idDescription);
            }

            public boolean matchesSafely(View view) {
                resources = view.getResources();

                RecyclerView recyclerView = (RecyclerView) MatcherUtils
                        .getParentViewById(view, recyclerViewId);
                if (recyclerView == null || recyclerView.getId() != recyclerViewId) {
                    return false;
                }
                View childView = recyclerView
                        .findViewHolderForAdapterPosition(position).itemView;

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