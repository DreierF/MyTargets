package de.dreier.mytargets.models;

import java.io.Serializable;

public class Round extends IdProvider implements Serializable {
    static final long serialVersionUID = 42L;

    public int ppp;
    public int target;
    public long training;
    public boolean indoor;
    public String distance;
    public int distanceVal;
    public String unit;
    public long bow;
    public int[] scoreCount = new int[3];
    public boolean compound;
    public String comment;
    public long arrow;
    public int reachedPoints;
    public int maxPoints;
}
