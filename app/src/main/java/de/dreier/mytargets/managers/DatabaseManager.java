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
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import de.dreier.mytargets.activities.EditBowActivity;
import de.dreier.mytargets.activities.NewRoundActivity;
import de.dreier.mytargets.adapters.TargetItemAdapter;
import de.dreier.mytargets.models.Arrow;
import de.dreier.mytargets.models.Bow;
import de.dreier.mytargets.models.Round;
import de.dreier.mytargets.models.Shot;
import de.dreier.mytargets.models.Target;
import de.dreier.mytargets.models.Training;
import de.dreier.mytargets.utils.BitmapUtils;

public class DatabaseManager extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "database";
    private static final int DATABASE_VERSION = 5;

    private static final String TABLE_TRAINING = "TRAINING";
    private static final String TRAINING_ID = "_id";
    public static final String TRAINING_TITLE = "title";
    public static final String TRAINING_DATE = "datum";

    private static final String TABLE_ROUND = "ROUND";
    public static final String ROUND_ID = "_id";
    private static final String ROUND_TRAINING = "training";
    public static final String ROUND_INDOOR = "indoor";
    public static final String ROUND_DISTANCE = "distance";
    public static final String ROUND_UNIT = "unit";
    private static final String ROUND_BOW = "bow";
    public static final String ROUND_PPP = "ppp";
    public static final String ROUND_TARGET = "target";
    public static final String ROUND_ARROW = "arrow";
    private static final String ROUND_COMMENT = "comment";

    private static final String TABLE_PASSE = "PASSE";
    public static final String PASSE_ID = "_id";
    private static final String PASSE_ROUND = "round";

    private static final String TABLE_SHOOT = "SHOOT";
    private static final String SHOOT_ID = "_id";
    private static final String SHOOT_PASSE = "passe";
    private static final String SHOOT_ZONE = "points";
    private static final String SHOOT_X = "x";
    private static final String SHOOT_Y = "y";
    private static final String SHOOT_COMMENT = "comment";

    private static final String TABLE_BOW = "BOW";
    private static final String BOW_ID = "_id";
    public static final String BOW_NAME = "name";
    private static final String BOW_BRAND = "brand";
    private static final String BOW_TYPE = "type";
    private static final String BOW_SIZE = "size";
    private static final String BOW_HEIGHT = "height";
    private static final String BOW_TILLER = "tiller";
    private static final String BOW_DESCRIPTION = "description";
    public static final String BOW_THUMBNAIL = "thumbnail";
    private static final String BOW_IMAGE = "image";

    private static final String TABLE_BOW_IMAGE = "BOW_IMAGE";

    private static final String TABLE_VISIER = "VISIER";
    private static final String VISIER_ID = "_id";
    private static final String VISIER_BOW = "bow";
    private static final String VISIER_DISTANCE = "distance";
    private static final String VISIER_SETTING = "setting";

    private static final String TABLE_ARROW = "ARROW";
    private static final String ARROW_ID = "_id";
    public static final String ARROW_NAME = "name";
    private static final String ARROW_LENGTH = "length";
    private static final String ARROW_MATERIAL = "material";
    private static final String ARROW_SPINE = "spine";
    private static final String ARROW_WEIGHT = "weight";
    private static final String ARROW_VANES = "vanes";
    private static final String ARROW_POINT = "point";
    private static final String ARROW_NOCK = "nock";
    private static final String ARROW_COMMENT = "comment";
    public static final String ARROW_THUMBNAIL = "thumbnail";
    private static final String ARROW_IMAGE = "image";

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

    private static final String CREATE_TABLE_VISIER =
            "CREATE TABLE IF NOT EXISTS " + TABLE_VISIER + " ( " +
                    VISIER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    VISIER_BOW + " REFERENCES " + TABLE_BOW + " ON DELETE CASCADE," +
                    VISIER_DISTANCE + " INTEGER," +
                    VISIER_SETTING + " TEXT);";

    private static final String CREATE_TABLE_TRAINING =
            "CREATE TABLE IF NOT EXISTS " + TABLE_TRAINING + " ( " +
                    TRAINING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TRAINING_DATE + " INTEGER," +
                    TRAINING_TITLE + " TEXT);";

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

    private static final String CREATE_TABLE_ARROW =
            "CREATE TABLE IF NOT EXISTS " + TABLE_ARROW + " (" +
                    ARROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ARROW_NAME + " TEXT," +
                    ARROW_LENGTH + " TEXT," +
                    ARROW_MATERIAL + " TEXT," +
                    ARROW_SPINE + " TEXT," +
                    ARROW_WEIGHT + " TEXT," +
                    ARROW_VANES + " TEXT," +
                    ARROW_POINT + " TEXT," +
                    ARROW_NOCK + " TEXT," +
                    ARROW_COMMENT + " TEXT," +
                    ARROW_THUMBNAIL + " BLOB," +
                    ARROW_IMAGE + " TEXT);";

    private static final String CREATE_TABLE_PASSE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_PASSE + " (" +
                    PASSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    PASSE_ROUND + " INTEGER REFERENCES " + TABLE_ROUND + " ON DELETE CASCADE);";

    private static final String CREATE_TABLE_SHOOT =
            "CREATE TABLE IF NOT EXISTS " + TABLE_SHOOT + " (" +
                    SHOOT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    SHOOT_PASSE + " INTEGER REFERENCES " + TABLE_PASSE + " ON DELETE CASCADE," +
                    SHOOT_ZONE + " INTEGER," +
                    SHOOT_X + " REAL," +
                    SHOOT_Y + " REAL," +
                    SHOOT_COMMENT + " TEXT);";
    private final Context mContext;

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

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
                    db.execSQL("UPDATE " + table + " SET distance=" + NewRoundActivity.distanceValues[i] + " WHERE distance=" + i);
                }
            }
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            int defaultDist = NewRoundActivity.distanceValues[prefs.getInt("distance", 0)];
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
        }
        onCreate(db);
    }

