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

package de.dreier.mytargets.test.base;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.DatePicker;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.BeforeClass;

import de.dreier.mytargets.R;
import de.dreier.mytargets.features.settings.SettingsManager;
import de.dreier.mytargets.test.utils.actions.NestedScrollToAction;
import de.dreier.mytargets.test.utils.matchers.MatcherUtils;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.Espresso.openContextualActionModeOverflowMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasMyPackageName;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static de.dreier.mytargets.test.utils.matchers.ParentViewMatcher.isNestedChildOfView;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public abstract class UITestBase extends InstrumentedTestBase {

    @BeforeClass
    public static void disableIntro() {
        SettingsManager.setShouldShowIntroActivity(false);
    }

    private static Matcher<View> androidHomeMatcher() {
        return allOf(withParent(withClassName(is(Toolbar.class.getName()))),
                withClassName(containsString("ImageButton"))
        );
    }

    protected static void navigateUp() {
        onView(androidHomeMatcher()).perform(click());
    }

    @NonNull
    protected static Matcher<View> matchFabMenu() {
        return Matchers.allOf(withParent(ViewMatchers.withId(R.id.fab)),
                withClassName(endsWith("ImageView")),
                isDisplayed());
    }

    protected static ViewAction nestedScrollTo() {
        return ViewActions.actionWithAssertions(new NestedScrollToAction());
    }

    protected static ViewAssertion assertItemCount(int expectedCount) {
        return (view, noViewFoundException) -> {
            if (noViewFoundException != null) {
                throw noViewFoundException;
            }

            RecyclerView recyclerView = (RecyclerView) view;
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            assertThat(adapter.getItemCount(), is(expectedCount));
        };
    }

    protected static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    protected static Matcher<View> isOnForegroundFragment() {
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

    protected void clickActionBarItem(@IdRes int menuItem, @StringRes int title) {
        onView(withId(menuItem)).withFailureHandler((error, viewMatcher) -> {
            openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
            onView(withText(title)).perform(click());
        }).perform(click());
    }

    protected void clickContextualActionBarItem(@IdRes int menuItem, @StringRes int title) {
        onView(allOf(withId(menuItem), isDisplayed(), isNestedChildOfView(
                anyOf(withId(R.id.action_context_bar), withId(R.id.action_mode_bar)))))
                .withFailureHandler((error, viewMatcher) -> {
                    openContextualActionModeOverflowMenu();
                    onView(withText(title)).perform(click());
                }).perform(click());
    }

    protected void enterDate(int year, int monthOfYear, int dayOfMonth) {
        onView(withClassName(equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(year, monthOfYear, dayOfMonth));
        onView(withId(android.R.id.button1)).perform(click());
    }

    public void save() {
        clickActionBarItem(R.id.action_save, R.string.save);
    }

    @NonNull
    protected Matcher<View> supportFab() {
        return Matchers
                .allOf(withId(R.id.fab), isDisplayed(), instanceOf(FloatingActionButton.class));
    }

    protected Matcher<Intent> hasClass(Class<?> clazz) {
        return hasComponent(allOf(hasClassName(clazz.getName()), hasMyPackageName()));
    }
}
