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

import de.dreier.mytargets.shared.models.Bow;

public class BowDataSource extends IdProviderDataSource<Bow> {
    public static final String TABLE = "BOW";
    public static final String NAME = "name";
    public static final String THUMBNAIL = "thumbnail";
    public static final String BRAND = "brand";
    public static final String TYPE = "type";
    public static final String SIZE = "size";
    public static final String HEIGHT = "height";
    public static final String TILLER = "tiller";
    public static final String DESCRIPTION = "description";
    public static final String IMAGE = "image";
    public static final String LIMBS = "limbs";
    public static final String SIGHT = "sight";
    public static final String WEIGHT = "draw_weight";
    public static final String CREATE_TABLE_BOW =
            "CREATE TABLE IF NOT EXISTS " + TABLE + " ( " +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    NAME + " TEXT," +
                    BRAND + " TEXT," +
                    TYPE + " INTEGER," +
                    SIZE + " INTEGER," +
                    HEIGHT + " TEXT," +
                    TILLER + " TEXT," +
                    LIMBS + " TEXT," +
                    SIGHT + " TEXT," +
                    WEIGHT + " TEXT," +
                    DESCRIPTION + " TEXT," +
                    THUMBNAIL + " BLOB," +
                    IMAGE + " TEXT);";

    public BowDataSource(Context context) {
        super(context, TABLE);
    }

    @Override
    public void update(Bow item) {
        super.update(item);
        new SightSettingDataSource(getContext()).update(item.getId(), item.sightSettings);
    }

    @Override
    public ContentValues getContentValues(Bow bow) {
        ContentValues values = new ContentValues();
        values.put(NAME, bow.name);
        values.put(TYPE, bow.type);
        values.put(BRAND, bow.brand);
        values.put(SIZE, bow.size);
        values.put(HEIGHT, bow.height);
        values.put(TILLER, bow.tiller);
        values.put(LIMBS, bow.limbs);
        values.put(SIGHT, bow.sight);
        values.put(WEIGHT, bow.drawWeight);
        values.put(DESCRIPTION, bow.description);
        values.put(THUMBNAIL, bow.thumb);
        values.put(IMAGE, bow.imageFile);
        return values;
    }

    private Bow cursorToBow(Cursor cursor, int startColumnIndex) {
        Bow bow = new Bow();
        bow.setId(cursor.getLong(startColumnIndex));
        bow.name = cursor.getString(startColumnIndex + 1);
        bow.type = cursor.getInt(startColumnIndex + 2);
        bow.brand = cursor.getString(startColumnIndex + 3);
        bow.size = cursor.getString(startColumnIndex + 4);
        bow.height = cursor.getString(startColumnIndex + 5);
        bow.tiller = cursor.getString(startColumnIndex + 6);
        bow.limbs = cursor.getString(startColumnIndex + 7);
        bow.sight = cursor.getString(startColumnIndex + 8);
        bow.drawWeight = cursor.getString(startColumnIndex + 9);
        bow.description = cursor.getString(startColumnIndex + 10);
        bow.thumb = cursor.getBlob(startColumnIndex + 11);
        bow.imageFile = cursor.getString(startColumnIndex + 12);
        return bow;
    }

    public Bow get(long bow) {
        Cursor cursor = database.rawQuery(
                "SELECT _id, name, type, brand, size, height, tiller, limbs, sight, draw_weight, description, thumbnail, image " +
                        "FROM BOW WHERE _id = " + bow, null);
        Bow b = null;
        if (cursor.moveToFirst()) {
            b = cursorToBow(cursor, 0);
            b.sightSettings = new SightSettingDataSource(getContext()).getAll(bow);
        }
        cursor.close();
        return b;
    }

    public ArrayList<Bow> getAll() {
        Cursor res = database.rawQuery(
                "SELECT _id, name, type, brand, size, height, tiller, limbs, sight, draw_weight, description, thumbnail, image " +
                        "FROM BOW " +
                        "ORDER BY _id ASC", null);
        ArrayList<Bow> list = new ArrayList<>(res.getCount());
        if (res.moveToFirst()) {
            do {
                Bow bow = cursorToBow(res,0);
                bow.sightSettings = new SightSettingDataSource(getContext()).getAll(bow.getId());
                list.add(bow);
            } while (res.moveToNext());
        }
        res.close();
        return list;
    }
}