////// GET COMPLETE TABLE //////

    public Cursor getTrainings() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_TRAINING, null, null, null, null, null, TRAINING_DATE + " DESC");
    }

    public Cursor getRounds(long training) {
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {"" + training};
        return db.query(TABLE_ROUND, null, ROUND_TRAINING + "=?", args, null, null, ROUND_ID + " ASC");
    }

    public Cursor getPasses(long round) {
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {"" + round};
        return db.query(TABLE_PASSE, null, PASSE_ROUND + "=?", args, null, null, PASSE_ID + " ASC");
    }

    public Cursor getBows() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_BOW, null, null, null, null, null, BOW_ID + " ASC");
    }

    public Cursor getArrows() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_ARROW, null, null, null, null, null, ARROW_ID + " ASC");
    }

////// INSERT //////

    public void addPasseToRound(long round, Shot[] passe) {
        SQLiteDatabase db = getWritableDatabase();
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
        db.close();
    }

    public long newTraining(String title) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TRAINING_DATE, System.currentTimeMillis());
        values.put(TRAINING_TITLE, title);
        long insertId = db.insert(TABLE_TRAINING, null, values);
        db.close();
        return insertId;
    }

    public long newRound(long training, long round, int distance, String unit, boolean indoor, int ppp, int target, long bow, long arrow, String comment) {
        SQLiteDatabase db = getWritableDatabase();
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
        db.close();
        return round;
    }

