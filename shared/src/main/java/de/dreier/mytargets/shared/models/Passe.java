package de.dreier.mytargets.shared.models;

import org.parceler.Parcel;

import java.util.Arrays;

@Parcel
public class Passe implements IIdSettable {
    protected long id;
    public Shot[] shot;
    public long roundId;
    public int index;
    public boolean exact;

    public Passe() {}

    public Passe(int ppp) {
        shot = new Shot[ppp];
        for (int i = 0; i < ppp; i++) {
            shot[i] = new Shot(i);
        }
    }

    public Passe(Passe p) {
        this.id = p.getId();
        shot = p.shot.clone();
    }

    public void setId(long id) {
        this.id = id;
        for (Shot s : shot) {
            s.passe = id;
        }
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

    @Override
    public boolean equals(Object another) {
        return another instanceof Passe &&
                getClass().equals(another.getClass()) &&
                id == ((Passe) another).id;
    }
}
