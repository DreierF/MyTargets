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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import de.dreier.mytargets.shared.models.db.RoundTemplate;

public class StandardRoundDataSource {
    public static final String ID = "_id";
    public static final String TABLE = "STANDARD_ROUND_TEMPLATE";
    public static final String NAME = "name";
    public static final String INSTITUTION = "club";
    public static final String INDOOR = "indoor";
    private final DatabaseWrapper database;

    public StandardRoundDataSource(DatabaseWrapper database) {
        this.database = database;
    }

    public StandardRoundOld get(long standardRoundId) {
        Cursor cursor = database.rawQuery("SELECT s._id, s.name, s.club, s.indoor, " +
                "a._id, a.r_index, a.arrows, a.target, a.scoring_style, a.target, a.scoring_style, a.distance, a.unit, " +
                "a.size, a.target_unit, a.passes, a.sid " +
                "FROM STANDARD_ROUND_TEMPLATE s " +
                "LEFT JOIN ROUND_TEMPLATE a ON s._id=a.sid " +
                "WHERE s._id = " + standardRoundId, null);

        StandardRoundOld sr = null;
        if (cursor.moveToFirst()) {
            sr = cursorToStandardRound(cursor);
            do {
                if (cursor.getLong(13) == 0) {
                    break;
                }
                sr.insert(RoundTemplateDataSource.cursorToRoundTemplate(cursor, 4));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return sr;
    }

    private StandardRoundOld cursorToStandardRound(Cursor cursor) {
        StandardRoundOld standardRound = new StandardRoundOld();
        standardRound.setId(cursor.getLong(0));
        standardRound.name = cursor.getString(1);
        standardRound.club = cursor.getInt(2);
        standardRound.indoor = cursor.getInt(3) == 1;
        return standardRound;
    }

    public void update(StandardRoundOld item) {
        ContentValues values = getContentValues(item);
        if (values == null) {
            return;
        }
        if (item.id <= 0) {
            item.setId(database.insertWithOnConflict(TABLE, null, values, SQLiteDatabase.CONFLICT_NONE));
        } else {
            values.put(ID, item.id);
            database.insertWithOnConflict(TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
        RoundTemplateDataSource rtds = new RoundTemplateDataSource(database);
        for (RoundTemplate template : item.rounds) {
            template.standardRound = item.id;
            rtds.update(template);
        }
    }

    private ContentValues getContentValues(StandardRoundOld standardRound) {
        ContentValues values = new ContentValues();
        values.put(NAME, standardRound.name);
        values.put(INSTITUTION, standardRound.club);
        values.put(INDOOR, standardRound.indoor ? 1 : 0);
        return values;
    }
}
