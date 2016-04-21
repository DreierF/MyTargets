package de.dreier.mytargets.shared.models;

import org.parceler.Parcel;

import de.dreier.mytargets.shared.targets.TargetDrawable;
import de.dreier.mytargets.shared.targets.TargetFactory;
import de.dreier.mytargets.shared.targets.TargetModelBase;

@Parcel
public class Target implements IIdProvider {
    public int id;
    public int scoringStyle;
    public Diameter size;
    private transient TargetModelBase model;
    private transient TargetDrawable drawable;

    public Target() {
    }

    public Target(int target, int scoringStyle) {
        this(target, scoringStyle, null);
        this.size = getModel().getDiameters()[0];
    }

    public Target(int target, int scoringStyle, Diameter diameter) {
        this.id = target;
        this.model = TargetFactory.getTarget(target);
        this.scoringStyle = scoringStyle;
        this.size = diameter;
    }

    public long getId() {
        return id;
    }

    /*public void setId(long id) {
        this.id = (int) id;
    }*/

    public TargetDrawable getDrawable() {
        if (drawable == null) {
            drawable = new TargetDrawable(this);
        }
        return drawable;
    }

    public String zoneToString(int zone, int arrow) {
        return getModel().zoneToString(zone, scoringStyle, arrow);
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Target &&
                getClass().equals(another.getClass()) &&
                id == ((Target) another).id;
    }

    public int getMaxPoints() {
        //FIXME
        return Math.max(getModel().getZonePoints(scoringStyle, 0), getModel().getZonePoints(scoringStyle, 1));
    }

    public int getZonePoints(int zone) {
        return getModel().getZonePoints(scoringStyle, zone);
    }

    public TargetModelBase getModel() {
        if (model == null) {
            model = TargetFactory.getTarget(id);
        }
        return model;
    }


    public int getPointsByZone(int zone, int arrow) {
        return model.getPointsByZone(zone, scoringStyle, arrow);
    }
}