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
import de.dreier.mytargets.shared.models.db.Training;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public final class MigrationTest {

    private File newFile;
    private File upgradedFile;

    @Before
    public void setup() throws IOException {
        File baseDir = new File("build/tmp/migration");
        newFile = new File(baseDir, "new.db");
        upgradedFile = RuntimeEnvironment.application.getDatabasePath(AppDatabase.DATABASE_FILE_NAME);
        File firstDbFile = new File("src/test/resources/origin.db");
        upgradedFile.getParentFile().mkdirs();
        newFile.getParentFile().mkdirs();
        FileUtils.copyFile(firstDbFile, upgradedFile);
    }

    @Test
    public void upgradeShouldBeTheSameAsCreate() {
        FlowManager.init(new FlowConfig.Builder(RuntimeEnvironment.application)
                .openDatabasesOnInit(false)
                .build());
        DatabaseHelperDelegate helper = FlowManager
                .getDatabase(AppDatabase.NAME).getHelper().getDelegate();
        final DatabaseWrapper upgradedDb = FlowManager
                .getDatabase(AppDatabase.NAME).getWritableDatabase();
        helper.onUpgrade(upgradedDb, 16, AppDatabase.VERSION);
        Set<String> upgradedSchema = extractSchema(upgradedDb);

        List<Training> trainings = Training.getAll();


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

            String s = "";
            Cursor cu = db.rawQuery("SELECT * FROM " + tableName, null);
            for (cu.moveToFirst(); !cu.isAfterLast(); cu.moveToNext()) {
                s += "\n";
                for (int i = 0; i < cu.getColumnCount(); i++) {
                    if (cu.getType(i) == Cursor.FIELD_TYPE_STRING) {
                        s += " " + cu.getString(i);
                    }
                }
            }

//            Cursor cu = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
//            for (cu.moveToFirst(); !cu.isAfterLast(); cu.moveToNext()) {
//                final String columnName = c.getString(1);
//                final String columnType = c.getString(2);
//                final String columnNullable = c.getString(3);
//                final String columnDefault = c.getString(4);
//
//                schema.add("TABLE " + tableName +
//                        " COLUMN " + columnName + " " + columnType +
//                        " NULLABLE=" + columnNullable +
//                        " DEFAULT=" + columnDefault);
//            }
//            cu.close();
            schema.add("TABLE " + tableName + s.substring(0, Math.min(100, s.length())));
        }
        c.close();
        return schema;
    }
}