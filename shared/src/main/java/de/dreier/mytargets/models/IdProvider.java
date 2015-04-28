package de.dreier.mytargets.models;

/**
 * Created by Florian on 13.03.2015.
 */
public abstract class IdProvider {
    public long id;
    public long getId() {
        return id;
    }

    public long getParentId() {
        return 0;
    }
}
