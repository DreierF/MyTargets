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

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import java.util.HashSet;
import java.util.List;

import de.dreier.mytargets.shared.AppDatabase;
import de.dreier.mytargets.shared.SharedApplicationInstance;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.db.StandardRound;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE;

@Migration(version = 9, database = AppDatabase.class)
public class Migration9 extends BaseMigration {

    @Override
    public void migrate(DatabaseWrapper database) {
        database.execSQL("DROP TABLE IF EXISTS ZONE_MATRIX");
        database.execSQL("ALTER TABLE VISIER ADD COLUMN unit TEXT DEFAULT 'm'");
        database.execSQL("ALTER TABLE PASSE ADD COLUMN image TEXT DEFAULT ''");
        database.execSQL("ALTER TABLE SHOOT ADD COLUMN arrow INTEGER DEFAULT '-1'");
        database.execSQL("ALTER TABLE SHOOT ADD COLUMN arrow_index INTEGER DEFAULT '-1'");
        database.execSQL("ALTER TABLE TRAINING ADD COLUMN weather INTEGER DEFAULT '0'");
        database.execSQL("ALTER TABLE TRAINING ADD COLUMN wind_speed INTEGER DEFAULT '0'");
        database.execSQL("ALTER TABLE TRAINING ADD COLUMN wind_direction INTEGER DEFAULT '0'");
        database.execSQL("ALTER TABLE TRAINING ADD COLUMN location TEXT DEFAULT ''");
        database.execSQL("ALTER TABLE TRAINING ADD COLUMN standard_round INTEGER DEFAULT '0'");
        database.execSQL("ALTER TABLE TRAINING ADD COLUMN bow INTEGER DEFAULT '0'");
        database.execSQL("ALTER TABLE TRAINING ADD COLUMN arrow INTEGER DEFAULT '0'");
        database.execSQL("ALTER TABLE TRAINING ADD COLUMN arrow_numbering INTEGER DEFAULT '0'");
        database.execSQL("ALTER TABLE TRAINING ADD COLUMN time INTEGER DEFAULT '-1'");
        database.execSQL("UPDATE ROUND SET target=11 WHERE target=6"); // DFBV Spiegel Spot
        database.execSQL("UPDATE ROUND SET target=10 WHERE target=5"); // DFBV Spiegel
        database.execSQL("UPDATE ROUND SET target=13 WHERE target=4"); // WA Field
        database.execSQL("UPDATE ROUND SET target=4 WHERE target=3"); // WA 3 Spot -> vegas

        // Set all compound 3 spot to vertical
        database.execSQL("UPDATE ROUND SET target=target+1 " +
                "WHERE _id IN (SELECT r._id FROM ROUND r " +
                "LEFT JOIN BOW b ON b._id=r.bow " +
                "WHERE (r.bow=-2 OR b.type=1) AND r.target=4)");

        // Add shot indices
        database.execSQL("UPDATE SHOOT SET arrow_index=( " +
                "SELECT COUNT(*) FROM SHOOT s " +
                "WHERE s._id<SHOOT._id " +
                "AND s.passe=SHOOT.passe) " +
                "WHERE arrow_index=-1");

        // transform before inner points to after inner points
        database.execSQL("UPDATE SHOOT SET x = x/2.0, y = y/2.0 " +
                "WHERE _id IN (SELECT s._id " +
                "FROM ROUND r " +
                "LEFT JOIN PASSE p ON r._id = p.round " +
                "LEFT JOIN SHOOT s ON p._id = s.passe " +
                "WHERE r.target<11 AND s.points=0);");
        database.execSQL("ALTER TABLE ROUND RENAME TO ROUND_OLD");

        Context context = SharedApplicationInstance.getContext();
        database.execSQL("CREATE TABLE IF NOT EXISTS " + StandardRoundDataSource.TABLE + " (" +
                IdProviderDataSource.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                StandardRoundDataSource.NAME + " TEXT," +
                StandardRoundDataSource.INSTITUTION + " INTEGER," +
                StandardRoundDataSource.INDOOR + " INTEGER);");
        database.execSQL("CREATE TABLE IF NOT EXISTS " + RoundTemplateDataSource.TABLE + " (" +
                IdProviderDataSource.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                RoundTemplateDataSource.STANDARD_ID + " INTEGER," +
                RoundTemplateDataSource.INDEX + " INTEGER," +
                RoundTemplateDataSource.DISTANCE + " INTEGER," +
                RoundTemplateDataSource.UNIT + " TEXT," +
                RoundTemplateDataSource.PASSES + " INTEGER," +
                RoundTemplateDataSource.ARROWS_PER_PASSE + " INTEGER," +
                RoundTemplateDataSource.TARGET + " INTEGER," +
                RoundTemplateDataSource.TARGET_SIZE + " INTEGER," +
                RoundTemplateDataSource.TARGET_SIZE_UNIT + " INTEGER," +
                RoundTemplateDataSource.SCORING_STYLE + " INTEGER," +
                "UNIQUE(sid, r_index) ON CONFLICT REPLACE);");
        List<StandardRound> rounds = StandardRoundFactory.initTable();
        StandardRoundDataSource standardRoundDataSource = new StandardRoundDataSource(context, database);
        for (StandardRound round : rounds) {
            standardRoundDataSource.update(new StandardRoundOld(round));
        }

        database.execSQL("CREATE TABLE IF NOT EXISTS ROUND (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "training INTEGER," +
                "comment TEXT," +
                "template INTEGER," +
                "target INTEGER," +
                "scoring_style INTEGER);");
        RoundTemplateDataSource roundTemplateDataSource = new RoundTemplateDataSource(SharedApplicationInstance.getContext(), database);

        Cursor trainings = database.rawQuery("SELECT _id FROM TRAINING", null);
        if (trainings.moveToFirst()) {
            do {
                long training = trainings.getLong(0);
                long sid = getOrCreateStandardRound(database, training);
                if (sid == 0) {
                    database.execSQL("DELETE FROM TRAINING WHERE _id=" + training);
                } else {
                    database.execSQL(
                            "UPDATE TRAINING SET standard_round=" + sid + " WHERE _id=" + training);
                }

                Cursor res = database.rawQuery(
                        "SELECT r._id, r.comment, r.target, r.bow, r.arrow " +
                                "FROM ROUND_OLD r " +
                                "WHERE r.training=" + training + " " +
                                "GROUP BY r._id " +
                                "ORDER BY r._id ASC", null);
                int index = 0;
                if (res.moveToFirst()) {
                    long bow = res.getLong(3);
                    long arrow = res.getLong(4);
                    database.execSQL(
                            "UPDATE TRAINING SET bow=" + bow + ", arrow=" + arrow + " WHERE _id=" + training);
                    do {
                        RoundTemplateOld info = roundTemplateDataSource.get(sid, index);
                        int target = res.getInt(2);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("_id", res.getLong(0));
                        contentValues.put("comment", res.getString(1));
                        contentValues.put("training", training);
                        contentValues.put("template", info.getId());
                        contentValues
                                .put("target", target == 4 ? 5 : target);
                        contentValues.put("scoring_style",
                                target == 5 ? 1 : 0);
                        database.insertWithOnConflict("ROUND", null, contentValues, CONFLICT_IGNORE);
                        index++;
                    } while (res.moveToNext());
                }
                res.close();
            } while (trainings.moveToNext());
        }
        trainings.close();
    }

