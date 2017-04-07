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

import de.dreier.mytargets.shared.models.db.StandardRound;

public class StandardRoundOld implements IIdSettableOld {

    public long id;
    public int club;
    public String name;
    public boolean indoor;
    public ArrayList<RoundTemplateOld> rounds = new ArrayList<>();

    public StandardRoundOld(StandardRound round) {
        id=round.getId();
        club=round.club;
        indoor=false;

    }

    public StandardRoundOld() {

    }

    public ArrayList<RoundTemplateOld> getRounds() {
        return rounds;
    }

    public void insert(RoundTemplateOld template) {
        template.index = rounds.size();
        template.standardRound = id;
        rounds.add(template);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
        for (RoundTemplateOld r : rounds) {
            r.standardRound = id;
        }
    }

    public String getName() {
        if (name != null) {
            return name;
        }
        return "";
    }

    public void setRounds(ArrayList<RoundTemplateOld> rounds) {
        this.rounds = rounds;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof StandardRoundOld &&
                getClass().equals(another.getClass()) &&
                id == ((StandardRoundOld) another).id;
    }
}
