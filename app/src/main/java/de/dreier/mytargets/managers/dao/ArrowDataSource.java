/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.managers.dao;


import android.content.ContentValues;
import android.database.Cursor;

import com.raizlabs.android.dbflow.data.Blob;

import java.util.ArrayList;

import de.dreier.mytargets.shared.models.Arrow;
import de.dreier.mytargets.shared.models.Thumbnail;

public class ArrowDataSource extends IdProviderDataSource<Arrow> {
    public static final String TABLE = "ARROW";
    private static final String NAME = "name";
    private static final String THUMBNAIL = "thumbnail";
    private static final String LENGTH = "length";
    private static final String MATERIAL = "material";
    private static final String SPINE = "spine";
    private static final String WEIGHT = "weight";
    private static final String TIP_WEIGHT = "tip_weight";
    private static final String VANES = "vanes";
    private static final String NOCK = "nock";
    private static final String COMMENT = "comment";
    private static final String IMAGE = "image";
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    NAME + " TEXT," +
                    LENGTH + " TEXT," +
                    MATERIAL + " TEXT," +
                    SPINE + " TEXT," +
                    WEIGHT + " TEXT," +
                    TIP_WEIGHT + " TEXT," +
                    VANES + " TEXT," +
                    NOCK + " TEXT," +
                    COMMENT + " TEXT," +
                    THUMBNAIL + " BLOB," +
                    IMAGE + " TEXT);";

    public ArrowDataSource() {
        super(TABLE);
    }

    @Override
    public void update(Arrow item) {
        super.update(item);
        new ArrowNumberDataSource().update(item.getId(), item.numbers);
    }

    @Override
    public ContentValues getContentValues(Arrow arrow) {
        ContentValues values = new ContentValues();
        values.put(NAME, arrow.name);
        values.put(LENGTH, arrow.length);
        values.put(MATERIAL, arrow.material);
        values.put(SPINE, arrow.spine);
        values.put(WEIGHT, arrow.weight);
        values.put(TIP_WEIGHT, arrow.tipWeight);
        values.put(VANES, arrow.vanes);
        values.put(NOCK, arrow.nock);
        values.put(COMMENT, arrow.comment);
        values.put(THUMBNAIL, arrow.thumbnail.getBlob().getBlob());
        values.put(IMAGE, arrow.imageFile);
        return values;
    }

    private Arrow cursorToArrow(Cursor cursor, int startColumnIndex) {
        Arrow arrow = new Arrow();
        arrow.setId(cursor.getLong(startColumnIndex));
        arrow.name = cursor.getString(startColumnIndex + 1);
        arrow.length = cursor.getString(startColumnIndex + 2);
        arrow.material = cursor.getString(startColumnIndex + 3);
        arrow.spine = cursor.getString(startColumnIndex + 4);
        arrow.weight = cursor.getString(startColumnIndex + 5);
        arrow.tipWeight = cursor.getString(startColumnIndex + 6);
        arrow.vanes = cursor.getString(startColumnIndex + 7);
        arrow.nock = cursor.getString(startColumnIndex + 8);
        arrow.comment = cursor.getString(startColumnIndex + 9);
        arrow.thumbnail = new Thumbnail(new Blob(cursor.getBlob(startColumnIndex + 10)));
        arrow.imageFile = cursor.getString(startColumnIndex + 11);
        return arrow;
    }

    public Arrow get(long arrow) {
        Cursor cursor = database.rawQuery("SELECT _id, name, length, material, " +
                "spine, weight, tip_weight, vanes, nock, comment, thumbnail, image " +
                "FROM ARROW WHERE _id = " + arrow, null);
        Arrow a = null;
        if (cursor.moveToFirst()) {
            a = cursorToArrow(cursor, 0);
            a.numbers = new ArrowNumberDataSource().getAll(a.getId());
        }
        cursor.close();
        return a;
    }

    public ArrayList<Arrow> getAll() {
        Cursor res = database.rawQuery(
                "SELECT _id, name, length, material, spine, weight, tip_weight, vanes, " +
                        "nock, comment, thumbnail, image " +
                        "FROM ARROW ORDER BY _id ASC", null);
        ArrayList<Arrow> list = new ArrayList<>(res.getCount());
        if (res.moveToFirst()) {
            ArrowNumberDataSource arrowNumberDataSource = new ArrowNumberDataSource();
            do {
                Arrow arrow = cursorToArrow(res, 0);
                arrow.numbers = arrowNumberDataSource.getAll(arrow.getId());
                list.add(arrow);
            } while (res.moveToNext());
        }
        res.close();
        return list;
    }
}
