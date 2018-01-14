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

package de.dreier.mytargets.shared.models.db;

import android.support.annotation.NonNull;

import junit.framework.Assert;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.dreier.mytargets.shared.streamwrapper.Stream;

public class ShotTest {

    @Test
    public void testCompareTo() throws Exception {
        Assert.assertEquals("0,1,-1,-2",
                toSortedShotList(Shot.Companion.getNOTHING_SELECTED(), Shot.Companion.getMISS(), 0, 1));
        Assert.assertEquals("0,2,-1,-2",
                toSortedShotList(0, 2, Shot.Companion.getMISS(), Shot.Companion
                        .getNOTHING_SELECTED()));
    }

    private String toSortedShotList(@NonNull int... zones) {
        List<Shot> shots = new ArrayList<>(zones.length);
        for (int i = 0; i < zones.length; i++) {
            shots.add(new Shot(i));
            shots.get(i).setScoringRing(zones[i]);
        }
        Collections.sort(shots);
        return Stream.of(shots)
                .map(s -> String.valueOf(s.getScoringRing()))
                .joinToString(",");
    }
}
