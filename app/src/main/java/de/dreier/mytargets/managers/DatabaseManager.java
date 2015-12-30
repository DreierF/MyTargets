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
import android.preference.PreferenceManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;

import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.dao.ArrowDataSource;
import de.dreier.mytargets.managers.dao.ArrowNumberDataSource;
import de.dreier.mytargets.managers.dao.BowDataSource;
import de.dreier.mytargets.managers.dao.PasseDataSource;
import de.dreier.mytargets.managers.dao.RoundDataSource;
import de.dreier.mytargets.managers.dao.RoundTemplateDataSource;
import de.dreier.mytargets.managers.dao.ShotDataSource;
import de.dreier.mytargets.managers.dao.SightSettingDataSource;
import de.dreier.mytargets.managers.dao.StandardRoundDataSource;
import de.dreier.mytargets.managers.dao.TrainingDataSource;
import de.dreier.mytargets.shared.models.Diameter;
import de.dreier.mytargets.shared.models.Distance;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.models.target.Target;
import de.dreier.mytargets.shared.models.target.TargetFactory;
import de.dreier.mytargets.utils.BackupUtils;

public class DatabaseManager extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "database";
    private static final int DATABASE_VERSION = 11;
    private static DatabaseManager sInstance;
    private final Context mContext;

    private DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public static DatabaseManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseManager(context.getApplicationContext());
        }
        return sInstance;
    }

    public static boolean Import(Context context, InputStream in) {
        try {
            // Unzip all images and database
            File file = BackupUtils.unzip(context, in);

            // Replace database file
            File db_file = context.getDatabasePath(DatabaseManager.DATABASE_NAME);
            DatabaseManager tmp = sInstance;
            sInstance = null;
            if (tmp != null) {
                tmp.close();
            }
            BackupUtils.copy(file, db_file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
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

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(BowDataSource.CREATE_TABLE_BOW);
        db.execSQL(SightSettingDataSource.CREATE_TABLE_VISIER);
        db.execSQL(ArrowNumberDataSource.CREATE_TABLE_NUMBER);
        db.execSQL(ArrowDataSource.CREATE_TABLE);
        db.execSQL(TrainingDataSource.CREATE_TABLE);
        db.execSQL(RoundTemplateDataSource.CREATE_TABLE);
        db.execSQL(RoundDataSource.CREATE_TABLE);
        db.execSQL(PasseDataSource.CREATE_TABLE);
        db.execSQL(ShotDataSource.CREATE_TABLE);
        fillStandardRound(db);
    }

    private void fillStandardRound(SQLiteDatabase db) {
        db.execSQL(StandardRoundDataSource.CREATE_TABLE);
        db.execSQL(RoundTemplateDataSource.CREATE_TABLE);
        ArrayList<StandardRound> rounds = StandardRound.initTable(mContext);
        StandardRoundDataSource standardRoundDataSource = new StandardRoundDataSource(mContext, this, db);
        for (StandardRound round : rounds) {
            standardRoundDataSource.update(round);
        }
    }

////// SCOREBOARD //////

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS BOW");
            db.execSQL("DROP TABLE IF EXISTS BOW_IMAGE");
            db.execSQL("DROP TABLE IF EXISTS VISIER");
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
            db.execSQL(ArrowDataSource.CREATE_TABLE);
            db.execSQL("ALTER TABLE ROUND ADD COLUMN arrow INTEGER REFERENCES " +
                    ArrowDataSource.TABLE +
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
            db.execSQL("UPDATE ROUND SET target=11 WHERE target=6"); // DFBV Spiegel Spot
            db.execSQL("UPDATE ROUND SET target=10 WHERE target=5"); // DFBV Spiegel
            db.execSQL("UPDATE ROUND SET target=13 WHERE target=4"); // WA Field
            db.execSQL("UPDATE ROUND SET target=4 WHERE target=3"); // WA 3 Spot -> vegas

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
            db.execSQL("UPDATE SHOOT SET x = x/2.0, y = y/2.0 " +
                    "WHERE _id IN (SELECT s._id " +
                    "FROM ROUND r " +
                    "LEFT JOIN PASSE p ON r._id = p.round " +
                    "LEFT JOIN SHOOT s ON p._id = s.passe " +
                    "WHERE r.target<11 AND s.points=0);");
            db.execSQL("ALTER TABLE ROUND RENAME TO ROUND_OLD");

            fillStandardRound(db);

            db.execSQL(RoundDataSource.CREATE_TABLE);
            RoundTemplateDataSource roundTemplateDataSource = new RoundTemplateDataSource(mContext, this, db);

            Cursor trainings = db.rawQuery("SELECT _id FROM TRAINING", null);
            if (trainings.moveToFirst()) {
                do {
                    long training = trainings.getLong(0);
                    long sid = getOrCreateStandardRound(db, training);
                    if (sid == 0) {
                        db.execSQL("DELETE FROM TRAINING WHERE _id=?",
                                new String[]{"" + training});
                    } else {
                        db.execSQL("UPDATE TRAINING SET standard_round=? WHERE _id=?",
                                new String[]{"" + sid, "" + training});
                    }

                    Cursor res = db.rawQuery(
                            "SELECT r._id, r.comment, r.target, r.bow, r.arrow " +
                                    "FROM ROUND_OLD r " +
                                    "WHERE r.training=" + training + " " +
                                    "GROUP BY r._id " +
                                    "ORDER BY r._id ASC", null);
                    int index = 0;
                    if (res.moveToFirst()) {
                        long bow = res.getLong(3);
                        long arrow = res.getLong(4);
                        db.execSQL("UPDATE TRAINING SET bow=?, arrow=? WHERE _id=?",
                                new String[]{"" + bow, "" + arrow, "" + training});
                        do {
                            RoundTemplate info = roundTemplateDataSource.get(sid, index);
                            int target = res.getInt(2);
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(RoundDataSource.ID, res.getLong(0));
                            contentValues.put(RoundDataSource.COMMENT, res.getString(1));
                            contentValues.put(RoundDataSource.TRAINING, training);
                            contentValues.put(RoundDataSource.TEMPLATE, info.getId());
                            contentValues
                                    .put(RoundTemplateDataSource.TARGET, target == 4 ? 5 : target);
                            contentValues.put(RoundTemplateDataSource.SCORING_STYLE,
                                    target == 5 ? 1 : 0);
                            db.insert(RoundDataSource.TABLE, null, contentValues);
                            index++;
                        } while (res.moveToNext());
                    }
                    res.close();
                } while (trainings.moveToNext());
            }
            trainings.close();
        }
        if (oldVersion < 10) {
            // Add new properties to bow
            db.execSQL("ALTER TABLE BOW ADD COLUMN limbs TEXT DEFAULT ''");
            db.execSQL("ALTER TABLE BOW ADD COLUMN sight TEXT DEFAULT ''");
            db.execSQL("ALTER TABLE BOW ADD COLUMN draw_weight TEXT DEFAULT ''");
        }
        if (oldVersion < 11) {
            db.execSQL("ALTER TABLE ARROW ADD COLUMN tip_weight TEXT DEFAULT ''");
            db.execSQL("ALTER TABLE PASSE ADD COLUMN exact INTEGER DEFAULT 0");
            db.execSQL("UPDATE PASSE SET exact=1 WHERE _id IN (SELECT DISTINCT p._id " +
                    "FROM PASSE p, SHOOT s " +
                    "WHERE p._id=s.passe " +
                    "AND s.x!=0)");
        }
        onCreate(db);
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
        sr.name = mContext.getString(R.string.practice);
        sr.club = StandardRound.CUSTOM_PRACTICE;
        if (res.moveToFirst()) {
            sr.indoor = res.getInt(5) == 1;
            do {
                RoundTemplate template = new RoundTemplate();
                template.arrowsPerPasse = res.getInt(0);
                int target = res.getInt(1);
                template.target = TargetFactory.createTarget(mContext,
                        target == 4 ? 5 : target, target == 5 ? 1 : 0);
                template.distance = new Distance(res.getInt(2), res.getString(3));
                template.passes = res.getInt(4);
                template.target.size = template.target.getDiameters()[0];
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
        new StandardRoundDataSource(mContext, this, db).update(sr);
        return sr.getId();
    }


////// GET AGGREGATED INFORMATION //////


////// EXPORT ALL //////

////// BACKUP DATABASE //////

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void exportAll(File file) throws IOException {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cur = db.rawQuery(
                "SELECT t.title,sr.name AS standard_round,datetime(t.datum/1000, 'unixepoch') AS date,sr.indoor,i.distance, i.unit," +
                        "r.target, r.scoring_style, i.size, i.target_unit, s.arrow_index, a.name, s.arrow, b.name AS bow, s.points AS score " +
                        "FROM TRAINING t, ROUND r, PASSE p, SHOOT s " +
                        "LEFT JOIN BOW b ON b._id=t.bow " +
                        "LEFT JOIN ARROW a ON a._id=t.arrow " +
                        "LEFT JOIN ROUND_TEMPLATE i ON r.template=i._id " +
                        "LEFT JOIN STANDARD_ROUND_TEMPLATE sr ON t.standard_round=sr._id " +
                        "WHERE t._id = r.training AND r._id = p.round AND p._id = s.passe", null);
        file.getParentFile().mkdirs();
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        writer.append("\"")
                .append(mContext.getString(R.string.title))
                .append("\";\"")
                .append(mContext.getString(R.string.standard_round)).append("\";\"")
                .append(mContext.getString(R.string.date)).append("\";\"")
                .append(mContext.getString(R.string.indoor)).append("\";\"")
                .append(mContext.getString(R.string.distance)).append("\";\"")
                .append(mContext.getString(R.string.target)).append("\";\"")
                .append(mContext.getString(R.string.points)).append("\";\"")
                .append(mContext.getString(R.string.bow)).append("\";\"")
                .append(mContext.getString(R.string.arrow)).append("\"\n");
        int titleInd = cur.getColumnIndexOrThrow("title");
        int standardRound = cur.getColumnIndexOrThrow("standard_round");
        int dateInd = cur.getColumnIndexOrThrow("date");
        int indoorInd = cur.getColumnIndexOrThrow("indoor");
        int distanceInd = cur.getColumnIndexOrThrow("distance");
        int distanceUnitInd = cur.getColumnIndexOrThrow("unit");
        int targetInd = cur.getColumnIndexOrThrow("target");
        int styleInd = cur.getColumnIndexOrThrow("scoring_style");
        int targetSizeInd = cur.getColumnIndexOrThrow("size");
        int bowInd = cur.getColumnIndexOrThrow("bow");
        int targetUnitInd = cur.getColumnIndexOrThrow("target_unit");
        int arrowInd = cur.getColumnIndexOrThrow("name");
        int arrowNumberInd = cur.getColumnIndexOrThrow("arrow");
        int shotInd = cur.getColumnIndexOrThrow("arrow_index");
        int scoreInd = cur.getColumnIndexOrThrow("score");
        if (cur.moveToFirst()) {
            do {
                // Title
                writer.append("\"");
                writer.append(cur.getString(titleInd));
                writer.append("\";\"");

                // StandardRound
                writer.append(cur.getString(standardRound));
                writer.append("\";\"");

                // Date
                writer.append(cur.getString(dateInd));
                writer.append("\";\"");

                // Indoor
                if (cur.getInt(indoorInd) == 0) {
                    writer.append("Outdoor\";\"");
                } else {
                    writer.append("Indoor\";\"");
                }

                // Distance
                writer.append(new Distance(
                        cur.getInt(distanceInd),
                        cur.getString(distanceUnitInd))
                        .toString(mContext));
                writer.append("\";\"");

                // Target
                Target target = TargetFactory.createTarget(mContext, cur.getInt(targetInd),
                        cur.getInt(styleInd));
                target.size = new Diameter(cur.getInt(targetSizeInd), cur.getString(targetUnitInd));
                writer.append(target.name)
                        .append(" (")
                        .append(target.size.toString(mContext))
                        .append(")\";\"");

                // Score
                writer.append(target.zoneToString(cur.getInt(scoreInd), cur.getInt(shotInd)));
                writer.append("\";\"");

                // Bow
                if (cur.getString(bowInd) != null) {
                    writer.append(cur.getString(bowInd));
                }
                writer.append("\";\"");

                // Arrow
                if (cur.getString(arrowInd) != null) {
                    writer.append(cur.getString(arrowInd));
                    if (cur.getInt(arrowNumberInd) > -1) {
                        writer.append(" (");
                        writer.append(String.valueOf(cur.getInt(arrowNumberInd)));
                        writer.append(")");
                    }
                }
                writer.append("\"\n");
            } while (cur.moveToNext());
        }
        writer.flush();
        writer.close();
        cur.close();
    }

    public String[] getImages() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
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
}