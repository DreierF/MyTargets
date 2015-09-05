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

import de.dreier.mytargets.shared.models.Distance;
import de.dreier.mytargets.shared.models.SightSetting;

public class SightSettingDataSource extends IdProviderDataSource<SightSetting> {
    public static final String TABLE = "VISIER";
    private static final String BOW = "bow";
    private static final String DISTANCE = "distance";
    private static final String UNIT = "unit";
    private static final String SETTING = "setting";
    public static final String CREATE_TABLE_VISIER =
            "CREATE TABLE IF NOT EXISTS " + TABLE + " ( " +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    BOW + " INTEGER," +
                    DISTANCE + " INTEGER," +
                    SETTING + " TEXT, " +
                    UNIT + " TEXT);";

    public SightSettingDataSource(Context context) {
        super(context, TABLE);
    }

    @Override
    public ContentValues getContentValues(SightSetting sightSetting) {
        ContentValues values = new ContentValues();
        values.put(BOW, sightSetting.bowId);
        values.put(DISTANCE, sightSetting.distance.value);
        values.put(UNIT, sightSetting.distance.unit);
        values.put(SETTING, sightSetting.value);
        return values;
    }

    public void update(long bowId, List<SightSetting> sightSettingsList) {
        database.delete(TABLE, BOW + "=" + bowId, null);
        for (SightSetting set : sightSettingsList) {
            ContentValues values = new ContentValues();
            values.put(BOW, bowId);
            values.put(DISTANCE, set.distance.value);
            values.put(UNIT, set.distance.unit);
            values.put(SETTING, set.value);
            database.insert(TABLE, null, values);
        }
    }

    private SightSetting cursorToSightSetting(Cursor cursor) {
        SightSetting sightSetting = new SightSetting();
        sightSetting.distance = new Distance(cursor.getInt(0), cursor.getString(1));
        sightSetting.value = cursor.getString(2);
        sightSetting.bowId = cursor.getLong(3);
        return sightSetting;
    }

    public SightSetting get(long bowId, Distance distance) {
        Cursor cursor = database.rawQuery(
                "SELECT distance, unit, setting, bow " +
                        "FROM VISIER " +
                        "WHERE bow = " + bowId + " " +
                        "AND distance = " + distance.value + " " +
                        "AND unit = \"" + distance.unit+"\"", null);
        SightSetting s = null;
        if (cursor.moveToFirst()) {
            s = cursorToSightSetting(cursor);
        }
        cursor.close();
        return s;
    }

    public ArrayList<SightSetting> getAll(long bow) {
        Cursor cursor = database.rawQuery(
                "SELECT distance, unit, setting, bow " +
                        "FROM VISIER " +
                        "WHERE bow = " + bow + " " +
                        "ORDER BY distance ASC", null);

        ArrayList<SightSetting> list = new ArrayList<>(cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToSightSetting(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}
