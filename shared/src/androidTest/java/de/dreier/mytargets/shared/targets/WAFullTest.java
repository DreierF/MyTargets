package de.dreier.mytargets.shared.targets;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.dreier.mytargets.shared.targets.models.WAFull;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class WAFullTest {

    private WAFull target;

    @Before
    public void setUp() throws Exception {
        target = new WAFull();
    }

    @Test
    public void testScoringStyle() {
        Assert.assertEquals(10, target.getScoringStyle(0).getPointsByZone(0, 0));
        Assert.assertEquals(10, target.getScoringStyle(0).getPointsByZone(1, 0));
        Assert.assertEquals(9, target.getScoringStyle(1).getPointsByZone(1, 0));
    }
}