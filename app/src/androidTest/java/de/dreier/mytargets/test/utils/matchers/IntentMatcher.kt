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

package de.dreier.mytargets.test.utils.matchers

import android.content.Intent
import android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName
import android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasMyPackageName
import android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import java.util.*

object IntentMatcher {
    fun hasLongArrayExtra(key: String, values: Set<Long>): Matcher<in Intent> {
        return object : TypeSafeMatcher<Intent>() {
            override fun describeTo(description: Description) {

            }

            override fun matchesSafely(intent: Intent): Boolean {
                val items = intent.getLongArrayExtra(key)
                return items != null && TreeSet(items.toList()) == values
            }
        }
    }

    fun hasClass(clazz: Class<*>): Matcher<Intent> {
        return hasComponent(allOf(hasClassName(clazz.name), hasMyPackageName()))
    }
}
