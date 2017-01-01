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

package de.dreier.mytargets.managers;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.google.common.truth.Truth;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.AndroidDatabase;
import com.raizlabs.android.dbflow.structure.database.DatabaseHelperDelegate;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import de.dreier.mytargets.ApplicationInstance;
import de.dreier.mytargets.InstrumentedTestBase;
import de.dreier.mytargets.features.settings.backup.provider.BackupUtils;
import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.EWeather;
import de.dreier.mytargets.shared.models.db.Bow;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Shot;
import de.dreier.mytargets.shared.models.db.Training;

import static android.support.test.InstrumentationRegistry.getContext;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static de.dreier.mytargets.shared.models.Dimension.Unit.CENTIMETER;
import static de.dreier.mytargets.shared.models.Dimension.Unit.METER;

@SmallTest
@RunWith(AndroidJUnit4.class)
public final class MigrationTest extends InstrumentedTestBase {

    /**
     * Handle to a database based on schema 16 is updated to the current schema during setUp.
     */
    private DatabaseWrapper upgradedDb;

    /**
     * A sqlite open helper for the app's data layer.
     */
    private DatabaseHelperDelegate helper;

    @Before
    public void setUp() throws IOException {
        ApplicationInstance.initFlowManager(getTargetContext());

        File tmpDb = getTargetContext().getDatabasePath(DatabaseHelperDelegate
                .getTempDbFileName(FlowManager.getDatabase(AppDatabase.NAME)));

        BackupUtils.copy(getContext().getAssets().open("database.db"), new FileOutputStream(tmpDb));

        helper = FlowManager.getDatabase(AppDatabase.NAME).getHelper().getDelegate();
        upgradedDb = FlowManager.getDatabase(AppDatabase.NAME).getWritableDatabase();
    }

    @After
    public void cleanup() {
        FlowManager.reset();
        FlowManager.destroy();
    }

    @Test
    public void upgradeShouldKeepData() {
        List<Training> trainings = Training.getAll();
        assertTraining1(trainings.get(0));
        assertTraining2(trainings.get(1));

        final List<Bow> bows = Bow.getAll();
        Truth.assertThat(bows.get(0).getImages().get(0).fileName)
                .isEqualTo("img175420839671886584-0f61-43a6-bc1e-dbcd8526056c794370927.jpg");
    }

    private void assertTraining1(Training training) {
        Truth.assertThat(training.getId()).isEqualTo(1);
        Truth.assertThat(training.title).isEqualTo("Training im Dez");
        Truth.assertThat(training.standardRoundId).isEqualTo(null);
        Truth.assertThat(training.bowId).isEqualTo(null);
        Truth.assertThat(training.arrowId).isEqualTo(null);
        Truth.assertThat(training.arrowNumbering).isEqualTo(false);
        Truth.assertThat(training.timePerEnd).isEqualTo(-1);
        Truth.assertThat(training.indoor).isEqualTo(true);
        Truth.assertThat(training.weather).isEqualTo(EWeather.SUNNY);
        Truth.assertThat(training.windDirection).isEqualTo(0);
        Truth.assertThat(training.windSpeed).isEqualTo(3);
        Truth.assertThat(training.location).isEqualTo("Neufahrn bei Freising");
        List<Round> rounds = training.getRounds();

        assertRound11(rounds);

        Truth.assertThat(rounds.get(1).getId()).isEqualTo(2L);
        Truth.assertThat(rounds.get(1).trainingId).isEqualTo(1L);
        Truth.assertThat(rounds.get(1).index).isEqualTo(1);
        Truth.assertThat(rounds.get(1).shotsPerEnd).isEqualTo(2);
        Truth.assertThat(rounds.get(1).maxEndCount).isEqualTo(null);
        Truth.assertThat(rounds.get(1).distance).isEqualTo(new Dimension(20, METER));
        Truth.assertThat(rounds.get(1).comment).isEqualTo("Kommentar");
        Truth.assertThat(rounds.get(1).getTarget().getId()).isEqualTo(0);
        Truth.assertThat(rounds.get(1).getTarget().scoringStyle).isEqualTo(0);
        Truth.assertThat(rounds.get(1).getTarget().size).isEqualTo(new Dimension(60, CENTIMETER));
        Truth.assertThat(rounds.get(1).getEnds()).hasSize(2);
    }

