/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.io.Serializable;
import java.util.ArrayList;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.models.target.DAIR3D;
import de.dreier.mytargets.shared.models.target.NFAAIndoor;
import de.dreier.mytargets.shared.models.target.Target;
import de.dreier.mytargets.shared.models.target.TargetFactory;
import de.dreier.mytargets.shared.models.target.Vertical3Spot;
import de.dreier.mytargets.shared.models.target.WA6RingTarget;
import de.dreier.mytargets.shared.models.target.WAFullTarget;
import de.dreier.mytargets.shared.models.target.Worcester;

public class StandardRound extends IdProvider implements Serializable, DatabaseSerializable {
    static final long serialVersionUID = 56L;
    public static final int WA = 0;
    private static final int GNAS_IMPERIAL = 1;
    private static final int GNAS_METRIC = 2;
    private static final int AUSTRALIAN = 3;
    private static final int IFAA = 4;
    private static final int NFAA = 5;
    private static final int NASP = 6;
    private static final int OTHER = 7;
    public static final int CUSTOM = 100;
    private static final boolean CAT_INDOOR = true;
    private static final boolean CAT_OUTDOOR = false;

    public static final String TABLE = "STANDARD_ROUND_TEMPLATE";
    private static final String NAME = "name";
    private static final String INSTITUTION = "institution";
    private static final String INDOOR = "indoor";
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    NAME + " TEXT," +
                    INSTITUTION + " INTEGER," +
                    INDOOR + " INTEGER);";

    public String name;
    public int institution;

    public ArrayList<RoundTemplate> getRounds() {
        return rounds;
    }

    private ArrayList<RoundTemplate> rounds = new ArrayList<>();
    public boolean indoor;

    @Override
    public String getTableName() {
        return TABLE;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(NAME, name);
        values.put(INSTITUTION, institution);
        values.put(INDOOR, indoor ? 1 : 0);
        return values;
    }

    @Override
    public void fromCursor(Context context, Cursor cursor, int startColumnIndex) {
        super.setId(cursor.getLong(0));
        name = cursor.getString(1);
        institution = cursor.getInt(2);
        indoor = cursor.getInt(3) == 1;
    }

