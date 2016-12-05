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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.joda.time.DateTime;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import de.dreier.mytargets.R;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;
import de.dreier.mytargets.features.settings.backup.provider.BackupUtils;

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.utils.FileUtils;
import de.dreier.mytargets.utils.BackupUtils;

public class DatabaseManager {

    public static boolean importZip(Context context, InputStream in) {
        // Unzip all images and database
        File file = BackupUtils.unzip(context, in);

            // Replace database file
            File db_file = context.getDatabasePath(AppDatabase.NAME);
            //TODO close all open handles to the database
            FileUtils.copy(file, db_file);
        sInstance = null;
        if (tmp != null) {
            tmp.close();
        }
        BackupUtils.copy(file, db_file);
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
