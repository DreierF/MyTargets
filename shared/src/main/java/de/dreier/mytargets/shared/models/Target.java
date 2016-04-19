package de.dreier.mytargets.shared.models;

import org.parceler.Parcel;

import de.dreier.mytargets.shared.models.target.TargetDrawable;
import de.dreier.mytargets.shared.models.target.TargetFactory;

@Parcel
public class Target implements IIdSettable {
    public long id;
    public int scoringStyle;
    public Diameter size;
    public Target() {
    }
    public Target(long target, int scoringStyle, Diameter diameter) {
        this.id = target;
        this.scoringStyle = scoringStyle;
        this.size = diameter;
    }

    public Target(long target, int scoringStyle) {
        this.id = target;
        this.scoringStyle = scoringStyle;
        this.size = TargetFactory.createTarget(id, scoringStyle).getDiameters()[0];
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public TargetDrawable getDrawable() {
        TargetDrawable target = TargetFactory.createTarget(id, scoringStyle);
        target.target = this;
        return target;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof StandardRound &&
                getClass().equals(another.getClass()) &&
                id == ((StandardRound) another).id;
    }
}