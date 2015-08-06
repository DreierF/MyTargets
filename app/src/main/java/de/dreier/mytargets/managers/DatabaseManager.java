/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.managers;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.EditBowActivity;
import de.dreier.mytargets.shared.models.Arrow;
import de.dreier.mytargets.shared.models.Bow;
import de.dreier.mytargets.shared.models.DatabaseSerializable;
import de.dreier.mytargets.shared.models.Distance;
import de.dreier.mytargets.shared.models.Passe;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.Shot;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.models.Training;
import de.dreier.mytargets.shared.models.target.Target;
import de.dreier.mytargets.shared.models.target.TargetFactory;
import de.dreier.mytargets.utils.BackupUtils;
import de.dreier.mytargets.utils.Pair;

public class DatabaseManager extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 9; //TODO set to 9 before publishing

    private static final String ID = "_id";
    public static final String DATABASE_NAME = "database";
    private static final String TABLE_VISIER = "VISIER";
    private static final String VISIER_BOW = "bow";
    private static final String VISIER_DISTANCE = "distance";
    private static final String VISIER_UNIT = "unit";
    private static final String VISIER_SETTING = "setting";
    private static final String CREATE_TABLE_VISIER =
            "CREATE TABLE IF NOT EXISTS " + TABLE_VISIER + " ( " +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    VISIER_BOW + " INTEGER," +
                    VISIER_DISTANCE + " INTEGER," +
                    VISIER_SETTING + " TEXT, " +
                    VISIER_UNIT + " TEXT);";
    private static final String TABLE_NUMBER = "NUMBER";
    private static final String NUMBER_ARROW = "arrow";
    private static final String NUMBER_VALUE = "value";
    private static final String CREATE_TABLE_NUMBER =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NUMBER + " ( " +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    NUMBER_ARROW + " INTEGER," +
                    NUMBER_VALUE + " INTEGER);";
    private static DatabaseManager sInstance;
    private final Context mContext;
    private SQLiteDatabase db;

    private DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
        db = getWritableDatabase();
    }

    public static DatabaseManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseManager(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Bow.CREATE_TABLE_BOW);
        db.execSQL(CREATE_TABLE_VISIER);
        db.execSQL(CREATE_TABLE_NUMBER);
        db.execSQL(Arrow.CREATE_TABLE);
        db.execSQL(Training.CREATE_TABLE);
        db.execSQL(RoundTemplate.CREATE_TABLE);
        db.execSQL(Round.CREATE_TABLE);
        db.execSQL(Passe.CREATE_TABLE);
        db.execSQL(Shot.CREATE_TABLE);
        fillStandardRound(db);
    }

    private void fillStandardRound(SQLiteDatabase db) {
        db.execSQL(StandardRound.CREATE_TABLE);
        this.db = db;
        ArrayList<StandardRound> rounds = StandardRound.initTable(mContext);
        for (StandardRound round : rounds) {
            update(round);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS " + Bow.TABLE);
            db.execSQL("DROP TABLE IF EXISTS BOW_IMAGE");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_VISIER);
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE SHOOT ADD COLUMN x REAL");
            db.execSQL("ALTER TABLE SHOOT ADD COLUMN y REAL");
            Cursor cur = db.rawQuery("SELECT s._id, s.points, r.target " +
                    "FROM SHOOT s, PASSE p, ROUND r " +
                    "WHERE s.passe=p._id " +
                    "AND p.round=r._id", null);
            if (cur.moveToFirst()) {
                do {
                    int shoot = cur.getInt(0);
                    db.execSQL("UPDATE SHOOT SET x=0, y=0 WHERE _id=" + shoot);
                } while (cur.moveToNext());
            }
            cur.close();
        }
        if (oldVersion < 4) {
            int[] valuesMetric = {10, 15, 18, 20, 25, 30, 40, 50, 60, 70, 90};
            for (String table : new String[]{"ROUND", "VISIER"}) {
                for (int i = 10; i >= 0; i--) {
                    db.execSQL("UPDATE " + table + " SET distance=" +
                            valuesMetric[i] + " WHERE distance=" +
                            i);
                }
            }
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            int defaultDist = valuesMetric[prefs.getInt("distance", 0)];
            prefs.edit().putInt("distance", defaultDist).apply();
        }
        if (oldVersion < 5) {
            db.execSQL(Arrow.CREATE_TABLE);
            db.execSQL("ALTER TABLE ROUND ADD COLUMN arrow INTEGER REFERENCES " + Arrow.TABLE +
                    " ON DELETE SET NULL");
            db.execSQL("ALTER TABLE ROUND ADD COLUMN comment TEXT DEFAULT ''");
            db.execSQL("ALTER TABLE SHOOT ADD COLUMN comment TEXT DEFAULT ''");
            db.execSQL("UPDATE ROUND SET target=0 WHERE target=1 OR target=2 OR target=3");
            db.execSQL("UPDATE ROUND SET target=2 WHERE target=5 OR target=6 OR target=7");
            db.execSQL("UPDATE ROUND SET target=3 WHERE target=4");
            db.execSQL("UPDATE ROUND SET target=4 WHERE target=8");
            db.execSQL("UPDATE ROUND SET target=5 WHERE target=9");
            db.execSQL("UPDATE ROUND SET target=6 WHERE target=10");
            db.execSQL("UPDATE SHOOT SET points=2 WHERE _id IN (SELECT s._id " +
                    "FROM ROUND r, PASSE p, SHOOT s LEFT JOIN BOW b ON b._id=r.bow " +
                    "WHERE r._id=p.round AND s.passe=p._id " +
                    "AND (r.bow=-2 OR b.type=1) AND s.points=1 AND r.target=3)");
        }
        if (oldVersion < 6) {
            File filesDir = mContext.getFilesDir();

            // Migrate all bow images
            Cursor cur = db.rawQuery("SELECT image FROM BOW WHERE image IS NOT NULL", null);
            if (cur.moveToFirst()) {
                String fileName = cur.getString(0);
                try {
                    File file = File.createTempFile("img_", ".png", filesDir);
                    BackupUtils.copy(new File(fileName), file);
                    db.execSQL("UPDATE BOW SET image=\"" + file.getName() + "\" WHERE image=\"" +
                            fileName + "\"");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            cur.close();

            // Migrate all arrows images
            cur = db.rawQuery("SELECT image FROM ARROW WHERE image IS NOT NULL", null);
            if (cur.moveToFirst()) {
                String fileName = cur.getString(0);
                try {
                    File file = File.createTempFile("img_", ".png", filesDir);
                    BackupUtils.copy(new File(fileName), file);
                    db.execSQL("UPDATE ARROW SET image=\"" + file.getName() + "\" WHERE image=\"" +
                            fileName + "\"");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            cur.close();
        }
        if (oldVersion < 7) {
            cleanup(db);
        }
        if (oldVersion < 8) {
            db.execSQL("UPDATE ROUND SET target=4 WHERE target=8");
            db.execSQL("UPDATE ROUND SET target=5 WHERE target=9");
            db.execSQL("UPDATE ROUND SET target=6 WHERE target=10");
        }
        if (oldVersion < 9) {
            db.execSQL("DROP TABLE IF EXISTS ZONE_MATRIX");
            db.execSQL("ALTER TABLE VISIER ADD COLUMN unit TEXT DEFAULT 'm'");
            db.execSQL("ALTER TABLE PASSE ADD COLUMN image TEXT DEFAULT ''");
            db.execSQL("ALTER TABLE SHOOT ADD COLUMN arrow INTEGER DEFAULT '-1'");
            db.execSQL("ALTER TABLE SHOOT ADD COLUMN arrow_index INTEGER DEFAULT '-1'");
            db.execSQL("ALTER TABLE TRAINING ADD COLUMN weather INTEGER DEFAULT '0'");
            db.execSQL("ALTER TABLE TRAINING ADD COLUMN wind_speed INTEGER DEFAULT '0'");
            db.execSQL("ALTER TABLE TRAINING ADD COLUMN wind_direction INTEGER DEFAULT '0'");
            db.execSQL("ALTER TABLE TRAINING ADD COLUMN location TEXT DEFAULT ''");
            db.execSQL("ALTER TABLE TRAINING ADD COLUMN standard_round INTEGER DEFAULT '0'");
            db.execSQL("ALTER TABLE TRAINING ADD COLUMN bow INTEGER DEFAULT '0'");
            db.execSQL("ALTER TABLE TRAINING ADD COLUMN arrow INTEGER DEFAULT '0'");
            db.execSQL("ALTER TABLE TRAINING ADD COLUMN arrow_numbering INTEGER DEFAULT '0'");
            db.execSQL("ALTER TABLE TRAINING ADD COLUMN time INTEGER DEFAULT '-1'");
            db.execSQL("UPDATE ROUND SET target=4 WHERE target=3"); // WA 3 Spot ->vegas
            db.execSQL("UPDATE ROUND SET target=13 WHERE target=4"); // WA Field
            db.execSQL("UPDATE ROUND SET target=10 WHERE target=5"); // DFBV Spiegel
            db.execSQL("UPDATE ROUND SET target=11 WHERE target=6"); // DFBV Spiegel Spot

            // Set all compound 3 spot to vertical
            db.execSQL("UPDATE ROUND SET target=target+1 " +
                    "WHERE _id IN (SELECT r._id FROM ROUND r " +
                    "LEFT JOIN BOW b ON b._id=r.bow " +
                    "WHERE (r.bow=-2 OR b.type=1) AND r.target=4)");

            // Add shot indices
            db.execSQL("UPDATE SHOOT SET arrow_index=( " +
                    "SELECT COUNT(*) FROM SHOOT s " +
                    "WHERE s._id<SHOOT._id " +
                    "AND s.passe=SHOOT.passe) " +
                    "WHERE arrow_index=-1");

            // transform before inner points to after inner points
            db.execSQL("UPDATE SHOOT SET s.x = s.x/2.0, s.y = s.y/2.0 " +
                    "WHERE _id IN (SELECT s._id " +
                    "FROM ROUND r " +
                    "LEFT JOIN PASSE p ON r._id = p.round " +
                    "LEFT JOIN SHOOT s ON p._id = s.passe " +
                    "WHERE r.target<11 AND s.points=0);");
            db.execSQL("ALTER TABLE ROUND RENAME TO ROUND_OLD");

            fillStandardRound(db);

            db.execSQL(Round.CREATE_TABLE);
            Cursor trainings = db.rawQuery("SELECT  FROM ROUND_OLD", null);
            if (trainings.moveToFirst()) {
                long training = trainings.getLong(0);
                long sid = getOrCreateStandardRound(db, training);

                db.execSQL("UPDATE TRAINING SET standard_round=? WHERE _id=?",
                        new String[]{"" + sid, "" + training});

                Cursor res = db.rawQuery(
                        "SELECT r._id, r.comment, r.bow, r.arrow " +
                                "FROM ROUND r " +
                                "LEFT JOIN PASSE p ON r._id = p.round " +
                                "WHERE r.training=" + training + " " +
                                "GROUP BY r._id " +
                                "ORDER BY r._id ASC", null);
                int index = 0;
                if (res.moveToFirst()) {
                    long bow = res.getLong(2);
                    long arrow = res.getLong(3);
                    db.execSQL("UPDATE TRAINING SET bow=?, arrow=? WHERE _id=?",
                            new String[]{"" + bow, "" + arrow, "" + training});
                    do {
                        long template = getTemplateId(sid, index);
                        ContentValues values = new ContentValues();
                        values.put(Round.ID, res.getLong(0));
                        values.put(Round.COMMENT, res.getString(1));
                        values.put(Round.TRAINING, training);
                        values.put(Round.TEMPLATE, template);
                        db.insert(Round.TABLE, null, values);
                        index++;
                    } while (res.moveToNext());
                }
                res.close();
            }
            trainings.close();
        }
        onCreate(db);
    }

    private long getTemplateId(long sid, int index) {
        Cursor sids = db.rawQuery("SELECT _id FROM ROUND_TEMPLATE " +
                        "WHERE sid=? AND index=?",
                new String[]{String.valueOf(sid),
                        String.valueOf(index)
                });
        long id = 0;
        if (sids.moveToFirst()) {
            id = sids.getLong(0);
        }
        sids.close();
        return id;
    }

    private Long getOrCreateStandardRound(SQLiteDatabase db, long training) {
        Cursor res = db.rawQuery(
                "SELECT r.ppp, r.target, r.distance, r.unit, COUNT(p._id), r.indoor " +
                        "FROM ROUND_OLD r " +
                        "LEFT JOIN PASSE p ON r._id = p.round " +
                        "WHERE r.training=" + training + " " +
                        "GROUP BY r._id " +
                        "ORDER BY r._id ASC", null);
        int index = 0;
        HashSet<Long> sid = new HashSet<>();
        StandardRound sr = new StandardRound();
        sr.club = StandardRound.CUSTOM;
        if (res.moveToFirst()) {
            sr.indoor = res.getInt(5) == 1;
            do {
                RoundTemplate template = new RoundTemplate();
                template.arrowsPerPasse = res.getInt(0);
                int target = res.getInt(1);
                template.target = TargetFactory.createTarget(mContext,
                        target == 4 ? 5 : target, target == 5 ? 1 : 0);
                template.distance = new Distance(res.getInt(3), res.getString(4));
                template.passes = res.getInt(5);
                template.target.size = template.target.getDiameters()[0];
                sr.insert(template);
                Cursor sids = db.rawQuery("SELECT sid FROM ROUND_TEMPLATE " +
                                "WHERE index=? " +
                                "AND distance=? " +
                                "AND unit=? " +
                                "AND arrows=? " +
                                "AND passes=? " +
                                "AND target=?",
                        new String[]{String.valueOf(index),
                                String.valueOf(template.distance.value),
                                template.distance.unit,
                                String.valueOf(template.arrowsPerPasse),
                                String.valueOf(template.passes),
                                String.valueOf(template.target.getId())
                        });
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

        update(sr);
        return sr.getId();
    }

////// GET COMPLETE TABLE //////

    public ArrayList<Training> getTrainings() {
        Cursor cursor = db
                .rawQuery("SELECT t._id, t.title, t.datum, t.bow, t.arrow, t.standard_round, " +
                        "t.arrow_numbering, t.time, " +
                        "t.weather, t.wind_speed, t.wind_direction, t.location " +
                        "FROM TRAINING t " +
                        "LEFT JOIN ROUND r ON t._id = r.training " +
                        "LEFT JOIN ROUND_TEMPLATE a ON r.template=a._id " +
                        "LEFT JOIN PASSE p ON r._id = p.round " +
                        "LEFT JOIN SHOOT s ON p._id = s.passe " +
                        "LEFT JOIN BOW b ON b._id = t.bow " +
                        "GROUP BY t._id " +
                        "ORDER BY t.datum DESC", null);

        ArrayList<Training> list = new ArrayList<>(cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                Training t = new Training();
                t.fromCursor(mContext, cursor, 0);
                list.add(t);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public Map<Pair<Integer, String>, Integer> getTrainingScoreDistribution(long training) {
        Cursor cursor = db.rawQuery("SELECT a.target, a.scoring_style, s.points, s.arrow_index, COUNT(*) " +
                "FROM ROUND r " +
                "LEFT JOIN ROUND_TEMPLATE a ON r.template=a._id " +
                "LEFT JOIN PASSE p ON r._id = p.round " +
                "LEFT JOIN SHOOT s ON p._id = s.passe " +
                "WHERE r.training=" + training + " " +
                "GROUP BY a.target, a.scoring_style, s.points, s.arrow_index", null);

        if (!cursor.moveToFirst()) {
            cursor.close();
            throw new IllegalStateException("There must be at least one round!");
        }
        Target t = TargetFactory.createTarget(mContext, cursor.getInt(0), cursor.getInt(1));
        Map<Pair<Integer, String>, Integer> scoreCount = new HashMap<>();
        for (int arrow = 0; arrow < 3; arrow++) {
            for (int zone = -1; zone < t.getZones(); zone++) {
                scoreCount.put(new Pair<>(t.getPointsByZone(zone, arrow),
                        t.zoneToString(zone, arrow)), 0);
            }
            if (!t.dependsOnArrowIndex()) {
                break;
            }
        }
        do {
            if (cursor.isNull(2)) {
                continue;
            }
            int zone = cursor.getInt(2);
            int arrow = cursor.getInt(3);
            int count = cursor.getInt(4);
            Pair<Integer, String> tuple = new Pair<>(t.getPointsByZone(zone, arrow),
                    t.zoneToString(zone, arrow));
            count += scoreCount.get(tuple);
            scoreCount.put(tuple, count);
        } while (cursor.moveToNext());
        cursor.close();
        return scoreCount;
    }

    public ArrayList<Pair<String, Integer>> getTrainingTopScoreDistribution(long training) {
        Map<Pair<Integer, String>, Integer> scoreCount = getTrainingScoreDistribution(training);
        ArrayList<Pair<Integer, String>> list = new ArrayList<>(scoreCount.keySet());
        Collections.sort(list, (lhs, rhs) -> {
            if (lhs.getFirst().equals(rhs.getFirst())) {
                return -lhs.getSecond().compareTo(rhs.getSecond());
            }
            if (lhs.getFirst() > rhs.getFirst()) {
                return -1;
            }
            return 1;
        });
        ArrayList<Pair<String, Integer>> topScore = new ArrayList<>();
        topScore.add(new Pair<>(list.get(0).getSecond(), scoreCount.get(list.get(0))));
        boolean collapseFirst = list.get(0).getFirst().equals(list.get(1).getFirst());
        if (collapseFirst) {
            topScore.add(new Pair<>(list.get(1).getSecond() + "+" + list.get(0).getSecond(),
                    scoreCount.get(list.get(1)) + scoreCount.get(list.get(0))));
        } else {
            topScore.add(new Pair<>(list.get(1).getSecond(), scoreCount.get(list.get(1))));
        }
        topScore.add(new Pair<>(list.get(2).getSecond(), scoreCount.get(list.get(2))));
        return topScore;
    }

    public ArrayList<Round> getRounds(long training) {
        Cursor res = db.rawQuery("SELECT r._id " +
                "FROM ROUND r " +
                "WHERE r.training=" + training + " " +
                "ORDER BY r._id ASC", null);
        ArrayList<Round> list = new ArrayList<>(res.getCount());
        if (res.moveToFirst()) {
            do {
                Round r = getRound(res.getLong(0));
                list.add(r);
            } while (res.moveToNext());
        }
        res.close();
        return list;
    }

    public ArrayList<Passe> getPasses(long training) {
        Cursor res = db.rawQuery(
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
                    passe.shot[i].fromCursor(mContext, res, 0);
                    res.moveToNext();
                }
                list.add(passe);
            } while (!res.isAfterLast());
        }
        res.close();
        return list;
    }

    public ArrayList<Passe> getPassesOfRound(long round) {
        Cursor res = db.rawQuery(
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
                    passe.shot[i].fromCursor(mContext, res, 0);
                    res.moveToNext();
                }
                list.add(passe);
            } while (!res.isAfterLast());
        }
        res.close();
        return list;
    }

    public ArrayList<Bow> getBows() {
        Cursor res = db.rawQuery(
                "SELECT _id, name, type, brand, size, height, tiller, description, thumbnail " +
                        "FROM BOW " +
                        "ORDER BY _id ASC", null);
        ArrayList<Bow> list = new ArrayList<>(res.getCount());
        if (res.moveToFirst()) {
            do {
                Bow bow = new Bow();
                bow.fromCursor(mContext, res, 0);
                byte[] data = res.getBlob(8);
                bow.image = BitmapFactory.decodeByteArray(data, 0, data.length);
                list.add(bow);
            } while (res.moveToNext());
        }
        res.close();
        return list;
    }

    public ArrayList<Arrow> getArrows() {
        Cursor res = db.rawQuery(
                "SELECT _id, name, length, material, spine, weight, vanes, nock, comment, thumbnail " +
                        "FROM ARROW " +
                        "ORDER BY _id ASC", null);
        ArrayList<Arrow> list = new ArrayList<>(res.getCount());
        if (res.moveToFirst()) {
            do {
                Arrow arrow = new Arrow();
                arrow.fromCursor(mContext, res, 0);
                byte[] data = res.getBlob(9);
                arrow.image = BitmapFactory.decodeByteArray(data, 0, data.length);
                arrow.numbers = getArrowNumbers(arrow.getId());
                list.add(arrow);
            } while (res.moveToNext());
        }
        res.close();
        return list;
    }

    public ArrayList<StandardRound> getStandardRounds() {
        Cursor cursor = db.rawQuery("SELECT s._id FROM STANDARD_ROUND_TEMPLATE s", null);
        ArrayList<StandardRound> list = new ArrayList<>(cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                list.add(getStandardRound(cursor.getLong(0)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

////// GET SINGLE ENTRY AS OBJECT //////

    public Training getTraining(long training) {
        Cursor cursor = db
                .rawQuery("SELECT t._id, t.title, t.datum, t.bow, t.arrow, t.standard_round, " +
                        "t.arrow_numbering, t.time, " +
                        "t.weather, t.wind_speed, t.wind_direction, t.location " +
                        "FROM TRAINING t " +
                        "LEFT JOIN ROUND r ON t._id = r.training " +
                        "LEFT JOIN ROUND_TEMPLATE a ON r.template=a._id " +
                        "LEFT JOIN PASSE p ON r._id = p.round " +
                        "LEFT JOIN SHOOT s ON p._id = s.passe " +
                        "WHERE t._id = " + training, null);

        Training tr = new Training();
        if (cursor.moveToFirst()) {
            tr.fromCursor(mContext, cursor, 0);
        }
        cursor.close();
        return tr;
    }

    public Round getRound(long round) {
        // Get all generic round attributes
        Cursor cursor = db.rawQuery(
                "SELECT r._id, r.comment, " +
                        "a._id, a.r_index, a.arrows, a.target, a.scoring_style, " +
                        "r.target, r.scoring_style, a.distance, a.unit, " +
                        "a.size, a.target_unit, a.passes, a.sid " +
                        "FROM ROUND r " +
                        "LEFT JOIN ROUND_TEMPLATE a ON r.template=a._id " +
                        "LEFT JOIN PASSE p ON r._id = p.round " +
                        "LEFT JOIN SHOOT s ON p._id = s.passe " +
                        "WHERE r._id=" + round, null);

        cursor.moveToFirst();
        Round r = new Round();
        r.fromCursor(mContext, cursor, 0);
        cursor.close();

        // Calculate reached points
        Cursor cur = db.rawQuery("SELECT s.points, s.arrow_index " +
                "FROM PASSE p, SHOOT s " +
                "WHERE p.round=" + round + " " +
                "AND s.passe=p._id", null);
        if (cur.moveToFirst()) {
            do {
                r.reachedPoints += r.info.target.getPointsByZone(cur.getInt(0), cur.getInt(1));
            } while (cur.moveToNext());
        }
        cur.close();
        return r;
    }

    public Passe getPasse(long passeId) {
        String[] cols = {ID, Shot.PASSE, Shot.ZONE, Shot.X, Shot.Y, Shot.COMMENT, Shot.ARROW,
                Shot.INDEX};
        Cursor res = db
                .query(Shot.TABLE, cols, Shot.PASSE + "=" + passeId, null, null, null,
                        ID + " ASC");
        int count = res.getCount();

        Passe p = new Passe(count);
        p.setId(passeId);
        p.index = -1;
        res.moveToFirst();
        for (int i = 0; i < count; i++) {
            p.shot[i].fromCursor(mContext, res, 0);
            res.moveToNext();
        }
        res.close();
        return p;
    }

    public Passe getPasse(long round, int passe) {
        String[] cols = {ID};
        Cursor res = db
                .query(Passe.TABLE, cols, Passe.ROUND + "=" + round, null, null, null, ID + " ASC");
        if (!res.moveToPosition(passe - 1)) {
            return null;
        }
        long passeId = res.getLong(0);
        res.close();
        Passe p = getPasse(passeId);
        p.index = passe - 1;
        return p;
    }

    public Bow getBow(long bowId, boolean small) {
        String[] cols = {Bow.ID, Bow.NAME, Bow.TYPE, Bow.BRAND, Bow.SIZE, Bow.HEIGHT, Bow.TILLER,
                Bow.DESCRIPTION, Bow.THUMBNAIL, Bow.IMAGE};
        Cursor res = db.query(Bow.TABLE, cols, ID + "=" + bowId, null, null, null, null);
        Bow bow = null;
        if (res.moveToFirst()) {
            bow = new Bow();
            bow.fromCursor(mContext, res, 0);
            if (small) {
                byte[] data = res.getBlob(8);
                bow.image = BitmapFactory.decodeByteArray(data, 0, data.length);
            } else {
                bow.imageFile = res.getString(9);
                try {
                    if (bow.imageFile != null) {
                        if (bow.imageFile.contains("/")) {
                            bow.imageFile = bow.imageFile
                                    .substring(bow.imageFile.lastIndexOf("/") + 1);
                        }
                        FileInputStream in = mContext.openFileInput(bow.imageFile);
                        bow.image = BitmapFactory.decodeStream(in);
                    } else {
                        bow.image = null;
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            res.close();
        }
        return bow;
    }

    public Arrow getArrow(long arrowId, boolean small) {
        String[] cols = {Arrow.ID, Arrow.NAME, Arrow.LENGTH, Arrow.MATERIAL, Arrow.SPINE,
                Arrow.WEIGHT,
                Arrow.VANES, Arrow.NOCK, Arrow.COMMENT, Arrow.THUMBNAIL, Arrow.IMAGE};
        Cursor res = db.query(Arrow.TABLE, cols, ID + "=" + arrowId, null, null, null, null);
        Arrow arrow = null;
        if (res.moveToFirst()) {
            arrow = new Arrow();
            arrow.fromCursor(mContext, res, 0);
            if (small) {
                byte[] data = res.getBlob(9);
                arrow.image = BitmapFactory.decodeByteArray(data, 0, data.length);
            } else {
                arrow.imageFile = res.getString(10);
                try {
                    if (arrow.imageFile != null) {
                        if (arrow.imageFile.contains("/")) {
                            arrow.imageFile = arrow.imageFile
                                    .substring(arrow.imageFile.lastIndexOf("/") + 1);
                        }
                        FileInputStream in = mContext.openFileInput(arrow.imageFile);
                        arrow.image = BitmapFactory.decodeStream(in);
                    } else {
                        arrow.image = null;
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            res.close();
            arrow.numbers = getArrowNumbers(arrowId);
        }
        return arrow;
    }

    public StandardRound getStandardRound(long standardRoundId) {
        Cursor cursor = db.rawQuery("SELECT s._id, s.name, s.club, s.indoor, " +
                "a._id, a.r_index, a.arrows, a.target, a.scoring_style, a.target, a.scoring_style, a.distance, a.unit, " +
                "a.size, a.target_unit, a.passes, a.sid " +
                "FROM STANDARD_ROUND_TEMPLATE s " +
                "LEFT JOIN ROUND_TEMPLATE a ON s._id=a.sid " +
                "WHERE s._id = " + standardRoundId, null);

        StandardRound sr = new StandardRound();
        if (cursor.moveToFirst()) {
            sr.fromCursor(mContext, cursor, 0);
            do {
                if (cursor.getLong(13) == 0) {
                    break;
                }
                RoundTemplate r = new RoundTemplate();
                r.fromCursor(mContext, cursor, 4);
                sr.insert(r);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return sr;
    }

////// GET AGGREGATED INFORMATION //////

    public ArrayList<Integer> getAllTrainings() {
        Cursor res = db.rawQuery("SELECT s.points, r.target, s.passe  " +
                "FROM TRAINING t, ROUND r, PASSE p, SHOOT s " +
                "WHERE t._id=r.training AND r._id=p.round " +
                "AND p._id=s.passe " +
                "ORDER BY t.datum, t._id, r._id, p._id, s._id", null);
        res.moveToFirst();

        int oldPasse = -1;
        int actCounter = 0, maxCounter = 0;
        ArrayList<Integer> history = new ArrayList<>();
        for (int i = 0; i < res.getCount(); i++) {
            int zone = res.getInt(0);
            int target = res.getInt(1);
            int passe = res.getInt(2);
            if (oldPasse != -1 && oldPasse != passe) {
                float percent = actCounter * 100.0f / (float) maxCounter;
                history.add((int) percent);
                actCounter = 0;
                maxCounter = 0;
            }
            //actCounter += Target.getPointsByZone(target, zone); //TODO
            //maxCounter += Target.getMaxPoints(target);
            oldPasse = passe;
            res.moveToNext();
        }
        res.close();
        return history;
    }

    public ArrayList<Integer> getAllRounds(long training) {
        Cursor res = db.rawQuery("SELECT s.points, r.target, s.passe  " +
                "FROM ROUND r, PASSE p, SHOOT s " +
                "WHERE " + training + "=r.training AND r._id=p.round " +
                "AND p._id=s.passe " +
                "ORDER BY r._id, p._id, s._id", null);
        res.moveToFirst();

        int oldPasse = -1;
        int actCounter = 0, maxCounter = 0;
        ArrayList<Integer> history = new ArrayList<>();
        for (int i = 0; i < res.getCount(); i++) {
            int zone = res.getInt(0);
            int target = res.getInt(1);
            int passe = res.getInt(2);
            if (oldPasse != -1 && oldPasse != passe) {
                float percent = actCounter * 100.0f / (float) maxCounter;
                history.add((int) percent);
                actCounter = 0;
                maxCounter = 0;
            }
            //actCounter += Target.getPointsByZone(target, zone); //TODO
            //maxCounter += Target.getMaxPoints(target);
            oldPasse = passe;
            res.moveToNext();
        }
        res.close();
        return history;
    }

    /*public int getRoundPoints(long round) {
        Cursor res = db.rawQuery("SELECT s.points, r.target, r.scoring_style" +
                " FROM ROUND r, PASSE p, SHOOT s" +
                " WHERE r._id=p.round AND p.round=" + round + " AND s.passe=p._id", null);
        res.moveToFirst();
        int sum = 0;
        for (int i = 0; i < res.getCount(); i++) {
            int zone = res.getInt(0);
            Target target = TargetFactory.createTarget(mContext, res.getInt(1), res.getInt(2));
            //sum += target.getPointsByZone(zone, arrow);
            res.moveToNext();
        }
        res.close();
        return sum;
    }*/

    public String getSetting(long bowId, Distance dist) {
        String[] cols = {VISIER_SETTING};
        Cursor res = db
                .query(TABLE_VISIER, cols,
                        VISIER_BOW + "=" + bowId + " AND " + VISIER_DISTANCE + "=" + dist.value +
                                " AND " + VISIER_UNIT + "='" + dist.unit + "'", null,
                        null, null, null);
        String s = "";
        if (res.moveToFirst()) {
            s = res.getString(0);
        }
        res.close();
        return s;
    }

////// CREATE AND UPDATE INFORMATION //////

    public void update(DatabaseSerializable item) {
        ContentValues values = item.getContentValues();
        if (values == null) {
            return;
        }
        if (item.getId() <= 0) {
            item.setId(db.insert(item.getTableName(), null, values));
        } else {
            values.put(ID, item.getId());
            db.replace(item.getTableName(), null, values);
        }
        if (item instanceof Passe) {
            Passe passe = (Passe) item;
            for (Shot shot : passe.shot) {
                update(shot);
            }
        } else if (item instanceof StandardRound) {
            StandardRound sr = (StandardRound) item;
            for (RoundTemplate template : sr.getRounds()) {
                update(template);
            }
        } else if(item instanceof Arrow) {
            updateArrowNumbers(item.getId(), ((Arrow) item).numbers);
        }
    }

    public void updateSightSettings(long bowId, List<EditBowActivity.SightSetting> sightSettingsList) {
        db.delete(TABLE_VISIER, VISIER_BOW + "=" + bowId, null);
        for (EditBowActivity.SightSetting set : sightSettingsList) {
            ContentValues values = new ContentValues();
            values.put(VISIER_BOW, bowId);
            values.put(VISIER_DISTANCE, set.distance.value);
            values.put(VISIER_UNIT, set.distance.unit);
            values.put(VISIER_SETTING, set.value);
            db.insert(TABLE_VISIER, null, values);
        }
    }

    public void updateArrowNumbers(long arrowId, List<Integer> arrowNumbersList) {
        db.delete(TABLE_NUMBER, NUMBER_ARROW + "=" + arrowId, null);
        for (Integer number : arrowNumbersList) {
            ContentValues values = new ContentValues();
            values.put(NUMBER_ARROW, arrowId);
            values.put(NUMBER_VALUE, number);
            db.insert(TABLE_NUMBER, null, values);
        }
    }

    public ArrayList<EditBowActivity.SightSetting> getSightSettings(long bowId) {
        String[] cols = {VISIER_DISTANCE, VISIER_UNIT, VISIER_SETTING};
        Cursor res = db.query(TABLE_VISIER, cols, VISIER_BOW + "=" + bowId, null, null, null,
                VISIER_DISTANCE + " ASC");
        ArrayList<EditBowActivity.SightSetting> list = new ArrayList<>();
        if (res.moveToFirst()) {
            do {
                EditBowActivity.SightSetting set = new EditBowActivity.SightSetting();
                set.distance = new Distance(res.getInt(0), res.getString(1));
                set.value = res.getString(2);
                list.add(set);
            } while (res.moveToNext());
        }
        res.close();
        return list;
    }

    public ArrayList<Integer> getArrowNumbers(long arrowId) {
        String[] cols = {NUMBER_VALUE};
        Cursor res = db.query(TABLE_NUMBER, cols, NUMBER_ARROW + "=" + arrowId, null, null, null,
                NUMBER_VALUE + " ASC");
        ArrayList<Integer> list = new ArrayList<>();
        if (res.moveToFirst()) {
            do {
                list.add(res.getInt(0));
            } while (res.moveToNext());
        }
        res.close();
        return list;
    }

////// EXPORT ALL //////

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void exportAll(File file) throws IOException {
        Cursor cur = db.rawQuery(
                "SELECT t.title,datetime(t.datum/1000, 'unixepoch') AS date,t.indoor,r.distance," +
                        "r.target, b.name AS bow, s.points AS score " +
                        "FROM TRAINING t, ROUND r, PASSE p, SHOOT s LEFT JOIN BOW b ON b._id=r.bow " +
                        "WHERE t._id = r.training AND r._id = p.round AND p._id = s.passe", null);
        String[] names = cur.getColumnNames();

        file.getParentFile().mkdirs();
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        for (String name : names) {
            writer.append("\"").append(name).append("\";");
        }
        writer.append("\n");
        int targetInd = cur.getColumnIndexOrThrow("target");
        int scoreInd = cur.getColumnIndexOrThrow("score");
        int indoorInd = cur.getColumnIndexOrThrow("indoor");
        if (cur.moveToFirst()) {
            do {
                for (int i = 0; i < names.length; i++) {
                    writer.append("\"");
                    if (i == targetInd) {
                        writer.append(TargetFactory.getList(mContext).get(cur.getInt(i)).name);
                    } else if (i == indoorInd) {
                        if (cur.getInt(i) == 0) {
                            writer.append("Outdoor");
                        } else {
                            writer.append("Indoor");
                        }
                    } else if (i == scoreInd) {
                        //String x = Target //TODO
                        //        .getStringByZone(cur.getInt(targetInd), cur.getInt(scoreInd));
                        //writer.append(x);
                    } else {
                        writer.append(cur.getString(i));
                    }
                    writer.append("\";");
                }
                writer.append("\n");
            } while (cur.moveToNext());
        }
        writer.flush();
        writer.close();
        cur.close();
    }

////// DELETE ENTRIES //////

    public void delete(DatabaseSerializable item) {
        db.delete(item.getTableName(), ID + "=" + item.getId(), null);
        cleanup(db);
    }

////// BACKUP DATABASE //////

    public static boolean Import(Context context, InputStream in) {
        try {
            // Unzip all images and database
            File file = BackupUtils.unzip(context, in);

            // Replace database file
            File db_file = sInstance.mContext.getDatabasePath(DatabaseManager.DATABASE_NAME);
            DatabaseManager tmp = sInstance;
            sInstance = null;
            tmp.close();
            BackupUtils.copy(file, db_file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String[] getImages() {
        ArrayList<String> list = new ArrayList<>();
        Cursor cur = db.rawQuery("SELECT image FROM BOW WHERE image IS NOT NULL", null);
        if (cur.moveToFirst()) {
            list.add(cur.getString(0));
        }
        cur.close();

        // Migrate all arrow images
        cur = db.rawQuery("SELECT image FROM ARROW WHERE image IS NOT NULL", null);
        if (cur.moveToFirst()) {
            list.add(cur.getString(0));
        }
        cur.close();
        return list.toArray(new String[list.size()]);
    }

    /**
     * Returns a list of all distances that are either default values or used somewhere in the app
     *
     * @param curDistId Distance id to add to the list, because it is the current value.
     * @return List of distances
     */
    public List<Distance> getDistances(long curDistId) {
        ArrayList<Distance> distances = new ArrayList<>();
        HashSet<Long> set = new HashSet<>();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        boolean defValue = Boolean.parseBoolean(mContext.getString(R.string.default_unit));
        boolean isMetric = prefs.getBoolean("pref_unit", defValue);

        // Add currently selected distance to list
        distances.add(Distance.fromId(curDistId));
        set.add(curDistId);

        // Get all distances used in ROUND or VISIER table
        Cursor cur = db.rawQuery(
                "SELECT * FROM (SELECT DISTINCT distance, unit FROM ROUND_TEMPLATE UNION SELECT DISTINCT distance, unit FROM VISIER) WHERE unit=?",
                new String[]{isMetric ? Distance.METER : Distance.YARDS});
        if (cur.moveToFirst()) {
            do {
                Distance d = new Distance(cur.getInt(0), cur.getString(1));
                if (!set.contains(d.getId())) {
                    distances.add(d);
                    set.add(d.getId());
                }
            } while (cur.moveToNext());
        }
        cur.close();
        Collections.sort(distances);
        return distances;
    }

    public static void cleanup(SQLiteDatabase db) {
        // Clean up rounds
        db.execSQL("DELETE FROM ROUND WHERE _id IN (SELECT r._id " +
                "FROM ROUND r LEFT JOIN TRAINING t ON t._id=r.training " +
                "WHERE t._id IS NULL)");

        // Clean up passes
        db.execSQL("DELETE FROM PASSE WHERE _id IN (SELECT p._id " +
                "FROM PASSE p LEFT JOIN ROUND r ON r._id=p.round " +
                "WHERE r._id IS NULL)");

        // Clean up shots
        db.execSQL("DELETE FROM SHOOT WHERE _id IN (SELECT s._id " +
                "FROM SHOOT s LEFT JOIN PASSE p ON p._id=s.passe " +
                "WHERE p._id IS NULL)");

        // Clean up arrow numbers
        db.execSQL("DELETE FROM NUMBER WHERE _id IN (SELECT s._id " +
                "FROM NUMBER s LEFT JOIN ARROW a ON a._id=s.arrow " +
                "WHERE a._id IS NULL)");
    }
}