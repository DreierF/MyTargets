/*
 * Copyright (C) 2018 Florian Dreier
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

package de.dreier.mytargets.test.utils.matchers

import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.support.design.widget.FloatingActionButton
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.contrib.RecyclerViewActions.actionOnItem
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import de.dreier.mytargets.R
import de.dreier.mytargets.test.utils.matchers.ParentViewMatcher.isOnForegroundFragment
import de.dreier.mytargets.test.utils.matchers.ParentViewMatcher.withSpeedDialItem
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matcher
import org.hamcrest.Matchers

object ViewMatcher {
    fun androidHomeMatcher(): Matcher<View> {
        return allOf(withParent(withClassName(`is`(Toolbar::class.java.name))),
                withClassName(containsString("ImageButton"))
        )
    }

    fun supportFab(): Matcher<View> {
        return allOf(withId(R.id.fab), isDisplayed(), instanceOf(FloatingActionButton::class.java))
    }

    fun clickFabSpeedDialItem(@IdRes id: Int) {
        onView(ViewMatcher.supportFab()).perform(click())
        onView(withSpeedDialItem(withId(R.id.fabSpeedDial), id)).perform(click())
    }

    fun clickOnPreference(@StringRes text: Int) {
        onView(Matchers.allOf(withId(R.id.list), isOnForegroundFragment()))
                .perform(actionOnItem<RecyclerView.ViewHolder>(hasDescendant(withText(text)), click()))
    }
}
