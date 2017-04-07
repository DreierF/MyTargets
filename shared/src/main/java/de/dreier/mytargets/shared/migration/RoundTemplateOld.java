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

import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.db.RoundTemplate;

public class RoundTemplateOld {
    public long id;
    public long standardRound;
    public int index;
    public int arrowsPerPasse;
    public Target target;
    public Dimension distance;
    public int passes;
    public Target targetTemplate;

    public RoundTemplateOld() {}

    public RoundTemplateOld(RoundTemplate roundTemplate) {
        id = roundTemplate.getId();
        standardRound = roundTemplate.standardRound;
        index = roundTemplate.index;
        arrowsPerPasse = roundTemplate.shotsPerEnd;
        target = roundTemplate.getTargetTemplate();
        distance = roundTemplate.distance;
        passes = roundTemplate.endCount;
        targetTemplate = roundTemplate.getTargetTemplate();
    }
}
