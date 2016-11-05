package de.dreier.mytargets.shared.targets;

import org.junit.Assert;
import org.junit.Test;

import de.dreier.mytargets.shared.models.Dimension;

public class TargetModelBaseTest {
    @Test
    public void trivialTargetRealSize() throws Exception {
        for(int id = NFAAField.ID; id<=WAField3Spot.ID;id++) {
            TargetModelBase target = TargetFactory.getTarget(id);
            final Dimension diameter = new Dimension(40, Dimension.Unit.CENTIMETER);
            Dimension realSize = target.getRealSize(diameter);
            Assert.assertEquals("Real size "+realSize.toString()+" for target id "+
                    id+" does not match with expected value 40cm",
                    realSize, new Dimension(40, Dimension.Unit.CENTIMETER));
        }
    }

    @Test
    public void waFieldRealSize() throws Exception {
        TargetModelBase target = TargetFactory.getTarget(WAField.ID);
        final Dimension diameter = new Dimension(40, Dimension.Unit.CENTIMETER);
        Dimension realSize = target.getRealSize(diameter);
        Assert.assertEquals(realSize, new Dimension(40, Dimension.Unit.CENTIMETER));
    }

    @Test
    public void waVertical3SpotRealSize() throws Exception {
        TargetModelBase target = TargetFactory.getTarget(Vertical3Spot.ID);
        final Dimension diameter = new Dimension(40, Dimension.Unit.CENTIMETER);
        Dimension realSize = target.getRealSize(diameter);
        Assert.assertEquals(realSize, new Dimension(20, Dimension.Unit.CENTIMETER));
    }

    @Test
    public void waFullRealSize() throws Exception {
        TargetModelBase target = TargetFactory.getTarget(WAFull.ID);
        final Dimension diameter = new Dimension(40, Dimension.Unit.CENTIMETER);
        Dimension realSize = target.getRealSize(diameter);
        Assert.assertEquals(realSize, new Dimension(40, Dimension.Unit.CENTIMETER));
    }

    @Test
    public void wa6RingRealSize() throws Exception {
        TargetModelBase target = TargetFactory.getTarget(WA6Ring.ID);
        final Dimension diameter = new Dimension(40, Dimension.Unit.CENTIMETER);
        Dimension realSize = target.getRealSize(diameter);
        Assert.assertEquals(realSize, new Dimension(24, Dimension.Unit.CENTIMETER));
    }

    @Test
    public void wa5RingRealSize() throws Exception {
        TargetModelBase target = TargetFactory.getTarget(WA5Ring.ID);
        final Dimension diameter = new Dimension(40, Dimension.Unit.CENTIMETER);
        Dimension realSize = target.getRealSize(diameter);
        Assert.assertEquals(realSize, new Dimension(20, Dimension.Unit.CENTIMETER));
    }

    @Test
    public void wa3RingRealSize() throws Exception {
        TargetModelBase target = TargetFactory.getTarget(WA3Ring.ID);
        final Dimension diameter = new Dimension(40, Dimension.Unit.CENTIMETER);
        Dimension realSize = target.getRealSize(diameter);
        Assert.assertEquals(realSize, new Dimension(12, Dimension.Unit.CENTIMETER));
    }

}