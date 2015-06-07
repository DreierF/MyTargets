package de.dreier.mytargets.shared.models;

import java.io.Serializable;

/**
 * Created by Florian on 13.03.2015.
 */
public abstract class IdProvider implements Serializable {
    public static final String ID = "_id";
    public long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getParentId() {
        return 0;
    }
}
