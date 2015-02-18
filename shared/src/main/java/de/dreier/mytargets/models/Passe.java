package de.dreier.mytargets.models;

import java.io.Serializable;

// TODO refactor to Shoot[]
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

    public void sort() {
        for (int n = zones.length; n > 1; n--) {
            for (int i = 0; i < n - 1; i++) {
                if ((zones[i] > zones[i + 1] && zones[i + 1] != -1) || zones[i] == -1) {
                    int tmp = zones[i];
                    float[] coords = points[i];
                    String com = comment[i];
                    zones[i] = zones[i + 1];
                    points[i] = points[i + 1];
                    comment[i] = comment[i + 1];
                    zones[i + 1] = tmp;
                    points[i + 1] = coords;
                    comment[i + 1] = com;
                }
            }
        }
    }
}
