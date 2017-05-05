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

import java.util.List;

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.shared.models.IRecursiveModel;
import de.dreier.mytargets.shared.models.Score;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.utils.LongUtils;
import de.dreier.mytargets.shared.utils.typeconverters.DimensionConverter;

@Parcel
@Table(database = AppDatabase.class)
public class Round extends BaseModel implements IIdSettable, Comparable<Round>, IRecursiveModel {

    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    Long id;

    @ForeignKey(tableClass = Training.class, references = {
            @ForeignKeyReference(columnName = "training", columnType = Long.class, foreignKeyColumnName = "_id")},
            onDelete = ForeignKeyAction.CASCADE)
    public Long trainingId;

    @Column
    public int index;

    @Column
    public int shotsPerEnd;

    @Column
    public Integer maxEndCount;

    @Column(typeConverter = DimensionConverter.class)
    public Dimension distance;

    @Column
    public String comment = "";

    @Column
    int targetId;

    @Column
    int targetScoringStyle;

    @Column(typeConverter = DimensionConverter.class)
    Dimension targetDiameter;

    public List<End> ends;

    public Round() {

    }

    public Round(RoundTemplate info) {
        distance = info.distance;
        shotsPerEnd = info.shotsPerEnd;
        maxEndCount = info.endCount;
        index = info.index;
        setTarget(info.getTargetTemplate());
    }

    public Round(Round round) {
        this.id = round.id;
        this.trainingId = round.trainingId;
        this.index = round.index;
        this.shotsPerEnd = round.shotsPerEnd;
        this.maxEndCount = round.maxEndCount;
        this.distance = round.distance;
        this.comment = round.comment;
        this.targetId = round.targetId;
        this.targetScoringStyle = round.targetScoringStyle;
        this.targetDiameter = round.targetDiameter;
        this.ends = round.ends;
    }

    public static Round get(Long id) {
        return SQLite.select()
                .from(Round.class)
                .where(Round_Table._id.eq(id))
                .querySingle();
    }

    public static List<Round> getAll(long[] roundIds) {
        return SQLite.select()
                .from(Round.class)
                .where(Round_Table._id.in(LongUtils.toList(roundIds)))
                .queryList();
    }

    @Override
    public void delete() {
        FlowManager.getDatabase(AppDatabase.class).executeTransaction(this::delete);
    }

    @Override
    public void delete(DatabaseWrapper databaseWrapper) {
        getEnds().forEach(e -> e.delete(databaseWrapper));
        super.delete(databaseWrapper);
        updateRoundIndicesForTraining();
    }

    private void updateRoundIndicesForTraining() {
        // TODO very inefficient
        int i = 0;
        Training training = Training.get(trainingId);
        if (training == null) {
            return; //FIXME This should not happen, but does for some users
        }
        for (Round r : training.getRounds()) {
            r.index = i;
            r.save();
            i++;
        }
    }

    public Target getTarget() {
        return new Target(targetId, targetScoringStyle, targetDiameter);
    }

    public void setTarget(Target targetTemplate) {
        targetId = targetTemplate.id;
        targetScoringStyle = targetTemplate.scoringStyle;
        targetDiameter = targetTemplate.size;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Round &&
                getClass().equals(another.getClass()) &&
                id.equals(((Round) another).id);
    }

    @OneToMany(methods = {}, variableName = "ends")
    public List<End> getEnds() {
        if (ends == null) {
            ends = SQLite.select()
                    .from(End.class)
                    .where(End_Table.round.eq(id))
                    .queryList();
        }
        return ends;
    }

    public Score getReachedScore() {
        final Target target = getTarget();
        return Stream.of(getEnds())
                .map(target::getReachedScore)
                .collect(Score.sum());
    }

    @Override
    public int compareTo(@NonNull Round round) {
        return index - round.index;
    }

    /**
     * Adds a new end to the internal list of ends, but does not yet save it.
     *
     * @return Returns the newly created end
     */
    public End addEnd() {
        End end = new End(shotsPerEnd, getEnds().size());
        end.roundId = id;
        end.save();
        getEnds().add(end);
        return end;
    }

    public Training getTraining() {
        return Training.get(trainingId);
    }

    @Override
    public void saveRecursively() {
        FlowManager.getDatabase(AppDatabase.class).executeTransaction(this::saveRecursively);
    }

    @Override
    public void saveRecursively(DatabaseWrapper databaseWrapper) {
        save(databaseWrapper);
        for (End end : getEnds()) {
            end.roundId = id;
            end.save(databaseWrapper);
        }
    }
}
