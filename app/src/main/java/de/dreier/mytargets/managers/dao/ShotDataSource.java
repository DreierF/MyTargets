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

import de.dreier.mytargets.shared.models.Shot;

public class ShotDataSource extends IdProviderDataSource<Shot> {
    public static final String TABLE = "SHOOT";
    public static final String PASSE = "passe";
    public static final String ZONE = "points";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String COMMENT = "comment";
    public static final String ARROW = "arrow";
    public static final String INDEX = "arrow_index";
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
