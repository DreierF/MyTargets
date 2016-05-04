package de.dreier.mytargets.shared.targets;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.dreier.mytargets.shared.models.Coordinate;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class DAIR3DTest {

    protected DAIR3D target;

    @Before
    public void setUp() throws Exception {
        target = new DAIR3D();
    }

    @Test
    public void testGetXFromZone() {
        for (int i = -1; i < target.getZoneCount(); i++) {
            assertCalculatedXisInZone(i);
        }
    }

    private void assertCalculatedXisInZone(int zone) {
        Coordinate coordinate = target.getCoordinateFromZone(zone);
        final float x = 500f + coordinate.x * 500f;
        final float y = 500f + coordinate.y * 500f;
        Assert.assertEquals("Calculated x (" + x + ", " + y + ") for zone " + zone + " is not in zone " + zone, zone, target.getZoneFromPoint(x, y));
    }

    @Test
    public void testScoringStyle() {
        /*Assert.assertEquals(target.getZonePoints(0, 0), 10);
        Assert.assertEquals(target.getZonePoints(0, 1), 10);
        Assert.assertEquals(target.getZonePoints(1, 1), 9);*/
    }
}