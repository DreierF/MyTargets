package de.dreier.mytargets.managers;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import de.dreier.mytargets.activities.EditBowActivity;
import de.dreier.mytargets.activities.EditRoundActivity;
import de.dreier.mytargets.adapters.TargetItemAdapter;
import de.dreier.mytargets.models.Arrow;
import de.dreier.mytargets.models.Bow;
import de.dreier.mytargets.models.Round;
import de.dreier.mytargets.models.Shot;
import de.dreier.mytargets.models.Target;
import de.dreier.mytargets.models.Training;
import de.dreier.mytargets.utils.BackupUtils;
import de.dreier.mytargets.utils.BitmapUtils;

public class DatabaseManager extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "database";
    public static final String TRAINING_TITLE = "title";
    public static final String TRAINING_DATE = "datum";
    public static final String ROUND_ID = "_id";
    public static final String ROUND_INDOOR = "indoor";
    public static final String ROUND_DISTANCE = "distance";
    public static final String ROUND_UNIT = "unit";
    public static final String ROUND_PPP = "ppp";
    public static final String ROUND_TARGET = "target";
    public static final String ROUND_ARROW = "arrow";
    public static final String PASSE_ID = "_id";
    public static final String BOW_NAME = "name";
    public static final String BOW_THUMBNAIL = "thumbnail";
    public static final String ARROW_NAME = "name";
    public static final String ARROW_THUMBNAIL = "thumbnail";
    private static final int DATABASE_VERSION = 5;
    private static final String TABLE_TRAINING = "TRAINING";
    private static final String TRAINING_ID = "_id";
    private static final String CREATE_TABLE_TRAINING =
            "CREATE TABLE IF NOT EXISTS " + TABLE_TRAINING + " ( " +
                    TRAINING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TRAINING_DATE + " INTEGER," +
                    TRAINING_TITLE + " TEXT);";
    private static final String TABLE_ROUND = "ROUND";
    private static final String ROUND_TRAINING = "training";
    private static final String ROUND_BOW = "bow";
    private static final String ROUND_COMMENT = "comment";
    private static final String TABLE_PASSE = "PASSE";
    private static final String PASSE_ROUND = "round";
    private static final String CREATE_TABLE_PASSE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_PASSE + " (" +
                    PASSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    PASSE_ROUND + " INTEGER REFERENCES " + TABLE_ROUND + " ON DELETE CASCADE);";
    private static final String TABLE_SHOOT = "SHOOT";
    private static final String SHOOT_ID = "_id";
    private static final String SHOOT_PASSE = "passe";
    private static final String SHOOT_ZONE = "points";
    private static final String SHOOT_X = "x";
    private static final String SHOOT_Y = "y";
    private static final String SHOOT_COMMENT = "comment";
    private static final String CREATE_TABLE_SHOOT =
            "CREATE TABLE IF NOT EXISTS " + TABLE_SHOOT + " (" +
                    SHOOT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    SHOOT_PASSE + " INTEGER REFERENCES " + TABLE_PASSE + " ON DELETE CASCADE," +
                    SHOOT_ZONE + " INTEGER," +
                    SHOOT_X + " REAL," +
                    SHOOT_Y + " REAL," +
                    SHOOT_COMMENT + " TEXT);";
    private static final String TABLE_BOW = "BOW";
    private static final String BOW_ID = "_id";
    private static final String BOW_BRAND = "brand";
    private static final String BOW_TYPE = "type";
    private static final String BOW_SIZE = "size";
    private static final String BOW_HEIGHT = "height";
    private static final String BOW_TILLER = "tiller";
    private static final String BOW_DESCRIPTION = "description";
    private static final String BOW_IMAGE = "image";
    private static final String CREATE_TABLE_BOW =
            "CREATE TABLE IF NOT EXISTS " + TABLE_BOW + " ( " +
                    BOW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    BOW_NAME + " TEXT," +
                    BOW_BRAND + " TEXT," +
                    BOW_TYPE + " INTEGER," +
                    BOW_SIZE + " INTEGER," +
                    BOW_HEIGHT + " TEXT," +
                    BOW_TILLER + " TEXT," +
                    BOW_DESCRIPTION + " TEXT," +
                    BOW_THUMBNAIL + " BLOB," +
                    BOW_IMAGE + " TEXT);";
    private static final String TABLE_BOW_IMAGE = "BOW_IMAGE";
    private static final String TABLE_VISIER = "VISIER";
    private static final String VISIER_ID = "_id";
    private static final String VISIER_BOW = "bow";
    private static final String VISIER_DISTANCE = "distance";
    private static final String VISIER_SETTING = "setting";
    private static final String CREATE_TABLE_VISIER =
            "CREATE TABLE IF NOT EXISTS " + TABLE_VISIER + " ( " +
                    VISIER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    VISIER_BOW + " REFERENCES " + TABLE_BOW + " ON DELETE CASCADE," +
                    VISIER_DISTANCE + " INTEGER," +
                    VISIER_SETTING + " TEXT);";
    private static final String TABLE_ARROW = "ARROW";
    private static final String CREATE_TABLE_ROUND =
            "CREATE TABLE IF NOT EXISTS " + TABLE_ROUND + " (" +
                    ROUND_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ROUND_INDOOR + " BOOLEAN," +
                    ROUND_DISTANCE + " INTEGER," +
                    ROUND_UNIT + " TEXT," +
                    ROUND_PPP + " INTEGER," +
                    ROUND_TARGET + " INTEGER," +
                    ROUND_BOW + " INTEGER REFERENCES " + TABLE_BOW + " ON DELETE SET NULL," +
                    ROUND_TRAINING + " INTEGER REFERENCES " + TABLE_TRAINING + " ON DELETE CASCADE," +
                    ROUND_ARROW + " INTEGER REFERENCES " + TABLE_ARROW + " ON DELETE SET NULL," +
                    ROUND_COMMENT + " TEXT);";
    private static final String ARROW_ID = "_id";
    private static final String ARROW_LENGTH = "length";
    private static final String ARROW_MATERIAL = "material";
    private static final String ARROW_SPINE = "spine";
    private static final String ARROW_WEIGHT = "weight";
    private static final String ARROW_VANES = "vanes";
    private static final String ARROW_NOCK = "nock";
    private static final String ARROW_COMMENT = "comment";
    private static final String ARROW_IMAGE = "image";
    private static final String CREATE_TABLE_ARROW =
            "CREATE TABLE IF NOT EXISTS " + TABLE_ARROW + " (" +
                    ARROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ARROW_NAME + " TEXT," +
                    ARROW_LENGTH + " TEXT," +
                    ARROW_MATERIAL + " TEXT," +
                    ARROW_SPINE + " TEXT," +
                    ARROW_WEIGHT + " TEXT," +
                    ARROW_VANES + " TEXT," +
                    ARROW_NOCK + " TEXT," +
                    ARROW_COMMENT + " TEXT," +
                    ARROW_THUMBNAIL + " BLOB," +
                    ARROW_IMAGE + " TEXT);";
    private static DatabaseManager sInstance;
    private final Context mContext;
    private final SQLiteDatabase db;

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

    /*@Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.execSQL("PRAGMA foreign_keys=ON");
    }*/

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BOW);
        db.execSQL(CREATE_TABLE_VISIER);
        db.execSQL(CREATE_TABLE_ARROW);
        db.execSQL(CREATE_TABLE_TRAINING);
        db.execSQL(CREATE_TABLE_ROUND);
        db.execSQL(CREATE_TABLE_PASSE);
        db.execSQL(CREATE_TABLE_SHOOT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOW);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOW_IMAGE);
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
                    int zone = cur.getInt(1);
                    int target = cur.getInt(2);
                    float x = Target.zoneToX(target, zone);
                    db.execSQL("UPDATE SHOOT SET x=" + x + ", y=0 WHERE _id=" + shoot);
                } while (cur.moveToNext());
            }
            cur.close();
        }
        if (oldVersion < 4) {
            for (String table : new String[]{"ROUND", "VISIER"}) {
                for (int i = 10; i >= 0; i--) {
                    db.execSQL("UPDATE " + table + " SET distance=" + EditRoundActivity.distanceValues[i] + " WHERE distance=" + i);
                }
            }
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            int defaultDist = EditRoundActivity.distanceValues[prefs.getInt("distance", 0)];
            prefs.edit().putInt("distance", defaultDist).apply();
        }
        if (oldVersion < 5) {
            db.execSQL(CREATE_TABLE_ARROW);
            db.execSQL("ALTER TABLE ROUND ADD COLUMN arrow INTEGER REFERENCES " + TABLE_ARROW + " ON DELETE SET NULL");
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
        onCreate(db);
    }

