package de.dreier.mytargets.shared.models;

import android.content.ContentValues;
import android.database.Cursor;

import java.io.Serializable;

public class Round extends IdProvider implements Serializable, DatabaseSerializable {
    static final long serialVersionUID = 56L;
    public static final String TABLE = "ROUND";
    public static final String TRAINING = "training";
    public static final String COMMENT = "comment";
    public static final String TEMPLATE = "template";
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TRAINING + " INTEGER," +
                    COMMENT + " TEXT," +
                    TEMPLATE + " INTEGER);";

    public long training;
    public RoundTemplate info;
    public int[] scoreCount = new int[3];
    public String comment;

    @Override
    public String getTableName() {
        return TABLE;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(COMMENT, comment);
        values.put(TRAINING, training);
        values.put(TEMPLATE, info.getId());
        return values;
    }

    @Override
    public void fromCursor(Cursor cursor, int startColumnIndex) {
        setId(cursor.getLong(startColumnIndex));
        comment = cursor.getString(startColumnIndex + 1);
        if (comment == null) {
            comment = "";
        }
        info = new RoundTemplate();
        info.fromCursor(cursor, 2);
    }
}
