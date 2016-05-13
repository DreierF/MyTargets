package de.dreier.mytargets.shared.targets;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class DAIR3DTest extends TargetTest<DAIR3D> {

    @Before
    public void setUp() throws Exception {
        target = new DAIR3D();
    }

    @Test
    public void testScoringStyle() {
        /*Assert.assertEquals(target.getZonePoints(0, 0), 10);
        Assert.assertEquals(target.getZonePoints(0, 1), 10);
        Assert.assertEquals(target.getZonePoints(1, 1), 9);*/
    }
}