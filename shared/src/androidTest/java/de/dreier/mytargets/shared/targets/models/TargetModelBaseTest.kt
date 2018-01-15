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
import android.support.test.runner.AndroidJUnit4
import de.dreier.mytargets.shared.SharedApplicationInstance
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.targets.TargetFactory
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TargetModelBaseTest {

    @Before
    fun setUp() {
        SharedApplicationInstance.context = InstrumentationRegistry.getContext()
    }

    @Test
    @Throws(Exception::class)
    fun trivialTargetRealSize() {
        for (id in NFAAField.ID..WAField3Spot.ID) {
            val target = TargetFactory.getTarget(id.toInt())
            val diameter = Dimension(40f, Dimension.Unit.CENTIMETER)
            val realSize = target.getRealSize(diameter)
            Assert.assertEquals("Real size $realSize for target id $id does not match with expected value 40cm",
                    realSize, Dimension(40f, Dimension.Unit.CENTIMETER))
        }
    }

    @Test
    @Throws(Exception::class)
    fun waFieldRealSize() {
        val target = TargetFactory.getTarget(WAField.ID.toInt())
        val diameter = Dimension(40f, Dimension.Unit.CENTIMETER)
        val realSize = target.getRealSize(diameter)
        Assert.assertEquals(realSize, Dimension(40f, Dimension.Unit.CENTIMETER))
    }

    @Test
    @Throws(Exception::class)
    fun waVertical3SpotRealSize() {
        val target = TargetFactory.getTarget(WAVertical3Spot.ID.toInt())
        val diameter = Dimension(40f, Dimension.Unit.CENTIMETER)
        val realSize = target.getRealSize(diameter)
        Assert.assertEquals(realSize, Dimension(20f, Dimension.Unit.CENTIMETER))
    }

    @Test
    @Throws(Exception::class)
    fun waFullRealSize() {
        val target = TargetFactory.getTarget(WAFull.ID.toInt())
        val diameter = Dimension(40f, Dimension.Unit.CENTIMETER)
        val realSize = target.getRealSize(diameter)
        Assert.assertEquals(realSize, Dimension(40f, Dimension.Unit.CENTIMETER))
    }

    @Test
    @Throws(Exception::class)
    fun wa6RingRealSize() {
        val target = TargetFactory.getTarget(WA6Ring.ID.toInt())
        val diameter = Dimension(40f, Dimension.Unit.CENTIMETER)
        val realSize = target.getRealSize(diameter)
        Assert.assertEquals(realSize, Dimension(24f, Dimension.Unit.CENTIMETER))
    }

    @Test
    @Throws(Exception::class)
    fun wa5RingRealSize() {
        val target = TargetFactory.getTarget(WA5Ring.ID.toInt())
        val diameter = Dimension(40f, Dimension.Unit.CENTIMETER)
        val realSize = target.getRealSize(diameter)
        Assert.assertEquals(realSize, Dimension(20f, Dimension.Unit.CENTIMETER))
    }

    @Test
    @Throws(Exception::class)
    fun wa3RingRealSize() {
        val target = TargetFactory.getTarget(WA3Ring.ID.toInt())
        val diameter = Dimension(40f, Dimension.Unit.CENTIMETER)
        val realSize = target.getRealSize(diameter)
        Assert.assertEquals(realSize, Dimension(12f, Dimension.Unit.CENTIMETER))
    }

}
