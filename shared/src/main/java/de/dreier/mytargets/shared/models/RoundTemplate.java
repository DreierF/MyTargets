package de.dreier.mytargets.shared.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.io.Serializable;

import de.dreier.mytargets.shared.models.target.Target;
import de.dreier.mytargets.shared.models.target.TargetFactory;

public class RoundTemplate extends IdProvider implements Serializable, DatabaseSerializable {
    static final long serialVersionUID = 56L;
    public static final String TABLE = "ROUND_TEMPLATE";
    private static final String STANDARD_ID = "sid";
    private static final String INDEX = "r_index";
    public static final String DISTANCE = "distance";
    public static final String UNIT = "unit";
    private static final String PASSES = "passes";
    public static final String ARROWS_PER_PASSE = "arrows";
    public static final String TARGET = "target";
    private static final String TARGET_SIZE = "size";
    private static final String TARGET_SIZE_UNIT = "target_unit";
    public static final String SCORING_STYLE = "scoring_style";
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    STANDARD_ID + " INTEGER," +
                    INDEX + " INTEGER," +
                    DISTANCE + " INTEGER," +
                    UNIT + " TEXT," +
                    PASSES + " INTEGER," +
                    ARROWS_PER_PASSE + " INTEGER," +
                    TARGET + " INTEGER," +
                    TARGET_SIZE + " INTEGER," +
                    TARGET_SIZE_UNIT + " INTEGER," +
                    SCORING_STYLE + " INTEGER," +
                    "UNIQUE(sid, r_index) ON CONFLICT REPLACE);";

    public long standardRound;
    public int index;
    public int arrowsPerPasse;
    public Target target;
    public Distance distance;
    public int passes;
    public Target targetTemplate;

    @Override
    public String getTableName() {
        return TABLE;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(STANDARD_ID, standardRound);
        values.put(INDEX, index);
        values.put(DISTANCE, distance.value);
        values.put(UNIT, distance.unit);
        values.put(PASSES, passes);
        values.put(ARROWS_PER_PASSE, arrowsPerPasse);
        values.put(TARGET, targetTemplate.id);
        values.put(TARGET_SIZE, targetTemplate.size.value);
        values.put(TARGET_SIZE_UNIT, targetTemplate.size.unit);
        values.put(SCORING_STYLE, targetTemplate.scoringStyle);
        return values;
    }

    @Override
    public void fromCursor(Context context, Cursor cursor, int startColumnIndex) {
        setId(cursor.getLong(startColumnIndex));
        index = cursor.getInt(startColumnIndex + 1);
        arrowsPerPasse = cursor.getInt(startColumnIndex + 2);
        targetTemplate = TargetFactory.createTarget(context, cursor.getInt(startColumnIndex + 3),
                cursor.getInt(startColumnIndex + 4));
        target = TargetFactory.createTarget(context, cursor.getInt(startColumnIndex + 5),
                cursor.getInt(startColumnIndex + 6));
        distance = new Distance(cursor.getInt(startColumnIndex + 7),
                cursor.getString(startColumnIndex + 8));
        target.size = new Diameter(
                cursor.getInt(startColumnIndex + 9), cursor.getString(startColumnIndex + 10));
        targetTemplate.size = target.size;
        passes = cursor.getInt(startColumnIndex + 11);
        standardRound = cursor.getLong(startColumnIndex + 12);
    }

    public int getMaxPoints() {
        return target.getMaxPoints() * passes * arrowsPerPasse;
    }
}
