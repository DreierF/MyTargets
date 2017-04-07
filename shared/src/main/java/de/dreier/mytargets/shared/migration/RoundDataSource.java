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
package de.dreier.mytargets.shared.migration;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import java.util.ArrayList;

import static de.dreier.mytargets.shared.migration.RoundTemplateDataSource.SCORING_STYLE;
import static de.dreier.mytargets.shared.migration.RoundTemplateDataSource.TARGET;


public class RoundDataSource extends IdProviderDataSource<RoundOld> {
    public static final String TABLE = "ROUND";
    public static final String TRAINING = "training";
    public static final String COMMENT = "comment";
    public static final String TEMPLATE = "template";

    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TRAINING + " INTEGER," +
                    COMMENT + " TEXT," +
                    TEMPLATE + " INTEGER," +
                    TARGET + " INTEGER," +
                    SCORING_STYLE + " INTEGER);";

    public RoundDataSource(Context context, DatabaseWrapper database) {
        super(context, TABLE, database);
    }

    @Override
    public ContentValues getContentValues(RoundOld round) {
        ContentValues values = new ContentValues();
        values.put(COMMENT, round.comment);
        values.put(TRAINING, round.training);
        values.put(TEMPLATE, round.info.getId());
        values.put(TARGET, round.info.target.getId());
        values.put(SCORING_STYLE, round.info.target.scoringStyle);
        return values;
    }

    private RoundOld cursorToRound(Cursor cursor, int startColumnIndex) {
        RoundOld round = new RoundOld();
        round.setId(cursor.getLong(startColumnIndex));
        round.training = cursor.getLong(startColumnIndex + 1);
        round.comment = cursor.getString(startColumnIndex + 2);
        if (round.comment == null) {
            round.comment = "";
        }
        round.info = RoundTemplateDataSource.cursorToRoundTemplate(cursor, 3);
        return round;
    }

    public RoundOld get(long round) {
        Cursor cursor = database.rawQuery(
                "SELECT r._id, r.training, r.comment, " +
                        "a._id, a.r_index, a.arrows, a.target, a.scoring_style, " +
                        "r.target, r.scoring_style, a.distance, a.unit, " +
                        "a.size, a.target_unit, a.passes, a.sid " +
                        "FROM ROUND r " +
                        "LEFT JOIN ROUND_TEMPLATE a ON r.template=a._id " +
                        "LEFT JOIN PASSE p ON r._id = p.round " +
                        "LEFT JOIN SHOOT s ON p._id = s.passe " +
                        "WHERE r._id=" + round, null);

        if (!cursor.moveToFirst()) {
            return null;
        }
        RoundOld r = cursorToRound(cursor, 0);
        cursor.close();
        return r;
    }

    public ArrayList<RoundOld> getAll(long training) {
        Cursor res = database.rawQuery("SELECT r._id " +
                "FROM ROUND r " +
                "WHERE r.training=" + training + " " +
                "ORDER BY r._id ASC", null);
        ArrayList<RoundOld> list = new ArrayList<>(res.getCount());
        if (res.moveToFirst()) {
            do {
                RoundOld r = get(res.getLong(0));
                list.add(r);
            } while (res.moveToNext());
        }
        res.close();
        return list;
    }
}