////// GET SINGLE ENTRY AS OBJECT //////

    public Training getTraining(long training) {
        SQLiteDatabase db = getReadableDatabase();
        String[] cols = {TRAINING_ID, TRAINING_TITLE, TRAINING_DATE};
        String[] args = {"" + training};
        Cursor res = db.query(TABLE_TRAINING, cols, TRAINING_ID + "=?", args, null, null, null);
        res.moveToFirst();

        Training tr = new Training();
        tr.id = res.getLong(0);
        tr.title = res.getString(1);
        tr.date = new Date(res.getLong(2));
        res.close();
        db.close();
        return tr;
    }

    public Round getRound(long round) {
        SQLiteDatabase db = getReadableDatabase();

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
        for (int i = 0; i < NewRoundActivity.distanceValues.length; i++)
            if (NewRoundActivity.distanceValues[i] == r.distanceVal)
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
        Cursor cur = db.rawQuery("SELECT s.points+(CASE WHEN (r.bow=-2 OR b.type=1) " +
                "AND s.points=1 AND r.target=3 THEN 1 ELSE 0 END) AS zone, COUNT(*) " +
                "FROM ROUND r, PASSE p, SHOOT s LEFT JOIN BOW b ON b._id=r.bow " +
                "WHERE r._id=p.round AND p.round=" + round + " AND s.passe=p._id AND " +
                "s.points<3 AND s.points>-1 GROUP BY zone", null);
        if (cur.moveToFirst()) {
            do {
                r.scoreCount[cur.getInt(0)] = cur.getInt(1);
            } while (cur.moveToNext());
        }
        cur.close();
        db.close();
        return r;
    }

    public Shot[] getPasse(long round, int passe) {
        SQLiteDatabase db = getReadableDatabase();
        String[] cols1 = {PASSE_ID};
        String[] args1 = {"" + round};
        Cursor res1 = db.query(TABLE_PASSE, cols1, PASSE_ROUND + "=?", args1, null, null, PASSE_ID + " ASC");
        if (!res1.moveToPosition(passe - 1))
            return null;
        long passeId = res1.getLong(0);
        res1.close();
        String[] cols2 = {SHOOT_ZONE, SHOOT_X, SHOOT_Y, SHOOT_COMMENT};
        String[] args2 = {"" + passeId};
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
        res.close();
        db.close();
        return p;
    }

    public ArrayList<Shot[]> getRoundPasses(long round, int excludePasse) {
        SQLiteDatabase db = getReadableDatabase();
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

            db.close();
            return list;
        }
        res1.close();
        db.close();
        return new ArrayList<>();
    }

    public int[] getPasse(long passe) {
        SQLiteDatabase db = getReadableDatabase();
        String[] cols = {SHOOT_ID, SHOOT_ZONE};
        String[] args = {"" + passe};
        Cursor res = db.query(TABLE_SHOOT, cols, SHOOT_PASSE + "=?", args, null, null, SHOOT_ID + " ASC");
        int count = res.getCount();
        int[] points = new int[count];
        res.moveToFirst();
        for (int i = 0; i < count; i++) {
            points[i] = res.getInt(1);
            res.moveToNext();
        }
        res.close();
        db.close();
        return points;
    }

    public Bow getBow(long bowId, boolean small) {
        SQLiteDatabase db = getReadableDatabase();
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
        db.close();
        return bow;
    }

    public Arrow getArrow(long arrowId, boolean small) {
        SQLiteDatabase db = getReadableDatabase();
        String[] cols = {ARROW_NAME, ARROW_LENGTH, ARROW_MATERIAL, ARROW_SPINE, ARROW_WEIGHT, ARROW_VANES, ARROW_POINT, ARROW_NOCK, ARROW_COMMENT, ARROW_THUMBNAIL, ARROW_IMAGE};
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
            arrow.point = res.getString(6);
            arrow.nock = res.getString(7);
            arrow.comment = res.getString(8);
            if (small) {
                byte[] data = res.getBlob(9);
                arrow.image = BitmapFactory.decodeByteArray(data, 0, data.length);
                res.close();
            } else {
                arrow.imageFile = res.getString(10);
                arrow.image = BitmapFactory.decodeFile(arrow.imageFile);
                res.close();
            }
        }
        db.close();
        return arrow;
    }

////// GET AGGREGATED INFORMATION //////

    public int[] getTrainingPoints(long training) {
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {"" + training};

        Cursor res = db.rawQuery("SELECT s.points, r.target, CASE WHEN r.bow=-2 OR b.type=1 THEN 1 ELSE 0 END compound" +
                " FROM ROUND r, PASSE p, SHOOT s LEFT JOIN BOW b ON b._id=r.bow" +
                " WHERE r._id=p.round AND r.training=? AND s.passe=p._id", args);
        res.moveToFirst();
        int[] sum = new int[2];
        for (int i = 0; i < res.getCount(); i++) {
            int zone = res.getInt(0);
            int target = res.getInt(1);
            boolean compound = res.getInt(2) == 1;
            sum[0] += Target.getPointsByZone(target, zone, compound);
            sum[1] += Target.getMaxPoints(target);
            res.moveToNext();
        }
        res.close();
        db.close();
        return sum;
    }

    public int getRoundPoints(long round) {
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {"" + round};

        Cursor res = db.rawQuery("SELECT s.points, r.target, CASE WHEN r.bow=-2 OR b.type=1 THEN 1 ELSE 0 END compound" +
                " FROM ROUND r, PASSE p, SHOOT s LEFT JOIN BOW b ON b._id=r.bow" +
                " WHERE r._id=p.round AND p.round=? AND s.passe=p._id", args);
        res.moveToFirst();
        int sum = 0;
        for (int i = 0; i < res.getCount(); i++) {
            int zone = res.getInt(0);
            int target = res.getInt(1);
            boolean compound = res.getInt(2) == 1;
            sum += Target.getPointsByZone(target, zone, compound);
            res.moveToNext();
        }
        res.close();
        db.close();
        return sum;
    }

    public int getRoundInd(long training, long round) {
        SQLiteDatabase db = getReadableDatabase();
        String[] cols = {ROUND_ID};
        String[] args = {"" + training};
        Cursor res = db.query(TABLE_ROUND, cols, ROUND_TRAINING + "=?", args, null, null, ROUND_ID + " ASC");
        res.moveToFirst();
        for (int i = 1; i < res.getCount() + 1; i++) {
            if (round == res.getLong(0)) {
                res.close();
                db.close();
                return i;
            }
            res.moveToNext();
        }
        res.close();
        db.close();
        return 0;
    }

    public String getSetting(long bowId, int dist) {
        SQLiteDatabase db = getReadableDatabase();
        String[] cols = {VISIER_SETTING};
        String[] args = {"" + bowId, "" + dist};
        Cursor res = db.query(TABLE_VISIER, cols, VISIER_BOW + "=? AND " + VISIER_DISTANCE + "=?", args, null, null, null);
        String s = "";
        if (res.moveToFirst()) {
            s = res.getString(0);
        }
        res.close();
        db.close();
        return s;
    }

