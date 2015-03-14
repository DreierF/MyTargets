package de.dreier.mytargets.managers;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.preference.PreferenceManager;

import java.io.File;
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
import de.dreier.mytargets.models.Passe;
import de.dreier.mytargets.models.Round;
import de.dreier.mytargets.models.Shot;
import de.dreier.mytargets.models.Target;
import de.dreier.mytargets.models.Training;
import de.dreier.mytargets.utils.BackupUtils;
import de.dreier.mytargets.utils.BitmapUtils;

public class DatabaseManager extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 6;

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
    private static final String TABLE_IMAGE = "IMAGE";
    private static final String IMAGE_ID = "_id";
    private static final String IMAGE_DATA = "data";
    private static final String CREATE_TABLE_IMAGE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_IMAGE + " (" +
                    IMAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    IMAGE_DATA + " BLOB);";
    private static final String TABLE_ZONE_MATRIX = "ZONE_MATRIX";
    private static final String CREATE_TABLE_ZONE_MATRIX =
            "CREATE TABLE IF NOT EXISTS " + TABLE_ZONE_MATRIX + " (" +
                    "target INTEGER," +
                    "zone INTEGER," +
                    "points INTEGER," +
                    "PRIMARY KEY (target, zone));";
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
        db.execSQL(CREATE_TABLE_IMAGE);
        db.execSQL(CREATE_TABLE_BOW);
        db.execSQL(CREATE_TABLE_VISIER);
        db.execSQL(CREATE_TABLE_ARROW);
        db.execSQL(CREATE_TABLE_TRAINING);
        db.execSQL(CREATE_TABLE_ROUND);
        db.execSQL(CREATE_TABLE_PASSE);
        db.execSQL(CREATE_TABLE_SHOOT);
        db.execSQL(CREATE_TABLE_ZONE_MATRIX);
        fillZoneMatrix(db);
    }

    private void fillZoneMatrix(SQLiteDatabase db) {
        for (int target = 0; target < Target.target_points.length; target++) {
            int[] zones = Target.target_points[target];
            for (int zone = 0; zone < zones.length; zone++) {
                db.execSQL("REPLACE INTO ZONE_MATRIX(target, zone, points) VALUES (" + target + "," + zone + "," + zones[zone] + ")");
            }
        }
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
        if (oldVersion < 6) {
            db.execSQL(CREATE_TABLE_IMAGE);

            // Migrate all bow images
            Cursor cur = db.rawQuery("SELECT image FROM BOW WHERE image IS NOT NULL", null);
            if (cur.moveToFirst()) {
                String fileName = cur.getString(0);
                Bitmap bmp = BitmapFactory.decodeFile(fileName);
                byte[] imageData = BitmapUtils.getBitmapAsByteArray(bmp);
                ContentValues values = new ContentValues();
                values.put(IMAGE_DATA, imageData);
                long id = db.insert(TABLE_IMAGE, null, values);
                db.execSQL("UPDATE BOW SET image=" + id + " WHERE image=\"" + fileName + "\"");
            }
            cur.close();

            // Migrate all arrow images
            cur = db.rawQuery("SELECT image FROM ARROW WHERE image IS NOT NULL", null);
            if (cur.moveToFirst()) {
                String fileName = cur.getString(0);
                Bitmap bmp = BitmapFactory.decodeFile(fileName);
                byte[] imageData = BitmapUtils.getBitmapAsByteArray(bmp);
                ContentValues values = new ContentValues();
                values.put(IMAGE_DATA, imageData);
                long id = db.insert(TABLE_IMAGE, null, values);
                db.execSQL("UPDATE ARROW SET image=" + id + " WHERE image=\"" + fileName + "\"");
            }
            cur.close();
        }
        onCreate(db);
    }

