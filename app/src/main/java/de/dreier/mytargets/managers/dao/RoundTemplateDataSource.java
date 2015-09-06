/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.managers.dao;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.shared.models.Diameter;
import de.dreier.mytargets.shared.models.Distance;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.target.TargetFactory;

public class RoundTemplateDataSource extends IdProviderDataSource<RoundTemplate> {
    public static final String TABLE = "ROUND_TEMPLATE";
    private static final String STANDARD_ID = "sid";
    private static final String INDEX = "r_index";
    public static final String DISTANCE = "distance";
    public static final String UNIT = "unit";
    private static final String PASSES = "passes";
    public static final String ARROWS_PER_PASSE = "arrows";
    public static final String TARGET = "target";
    private static final String TARGET_SIZE = "size";
    private static final String TARGET_SIZE_UNIT = "target_unit";
    public static final String SCORING_STYLE = "scoring_style";
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    STANDARD_ID + " INTEGER," +
                    INDEX + " INTEGER," +
                    DISTANCE + " INTEGER," +
                    UNIT + " TEXT," +
                    PASSES + " INTEGER," +
                    ARROWS_PER_PASSE + " INTEGER," +
                    TARGET + " INTEGER," +
                    TARGET_SIZE + " INTEGER," +
                    TARGET_SIZE_UNIT + " INTEGER," +
                    SCORING_STYLE + " INTEGER," +
                    "UNIQUE(sid, r_index) ON CONFLICT REPLACE);";

    public RoundTemplateDataSource(Context context) {
        super(context, TABLE);
    }

    public RoundTemplateDataSource(Context context, DatabaseManager dbHelper, SQLiteDatabase database) {
        super(context, TABLE, dbHelper, database);
    }

    @Override
    public ContentValues getContentValues(RoundTemplate roundTemplate) {
        ContentValues values = new ContentValues();
        values.put(STANDARD_ID, roundTemplate.standardRound);
        values.put(INDEX, roundTemplate.index);
        values.put(DISTANCE, roundTemplate.distance.value);
        values.put(UNIT, roundTemplate.distance.unit);
        values.put(PASSES, roundTemplate.passes);
        values.put(ARROWS_PER_PASSE, roundTemplate.arrowsPerPasse);
        values.put(TARGET, roundTemplate.targetTemplate.id);
        values.put(TARGET_SIZE, roundTemplate.targetTemplate.size.value);
        values.put(TARGET_SIZE_UNIT, roundTemplate.targetTemplate.size.unit);
        values.put(SCORING_STYLE, roundTemplate.targetTemplate.scoringStyle);
        return values;
    }

    static RoundTemplate cursorToRoundTemplate(Cursor cursor, Context context, int startColumnIndex) {
        RoundTemplate roundTemplate = new RoundTemplate();
        roundTemplate.setId(cursor.getLong(startColumnIndex));
        roundTemplate.index = cursor.getInt(startColumnIndex + 1);
        roundTemplate.arrowsPerPasse = cursor.getInt(startColumnIndex + 2);
        roundTemplate.targetTemplate = TargetFactory
                .createTarget(context, cursor.getInt(startColumnIndex + 3),
                        cursor.getInt(startColumnIndex + 4));
        roundTemplate.target = TargetFactory
                .createTarget(context, cursor.getInt(startColumnIndex + 5),
                        cursor.getInt(startColumnIndex + 6));
        roundTemplate.distance = new Distance(cursor.getInt(startColumnIndex + 7),
                cursor.getString(startColumnIndex + 8));
        roundTemplate.target.size = new Diameter(
                cursor.getInt(startColumnIndex + 9), cursor.getString(startColumnIndex + 10));
        roundTemplate.targetTemplate.size = roundTemplate.target.size;
        roundTemplate.passes = cursor.getInt(startColumnIndex + 11);
        roundTemplate.standardRound = cursor.getLong(startColumnIndex + 12);
        return roundTemplate;
    }

    public RoundTemplate get(long sid, int index) {
        Cursor cursor = database.rawQuery("SELECT _id, r_index, arrows, target, scoring_style, " +
                        "target, scoring_style, distance, unit, size, target_unit, passes, sid " +
                        "FROM ROUND_TEMPLATE WHERE sid=? AND r_index=?",
                new String[]{String.valueOf(sid),
                        String.valueOf(index)
                });
        RoundTemplate r = null;
        if (cursor.moveToFirst()) {
            r = cursorToRoundTemplate(cursor, getContext(), 0);
        }
        cursor.close();
        return r;
    }
}
