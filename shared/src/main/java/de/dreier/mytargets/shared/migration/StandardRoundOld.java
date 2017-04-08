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

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.models.db.RoundTemplate;
import de.dreier.mytargets.shared.models.db.StandardRound;

public class StandardRoundOld {

    public long id;
    public int club;
    public String name;
    public boolean indoor;
    public List<RoundTemplate> rounds = new ArrayList<>();

    public StandardRoundOld() {}

    public StandardRoundOld(StandardRound round) {
        id = round.getId();
        club = round.club;
        indoor = false;
        rounds = round.getRounds();
    }

    public void insert(RoundTemplate template) {
        template.index = rounds.size();
        template.standardRound = id;
        rounds.add(template);
    }

    public void setId(long id) {
        this.id = id;
        for (RoundTemplate r : rounds) {
            r.standardRound = id;
        }
    }
}
