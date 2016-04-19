package de.dreier.mytargets.shared.models;

import org.parceler.Parcel;

@Parcel
public class RoundTemplate implements IIdSettable {
    public long standardRound;
    public int index;
    public int arrowsPerPasse;
    public Target target;
    public Distance distance;
    public int passes;
    public Target targetTemplate;
    protected long id;

    public int getMaxPoints() {
        return target.getDrawable().getMaxPoints() * passes * arrowsPerPasse;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof RoundTemplate &&
                getClass().equals(another.getClass()) &&
                id == ((RoundTemplate) another).id;
    }
}
