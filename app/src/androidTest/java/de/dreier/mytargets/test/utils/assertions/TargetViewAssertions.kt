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

import android.support.test.espresso.ViewAssertion
import de.dreier.mytargets.features.training.input.TargetView
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull

object TargetViewAssertions {
    fun virtualButtonNotExists(desc: String): ViewAssertion {
        return ViewAssertion { view, noViewFoundException ->
            if (noViewFoundException != null) {
                throw noViewFoundException
            }

            val targetView = view as TargetView
            val vv = targetView.virtualViews.firstOrNull { it.description == desc }
            assertNull("Virtual button does exist", vv)
        }
    }

    fun virtualButtonExists(desc: String): ViewAssertion {
        return ViewAssertion { view, noViewFoundException ->
            if (noViewFoundException != null) {
                throw noViewFoundException
            }

            val targetView = view as TargetView
            val vv = targetView.virtualViews.firstOrNull { it.description == desc }
            assertNotNull("Virtual button does not exist", vv)
        }
    }
}
