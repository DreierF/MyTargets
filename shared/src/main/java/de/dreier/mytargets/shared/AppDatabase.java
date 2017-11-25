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

package de.dreier.mytargets.shared;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.dreier.mytargets.shared.models.db.ArrowImage;
import de.dreier.mytargets.shared.models.db.BowImage;
import de.dreier.mytargets.shared.models.db.EndImage;
import de.dreier.mytargets.shared.models.db.Image;
import de.dreier.mytargets.shared.models.db.StandardRound;
import de.dreier.mytargets.shared.utils.FileUtils;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;

@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION, foreignKeysSupported = true)
public class AppDatabase {

    public static final String NAME = "database";
    public static final String DATABASE_FILE_NAME = "database.db";
    public static final String DATABASE_IMPORT_FILE_NAME = "database";

    public static final int VERSION = 24;

    @Migration(version = 0, database = AppDatabase.class)
    public static class Migration0 extends BaseMigration {

        @Override
        public void migrate(DatabaseWrapper database) {
            fillStandardRound(database);
        }
    }

    @Migration(version = 22, database = AppDatabase.class)
    public static class Migration22 extends BaseMigration {

        @Override
        public void migrate(DatabaseWrapper database) {
            fillStandardRound(database);
        }
    }

    @Migration(version = 23, database = AppDatabase.class)
    public static class Migration23 extends BaseMigration {

        @Override
        public void migrate(DatabaseWrapper database) {
            removeFilePath(database, EndImage.class);
            removeFilePath(database, BowImage.class);
            removeFilePath(database, ArrowImage.class);
        }

        private <T extends BaseModel & Image> void removeFilePath(DatabaseWrapper database, Class<? extends T> clazz) {
            List<? extends T> images = SQLite.select().from(clazz).queryList(database);
            for (T image : images) {
                File filesDir = SharedApplicationInstance.getContext().getFilesDir();
                File imageFile = new File(filesDir, image.getFileName());

                File imageFromSomewhere = new File(image.getFileName());
                File imageFileFromSomewhere = new File(filesDir, imageFromSomewhere.getName());

                // If imagePath is just the name and is placed inside files directory or
                // In case the image was already copied to the files, but does still contain the wrong path
                if (imageFile.exists() || imageFileFromSomewhere.exists()) {
                    image.setFileName(imageFile.getName());
                    image.save(database);
                    continue;
                }

                // In case the image is placed somewhere else, but still exists
                if (imageFromSomewhere.exists()) {
                    try {
                        imageFile = File
                                .createTempFile("img", imageFromSomewhere.getName(), filesDir);
                        FileUtils.move(imageFromSomewhere, imageFile);
                        image.setFileName(imageFile.getName());
                        image.save(database);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    image.delete(database);
                }
            }
        }
    }

    private static void fillStandardRound(DatabaseWrapper db) {
        List<StandardRound> rounds = StandardRoundFactory.initTable();
        for (StandardRound round : rounds) {
            round.save(db);
        }
    }

    @Migration(version = 3, database = AppDatabase.class)
    public static class Migration3 extends BaseMigration {

        @Override
        public void migrate(@NonNull DatabaseWrapper database) {
            database.execSQL("ALTER TABLE SHOOT ADD COLUMN x REAL");
            database.execSQL("ALTER TABLE SHOOT ADD COLUMN y REAL");
            Cursor cur = database.rawQuery("SELECT s._id, s.points, r.target " +
                    "FROM SHOOT s, PASSE p, ROUND r " +
                    "WHERE s.passe=p._id " +
                    "AND p.round=r._id", null);
            if (cur.moveToFirst()) {
                do {
                    int shoot = cur.getInt(0);
                    database.execSQL("UPDATE SHOOT SET x=0, y=0 WHERE _id=" + shoot);
                } while (cur.moveToNext());
            }
            cur.close();
        }
    }

    @Migration(version = 4, database = AppDatabase.class)
    public static class Migration4 extends BaseMigration {

        @Override
        public void migrate(@NonNull DatabaseWrapper database) {
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS VISIER ( _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "bow REFERENCES BOW ON DELETE CASCADE," +
                            "distance INTEGER," +
                            "setting TEXT);");
            int[] valuesMetric = {10, 15, 18, 20, 25, 30, 40, 50, 60, 70, 90};
            for (String table : new String[]{"ROUND", "VISIER"}) {
                for (int i = 10; i >= 0; i--) {
                    database.execSQL("UPDATE " + table + " SET distance=" +
                            valuesMetric[i] + " WHERE distance=" + i);
                }
            }
            database.execSQL("ALTER TABLE BOW ADD COLUMN height TEXT DEFAULT '';");
        }
    }

    @Migration(version = 6, database = AppDatabase.class)
    public static class Migration6 extends BaseMigration {

        @Override
        public void migrate(@NonNull DatabaseWrapper database) {
            File filesDir = SharedApplicationInstance.getContext().getFilesDir();

            // Migrate all bow images
            Cursor cur = database.rawQuery("SELECT image FROM BOW WHERE image IS NOT NULL", null);
            if (cur.moveToFirst()) {
                String fileName = cur.getString(0);
                try {
                    File file = File.createTempFile("img_", ".png", filesDir);
                    FileUtils.copy(new File(fileName), file);
                    database.execSQL(
                            "UPDATE BOW SET image=\"" + file.getName() + "\" WHERE image=\"" +
                                    fileName + "\"");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            cur.close();

            // Migrate all arrows images
            cur = database.rawQuery("SELECT image FROM ARROW WHERE image IS NOT NULL", null);
            if (cur.moveToFirst()) {
                String fileName = cur.getString(0);
                try {
                    File file = File.createTempFile("img_", ".png", filesDir);
                    FileUtils.copy(new File(fileName), file);
                    database.execSQL(
                            "UPDATE ARROW SET image=\"" + file.getName() + "\" WHERE image=\"" +
                                    fileName + "\"");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            cur.close();
        }
    }
}
