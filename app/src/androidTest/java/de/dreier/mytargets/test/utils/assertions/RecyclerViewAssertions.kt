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

package de.dreier.mytargets.test.utils.assertions

import android.support.annotation.StringRes
import android.support.test.espresso.ViewAssertion
import android.support.test.espresso.matcher.ViewMatchers.assertThat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.View.FIND_VIEWS_WITH_TEXT
import android.widget.TextView
import com.google.common.truth.Truth
import org.hamcrest.Matcher
import org.junit.Assert
import java.util.*

object RecyclerViewAssertions {
    fun itemCount(matcher: Matcher<Int>): ViewAssertion {
        return ViewAssertion { view, noViewFoundException ->
            if (noViewFoundException != null) {
                throw noViewFoundException
            }

            val recyclerView = view as RecyclerView
            val adapter = recyclerView.adapter
            assertThat(adapter.itemCount, matcher)
        }
    }

    fun itemHasSummary(@StringRes item: Int, summary: String): ViewAssertion {
        return ViewAssertion { view, e ->
            if (view !is RecyclerView) {
                throw e
            }
            val rv = view
            val outviews = ArrayList<View>()
            val title = view.getContext().getString(item)
            for (index in 0 until rv.adapter.itemCount) {
                val viewHolder = rv.findViewHolderForAdapterPosition(index) ?: continue
                val itemView = viewHolder.itemView
                itemView.findViewsWithText(outviews, title, FIND_VIEWS_WITH_TEXT)
                if (outviews.size > 0) {
                    val summaryView = itemView.findViewById<TextView>(android.R.id.summary)
                    Truth.assertThat(summaryView.text).isEqualTo(summary)
                    return@ViewAssertion
                }
            }
            Assert.fail("No view with text '$title' found!")
        }
    }
}
