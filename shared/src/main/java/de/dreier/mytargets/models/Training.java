package de.dreier.mytargets.models;

import java.util.Date;

public class Training extends IdProvider {
    public String title = "";
    public Date date = new Date();
    public int reachedPoints;
    public int maxPoints;
}
