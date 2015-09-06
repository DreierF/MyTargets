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

import java.util.ArrayList;

import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.StandardRound;

public class StandardRoundDataSource extends IdProviderDataSource<StandardRound> {
    public static final String TABLE = "STANDARD_ROUND_TEMPLATE";
    private static final String NAME = "name";
    private static final String INSTITUTION = "club";
    private static final String INDOOR = "indoor";
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    NAME + " TEXT," +
                    INSTITUTION + " INTEGER," +
                    INDOOR + " INTEGER);";

    public StandardRoundDataSource(Context context, DatabaseManager dbHelper, SQLiteDatabase db) {
        super(context, TABLE, dbHelper, db);
    }

    public StandardRoundDataSource(Context context) {
        super(context, TABLE);
    }

    @Override
    public void update(StandardRound item) {
        super.update(item);
        RoundTemplateDataSource rtds = new RoundTemplateDataSource(getContext(), dbHelper, database);
        for (RoundTemplate template : item.getRounds()) {
            rtds.update(template);
        }
    }

    @Override
    public ContentValues getContentValues(StandardRound standardRound) {
        ContentValues values = new ContentValues();
        values.put(NAME, standardRound.name);
        values.put(INSTITUTION, standardRound.club);
        values.put(INDOOR, standardRound.indoor ? 1 : 0);
        return values;
    }

    private StandardRound cursorToStandardRound(Cursor cursor) {
        StandardRound standardRound = new StandardRound();
        standardRound.setId(cursor.getLong(0));
        standardRound.name = cursor.getString(1);
        standardRound.club = cursor.getInt(2);
        standardRound.indoor = cursor.getInt(3) == 1;
        return standardRound;
    }


    public StandardRound get(long standardRoundId) {
        Cursor cursor = database.rawQuery("SELECT s._id, s.name, s.club, s.indoor, " +
                "a._id, a.r_index, a.arrows, a.target, a.scoring_style, a.target, a.scoring_style, a.distance, a.unit, " +
                "a.size, a.target_unit, a.passes, a.sid " +
                "FROM STANDARD_ROUND_TEMPLATE s " +
                "LEFT JOIN ROUND_TEMPLATE a ON s._id=a.sid " +
                "WHERE s._id = " + standardRoundId, null);

        StandardRound sr = null;
        if (cursor.moveToFirst()) {
            sr = cursorToStandardRound(cursor);
            do {
                if (cursor.getLong(13) == 0) {
                    break;
                }
                sr.insert(RoundTemplateDataSource.cursorToRoundTemplate(cursor, getContext(), 4));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return sr;
    }

    public ArrayList<StandardRound> getAll() {
        Cursor cursor = database.rawQuery("SELECT s._id FROM STANDARD_ROUND_TEMPLATE s", null);
        ArrayList<StandardRound> list = new ArrayList<>(cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                list.add(get(cursor.getLong(0)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}
