package de.dreier.mytargets.shared.models;

import de.dreier.mytargets.shared.models.target.Target;

public class RoundTemplate extends IdProvider {
    static final long serialVersionUID = 56L;

    public long standardRound;
    public int index;
    public int arrowsPerPasse;
    public Target target;
    public Distance distance;
    public int passes;
    public Target targetTemplate;

    public int getMaxPoints() {
        return target.getMaxPoints() * passes * arrowsPerPasse;
    }
}