////// UPDATE INFORMATION //////

    public void updatePasse(long round, int passe, Shot[] p) {
        SQLiteDatabase db = getReadableDatabase();
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
        db.close();
    }

    public long updateBow(Bow bow) {
        SQLiteDatabase db = getWritableDatabase();
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
        db.close();
        return bow.id;
    }

    public long updateArrow(Arrow arrow) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ARROW_NAME, arrow.name);
        values.put(ARROW_LENGTH, arrow.length);
        values.put(ARROW_MATERIAL, arrow.material);
        values.put(ARROW_SPINE, arrow.spine);
        values.put(ARROW_WEIGHT, arrow.weight);
        values.put(ARROW_VANES, arrow.vanes);
        values.put(ARROW_POINT, arrow.point);
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
        db.close();
        return arrow.id;
    }

    public void updateSightSettings(long bowId, ArrayList<EditBowActivity.SightSetting> sightSettingsList) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_VISIER, VISIER_BOW + "=" + bowId, null);
        for (EditBowActivity.SightSetting set : sightSettingsList) {
            ContentValues values = new ContentValues();
            values.put(VISIER_BOW, bowId);
            values.put(VISIER_DISTANCE, set.distanceVal);
            values.put(VISIER_SETTING, set.value);
            db.insert(TABLE_VISIER, null, values);
        }
        db.close();
    }

    public ArrayList<EditBowActivity.SightSetting> getSettings(long bowId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] cols = {VISIER_DISTANCE, VISIER_SETTING};
        Cursor res = db.query(TABLE_VISIER, cols, VISIER_BOW + "=" + bowId, null, null, null, null);
        ArrayList<EditBowActivity.SightSetting> list = new ArrayList<>();
        if (res.moveToFirst()) {
            do {
                EditBowActivity.SightSetting set = new EditBowActivity.SightSetting();
                set.distanceVal = res.getInt(0);
                set.distanceInd = -1;
                for (int i = 0; i < NewRoundActivity.distanceValues.length; i++)
                    if (NewRoundActivity.distanceValues[i] == set.distanceVal)
                        set.distanceInd = i;
                set.value = res.getString(1);
                list.add(set);
            } while (res.moveToNext());
        }
        res.close();
        db.close();
        return list;
    }

////// EXPORT ALL //////

    public void exportAll(File file) throws IOException {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT t.title,datetime(t.datum/1000, 'unixepoch') AS date,r.indoor,r.distance," +
                "r.target, b.name AS bow, s.points AS score, CASE WHEN r.bow=-2 OR b.type=1 THEN 1 ELSE 0 END compound " +
                "FROM TRAINING t, ROUND r, PASSE p, SHOOT s LEFT JOIN BOW b ON r.bow = b._id " +
                "WHERE t._id = r.training AND r._id = p.round AND p._id = s.passe", null);
        String[] names = cur.getColumnNames();

        file.getParentFile().mkdirs();
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        for (int i = 0; i < names.length - 1; i++) {
            writer.append("\"").append(names[i]).append("\";");
        }
        writer.append("\n");
        int targetInd = cur.getColumnIndexOrThrow("target");
        int scoreInd = cur.getColumnIndexOrThrow("score");
        int indoorInd = cur.getColumnIndexOrThrow("indoor");
        int compoundInd = cur.getColumnIndexOrThrow("compound");
        if (cur.moveToFirst()) {
            do {
                for (int i = 0; i < names.length - 1; i++) {
                    writer.append("\"");
                    if (i == targetInd) {
                        writer.append(TargetItemAdapter.targets[cur.getInt(i)]);
                    } else if (i == indoorInd) {
                        if (cur.getInt(i) == 0)
                            writer.append("Outdoor");
                        else
                            writer.append("Indoor");
                    } else if (i == scoreInd) {
                        String x = Target.getStringByZone(cur.getInt(targetInd), cur.getInt(scoreInd), cur.getInt(compoundInd) == 1);
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
        db.close();
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
        SQLiteDatabase db = getReadableDatabase();
        for (long id : ids) {
            String[] args = {"" + id};
            db.delete(table, "_id=?", args);
        }
        db.close();
    }

}