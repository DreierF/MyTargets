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

package de.dreier.mytargets.shared.targets.models;

import org.junit.Assert;
import org.junit.Test;

import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.targets.TargetFactory;

public class TargetModelBaseTest {
    @Test
    public void trivialTargetRealSize() throws Exception {
        for (int id = NFAAField.Companion.getID(); id <= WAField3Spot.Companion.getID(); id++) {
            TargetModelBase target = TargetFactory.INSTANCE.getTarget(id);
            final Dimension diameter = new Dimension(40, Dimension.Unit.CENTIMETER);
            Dimension realSize = target.getRealSize(diameter);
            Assert.assertEquals("Real size " + realSize.toString() + " for target id " +
                            id + " does not match with expected value 40cm",
                    realSize, new Dimension(40, Dimension.Unit.CENTIMETER));
        }
    }

    @Test
    public void waFieldRealSize() throws Exception {
        TargetModelBase target = TargetFactory.INSTANCE.getTarget(WAField.Companion.getID());
        final Dimension diameter = new Dimension(40, Dimension.Unit.CENTIMETER);
        Dimension realSize = target.getRealSize(diameter);
        Assert.assertEquals(realSize, new Dimension(40, Dimension.Unit.CENTIMETER));
    }

    @Test
    public void waVertical3SpotRealSize() throws Exception {
        TargetModelBase target = TargetFactory.INSTANCE.getTarget(WAVertical3Spot.Companion.getID());
        final Dimension diameter = new Dimension(40, Dimension.Unit.CENTIMETER);
        Dimension realSize = target.getRealSize(diameter);
        Assert.assertEquals(realSize, new Dimension(20, Dimension.Unit.CENTIMETER));
    }

    @Test
    public void waFullRealSize() throws Exception {
        TargetModelBase target = TargetFactory.INSTANCE.getTarget(WAFull.Companion.getID());
        final Dimension diameter = new Dimension(40, Dimension.Unit.CENTIMETER);
        Dimension realSize = target.getRealSize(diameter);
        Assert.assertEquals(realSize, new Dimension(40, Dimension.Unit.CENTIMETER));
    }

    @Test
    public void wa6RingRealSize() throws Exception {
        TargetModelBase target = TargetFactory.INSTANCE.getTarget(WA6Ring.Companion.getID());
        final Dimension diameter = new Dimension(40, Dimension.Unit.CENTIMETER);
        Dimension realSize = target.getRealSize(diameter);
        Assert.assertEquals(realSize, new Dimension(24, Dimension.Unit.CENTIMETER));
    }

    @Test
    public void wa5RingRealSize() throws Exception {
        TargetModelBase target = TargetFactory.INSTANCE.getTarget(WA5Ring.Companion.getID());
        final Dimension diameter = new Dimension(40, Dimension.Unit.CENTIMETER);
        Dimension realSize = target.getRealSize(diameter);
        Assert.assertEquals(realSize, new Dimension(20, Dimension.Unit.CENTIMETER));
    }

    @Test
    public void wa3RingRealSize() throws Exception {
        TargetModelBase target = TargetFactory.INSTANCE.getTarget(WA3Ring.Companion.getID());
        final Dimension diameter = new Dimension(40, Dimension.Unit.CENTIMETER);
        Dimension realSize = target.getRealSize(diameter);
        Assert.assertEquals(realSize, new Dimension(12, Dimension.Unit.CENTIMETER));
    }

}