    private void assertRound11(List<Round> rounds) {
        final Round round1 = rounds.get(0);
        Truth.assertThat(round1.getId()).isEqualTo(1L);
        Truth.assertThat(round1.trainingId).isEqualTo(1L);
        Truth.assertThat(round1.index).isEqualTo(0);
        Truth.assertThat(round1.shotsPerEnd).isEqualTo(3);
        Truth.assertThat(round1.maxEndCount).isEqualTo(null);
        Truth.assertThat(round1.distance).isEqualTo(new Dimension(50, METER));
        Truth.assertThat(round1.comment).isEqualTo("");
        Truth.assertThat(round1.getTarget().getId()).isEqualTo(1);
        Truth.assertThat(round1.getTarget().scoringStyle).isEqualTo(1);
        Truth.assertThat(round1.getTarget().size).isEqualTo(new Dimension(40, CENTIMETER));
        final List<End> ends = round1.getEnds();
        Truth.assertThat(ends).hasSize(3);
        Truth.assertThat(ends.get(0).getId()).isEqualTo(1L);
        Truth.assertThat(ends.get(0).roundId).isEqualTo(1L);
        Truth.assertThat(ends.get(0).index).isEqualTo(0);
        Truth.assertThat(ends.get(0).getImages()).isEmpty();
        Truth.assertThat(ends.get(0).exact).isEqualTo(true);
        Truth.assertThat(ends.get(2).exact).isEqualTo(false);
        final List<Shot> shots = ends.get(0).getShots();
        Truth.assertThat(shots).hasSize(4);
        Truth.assertThat(shots.get(0).index).isEqualTo(0);
        Truth.assertThat(shots.get(0).x).isWithin(0f).of(-0.41206896f);
        Truth.assertThat(shots.get(0).y).isWithin(0f).of(0.03448276f);
        Truth.assertThat(shots.get(0).scoringRing).isEqualTo(3);
        Truth.assertThat(shots.get(0).arrowNumber).isEqualTo(null);
        Truth.assertThat(shots.get(1).index).isEqualTo(1);
        Truth.assertThat(shots.get(1).scoringRing).isEqualTo(2);
        Truth.assertThat(shots.get(2).index).isEqualTo(2);
        Truth.assertThat(shots.get(2).scoringRing).isEqualTo(1);
        Truth.assertThat(shots.get(3).index).isEqualTo(3);
        Truth.assertThat(shots.get(3).scoringRing).isEqualTo(0);
    }

    private void assertTraining2(Training training) {
        Truth.assertThat(training.getId()).isEqualTo(2);
        Truth.assertThat(training.title).isEqualTo("Training");
        Truth.assertThat(training.standardRoundId).isEqualTo(32L);
        Truth.assertThat(training.bowId).isEqualTo(1);
        Truth.assertThat(training.arrowId).isEqualTo(1);
        Truth.assertThat(training.arrowNumbering).isEqualTo(false);
        Truth.assertThat(training.timePerEnd).isEqualTo(-1);
        Truth.assertThat(training.indoor).isEqualTo(false);
        Truth.assertThat(training.weather).isEqualTo(EWeather.LIGHT_RAIN);
        Truth.assertThat(training.windDirection).isEqualTo(0);
        Truth.assertThat(training.windSpeed).isEqualTo(3);
        Truth.assertThat(training.location).isEqualTo("Neufahrn bei Freising");
        List<Round> rounds = training.getRounds();
        Truth.assertThat(rounds.get(0).getEnds()).hasSize(6);
        Truth.assertThat(rounds.get(1).getEnds()).hasSize(6);
    }

    @Test
    public void upgradeShouldKeepModel() throws IOException {
        Set<String> upgradedSchema = extractSchema(upgradedDb);

        File newFile = File.createTempFile("newDb", ".db");
        AndroidDatabase newDb = AndroidDatabase
                .from(SQLiteDatabase.openOrCreateDatabase(newFile, null));
        helper.onCreate(newDb);
        Set<String> newSchema = extractSchema(newDb);

        Truth.assertThat(upgradedSchema).isEqualTo(newSchema);
        Truth.assertThat(upgradedSchema.size() > 0);
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