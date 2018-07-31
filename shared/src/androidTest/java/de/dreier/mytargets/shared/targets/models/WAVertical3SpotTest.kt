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
class WAVertical3SpotTest {

    private var target = WAVertical3Spot()

    @Before
    @Throws(Exception::class)
    fun setUp() {
        SharedApplicationInstance.context = InstrumentationRegistry.getContext()
    }

    @Test
    @Throws(Exception::class)
    fun testShouldDrawZone() {
        Assert.assertEquals(true, target.shouldDrawZone(0, 0))
        Assert.assertEquals(true, target.shouldDrawZone(1, 0))
        Assert.assertEquals(true, target.shouldDrawZone(2, 0))
        Assert.assertEquals(false, target.shouldDrawZone(0, 1))
        Assert.assertEquals(true, target.shouldDrawZone(1, 1))
        Assert.assertEquals(true, target.shouldDrawZone(2, 1))
        Assert.assertEquals(true, target.shouldDrawZone(0, 2))
        Assert.assertEquals(false, target.shouldDrawZone(1, 2))
        Assert.assertEquals(true, target.shouldDrawZone(2, 2))
    }
}
