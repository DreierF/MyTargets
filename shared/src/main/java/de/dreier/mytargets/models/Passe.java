package de.dreier.mytargets.models;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Florian on 13.03.2015.
 */
public class Passe extends IdProvider implements Serializable {
    static final long serialVersionUID = 45L;
    public Shot[] shot;

    public Passe(int ppp) {
        shot = new Shot[ppp];
        for (int i = 0; i < ppp; i++) {
            shot[i] = new Shot();
        }
    }

    public Passe(Passe p) {
        id = p.id;
        shot = p.shot.clone();
    }

    public void sort() {
        Arrays.sort(shot);
    }
}
