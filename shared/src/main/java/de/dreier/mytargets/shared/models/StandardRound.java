/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.models.target.CombinedSpot;
import de.dreier.mytargets.shared.models.target.DAIR3D;
import de.dreier.mytargets.shared.models.target.IFAAAnimal;
import de.dreier.mytargets.shared.models.target.NFAAAnimal;
import de.dreier.mytargets.shared.models.target.NFAAExpertField;
import de.dreier.mytargets.shared.models.target.NFAAField;
import de.dreier.mytargets.shared.models.target.NFAAHunter;
import de.dreier.mytargets.shared.models.target.NFAAIndoor;
import de.dreier.mytargets.shared.models.target.NFASField;
import de.dreier.mytargets.shared.models.target.Target;
import de.dreier.mytargets.shared.models.target.TargetFactory;
import de.dreier.mytargets.shared.models.target.Vertical3Spot;
import de.dreier.mytargets.shared.models.target.WA5RingTarget;
import de.dreier.mytargets.shared.models.target.WA6RingTarget;
import de.dreier.mytargets.shared.models.target.WAField;
import de.dreier.mytargets.shared.models.target.WAFullTarget;
import de.dreier.mytargets.shared.models.target.Worcester;

public class StandardRound extends IdProvider {
    static final long serialVersionUID = 56L;
    private static final int ASA = 1;
    private static final int AUSTRALIAN = 2;
    private static final int GNAS = 4;
    public static final int IFAA = 8;
    private static final int NASP = 16;
    private static final int NFAA = 32;
    private static final int NFAS = 64;
    private static final int WA = 128;
    public static final int CUSTOM = 256;
    public static final int CUSTOM_PRACTICE = 512;

    private static final boolean CAT_INDOOR = true;
    private static final boolean CAT_OUTDOOR = false;

    public String name;
    public int club;
    private ArrayList<RoundTemplate> rounds = new ArrayList<>();
    public boolean indoor;

    public ArrayList<RoundTemplate> getRounds() {
        return rounds;
    }