    public static ArrayList<StandardRound> initTable(Context context) {
        Target WA_10_1 = TargetFactory.createTarget(context, WAFullTarget.ID, 0);
        Target WA_10_6 = TargetFactory.createTarget(context, 2, 0);
        Target WA_10_6R = TargetFactory.createTarget(context, WA6RingTarget.ID, 0);
        Target WA_3_SPOT = TargetFactory.createTarget(context, Vertical3Spot.ID, 0);
        Target GNAS_9_1 = TargetFactory.createTarget(context, WAFullTarget.ID, 4);
        Target TARGET_5_1 = TargetFactory.createTarget(context, Worcester.ID, 0);
        Target DAIR = TargetFactory.createTarget(context, DAIR3D.ID, 0);
        Target NFAA_INDOOR = TargetFactory.createTarget(context, NFAAIndoor.ID, 2);
        ArrayList<StandardRound> rounds = new ArrayList<>();

        // Indoor
        rounds.add(build(context, AUSTRALIAN, "Australian Combined Indoor", CAT_INDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_6, 3, 18, 40, 10, 25, 60, 10));
        rounds.add(build(context, AUSTRALIAN, "Australian Indoor 1", CAT_INDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_6, 3, 18, 40, 10));
        rounds.add(build(context, AUSTRALIAN, "Australian Indoor 2", CAT_INDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_6, 3, 25, 60, 10));
        rounds.add(build(context, OTHER, "DAIR 380", CAT_INDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                DAIR, 3, 25, 60, 10));
        rounds.add(build(context, WA, "WA 18 (40cm)", CAT_INDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_6, 3, 18, 40, 20)); // in 2 min
        rounds.add(build(context, WA, "WA 18 (60cm)", CAT_INDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_6, 3, 18, 60, 20)); // in 2 min
        rounds.add(build(context, WA, "WA 3 Spot", CAT_INDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_3_SPOT, 3, 18, 60, 20)); // in 2 min
        rounds.add(build(context, WA, "WA 25", CAT_INDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 3, 25, 60, 20)); // in 2 min
        rounds.add(build(context, WA, "WA Combined", CAT_INDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 25, 60, 10, 18, 40, 10)); // in 2 min
        rounds.add(build(context, WA, R.string.match_round, CAT_INDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_3_SPOT, 3, 18, 40, 4));
        rounds.add(build(context, GNAS_METRIC, R.string.stafford, CAT_INDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 3, 30, 80, 24));
        rounds.add(build(context, GNAS_IMPERIAL, R.string.bray_i, CAT_INDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WA_10_1, 6, 20, 40, 5)); // in 4 min
        rounds.add(build(context, GNAS_IMPERIAL, R.string.bray_ii, CAT_INDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WA_10_1, 6, 25, 60, 5)); // in 4 min
        rounds.add(build(context, GNAS_IMPERIAL, R.string.portsmouth, CAT_INDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WA_10_1, 3, 20, 60, 20)); // in 2 min
        rounds.add(build(context, GNAS_IMPERIAL, R.string.worcester, CAT_INDOOR,
                Dimension.YARDS, Dimension.INCH,
                TARGET_5_1, 5, 20, 16, 12)); // in 2 min
        rounds.add(build(context, GNAS_IMPERIAL, "Vegas 300", CAT_INDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WA_10_6, 3, 20, 40, 20)); // in 2 min
        rounds.add(build(context, IFAA, "IFAA 150 Indoor: Adult/Youth", CAT_INDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WA_10_6, 5, 20, 40, 6)); // in 4 min
        rounds.add(build(context, IFAA, "IFAA 150 Indoor: Cub", CAT_INDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WA_10_6, 5, 10, 40, 6)); // in 4 min
        rounds.add(build(context, NASP, "NASP 300 Round", CAT_INDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_6, 5, 10, 80, 3, 15, 80, 3)); // in 4 min
        rounds.add(build(context, NFAA, "NFAA 420", CAT_INDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                NFAA_INDOOR, 5, 10, 40, 12));
        rounds.add(build(context, NFAA, "NFAA/IFAA 300: Cub", CAT_INDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                NFAA_INDOOR, 5, 20, 40, 12)); // in 4 min


        // WA Elimination
        rounds.add(build(context, WA, "WA Individual Compound Elimination 18m", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 3, 18, 40, 5)); // in 4 min
        rounds.add(build(context, WA, "WA Individual Compound Elimination 40m", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_6R, 3, 40, 80, 5)); // in 4 min
        rounds.add(build(context, WA, "WA Individual Compound Elimination 50m", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_6R, 3, 50, 80, 5)); // in 4 min
        rounds.add(build(context, WA, "WA Recurve Elimination 18m", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 3, 18, 40, 5)); // in 4 min
        rounds.add(build(context, WA, "WA Recurve Elimination 50m", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_6R, 3, 50, 122, 5)); // in 4 min
        rounds.add(build(context, WA, "WA Recurve Elimination 60m", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_6R, 3, 60, 122, 5)); // in 4 min
        rounds.add(build(context, WA, "WA Recurve Elimination 70m", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_6R, 3, 70, 122, 5)); // in 4 min

        // WA
        rounds.add(build(context, WA, R.string.wa_50, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 50, 80, 12)); // in 4 min
        rounds.add(build(context, WA, R.string.wa_60, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 60, 122, 12)); // in 4 min
        rounds.add(build(context, WA, R.string.wa_70, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 70, 122, 12)); // in 4 min
        rounds.add(build(context, WA, R.string.wa_720, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 70, 122, 12)); // in 4 min
        rounds.add(build(context, WA, R.string.wa_900, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 60, 122, 5, 50, 122, 5,
                40, 122, 5)); // in 4 min
        rounds.add(build(context, WA, "WA Bowman", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 30, 122, 6, 25, 122, 6, 25, 80, 6, 20, 80, 6)); // in 4 min
        rounds.add(build(context, WA, "WA 1440 Cadet Men", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 70, 122, 6, 60, 122, 6,
                50, 80, 6, 30, 80, 6)); // in 4 min
        rounds.add(build(context, WA, "WA 1440 Cadet Woman", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 60, 122, 6,
                50, 122, 6, 40, 80, 6, 30, 80, 6)); // in 4 min
        rounds.add(build(context, WA, "WA Compound Individual", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_6R, 3, 50, 80, 5)); // in 2 min
        rounds.add(build(context, WA, "WA Compound Qualification", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 50, 80, 12)); // in 4 min
        rounds.add(build(context, WA, "WA Cub", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 50, 122, 6, 40, 80, 6, 30, 122,
                6, 20, 80, 6)); // in 4 min
        rounds.add(build(context, WA, R.string.wa_standard, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 50, 122, 6, 30, 122, 6)); // in 4 min
        rounds.add(build(context, WA, R.string.olympic_round, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 3, 70, 122, 4));

        rounds.add(build(context, WA, "WA 1440 Junior Men", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 90, 122, 6, 70, 122, 6, 50, 80, 6, 30, 80, 6)); // in 4 min
        rounds.add(build(context, WA, "WA 1440 Junior Woman", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 70, 122, 6, 60, 122, 6, 50, 80, 6, 30, 80, 6)); // in 4 min


        rounds.add(build(context, WA, "WA 1440 Men Master 50+", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 70, 122, 6, 60, 122, 6, 50, 80, 6, 30, 80, 6)); // in 4 min
        rounds.add(build(context, WA, "WA 1440 Men Master 60+", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 70, 122, 6, 60, 122, 6, 50, 80, 6, 30, 80, 6)); // in 4 min
        rounds.add(build(context, WA, "WA 1440 Men Master 70+", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 60, 122, 6, 50, 122, 6, 40, 80, 6, 30, 80, 6)); // in 4 min
        rounds.add(build(context, WA, "WA 1440 Men Senior", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 90, 122, 6, 70, 122, 6, 50, 80, 6, 30, 80, 6)); // in 4 min
        rounds.add(build(context, WA, "WA 1440 Women Master 50+", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 60, 122, 6, 50, 122, 6, 40, 80, 6, 30, 80, 6)); // in 4 min
        rounds.add(build(context, WA, "WA 1440 Women Master 60+", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 60, 122, 6, 50, 122, 6, 40, 80, 6, 30, 80, 6)); // in 4 min
        rounds.add(build(context, WA, "WA 1440 Women Master 70+", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 60, 122, 6, 50, 122, 6, 40, 80, 6, 30, 80, 6)); // in 4 min
        rounds.add(build(context, WA, "WA 1440 Women Senior", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 70, 122, 6, 60, 122, 6, 50, 80, 6, 30, 80, 6)); // in 4 min
        rounds.add(build(context, WA, "WA 1440 Women Senior", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 70, 122, 6, 60, 122, 6, 50, 80, 6, 30, 80, 6)); // in 4 min
        rounds.add(build(context, WA, "Half WA 1440 Cadet Men", CAT_OUTDOOR,
                Dimension.METER, Dimension.METER,
                WA_10_1, 6, 70, 122, 3, 60, 122, 3,
                50, 80, 3, 30, 80, 3));
        rounds.add(build(context, WA, "Half WA 1440 Cadet Women", CAT_OUTDOOR,
                Dimension.METER, Dimension.METER,
                WA_10_1, 6, 60, 122, 3, 50, 122, 3,
                40, 80, 3, 30, 80, 3));
        rounds.add(build(context, WA, "Half WA 1440 Junior Men", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 90, 122, 3,
                70, 122, 3, 50, 80, 3, 30, 80, 3)); // in 4 min
        rounds.add(build(context, WA, "Half WA 1440 Junior Women", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 70, 122, 3,
                60, 122, 3, 50, 80, 3, 30, 80, 3)); // in 4 min
        rounds.add(build(context, WA, "Half WA 1440 Men Master 50+", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 70, 122, 3,
                60, 122, 3, 50, 80, 3, 30, 80, 3)); // in 4 min
        rounds.add(build(context, WA, "Half WA 1440 Men Master 60+", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 70, 122, 3,
                60, 122, 3, 50, 80, 3, 30, 80, 3)); // in 4 min
        rounds.add(build(context, WA, "Half WA 1440 Men Master 70+", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 60, 122, 3,
                50, 122, 3, 40, 80, 3, 30, 80, 3)); // in 4 min
        rounds.add(build(context, WA, "Half WA 1440 Men Senior", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 90, 122, 3,
                70, 122, 3, 50, 80, 3, 30, 80, 3)); // in 4 min
        rounds.add(build(context, WA, "Half WA 1440 Women Master 50+", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 60, 122, 3, 50, 122, 3, 40, 80, 3, 30, 80, 3)); // in 4 min
        rounds.add(build(context, WA, "Half WA 1440 Women Master 60+", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 60, 122, 3, 50, 122, 3, 40, 80, 3, 30, 80, 3)); // in 4 min
        rounds.add(build(context, WA, "Half WA 1440 Women Master 70+", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 60, 122, 3, 50, 122, 3, 40, 80, 3, 30, 80, 3)); // in 4 min
        rounds.add(build(context, WA, "Half WA 1440 Women Senior", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 70, 122, 3, 60, 122, 3, 50, 80, 3, 30, 80, 3)); // in 4 min
        rounds.add(build(context, WA, "WA Double 720 30m (80cm)", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 30, 80, 12, 30, 80, 12)); // in 4 min
        rounds.add(build(context, WA, "WA Double 720 40m (122cm)", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 40, 122, 12, 40, 122, 12)); // in 4 min
        rounds.add(build(context, WA, "WA Double 720 40m (80cm)", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 40, 80, 12, 40, 80, 12)); // in 4 min
        rounds.add(build(context, WA, "WA Double 720 50m (122cm)", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 50, 122, 12, 50, 122, 12)); // in 4 min
        rounds.add(build(context, WA, "WA Double 720 50m (80cm)", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 50, 80, 12, 50, 80, 12)); // in 4 min
        rounds.add(build(context, WA, "WA Double 720 60m (122cm)", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 60, 122, 12, 60, 122, 12)); // in 4 min
        rounds.add(build(context, WA, "WA Double 720 70m (122cm)", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 70, 122, 12, 70, 122, 12)); // in 4 min

        // GNAS Imperial
        rounds.add(build(context, GNAS_IMPERIAL, R.string.albion, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 6, 80, 122, 6, 60, 122, 6, 50, 122, 6));
        rounds.add(build(context, GNAS_IMPERIAL, R.string.american, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 6, 60, 122, 5, 50, 122, 5, 40, 122, 5));
        
        rounds.add(build(context, GNAS_IMPERIAL, R.string.bristol_i, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 6, 80, 122, 12, 60, 122, 8, 50, 122, 4));
        rounds.add(build(context, GNAS_IMPERIAL, R.string.bristol_ii, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 6, 60, 122, 12, 50, 122, 8, 40, 122, 4));
        rounds.add(build(context, GNAS_IMPERIAL, R.string.bristol_iii, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 6, 50, 122, 12, 40, 122, 8, 30, 122, 4));
        rounds.add(build(context, GNAS_IMPERIAL, R.string.bristol_iv, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 6, 40, 122, 12, 30, 122, 8, 20, 122, 4));
        rounds.add(build(context, GNAS_IMPERIAL, R.string.bristol_v, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 6, 30, 122, 12, 20, 122, 8, 10, 122, 4));
        
        rounds.add(build(context, GNAS_IMPERIAL, R.string.hereford, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 6, 80, 122, 12, 60, 122, 8, 50, 122, 4));
        
        rounds.add(build(context, GNAS_IMPERIAL, R.string.junior_national, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 6, 40, 122, 8, 30, 122, 4));
        rounds.add(build(context, GNAS_IMPERIAL, R.string.junior_warwick, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 6, 40, 122, 4, 30, 122, 4));
        rounds.add(build(context, GNAS_IMPERIAL, R.string.junior_western, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 6, 40, 122, 8, 30, 122, 8));
        rounds.add(build(context, GNAS_IMPERIAL, R.string.junior_windsor, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 6, 40, 122, 6, 30, 122, 6, 20, 122, 6));
        
        rounds.add(build(context, GNAS_IMPERIAL, R.string.long_national, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 6, 80, 122, 8, 60, 122, 4));
        rounds.add(build(context, GNAS_IMPERIAL, R.string.long_warwick, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 6, 80, 122, 4, 60, 122, 4));
        rounds.add(build(context, GNAS_IMPERIAL, R.string.long_western, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 6, 80, 122, 8, 60, 122, 8));
        
        rounds.add(build(context, GNAS_IMPERIAL, R.string.national, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 6, 60, 122, 8, 50, 122, 4));
        
        rounds.add(build(context, GNAS_IMPERIAL, R.string.new_national, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 6, 100, 122, 8, 80, 122, 4));
        rounds.add(build(context, GNAS_IMPERIAL, R.string.new_warwick, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 6, 100, 122, 4, 80, 122, 4));
        rounds.add(build(context, GNAS_IMPERIAL, R.string.new_western, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 6, 100, 122, 8, 80, 122, 8));
        
        rounds.add(build(context, GNAS_IMPERIAL, R.string.short_junior_national, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 6, 30, 122, 8, 20, 122, 4));
        rounds.add(build(context, GNAS_IMPERIAL, R.string.short_junior_warwick, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 6, 30, 122, 4, 20, 122, 4));
        rounds.add(build(context, GNAS_IMPERIAL, R.string.short_junior_western, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 6, 30, 122, 8, 20, 122, 8));
        rounds.add(build(context, GNAS_IMPERIAL, R.string.short_junior_windsor, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 6, 30, 122, 6, 20,
                122, 6, 10, 122, 6));
        
        rounds.add(build(context, GNAS_IMPERIAL, R.string.short_national, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 6, 50, 122, 8, 40, 122, 4));
        rounds.add(build(context, GNAS_IMPERIAL, R.string.short_warwick, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 6, 50, 122, 4, 40, 122, 4));
        rounds.add(build(context, GNAS_IMPERIAL, R.string.short_western, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 6, 50, 122, 8, 40, 122, 8));
        rounds.add(build(context, GNAS_IMPERIAL, R.string.short_windsor, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 6, 50, 122, 6, 40, 122, 6, 30, 122, 6));
        
        rounds.add(build(context, GNAS_IMPERIAL, R.string.st_george, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 6, 100, 122, 6, 80, 122, 6, 60, 122, 6));
        rounds.add(build(context, GNAS_IMPERIAL, R.string.st_nicholas, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 6, 40, 122, 8, 30, 122, 6));
        rounds.add(build(context, GNAS_IMPERIAL, R.string.warwick, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 6, 60, 122, 4, 50, 122, 4));
        rounds.add(build(context, GNAS_IMPERIAL, R.string.western, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 6, 60, 122, 8, 50, 122, 8));
        rounds.add(build(context, GNAS_IMPERIAL, R.string.windsor, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 6, 60, 122, 6, 50, 122, 6, 40, 122, 6));
        rounds.add(build(context, GNAS_IMPERIAL, R.string.york, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 6, 100, 122, 12, 80, 122, 8, 60, 122, 4));

        // GNAS Metric
        rounds.add(build(context, GNAS_METRIC, "Frostbite", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 3, 30, 80, 12)); // in 2 min
        rounds.add(build(context, GNAS_METRIC, R.string.half_metric_i, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 70, 122, 3, 60,
                122, 3, 50, 80, 3, 30, 80, 3));
        rounds.add(build(context, GNAS_METRIC, R.string.half_metric_ii, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 60, 122, 3, 50, 122, 3, 40, 80, 3, 30, 80, 3));
        rounds.add(build(context, GNAS_METRIC, R.string.half_metric_iii, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 50, 122, 3, 40, 122, 3, 30, 80, 3, 20, 80, 3));
        rounds.add(build(context, GNAS_METRIC, R.string.half_metric_iv, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 40, 122, 3, 30, 122, 3, 20, 80, 3, 10, 80, 3));
        rounds.add(build(context, GNAS_METRIC, R.string.half_metric_v, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 30, 122, 3, 20, 122, 3, 15, 80, 3, 10, 80, 3));

        rounds.add(build(context, GNAS_METRIC, R.string.long_metric_gents, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 90, 122, 6, 70, 122, 6));
        rounds.add(build(context, GNAS_METRIC, R.string.long_metric_ladies, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 70, 122, 6, 60, 122, 6));
        rounds.add(build(context, GNAS_METRIC, R.string.long_metric_i, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 70, 122, 6, 60, 122, 6));
        rounds.add(build(context, GNAS_METRIC, R.string.long_metric_ii, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 60, 122, 6, 50, 122, 6));
        rounds.add(build(context, GNAS_METRIC, R.string.long_metric_iii, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 50, 122, 6, 40, 122, 6));
        rounds.add(build(context, GNAS_METRIC, R.string.long_metric_iv, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 40, 122, 6, 30, 122, 6));
        rounds.add(build(context, GNAS_METRIC, R.string.long_metric_v, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 30, 122, 6, 20, 122, 6));

        rounds.add(build(context, GNAS_METRIC, R.string.metric_i, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 70, 122, 6, 60, 122, 6, 50, 80, 6, 30, 80, 6));
        rounds.add(build(context, GNAS_METRIC, R.string.metric_ii, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 60, 122, 6, 50, 122, 6, 40, 80, 6, 30, 80, 6));
        rounds.add(build(context, GNAS_METRIC, R.string.metric_iii, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 50, 122, 6, 40, 122, 6, 30, 80, 6, 20, 80, 6));
        rounds.add(build(context, GNAS_METRIC, R.string.metric_iv, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 40, 122, 6, 30, 122, 6, 20, 80, 6, 10, 80, 6));
        rounds.add(build(context, GNAS_METRIC, R.string.metric_v, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 30, 122, 6, 20, 122, 6, 15, 80, 6, 10, 80, 6));

        rounds.add(build(context, GNAS_METRIC, R.string.short_metric_i, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 50, 80, 6, 30, 80, 6));
        rounds.add(build(context, GNAS_METRIC, R.string.short_metric_ii, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 40, 80, 6, 30, 80, 6));
        rounds.add(build(context, GNAS_METRIC, R.string.short_metric_iii, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 30, 80, 6, 20, 80, 6));
        rounds.add(build(context, GNAS_METRIC, R.string.short_metric_iv, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 20, 80, 6, 10, 80, 6));
        rounds.add(build(context, GNAS_METRIC, R.string.short_metric_v, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 15, 80, 6, 10, 80, 6));

        // Australia
        rounds.add(build(context, AUSTRALIAN, "Adelaide", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 60, 122, 5, 50, 122, 5, 40, 80, 5, 30, 80, 5));
        rounds.add(build(context, AUSTRALIAN, "Brisbane", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 70, 122, 5, 60, 122, 5, 50, 80, 5, 40, 80, 5));
        rounds.add(build(context, AUSTRALIAN, "Canberra", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 60, 122, 5, 50, 122, 5, 40, 122, 5));
        rounds.add(build(context, AUSTRALIAN, "Darwin", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 40, 122, 15));
        rounds.add(build(context, AUSTRALIAN, "Drake", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 30, 80, 15));
        rounds.add(build(context, AUSTRALIAN, "Fremantle", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 60, 122, 6, 50, 122, 6, 40, 80, 6, 30, 80, 6));
        rounds.add(build(context, AUSTRALIAN, "Geelong", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 30, 122, 15));
        rounds.add(build(context, AUSTRALIAN, "Grange", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 60, 122, 15));
        rounds.add(build(context, AUSTRALIAN, "Hobart", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 90, 122, 5, 70, 122, 5, 50, 122, 5));
        rounds.add(build(context, AUSTRALIAN, "Holt", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 50, 80, 15));
        rounds.add(build(context, AUSTRALIAN, "Horsham", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 40, 122, 6, 35, 122, 6, 30, 80, 6, 25, 80, 6));
        rounds.add(build(context, AUSTRALIAN, "Intermediate", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 55, 122, 6, 45, 122, 6, 35, 80, 6, 25, 80, 6));
        rounds.add(build(context, AUSTRALIAN, "Junior Canberra", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 40, 122, 5, 30, 122, 5, 20, 122, 5));
        rounds.add(build(context, AUSTRALIAN, "Launcheston", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 50, 80, 6, 30, 80, 6));
        rounds.add(build(context, AUSTRALIAN, "Long Brisbane", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 90, 122, 5, 70, 122, 5, 60, 80, 5, 50, 80, 5));
        rounds.add(build(context, AUSTRALIAN, "Long Sydney", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 90, 122, 5, 70, 122, 5, 60, 122, 5, 50, 122, 5));
        rounds.add(build(context, AUSTRALIAN, "Melbourne", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 90, 122, 15));
        rounds.add(build(context, AUSTRALIAN, "Newcastle", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 20, 122, 15));
        rounds.add(build(context, AUSTRALIAN, "Perth", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 70, 122, 5, 60, 122, 5, 50, 122, 5));
        rounds.add(build(context, AUSTRALIAN, "Short Adelaide", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 50, 122, 5, 40, 122, 5, 30, 80, 5, 20, 80, 5));
        rounds.add(build(context, AUSTRALIAN, "Short Canberra", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 50, 122, 5, 40, 122, 5, 30, 122, 5));
        rounds.add(build(context, AUSTRALIAN, "Sydney", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 70, 122, 5, 60, 122, 5, 50, 122, 5, 40, 122, 5));
        rounds.add(build(context, AUSTRALIAN, "Townsville", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 60, 122, 6, 50, 122, 6));
        rounds.add(build(context, AUSTRALIAN, "Wollongong", CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA_10_1, 6, 90, 122, 6, 70, 122, 6));

        // NFAA
        rounds.add(build(context, NFAA, "NFAA 600", CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WA_10_1, 5, 60, 122, 4, 50, 122, 4, 40, 122, 4));
        rounds.add(build(context, NFAA, "NFAA 600 Classic/Dakota", CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WA_10_1, 5, 40, 92, 4, 50, 92, 4, 60, 92, 4));
        rounds.add(build(context, NFAA, "NFAA 600 Classic/Dakota: Cub", CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WA_10_1, 5, 10, 92, 4, 20, 92, 4, 30, 92, 4));
        rounds.add(build(context, NFAA, "NFAA 600 Classic/Dakota: Junior/Youth", CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WA_10_1, 5, 30, 92, 4, 40, 92, 4, 50, 92, 4));
        rounds.add(build(context, NFAA, "NFAA 600: Cub", CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WA_10_1, 5, 30, 122, 4, 20, 122, 4, 10, 122, 4));
        rounds.add(build(context, NFAA, "NFAA 600: Junior/Youth", CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WA_10_1, 5, 50, 122, 4, 40, 122, 4, 30, 122, 4));
        rounds.add(build(context, NFAA, "NFAA/American 810", CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 5, 60, 122, 6, 50, 122, 6, 40, 122, 6));
        rounds.add(build(context, NFAA, "NFAA/American 810: Cub", CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 5, 30, 122, 6, 20, 122, 6, 10, 122, 6));
        rounds.add(build(context, NFAA, "NFAA/American 810: Junior/Youth", CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                GNAS_9_1, 5, 50, 122, 6, 40, 122, 6, 30, 122, 6));
        rounds.add(build(context, NFAA, "NFAA/American 900", CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WA_10_1, 5, 60, 122, 6, 50, 122, 6, 40, 122, 6));
        rounds.add(build(context, NFAA, "NFAA/American 900: Cub", CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WA_10_1, 5, 30, 122, 6, 20, 122, 6, 10, 122, 6));
        rounds.add(build(context, NFAA, "NFAA/American 900: Junior/Youth", CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WA_10_1, 5, 50, 122, 6, 40, 122, 6, 30, 122, 6));


        // Other
        rounds.add(build(context, NFAA, "Canadian 900", CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WA_10_1, 6, 55, 122, 5, 45, 122, 5, 35, 122, 5)); // in 4 min
        rounds.add(build(context, NFAA, "T2S 900", CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WA_10_1, 6, 35, 80, 5, 30, 80, 5, 25, 80, 5)); // in 4 min

        return rounds;
    }

