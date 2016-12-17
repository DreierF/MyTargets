package de.dreier.mytargets.shared;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.db.RoundTemplate;
import de.dreier.mytargets.shared.models.db.StandardRound;
import de.dreier.mytargets.shared.utils.FileUtils;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE;

@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION)
public class AppDatabase {

    public static final String NAME = "database";
    public static final String DATABASE_FILE_NAME = "database.db";
    public static final String DATABASE_IMPORT_FILE_NAME = "database";

    public static final int VERSION = 18;

    @Migration(version = 0, database = AppDatabase.class)
    public static class Migration0 extends BaseMigration {

        @Override
        public void migrate(DatabaseWrapper database) {
            fillStandardRound(database);
        }
    }

    private static void fillStandardRound(DatabaseWrapper db) {
        db.beginTransaction();
        List<StandardRound> rounds = StandardRoundFactory.initTable();
        for (StandardRound round : rounds) {
            round.insert(db);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    @Migration(version = 3, database = AppDatabase.class)
    public static class Migration3 extends BaseMigration {

        @Override
        public void migrate(DatabaseWrapper database) {
            database.execSQL("ALTER TABLE SHOOT ADD COLUMN x REAL");
            database.execSQL("ALTER TABLE SHOOT ADD COLUMN y REAL");
            Cursor cur = database.rawQuery("SELECT s._id, s.points, r.target " +
                    "FROM SHOOT s, PASSE p, ROUND r " +
                    "WHERE s.passe=p._id " +
                    "AND p.round=r._id", null);
            if (cur.moveToFirst()) {
                do {
                    int shoot = cur.getInt(0);
                    database.execSQL("UPDATE SHOOT SET x=0, y=0 WHERE _id=" + shoot);
                } while (cur.moveToNext());
            }
            cur.close();
        }
    }

    @Migration(version = 4, database = AppDatabase.class)
    public static class Migration4 extends BaseMigration {

        @Override
        public void migrate(DatabaseWrapper database) {
            int[] valuesMetric = {10, 15, 18, 20, 25, 30, 40, 50, 60, 70, 90};
            for (String table : new String[]{"ROUND", "VISIER"}) {
                for (int i = 10; i >= 0; i--) {
                    database.execSQL("UPDATE " + table + " SET distance=" +
                            valuesMetric[i] + " WHERE distance=" +
                            i);
                }
            }
            SharedPreferences prefs = SharedApplicationInstance.getSharedPreferences();
            int defaultDist = valuesMetric[prefs.getInt("distance", 0)];
            prefs.edit().putInt("distance", defaultDist).apply();
        }
    }

    @Migration(version = 6, database = AppDatabase.class)
    public static class Migration6 extends BaseMigration {

        @Override
        public void migrate(DatabaseWrapper database) {
            File filesDir = SharedApplicationInstance.getContext().getFilesDir();

            // Migrate all bow images
            Cursor cur = database.rawQuery("SELECT image FROM BOW WHERE image IS NOT NULL", null);
            if (cur.moveToFirst()) {
                String fileName = cur.getString(0);
                try {
                    File file = File.createTempFile("img_", ".png", filesDir);
                    FileUtils.copy(new File(fileName), file);
                    database.execSQL(
                            "UPDATE BOW SET image=\"" + file.getName() + "\" WHERE image=\"" +
                                    fileName + "\"");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            cur.close();

            // Migrate all arrows images
            cur = database.rawQuery("SELECT image FROM ARROW WHERE image IS NOT NULL", null);
            if (cur.moveToFirst()) {
                String fileName = cur.getString(0);
                try {
                    File file = File.createTempFile("img_", ".png", filesDir);
                    FileUtils.copy(new File(fileName), file);
                    database.execSQL(
                            "UPDATE ARROW SET image=\"" + file.getName() + "\" WHERE image=\"" +
                                    fileName + "\"");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            cur.close();
        }
    }

    @Migration(version = 9, database = AppDatabase.class)
    public static class Migration9 extends BaseMigration {

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

            fillStandardRound(database);

            database.execSQL("CREATE TABLE IF NOT EXISTS ROUND (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "training INTEGER," +
                    "comment TEXT," +
                    "template INTEGER," +
                    "target INTEGER," +
                    "scoring_style INTEGER);");

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
                            RoundTemplate info = RoundTemplate.get(sid, index);
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
            StandardRound sr = new StandardRound();
            sr.name = "Practice";
            sr.club = 512;
            if (res.moveToFirst()) {
                //FIXME sr.indoor = res.getInt(5) == 1;
                do {
                    RoundTemplate template = new RoundTemplate();
                    template.shotsPerEnd = res.getInt(0);
                    int target = res.getInt(1);
                    template.setTargetTemplate(new Target(target == 4 ? 5 : target, target == 5 ? 1 : 0));
                    template.distance = new Dimension(res.getInt(2), res.getString(3));
                    template.endCount = res.getInt(4);
                    sr.insert(template);
                    long tid = template.getTargetTemplate().getId();
                    tid = tid < 7 ? 0 : tid;
                    tid = tid == 11 ? 10 : tid;
                    Cursor sids = db.rawQuery("SELECT sid FROM ROUND_TEMPLATE " +
                            "WHERE r_index=" + index + " " +
                            "AND distance=" + template.distance.value + " " +
                            "AND unit=\"" + template.distance.unit + "\" " +
                            "AND arrows=" + template.shotsPerEnd + " " +
                            "AND passes=" + template.endCount + " " +
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
            sr.save();
            return sr.getId();
        }
    }
}