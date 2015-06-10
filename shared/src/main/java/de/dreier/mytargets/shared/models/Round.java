package de.dreier.mytargets.shared.models;

import android.content.ContentValues;
import android.database.Cursor;

import java.io.Serializable;

public class Round extends IdProvider implements Serializable, DatabaseSerializable {
    static final long serialVersionUID = 56L;
    public static final String TABLE = "ROUND";
    public static final String INDOOR = "indoor";
    public static final String DISTANCE = "distance";
    public static final String UNIT = "unit";
    public static final String PPP = "ppp";
    public static final String TARGET = "target";
    public static final String ARROW = "arrow";
    public static final String TRAINING = "training";
    public static final String BOW = "bow";
    public static final String COMMENT = "comment";
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    INDOOR + " BOOLEAN," +
                    DISTANCE + " INTEGER," +
                    UNIT + " TEXT," +
                    PPP + " INTEGER," +
                    TARGET + " INTEGER," +
                    BOW + " INTEGER REFERENCES " + Bow.TABLE + " ON DELETE SET NULL," +
                    TRAINING + " INTEGER REFERENCES " + Training.TABLE +
                    " ON DELETE CASCADE," +
                    ARROW + " INTEGER REFERENCES " + Arrow.TABLE + " ON DELETE SET NULL," +
                    COMMENT + " TEXT," +
                    Environment.WEATHER + " INTEGER," +
                    Environment.WIND_SPEED + " INTEGER," +
                    Environment.WIND_DIRECTION + " INTEGER," +
                    Environment.LOCATION + " TEXT);";

    public int ppp;
    public int target;
    public long training;
    public boolean indoor;
    public Distance distance;
    public long bow;
    public int[] scoreCount = new int[3];
    public boolean compound;
    public String comment;
    public long arrow;
    public int reachedPoints;
    public int maxPoints;
    public Environment environment;

    @Override
    public String getTableName() {
        return TABLE;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(DISTANCE, distance.distance);
        values.put(UNIT, distance.unit);
        values.put(INDOOR, indoor);
        values.put(TARGET, target);
        values.put(BOW, bow);
        values.put(ARROW, arrow);
        values.put(COMMENT, comment);
        values.put(PPP, ppp);
        values.put(TRAINING, training);
        values.putAll(environment.getContentValues());
        return values;
    }

    @Override
    public void fromCursor(Cursor cursor) {
        id = cursor.getLong(0);
        ppp = cursor.getInt(1);
        target = cursor.getInt(2);
        indoor = cursor.getInt(3) != 0;
        distance = new Distance(cursor.getInt(4), cursor.getString(5));
        bow = cursor.getInt(6);
        arrow = cursor.getInt(7);
        compound = bow == -2 || cursor.getInt(8) == 1;
        comment = cursor.getString(9);
        if (comment == null) {
            comment = "";
        }
        reachedPoints = cursor.getInt(10);
        maxPoints = cursor.getInt(11);
        if (maxPoints <= 10) {
            maxPoints = 0;
        }
        environment = new Environment();
        environment.fromCursor(cursor);
    }
}
