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

package de.dreier.mytargets.test.base

import android.os.Environment
import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.support.test.InstrumentationRegistry
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import android.support.test.espresso.ViewAction
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.pressBack
import android.support.test.espresso.contrib.PickerActions
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.uiautomator.UiDevice
import android.widget.DatePicker
import de.dreier.mytargets.R
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.test.utils.actions.NestedScrollToAction
import de.dreier.mytargets.test.utils.matchers.ParentViewMatcher.isNestedChildOfView
import de.dreier.mytargets.test.utils.matchers.ViewMatcher
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.anyOf
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.equalTo
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.rules.TestRule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.io.File
import java.io.IOException

abstract class UITestBase : InstrumentedTestBase() {

    @get:Rule
    var watcher: TestRule = object : TestWatcher() {
        override fun failed(e: Throwable?, description: Description?) {
            // Save to external storage (usually /sdcard/screenshots)
            val path = File(Environment.getExternalStorageDirectory(), "screenshots")
            if (!path.exists()) {
                path.mkdirs()
            }

            // Take advantage of UiAutomator screenshot method
            val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
            val filename = (description!!.className.replace(".*\\.([^.]+)".toRegex(), "$1")
                    + "-" + description.methodName)
            device.takeScreenshot(File(path, filename + ".png"))

            try {
                device.dumpWindowHierarchy(File(path, filename + ".txt"))
            } catch (e1: IOException) {
                e1.printStackTrace()
            }

        }
    }

    protected fun clickActionBarItem(@IdRes menuItem: Int, @StringRes title: Int) {
        onView(withId(menuItem)).withFailureHandler { _, _ ->
            openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
            onView(withText(title)).perform(click())
        }.perform(click())
    }

    protected fun clickContextualActionBarItem(@IdRes menuItem: Int, @StringRes title: Int) {
        onView(allOf(withId(menuItem), isDisplayed(), isNestedChildOfView(
                anyOf(withId(R.id.action_context_bar), withId(R.id.action_mode_bar)))))
                .withFailureHandler { _, _ ->
                    openContextualActionModeOverflowMenu()
                    onView(withText(title)).perform(click())
                }.perform(click())
    }

    protected fun enterDate(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        onView(withClassName(equalTo(DatePicker::class.java.name)))
                .perform(PickerActions.setDate(year, monthOfYear, dayOfMonth))
        onView(withId(android.R.id.button1)).perform(click())
    }

    fun save() {
        clickActionBarItem(R.id.action_save, R.string.save)
    }


    protected fun getString(@StringRes resString: Int): String {
        return InstrumentationRegistry.getTargetContext().getString(resString)
    }

    protected fun navigateUp() {
        onView(ViewMatcher.androidHomeMatcher()).perform(click())
    }

    protected fun nestedScrollTo(): ViewAction {
        return ViewActions.actionWithAssertions(NestedScrollToAction())
    }

    private fun openContextualActionModeOverflowMenu() {
        onView(allOf(anyOf(
                allOf(isDisplayed(), withContentDescription("More options")),
                allOf(isDisplayed(), withClassName(endsWith("OverflowMenuButton")))),
                isNestedChildOfView(withId(R.id.action_mode_bar))))
                .perform(click(pressBack()))
    }

    companion object {

        @BeforeClass
        @JvmStatic
        fun disableIntro() {
            SettingsManager.shouldShowIntroActivity = false
        }
    }

}
