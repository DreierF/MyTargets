/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.managers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.utils.FileUtils;
import de.dreier.mytargets.utils.BackupUtils;

public class DatabaseManager {

    public static boolean importZip(Context context, InputStream in) {
        try {
            // Unzip all images and database
            File file = BackupUtils.unzip(context, in);

            // Replace database file
            File db_file = context.getDatabasePath(AppDatabase.NAME);
            //TODO close all open handles to the database
            FileUtils.copy(file, db_file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void cleanup(SQLiteDatabase db) {
        // Clean up rounds
        db.execSQL("DELETE FROM ROUND WHERE _id IN (SELECT r._id " +
                "FROM ROUND r LEFT JOIN TRAINING t ON t._id=r.training " +
                "WHERE t._id IS NULL)");

        // Clean up passes
        db.execSQL("DELETE FROM PASSE WHERE _id IN (SELECT p._id " +
                "FROM PASSE p LEFT JOIN ROUND r ON r._id=p.round " +
                "WHERE r._id IS NULL)");

        // Clean up shots
        db.execSQL("DELETE FROM SHOOT WHERE _id IN (SELECT s._id " +
                "FROM SHOOT s LEFT JOIN PASSE p ON p._id=s.passe " +
                "WHERE p._id IS NULL)");

        // Clean up arrow numbers
        db.execSQL("DELETE FROM NUMBER WHERE _id IN (SELECT s._id " +
                "FROM NUMBER s LEFT JOIN ARROW a ON a._id=s.arrow " +
                "WHERE a._id IS NULL)");
    }
}
