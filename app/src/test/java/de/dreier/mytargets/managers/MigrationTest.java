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

package de.dreier.mytargets.managers;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.AndroidDatabase;
import com.raizlabs.android.dbflow.structure.database.DatabaseHelperDelegate;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import de.dreier.mytargets.BuildConfig;
import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.EWeather;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Shot;
import de.dreier.mytargets.shared.models.db.Training;

import static com.google.common.truth.Truth.assertThat;
import static de.dreier.mytargets.shared.models.Dimension.Unit.CENTIMETER;
import static de.dreier.mytargets.shared.models.Dimension.Unit.METER;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public final class MigrationTest {

    private File newFile;
    private DatabaseWrapper upgradedDb;
    private DatabaseHelperDelegate helper;

    @Before
    public void setup() throws IOException {
        File baseDir = new File("build/tmp/migration");
        newFile = new File(baseDir, "new.db");
        File upgradedFile = RuntimeEnvironment.application
                .getDatabasePath(AppDatabase.DATABASE_FILE_NAME);
        File firstDbFile = new File("src/test/resources/origin.db");
        upgradedFile.getParentFile().mkdirs();
        newFile.getParentFile().mkdirs();
        FileUtils.copyFile(firstDbFile, upgradedFile);

        FlowManager.init(new FlowConfig.Builder(RuntimeEnvironment.application).build());
        helper = FlowManager.getDatabase(AppDatabase.NAME).getHelper().getDelegate();
        upgradedDb = FlowManager.getDatabase(AppDatabase.NAME).getWritableDatabase();
        helper.onUpgrade(upgradedDb, 16, AppDatabase.VERSION);
    }

    @After
    public void cleanup() {
        FlowManager.reset();
    }

    @Test
    public void upgradeShouldKeepData() {
        List<Training> trainings = Training.getAll();
        assertTraining1(trainings.get(0));
        assertTraining2(trainings.get(1));
    }

    private void assertTraining1(Training training) {
        assertThat(training.getId()).isEqualTo(1);
        assertThat(training.title).isEqualTo("Training im Dez");
        assertThat(training.standardRoundId).isEqualTo(null);
        assertThat(training.bowId).isEqualTo(null);
        assertThat(training.arrowId).isEqualTo(null);
        assertThat(training.arrowNumbering).isEqualTo(false);
        assertThat(training.timePerEnd).isEqualTo(-1);
        assertThat(training.indoor).isEqualTo(true);
        assertThat(training.weather).isEqualTo(EWeather.SUNNY);
        assertThat(training.windDirection).isEqualTo(0);
        assertThat(training.windSpeed).isEqualTo(3);
        assertThat(training.location).isEqualTo("Neufahrn bei Freising");
        List<Round> rounds = training.getRounds();

        assertRound11(rounds);

        assertThat(rounds.get(1).getId()).isEqualTo(2L);
        assertThat(rounds.get(1).trainingId).isEqualTo(1L);
        assertThat(rounds.get(1).index).isEqualTo(1);
        assertThat(rounds.get(1).shotsPerEnd).isEqualTo(2);
        assertThat(rounds.get(1).maxEndCount).isEqualTo(null);
        assertThat(rounds.get(1).distance).isEqualTo(new Dimension(20, METER));
        assertThat(rounds.get(1).comment).isEqualTo("Kommentar");
        assertThat(rounds.get(1).getTarget().getId()).isEqualTo(0);
        assertThat(rounds.get(1).getTarget().scoringStyle).isEqualTo(0);
        assertThat(rounds.get(1).getTarget().size).isEqualTo(new Dimension(60, CENTIMETER));
        assertThat(rounds.get(1).getEnds()).hasSize(2);
    }

    private void assertRound11(List<Round> rounds) {
        final Round round1 = rounds.get(0);
        assertThat(round1.getId()).isEqualTo(1L);
        assertThat(round1.trainingId).isEqualTo(1L);
        assertThat(round1.index).isEqualTo(0);
        assertThat(round1.shotsPerEnd).isEqualTo(3);
        assertThat(round1.maxEndCount).isEqualTo(null);
        assertThat(round1.distance).isEqualTo(new Dimension(50, METER));
        assertThat(round1.comment).isEqualTo("");
        assertThat(round1.getTarget().getId()).isEqualTo(1);
        assertThat(round1.getTarget().scoringStyle).isEqualTo(1);
        assertThat(round1.getTarget().size).isEqualTo(new Dimension(40, CENTIMETER));
        final List<End> ends = round1.getEnds();
        assertThat(ends).hasSize(3);
        assertThat(ends.get(0).getId()).isEqualTo(1L);
        assertThat(ends.get(0).roundId).isEqualTo(1L);
        assertThat(ends.get(0).index).isEqualTo(0);
        assertThat(ends.get(0).images).isEqualTo(null);
        assertThat(ends.get(0).exact).isEqualTo(true);
        assertThat(ends.get(2).exact).isEqualTo(false);
        final List<Shot> shots = ends.get(0).getShots();
        assertThat(shots).hasSize(4);
        assertThat(shots.get(0).index).isEqualTo(0);
        assertThat(shots.get(0).x).isWithin(0f).of(-0.41206896f);
        assertThat(shots.get(0).y).isWithin(0f).of(0.03448276f);
        assertThat(shots.get(0).scoringRing).isEqualTo(3);
        assertThat(shots.get(0).arrowNumber).isEqualTo(null);
        assertThat(shots.get(1).index).isEqualTo(1);
        assertThat(shots.get(1).scoringRing).isEqualTo(2);
        assertThat(shots.get(2).index).isEqualTo(2);
        assertThat(shots.get(2).scoringRing).isEqualTo(1);
        assertThat(shots.get(3).index).isEqualTo(3);
        assertThat(shots.get(3).scoringRing).isEqualTo(0);
    }

    private void assertTraining2(Training training) {
        assertThat(training.getId()).isEqualTo(2);
        assertThat(training.title).isEqualTo("Training");
        assertThat(training.standardRoundId).isEqualTo(32L);
        assertThat(training.bowId).isEqualTo(1);
        assertThat(training.arrowId).isEqualTo(1);
        assertThat(training.arrowNumbering).isEqualTo(false);
        assertThat(training.timePerEnd).isEqualTo(-1);
        assertThat(training.indoor).isEqualTo(false);
        assertThat(training.weather).isEqualTo(EWeather.LIGHT_RAIN);
        assertThat(training.windDirection).isEqualTo(0);
        assertThat(training.windSpeed).isEqualTo(3);
        assertThat(training.location).isEqualTo("Neufahrn bei Freising");
        List<Round> rounds = training.getRounds();
        assertThat(rounds.get(0).getEnds()).hasSize(6);
        assertThat(rounds.get(1).getEnds()).hasSize(6);
    }

    @Test
    public void upgradeShouldKeepModel() {
        Set<String> upgradedSchema = extractSchema(upgradedDb);

        AndroidDatabase newDb = AndroidDatabase
                .from(SQLiteDatabase.openOrCreateDatabase(newFile, null));
        helper.onCreate(newDb);
        Set<String> newSchema = extractSchema(newDb);

        assertThat(upgradedSchema).isEqualTo(newSchema);
    }

    private Set<String> extractSchema(DatabaseWrapper db) {
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        final Set<String> schema = new TreeSet<>();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            String tableName = c.getString(0);

            Cursor cu = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
            for (cu.moveToFirst(); !cu.isAfterLast(); cu.moveToNext()) {
                final String columnName = cu.getString(1);
                final String columnType = cu.getString(2);
                final String columnNullable = cu.getString(3);
                final String columnDefault = cu.getString(4);

                schema.add("TABLE " + tableName +
                        " COLUMN " + columnName + " " + columnType +
                        " NULLABLE=" + columnNullable +
                        " DEFAULT=" + columnDefault);
            }
            cu.close();
        }
        c.close();
        return schema;
    }
}