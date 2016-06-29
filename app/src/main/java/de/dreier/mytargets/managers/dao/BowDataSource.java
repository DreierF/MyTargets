/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.managers.dao;


import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;

import de.dreier.mytargets.shared.models.Bow;
import de.dreier.mytargets.shared.models.EBowType;

public class BowDataSource extends IdProviderDataSource<Bow> {
    private static final String TABLE = "BOW";
    private static final String NAME = "name";
    private static final String THUMBNAIL = "thumbnail";
    private static final String BRAND = "brand";
    private static final String TYPE = "type";
    private static final String SIZE = "size";
    private static final String HEIGHT = "height";
    private static final String TILLER = "tiller";
    private static final String DESCRIPTION = "description";
    private static final String IMAGE = "image";
    private static final String LIMBS = "limbs";
    private static final String SIGHT = "sight";
    private static final String WEIGHT = "draw_weight";
    private static final String STABILIZER = "stabilizer";
    private static final String CLICKER = "clicker";
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
                    STABILIZER + " TEXT," +
                    CLICKER + " TEXT," +
                    DESCRIPTION + " TEXT," +
                    THUMBNAIL + " BLOB," +
                    IMAGE + " TEXT);";

    public BowDataSource() {
        super(TABLE);
    }

    @Override
    public void update(Bow item) {
        super.update(item);
        new SightSettingDataSource().update(item.getId(), item.sightSettings);
    }

    @Override
    public ContentValues getContentValues(Bow bow) {
        ContentValues values = new ContentValues();
        values.put(NAME, bow.name);
        values.put(TYPE, bow.type.getId());
        values.put(BRAND, bow.brand);
        values.put(SIZE, bow.size);
        values.put(HEIGHT, bow.braceHeight);
        values.put(TILLER, bow.tiller);
        values.put(LIMBS, bow.limbs);
        values.put(SIGHT, bow.sight);
        values.put(WEIGHT, bow.drawWeight);
        values.put(STABILIZER, bow.stabilizer);
        values.put(CLICKER, bow.clicker);
        values.put(DESCRIPTION, bow.description);
        values.put(THUMBNAIL, bow.thumb);
        values.put(IMAGE, bow.imageFile);
        return values;
    }

    private Bow cursorToBow(Cursor cursor, int startColumnIndex) {
        Bow bow = new Bow();
        bow.setId(cursor.getLong(startColumnIndex));
        bow.name = cursor.getString(startColumnIndex + 1);
        bow.type = EBowType.fromId(cursor.getInt(startColumnIndex + 2));
        bow.brand = cursor.getString(startColumnIndex + 3);
        bow.size = cursor.getString(startColumnIndex + 4);
        bow.braceHeight = cursor.getString(startColumnIndex + 5);
        bow.tiller = cursor.getString(startColumnIndex + 6);
        bow.limbs = cursor.getString(startColumnIndex + 7);
        bow.sight = cursor.getString(startColumnIndex + 8);
        bow.drawWeight = cursor.getString(startColumnIndex + 9);
        bow.stabilizer = cursor.getString(startColumnIndex + 10);
        bow.clicker = cursor.getString(startColumnIndex + 11);
        bow.description = cursor.getString(startColumnIndex + 12);
        bow.thumb = cursor.getBlob(startColumnIndex + 13);
        bow.imageFile = cursor.getString(startColumnIndex + 14);
        return bow;
    }

    public Bow get(long bow) {
        Cursor cursor = database.rawQuery(
                "SELECT _id, name, type, brand, size, height, tiller, limbs, sight, draw_weight, stabilizer, clicker, description, thumbnail, image " +
                        "FROM BOW WHERE _id = " + bow, null);
        Bow b = null;
        if (cursor.moveToFirst()) {
            b = cursorToBow(cursor, 0);
            b.sightSettings = new SightSettingDataSource().getAll(bow);
        }
        cursor.close();
        return b;
    }

    public ArrayList<Bow> getAll() {
        Cursor res = database.rawQuery(
                "SELECT _id, name, type, brand, size, height, tiller, limbs, sight, draw_weight, stabilizer, clicker, description, thumbnail, image " +
                        "FROM BOW " +
                        "ORDER BY _id ASC", null);
        ArrayList<Bow> list = new ArrayList<>(res.getCount());
        if (res.moveToFirst()) {
            do {
                Bow bow = cursorToBow(res, 0);
                bow.sightSettings = new SightSettingDataSource().getAll(bow.getId());
                list.add(bow);
            } while (res.moveToNext());
        }
        res.close();
        return list;
    }
}
