package de.dreier.mytargets.shared.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.io.Serializable;

import static de.dreier.mytargets.shared.models.RoundTemplate.SCORING_STYLE;
import static de.dreier.mytargets.shared.models.RoundTemplate.TARGET;

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
                    TEMPLATE + " INTEGER," +
                    TARGET + " INTEGER," +
                    SCORING_STYLE + " INTEGER);";

    public long training;
    public RoundTemplate info;
    public String comment;
    public int reachedPoints;

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
        values.put(TARGET, info.target.getId());
        values.put(SCORING_STYLE, info.target.scoringStyle);
        return values;
    }

    @Override
    public void fromCursor(Context context, Cursor cursor, int startColumnIndex) {
        setId(cursor.getLong(startColumnIndex));
        comment = cursor.getString(startColumnIndex + 1);
        if (comment == null) {
            comment = "";
        }
        info = new RoundTemplate();
        info.fromCursor(context, cursor, 2);
    }
}
