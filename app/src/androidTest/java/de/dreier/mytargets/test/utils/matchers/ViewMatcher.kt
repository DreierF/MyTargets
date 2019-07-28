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

import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.matcher.ViewMatchers.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.dreier.mytargets.R
import de.dreier.mytargets.test.utils.matchers.ParentViewMatcher.isOnForegroundFragment
import de.dreier.mytargets.test.utils.matchers.ParentViewMatcher.withSpeedDialItem
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matcher
import org.hamcrest.Matchers

object ViewMatcher {
    fun androidHomeMatcher(): Matcher<View> {
        return allOf(
            withParent(withClassName(`is`(Toolbar::class.java.name))),
            withClassName(containsString("ImageButton"))
        )
    }

    fun supportFab(): Matcher<View> {
        return allOf(withId(R.id.fab), isDisplayed(), instanceOf(FloatingActionButton::class.java))
    }

    fun clickFabSpeedDialItem(@IdRes id: Int) {
        onView(supportFab()).perform(click())
        onView(withSpeedDialItem(withId(R.id.fabSpeedDial), id)).perform(click())
    }

    fun clickOnPreference(@StringRes text: Int) {
        onView(Matchers.allOf(withId(android.R.id.list), isOnForegroundFragment()))
            .perform(actionOnItem<RecyclerView.ViewHolder>(hasDescendant(withText(text)), click()))
    }
}