    /**
     * Builds a new standard round instance
     *
     * @param institution    Institution that specified the round (GNAS or FITA)
     * @param name           Name of the round
     * @param indoor         CAT_INDOOR or CAT_OUTDOOR
     * @param distanceUnit   Unit of the distance specified in round Details
     * @param targetUnit     Unit of the target size specified in roundDetails
     * @param target         Index of the target that is used for shooting
     * @param arrowsPerPasse Number of arrows that are shot per passe
     * @param roundDetails   Per round distance, targetSize and number of passes are expected
     * @return The standard round with the specified properties
     */
    public static StandardRound build(Context context, int institution, String name, boolean indoor, String distanceUnit, String targetUnit, Target target, int arrowsPerPasse, int... roundDetails) {
        StandardRound standardRound = new StandardRound();
        standardRound.name = name;
        standardRound.indoor = indoor;
        standardRound.institution = institution;
        for (int i = 0; i < roundDetails.length; i += 3) {
            RoundTemplate roundTemplate = new RoundTemplate();
            roundTemplate.arrowsPerPasse = arrowsPerPasse;
            roundTemplate.distance = new Distance(roundDetails[i], distanceUnit);
            roundTemplate.target = target;
            roundTemplate.target.size = new Diameter(roundDetails[i + 1], targetUnit);
            roundTemplate.passes = roundDetails[i + 2];
            standardRound.insert(roundTemplate);
        }
        return standardRound;
    }

