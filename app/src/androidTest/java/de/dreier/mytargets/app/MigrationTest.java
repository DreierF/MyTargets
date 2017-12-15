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

package de.dreier.mytargets.app;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.google.common.truth.Truth;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.AndroidDatabase;
import com.raizlabs.android.dbflow.structure.database.DatabaseHelperDelegate;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import de.dreier.mytargets.features.settings.backup.provider.BackupUtils;
import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.EWeather;
import de.dreier.mytargets.shared.models.db.Bow;
import de.dreier.mytargets.shared.models.db.End;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Shot;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.test.base.InstrumentedTestBase;

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

    @BeforeClass
    public static void prepare() {
        FlowManager.reset();
        FlowManager.destroy();
    }

    @Before
    public void setUp() throws IOException {
        ApplicationInstance.initFlowManager(getTargetContext());

        // Create artificial image file to ensure the according database entry is not deleted (Migration23)
        getTargetContext()
                .openFileOutput("img175420839671886584-0f61-43a6-bc1e-dbcd8526056c794370927.jpg", Context.MODE_PRIVATE)
                .close();

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
    public void upgradeShouldKeepData() throws IOException {
        List<Training> trainings = Training.Companion.getAll();
        assertTraining1(trainings.get(0));
        assertTraining2(trainings.get(1));

        final List<Bow> bows = Bow.Companion.getAll();
        Truth.assertThat(bows.get(0).loadImages().get(0).getFileName())
                .isEqualTo("img175420839671886584-0f61-43a6-bc1e-dbcd8526056c794370927.jpg");
    }

    private void assertTraining1(@NonNull Training training) {
        Truth.assertThat(training.getId()).isEqualTo(1);
        Truth.assertThat(training.getTitle()).isEqualTo("Training im Dez");
        Truth.assertThat(training.getStandardRoundId()).isEqualTo(null);
        Truth.assertThat(training.getBowId()).isEqualTo(null);
        Truth.assertThat(training.getArrowId()).isEqualTo(null);
        Truth.assertThat(training.getArrowNumbering()).isEqualTo(false);
        Truth.assertThat(training.getIndoor()).isEqualTo(true);
        Truth.assertThat(training.getWeather()).isEqualTo(EWeather.SUNNY);
        Truth.assertThat(training.getWindDirection()).isEqualTo(0);
        Truth.assertThat(training.getWindSpeed()).isEqualTo(3);
        Truth.assertThat(training.getLocation()).isEqualTo("Neufahrn bei Freising");
        List<Round> rounds = training.loadRounds();

        assertRound11(rounds);

        Truth.assertThat(rounds.get(1).getId()).isEqualTo(2L);
        Truth.assertThat(rounds.get(1).getTrainingId()).isEqualTo(1L);
        Truth.assertThat(rounds.get(1).getIndex()).isEqualTo(1);
        Truth.assertThat(rounds.get(1).getShotsPerEnd()).isEqualTo(3);
        Truth.assertThat(rounds.get(1).getMaxEndCount()).isEqualTo(null);
        Truth.assertThat(rounds.get(1).getDistance()).isEqualTo(new Dimension(20, METER));
        Truth.assertThat(rounds.get(1).getComment()).isEqualTo("Kommentar");
        Truth.assertThat(rounds.get(1).getTarget().getId()).isEqualTo(0);
        Truth.assertThat(rounds.get(1).getTarget().scoringStyle).isEqualTo(0);
        Truth.assertThat(rounds.get(1).getTarget().diameter).isEqualTo(new Dimension(60, CENTIMETER));
        Truth.assertThat(rounds.get(1).loadEnds()).hasSize(2);
    }

    private void assertRound11(@NonNull List<Round> rounds) {
        final Round round1 = rounds.get(0);
        Truth.assertThat(round1.getId()).isEqualTo(1L);
        Truth.assertThat(round1.getTrainingId()).isEqualTo(1L);
        Truth.assertThat(round1.getIndex()).isEqualTo(0);
        Truth.assertThat(round1.getShotsPerEnd()).isEqualTo(4);
        Truth.assertThat(round1.getMaxEndCount()).isEqualTo(null);
        Truth.assertThat(round1.getDistance()).isEqualTo(new Dimension(50, METER));
        Truth.assertThat(round1.getComment()).isEqualTo("");
        Truth.assertThat(round1.getTarget().getId()).isEqualTo(1);
        Truth.assertThat(round1.getTarget().scoringStyle).isEqualTo(2);
        Truth.assertThat(round1.getTarget().diameter).isEqualTo(new Dimension(40, CENTIMETER));
        final List<End> ends = round1.loadEnds();
        Truth.assertThat(ends).hasSize(3);
        Truth.assertThat(ends.get(0).getId()).isEqualTo(1L);
        Truth.assertThat(ends.get(0).getRoundId()).isEqualTo(1L);
        Truth.assertThat(ends.get(0).getIndex()).isEqualTo(0);
        Truth.assertThat(ends.get(0).loadImages()).isEmpty();
        Truth.assertThat(ends.get(0).getExact()).isEqualTo(true);
        Truth.assertThat(ends.get(2).getExact()).isEqualTo(false);
        final List<Shot> shots = ends.get(0).loadShots();
        Truth.assertThat(shots).hasSize(4);
        Truth.assertThat(shots.get(0).getIndex()).isEqualTo(0);
        Truth.assertThat(shots.get(0).getX()).isWithin(0f).of(-0.41206896f);
        Truth.assertThat(shots.get(0).getY()).isWithin(0f).of(0.03448276f);
        Truth.assertThat(shots.get(0).getScoringRing()).isEqualTo(3);
        Truth.assertThat(shots.get(0).getArrowNumber()).isEqualTo(null);
        Truth.assertThat(shots.get(1).getIndex()).isEqualTo(1);
        Truth.assertThat(shots.get(1).getScoringRing()).isEqualTo(2);
        Truth.assertThat(shots.get(2).getIndex()).isEqualTo(2);
        Truth.assertThat(shots.get(2).getScoringRing()).isEqualTo(1);
        Truth.assertThat(shots.get(3).getIndex()).isEqualTo(3);
        Truth.assertThat(shots.get(3).getScoringRing()).isEqualTo(0);
    }

    private void assertTraining2(@NonNull Training training) {
        Truth.assertThat(training.getId()).isEqualTo(2);
        Truth.assertThat(training.getTitle()).isEqualTo("Training");
        Truth.assertThat(training.getStandardRoundId()).isEqualTo(32L);
        Truth.assertThat(training.getBowId()).isEqualTo(1);
        Truth.assertThat(training.getArrowId()).isEqualTo(1);
        Truth.assertThat(training.getArrowNumbering()).isEqualTo(false);
        Truth.assertThat(training.getIndoor()).isEqualTo(false);
        Truth.assertThat(training.getWeather()).isEqualTo(EWeather.LIGHT_RAIN);
        Truth.assertThat(training.getWindDirection()).isEqualTo(0);
        Truth.assertThat(training.getWindSpeed()).isEqualTo(3);
        Truth.assertThat(training.getLocation()).isEqualTo("Neufahrn bei Freising");
        List<Round> rounds = training.loadRounds();
        Truth.assertThat(rounds.get(0).loadEnds()).hasSize(6);
        Truth.assertThat(rounds.get(1).loadEnds()).hasSize(6);
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

    @NonNull
    private Set<String> extractSchema(@NonNull DatabaseWrapper db) {
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
