package de.dreier.mytargets.shared.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import java.io.Serializable;

public class Shot extends IdProvider
        implements Comparable<Shot>, Serializable, DatabaseSerializable {
    static final long serialVersionUID = 57L;
    public static final String TABLE = "SHOOT";
    public static final String PASSE = "passe";
    public static final String ZONE = "points";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String COMMENT = "comment";
    public static final String ARROW = "arrow";
    public static final String INDEX = "arrow_index";
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    PASSE + " INTEGER," +
                    ZONE + " INTEGER," +
                    X + " REAL," +
                    Y + " REAL," +
                    COMMENT + " TEXT," +
                    ARROW + " INTEGER," +
                    INDEX + " INTEGER);";

    public static final int NOTHING_SELECTED = -2;
    public int zone = NOTHING_SELECTED;
    public static final int MISS = -1;
    public long passe;
    public float x, y;
    public String comment = "";
    public int arrow = -1;
    public int index;

    public Shot(int i) {
        index = i;
    }

    @Override
    public int compareTo(@NonNull Shot another) {
        if (another.zone == zone) {
            return 0;
        }
        return ((zone > another.zone && another.zone != MISS) || zone == MISS) ? 1 : -1;
    }

    @Override
    public String getTableName() {
        return TABLE;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(Shot.PASSE, passe);
        values.put(Shot.ZONE, zone);
        values.put(Shot.X, x);
        values.put(Shot.Y, y);
        values.put(Shot.COMMENT, comment);
        values.put(Shot.ARROW, arrow);
        values.put(Shot.INDEX, index);
        return values;
    }

    @Override
    public void fromCursor(Context context, Cursor cursor, int startColumnIndex) {
        setId(cursor.getLong(0));
        passe = cursor.getLong(1);
        zone = cursor.getInt(2);
        x = cursor.getFloat(3);
        y = cursor.getFloat(4);
        comment = cursor.getString(5);
        arrow = cursor.getInt(6);
        index = cursor.getInt(7);
    }
}
