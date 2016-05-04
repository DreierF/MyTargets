package de.dreier.mytargets.shared.targets;

import org.junit.Assert;
import org.junit.Test;

public class TargetTest<T extends TargetModelBase> {
    protected T target;

    @Test
    public void testGetXFromZone() {
        for (int i = -1; i < target.getZoneCount(); i++) {
            assertCalculatedXisInZone(i);
        }
    }

    private void assertCalculatedXisInZone(int zone) {
        final float x = 500f + target.getXFromZone(zone) * 500f;
        Assert.assertEquals("Calculated x (" + x + ", 500) for zone " + zone + " is not in zone " + zone, zone, target.getZoneFromPoint(x, 500));
    }
}
