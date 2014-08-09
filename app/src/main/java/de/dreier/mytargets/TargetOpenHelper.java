package de.dreier.mytargets;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import java.util.Date;

public class TargetOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "database";
    private static final int DATABASE_VERSION = 4;

    private static final String TABLE_TRAINING = "TRAINING";
    public static final String TRAINING_ID = "_id";
    public static final String TRAINING_TITLE = "title";
    public static final String TRAINING_DATE = "datum";

    private static final String TABLE_ROUND = "ROUND";
    public static final String RUNDE_ID = "_id";
    public static final String RUNDE_TRAINING = "training";
    public static final String RUNDE_INDOOR = "indoor";
    public static final String RUNDE_DISTANCE = "distance";
    public static final String RUNDE_UNIT = "unit";
    public static final String RUNDE_BOW = "bow";
    public static final String RUNDE_PPP = "ppp";
    public static final String RUNDE_TARGET = "target";

    private static final String TABLE_PASSE = "PASSE";
    public static final String PASSE_ID = "_id";
    public static final String PASSE_ROUND = "round";

    private static final String TABLE_SHOOT = "SHOOT";
    public static final String SHOOT_ID = "_id";
    public static final String SHOOT_PASSE = "passe";
    public static final String SHOOT_ZONE = "points";

    private static final String TABLE_BOW = "BOW";
    public static final String BOW_ID = "_id";
    public static final String BOW_NAME = "name";
    public static final String BOW_MARKE = "marke";
    public static final String BOW_TYPE = "type";
    public static final String BOW_SIZE = "size";
    public static final String BOW_HEIGHT = "hight";
    public static final String BOW_TILLER = "tiller";
    public static final String BOW_DESCRIPTION = "description";
    public static final String BOW_IMAGE = "image";


    private static final String TABLE_VISIER = "VISIER";
    public static final String VISIER_ID = "_id";
    public static final String VISIER_BOW = "bow";
    public static final String VISIER_DISTANCE = "distance";

    private static final String CREATE_TABLE_BOW =
            "CREATE TABLE "+ TABLE_BOW +" ( "+
                    BOW_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    BOW_NAME +" TEXT,"+
                    BOW_MARKE +" TEXT,"+
                    BOW_TYPE +" INTEGER,"+
                    BOW_SIZE +" INTEGER,"+
                    BOW_HEIGHT +" TEXT,"+
                    BOW_TILLER +" TEXT,"+
                    BOW_DESCRIPTION +" TEXT,"+
                    BOW_IMAGE +" BLOB);";

    private static final String CREATE_TABLE_VISIER =
            "CREATE TABLE "+TABLE_VISIER+" ( "+
                    VISIER_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    VISIER_BOW +" REFERENCES "+ TABLE_BOW +" ON DELETE CASCADE,"+
                    VISIER_DISTANCE+" INTEGER);";

    private static final String CREATE_TABLE_TRAINING =
            "CREATE TABLE "+TABLE_TRAINING+" ( "+
                    TRAINING_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    TRAINING_DATE +" INTEGER,"+
                    TRAINING_TITLE+" TEXT);";

    private static final String CREATE_TABLE_ROUND =
            "CREATE TABLE "+ TABLE_ROUND +" (" +
                    RUNDE_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    RUNDE_INDOOR+" BOOLEAN,"+
                    RUNDE_DISTANCE+" INTEGER," +
                    RUNDE_UNIT +" TEXT," +
                    RUNDE_PPP+" INTEGER,"+
                    RUNDE_TARGET +" INTEGER,"+
                    RUNDE_BOW +" REFERENCES "+ TABLE_BOW +" ON DELETE SET NULL,"+
                    RUNDE_TRAINING+" INTEGER REFERENCES "+TABLE_TRAINING+" ON DELETE CASCADE);";

    private static final String CREATE_TABLE_PASSE =
            "CREATE TABLE "+TABLE_PASSE+" (" +
                    PASSE_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    PASSE_ROUND +" INTEGER REFERENCES "+ TABLE_ROUND +" ON DELETE CASCADE);";

    private static final String CREATE_TABLE_SHOOT =
            "CREATE TABLE "+TABLE_SHOOT+" (" +
                    SHOOT_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    SHOOT_PASSE+" INTEGER REFERENCES "+TABLE_PASSE+" ON DELETE CASCADE,"+
                    SHOOT_ZONE +" INTEGER);";

    TargetOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BOW);
        db.execSQL(CREATE_TABLE_VISIER);
        db.execSQL(CREATE_TABLE_TRAINING);
        db.execSQL(CREATE_TABLE_ROUND);
        db.execSQL(CREATE_TABLE_PASSE);
        db.execSQL(CREATE_TABLE_SHOOT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_TRAINING);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_ROUND);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_PASSE);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_SHOOT);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_BOW);
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
        Cursor res = db.query(TABLE_ROUND,null,RUNDE_TRAINING+"=?",args,null,null,RUNDE_ID+" ASC");
        return res;
    }

    public Cursor getPasses(long round) {
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {""+round};
        Cursor res = db.query(TABLE_PASSE,null, PASSE_ROUND +"=?",args,null,null,PASSE_ID+" ASC");
        return res;
    }

    public Cursor getBows() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_BOW,null,null,null,null,null, BOW_ID +" ASC");
    }

    public long addPasseToRound(long round, int[] points) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PASSE_ROUND, round);
        long insertId = db.insert(TABLE_PASSE, null, values);
        for(int i=0;i<points.length;i++) {
            values = new ContentValues();
            values.put(SHOOT_PASSE, insertId);
            values.put(SHOOT_ZONE, points[i]);
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
        long round = db.insert(TABLE_ROUND, null, values);
        db.close();
        return round;
    }

    public int[] getPasse(long round, int passe) {
        SQLiteDatabase db = getReadableDatabase();
        String[] cols1 = {PASSE_ID};
        String[] args1 = {""+round};
        Cursor res1 = db.query(TABLE_PASSE,cols1, PASSE_ROUND +"=?",args1,null,null,PASSE_ID+" ASC");
        if(!res1.moveToPosition(passe-1))
            return null;
        long passeId = res1.getLong(0);
        String[] cols2 = {SHOOT_ZONE};
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
        String[] cols = {SHOOT_ID, SHOOT_ZONE};
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
        String[] cols = {RUNDE_PPP,RUNDE_TRAINING,RUNDE_TARGET,RUNDE_INDOOR,RUNDE_DISTANCE,RUNDE_UNIT,RUNDE_BOW};
        String[] args = {""+round};
        Cursor res = db.query(TABLE_ROUND,cols,RUNDE_ID+"=?",args,null,null,null);
        res.moveToFirst();
        Round r = new Round();
        r.ppp = res.getInt(0);
        r.training = res.getLong(1);
        r.target = res.getInt(2);
        r.indoor = res.getInt(3)!=0;
        r.distance = ""+res.getInt(4)+res.getString(5);
        r.bow = res.getInt(6);
        res.close();
        db.close();
        return r;
    }

    public int getRoundPoints(long round, int tar) {
        SQLiteDatabase db = getReadableDatabase();
        String[] args = {""+round};
        Cursor res = db.rawQuery("SELECT s."+ SHOOT_ZONE +
                " FROM "+TABLE_PASSE+" p, "+TABLE_SHOOT+" s"+
                " WHERE p."+ PASSE_ROUND +"=? AND s."+SHOOT_PASSE+"=p."+PASSE_ID,args);
        res.moveToFirst();
        int[] target = TargetView.target_points[tar];
        int sum = 0;
        for(int i=0;i<res.getCount();i++) {
            sum+=target[res.getInt(0)];
            res.moveToNext();
        }
        res.close();
        db.close();
        return sum;
    }

    public int getRoundInd(long training, long round) {
        SQLiteDatabase db = getReadableDatabase();
        String[] cols = {RUNDE_ID};
        String[] args = {""+training};
        Cursor res = db.query(TABLE_ROUND,cols,RUNDE_TRAINING+"=?",args,null,null,RUNDE_ID+" ASC");
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

    public void deletePasses(long[] ids) {
        SQLiteDatabase db = getReadableDatabase();
        for(long id:ids) {
            String[] args = {""+id};
            db.delete(TABLE_PASSE,PASSE_ID+"=?",args);
        }
        db.close();
    }

    public void deleteRounds(long[] ids) {
        SQLiteDatabase db = getReadableDatabase();
        for(long id:ids) {
            String[] args = {""+id};
            db.delete(TABLE_ROUND,RUNDE_ID+"=?",args);
        }
        db.close();
    }

    public void deleteTrainings(long[] ids) {
        SQLiteDatabase db = getReadableDatabase();
        for(long id:ids) {
            String[] args = {""+id};
            db.delete(TABLE_TRAINING,TRAINING_ID+"=?",args);
        }
        db.close();
    }

    public void deleteBows(long[] ids) {
        SQLiteDatabase db = getReadableDatabase();
        for(long id:ids) {
            String[] args = {""+id};
            db.delete(TABLE_BOW, BOW_ID +"=?",args);
        }
        db.close();
    }

    public void updatePasse(long round, int passe, int[] zones) {
        SQLiteDatabase db = getReadableDatabase();
        String[] cols1 = {PASSE_ID};
        String[] args1 = {""+round};
        Cursor res1 = db.query(TABLE_PASSE,cols1, PASSE_ROUND +"=?",args1,null,null,PASSE_ID+" ASC");
        if(!res1.moveToPosition(passe-1))
            return;
        long passeId = res1.getLong(0);
        String[] cols2 = {SHOOT_ID};
        String[] args2 = {""+passeId};
        Cursor res = db.query(TABLE_SHOOT,cols2,SHOOT_PASSE+"=?",args2,null,null,SHOOT_ID+" ASC");
        int count = res.getCount();
        res.moveToFirst();
        for(int i=0;i<count;i++) {
            String[] args3 = {""+res.getLong(0)};
            ContentValues values = new ContentValues();
            values.put(SHOOT_ZONE,zones[i]);
            db.update(TABLE_SHOOT,values,SHOOT_ID+"=?",args3);
            res.moveToNext();
        }
        res.close();
        db.close();
    }

    public void updateBow(long bowId, String name, int bowType, String marke, String size, String height, String tiller, String desc, Bitmap img) {
        ContentValues values = new ContentValues();
        values.put(BOW_NAME,name);
        values.put(BOW_TYPE,bowType);
        values.put(BOW_MARKE,marke);
        values.put(BOW_SIZE,size);
        values.put(BOW_HEIGHT,height);
        values.put(BOW_TILLER,tiller);
        values.put(BOW_DESCRIPTION,desc);
        //values.put(BOW_IMAGE,img);
        //TODO
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
        public boolean indoor;
        public String distance;
        public int bow;
    }
}