////// GET COMPLETE TABLE //////

    public Cursor getTrainings() {
        return db.query(TABLE_TRAINING, null, null, null, null, null, TRAINING_DATE + " DESC");
    }

    public Cursor getRounds(long training) {
        String[] args = {"" + training};
        return db.query(TABLE_ROUND, null, ROUND_TRAINING + "=?", args, null, null, ROUND_ID + " ASC");
    }

    public Cursor getPasses(long round) {
        String[] args = {"" + round};
        return db.query(TABLE_PASSE, null, PASSE_ROUND + "=?", args, null, null, PASSE_ID + " ASC");
    }

    public Cursor getBows() {
        return db.query(TABLE_BOW, null, null, null, null, null, BOW_ID + " ASC");
    }

    public Cursor getArrows() {
        return db.query(TABLE_ARROW, null, null, null, null, null, ARROW_ID + " ASC");
    }

////// INSERT //////

    public void addPasseToRound(long round, Shot[] passe) {
        ContentValues values = new ContentValues();
        values.put(PASSE_ROUND, round);
        long insertId = db.insert(TABLE_PASSE, null, values);
        for (Shot aPasse : passe) {
            values = new ContentValues();
            values.put(SHOOT_PASSE, insertId);
            values.put(SHOOT_ZONE, aPasse.zone);
            values.put(SHOOT_X, aPasse.x);
            values.put(SHOOT_Y, aPasse.y);
            values.put(SHOOT_COMMENT, aPasse.comment);
            db.insert(TABLE_SHOOT, null, values);
        }
    }

    public long newTraining(String title) {
        ContentValues values = new ContentValues();
        values.put(TRAINING_DATE, System.currentTimeMillis());
        values.put(TRAINING_TITLE, title);
        return db.insert(TABLE_TRAINING, null, values);
    }