////// GET COMPLETE TABLE //////

    public ArrayList<Training> getTrainings() {
        Cursor cursor = db.rawQuery("SELECT t._id, t.datum, t.title, " +
                "SUM(m.points), SUM((SELECT MAX(points) FROM ZONE_MATRIX WHERE target=r.target)) " +
                "FROM TRAINING t " +
                "LEFT JOIN ROUND r ON t._id = r.training " +
                "LEFT JOIN PASSE p ON r._id = p.round " +
                "LEFT JOIN SHOOT s ON p._id = s.passe " +
                "LEFT JOIN ZONE_MATRIX m ON s.points = m.zone AND m.target = r.target " +
                "GROUP BY t._id " +
                "ORDER BY t.datum DESC", null);
        ArrayList<Training> list = new ArrayList<>(cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                Training t = new Training();
                t.id = cursor.getLong(0);
                t.date = new Date(cursor.getLong(1));
                t.title = cursor.getString(2);
                t.reachedPoints = cursor.getInt(3);
                t.maxPoints = cursor.getInt(4);
                list.add(t);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public ArrayList<Round> getRounds(long training) {
        Cursor res = db.rawQuery("SELECT r._id, r.ppp, r.target, r.indoor, r.distance, r.unit, r.bow, r.arrow, b.type, r.comment, " +
                "SUM(m.points), SUM((SELECT MAX(points) FROM ZONE_MATRIX WHERE target=r.target)) " +
                "FROM ROUND r " +
                "LEFT JOIN PASSE p ON r._id = p.round " +
                "LEFT JOIN SHOOT s ON p._id = s.passe " +
                "LEFT JOIN ZONE_MATRIX m ON s.points = m.zone AND m.target = r.target " +
                "LEFT JOIN BOW b ON b._id = r.bow " +
                "WHERE r.training=" + training + " " +
                "GROUP BY r._id " +
                "ORDER BY r._id ASC", null);
        ArrayList<Round> list = new ArrayList<>(res.getCount());
        if (res.moveToFirst()) {
            do {
                Round r = new Round();
                r.id = res.getLong(0);
                r.ppp = res.getInt(1);
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
                r.reachedPoints = res.getInt(10);
                r.maxPoints = res.getInt(11);
                list.add(r);
            } while (res.moveToNext());
        }
        res.close();
        return list;
    }

    public ArrayList<Passe> getPasses(long round) {
        String[] args = {"" + round};
        Cursor res = db.rawQuery("SELECT p._id, s._id, s.points, s.x, s.y, s.comment, " +
                "(SELECT COUNT(t._id) FROM SHOOT t WHERE t.passe=p._id) " +
                "FROM PASSE p " +
                "LEFT JOIN SHOOT s ON p._id = s.passe " +
                "WHERE p.round = ? " +
                "ORDER BY p._id ASC, s._id ASC", args);
        ArrayList<Passe> list = new ArrayList<>();
        if (res.moveToFirst()) {
            do {
                int ppp = res.getInt(6);
                Passe passe = new Passe(ppp);
                passe.id = res.getLong(0);
                for (int i = 0; i < ppp; i++) {
                    passe.shot[i].id = res.getLong(1);
                    passe.shot[i].zone = res.getInt(2);
                    passe.shot[i].x = res.getFloat(3);
                    passe.shot[i].y = res.getFloat(4);
                    passe.shot[i].comment = res.getString(5);
                    res.moveToNext();
                }
                list.add(passe);
            } while (!res.isAfterLast());
        }
        res.close();
        return list;
    }

    public ArrayList<Bow> getBows() {
        Cursor res = db.rawQuery("SELECT _id, name, type, brand, size, height, tiller, description, thumbnail " +
                "FROM BOW " +
                "ORDER BY _id ASC", null);
        ArrayList<Bow> list = new ArrayList<>(res.getCount());
        if (res.moveToFirst()) {
            do {
                Bow bow = new Bow();
                bow.id = res.getLong(0);
                bow.name = res.getString(1);
                bow.type = res.getInt(2);
                bow.brand = res.getString(3);
                bow.size = res.getString(4);
                bow.height = res.getString(5);
                bow.tiller = res.getString(6);
                bow.description = res.getString(7);
                byte[] data = res.getBlob(8);
                bow.image = BitmapFactory.decodeByteArray(data, 0, data.length);
                list.add(bow);
            } while (res.moveToNext());
        }
        res.close();
        return list;
    }

    public ArrayList<Arrow> getArrows() {
        Cursor res = db.rawQuery("SELECT _id, name, length, material, spine, weight, vanes, nock, comment, thumbnail " +
                "FROM ARROW " +
                "ORDER BY _id ASC", null);
        ArrayList<Arrow> list = new ArrayList<>(res.getCount());
        if (res.moveToFirst()) {
            do {
                Arrow arrow = new Arrow();
                arrow.id = res.getLong(0);
                arrow.name = res.getString(1);
                arrow.length = res.getString(2);
                arrow.material = res.getString(3);
                arrow.spine = res.getString(4);
                arrow.weight = res.getString(5);
                arrow.vanes = res.getString(6);
                arrow.nock = res.getString(7);
                arrow.comment = res.getString(8);
                byte[] data = res.getBlob(9);
                arrow.image = BitmapFactory.decodeByteArray(data, 0, data.length);
                list.add(arrow);
            } while (res.moveToNext());
        }
        res.close();
        return list;
    }

////// INSERT //////

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

    public Passe getPasse(long passeId) {
        String[] cols = {SHOOT_ID, SHOOT_ZONE, SHOOT_X, SHOOT_Y, SHOOT_COMMENT};
        String[] args = {"" + passeId};
        Cursor res = db.query(TABLE_SHOOT, cols, SHOOT_PASSE + "=?", args, null, null, SHOOT_ID + " ASC");
        int count = res.getCount();

        Passe p = new Passe(count);
        p.id = passeId;
        res.moveToFirst();
        for (int i = 0; i < count; i++) {
            p.shot[i].id = res.getLong(0);
            p.shot[i].zone = res.getInt(1);
            p.shot[i].x = res.getFloat(2);
            p.shot[i].y = res.getFloat(3);
            p.shot[i].comment = res.getString(4);
            res.moveToNext();
        }
        res.close();
        return p;
    }

    public Passe getPasse(long round, int passe) {
        String[] cols = {PASSE_ID};
        String[] args = {"" + round};
        Cursor res = db.query(TABLE_PASSE, cols, PASSE_ROUND + "=?", args, null, null, PASSE_ID + " ASC");
        if (!res.moveToPosition(passe - 1))
            return null;
        long passeId = res.getLong(0);
        res.close();
        return getPasse(passeId);
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
                bow.image = loadImage(res.getLong(8));
                res.close();
            }
        }
        return bow;
    }

    private Bitmap loadImage(long imageId) {
        Cursor res = db.rawQuery("SELECT data FROM IMAGE WHERE _id=" + imageId, null);
        Bitmap image = null;
        if (res.moveToFirst()) {
            byte[] data = res.getBlob(0);
            image = BitmapFactory.decodeByteArray(data, 0, data.length);
        }
        res.close();
        return image;
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
                arrow.image = loadImage(res.getLong(9));
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

    public int getPasseInd(long round, long passe) {
        String[] cols = {PASSE_ID};
        String[] args = {"" + round};
        Cursor res = db.query(TABLE_PASSE, cols, PASSE_ROUND + "=?", args, null, null, PASSE_ID + " ASC");
        res.moveToFirst();
        for (int i = 1; i < res.getCount() + 1; i++) {
            if (passe == res.getLong(0)) {
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

    public void updatePasse(long round, Passe passe) {
        if (passe.id == 0) {
            ContentValues values = new ContentValues();
            values.put(PASSE_ROUND, round);
            passe.id = db.insert(TABLE_PASSE, null, values);
        }
        for (Shot shot : passe.shot) {
            ContentValues values = new ContentValues();
            values.put(SHOOT_PASSE, passe.id);
            values.put(SHOOT_ZONE, shot.zone);
            values.put(SHOOT_X, shot.x);
            values.put(SHOOT_Y, shot.y);
            values.put(SHOOT_COMMENT, shot.comment);
            if (shot.id == 0) {
                shot.id = db.insert(TABLE_SHOOT, null, values);
            } else {
                String[] args3 = {"" + shot.id};
                db.update(TABLE_SHOOT, values, SHOOT_ID + "=?", args3);
            }
        }
    }

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

    public long updateBow(Bow bow) {
        ContentValues values = new ContentValues();
        values.put(BOW_NAME, bow.name);
        values.put(BOW_TYPE, bow.type);
        values.put(BOW_BRAND, bow.brand);
        values.put(BOW_SIZE, bow.size);
        values.put(BOW_HEIGHT, bow.height);
        values.put(BOW_TILLER, bow.tiller);
        values.put(BOW_DESCRIPTION, bow.description);
        Bitmap thumb = ThumbnailUtils.extractThumbnail(bow.image, 100, 100);
        byte[] imageData = BitmapUtils.getBitmapAsByteArray(thumb);
        values.put(BOW_THUMBNAIL, imageData);
        values.put(BOW_IMAGE, saveImage(bow.image));
        if (bow.id == -1) {
            bow.id = db.insert(TABLE_BOW, null, values);
        } else {
            String[] args = {"" + bow.id};
            db.update(TABLE_BOW, values, BOW_ID + "=?", args);
        }
        return bow.id;
    }

    private long saveImage(Bitmap image) {
        byte[] imageData = BitmapUtils.getBitmapAsByteArray(image);
        ContentValues values = new ContentValues();
        values.put(IMAGE_DATA, imageData);
        return db.insert(TABLE_IMAGE, null, values);
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
        Bitmap thumb = ThumbnailUtils.extractThumbnail(arrow.image, 100, 100);
        byte[] imageData = BitmapUtils.getBitmapAsByteArray(thumb);
        values.put(ARROW_THUMBNAIL, imageData);
        values.put(ARROW_IMAGE, saveImage(arrow.image));
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

    public void deleteTraining(long ids) {
        deleteEntry(TABLE_TRAINING, ids);
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
            deleteEntry(table, id);
        }
    }

    public void deleteEntry(String table, long id) {
        String[] args = {"" + id};
        db.delete(table, "_id=?", args);
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