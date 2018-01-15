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

package de.dreier.mytargets.shared.targets.models

import android.support.test.InstrumentationRegistry
import android.support.test.filters.SmallTest
import android.support.test.runner.AndroidJUnit4
import de.dreier.mytargets.shared.SharedApplicationInstance

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@RunWith(AndroidJUnit4::class)
class DAIR3DTest {

    private var target = DAIR3D()

    @Before
    @Throws(Exception::class)
    fun setUp() {
        SharedApplicationInstance.context = InstrumentationRegistry.getContext()
    }

    @Test
    fun testScoringStyle() {
        Assert.assertEquals(8, target.getScoringStyle(0).getPointsByScoringRing(0, 0).toLong())
        Assert.assertEquals(12, target.getScoringStyle(0).getPointsByScoringRing(2, 0).toLong())
        Assert.assertEquals(14, target.getScoringStyle(1).getPointsByScoringRing(1, 0).toLong())
    }
}