////// GET SINGLE ENTRY AS OBJECT //////

    public Training getTraining(long training) {
        String[] cols = {TRAINING_ID, TRAINING_TITLE, TRAINING_DATE};
        String[] args = {"" + training};
        Cursor res = db.query(TABLE_TRAINING, cols, TRAINING_ID + "=?", args, null, null, null);
        res.moveToFirst();

        Training tr = new Training();
        tr.id = res.getLong(0);
        tr.title = res.getString(1);
        tr.date = new Date(res.getLong(2));
        res.close();
        return tr;
    }

    public Round getRound(long round) {
        // Get all generic round attributes
        Cursor res = db.rawQuery("SELECT r.ppp, r.training, r.target, r.indoor, r.distance, r.unit, r.bow, r.arrow, b.type, r.comment " +
                "FROM ROUND r LEFT JOIN BOW b ON b._id=r.bow WHERE r._id=" + round, null);
        res.moveToFirst();
        Round r = new Round();
        r.ppp = res.getInt(0);
        r.training = res.getLong(1);
        r.target = res.getInt(2);
        r.indoor = res.getInt(3) != 0;
        r.distanceVal = res.getInt(4);
        r.distanceInd = -1;
        for (int i = 0; i < EditRoundActivity.distanceValues.length; i++)
            if (EditRoundActivity.distanceValues[i] == r.distanceVal)
                r.distanceInd = i;
        r.distance = "" + r.distanceVal + res.getString(5);
        r.bow = res.getInt(6);
        r.arrow = res.getInt(7);
        r.compound = r.bow == -2 || res.getInt(8) == 1;
        r.comment = res.getString(9);
        if (r.comment == null)
            r.comment = "";
        res.close();

        // Get number of X, 10 and 9 score
        Cursor cur = db.rawQuery("SELECT s.points AS zone, COUNT(*) " +
                "FROM ROUND r, PASSE p, SHOOT s " +
                "WHERE r._id=p.round AND p.round=" + round + " AND s.passe=p._id AND " +
                "s.points<3 AND s.points>-1 GROUP BY zone", null);
        if (cur.moveToFirst()) {
            do {
                r.scoreCount[cur.getInt(0)] = cur.getInt(1);
            } while (cur.moveToNext());
        }
        cur.close();
        return r;
    }

    public Shot[] getPasse(long passe) {
        String[] cols = {SHOOT_ZONE, SHOOT_X, SHOOT_Y, SHOOT_COMMENT};
        String[] args = {"" + passe};
        Cursor res = db.query(TABLE_SHOOT, cols, SHOOT_PASSE + "=?", args, null, null, SHOOT_ID + " ASC");
        int count = res.getCount();

        Shot[] p = Shot.newArray(count);
        res.moveToFirst();
        for (int i = 0; i < count; i++) {
            p[i].zone = res.getInt(0);
            p[i].x = res.getFloat(1);
            p[i].y = res.getFloat(2);
            p[i].comment = res.getString(3);
            res.moveToNext();
        }
        res.close();
        return p;
    }

    public Shot[] getPasse(long round, int passe) {
        String[] cols = {PASSE_ID};
        String[] args = {"" + round};
        Cursor res = db.query(TABLE_PASSE, cols, PASSE_ROUND + "=?", args, null, null, PASSE_ID + " ASC");
        if (!res.moveToPosition(passe - 1))
            return null;
        long passeId = res.getLong(0);
        res.close();
        return getPasse(passeId);
    }

    public ArrayList<Shot[]> getRoundPasses(long round, int excludePasse) {
        String[] cols1 = {PASSE_ID};
        String[] cols2 = {SHOOT_ZONE, SHOOT_X, SHOOT_Y, SHOOT_COMMENT};
        String[] args1 = {"" + round};
        Cursor res1 = db.query(TABLE_PASSE, cols1, PASSE_ROUND + "=?", args1, null, null, PASSE_ID + " ASC");
        if (res1.moveToFirst()) {
            ArrayList<Shot[]> list = new ArrayList<>();
            do {
                if (res1.getPosition() != excludePasse - 1) {
                    String[] args2 = {"" + res1.getLong(0)}; // passe id
                    Cursor res = db.query(TABLE_SHOOT, cols2, SHOOT_PASSE + "=?", args2, null, null, SHOOT_ID + " ASC");
                    int count = res.getCount();
                    Shot[] p = Shot.newArray(count);
                    res.moveToFirst();
                    for (int i = 0; i < count; i++) {
                        p[i].zone = res.getInt(0);
                        p[i].x = res.getFloat(1);
                        p[i].y = res.getFloat(2);
                        p[i].comment = res.getString(3);
                        res.moveToNext();
                    }
                    list.add(p);
                    res.close();
                }
            } while (res1.moveToNext());
            return list;
        }
        res1.close();
        return new ArrayList<>();
    }

    public Bow getBow(long bowId, boolean small) {
        String[] cols = {BOW_NAME, BOW_TYPE, BOW_BRAND, BOW_SIZE, BOW_HEIGHT, BOW_TILLER, BOW_DESCRIPTION, BOW_THUMBNAIL, BOW_IMAGE};
        String[] args = {"" + bowId};
        Cursor res = db.query(TABLE_BOW, cols, BOW_ID + "=?", args, null, null, null);
        Bow bow = null;
        if (res.moveToFirst()) {
            bow = new Bow();
            bow.id = bowId;
            bow.name = res.getString(0);
            bow.type = res.getInt(1);
            bow.brand = res.getString(2);
            bow.size = res.getString(3);
            bow.height = res.getString(4);
            bow.tiller = res.getString(5);
            bow.description = res.getString(6);
            if (small) {
                byte[] data = res.getBlob(7);
                bow.image = BitmapFactory.decodeByteArray(data, 0, data.length);
                res.close();
            } else {
                bow.imageFile = res.getString(8);
                bow.image = BitmapFactory.decodeFile(bow.imageFile);
                res.close();
            }
        }
        return bow;
    }

    public Arrow getArrow(long arrowId, boolean small) {
        String[] cols = {ARROW_NAME, ARROW_LENGTH, ARROW_MATERIAL, ARROW_SPINE, ARROW_WEIGHT, ARROW_VANES, ARROW_NOCK, ARROW_COMMENT, ARROW_THUMBNAIL, ARROW_IMAGE};
        String[] args = {"" + arrowId};
        Cursor res = db.query(TABLE_ARROW, cols, ARROW_ID + "=?", args, null, null, null);
        Arrow arrow = null;
        if (res.moveToFirst()) {
            arrow = new Arrow();
            arrow.id = arrowId;
            arrow.name = res.getString(0);
            arrow.length = res.getString(1);
            arrow.material = res.getString(2);
            arrow.spine = res.getString(3);
            arrow.weight = res.getString(4);
            arrow.vanes = res.getString(5);
            arrow.nock = res.getString(6);
            arrow.comment = res.getString(7);
            if (small) {
                byte[] data = res.getBlob(8);
                arrow.image = BitmapFactory.decodeByteArray(data, 0, data.length);
                res.close();
            } else {
                arrow.imageFile = res.getString(9);
                arrow.image = BitmapFactory.decodeFile(arrow.imageFile);
                res.close();
            }
        }
        return arrow;
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
            actCounter += Target.getPointsByZone(target, zone);
            maxCounter += Target.getMaxPoints(target);
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
            actCounter += Target.getPointsByZone(target, zone);
            maxCounter += Target.getMaxPoints(target);
            oldPasse = passe;
            res.moveToNext();
        }
        res.close();
        return history;
    }

    public int[] getTrainingPoints(long training) {
        String[] args = {"" + training};

        Cursor res = db.rawQuery("SELECT s.points, r.target" +
                " FROM ROUND r, PASSE p, SHOOT s" +
                " WHERE r._id=p.round AND r.training=? AND s.passe=p._id", args);
        res.moveToFirst();
        int[] sum = new int[2];
        for (int i = 0; i < res.getCount(); i++) {
            int zone = res.getInt(0);
            int target = res.getInt(1);
            sum[0] += Target.getPointsByZone(target, zone);
            sum[1] += Target.getMaxPoints(target);
            res.moveToNext();
        }
        res.close();
        return sum;
    }

    public int getRoundPoints(long round) {
        String[] args = {"" + round};

        Cursor res = db.rawQuery("SELECT s.points, r.target" +
                " FROM ROUND r, PASSE p, SHOOT s" +
                " WHERE r._id=p.round AND p.round=? AND s.passe=p._id", args);
        res.moveToFirst();
        int sum = 0;
        for (int i = 0; i < res.getCount(); i++) {
            int zone = res.getInt(0);
            int target = res.getInt(1);
            sum += Target.getPointsByZone(target, zone);
            res.moveToNext();
        }
        res.close();
        return sum;
    }

    public int getRoundInd(long training, long round) {
        String[] cols = {ROUND_ID};
        String[] args = {"" + training};
        Cursor res = db.query(TABLE_ROUND, cols, ROUND_TRAINING + "=?", args, null, null, ROUND_ID + " ASC");
        res.moveToFirst();
        for (int i = 1; i < res.getCount() + 1; i++) {
            if (round == res.getLong(0)) {
                res.close();
                return i;
            }
            res.moveToNext();
        }
        res.close();
        return 0;
    }

    public String getSetting(long bowId, int dist) {
        String[] cols = {VISIER_SETTING};
        String[] args = {"" + bowId, "" + dist};
        Cursor res = db.query(TABLE_VISIER, cols, VISIER_BOW + "=? AND " + VISIER_DISTANCE + "=?", args, null, null, null);
        String s = "";
        if (res.moveToFirst()) {
            s = res.getString(0);
        }
        res.close();
        return s;
    }

