package de.dreier.mytargets.shared.models;

import java.util.Arrays;

public class Passe extends IdProvider {
    static final long serialVersionUID = 55L;

    public Shot[] shot;
    public long roundId;
    public int index;
    public boolean exact;

    public Passe(int ppp) {
        shot = new Shot[ppp];
        for (int i = 0; i < ppp; i++) {
            shot[i] = new Shot(i);
        }
    }

    public Passe(Passe p) {
        super.setId(p.getId());
        shot = p.shot.clone();
    }

    @Override
    public void setId(long id) {
        super.setId(id);
        for (Shot s : shot) {
            s.passe = id;
        }
    }

    @Override
    public long getParentId() {
        return roundId;
    }

    public void sort() {
        Arrays.sort(shot);
        for (int i = 0; i < shot.length; i++) {
            shot[i].index = i;
        }
    }
}
