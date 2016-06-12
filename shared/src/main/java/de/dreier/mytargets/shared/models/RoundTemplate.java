package de.dreier.mytargets.shared.models;

public class RoundTemplate implements IIdSettable {
    public long standardRound;
    public int index;
    public int arrowsPerPasse;
    public Target target;
    public Dimension distance;
    public int passes;
    public Target targetTemplate;
    long id;

    public int getMaxPoints() {
        return target.getEndMaxPoints(arrowsPerPasse) * passes;
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