////// UPDATE INFORMATION //////

    public long updateRound(long training, long round, int distance, String unit, boolean indoor, int ppp, int target, long bow, long arrow, String comment) {
        ContentValues values = new ContentValues();
        values.put(ROUND_DISTANCE, distance);
        values.put(ROUND_UNIT, unit);
        values.put(ROUND_INDOOR, indoor);
        values.put(ROUND_TARGET, target);
        values.put(ROUND_BOW, bow);
        values.put(ROUND_ARROW, arrow);
        values.put(ROUND_COMMENT, comment);
        values.put(ROUND_PPP, ppp);
        values.put(ROUND_TRAINING, training);
        if (round == -1) {
            round = db.insert(TABLE_ROUND, null, values);
        } else {
            values.put(ROUND_ID, round);
            db.replace(TABLE_ROUND, null, values);
        }
        return round;
    }

    public void updatePasse(long round, int passe, Shot[] p) {
        String[] cols1 = {PASSE_ID};
        String[] args1 = {"" + round};
        Cursor res1 = db.query(TABLE_PASSE, cols1, PASSE_ROUND + "=?", args1, null, null, PASSE_ID + " ASC");
        if (!res1.moveToPosition(passe - 1))
            return;
        long passeId = res1.getLong(0);
        res1.close();
        String[] cols2 = {SHOOT_ID};
        String[] args2 = {"" + passeId};
        Cursor res = db.query(TABLE_SHOOT, cols2, SHOOT_PASSE + "=?", args2, null, null, SHOOT_ID + " ASC");
        int count = res.getCount();
        res.moveToFirst();
        for (int i = 0; i < count; i++) {
            String[] args3 = {"" + res.getLong(0)};
            ContentValues values = new ContentValues();
            values.put(SHOOT_ZONE, p[i].zone);
            values.put(SHOOT_X, p[i].x);
            values.put(SHOOT_Y, p[i].y);
            values.put(SHOOT_COMMENT, p[i].comment);
            db.update(TABLE_SHOOT, values, SHOOT_ID + "=?", args3);
            res.moveToNext();
        }
        res.close();
    }

    public long updateBow(Bow bow) {
        ContentValues values = new ContentValues();
        values.put(BOW_NAME, bow.name);
        values.put(BOW_TYPE, bow.type);
        values.put(BOW_BRAND, bow.brand);
        values.put(BOW_SIZE, bow.size);
        values.put(BOW_HEIGHT, bow.height);
        values.put(BOW_TILLER, bow.tiller);
        values.put(BOW_DESCRIPTION, bow.description);
        byte[] imageData = BitmapUtils.getBitmapAsByteArray(bow.image);
        values.put(BOW_THUMBNAIL, imageData);
        values.put(BOW_IMAGE, bow.imageFile);
        if (bow.id == -1) {
            bow.id = db.insert(TABLE_BOW, null, values);
        } else {
            String[] args = {"" + bow.id};
            db.update(TABLE_BOW, values, BOW_ID + "=?", args);
        }
        return bow.id;
    }

    public long updateArrow(Arrow arrow) {
        ContentValues values = new ContentValues();
        values.put(ARROW_NAME, arrow.name);
        values.put(ARROW_LENGTH, arrow.length);
        values.put(ARROW_MATERIAL, arrow.material);
        values.put(ARROW_SPINE, arrow.spine);
        values.put(ARROW_WEIGHT, arrow.weight);
        values.put(ARROW_VANES, arrow.vanes);
        values.put(ARROW_NOCK, arrow.nock);
        values.put(ARROW_COMMENT, arrow.comment);
        byte[] imageData = BitmapUtils.getBitmapAsByteArray(arrow.image);
        values.put(ARROW_THUMBNAIL, imageData);
        values.put(ARROW_IMAGE, arrow.imageFile);
        if (arrow.id == -1) {
            arrow.id = db.insert(TABLE_ARROW, null, values);
        } else {
            String[] args = {"" + arrow.id};
            db.update(TABLE_ARROW, values, ARROW_ID + "=?", args);
        }
        return arrow.id;
    }

    public void updateSightSettings(long bowId, ArrayList<EditBowActivity.SightSetting> sightSettingsList) {
        db.delete(TABLE_VISIER, VISIER_BOW + "=" + bowId, null);
        for (EditBowActivity.SightSetting set : sightSettingsList) {
            ContentValues values = new ContentValues();
            values.put(VISIER_BOW, bowId);
            values.put(VISIER_DISTANCE, set.distanceVal);
            values.put(VISIER_SETTING, set.value);
            db.insert(TABLE_VISIER, null, values);
        }
    }

    public ArrayList<EditBowActivity.SightSetting> getSettings(long bowId) {
        String[] cols = {VISIER_DISTANCE, VISIER_SETTING};
        Cursor res = db.query(TABLE_VISIER, cols, VISIER_BOW + "=" + bowId, null, null, null, null);
        ArrayList<EditBowActivity.SightSetting> list = new ArrayList<>();
        if (res.moveToFirst()) {
            do {
                EditBowActivity.SightSetting set = new EditBowActivity.SightSetting();
                set.distanceVal = res.getInt(0);
                set.distanceInd = -1;
                for (int i = 0; i < EditRoundActivity.distanceValues.length; i++)
                    if (EditRoundActivity.distanceValues[i] == set.distanceVal)
                        set.distanceInd = i;
                set.value = res.getString(1);
                list.add(set);
            } while (res.moveToNext());
        }
        res.close();
        return list;
    }

