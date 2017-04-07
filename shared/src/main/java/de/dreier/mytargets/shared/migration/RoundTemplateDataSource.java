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

import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Target;

public class RoundTemplateDataSource extends IdProviderDataSource<RoundTemplateOld> {
    public static final String TARGET = "target";
    public static final String SCORING_STYLE = "scoring_style";
    public static final String TABLE = "ROUND_TEMPLATE";
    public static final String STANDARD_ID = "sid";
    public static final String INDEX = "r_index";
    public static final String DISTANCE = "distance";
    public static final String UNIT = "unit";
    public static final String PASSES = "passes";
    public static final String ARROWS_PER_PASSE = "arrows";
    public static final String TARGET_SIZE = "size";
    public static final String TARGET_SIZE_UNIT = "target_unit";

    public RoundTemplateDataSource(Context context, DatabaseWrapper database) {
        super(context, TABLE, database);
    }

    static RoundTemplateOld cursorToRoundTemplate(Cursor cursor, int startColumnIndex) {
        RoundTemplateOld roundTemplate = new RoundTemplateOld();
        roundTemplate.setId(cursor.getLong(startColumnIndex));
        roundTemplate.index = cursor.getInt(startColumnIndex + 1);
        roundTemplate.arrowsPerPasse = cursor.getInt(startColumnIndex + 2);
        final Dimension diameter = new Dimension(
                cursor.getInt(startColumnIndex + 9), cursor.getString(startColumnIndex + 10));
        roundTemplate.targetTemplate = new Target(cursor.getInt(startColumnIndex + 3),
                cursor.getInt(startColumnIndex + 4), diameter);
        roundTemplate.target = new Target(cursor.getInt(startColumnIndex + 5),
                cursor.getInt(startColumnIndex + 6), diameter);
        roundTemplate.distance = new DistanceOld(cursor.getInt(startColumnIndex + 7),
                cursor.getString(startColumnIndex + 8));
        roundTemplate.passes = cursor.getInt(startColumnIndex + 11);
        roundTemplate.standardRound = cursor.getLong(startColumnIndex + 12);
        return roundTemplate;
    }

    @Override
    public ContentValues getContentValues(RoundTemplateOld roundTemplate) {
        ContentValues values = new ContentValues();
        values.put(STANDARD_ID, roundTemplate.standardRound);
        values.put(INDEX, roundTemplate.index);
        values.put(DISTANCE, roundTemplate.distance.value);
        values.put(UNIT, roundTemplate.distance.unit);
        values.put(PASSES, roundTemplate.passes);
        values.put(ARROWS_PER_PASSE, roundTemplate.arrowsPerPasse);
        values.put(TARGET, roundTemplate.targetTemplate.id);
        values.put(TARGET_SIZE, roundTemplate.targetTemplate.size.value);
        values.put(TARGET_SIZE_UNIT, Dimension.Unit.toStringHandleNull(roundTemplate.targetTemplate.size.unit));
        values.put(SCORING_STYLE, roundTemplate.targetTemplate.scoringStyle);
        return values;
    }

    public RoundTemplateOld get(long sid, int index) {
        Cursor cursor = database.rawQuery("SELECT _id, r_index, arrows, target, scoring_style, " +
                        "target, scoring_style, distance, unit, size, target_unit, passes, sid " +
                        "FROM ROUND_TEMPLATE WHERE sid=? AND r_index=?",
                new String[]{String.valueOf(sid),
                        String.valueOf(index)
                });
        RoundTemplateOld r = null;
        if (cursor.moveToFirst()) {
            r = cursorToRoundTemplate(cursor, 0);
        }
        cursor.close();
        return r;
    }
}
