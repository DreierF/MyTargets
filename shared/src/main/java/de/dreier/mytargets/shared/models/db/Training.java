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
import android.support.annotation.Nullable;

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
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.models.EWeather;
import de.dreier.mytargets.shared.models.Environment;
import de.dreier.mytargets.shared.models.IIdSettable;
import de.dreier.mytargets.shared.models.IRecursiveModel;
import de.dreier.mytargets.shared.models.Score;
import de.dreier.mytargets.shared.utils.typeconverters.EWeatherConverter;
import de.dreier.mytargets.shared.utils.typeconverters.LocalDateConverter;

@Parcel
@Table(database = AppDatabase.class)
public class Training extends BaseModel implements IIdSettable, Comparable<Training>, IRecursiveModel {

    @Nullable
    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    Long id;

    @Nullable
    @Column
    public String title = "";

    @Nullable
    @Column(typeConverter = LocalDateConverter.class)
    public LocalDate date;

    @Nullable
    @ForeignKey(tableClass = StandardRound.class, references = {
            @ForeignKeyReference(columnName = "standardRound", columnType = Long.class, foreignKeyColumnName = "_id")},
            onDelete = ForeignKeyAction.SET_NULL)
    public Long standardRoundId;

    @Nullable
    @ForeignKey(tableClass = Bow.class, references = {
            @ForeignKeyReference(columnName = "bow", columnType = Long.class, foreignKeyColumnName = "_id")},
            onDelete = ForeignKeyAction.SET_NULL)
    public Long bowId;

    @Nullable
    @ForeignKey(tableClass = Arrow.class, references = {
            @ForeignKeyReference(columnName = "arrow", columnType = Long.class, foreignKeyColumnName = "_id")},
            onDelete = ForeignKeyAction.SET_NULL)
    public Long arrowId;

    @Column
    public boolean arrowNumbering;

    @Column
    public boolean indoor;

    @Nullable
    @Column(typeConverter = EWeatherConverter.class)
    public EWeather weather;

    @Column
    public int windDirection;

    @Column
    public int windSpeed;

    @Nullable
    @Column
    public String location = "";

    @Nullable
    @Column
    public String comment = "";

    public List<Round> rounds;

    @Nullable
    public static Training get(Long id) {
        return SQLite.select()
                .from(Training.class)
                .where(Training_Table._id.eq(id))
                .querySingle();
    }

    public static List<Training> getAll() {
        return SQLite.select().from(Training.class).queryList();
    }

    @Nullable
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Training &&
                getClass().equals(another.getClass()) &&
                id.equals(((Training) another).id);
    }

    @Nullable
    public Environment getEnvironment() {
        return new Environment(indoor, weather, windSpeed, windDirection, location);
    }

    public void setEnvironment(@NonNull Environment env) {
        indoor = env.indoor;
        weather = env.weather;
        windDirection = env.windDirection;
        windSpeed = env.windSpeed;
        location = env.location;
    }

    @OneToMany(methods = {}, variableName = "rounds")
    public List<Round> getRounds() {
        if (rounds == null) {
            rounds = SQLite.select()
                    .from(Round.class)
                    .where(Round_Table.training.eq(id))
                    .orderBy(Round_Table.index, true)
                    .queryList();
        }
        return rounds;
    }

    @Nullable
    public StandardRound getStandardRound() {
        return SQLite.select()
                .from(StandardRound.class)
                .where(StandardRound_Table._id.eq(standardRoundId))
                .querySingle();
    }

    @Nullable
    public Bow getBow() {
        return SQLite.select()
                .from(Bow.class)
                .where(Bow_Table._id.eq(bowId))
                .querySingle();
    }

    @Nullable
    public Arrow getArrow() {
        return SQLite.select()
                .from(Arrow.class)
                .where(Arrow_Table._id.eq(arrowId))
                .querySingle();
    }

    public String getFormattedDate() {
        return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
    }

    public Score getReachedScore() {
        return Stream.of(getRounds())
                .map(Round::getReachedScore)
                .collect(Score.sum());
    }

    @Override
    public int compareTo(@NonNull Training training) {
        if (date.equals(training.date)) {
            return (int) (id - training.id);
        }
        return date.compareTo(training.date);
    }


    @Override
    public void save() {
        FlowManager.getDatabase(AppDatabase.class).executeTransaction(this::save);
    }

    @Override
    public void save(DatabaseWrapper databaseWrapper) {
        super.save(databaseWrapper);
        // TODO Replace this super ugly workaround by stubbed Relationship in version 4 of dbFlow
        for (Round s : getRounds()) {
            s.trainingId = id;
            s.save(databaseWrapper);
        }
    }

    @Override
    public void delete() {
        FlowManager.getDatabase(AppDatabase.class).executeTransaction(this::delete);
    }

    @Override
    public void delete(DatabaseWrapper databaseWrapper) {
        for (Round round : getRounds()) {
            round.delete(databaseWrapper);
        }
        super.delete(databaseWrapper);
    }

    @NonNull
    public Training ensureLoaded() {
        for (Round round : getRounds()) {
            for (End end : round.getEnds()) {
                end.getShots();
            }
        }
        return this;
    }

    public void initRoundsFromTemplate(@NonNull StandardRound standardRound) {
        rounds = new ArrayList<>();
        for (RoundTemplate template : standardRound.getRounds()) {
            Round round = new Round(template);
            round.trainingId = id;
            round.setTarget(template.getTargetTemplate());
            round.comment = "";
            round.ends = new ArrayList<>();
            round.save();
            rounds.add(round);
        }
    }

    @Override
    public void saveRecursively() {
        FlowManager.getDatabase(AppDatabase.class).executeTransaction(this::saveRecursively);
    }

    @Override
    public void saveRecursively(DatabaseWrapper databaseWrapper) {
        super.save(databaseWrapper);
        for (Round s : getRounds()) {
            s.trainingId = id;
            s.saveRecursively(databaseWrapper);
        }
    }

    @NonNull
    public List<Round> getRounds(DatabaseWrapper databaseWrapper) {
        return SQLite.select()
                .from(Round.class)
                .where(Round_Table.training.eq(id))
                .orderBy(Round_Table.index, true)
                .queryList(databaseWrapper);
    }
}
