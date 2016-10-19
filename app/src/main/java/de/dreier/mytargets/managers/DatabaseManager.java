/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.managers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import org.joda.time.DateTime;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.dreier.mytargets.R;
import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.utils.FileUtils;
import de.dreier.mytargets.utils.BackupUtils;

import static de.dreier.mytargets.shared.SharedApplicationInstance.get;

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


////// EXPORT ALL //////

    // TODO move to separate class and introduce helper class to make it more readable
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void exportAll(File file) throws IOException {
        file.getParentFile().mkdirs();
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        writeExportData(writer);
    }

    public static void writeExportData(Writer writer) throws IOException {
        DatabaseWrapper db = FlowManager.getWritableDatabase(AppDatabase.class);
        Cursor cur = db.rawQuery(
                "SELECT t.title,sr.name AS standard_round,date(t.datum/1000, 'unixepoch', 'localtime') AS date, sr.indoor, i.r_index, i.distance, i.unit," +
                        "r.target, r.scoring_style, i.size, i.target_unit, s.arrow_index, a.name, s.x, s.y, s.arrow, b.name AS bow, s.points AS score, " +
                        "(SELECT COUNT(x._id) FROM PASSE x WHERE x.round=p.round AND x._id<=p._id) AS end_index, p.save_time " +
                        "FROM TRAINING t, ROUND r, PASSE p, SHOOT s " +
                        "LEFT JOIN BOW b ON b._id=t.bow " +
                        "LEFT JOIN ARROW a ON a._id=t.arrow " +
                        "LEFT JOIN ROUND_TEMPLATE i ON r.template=i._id " +
                        "LEFT JOIN STANDARD_ROUND_TEMPLATE sr ON t.standard_round=sr._id " +
                        "WHERE t._id = r.training AND r._id=p.round AND p._id=s.passe", null);
        writer.append("\"")
                .append(get(R.string.title)).append("\";\"")
                .append(get(R.string.date)).append("\";\"")
                .append(get(R.string.standard_round)).append("\";\"")
                .append(get(R.string.indoor)).append("\";\"")
                .append(get(R.string.bow)).append("\";\"")
                .append(get(R.string.arrow)).append("\";\"")
                .append(get(R.string.round)).append("\";\"")
                .append(get(R.string.distance)).append("\";\"")
                .append(get(R.string.target)).append("\";\"")
                .append(get(R.string.passe)).append("\";\"")
                .append(get(R.string.timestamp)).append("\";\"")
                .append(get(R.string.points)).append("\";\"")
                .append("x").append("\";\"")
                .append("y").append("\"\n");
        int titleInd = cur.getColumnIndexOrThrow("title");
        int standardRound = cur.getColumnIndexOrThrow("standard_round");
        int roundIndex = cur.getColumnIndexOrThrow("r_index");
        int dateInd = cur.getColumnIndexOrThrow("date");
        int indoorInd = cur.getColumnIndexOrThrow("indoor");
        int distanceInd = cur.getColumnIndexOrThrow("distance");
        int distanceUnitInd = cur.getColumnIndexOrThrow("unit");
        int targetInd = cur.getColumnIndexOrThrow("target");
        int styleInd = cur.getColumnIndexOrThrow("scoring_style");
        int targetSizeInd = cur.getColumnIndexOrThrow("size");
        int bowInd = cur.getColumnIndexOrThrow("bow");
        int xInd = cur.getColumnIndexOrThrow("x");
        int yInd = cur.getColumnIndexOrThrow("y");
        int targetUnitInd = cur.getColumnIndexOrThrow("target_unit");
        int arrowInd = cur.getColumnIndexOrThrow("name");
        int arrowNumberInd = cur.getColumnIndexOrThrow("arrow");
        int shotInd = cur.getColumnIndexOrThrow("arrow_index");
        int scoreInd = cur.getColumnIndexOrThrow("score");
        int endInd = cur.getColumnIndexOrThrow("end_index");
        int timestampInd = cur.getColumnIndexOrThrow("save_time");
        if (cur.moveToFirst()) {
            do {
                // Title
                writer.append("\"");
                writer.append(cur.getString(titleInd));
                writer.append("\";\"");

                // Date
                writer.append(cur.getString(dateInd));
                writer.append("\";\"");

                // StandardRound
                writer.append(cur.getString(standardRound));
                writer.append("\";\"");

                // Indoor
                if (cur.getInt(indoorInd) == 0) {
                    writer.append("Outdoor\";\"");
                } else {
                    writer.append("Indoor\";\"");
                }

                // Bow
                if (cur.getString(bowInd) != null) {
                    writer.append(cur.getString(bowInd));
                }
                writer.append("\";\"");

                // Arrow
                if (cur.getString(arrowInd) != null) {
                    writer.append(cur.getString(arrowInd));
                    if (cur.getInt(arrowNumberInd) > -1) {
                        writer.append(" (");
                        writer.append(String.valueOf(cur.getInt(arrowNumberInd)));
                        writer.append(")");
                    }
                }
                writer.append("\";\"");

                // Round
                writer.append(String.valueOf(cur.getInt(roundIndex) + 1));
                writer.append("\";\"");

                // Distance
                writer.append(new Dimension(
                        cur.getInt(distanceInd),
                        cur.getString(distanceUnitInd))
                        .toString());
                writer.append("\";\"");

                // Target
                Target target = new Target(cur.getInt(targetInd), cur.getInt(styleInd),
                        new Dimension(cur.getInt(targetSizeInd), cur.getString(targetUnitInd)));
                writer.append(target.getModel().toString())
                        .append(" (")
                        .append(target.size.toString())
                        .append(")\";\"");

                // End
                writer.append(String.valueOf(cur.getInt(endInd)));
                writer.append("\";\"");

                // Timestamp
                Date saveDate = new DateTime(cur.getLong(timestampInd)).toDate();
                writer.append(SimpleDateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.GERMAN)
                        .format(saveDate));
                writer.append("\";\"");

                // Score
                writer.append(target.zoneToString(cur.getInt(scoreInd), cur.getInt(shotInd)));
                writer.append("\";\"");

                // Coordinates (X, Y)
                writer.append(String.valueOf(cur.getFloat(xInd)));
                writer.append("\";\"");
                writer.append(String.valueOf(cur.getFloat(yInd)));

                writer.append("\"\n");
            } while (cur.moveToNext());
        }
        writer.flush();
        writer.close();
        cur.close();
    }
}
