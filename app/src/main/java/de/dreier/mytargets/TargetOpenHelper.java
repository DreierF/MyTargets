package de.dreier.mytargets;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

public class TargetOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "database";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_TRAINING = "TRAINING";
    private static final String TRAINING_ID = "_id";
    public static final String TRAINING_TITLE = "title";
    public static final String TRAINING_DATE = "datum";

    private static final String TABLE_RUNDE = "RUNDE";
    private static final String RUNDE_ID = "_id";
    private static final String RUNDE_TRAINING = "training";
    private static final String RUNDE_INDOOR = "indoor";
    private static final String RUNDE_DISTANCE = "distance";
    private static final String RUNDE_UNIT = "unit";
    private static final String RUNDE_BOW = "bow";
    private static final String RUNDE_PPP = "ppp";
    private static final String RUNDE_TARGET = "target";

    private static final String TABLE_PASSE = "PASSE";
    public static final String PASSE_ID = "_id";
    private static final String PASSE_RUNDE = "runde";

    private static final String TABLE_SHOOT = "SHOOT";
    private static final String SHOOT_ID = "_id";
    private static final String SHOOT_PASSE = "passe";
    private static final String SHOOT_POINTS = "points";

    private static final String TABLE_BOGEN = "BOGEN";
    private static final String TABLE_VISIER = "VISIER";

    private static final String CREATE_TABLE_BOGEN =
            "CREATE TABLE "+TABLE_BOGEN+" ( "+
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    "name TEXT, marke TEXT, groesse INTEGER, standhoehe TEXT, tiller TEXT,"+
                    "beschreibung TEXT);";

    private static final String CREATE_TABLE_VISIER =
            "CREATE TABLE "+TABLE_VISIER+" ( "+
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    "bogen REFERENCES "+TABLE_BOGEN+" ON DELETE CASCADE, distance INTEGER);";

    private static final String CREATE_TABLE_TRAINING =
            "CREATE TABLE "+TABLE_TRAINING+" ( "+
                    TRAINING_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    TRAINING_DATE +" INTEGER,"+
                    TRAINING_TITLE+" TEXT);";

    private static final String CREATE_TABLE_RUNDE =
            "CREATE TABLE "+TABLE_RUNDE+" (" +
                    RUNDE_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    RUNDE_INDOOR+" BOOLEAN,"+
                    RUNDE_DISTANCE+" INTEGER," +
                    RUNDE_UNIT +" TEXT," +
                    RUNDE_PPP+" INTEGER,"+
                    RUNDE_TARGET +" INTEGER,"+
                    RUNDE_BOW +" REFERENCES "+TABLE_BOGEN+" ON DELETE SET NULL,"+
                    RUNDE_TRAINING+" INTEGER REFERENCES "+TABLE_TRAINING+" ON DELETE CASCADE);";

    private static final String CREATE_TABLE_PASSE =
            "CREATE TABLE "+TABLE_PASSE+" (" +
                    PASSE_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    PASSE_RUNDE+" INTEGER REFERENCES "+TABLE_RUNDE+" ON DELETE CASCADE);";

    private static final String CREATE_TABLE_SHOOT =
            "CREATE TABLE "+TABLE_SHOOT+" (" +
                    SHOOT_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    SHOOT_PASSE+" INTEGER REFERENCES "+TABLE_PASSE+" ON DELETE CASCADE,"+
                    SHOOT_POINTS+" INTEGER);";

    TargetOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BOGEN);
        db.execSQL(CREATE_TABLE_VISIER);
        db.execSQL(CREATE_TABLE_TRAINING);
        db.execSQL(CREATE_TABLE_RUNDE);
        db.execSQL(CREATE_TABLE_PASSE);
        db.execSQL(CREATE_TABLE_SHOOT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_TRAINING);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_RUNDE);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_PASSE);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_SHOOT);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_BOGEN);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_VISIER);
        onCreate(db);
    }

    public Cursor getTrainings() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor res = db.query(TABLE_TRAINING, null, null, null, null, null, TRAINING_DATE+" ASC");
        return res;
    }

    public Cursor getRunden(long training) {
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {""+training};
        Cursor res = db.query(TABLE_RUNDE,null,RUNDE_TRAINING+"=?",args,null,null,RUNDE_ID+" ASC");
        return res;
    }

    public Cursor getPasses(long round) {
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {""+round};
        Cursor res = db.query(TABLE_PASSE,null,PASSE_RUNDE+"=?",args,null,null,PASSE_ID+" ASC");
        return res;
    }

    public long addPasseToRound(long round, int[] points) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PASSE_RUNDE, round);
        long insertId = db.insert(TABLE_PASSE, null, values);
        for(int i=0;i<points.length;i++) {
            values = new ContentValues();
            values.put(SHOOT_PASSE, insertId);
            values.put(SHOOT_POINTS, points[i]);
            db.insert(TABLE_SHOOT, null, values);
        }
        db.close();
        return insertId;
    }

    public long newTraining() {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TRAINING_DATE, System.currentTimeMillis());
        values.put(TRAINING_TITLE, "Training");
        long insertId = db.insert(TABLE_TRAINING, null, values);
        db.close();
        return insertId;
    }

    public long newRound(long training, int distance, String unit, boolean indoor, int ppp, int target, long bow) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RUNDE_DISTANCE, distance);
        values.put(RUNDE_UNIT, unit);
        values.put(RUNDE_INDOOR, indoor);
        values.put(RUNDE_TARGET, target);
        values.put(RUNDE_BOW, bow);
        values.put(RUNDE_PPP, ppp);
        values.put(RUNDE_TRAINING, training);
        long round = db.insert(TABLE_RUNDE, null, values);
        db.close();
        return round;
    }

    public int[] getPasse(long round, int passe) {
        SQLiteDatabase db = getReadableDatabase();
        String[] cols1 = {PASSE_ID};
        String[] args1 = {""+round};
        Cursor res1 = db.query(TABLE_PASSE,cols1,PASSE_RUNDE+"=?",args1,null,null,PASSE_ID+" ASC");
        if(!res1.moveToPosition(passe-1))
            return null;
        long passeId = res1.getLong(0);
        String[] cols2 = {SHOOT_POINTS};
        String[] args2 = {""+passeId};
        Cursor res = db.query(TABLE_SHOOT,cols2,SHOOT_PASSE+"=?",args2,null,null,SHOOT_ID+" ASC");
        int count = res.getCount();
        int[] points = new int[count];
        res.moveToFirst();
        for(int i=0;i<count;i++) {
            points[i] = res.getInt(0);
            res.moveToNext();
        }
        res.close();
        db.close();
        return points;
    }


    public Training getTraining(long training) {
        SQLiteDatabase db = getReadableDatabase();
        String[] cols = {TRAINING_ID,TRAINING_TITLE,TRAINING_DATE};
        String[] args = {""+training};
        Cursor res = db.query(TABLE_TRAINING,cols,TRAINING_ID+"=?",args,null,null,null);
        res.moveToFirst();

        Training tr = new Training();
        tr.id = res.getLong(0);
        tr.title = res.getString(1);
        tr.date = new Date(res.getLong(2));
        res.close();
        db.close();
        return tr;
    }

    public int[] getPasse(long passe) {
        SQLiteDatabase db = getReadableDatabase();
        String[] cols = {SHOOT_ID,SHOOT_POINTS};
        String[] args = {""+passe};
        Cursor res = db.query(TABLE_SHOOT,cols,SHOOT_PASSE+"=?",args,null,null,SHOOT_ID+" ASC");
        int count = res.getCount();
        int[] points = new int[count];
        res.moveToFirst();
        for(int i=0;i<count;i++) {
            points[i] = res.getInt(1);
            res.moveToNext();
        }
        res.close();
        db.close();
        return points;
    }

    public Round getRound(long round) {
        SQLiteDatabase db = getReadableDatabase();
        String[] cols = {RUNDE_PPP,RUNDE_TRAINING,RUNDE_TARGET};
        String[] args = {""+round};
        Cursor res = db.query(TABLE_RUNDE,cols,RUNDE_ID+"=?",args,null,null,null);
        res.moveToFirst();
        Round r = new Round();
        r.ppp = res.getInt(0);
        r.training = res.getLong(1);
        r.target = res.getInt(2);
        res.close();
        db.close();
        return r;
    }

    public int getRoundInd(long training, long round) {
        SQLiteDatabase db = getReadableDatabase();
        String[] cols = {RUNDE_ID};
        String[] args = {""+training};
        Cursor res = db.query(TABLE_RUNDE,cols,RUNDE_TRAINING+"=?",args,null,null,RUNDE_ID+" ASC");
        res.moveToFirst();
        for(int i=1;i<res.getCount()+1;i++) {
            if(round==res.getLong(0)) {
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

    public class Training {
        long id;
        String title;
        Date date;
    }

    public class Round {
        int ppp;
        int target;
        long training;
    }
}