    private Long getOrCreateStandardRound(DatabaseWrapper db, long training) {
        Cursor res = db.rawQuery(
                "SELECT r.ppp, r.target, r.distance, r.unit, COUNT(p._id), r.indoor " +
                        "FROM ROUND_OLD r " +
                        "LEFT JOIN PASSE p ON r._id = p.round " +
                        "WHERE r.training=" + training + " " +
                        "GROUP BY r._id " +
                        "ORDER BY r._id ASC", null);
        int index = 0;
        HashSet<Long> sid = new HashSet<>();
        StandardRoundOld sr = new StandardRoundOld();
        sr.name = "Practice";
        sr.club = 512;
        if (res.moveToFirst()) {
            sr.indoor = res.getInt(5) == 1;
            do {
                RoundTemplateOld template = new RoundTemplateOld();
                template.arrowsPerPasse = res.getInt(0);
                int target = res.getInt(1);
                template.target = new Target(target == 4 ? 5 : target, target == 5 ? 1 : 0);
                template.distance = new DistanceOld(res.getInt(2), res.getString(3));
                template.passes = res.getInt(4);
                template.targetTemplate = template.target;
                sr.insert(template);
                long tid = template.target.getId();
                tid = tid < 7 ? 0 : tid;
                tid = tid == 11 ? 10 : tid;
                Cursor sids = db.rawQuery("SELECT sid FROM ROUND_TEMPLATE " +
                        "WHERE r_index=" + index + " " +
                        "AND distance=" + template.distance.value + " " +
                        "AND unit=\"" + template.distance.unit + "\" " +
                        "AND arrows=" + template.arrowsPerPasse + " " +
                        "AND passes=" + template.passes + " " +
                        "AND target=" + tid + " " +
                        "AND (SELECT COUNT(r._id) FROM ROUND_TEMPLATE r WHERE r.sid=ROUND_TEMPLATE.sid)=" +
                        res.getCount(), null);
                HashSet<Long> sid_tmp = new HashSet<>();
                if (sids.moveToFirst()) {
                    do {
                        sid_tmp.add(sids.getLong(0));
                    } while (sids.moveToNext());
                }
                sids.close();
                if (index == 0) {
                    sid = sid_tmp;
                } else {
                    sid.retainAll(sid_tmp);
                }
                index++;
            } while (res.moveToNext());
        }
        res.close();

        // A standard round exists that matches this specific pattern
        if (!sid.isEmpty()) {
            return sid.toArray(new Long[sid.size()])[0];
        }

        if (sr.getRounds().isEmpty()) {
            return 0L;
        }
        new StandardRoundDataSource(SharedApplicationInstance.getContext(), db).update(sr);
        return sr.getId();
    }
}
