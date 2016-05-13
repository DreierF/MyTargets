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
        Assert.assertEquals(target.getScoringStyle(0).getPoints(0, 0), 10);
        Assert.assertEquals(target.getScoringStyle(0).getPoints(1, 0), 10);
        Assert.assertEquals(target.getScoringStyle(1).getPoints(1, 0), 9);
    }
}