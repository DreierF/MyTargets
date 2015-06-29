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

public class StandardRound extends IdProvider implements Serializable, DatabaseSerializable {
    static final long serialVersionUID = 56L;
    public static final int FITA = 0;
    private static final int GNAS = 1;
    public static final int CUSTOM = 2;
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
    public void fromCursor(Cursor cursor, int startColumnIndex) {
        super.setId(cursor.getLong(0));
        name = cursor.getString(1);
        institution = cursor.getInt(2);
        indoor = cursor.getInt(3) == 1;
    }

    public static ArrayList<StandardRound> initTable(Context context) {
        ArrayList<StandardRound> rounds = new ArrayList<>();
        rounds.add(build(GNAS, context.getString(R.string.long_metric_v), CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                Target.WA_10_1, 6, 30, 122, 6, 20,
                122, 6));
        rounds.add(
                build(GNAS, context.getString(R.string.short_metric), CAT_OUTDOOR, Dimension.METER,
                        Dimension.CENTIMETER,
                        Target.WA_10_1, 6, 50, 80, 6, 30, 80,
                        6));
        rounds.add(build(GNAS, context.getString(R.string.short_metric_i), CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                Target.WA_10_1, 6, 50, 80, 6, 30,
                80, 6));
        rounds.add(build(GNAS, context.getString(R.string.short_metric_ii), CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                Target.WA_10_1, 6, 40, 80, 6, 30,
                80, 6));
        rounds.add(build(GNAS, context.getString(R.string.short_metric_iii), CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                Target.WA_10_1, 6, 30, 80, 6, 20,
                80, 6));
        rounds.add(build(GNAS, context.getString(R.string.short_metric_iv), CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                Target.WA_10_1, 6, 20, 80, 6, 10,
                80, 6));
        rounds.add(build(GNAS, context.getString(R.string.short_metric_v), CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                Target.WA_10_1, 6, 15, 80, 6, 10,
                80, 6));
        rounds.add(build(GNAS, context.getString(R.string.half_metric_i), CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                Target.WA_10_1, 6, 70, 122, 3, 60,
                122, 3, 50, 80, 3, 30, 80, 3));
        rounds.add(build(GNAS, context.getString(R.string.half_metric_ii), CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                Target.WA_10_1, 6, 60, 122, 3, 50,
                122, 3, 40, 80, 3, 30, 80, 3));
        rounds.add(build(GNAS, context.getString(R.string.half_metric_iii), CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                Target.WA_10_1, 6, 50, 122, 3, 40,
                122, 3, 30, 80, 3, 20, 80, 3));
        rounds.add(build(GNAS, context.getString(R.string.half_metric_iv), CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                Target.WA_10_1, 6, 40, 122, 3, 30,
                122, 3, 20, 80, 3, 10, 80, 3));
        rounds.add(build(GNAS, context.getString(R.string.half_metric_v), CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                Target.WA_10_1, 6, 30, 122, 3, 20, 122, 3, 15, 80, 3, 10, 80, 3));
        rounds.add(
                build(GNAS, context.getString(R.string.york), CAT_OUTDOOR, Dimension.YARDS,
                        Dimension.CENTIMETER, Target.GNAS_9_1,
                        6, 100, 122, 12, 80, 122, 8, 60, 122, 4));
        rounds.add(build(GNAS, context.getString(R.string.hereford), CAT_OUTDOOR, Dimension.YARDS,
                Dimension.CENTIMETER,
                Target.GNAS_9_1, 6, 80, 122, 12, 60, 122, 8, 50, 122, 4));
        rounds.add(build(GNAS, context.getString(R.string.bristol_i), CAT_OUTDOOR, Dimension.YARDS,
                Dimension.CENTIMETER,
                Target.GNAS_9_1, 6, 80, 122, 12, 60, 122, 8, 50, 122, 4));
        rounds.add(
                build(GNAS, context.getString(R.string.bristol_ii), CAT_OUTDOOR, Dimension.YARDS,
                        Dimension.CENTIMETER,
                        Target.GNAS_9_1, 6, 60, 122, 12, 50, 122, 8, 40, 122, 4));
        rounds.add(
                build(GNAS, context.getString(R.string.bristol_iii), CAT_OUTDOOR, Dimension.YARDS,
                        Dimension.CENTIMETER,
                        Target.GNAS_9_1, 6, 50, 122, 12, 40, 122, 8, 30, 122, 4));
        rounds.add(
                build(GNAS, context.getString(R.string.bristol_iv), CAT_OUTDOOR, Dimension.YARDS,
                        Dimension.CENTIMETER,
                        Target.GNAS_9_1, 6, 40, 122, 12, 30, 122, 8, 20, 122, 4));
        rounds.add(build(GNAS, context.getString(R.string.bristol_v), CAT_OUTDOOR, Dimension.YARDS,
                Dimension.CENTIMETER,
                Target.GNAS_9_1, 6, 30, 122, 12, 20, 122, 8, 10, 122, 4));
        rounds.add(build(GNAS, context.getString(R.string.st_george), CAT_OUTDOOR, Dimension.YARDS,
                Dimension.CENTIMETER,
                Target.GNAS_9_1, 6, 100, 122, 6, 80, 122, 6, 60, 122, 6));
        rounds.add(build(GNAS, context.getString(R.string.albion), CAT_OUTDOOR, Dimension.YARDS,
                Dimension.CENTIMETER,
                Target.GNAS_9_1, 6, 80, 122, 6, 60, 122, 6, 50, 122, 6));
        rounds.add(build(GNAS, context.getString(R.string.windsor), CAT_OUTDOOR, Dimension.YARDS,
                Dimension.CENTIMETER,
                Target.GNAS_9_1, 6, 60, 122, 6, 50, 122, 6, 40, 122, 6));
        rounds.add(build(GNAS, context.getString(R.string.short_windsor), CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                Target.GNAS_9_1, 6, 50, 122, 6, 40, 122, 6, 30, 122, 6));
        rounds.add(build(GNAS, context.getString(R.string.junior_windsor), CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                Target.GNAS_9_1, 6, 40, 122, 6, 30, 122, 6, 20, 122, 6));
        rounds.add(
                build(GNAS, context.getString(R.string.short_junior_windsor), CAT_OUTDOOR,
                        Dimension.YARDS, Dimension.CENTIMETER,
                        Target.GNAS_9_1, 6, 30, 122, 6, 20,
                        122, 6, 10, 122, 6));
        rounds.add(
                build(GNAS, context.getString(R.string.new_western), CAT_OUTDOOR, Dimension.YARDS,
                        Dimension.CENTIMETER,
                        Target.GNAS_9_1, 6, 100, 122, 8, 80, 122, 8));
        rounds.add(
                build(FITA, context.getString(R.string.fita_gents), CAT_OUTDOOR, Dimension.METER,
                        Dimension.CENTIMETER,
                        Target.WA_10_1, 6, 90, 122, 6, 70,
                        122, 6, 50, 80, 6, 30, 80, 6));
        rounds.add(
                build(GNAS, context.getString(R.string.long_western), CAT_OUTDOOR, Dimension.YARDS,
                        Dimension.CENTIMETER,
                        Target.GNAS_9_1, 6, 80, 122, 8, 60, 122, 8));
        rounds.add(build(GNAS, context.getString(R.string.western), CAT_OUTDOOR, Dimension.YARDS,
                Dimension.CENTIMETER,
                Target.GNAS_9_1, 6, 60, 122, 8, 50, 122, 8));
        rounds.add(build(GNAS, context.getString(R.string.short_western), CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                Target.GNAS_9_1, 6, 50, 122, 8, 40, 122, 8));
        rounds.add(build(GNAS, context.getString(R.string.junior_western), CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                Target.GNAS_9_1, 6, 40, 122, 8, 30, 122, 8));
        rounds.add(
                build(GNAS, context.getString(R.string.short_junior_western), CAT_OUTDOOR,
                        Dimension.YARDS, Dimension.CENTIMETER,
                        Target.GNAS_9_1, 6, 30, 122, 8, 20, 122, 8));
        rounds.add(build(GNAS, context.getString(R.string.american), CAT_OUTDOOR, Dimension.YARDS,
                Dimension.CENTIMETER,
                Target.GNAS_9_1, 6, 60, 122, 5, 50, 122, 5, 40, 122, 5));
        rounds.add(
                build(GNAS, context.getString(R.string.st_nicholas), CAT_OUTDOOR, Dimension.YARDS,
                        Dimension.CENTIMETER,
                        Target.GNAS_9_1, 6, 40, 122, 8, 30, 122, 6));
        rounds.add(
                build(GNAS, context.getString(R.string.new_national), CAT_OUTDOOR, Dimension.YARDS,
                        Dimension.CENTIMETER,
                        Target.GNAS_9_1, 6, 100, 122, 8, 80, 122, 4));
        rounds.add(build(GNAS, context.getString(R.string.long_national), CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                Target.GNAS_9_1, 6, 80, 122, 8, 60, 122, 4));
        rounds.add(build(GNAS, context.getString(R.string.national), CAT_OUTDOOR, Dimension.YARDS,
                Dimension.CENTIMETER,
                Target.GNAS_9_1, 6, 60, 122, 8, 50, 122, 4));
        rounds.add(build(GNAS, context.getString(R.string.short_national), CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                Target.GNAS_9_1, 6, 50, 122, 8, 40, 122, 4));
        rounds.add(build(GNAS, context.getString(R.string.junior_national), CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                Target.GNAS_9_1, 6, 40, 122, 8, 30, 122, 4));
        rounds.add(
                build(GNAS, context.getString(R.string.short_junior_national), CAT_OUTDOOR,
                        Dimension.YARDS, Dimension.CENTIMETER,
                        Target.GNAS_9_1, 6, 30, 122, 8, 20, 122, 4));
        rounds.add(
                build(GNAS, context.getString(R.string.new_warwick), CAT_OUTDOOR, Dimension.YARDS,
                        Dimension.CENTIMETER,
                        Target.GNAS_9_1, 6, 100, 122, 4, 80, 122, 4));
        rounds.add(
                build(GNAS, context.getString(R.string.long_warwick), CAT_OUTDOOR, Dimension.YARDS,
                        Dimension.CENTIMETER,
                        Target.GNAS_9_1, 6, 80, 122, 4, 60, 122, 4));
        rounds.add(build(GNAS, context.getString(R.string.warwick), CAT_OUTDOOR, Dimension.YARDS,
                Dimension.CENTIMETER,
                Target.GNAS_9_1, 6, 60, 122, 4, 50, 122, 4));
        rounds.add(build(GNAS, context.getString(R.string.short_warwick), CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                Target.GNAS_9_1, 6, 50, 122, 4, 40, 122, 4));
        rounds.add(build(GNAS, context.getString(R.string.junior_warwick), CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                Target.GNAS_9_1, 6, 40, 122, 4, 30, 122, 4));
        rounds.add(
                build(GNAS, context.getString(R.string.short_junior_warwick), CAT_OUTDOOR,
                        Dimension.YARDS, Dimension.CENTIMETER,
                        Target.GNAS_9_1, 6, 30, 122, 4, 20, 122, 4));
        rounds.add(
                build(GNAS, context.getString(R.string.bray_i), CAT_INDOOR, Dimension.YARDS,
                        Dimension.CENTIMETER, Target.WA_10_1,
                        6, 20, 40, 5));
        rounds.add(build(GNAS, context.getString(R.string.bray_ii), CAT_INDOOR, Dimension.YARDS,
                Dimension.CENTIMETER,
                Target.WA_10_1, 6, 25, 60, 5));
        rounds.add(build(GNAS, context.getString(R.string.portsmouth), CAT_INDOOR, Dimension.YARDS,
                Dimension.CENTIMETER,
                Target.WA_10_1, 6, 20, 60, 10));
        rounds.add(build(GNAS, context.getString(R.string.worcester), CAT_INDOOR, Dimension.YARDS,
                Dimension.INCH,
                Target.TARGET_5_1, 5, 20, 16, 12));
        rounds.add(build(GNAS, context.getString(R.string.stafford), CAT_INDOOR, Dimension.METER,
                Dimension.CENTIMETER,
                Target.WA_10_1, 6, 30, 80, 12));
        rounds.add(
                build(GNAS, context.getString(R.string.vegas), CAT_INDOOR, Dimension.METER,
                        Dimension.CENTIMETER, Target.WA_10_6,
                        6, 18, 40, 10));
        rounds.add(
                build(FITA, context.getString(R.string.fita_ladies), CAT_OUTDOOR, Dimension.METER,
                        Dimension.CENTIMETER,
                        Target.WA_10_1, 6, 70, 122, 6, 60,
                        122, 6, 50, 80, 6, 30, 80, 6));
        rounds.add(
                build(FITA, context.getString(R.string.fita_cadet_ladies), CAT_OUTDOOR,
                        Dimension.METER, Dimension.CENTIMETER,
                        Target.WA_10_1, 6, 60, 122, 6,
                        50, 122, 6, 40, 80, 6, 30, 80, 6));
        rounds.add(build(FITA, context.getString(R.string.half_fita_gents), CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                Target.WA_10_1, 6, 90, 122, 3,
                70, 122, 3, 50, 80, 3, 30, 80, 3));
        rounds.add(
                build(FITA, context.getString(R.string.half_fita_ladies), CAT_OUTDOOR,
                        Dimension.METER, Dimension.CENTIMETER,
                        Target.WA_10_1, 6, 70, 122, 3,
                        60, 122, 3, 50, 80, 3, 30, 80, 3));
        rounds.add(build(FITA, context.getString(R.string.half_fita_cadet_ladies), CAT_OUTDOOR,
                Dimension.METER,
                Dimension.METER, Target.WA_10_1, 6, 60,
                122, 3, 50, 122, 3, 40, 80, 3, 30, 80, 3));
        rounds.add(build(FITA, context.getString(R.string.fita_900), CAT_OUTDOOR, Dimension.METER,
                Dimension.CENTIMETER,
                Target.WA_10_1, 6, 60, 122, 5, 50, 122, 5,
                40, 122, 5));
        rounds.add(build(FITA, context.getString(R.string.fita_70), CAT_OUTDOOR, Dimension.METER,
                Dimension.CENTIMETER,
                Target.WA_10_1, 6, 70, 122, 12));
        rounds.add(build(FITA, context.getString(R.string.fita_60), CAT_OUTDOOR, Dimension.METER,
                Dimension.CENTIMETER,
                Target.WA_10_1, 6, 60, 122, 12));
        rounds.add(build(FITA, context.getString(R.string.fita_standard), CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                Target.WA_10_1, 6, 50, 122, 6, 30,
                122, 6));
        rounds.add(
                build(FITA, context.getString(R.string.olympic_round), CAT_OUTDOOR, Dimension.METER,
                        Dimension.CENTIMETER,
                        Target.WA_10_1, 3, 70, 122, 4));
        rounds.add(build(FITA, context.getString(R.string.fita_18), CAT_INDOOR, Dimension.METER,
                Dimension.CENTIMETER,
                Target.WA_10_6, 6, 18, 40, 10));
        rounds.add(build(FITA, context.getString(R.string.fita_25), CAT_INDOOR, Dimension.METER,
                Dimension.CENTIMETER,
                Target.WA_10_1, 6, 25, 60, 10));
        rounds.add(
                build(FITA, context.getString(R.string.combined_fita), CAT_INDOOR, Dimension.METER,
                        Dimension.CENTIMETER,
                        Target.WA_10_1, 6, 25, 60, 10, 18, 40, 10));
        rounds.add(build(FITA, context.getString(R.string.match_round), CAT_INDOOR, Dimension.METER,
                Dimension.CENTIMETER,
                Target.WA_10_6, 3, 18, 40, 4));
        rounds.add(build(GNAS, context.getString(R.string.metric_i), CAT_OUTDOOR, Dimension.METER,
                Dimension.CENTIMETER,
                Target.WA_10_1, 6, 70, 122, 6, 60, 122, 6, 50, 80, 6, 30, 80, 6));
        rounds.add(build(GNAS, context.getString(R.string.metric_ii), CAT_OUTDOOR, Dimension.METER,
                Dimension.CENTIMETER,
                Target.WA_10_1, 6, 60, 122, 6, 50, 122, 6, 40, 80, 6, 30, 80, 6));
        rounds.add(build(GNAS, context.getString(R.string.metric_iii), CAT_OUTDOOR, Dimension.METER,
                Dimension.CENTIMETER,
                Target.WA_10_1, 6, 50, 122, 6, 40, 122, 6, 30, 80, 6, 20, 80, 6));
        rounds.add(build(GNAS, context.getString(R.string.metric_iv), CAT_OUTDOOR, Dimension.METER,
                Dimension.CENTIMETER,
                Target.WA_10_1, 6, 40, 122, 6, 30, 122, 6, 20, 80, 6, 10, 80, 6));
        rounds.add(build(GNAS, context.getString(R.string.metric_v), CAT_OUTDOOR, Dimension.METER,
                Dimension.CENTIMETER,
                Target.WA_10_1, 6, 30, 122, 6, 20, 122, 6, 15, 80, 6, 10, 80, 6));
        rounds.add(
                build(GNAS, context.getString(R.string.long_metric_gents), CAT_OUTDOOR,
                        Dimension.METER,
                        Dimension.CENTIMETER,
                        Target.WA_10_1, 6, 90, 122, 6, 70, 122, 6));
        rounds.add(
                build(GNAS, context.getString(R.string.long_metric_ladies), CAT_OUTDOOR,
                        Dimension.METER,
                        Dimension.CENTIMETER,
                        Target.WA_10_1, 6, 70, 122, 6, 60, 122, 6));
        rounds.add(
                build(GNAS, context.getString(R.string.long_metric_i), CAT_OUTDOOR, Dimension.METER,
                        Dimension.CENTIMETER,
                        Target.WA_10_1, 6, 70, 122, 6, 60, 122, 6));
        rounds.add(
                build(GNAS, context.getString(R.string.long_metric_ii), CAT_OUTDOOR,
                        Dimension.METER, Dimension.CENTIMETER,
                        Target.WA_10_1, 6, 60, 122, 6, 50, 122, 6));
        rounds.add(
                build(GNAS, context.getString(R.string.long_metric_iii), CAT_OUTDOOR,
                        Dimension.METER, Dimension.CENTIMETER,
                        Target.WA_10_1, 6, 50, 122, 6, 40, 122, 6));
        rounds.add(
                build(GNAS, context.getString(R.string.long_metric_iv), CAT_OUTDOOR,
                        Dimension.METER, Dimension.CENTIMETER,
                        Target.WA_10_1, 6, 40, 122, 6, 30, 122, 6));
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
    public static StandardRound build(int institution, String name, boolean indoor, String distanceUnit, String targetUnit, int target, int arrowsPerPasse, int... roundDetails) {
        StandardRound standardRound = new StandardRound();
        standardRound.name = name;
        standardRound.indoor = indoor;
        standardRound.institution = institution;
        for (int i = 0; i < roundDetails.length; i += 3) {
            RoundTemplate roundTemplate = new RoundTemplate();
            roundTemplate.arrowsPerPasse = arrowsPerPasse;
            roundTemplate.distance = new Distance(roundDetails[i], distanceUnit);
            roundTemplate.target = target;
            roundTemplate.targetSize = new Dimension(roundDetails[i + 1], targetUnit);
            roundTemplate.passes = roundDetails[i + 2];
            standardRound.insert(roundTemplate);
        }
        return standardRound;
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
        return name;
    }
}
