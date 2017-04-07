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

public class StandardRoundDataSource extends IdProviderDataSource<StandardRoundOld> {
    public static final String TABLE = "STANDARD_ROUND_TEMPLATE";
    public static final String NAME = "name";
    public static final String INSTITUTION = "club";
    public static final String INDOOR = "indoor";

    public StandardRoundDataSource(Context context, DatabaseWrapper database) {
        super(context, TABLE, database);
    }

    @Override
    public void update(StandardRoundOld item) {
        super.update(item);
        RoundTemplateDataSource rtds = new RoundTemplateDataSource(getContext(), database);
        for (RoundTemplateOld template : item.getRounds()) {
            template.standardRound = item.getId();
            rtds.update(template);
        }
    }

    @Override
    public ContentValues getContentValues(StandardRoundOld standardRound) {
        ContentValues values = new ContentValues();
        values.put(NAME, standardRound.name);
        values.put(INSTITUTION, standardRound.club);
        values.put(INDOOR, standardRound.indoor ? 1 : 0);
        return values;
    }

    private StandardRoundOld cursorToStandardRound(Cursor cursor) {
        StandardRoundOld standardRound = new StandardRoundOld();
        standardRound.setId(cursor.getLong(0));
        standardRound.name = cursor.getString(1);
        standardRound.club = cursor.getInt(2);
        standardRound.indoor = cursor.getInt(3) == 1;
        return standardRound;
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
}
