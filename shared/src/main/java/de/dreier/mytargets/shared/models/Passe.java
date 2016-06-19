package de.dreier.mytargets.shared.models;

import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.List;

public class Passe implements IIdSettable {

    long id;
    public int index;
    public long roundId;
    public Shot[] shot;
    public boolean exact;
    public DateTime saveDate = new DateTime();

    public Passe() {
    }

    public Passe(int ppp) {
        shot = new Shot[ppp];
        for (int i = 0; i < ppp; i++) {
            shot[i] = new Shot(i);
            shot[i].index = i;
        }
    }

    public Passe(Passe p) {
        id = p.id;
        roundId = p.roundId;
        index = p.index;
        exact = p.exact;
        shot = p.shot.clone();
    }

    public List<Shot> shotList() {
        return Arrays.asList(shot);
    }

    public void sort() {
        Arrays.sort(shot);
        for (int i = 0; i < shot.length; i++) {
            shot[i].index = i;
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
        for (Shot s : shot) {
            s.passe = id;
        }
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Passe &&
                getClass().equals(another.getClass()) &&
                id == ((Passe) another).id;
    }

    public int getReachedPoints(Target target) {
        return target.getReachedPoints(this);
    }
}
