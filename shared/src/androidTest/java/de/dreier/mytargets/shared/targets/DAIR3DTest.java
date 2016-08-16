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
        Assert.assertEquals(8, target.getScoringStyle(0).getPointsByZone(0, 0));
        Assert.assertEquals(12, target.getScoringStyle(0).getPointsByZone(2, 0));
        Assert.assertEquals(14, target.getScoringStyle(1).getPointsByZone(1, 0));
    }
}