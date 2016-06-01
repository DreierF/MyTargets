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
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.shared.models.Distance;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.targets.TargetModelBase;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;

public class StandardRoundDataSource extends IdProviderDataSource<StandardRound> {
    private static final String TABLE = "STANDARD_ROUND_TEMPLATE";
    private static final String NAME = "name";
    private static final String INSTITUTION = "club";
    private static final String INDOOR = "indoor";
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    NAME + " TEXT," +
                    INSTITUTION + " INTEGER," +
                    INDOOR + " INTEGER);";

    public StandardRoundDataSource(Context context, DatabaseManager dbHelper, SQLiteDatabase db) {
        super(context, TABLE, dbHelper, db);
    }

    public StandardRoundDataSource(Context context) {
        super(context, TABLE);
    }

    @Override
    public void update(StandardRound item) {
        super.update(item);
        RoundTemplateDataSource rtds = new RoundTemplateDataSource(getContext(), dbHelper, database);
        for (RoundTemplate template : item.getRounds()) {
            template.standardRound = item.getId();
            rtds.update(template);
        }
    }

    @Override
    public ContentValues getContentValues(StandardRound standardRound) {
        ContentValues values = new ContentValues();
        values.put(NAME, standardRound.name);
        values.put(INSTITUTION, standardRound.club);
        values.put(INDOOR, standardRound.indoor ? 1 : 0);
        return values;
    }

    private StandardRound cursorToStandardRound(Cursor cursor) {
        StandardRound standardRound = new StandardRound();
        standardRound.setId(cursor.getLong(0));
        standardRound.name = cursor.getString(1);
        standardRound.club = cursor.getInt(2);
        standardRound.indoor = cursor.getInt(3) == 1;
        return standardRound;
    }


    public StandardRound get(long standardRoundId) {
        Cursor cursor = database.rawQuery("SELECT s._id, s.name, s.club, s.indoor, " +
                "a._id, a.r_index, a.arrows, a.target, a.scoring_style, a.target, a.scoring_style, a.distance, a.unit, " +
                "a.size, a.target_unit, a.passes, a.sid " +
                "FROM STANDARD_ROUND_TEMPLATE s " +
                "LEFT JOIN ROUND_TEMPLATE a ON s._id=a.sid " +
                "WHERE s._id = " + standardRoundId, null);

        StandardRound sr = null;
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

    public ArrayList<StandardRound> getAll() {
        Cursor cursor = database.rawQuery("SELECT s._id FROM STANDARD_ROUND_TEMPLATE s", null);
        ArrayList<StandardRound> list = new ArrayList<>(cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                list.add(get(cursor.getLong(0)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    @NonNull
    public List<StandardRound> getAllFiltered(int clubs, boolean indoor, boolean isMetric, int checked) {
        List<StandardRound> list = getAll();
        ArrayList<StandardRound> displayList = new ArrayList<>();
        String unitDistance = isMetric ? Distance.METER : Distance.YARDS;
        for (StandardRound r : list) {
            ArrayList<RoundTemplate> rounds = r.getRounds();
            if (rounds.size() > 0 && ((r.club & clubs) != 0 ||
                    r.name.startsWith("NFAA/IFAA") && (clubs & StandardRoundFactory.IFAA) != 0) &&
                    rounds.get(0).distance.unit.equals(unitDistance) &&
                    r.indoor == indoor) {
                TargetModelBase target = rounds.get(0).target.getModel();
                if ((checked != R.id.field || target.isFieldTarget()) &&
                        (checked != R.id.three_d || target.is3DTarget())) {
                    displayList.add(r);
                }
            }
        }
        return displayList;
    }

    public List<StandardRound> getAllSearch(String query) {
        query = query.replace(' ', '%');
        Cursor cursor = database.rawQuery("SELECT s._id FROM STANDARD_ROUND_TEMPLATE s WHERE s.name LIKE '%' || ? || '%' AND s.club!=512", new String[]{query});
        ArrayList<StandardRound> list = new ArrayList<>(cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                list.add(get(cursor.getLong(0)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}
