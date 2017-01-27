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
import android.content.res.Resources;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Set;
import java.util.TreeSet;

import de.dreier.mytargets.shared.utils.LongUtils;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;

public class MatcherUtils {
    public static View getParentViewById(View view, int parentViewId) {
        if (view.getId() == parentViewId) {
            return view;
        } else if (view.getParent() != null && view.getParent() instanceof ViewGroup) {
            return getParentViewById((View) view.getParent(), parentViewId);
        }
        return null;
    }

    public static boolean isInViewHierarchy(View view, View viewToFind) {
        if (view == viewToFind) {
            return true;
        } else if (view.getParent() != null && view.getParent() instanceof ViewGroup) {
            return isInViewHierarchy((View) view.getParent(), viewToFind);
        }
        return false;
    }

    public static ViewInteraction matchToolbarTitle(CharSequence title) {
        return onView(isAssignableFrom(Toolbar.class))
                .check(ViewAssertions.matches(withToolbarTitle(is(title))));
    }

    private static Matcher<Object> withToolbarTitle(
            final Matcher<CharSequence> textMatcher) {
        return new BoundedMatcher<Object, Toolbar>(Toolbar.class) {
            @Override
            public boolean matchesSafely(Toolbar toolbar) {
                return textMatcher.matches(toolbar.getTitle());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with toolbar title: ");
                textMatcher.describeTo(description);
            }
        };
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

    public static View getMatchingParent(View view, Matcher<View> matcher) {
        if (view == null) {
            return null;
        }
        if (matcher.matches(view)) {
            return view;
        } else if (view.getParent() != null && view.getParent() instanceof ViewGroup) {
            return getMatchingParent((View) view.getParent(), matcher);
        }
        return null;
    }

    /**
     * Returns a matcher that matches a descendant of {@link TextView} that is displaying the string
     * associated with the given resource id.
     *
     * @param resourceId the string resource the text view is expected to hold.
     */
    public static Matcher<View> containsStringRes(final int resourceId) {
        return new BoundedMatcher<View, TextView>(TextView.class) {
            private String resourceName = null;
            private String expectedText = null;

            @Override
            public void describeTo(Description description) {
                description.appendText("contains string from resource id: ");
                description.appendValue(resourceId);
                if (resourceName != null) {
                    description.appendText("[");
                    description.appendText(resourceName);
                    description.appendText("]");
                }
                if (expectedText != null) {
                    description.appendText(" value: ");
                    description.appendText(expectedText);
                }
            }

            @Override
            public boolean matchesSafely(TextView textView) {
                if (expectedText == null) {
                    try {
                        expectedText = textView.getResources().getString(resourceId);
                        resourceName = textView.getResources().getResourceEntryName(resourceId);
                    } catch (Resources.NotFoundException ignored) {
            /* view could be from a context unaware of the resource id. */
                    }
                }
                CharSequence actualText = textView.getText();
                // FYI: actualText may not be string ... its just a char sequence convert to string.
                return expectedText != null && actualText != null &&
                        actualText.toString().contains(expectedText);
            }
        };
    }

    public static Matcher<? super Intent> hasLongArrayExtra(String key, Set<Long> values) {
        return new TypeSafeMatcher<Intent>() {
            @Override
            public void describeTo(Description description) {

            }

            @Override
            protected boolean matchesSafely(Intent intent) {
                long[] items = intent.getLongArrayExtra(key);
                return items != null && new TreeSet(LongUtils.toList(items)).equals(values);
            }
        };
    }
}
