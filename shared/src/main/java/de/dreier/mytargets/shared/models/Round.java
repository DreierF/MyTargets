package de.dreier.mytargets.shared.models;

public class Round implements IIdSettable {
    public long trainingId;
    public RoundTemplate info;
    public String comment;
    public int reachedPoints;
    long id;

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
        String percent = maxPoints == 0 ? "" : " (" + (reachedPoints * 100 / maxPoints) + "%)";
        return reachedPoints + "/" + maxPoints + percent;
    }
}
