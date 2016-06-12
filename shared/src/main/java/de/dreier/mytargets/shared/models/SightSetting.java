/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models;

import static de.dreier.mytargets.shared.models.Dimension.Unit.METER;

public class SightSetting implements IIdSettable {
    public long bowId;
    public Dimension distance = new Dimension(18, METER);
    public String value = "";
    long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof SightSetting &&
                getClass().equals(another.getClass()) &&
                id == ((SightSetting) another).id;
    }
}
