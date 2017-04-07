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

import de.dreier.mytargets.shared.models.Shot;

public class ShotDataSource extends IdProviderDataSource<Shot> {
    private static final String TABLE = "SHOOT";
    private static final String PASSE = "passe";
    private static final String ZONE = "points";
    private static final String X = "x";
    private static final String Y = "y";
    private static final String COMMENT = "comment";
    private static final String ARROW = "arrow";
    private static final String INDEX = "arrow_index";
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    PASSE + " INTEGER," +
                    ZONE + " INTEGER," +
                    X + " REAL," +
                    Y + " REAL," +
                    COMMENT + " TEXT," +
                    ARROW + " INTEGER," +
                    INDEX + " INTEGER);";

    public ShotDataSource(Context context) {
        super(context, TABLE);
    }

    @Override
    public ContentValues getContentValues(Shot shot) {
        ContentValues values = new ContentValues();
        values.put(PASSE, shot.passe);
        values.put(ZONE, shot.zone);
        values.put(X, shot.x);
        values.put(Y, shot.y);
        values.put(COMMENT, shot.comment);
        values.put(ARROW, shot.arrow);
        values.put(INDEX, shot.index);
        return values;
    }

    static Shot cursorToShot(Cursor cursor, int i) {
        Shot shot = new Shot(i);
        shot.setId(cursor.getLong(0));
        shot.passe = cursor.getLong(1);
        shot.zone = cursor.getInt(2);
        shot.x = cursor.getFloat(3);
        shot.y = cursor.getFloat(4);
        shot.comment = cursor.getString(5);
        shot.arrow = cursor.getInt(6);
        shot.index = cursor.getInt(7);
        return shot;
    }
}
