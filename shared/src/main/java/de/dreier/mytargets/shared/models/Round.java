package de.dreier.mytargets.shared.models;

public class Round extends IdProvider {
    static final long serialVersionUID = 56L;

    public long training;
    public RoundTemplate info;
    public String comment;
    public int reachedPoints;
}
