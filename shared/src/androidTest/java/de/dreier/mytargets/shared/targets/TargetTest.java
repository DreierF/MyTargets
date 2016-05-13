package de.dreier.mytargets.shared.targets;

import org.junit.Test;

public abstract class TargetTest<T extends TargetModelBase> {
    protected T target;

    @Test
    public void testGetCoordinateFromZone() {
        /*List<TargetModelBase.SelectableZone> zones = target.getSelectableZoneList(0, 0);
        for (TargetModelBase.SelectableZone zone : zones) {
            //assertCalculatedCoordinateIsInZone(zone);
        }*/
    }
}
