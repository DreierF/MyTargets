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

import android.os.Environment;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.uiautomator.UiDevice;
import android.widget.DatePicker;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.File;
import java.io.IOException;

import de.dreier.mytargets.R;
import de.dreier.mytargets.features.settings.SettingsManager;
import de.dreier.mytargets.test.utils.actions.NestedScrollToAction;
import de.dreier.mytargets.test.utils.matchers.ViewMatcher;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static de.dreier.mytargets.test.utils.matchers.ParentViewMatcher.isNestedChildOfView;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;

public abstract class UITestBase extends InstrumentedTestBase {

    @BeforeClass
    public static void disableIntro() {
        SettingsManager.INSTANCE.setShouldShowIntroActivity(false);
    }

    @Rule
    public TestRule watcher = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            // Save to external storage (usually /sdcard/screenshots)
            File path = new File(Environment.getExternalStorageDirectory(), "screenshots");
            if (!path.exists()) {
                path.mkdirs();
            }

            // Take advantage of UiAutomator screenshot method
            UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
            String filename = description.getClassName().replaceAll(".*\\.([^.]+)", "$1")
                    + "-" + description.getMethodName();
            device.takeScreenshot(new File(path, filename + ".png"));

            try {
                device.dumpWindowHierarchy(new File(path, filename + ".txt"));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    };

    protected static void navigateUp() {
        onView(ViewMatcher.androidHomeMatcher()).perform(click());
    }

    protected static ViewAction nestedScrollTo() {
        return ViewActions.actionWithAssertions(new NestedScrollToAction());
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

    private static void openContextualActionModeOverflowMenu() {
        onView(allOf(anyOf(
                allOf(isDisplayed(), withContentDescription("More options")),
                allOf(isDisplayed(), withClassName(endsWith("OverflowMenuButton")))),
                isNestedChildOfView(withId(R.id.action_mode_bar))))
                .perform(click(pressBack()));
    }

    protected void enterDate(int year, int monthOfYear, int dayOfMonth) {
        onView(withClassName(equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(year, monthOfYear, dayOfMonth));
        onView(withId(android.R.id.button1)).perform(click());
    }

    public void save() {
        clickActionBarItem(R.id.action_save, R.string.save);
    }

}
