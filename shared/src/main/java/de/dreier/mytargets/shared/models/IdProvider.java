package de.dreier.mytargets.shared.models;

import java.io.Serializable;

/**
 * Created by Florian on 13.03.2015.
 */
public abstract class IdProvider implements Serializable {
    public long id;
    public long getId() {
        return id;
    }

    public long getParentId() {
        return 0;
    }
}
