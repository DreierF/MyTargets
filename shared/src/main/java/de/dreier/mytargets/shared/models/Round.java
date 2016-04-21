package de.dreier.mytargets.shared.models;

import org.parceler.Parcel;

@Parcel
public class Round implements IIdSettable {
    public long training;
    public RoundTemplate info;
    public String comment;
    public int reachedPoints;
    protected long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Round &&
                getClass().equals(another.getClass()) &&
                id == ((Round) another).id;
    }
}
