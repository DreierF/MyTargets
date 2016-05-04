package de.dreier.mytargets.shared.targets;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class WAFullTest extends TargetTest<WAFull> {

    @Before
    public void setUp() throws Exception {
        target = new WAFull();
    }

    @Test
    public void testScoringStyle() {
        Assert.assertEquals(target.getZonePoints(0, 0), 10);
        Assert.assertEquals(target.getZonePoints(0, 1), 10);
        Assert.assertEquals(target.getZonePoints(1, 1), 9);
    }
}