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

package de.dreier.mytargets.test.utils.assertions;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.test.espresso.ViewAssertion;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.common.truth.Truth;

import junit.framework.Assert;

import org.hamcrest.Matcher;

import java.util.ArrayList;

import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.view.View.FIND_VIEWS_WITH_TEXT;

public class RecyclerViewAssertions {
    public static ViewAssertion itemCount(@NonNull Matcher<Integer> matcher) {
        return (view, noViewFoundException) -> {
            if (noViewFoundException != null) {
                throw noViewFoundException;
            }

            RecyclerView recyclerView = (RecyclerView) view;
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            assertThat(adapter.getItemCount(), matcher);
        };
    }

    public static ViewAssertion itemHasSummary(@StringRes final int item, String summary) {
        return (view, e) -> {
            if (!(view instanceof RecyclerView)) {
                throw e;
            }
            RecyclerView rv = (RecyclerView) view;
            ArrayList<View> outviews = new ArrayList<>();
            String title = view.getContext().getString(item);
            for (int index = 0; index < rv.getAdapter().getItemCount(); index++) {
                View itemView = rv.findViewHolderForAdapterPosition(index).itemView;
                itemView.findViewsWithText(outviews, title,
                        FIND_VIEWS_WITH_TEXT);
                if (outviews.size() > 0) {
                    TextView summaryView = itemView.findViewById(android.R.id.summary);
                    Truth.assertThat(summaryView.getText()).isEqualTo(summary);
                    return;
                }
            }
            Assert.fail("No view with text '" + title + "' found!");
        };
    }
}
