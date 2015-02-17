package de.dreier.mytargets.models;

import java.io.Serializable;

/**
* Created by Florian on 07.02.2015.
*/
public class Passe implements Serializable {
    public int[] zones;
    public float[][] points;
    public String[] comment;

    public Passe(int count) {
        zones = new int[count];
        points = new float[count][];
        for (int i = 0; i < count; i++)
            points[i] = new float[2];
        comment = new String[count];
    }

    public Passe(Passe p) {
        zones = p.zones.clone();
        points = p.points.clone();
        comment = p.comment.clone();
    }
}
