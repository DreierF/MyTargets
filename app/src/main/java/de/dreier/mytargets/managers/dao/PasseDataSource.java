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

import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.Shot;

public class PasseDataSource extends IdProviderDataSource<Passe> {
    public static final String TABLE = "PASSE";
    public static final String ROUND = "round";
    private static final String IMAGE = "image";
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ROUND + " INTEGER," +
                    IMAGE + " TEXT);";

    public PasseDataSource(Context context) {
        super(context, TABLE);
    }

    @Override
    public void update(Passe item) {
        super.update(item);
        ShotDataSource sds = new ShotDataSource(getContext());
        for (Shot shot : item.shot) {
            sds.update(shot);
        }
    }

    @Override
    public ContentValues getContentValues(Passe passe) {
        if (passe.shot.length == 0) {
            return null;
        }
        ContentValues values = new ContentValues();
        values.put(ROUND, passe.roundId);
        return values;
    }

    public Passe get(long round, int passe) {
        open();
        Cursor cursor = database
                .rawQuery("SELECT _id FROM PASSE WHERE round = " + round + " ORDER BY _id ASC",
                        null);
        if (!cursor.moveToPosition(passe)) {
            return null;
        }
        long passeId = cursor.getLong(0);
        cursor.close();
        Passe p = get(passeId);
        p.index = passe;
        return p;
    }

    private Passe get(long passeId) {
        Cursor res = database.rawQuery(
                "SELECT _id, passe, points, x, y, comment, arrow, arrow_index " +
                        "FROM SHOOT s " +
                        "WHERE s.passe = " + passeId + " " +
                        "ORDER BY _id ASC", null);
        int count = res.getCount();

        Passe p = new Passe(count);
        p.setId(passeId);
        p.index = -1;
        res.moveToFirst();
        for (int i = 0; i < count; i++) {
            p.shot[i] = ShotDataSource.cursorToShot(res, i);
            res.moveToNext();
        }
        res.close();
        return p;
    }

    public ArrayList<Passe> getAll(long round) {
        open();
        Cursor res = database.rawQuery(
                "SELECT s._id, s.passe, s.points, s.x, s.y, s.comment, s.arrow, s.arrow_index, " +
                        "(SELECT COUNT(x._id) FROM SHOOT x WHERE x.passe=p._id) " +
                        "FROM PASSE p  " +
                        "LEFT JOIN SHOOT s ON p._id = s.passe " +
                        "WHERE p.round = " + round + " " +
                        "ORDER BY p._id ASC, s._id ASC", null);
        ArrayList<Passe> list = new ArrayList<>();
        if (res.moveToFirst()) {
            long oldRoundId = -1;
            int pIndex = 0;
            do {
                int ppp = res.getInt(8);
                if (ppp == 0) {
                    res.moveToNext();
                    continue;
                }
                Passe passe = new Passe(ppp);
                passe.setId(res.getLong(1));
                passe.roundId = round;
                if (oldRoundId != passe.roundId) {
                    pIndex = 0;
                    oldRoundId = passe.roundId;
                }
                passe.index = pIndex++;
                for (int i = 0; i < ppp; i++) {
                    passe.shot[i] = ShotDataSource.cursorToShot(res, i);
                    res.moveToNext();
                }
                list.add(passe);
            } while (!res.isAfterLast());
        }
        res.close();
        return list;
    }

    public ArrayList<Passe> getAllByTraining(long training) {
        open();
        Cursor res = database.rawQuery(
                "SELECT s._id, s.passe, s.points, s.x, s.y, s.comment, s.arrow, s.arrow_index, r._id, " +
                        "(SELECT COUNT(x._id) FROM SHOOT x WHERE x.passe=p._id) " +
                        "FROM ROUND r " +
                        "LEFT JOIN PASSE p ON r._id = p.round " +
                        "LEFT JOIN SHOOT s ON p._id = s.passe " +
                        "WHERE r.training = " + training + " " +
                        "ORDER BY r._id ASC, p._id ASC, s._id ASC", null);
        ArrayList<Passe> list = new ArrayList<>();
        if (res.moveToFirst()) {
            long oldRoundId = -1;
            int pIndex = 0;
            do {
                int ppp = res.getInt(9);
                if (ppp == 0) {
                    res.moveToNext();
                    continue;
                }
                Passe passe = new Passe(ppp);
                passe.setId(res.getLong(1));
                passe.roundId = res.getLong(8);
                if (oldRoundId != passe.roundId) {
                    pIndex = 0;
                    oldRoundId = passe.roundId;
                }
                passe.index = pIndex++;
                for (int i = 0; i < ppp; i++) {
                    passe.shot[i] = ShotDataSource.cursorToShot(res, i);
                    res.moveToNext();
                }
                list.add(passe);
            } while (!res.isAfterLast());
        }
        res.close();
        return list;
    }
}
