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

import androidx.annotation.IdRes
import android.view.View
import android.view.ViewGroup
import de.dreier.mytargets.views.speeddial.FabSpeedDial
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

object ParentViewMatcher {

    fun isOnForegroundFragment(): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("is on foreground fragment")
            }

            public override fun matchesSafely(view: View): Boolean {
                val content = MatcherUtils.getParentViewById(view, android.R.id.content)
                if (content != null && content is ViewGroup) {
                    val currentFragment = content
                            .getChildAt(content.childCount - 1)
                    return MatcherUtils.isInViewHierarchy(view, currentFragment)
                }
                return false
            }
        }
    }

    fun isNestedChildOfView(parentViewMatcher: Matcher<View>): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("is nested child of view ")
                parentViewMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                return MatcherUtils.getMatchingParent(view, parentViewMatcher) != null
            }
        }
    }

    fun withSpeedDialItem(speedDialViewMatcher: Matcher<View>, @IdRes id: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("with id $id on speed dial ")
                speedDialViewMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val speedDialView = MatcherUtils.getMatchingParent(view, speedDialViewMatcher)
                if (speedDialView != null && speedDialView is FabSpeedDial) {
                    val fabFromMenuId = speedDialView
                            .getFabFromMenuId(id)
                    val parent = fabFromMenuId.parent
                    if (parent != null && parent is ViewGroup &&
                            parent.getChildAt(0) === view) {
                        return true
                    }
                }
                return false
            }
        }
    }
}
