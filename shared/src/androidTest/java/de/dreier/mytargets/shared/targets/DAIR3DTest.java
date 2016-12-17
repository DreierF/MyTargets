/*
 * Copyright (C) 2016 Florian Dreier
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

package de.dreier.mytargets.shared.targets;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.dreier.mytargets.shared.targets.models.DAIR3D;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class DAIR3DTest {

    private DAIR3D target;

    @Before
    public void setUp() throws Exception {
        target = new DAIR3D();
    }

    @Test
    public void testScoringStyle() {
        Assert.assertEquals(8, target.getScoringStyle(0).getScoreByScoringRing(0, 0));
        Assert.assertEquals(12, target.getScoringStyle(0).getScoreByScoringRing(2, 0));
        Assert.assertEquals(14, target.getScoringStyle(1).getScoreByScoringRing(1, 0));
    }
}