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
    public Distance distance;
    public long bow;
    public int[] scoreCount = new int[3];
    public boolean compound;
    public String comment;
    public long arrow;
    public int reachedPoints;
    public int maxPoints;

    @Override
    public String getTableName() {
        return TABLE;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(DISTANCE, distance.distance);
        values.put(UNIT, distance.unit);
        values.put(TARGET, target);
        values.put(BOW, bow);
        values.put(ARROW, arrow);
        values.put(COMMENT, comment);
        values.put(PPP, ppp);
        values.put(TRAINING, training);
        return values;
    }

    @Override
    public void fromCursor(Cursor cursor) {
        id = cursor.getLong(0);
        ppp = cursor.getInt(1);
        target = cursor.getInt(2);
        distance = new Distance(cursor.getInt(3), cursor.getString(4));
        bow = cursor.getInt(5);
        arrow = cursor.getInt(6);
        compound = bow == -2 || cursor.getInt(7) == 1;
        comment = cursor.getString(8);
        if (comment == null) {
            comment = "";
        }
        reachedPoints = cursor.getInt(9);
        maxPoints = cursor.getInt(10);
        if (maxPoints <= 10) {
            maxPoints = 0;
        }
    }

    private static final int FITA = 0;
    private static final int GNAS = 1;
    private static final boolean CAT_INDOOR = true;
    private static final boolean CAT_OUTDOOR = false;
    private static final String METRIC = "m";
    private static final String IMPERIAL = "yd";

   /* static {
        insert(GNAS, "Long Metric V", CAT_OUTDOOR, METRIC, METRIC, Target.WA_10_1, 6, 30, 122, 6, 20,
                122, 6, 0, 0, 0, 0, 0, 0);
        insert(GNAS, "Short Metric", CAT_OUTDOOR, METRIC, METRIC, Target.WA_10_1, 6, 50, 80, 6, 30, 80,
                6, 0, 0, 0, 0, 0, 0);
        insert(GNAS, "Short Metric I", CAT_OUTDOOR, METRIC, METRIC, Target.WA_10_1, 6, 50, 80, 6, 30,
                80, 6, 0, 0, 0, 0, 0, 0);
        insert(GNAS, "Short Metric II", CAT_OUTDOOR, METRIC, METRIC, Target.WA_10_1, 6, 40, 80, 6, 30,
                80, 6, 0, 0, 0, 0, 0, 0);
        insert(GNAS, "Short Metric III", CAT_OUTDOOR, METRIC, METRIC, Target.WA_10_1, 6, 30, 80, 6, 20,
                80, 6, 0, 0, 0, 0, 0, 0);
        insert(GNAS, "Short Metric IV", CAT_OUTDOOR, METRIC, METRIC, Target.WA_10_1, 6, 20, 80, 6, 10,
                80, 6, 0, 0, 0, 0, 0, 0);
        insert(GNAS, "Short Metric V", CAT_OUTDOOR, METRIC, METRIC, Target.WA_10_1, 6, 15, 80, 6, 10,
                80, 6, 0, 0, 0, 0, 0, 0);
        insert(GNAS, "Half Metric I", CAT_OUTDOOR, METRIC, METRIC, Target.WA_10_1, 6, 70, 122, 3, 60,
                122, 3, 50, 80, 3, 30, 80, 3);
        insert(GNAS, "Half Metric II", CAT_OUTDOOR, METRIC, METRIC, Target.WA_10_1, 6, 60, 122, 3, 50,
                122, 3, 40, 80, 3, 30, 80, 3);
        insert(GNAS, "Half Metric III", CAT_OUTDOOR, METRIC, METRIC, Target.WA_10_1, 6, 50, 122, 3, 40,
                122, 3, 30, 80, 3, 20, 80, 3);
        insert(GNAS, "Half Metric IV", CAT_OUTDOOR, METRIC, METRIC, Target.WA_10_1, 6, 40, 122, 3, 30,
                122, 3, 20, 80, 3, 10, 80, 3);
        insert(GNAS, "Half Metric V", CAT_OUTDOOR, METRIC, METRIC, Target.WA_10_1, 6, 30, 122, 3, 20,
                122, 3, 15, 80, 3, 10, 80, 3);
        insert(GNAS, "York", CAT_OUTDOOR, IMPERIAL, METRIC, Target.GNAS_9_1, 6, 100, 122, 12, 80, 122, 8, 60,
                122, 4, 0, 0, 0);
        insert(GNAS, "Hereford", CAT_OUTDOOR, IMPERIAL, METRIC, Target.GNAS_9_1, 6, 80, 122, 12, 60, 122, 8,
                50, 122, 4, 0, 0, 0);
        insert(GNAS, "Bristol I", CAT_OUTDOOR, IMPERIAL, METRIC, Target.GNAS_9_1, 6, 80, 122, 12, 60, 122, 8,
                50, 122, 4, 0, 0, 0);
        insert(GNAS, "Bristol II", CAT_OUTDOOR, IMPERIAL, METRIC, Target.GNAS_9_1, 6, 60, 122, 12, 50, 122, 8,
                40, 122, 4, 0, 0, 0);
        insert(GNAS, "Bristol III", CAT_OUTDOOR, IMPERIAL, METRIC, Target.GNAS_9_1, 6, 50, 122, 12, 40, 122, 8,
                30, 122, 4, 0, 0, 0);
        insert(GNAS, "Bristol IV", CAT_OUTDOOR, IMPERIAL, METRIC, Target.GNAS_9_1, 6, 40, 122, 12, 30, 122, 8,
                20, 122, 4, 0, 0, 0);
        insert(GNAS, "Bristol V", CAT_OUTDOOR, IMPERIAL, METRIC, Target.GNAS_9_1, 6, 30, 122, 12, 20, 122, 8,
                10, 122, 4, 0, 0, 0);
        insert(GNAS, "St George", CAT_OUTDOOR, IMPERIAL, METRIC, Target.GNAS_9_1, 6, 100, 122, 6, 80, 122, 6,
                60, 122, 6, 0, 0, 0);
        insert(GNAS, "Albion", CAT_OUTDOOR, IMPERIAL, METRIC, Target.GNAS_9_1, 6, 80, 122, 6, 60, 122, 6, 50,
                122, 6, 0, 0, 0);
        insert(GNAS, "Windsor", CAT_OUTDOOR, IMPERIAL, METRIC, Target.GNAS_9_1, 6, 60, 122, 6, 50, 122, 6, 40,
                122, 6, 0, 0, 0);
        insert(GNAS, "Short Windsor", CAT_OUTDOOR, IMPERIAL, METRIC, Target.GNAS_9_1, 6, 50, 122, 6, 40, 122,
                6, 30, 122, 6, 0, 0, 0);
        insert(GNAS, "Junior Windsor", CAT_OUTDOOR, IMPERIAL, METRIC, Target.GNAS_9_1, 6, 40, 122, 6, 30, 122,
                6, 20, 122, 6, 0, 0, 0);
        insert(GNAS, "Short Junior Windsor", CAT_OUTDOOR, IMPERIAL, METRIC, Target.GNAS_9_1, 6, 30, 122, 6, 20,
                122, 6, 10, 122, 6, 0, 0, 0);
        insert(GNAS, "New Western", CAT_OUTDOOR, IMPERIAL, METRIC, Target.GNAS_9_1, 6, 100, 122, 8, 80, 122, 8,
                0, 0, 0, 0, 0, 0);
        insert(FITA, "FITA, (Gents)", CAT_OUTDOOR, METRIC, METRIC, Target.WA_10_1, 6, 90, 122, 6, 70,
                122, 6, 50, 80, 6, 30, 80, 6);
        insert(GNAS, "Long Western", CAT_OUTDOOR, IMPERIAL, METRIC, Target.GNAS_9_1, 6, 80, 122, 8, 60, 122, 8,
                0, 0, 0, 0, 0, 0);
        insert(GNAS, "Western", CAT_OUTDOOR, IMPERIAL, METRIC, Target.GNAS_9_1, 6, 60, 122, 8, 50, 122, 8, 0,
                0, 0, 0, 0, 0);
        insert(GNAS, "Short Western", CAT_OUTDOOR, IMPERIAL, METRIC, Target.GNAS_9_1, 6, 50, 122, 8, 40, 122,
                8, 0, 0, 0, 0, 0, 0);
        insert(GNAS, "Junior Western", CAT_OUTDOOR, IMPERIAL, METRIC, Target.GNAS_9_1, 6, 40, 122, 8, 30, 122,
                8, 0, 0, 0, 0, 0, 0);
        insert(GNAS, "Short Junior Western", CAT_OUTDOOR, IMPERIAL, METRIC, Target.GNAS_9_1, 6, 30, 122, 8, 20,
                122, 8, 0, 0, 0, 0, 0, 0);
        insert(GNAS, "American", CAT_OUTDOOR, IMPERIAL, METRIC, Target.GNAS_9_1, 6, 60, 122, 5, 50, 122, 5, 40,
                122, 5, 0, 0, 0);
        insert(GNAS, "St Nicholas", CAT_OUTDOOR, IMPERIAL, METRIC, Target.GNAS_9_1, 6, 40, 122, 8, 30, 122, 6,
                0, 0, 0, 0, 0, 0);
        insert(GNAS, "New National", CAT_OUTDOOR, IMPERIAL, METRIC, Target.GNAS_9_1, 6, 100, 122, 8, 80, 122,
                4, 0, 0, 0, 0, 0, 0);
        insert(GNAS, "Long National", CAT_OUTDOOR, IMPERIAL, METRIC, Target.GNAS_9_1, 6, 80, 122, 8, 60, 122,
                4, 0, 0, 0, 0, 0, 0);
        insert(GNAS, "National", CAT_OUTDOOR, IMPERIAL, METRIC, Target.GNAS_9_1, 6, 60, 122, 8, 50, 122, 4, 0,
                0, 0, 0, 0, 0);
        insert(GNAS, "Short National", CAT_OUTDOOR, IMPERIAL, METRIC, Target.GNAS_9_1, 6, 50, 122, 8, 40, 122,
                4, 0, 0, 0, 0, 0, 0);
        insert(GNAS, "Junior National", CAT_OUTDOOR, IMPERIAL, METRIC, Target.GNAS_9_1, 6, 40, 122, 8, 30, 122,
                4, 0, 0, 0, 0, 0, 0);
        insert(GNAS, "Short Junior National", CAT_OUTDOOR, IMPERIAL, METRIC, Target.GNAS_9_1, 6, 30, 122, 8,
                20, 122, 4, 0, 0, 0, 0, 0, 0);
        insert(GNAS, "New Warwick", CAT_OUTDOOR, IMPERIAL, METRIC, Target.GNAS_9_1, 6, 100, 122, 4, 80, 122, 4,
                0, 0, 0, 0, 0, 0);
        insert(GNAS, "Long Warwick", CAT_OUTDOOR, IMPERIAL, METRIC, Target.GNAS_9_1, 6, 80, 122, 4, 60, 122, 4,
                0, 0, 0, 0, 0, 0);
        insert(GNAS, "Warwick", CAT_OUTDOOR, IMPERIAL, METRIC, Target.GNAS_9_1, 6, 60, 122, 4, 50, 122, 4, 0,
                0, 0, 0, 0, 0);
        insert(GNAS, "Short Warwick", CAT_OUTDOOR, IMPERIAL, METRIC, Target.GNAS_9_1, 6, 50, 122, 4, 40, 122,
                4, 0, 0, 0, 0, 0, 0);
        insert(GNAS, "Junior Warwick", CAT_OUTDOOR, IMPERIAL, METRIC, Target.GNAS_9_1, 6, 40, 122, 4, 30, 122,
                4, 0, 0, 0, 0, 0, 0);
        insert(GNAS, "Short Junior Warwick", CAT_OUTDOOR, IMPERIAL, METRIC, Target.GNAS_9_1, 6, 30, 122, 4, 20,
                122, 4, 0, 0, 0, 0, 0, 0);
        insert(GNAS, "Bray I", CAT_INDOOR, IMPERIAL, METRIC, Target.WA_10_1, 6, 20, 40, 5, 0, 0, 0, 0,
                0, 0, 0, 0, 0);
        insert(GNAS, "Bray II", CAT_INDOOR, IMPERIAL, METRIC, Target.WA_10_1, 6, 25, 60, 5, 0, 0, 0, 0,
                0, 0, 0, 0, 0);
        insert(GNAS, "Portsmouth", CAT_INDOOR, IMPERIAL, METRIC, Target.WA_10_1, 6, 20, 60, 10, 0, 0, 0,
                0, 0, 0, 0, 0, 0);
        insert(GNAS, "Worcester", CAT_INDOOR, IMPERIAL, IMPERIAL, Target.TARGET_5_1, 5, 20, 16, 12, 0, 0, 0, 0,
                0, 0, 0, 0, 0);
        insert(GNAS, "Stafford", CAT_INDOOR, METRIC, METRIC, Target.WA_10_1, 6, 30, 80, 12, 0, 0, 0, 0,
                0, 0, 0, 0, 0);
        insert(GNAS, "Vegas", CAT_INDOOR, METRIC, METRIC, Target.WA_10_6, 6, 18, 40, 10, 0, 0, 0, 0, 0,
                0, 0, 0, 0);
        insert(FITA, "FITA, (Ladies)", CAT_OUTDOOR, METRIC, METRIC, Target.WA_10_1, 6, 70, 122, 6, 60,
                122, 6, 50, 80, 6, 30, 80, 6);
        insert(FITA, "FITA, (Cadet Ladies)", CAT_OUTDOOR, METRIC, METRIC, Target.WA_10_1, 6, 60, 122, 6,
                50, 122, 6, 40, 80, 6, 30, 80, 6);
        insert(FITA, "Half FITA, (Gents)", CAT_OUTDOOR, METRIC, METRIC, Target.WA_10_1, 6, 90, 122, 3,
                70, 122, 3, 50, 80, 3, 30, 80, 3);
        insert(FITA, "Half FITA, (Ladies)", CAT_OUTDOOR, METRIC, METRIC, Target.WA_10_1, 6, 70, 122, 3,
                60, 122, 3, 50, 80, 3, 30, 80, 3);
        insert(FITA, "Half FITA, (Cadet Ladies)", CAT_OUTDOOR, METRIC, METRIC, Target.WA_10_1, 6, 60,
                122, 3, 50, 122, 3, 40, 80, 3, 30, 80, 3);
        insert(FITA, "FITA 900", CAT_OUTDOOR, METRIC, METRIC, Target.WA_10_1, 6, 60, 122, 5, 50, 122, 5,
                40, 122, 5, 0, 0, 0);
        insert(FITA, "FITA 70", CAT_OUTDOOR, METRIC, METRIC, Target.WA_10_1, 6, 70, 122, 12, 0, 0, 0, 0,
                0, 0, 0, 0, 0);
        insert(FITA, "FITA 60", CAT_OUTDOOR, METRIC, METRIC, Target.WA_10_1, 6, 60, 122, 12, 0, 0, 0, 0,
                0, 0, 0, 0, 0);
        insert(FITA, "FITA Standard", CAT_OUTDOOR, METRIC, METRIC, Target.WA_10_1, 6, 50, 122, 6, 30,
                122, 6, 0, 0, 0, 0, 0, 0);
        insert(FITA, "Olympic Round", CAT_OUTDOOR, METRIC, METRIC, Target.WA_10_1, 3, 70, 122, 4, 0, 0,
                0, 0, 0, 0, 0, 0, 0);
        insert(FITA, "FITA 18", CAT_INDOOR, METRIC, METRIC, Target.WA_10_6, 6, 18, 40, 10, 0, 0, 0, 0,
                0, 0, 0, 0, 0);
        insert(FITA, "FITA 25", CAT_INDOOR, METRIC, METRIC, Target.WA_10_1, 6, 25, 60, 10, 0, 0, 0, 0,
                0, 0, 0, 0, 0);
        insert(FITA, "Combined FITA", CAT_INDOOR, METRIC, METRIC, Target.WA_10_1, 6, 25, 60, 10, 18, 40,
                10, 0, 0, 0, 0, 0, 0);
        insert(FITA, "Match Round", CAT_INDOOR, METRIC, METRIC, Target.WA_10_6, 3, 18, 40, 4, 0, 0, 0,
                0, 0, 0, 0, 0, 0);
        insert(GNAS, "Metric I", CAT_OUTDOOR, METRIC, METRIC, Target.WA_10_1, 6, 70, 122, 6, 60, 122, 6,
                50, 80, 6, 30, 80, 6);
        insert(GNAS, "Metric II", CAT_OUTDOOR, METRIC, METRIC, Target.WA_10_1, 6, 60, 122, 6, 50, 122,
                6, 40, 80, 6, 30, 80, 6);
        insert(GNAS, "Metric III", CAT_OUTDOOR, METRIC, METRIC, Target.WA_10_1, 6, 50, 122, 6, 40, 122,
                6, 30, 80, 6, 20, 80, 6);
        insert(GNAS, "Metric IV", CAT_OUTDOOR, METRIC, METRIC, Target.WA_10_1, 6, 40, 122, 6, 30, 122,
                6, 20, 80, 6, 10, 80, 6);
        insert(GNAS, "Metric V", CAT_OUTDOOR, METRIC, METRIC, Target.WA_10_1, 6, 30, 122, 6, 20, 122, 6,
                15, 80, 6, 10, 80, 6);
        insert(GNAS, "Long Metric, (Gents)", CAT_OUTDOOR, METRIC, METRIC, Target.WA_10_1, 6, 90, 122, 6,
                70, 122, 6, 0, 0, 0, 0, 0, 0);
        insert(GNAS, "Long Metric, (Ladies)", CAT_OUTDOOR, METRIC, METRIC, Target.WA_10_1, 6, 70, 122,
                6, 60, 122, 6, 0, 0, 0, 0, 0, 0);
        insert(GNAS, "Long Metric I", CAT_OUTDOOR, METRIC, METRIC, Target.WA_10_1, 6, 70, 122, 6, 60,
                122, 6, 0, 0, 0, 0, 0, 0);
        insert(GNAS, "Long Metric II", CAT_OUTDOOR, METRIC, METRIC, Target.WA_10_1, 6, 60, 122, 6, 50,
                122, 6, 0, 0, 0, 0, 0, 0);
        insert(GNAS, "Long Metric III", CAT_OUTDOOR, METRIC, METRIC, Target.WA_10_1, 6, 50, 122, 6, 40,
                122, 6, 0, 0, 0, 0, 0, 0);
        insert(GNAS, "Long Metric IV", CAT_OUTDOOR, METRIC, METRIC, Target.WA_10_1, 6, 40, 122, 6, 30,
                122, 6, 0, 0, 0, 0, 0, 0);
    }
    public static void insert(int institution, String name, boolean indoor, String distanceUnit, String targetUnit, int target, int arrowsPerPasse,
            int distanceR1, int targetSizeR1, int passesR1,
            int distanceR2, int targetSizeR2, int passesR2,
            int distanceR3, int targetSizeR3, int passesR3,
            int distanceR4, int targetSizeR4, int passesR4) {
        //FITA, round_name, OUTDOOR, METRIC, TARGET_METRIC, TARGET_MAX_MIN,
        // arrows_per_passe, distance, target_size, passes, distance_round_2, target_size_round_2, passes_2, distance_round_3, target_size_3, passes_3, distance_round_4, target_size_4, passes_4
    }*/
}
