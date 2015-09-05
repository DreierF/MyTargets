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

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.models.ArrowNumber;

public class ArrowNumberDataSource extends IdProviderDataSource<ArrowNumber> {
    private static final String TABLE = "NUMBER";
    private static final String NUMBER_ARROW = "arrow";
    private static final String NUMBER_VALUE = "value";
    public static final String CREATE_TABLE_NUMBER =
            "CREATE TABLE IF NOT EXISTS " + TABLE + " ( " +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    NUMBER_ARROW + " INTEGER," +
                    NUMBER_VALUE + " INTEGER);";

    public ArrowNumberDataSource(Context context) {
        super(context, TABLE);
    }

    @Override
    public ContentValues getContentValues(ArrowNumber arrowNumber) {
        ContentValues values = new ContentValues();
        values.put(NUMBER_VALUE, arrowNumber.number);
        return values;
    }

    public void update(long arrowId, List<ArrowNumber> arrowNumbersList) {
        database.delete(TABLE, NUMBER_ARROW + "=" + arrowId, null);
        for (ArrowNumber number : arrowNumbersList) {
            ContentValues contentValues = getContentValues(number);
            contentValues.put(NUMBER_ARROW, arrowId);
            database.insert(TABLE, null, contentValues);
        }
    }

    public ArrayList<ArrowNumber> getAll(long arrowId) {
        Cursor res = database
                .query(TABLE, new String[]{NUMBER_VALUE}, NUMBER_ARROW + "=" + arrowId, null, null,
                        null,
                        NUMBER_VALUE + " ASC");
        ArrayList<ArrowNumber> list = new ArrayList<>();
        if (res.moveToFirst()) {
            do {
                ArrowNumber an = new ArrowNumber();
                an.number = res.getInt(0);
                list.add(an);
            } while (res.moveToNext());
        }
        res.close();
        return list;
    }
}
