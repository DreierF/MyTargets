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

package de.dreier.mytargets.shared.models.db;

import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import org.parceler.Parcel;
import org.threeten.bp.LocalTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.shared.models.IRecursiveModel;
import de.dreier.mytargets.shared.models.SelectableZone;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.utils.typeconverters.LocalTimeConverter;

@Parcel
@Table(database = AppDatabase.class)
public class End extends BaseModel implements IIdSettable, Comparable<End>, IRecursiveModel {

    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    Long id;

    @Column
    public int index;

    public List<EndImage> images;

    @ForeignKey(tableClass = Round.class, references = {
            @ForeignKeyReference(columnName = "round", columnType = Long.class, foreignKeyColumnName = "_id")},
            onDelete = ForeignKeyAction.CASCADE)
    public Long roundId;

    @Column
    public boolean exact;

    @Column(typeConverter = LocalTimeConverter.class)
    public LocalTime saveTime;

    @Column
    public String comment = "";

    List<Shot> shots;

    public End() {
    }

    public End(int shotCount, int index) {
        this.index = index;
        shots = new ArrayList<>();
        for (int i = 0; i < shotCount; i++) {
            shots.add(new Shot(i));
        }
    }

    public End(End end) {
        this.index = end.index;
        this.shots = new ArrayList<>(end.getShots());
    }

    @NonNull
    @OneToMany(methods = {}, variableName = "shots")
    public List<Shot> getShots() {
        if (shots == null) {
            shots = SQLite.select()
                    .from(Shot.class)
                    .where(Shot_Table.end.eq(id))
                    .queryList();
        }
        return shots;
    }

    @OneToMany(methods = {}, variableName = "images")
    public List<EndImage> getImages() {
        if (images == null) {
            images = SQLite.select()
                    .from(EndImage.class)
                    .where(EndImage_Table.end.eq(id))
                    .queryList();
        }
        return images;
    }

    public void setShots(List<Shot> shots) {
        this.shots = shots;
    }

    @NonNull
    private static Map<SelectableZone, Integer> getRoundScores(List<Round> rounds) {
        final Target t = rounds.get(0).getTarget();
        Map<SelectableZone, Integer> scoreCount = getAllPossibleZones(t);
        for (Round round : rounds) {
            for (End end : round.getEnds()) {
                for (Shot s : end.getShots()) {
                    if (s.scoringRing != Shot.NOTHING_SELECTED) {
                        SelectableZone tuple = new SelectableZone(s.scoringRing,
                                t.getModel().getZone(s.scoringRing),
                                t.zoneToString(s.scoringRing, s.index),
                                t.getScoreByZone(s.scoringRing, s.index));
                        final Integer integer = scoreCount.get(tuple);
                        if (integer != null) {
                            int count = integer + 1;
                            scoreCount.put(tuple, count);
                        }
                    }
                }
            }
        }
        return scoreCount;
    }

    @NonNull
    private static Map<SelectableZone, Integer> getAllPossibleZones(Target t) {
        Map<SelectableZone, Integer> scoreCount = new HashMap<>();
        for (int arrow = 0; arrow < 3; arrow++) {
            final List<SelectableZone> zoneList = t.getSelectableZoneList(arrow);
            for (SelectableZone selectableZone : zoneList) {
                scoreCount.put(selectableZone, 0);
            }
            if (!t.getModel().dependsOnArrowIndex()) {
                break;
            }
        }
        return scoreCount;
    }

    public static List<Pair<String, Integer>> getTopScoreDistribution(List<Map.Entry<SelectableZone, Integer>> sortedScore) {
        final List<Pair<String, Integer>> result = Stream.of(sortedScore)
                .map(value -> new Pair<>(value.getKey().text, value.getValue()))
                .collect(Collectors.toList());

        // Collapse first two entries if they yield the same score points,
        // e.g. 10 and X => {X, 10+X, 9, ...}
        if (sortedScore.size() > 1) {
            Map.Entry<SelectableZone, Integer> first = sortedScore.get(0);
            Map.Entry<SelectableZone, Integer> second = sortedScore.get(1);
            if (first.getKey().points == second.getKey().points) {
                final String newTitle = second.getKey().text + "+" + first.getKey().text;
                result.set(1, new Pair<>(newTitle, second.getValue() + first.getValue()));
            }
        }
        return result;
    }

    /**
     * Compound 9ers are already collapsed to one SelectableZone.
     */
    public static List<Map.Entry<SelectableZone, Integer>> getSortedScoreDistribution(List<Round> rounds) {
        Map<SelectableZone, Integer> scoreCount = getRoundScores(rounds);
        return Stream.of(scoreCount)
                .sorted((lhs, rhs) -> lhs.getKey().compareTo(rhs.getKey()))
                .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void save() {
        FlowManager.getDatabase(AppDatabase.class).executeTransaction(this::save);
    }

    @Override
    public void save(DatabaseWrapper databaseWrapper) {
        if (saveTime == null) {
            saveTime = LocalTime.now();
        }
        super.save(databaseWrapper);
        if (shots != null) {
            SQLite.delete(Shot.class)
                    .where(Shot_Table.end.eq(id))
                    .execute(databaseWrapper);
            // TODO Replace this super ugly workaround by stubbed Relationship in version 4 of dbFlow
            for (Shot s : shots) {
                s.endId = id;
                s.save(databaseWrapper);
            }
        }
        if (images != null) {
            SQLite.delete(EndImage.class)
                    .where(EndImage_Table.end.eq(id))
                    .execute(databaseWrapper);
            // TODO Replace this super ugly workaround by stubbed Relationship in version 4 of dbFlow
            for (EndImage image : getImages()) {
                image.endId = id;
                image.save(databaseWrapper);
            }
        }
    }

    @Override
    public void delete() {
        FlowManager.getDatabase(AppDatabase.class).executeTransaction(this::delete);
    }

    @Override
    public void delete(DatabaseWrapper databaseWrapper) {
        for (Shot shot : getShots()) {
            shot.delete(databaseWrapper);
        }
        for (EndImage endImage : getImages()) {
            endImage.delete(databaseWrapper);
        }
        super.delete(databaseWrapper);
        updateEndIndicesForRound(databaseWrapper);
    }

    private void updateEndIndicesForRound(DatabaseWrapper databaseWrapper) {
        // FIXME very inefficient
        Round round = Round.get(roundId);
        if (round == null) {
            return;
        }

        int i = 0;
        for (End end : round.getEnds(databaseWrapper)) {
            end.index = i;
            end.save(databaseWrapper);
            i++;
        }
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof End &&
                getClass().equals(another.getClass()) &&
                id.equals(((End) another).id);
    }

    @Override
    public int compareTo(@NonNull End end) {
        return index - end.index;
    }

    public boolean isEmpty() {
        return Stream.of(getShots())
                .anyMatch(s -> s.scoringRing == Shot.NOTHING_SELECTED) && getImages().isEmpty();
    }

    @Override
    public void saveRecursively() {
        FlowManager.getDatabase(AppDatabase.class).executeTransaction(this::saveRecursively);
    }

    @Override
    public void saveRecursively(DatabaseWrapper databaseWrapper) {
        Round round = Round.get(roundId);
        List<End> ends = round.getEnds(databaseWrapper);

        int pos = Collections.binarySearch(ends, this);
        if (pos < 0) {
            ends.add(-pos - 1, this);
        } else {
            ends.add(pos, this);
        }

        int i = 0;
        for (End end : ends) {
            end.index = i;
            end.save(databaseWrapper);
            i++;
        }
    }
}