////// EXPORT ALL //////

    public void exportAll(File file) throws IOException {
        Cursor cur = db.rawQuery("SELECT t.title,datetime(t.datum/1000, 'unixepoch') AS date,r.indoor,r.distance," +
                "r.target, b.name AS bow, s.points AS score " +
                "FROM TRAINING t, ROUND r, PASSE p, SHOOT s LEFT JOIN BOW b ON b._id=r.bow " +
                "WHERE t._id = r.training AND r._id = p.round AND p._id = s.passe", null);
        String[] names = cur.getColumnNames();

        file.getParentFile().mkdirs();
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        for (int i = 0; i < names.length; i++) {
            writer.append("\"").append(names[i]).append("\";");
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
                        writer.append(TargetItemAdapter.targets[cur.getInt(i)]);
                    } else if (i == indoorInd) {
                        if (cur.getInt(i) == 0)
                            writer.append("Outdoor");
                        else
                            writer.append("Indoor");
                    } else if (i == scoreInd) {
                        String x = Target.getStringByZone(cur.getInt(targetInd), cur.getInt(scoreInd));
                        writer.append(x);
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

    public void deleteTrainings(long[] ids) {
        deleteEntries(TABLE_TRAINING, ids);
    }

    public void deleteRounds(long[] ids) {
        deleteEntries(TABLE_ROUND, ids);
    }

    public void deletePasses(long[] ids) {
        deleteEntries(TABLE_PASSE, ids);
    }

    public void deleteBows(long[] ids) {
        deleteEntries(TABLE_BOW, ids);
    }

    public void deleteArrows(long[] ids) {
        deleteEntries(TABLE_ARROW, ids);
    }

    public void deleteEntries(String table, long[] ids) {
        for (long id : ids) {
            String[] args = {"" + id};
            db.delete(table, "_id=?", args);
        }
    }

////// BACKUP DATABASE //////

    public static boolean Import(InputStream st) {
        File tmp;
        try {
            // Copy backup stream to temp file
            tmp = File.createTempFile("import", ".db");
            BackupUtils.copy(st, tmp);

            // Replace database file
            File db_file = sInstance.mContext.getDatabasePath(DatabaseManager.DATABASE_NAME);
            sInstance.close();
            BackupUtils.copy(tmp, db_file);
            sInstance = null;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}