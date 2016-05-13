package de.dreier.mytargets.shared.targets;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class Vertical3SpotTest {

    private Vertical3Spot target;

    @Before
    public void setUp() throws Exception {
        target = new Vertical3Spot();
    }

    @Test
    public void testShouldDrawZone() throws Exception {
        Assert.assertEquals(target.shouldDrawZone(1, 0), true);
        Assert.assertEquals(target.shouldDrawZone(0, 1), true);
        Assert.assertEquals(target.shouldDrawZone(1, 1), false);
        Assert.assertEquals(target.shouldDrawZone(2, 1), true);
    }
}