    /**
     * Builds a new standard round instance
     *
     * @param institution    Institution that specified the round (GNAS or FITA)
     * @param name           Name of the round
     * @param indoor         CAT_INDOOR or CAT_OUTDOOR
     * @param distanceUnit   Unit of the distance specified in round Details
     * @param targetUnit     Unit of the target size specified in roundDetails
     * @param target         Index of the target that is used for shooting
     * @param arrowsPerPasse Number of arrows that are shot per passe
     * @param roundDetails   Per round distance, targetSize and number of passes are expected
     * @return The standard round with the specified properties
     */
    public static StandardRound build(Context context, int institution, int name, boolean indoor, String distanceUnit, String targetUnit, Target target, int arrowsPerPasse, int... roundDetails) {
        return build(context, institution, context.getString(name), indoor, distanceUnit, targetUnit, target, arrowsPerPasse, roundDetails);
    }

    public void insert(RoundTemplate template) {
        template.index = rounds.size();
        template.standardRound = getId();
        rounds.add(template);
    }

    @Override
    public void setId(long id) {
        super.setId(id);
        for (RoundTemplate r : rounds) {
            r.standardRound = id;
        }
    }

    public String getName() {
        if (name != null) {
            return name;
        }
        return "";
    }

    public String getDescription(Context context) {
        String desc = "";
        for (RoundTemplate r : rounds) {
            if (!desc.isEmpty()) {
                desc += "\n";
            }
            desc += context.getString(R.string.round_desc, r.distance, r.passes,
                    r.arrowsPerPasse, r.target.size);
        }
        return desc;
    }

    public void setRounds(ArrayList<RoundTemplate> rounds) {
        this.rounds = rounds;
    }
}
