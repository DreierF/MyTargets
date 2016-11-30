/*
 * Copyright (C) 2016 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
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
