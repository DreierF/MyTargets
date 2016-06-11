package de.dreier.mytargets.shared.targets;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class Vertical3SpotTest {

    private Vertical3Spot target;

    @Before
    public void setUp() throws Exception {
        target = new Vertical3Spot();
    }

    @Test
    public void testShouldDrawZone() throws Exception {
        Assert.assertEquals(true, target.shouldDrawZone(1, 0));
        Assert.assertEquals(true, target.shouldDrawZone(0, 1));
        Assert.assertEquals(false, target.shouldDrawZone(1, 1));
        Assert.assertEquals(true, target.shouldDrawZone(2, 1));
    }
}