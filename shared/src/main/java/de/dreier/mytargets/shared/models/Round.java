package de.dreier.mytargets.shared.models;

import android.support.annotation.NonNull;

public class Round implements IIdSettable, Comparable<Round> {
    long id;
    public long trainingId;
    public RoundTemplate info;
    public String comment;
    public int reachedPoints;

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

    public String getReachedPointsFormatted() {
        final int maxPoints = info.getMaxPoints();
        return reachedPoints + "/" + maxPoints;
    }

    @Override
    public int compareTo(@NonNull Round round) {
        return info.index - round.info.index;
    }
}
