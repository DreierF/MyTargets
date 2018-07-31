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

import android.content.res.Resources
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.v7.widget.RecyclerView
import android.view.View
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

class RecyclerViewMatcher(private val recyclerViewMatcher: Matcher<View>) {

    fun atPosition(position: Int): Matcher<View> {
        return atPositionOnView(position, -1)
    }

    fun atPositionOnView(position: Int, targetViewId: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            internal var resources: Resources? = null
            internal var childView: View? = null

            override fun describeTo(description: Description) {
                recyclerViewMatcher.describeTo(description)
                description.appendText(" at position " + position)
                if (targetViewId != -1) {
                    var idDescription = Integer.toString(targetViewId)
                    if (resources != null) {
                        try {
                            idDescription = resources!!.getResourceName(targetViewId)
                        } catch (var4: Resources.NotFoundException) {
                            idDescription = targetViewId.toString() + " (resource name not found)"
                        }

                    }
                    description.appendText(" on view with id " + idDescription)
                }
            }

            public override fun matchesSafely(view: View): Boolean {
                resources = view.resources

                if (childView == null) {
                    val parent = MatcherUtils.getMatchingParent(view, recyclerViewMatcher)
                    if (parent == null || parent !is RecyclerView) {
                        return false
                    }
                    val recyclerView = parent as RecyclerView?
                    val viewHolder = recyclerView!!
                            .findViewHolderForAdapterPosition(position) ?: return false
                    childView = viewHolder.itemView
                }

                return if (targetViewId == -1) {
                    view === childView
                } else {
                    val targetView = childView!!.findViewById<View>(targetViewId)
                    view === targetView
                }

            }
        }
    }

    companion object {

        fun withRecyclerView(recyclerViewMatcher: Matcher<View>): RecyclerViewMatcher {
            return RecyclerViewMatcher(recyclerViewMatcher)
        }

        fun withRecyclerView(recyclerViewId: Int): RecyclerViewMatcher {
            return RecyclerViewMatcher(allOf(withId(recyclerViewId), isDisplayed()))
        }

        fun withNestedRecyclerView(recyclerViewId: Int): RecyclerViewMatcher {
            return RecyclerViewMatcher(allOf(withId(recyclerViewId),
                    withParent(withParent(isDisplayed()))))
        }
    }
}