    public static ArrayList<StandardRound> initTable(Context context) {
        ArrayList<StandardRound> rounds = new ArrayList<>();

        /*
        * 3 arrows = 2 min
        * 4 arrows = 2 min
        * 5 arrows, indoor, gnas = 2 min
        * 5 arrows, indoor, i/nfaa, nasp = 4 min
        * 6 arrows = 4 min
        * */

        // Indoor
        rounds.add(build(context, AUSTRALIAN, R.string.australian_combined_indoor, CAT_INDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA5RingTarget.ID, 0, 3, 18, 40, 10, 25, 60, 10));
        rounds.add(build(context, AUSTRALIAN, R.string.australian_indoor_1, CAT_INDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA5RingTarget.ID, 0, 3, 18, 40, 10));
        rounds.add(build(context, AUSTRALIAN, R.string.australian_indoor_2, CAT_INDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA5RingTarget.ID, 0, 3, 25, 60, 10));
        rounds.add(build(context, ASA, R.string.dair_380, CAT_INDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                DAIR3D.ID, 0, 3, 25, 60, 10));
        rounds.add(build(context, WA, R.string.wa_18_40cm, CAT_INDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA5RingTarget.ID, 0, 3, 18, 40, 20));
        rounds.add(build(context, WA, R.string.wa_18_60cm, CAT_INDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA5RingTarget.ID, 0, 3, 18, 60, 20));
        rounds.add(build(context, WA, R.string.wa_25, CAT_INDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 3, 25, 60, 20));
        rounds.add(build(context, WA, R.string.wa_combined, CAT_INDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 3, 25, 60, 20, 18, 40, 20));
        rounds.add(build(context, WA, R.string.match_round, CAT_INDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                Vertical3Spot.ID, 0, 3, 18, 40, 4));
        rounds.add(build(context, GNAS, R.string.stafford, CAT_INDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 3, 30, 80, 24));
        rounds.add(build(context, GNAS, R.string.bray_i, CAT_INDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 20, 40, 5));
        rounds.add(build(context, GNAS, R.string.bray_ii, CAT_INDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 25, 60, 5));
        rounds.add(build(context, GNAS, R.string.portsmouth, CAT_INDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 3, 20, 60, 20));
        rounds.add(build(context, GNAS, R.string.worcester, CAT_INDOOR,
                Dimension.YARDS, Dimension.INCH,
                Worcester.ID, 0, 5, 20, 16, 12));
        rounds.add(build(context, GNAS, R.string.vegas_300, CAT_INDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WA5RingTarget.ID, 0, 3, 20, 40, 20));
        rounds.add(build(context, IFAA, R.string.ifaa_150_indoor, CAT_INDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WA5RingTarget.ID, 0, 5, 20, 40, 6));
        rounds.add(build(context, IFAA, R.string.ifaa_150_indoor_cub, CAT_INDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WA5RingTarget.ID, 0, 5, 10, 40, 6));
        rounds.add(build(context, NASP, R.string.nasp_300, CAT_INDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA5RingTarget.ID, 0, 5, 10, 80, 3, 15, 80, 3));
        rounds.add(build(context, NFAA, R.string.nfaa_420, CAT_INDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                NFAAIndoor.ID, 2, 5, 10, 40, 12));
        rounds.add(build(context, NFAA, R.string.nfaa_300_cub, CAT_INDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                NFAAIndoor.ID, 2, 5, 20, 40, 12));
        rounds.add(build(context, NFAA, R.string.flint_bowman_indoor, CAT_INDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                NFAAField.ID, 0, 4, -1, -1, 7));

        // WA
        rounds.add(build(context, WA, R.string.wa_50, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 50, 80, 12));
        rounds.add(build(context, WA, R.string.wa_60, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 60, 122, 12));
        rounds.add(build(context, WA, R.string.wa_70, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 70, 122, 12));
        rounds.add(build(context, WA, R.string.wa_900, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 60, 122, 5, 50, 122, 5,
                40, 122, 5));
        rounds.add(build(context, WA, R.string.wa_bowman, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 30, 122, 6, 25, 122, 6, 25, 80, 6, 20, 80, 6));
        rounds.add(build(context, WA, R.string.wa_1440_cadet_men, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 70, 122, 6, 60, 122, 6,
                50, 80, 6, 30, 80, 6));
        rounds.add(build(context, WA, R.string.wa_1440_cadet_women, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 60, 122, 6,
                50, 122, 6, 40, 80, 6, 30, 80, 6));
        rounds.add(build(context, WA, R.string.wa_compound_individual, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA6RingTarget.ID, 0, 3, 50, 80, 5));
        rounds.add(build(context, WA, R.string.wa_compound_qualification, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 50, 80, 12));
        rounds.add(build(context, WA, R.string.wa_cub, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 50, 122, 6, 40, 122, 6, 30, 80,
                6, 20, 80, 6));
        rounds.add(build(context, WA, R.string.wa_standard, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 50, 122, 6, 30, 122, 6));
        rounds.add(build(context, WA, R.string.olympic_round, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 3, 70, 122, 4));

        rounds.add(build(context, WA, R.string.wa_1440_junior_men, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 90, 122, 6, 70, 122, 6, 50, 80, 6, 30, 80, 6));
        rounds.add(build(context, WA, R.string.wa_1440_junior_women, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 70, 122, 6, 60, 122, 6, 50, 80, 6, 30, 80, 6));

        rounds.add(build(context, WA, R.string.wa_1440_men_master_50, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 70, 122, 6, 60, 122, 6, 50, 80, 6, 30, 80, 6));
        rounds.add(build(context, WA, R.string.wa_1440_men_master_60, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 70, 122, 6, 60, 122, 6, 50, 80, 6, 30, 80, 6));
        rounds.add(build(context, WA, R.string.wa_1440_men_master_70, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 60, 122, 6, 50, 122, 6, 40, 80, 6, 30, 80, 6));
        rounds.add(build(context, WA, R.string.wa_1440_men_senior, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 90, 122, 6, 70, 122, 6, 50, 80, 6, 30, 80, 6));
        rounds.add(build(context, WA, R.string.wa_1440_women_master_50, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 60, 122, 6, 50, 122, 6, 40, 80, 6, 30, 80, 6));
        rounds.add(build(context, WA, R.string.wa_1440_women_master_60, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 60, 122, 6, 50, 122, 6, 40, 80, 6, 30, 80, 6));
        rounds.add(build(context, WA, R.string.wa_1440_women_master_70, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 60, 122, 6, 50, 122, 6, 40, 80, 6, 30, 80, 6));
        rounds.add(build(context, WA, R.string.wa_1440_women_senior, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 70, 122, 6, 60, 122, 6, 50, 80, 6, 30, 80, 6));
        rounds.add(build(context, WA, R.string.half_wa_1440_cadet_men, CAT_OUTDOOR,
                Dimension.METER, Dimension.METER,
                WAFullTarget.ID, 0, 6, 70, 122, 3, 60, 122, 3,
                50, 80, 3, 30, 80, 3));
        rounds.add(build(context, WA, R.string.half_wa_1440_cadet_women, CAT_OUTDOOR,
                Dimension.METER, Dimension.METER,
                WAFullTarget.ID, 0, 6, 60, 122, 3, 50, 122, 3,
                40, 80, 3, 30, 80, 3));
        rounds.add(build(context, WA, R.string.half_wa_1440_junior_men, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 90, 122, 3,
                70, 122, 3, 50, 80, 3, 30, 80, 3));
        rounds.add(build(context, WA, R.string.half_wa_1440_junior_women, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 70, 122, 3,
                60, 122, 3, 50, 80, 3, 30, 80, 3));
        rounds.add(build(context, WA, R.string.half_wa_1440_men_master_50, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 70, 122, 3,
                60, 122, 3, 50, 80, 3, 30, 80, 3));
        rounds.add(build(context, WA, R.string.half_wa_1440_men_master_60, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 70, 122, 3,
                60, 122, 3, 50, 80, 3, 30, 80, 3));
        rounds.add(build(context, WA, R.string.half_wa_1440_men_master_70, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 60, 122, 3,
                50, 122, 3, 40, 80, 3, 30, 80, 3));
        rounds.add(build(context, WA, R.string.half_wa_1440_men_senior, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 90, 122, 3,
                70, 122, 3, 50, 80, 3, 30, 80, 3));
        rounds.add(build(context, WA, R.string.half_wa_1440_women_master_50, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 60, 122, 3, 50, 122, 3, 40, 80, 3, 30, 80, 3));
        rounds.add(build(context, WA, R.string.half_wa_1440_women_master_60, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 60, 122, 3, 50, 122, 3, 40, 80, 3, 30, 80, 3));
        rounds.add(build(context, WA, R.string.half_wa_1440_women_master_70, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 60, 122, 3, 50, 122, 3, 40, 80, 3, 30, 80, 3));
        rounds.add(build(context, WA, R.string.half_wa_1440_women_senior, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 70, 122, 3, 60, 122, 3, 50, 80, 3, 30, 80, 3));
        rounds.add(build(context, WA, R.string.wa_double_720_30_80cm, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 30, 80, 12, 30, 80, 12));
        rounds.add(build(context, WA, R.string.wa_double_720_40_80cm, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 40, 80, 12, 40, 80, 12));
        rounds.add(build(context, WA, R.string.wa_double_720_40_122cm, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 40, 122, 12, 40, 122, 12));
        rounds.add(build(context, WA, R.string.wa_double_720_50_80cm, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 50, 80, 12, 50, 80, 12));
        rounds.add(build(context, WA, R.string.wa_double_720_50_122cm, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 50, 122, 12, 50, 122, 12));
        rounds.add(build(context, WA, R.string.wa_double_720_60_122cm, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 60, 122, 12, 60, 122, 12));
        rounds.add(build(context, WA, R.string.wa_double_720_70_122cm, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 70, 122, 12, 70, 122, 12));
        rounds.add(build(context, WA, R.string.wa_individual_compound_eleminiation_18, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 3, 18, 40, 5));
        rounds.add(build(context, WA, R.string.wa_individual_compound_eleminiation_40, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA6RingTarget.ID, 0, 3, 40, 80, 5));
        rounds.add(build(context, WA, R.string.wa_individual_compound_eleminiation_50, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA6RingTarget.ID, 0, 3, 50, 80, 5));
        rounds.add(build(context, WA, R.string.wa_recurve_elimination_18, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 3, 18, 40, 5));
        rounds.add(build(context, WA, R.string.wa_recurve_elimination_50, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA6RingTarget.ID, 0, 3, 50, 122, 5));
        rounds.add(build(context, WA, R.string.wa_recurve_elimination_60, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA6RingTarget.ID, 0, 3, 60, 122, 5));
        rounds.add(build(context, WA, R.string.wa_recurve_elimination_70, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WA6RingTarget.ID, 0, 3, 70, 122, 5));

        // GNAS Imperial
        rounds.add(build(context, GNAS, R.string.albion, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 6, 80, 122, 6, 60, 122, 6, 50, 122, 6));
        rounds.add(build(context, GNAS, R.string.american, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 6, 60, 122, 5, 50, 122, 5, 40, 122, 5));

        rounds.add(build(context, GNAS, R.string.bristol_i, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 6, 80, 122, 12, 60, 122, 8, 50, 122, 4));
        rounds.add(build(context, GNAS, R.string.bristol_ii, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 6, 60, 122, 12, 50, 122, 8, 40, 122, 4));
        rounds.add(build(context, GNAS, R.string.bristol_iii, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 6, 50, 122, 12, 40, 122, 8, 30, 122, 4));
        rounds.add(build(context, GNAS, R.string.bristol_iv, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 6, 40, 122, 12, 30, 122, 8, 20, 122, 4));
        rounds.add(build(context, GNAS, R.string.bristol_v, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 6, 30, 122, 12, 20, 122, 8, 10, 122, 4));

        rounds.add(build(context, GNAS, R.string.hereford, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 6, 80, 122, 12, 60, 122, 8, 50, 122, 4));

        rounds.add(build(context, GNAS, R.string.short_junior_national, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 6, 30, 122, 8, 20, 122, 4));
        rounds.add(build(context, GNAS, R.string.short_junior_warwick, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 6, 30, 122, 4, 20, 122, 4));
        rounds.add(build(context, GNAS, R.string.short_junior_western, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 6, 30, 122, 8, 20, 122, 8));
        rounds.add(build(context, GNAS, R.string.short_junior_windsor, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 6, 30, 122, 6, 20,
                122, 6, 10, 122, 6));

        rounds.add(build(context, GNAS, R.string.junior_national, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 6, 40, 122, 8, 30, 122, 4));
        rounds.add(build(context, GNAS, R.string.junior_warwick, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 6, 40, 122, 4, 30, 122, 4));
        rounds.add(build(context, GNAS, R.string.junior_western, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 6, 40, 122, 8, 30, 122, 8));
        rounds.add(build(context, GNAS, R.string.junior_windsor, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 6, 40, 122, 6, 30, 122, 6, 20, 122, 6));

        rounds.add(build(context, GNAS, R.string.short_national, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 6, 50, 122, 8, 40, 122, 4));
        rounds.add(build(context, GNAS, R.string.short_warwick, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 6, 50, 122, 4, 40, 122, 4));
        rounds.add(build(context, GNAS, R.string.short_western, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 6, 50, 122, 8, 40, 122, 8));
        rounds.add(build(context, GNAS, R.string.short_windsor, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 6, 50, 122, 6, 40, 122, 6, 30, 122, 6));

        rounds.add(build(context, GNAS, R.string.national, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 6, 60, 122, 8, 50, 122, 4));
        rounds.add(build(context, GNAS, R.string.st_george, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 6, 100, 122, 6, 80, 122, 6, 60, 122, 6));
        rounds.add(build(context, GNAS, R.string.st_nicholas, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 6, 40, 122, 8, 30, 122, 6));
        rounds.add(build(context, GNAS, R.string.warwick, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 6, 60, 122, 4, 50, 122, 4));
        rounds.add(build(context, GNAS, R.string.western, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 6, 60, 122, 8, 50, 122, 8));
        rounds.add(build(context, GNAS, R.string.windsor, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 6, 60, 122, 6, 50, 122, 6, 40, 122, 6));
        rounds.add(build(context, GNAS, R.string.york, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 6, 100, 122, 12, 80, 122, 8, 60, 122, 4));

        rounds.add(build(context, GNAS, R.string.long_national, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 6, 80, 122, 8, 60, 122, 4));
        rounds.add(build(context, GNAS, R.string.long_warwick, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 6, 80, 122, 4, 60, 122, 4));
        rounds.add(build(context, GNAS, R.string.long_western, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 6, 80, 122, 8, 60, 122, 8));

        rounds.add(build(context, GNAS, R.string.new_national, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 6, 100, 122, 8, 80, 122, 4));
        rounds.add(build(context, GNAS, R.string.new_warwick, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 6, 100, 122, 4, 80, 122, 4));
        rounds.add(build(context, GNAS, R.string.new_western, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 6, 100, 122, 8, 80, 122, 8));

        // GNAS Metric
        rounds.add(build(context, GNAS, R.string.frostbite, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 3, 30, 80, 12));
        rounds.add(build(context, GNAS, R.string.half_metric_i, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 70, 122, 3, 60,
                122, 3, 50, 80, 3, 30, 80, 3));
        rounds.add(build(context, GNAS, R.string.half_metric_ii, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 60, 122, 3, 50, 122, 3, 40, 80, 3, 30, 80, 3));
        rounds.add(build(context, GNAS, R.string.half_metric_iii, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 50, 122, 3, 40, 122, 3, 30, 80, 3, 20, 80, 3));
        rounds.add(build(context, GNAS, R.string.half_metric_iv, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 40, 122, 3, 30, 122, 3, 20, 80, 3, 10, 80, 3));
        rounds.add(build(context, GNAS, R.string.half_metric_v, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 30, 122, 3, 20, 122, 3, 15, 80, 3, 10, 80, 3));

        rounds.add(build(context, GNAS, R.string.short_metric_i, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 50, 80, 6, 30, 80, 6));
        rounds.add(build(context, GNAS, R.string.short_metric_ii, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 40, 80, 6, 30, 80, 6));
        rounds.add(build(context, GNAS, R.string.short_metric_iii, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 30, 80, 6, 20, 80, 6));
        rounds.add(build(context, GNAS, R.string.short_metric_iv, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 20, 80, 6, 10, 80, 6));
        rounds.add(build(context, GNAS, R.string.short_metric_v, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 15, 80, 6, 10, 80, 6));

        rounds.add(build(context, GNAS, R.string.metric_i, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 70, 122, 6, 60, 122, 6, 50, 80, 6, 30, 80, 6));
        rounds.add(build(context, GNAS, R.string.metric_ii, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 60, 122, 6, 50, 122, 6, 40, 80, 6, 30, 80, 6));
        rounds.add(build(context, GNAS, R.string.metric_iii, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 50, 122, 6, 40, 122, 6, 30, 80, 6, 20, 80, 6));
        rounds.add(build(context, GNAS, R.string.metric_iv, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 40, 122, 6, 30, 122, 6, 20, 80, 6, 10, 80, 6));
        rounds.add(build(context, GNAS, R.string.metric_v, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 30, 122, 6, 20, 122, 6, 15, 80, 6, 10, 80, 6));

        rounds.add(build(context, GNAS, R.string.long_metric_i, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 70, 122, 6, 60, 122, 6));
        rounds.add(build(context, GNAS, R.string.long_metric_ii, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 60, 122, 6, 50, 122, 6));
        rounds.add(build(context, GNAS, R.string.long_metric_iii, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 50, 122, 6, 40, 122, 6));
        rounds.add(build(context, GNAS, R.string.long_metric_iv, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 40, 122, 6, 30, 122, 6));
        rounds.add(build(context, GNAS, R.string.long_metric_v, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 30, 122, 6, 20, 122, 6));
        rounds.add(build(context, GNAS, R.string.long_metric_gents, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 90, 122, 6, 70, 122, 6));
        rounds.add(build(context, GNAS, R.string.long_metric_ladies, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 70, 122, 6, 60, 122, 6));

        // Australia
        rounds.add(build(context, AUSTRALIAN, R.string.adelaide, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 60, 122, 5, 50, 122, 5, 40, 80, 5, 30, 80, 5));
        rounds.add(build(context, AUSTRALIAN, R.string.brisbane, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 70, 122, 5, 60, 122, 5, 50, 80, 5, 40, 80, 5));
        rounds.add(build(context, AUSTRALIAN, R.string.canberra, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 60, 122, 5, 50, 122, 5, 40, 122, 5));
        rounds.add(build(context, AUSTRALIAN, R.string.darwin, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 40, 122, 15));
        rounds.add(build(context, AUSTRALIAN, R.string.drake, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 30, 80, 15));
        rounds.add(build(context, AUSTRALIAN, R.string.fremantle, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 60, 122, 6, 50, 122, 6, 40, 80, 6, 30, 80, 6));
        rounds.add(build(context, AUSTRALIAN, R.string.geelong, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 30, 122, 15));
        rounds.add(build(context, AUSTRALIAN, R.string.grange, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 60, 122, 15));
        rounds.add(build(context, AUSTRALIAN, R.string.hobart, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 90, 122, 5, 70, 122, 5, 50, 122, 5));
        rounds.add(build(context, AUSTRALIAN, R.string.holt, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 50, 80, 15));
        rounds.add(build(context, AUSTRALIAN, R.string.horsham, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 40, 122, 6, 35, 122, 6, 30, 80, 6, 25, 80, 6));
        rounds.add(build(context, AUSTRALIAN, R.string.intermediate, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 55, 122, 6, 45, 122, 6, 35, 80, 6, 25, 80, 6));
        rounds.add(build(context, AUSTRALIAN, R.string.junior_canberra, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 40, 122, 5, 30, 122, 5, 20, 122, 5));
        rounds.add(build(context, AUSTRALIAN, R.string.launcheston, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 50, 80, 6, 30, 80, 6));
        rounds.add(build(context, AUSTRALIAN, R.string.long_brisbane, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 90, 122, 5, 70, 122, 5, 60, 80, 5, 50, 80, 5));
        rounds.add(build(context, AUSTRALIAN, R.string.long_sydney, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 90, 122, 5, 70, 122, 5, 60, 122, 5, 50, 122, 5));
        rounds.add(build(context, AUSTRALIAN, R.string.melbourne, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 90, 122, 15));
        rounds.add(build(context, AUSTRALIAN, R.string.newcastle, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 20, 122, 15));
        rounds.add(build(context, AUSTRALIAN, R.string.perth, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 70, 122, 5, 60, 122, 5, 50, 122, 5));
        rounds.add(build(context, AUSTRALIAN, R.string.short_adelaide, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 50, 122, 5, 40, 122, 5, 30, 80, 5, 20, 80, 5));
        rounds.add(build(context, AUSTRALIAN, R.string.short_canberra, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 50, 122, 5, 40, 122, 5, 30, 122, 5));
        rounds.add(build(context, AUSTRALIAN, R.string.sydney, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 70, 122, 5, 60, 122, 5, 50, 122, 5, 40, 122, 5));
        rounds.add(build(context, AUSTRALIAN, R.string.townsville, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 60, 122, 6, 50, 122, 6));
        rounds.add(build(context, AUSTRALIAN, R.string.wollongong, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 90, 122, 6, 70, 122, 6));

        // NFAA
        rounds.add(build(context, NFAA, R.string.nfaa_600, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 5, 60, 122, 4, 50, 122, 4, 40, 122, 4));
        rounds.add(build(context, NFAA, R.string.nfaa_600_classic, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 5, 40, 92, 4, 50, 92, 4, 60, 92, 4));
        rounds.add(build(context, NFAA, R.string.nfaa_600_classic_cub, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 5, 10, 92, 4, 20, 92, 4, 30, 92, 4));
        rounds.add(build(context, NFAA, R.string.nfaa_600_classic_junior, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 5, 30, 92, 4, 40, 92, 4, 50, 92, 4));
        rounds.add(build(context, NFAA, R.string.nfaa_600_cub, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 5, 30, 122, 4, 20, 122, 4, 10, 122, 4));
        rounds.add(build(context, NFAA, R.string.nfaa_600_junior, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 5, 50, 122, 4, 40, 122, 4, 30, 122, 4));
        rounds.add(build(context, NFAA, R.string.nfaa_810, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 5, 60, 122, 6, 50, 122, 6, 40, 122, 6));
        rounds.add(build(context, NFAA, R.string.nfaa_810_cub, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 5, 30, 122, 6, 20, 122, 6, 10, 122, 6));
        rounds.add(build(context, NFAA, R.string.nfaa_810_junior, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 4, 5, 50, 122, 6, 40, 122, 6, 30, 122, 6));
        rounds.add(build(context, NFAA, R.string.nfaa_900, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 5, 60, 122, 6, 50, 122, 6, 40, 122, 6));
        rounds.add(build(context, NFAA, R.string.nfaa_900_cub, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 5, 30, 122, 6, 20, 122, 6, 10, 122, 6));
        rounds.add(build(context, NFAA, R.string.nfaa_900_junior, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 5, 50, 122, 6, 40, 122, 6, 30, 122, 6));

        // Other
        rounds.add(build(context, NFAA, R.string.canadian_900, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 55, 122, 5, 45, 122, 5, 35, 122, 5));
        rounds.add(build(context, NFAA, R.string.t2s_900, CAT_OUTDOOR,
                Dimension.YARDS, Dimension.CENTIMETER,
                WAFullTarget.ID, 0, 6, 35, 80, 5, 30, 80, 5, 25, 80, 5));

        // Field
        rounds.add(build(context, IFAA, R.string.ifaa_animal_280, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                IFAAAnimal.ID, 0, 3, -1, -1, 14));
        rounds.add(build(context, IFAA, R.string.ifaa_animal_300, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                IFAAAnimal.ID, 0, 3, -1, -1, 15));
        rounds.add(build(context, IFAA, R.string.ifaa_animal_560, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                IFAAAnimal.ID, 0, 3, -1, -1, 14, -1, -1, 14));
        rounds.add(build(context, NFAA, R.string.nfaa_international_150, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                NFAAHunter.ID, 0, 3, -1, -1, 10));
        rounds.add(build(context, NFAA, R.string.nfaa_international_300, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                NFAAHunter.ID, 0, 3, -1, -1, 10, -1, -1, 10));
        rounds.add(build(context, NFAA, R.string.nfaa_animal_280, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                NFAAAnimal.ID, 0, 3, -1, -1, 14));
        rounds.add(build(context, NFAA, R.string.nfaa_animal_300, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                NFAAAnimal.ID, 0, 3, -1, -1, 15));
        rounds.add(build(context, NFAA, R.string.nfaa_animal_588, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                NFAAAnimal.ID, 0, 3, -1, -1, 14, -1, -1, 14));
        rounds.add(build(context, NFAA, R.string.nfaa_lake_of_woods, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAFullTarget.ID, 3, 3, -1, -1, 10));
        rounds.add(build(context, NFAA, R.string.nfaa_expert_field_300, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                NFAAExpertField.ID, 0, 4, -1, -1, 15));
        rounds.add(build(context, NFAA, R.string.nfaa_expert_field_only_560, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                NFAAExpertField.ID, 0, 4, -1, -1, 14, -1, -1, 14));
        rounds.add(build(context, NFAA, R.string.nfaa_field_280, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                NFAAField.ID, 0, 4, -1, -1, 14));
        rounds.add(build(context, NFAA, R.string.nfaa_field_300, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                NFAAField.ID, 0, 4, -1, -1, 15));
        rounds.add(build(context, NFAA, R.string.nfaa_field_560, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                NFAAField.ID, 0, 4, -1, -1, 14, -1, -1, 14));
        rounds.add(build(context, NFAA, R.string.nfaa_hunter_280, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                NFAAHunter.ID, 0, 4, -1, -1, 14));
        rounds.add(build(context, NFAA, R.string.nfaa_hunter_300, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                NFAAHunter.ID, 0, 4, -1, -1, 15));
        rounds.add(build(context, NFAA, R.string.nfaa_hunter_560, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                NFAAHunter.ID, 0, 4, -1, -1, 14, -1, -1, 14));
        rounds.add(build(context, NFAA, R.string.nfaa_field_hunter_560, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                NFAAField.ID, 0, TargetFactory.createTarget(context, NFAAHunter.ID, 0), 4, -1, -1,
                14, -1, -1, 14));
        rounds.add(build(context, WA, R.string.wa_field_unmarked_marked_red, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAField.ID, 0, 3, -1, -1, 12, -1, -1, 12));
        rounds.add(build(context, WA, R.string.wa_field_marked_12_red, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAField.ID, 0, 3, -1, -1, 12));
        rounds.add(build(context, WA, R.string.wa_field_marked_24_red, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAField.ID, 0, 3, -1, -1, 24));
        rounds.add(build(context, WA, R.string.wa_field_unmarked_12_red, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAField.ID, 0, 3, -1, -1, 12));
        rounds.add(build(context, WA, R.string.wa_field_unmarked_24_red, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAField.ID, 0, 3, -1, -1, 24));
        rounds.add(build(context, WA, R.string.wa_field_unmarked_marked_blue, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAField.ID, 0, 3, -1, -1, 12, -1, -1, 12));
        rounds.add(build(context, WA, R.string.wa_field_marked_12_blue, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAField.ID, 0, 3, -1, -1, 12));
        rounds.add(build(context, WA, R.string.wa_field_marked_24_blue, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAField.ID, 0, 3, -1, -1, 24));
        rounds.add(build(context, WA, R.string.wa_field_unmarked_12_blue, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAField.ID, 0, 3, -1, -1, 12));
        rounds.add(build(context, WA, R.string.wa_field_unmarked_24_blue, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAField.ID, 0, 3, -1, -1, 24));
        rounds.add(build(context, WA, R.string.wa_field_unmarked_marked_yellow, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAField.ID, 0, 3, -1, -1, 12, -1, -1, 12));
        rounds.add(build(context, WA, R.string.wa_field_marked_12_yellow, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAField.ID, 0, 3, -1, -1, 12));
        rounds.add(build(context, WA, R.string.wa_field_marked_24_yellow, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAField.ID, 0, 3, -1, -1, 24));
        rounds.add(build(context, WA, R.string.wa_field_unmarked_12_yellow, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAField.ID, 0, 3, -1, -1, 12));
        rounds.add(build(context, WA, R.string.wa_field_unmarked_24_yellow, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                WAField.ID, 0, 3, -1, -1, 24));
        rounds.add(build(context, NFAS, R.string.big_game_36, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                NFASField.ID, 0, 3, -1, -1, 24));
        rounds.add(build(context, NFAS, R.string.big_game_40, CAT_OUTDOOR,
                Dimension.METER, Dimension.CENTIMETER,
                NFASField.ID, 0, 3, -1, -1, 40));

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
    public static StandardRound build(Context context, int institution, int name, boolean indoor, String distanceUnit, String targetUnit, int target, int scoringStyle, int arrowsPerPasse, int... roundDetails) {
        StandardRound standardRound = new StandardRound();
        standardRound.name = context.getString(name);
        standardRound.indoor = indoor;
        standardRound.club = institution;
        for (int i = 0; i < roundDetails.length; i += 3) {
            RoundTemplate roundTemplate = new RoundTemplate();
            roundTemplate.arrowsPerPasse = arrowsPerPasse;
            roundTemplate.distance = new Distance(roundDetails[i], distanceUnit);
            roundTemplate.targetTemplate = TargetFactory
                    .createTarget(context, target, scoringStyle);
            roundTemplate.targetTemplate.size = new Diameter(roundDetails[i + 1], targetUnit);
            roundTemplate.passes = roundDetails[i + 2];
            standardRound.insert(roundTemplate);
        }
        return standardRound;
    }

    public static StandardRound build(Context context, int institution, int name, boolean indoor, String distanceUnit, String targetUnit, int target, int scoringStyle, Target target2, int arrowsPerPasse, int... roundDetails) {
        StandardRound standardRound = build(context, institution, name, indoor, distanceUnit,
                targetUnit, target, scoringStyle, arrowsPerPasse, roundDetails);
        RoundTemplate round2 = standardRound.getRounds().get(1);
        target2.size = round2.targetTemplate.size;
        round2.targetTemplate = target2;
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
            desc += context.getString(R.string.round_desc, r.distance.toString(context), r.passes,
                    r.arrowsPerPasse, r.target.size.toString(context));
        }
        return desc;
    }

    public void setRounds(ArrayList<RoundTemplate> rounds) {
        this.rounds = rounds;
    }

    public Drawable getTargetDrawable(Context context) {
        List<Target> targets = new ArrayList<>();
        for(RoundTemplate r: rounds) {
            targets.add(r.target);
        }
        return new CombinedSpot(context, targets);
    }
}
