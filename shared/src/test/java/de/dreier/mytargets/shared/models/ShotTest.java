package de.dreier.mytargets.shared.models;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import junit.framework.Assert;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.dreier.mytargets.shared.models.db.Shot;

public class ShotTest {

    @Test
    public void testCompareTo() throws Exception {
        Assert.assertEquals("0,1,-1,-2",
                toSortedShotList(Shot.NOTHING_SELECTED, Shot.MISS, 0, 1));
        Assert.assertEquals("0,2,-1,-2",
                toSortedShotList(0, 2, Shot.MISS, Shot.NOTHING_SELECTED));
    }

    private String toSortedShotList(int... zones) {
        List<Shot> shots = new ArrayList<>(zones.length);
        for (int i = 0; i < zones.length; i++) {
            shots.add(new Shot(i));
            shots.get(i).zone = zones[i];
        }
        Collections.sort(shots);
        return Stream.of(shots)
                .map(s -> String.valueOf(s.zone))
                .collect(Collectors.joining(","));
    }
}