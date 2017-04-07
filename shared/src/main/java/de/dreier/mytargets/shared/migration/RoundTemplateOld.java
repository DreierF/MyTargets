/*
 * Copyright (C) 2017 Florian Dreier
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

package de.dreier.mytargets.shared.migration;

import de.dreier.mytargets.shared.models.Target;

public class RoundTemplateOld implements IIdSettableOld {
    public long standardRound;
    public int index;
    public int arrowsPerPasse;
    public Target target;
    public DistanceOld distance;
    public int passes;
    public Target targetTemplate;
    protected long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof RoundTemplateOld &&
                getClass().equals(another.getClass()) &&
                id == ((RoundTemplateOld) another).id;
    }
}
