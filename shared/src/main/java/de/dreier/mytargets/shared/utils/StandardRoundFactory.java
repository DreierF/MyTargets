/*
 * Copyright (C) 2016 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.shared.utils;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.SharedApplicationInstance;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.db.RoundTemplate;
import de.dreier.mytargets.shared.models.db.StandardRound;
import de.dreier.mytargets.shared.targets.models.DAIR3D;
import de.dreier.mytargets.shared.targets.models.IFAAAnimal;
import de.dreier.mytargets.shared.targets.models.NFAAAnimal;
import de.dreier.mytargets.shared.targets.models.NFAAExpertField;
import de.dreier.mytargets.shared.targets.models.NFAAField;
import de.dreier.mytargets.shared.targets.models.NFAAHunter;
import de.dreier.mytargets.shared.targets.models.NFAAIndoor;
import de.dreier.mytargets.shared.targets.models.NFASField;
import de.dreier.mytargets.shared.targets.models.Vertical3Spot;
import de.dreier.mytargets.shared.targets.models.WA5Ring;
import de.dreier.mytargets.shared.targets.models.WA6Ring;
import de.dreier.mytargets.shared.targets.models.WAField;
import de.dreier.mytargets.shared.targets.models.WAFull;
import de.dreier.mytargets.shared.targets.models.Worcester;

import static de.dreier.mytargets.shared.models.Dimension.Unit.CENTIMETER;
import static de.dreier.mytargets.shared.models.Dimension.Unit.INCH;
import static de.dreier.mytargets.shared.models.Dimension.Unit.METER;
import static de.dreier.mytargets.shared.models.Dimension.Unit.YARDS;

public class StandardRoundFactory {
    public static final int IFAA = 8;
    public static final int CUSTOM = 256;
    public static final int CUSTOM_PRACTICE = 512;
    private static final int ASA = 1;
    private static final int AUSTRALIAN = 2;
    private static final int ARCHERY_GB = 4;
    private static final int NASP = 16;
    private static final int NFAA = 32;
    private static final int NFAS = 64;
    private static final int WA = 128;
    private static final boolean CAT_INDOOR = true;
    private static final boolean CAT_OUTDOOR = false;
    private static long idCounter;
    private static long roundCounter;

    public static List<StandardRound> initTable() {
        List<StandardRound> rounds = new ArrayList<>();

            /*
            * 3 arrows = 2 min
            * 4 arrows = 2 min
            * 5 arrows, indoor, gnas = 2 min
            * 5 arrows, indoor, i/nfaa, nasp = 4 min
            * 6 arrows = 4 min
            * */

        idCounter = 0;
        roundCounter = 0;

        // Indoor
        rounds.add(build(AUSTRALIAN, R.string.australian_combined_indoor, CAT_INDOOR,
                METER, CENTIMETER,
                WA5Ring.ID, 0, 3, 18, 40, 10, 25, 60, 10));
        rounds.add(build(AUSTRALIAN, R.string.australian_indoor_1, CAT_INDOOR,
                METER, CENTIMETER,
                WA5Ring.ID, 0, 3, 18, 40, 10));
        rounds.add(build(AUSTRALIAN, R.string.australian_indoor_2, CAT_INDOOR,
                METER, CENTIMETER,
                WA5Ring.ID, 0, 3, 25, 60, 10));
        rounds.add(build(ASA, R.string.dair_380, CAT_INDOOR,
                METER, CENTIMETER,
                DAIR3D.ID, 0, 3, 25, 60, 10));
        rounds.add(build(WA, R.string.wa_18_40cm, CAT_INDOOR,
                METER, CENTIMETER,
                WA5Ring.ID, 0, 3, 18, 40, 20));
        rounds.add(build(WA, R.string.wa_18_60cm, CAT_INDOOR,
                METER, CENTIMETER,
                WA5Ring.ID, 0, 3, 18, 60, 20));
        rounds.add(build(WA, R.string.wa_25, CAT_INDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 3, 25, 60, 20));
        rounds.add(build(WA, R.string.wa_combined, CAT_INDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 3, 25, 60, 20, 18, 40, 20));
        rounds.add(build(WA, R.string.match_round, CAT_INDOOR,
                METER, CENTIMETER,
                Vertical3Spot.ID, 0, 3, 18, 40, 4));
        rounds.add(build(ARCHERY_GB, R.string.stafford, CAT_INDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 3, 30, 80, 24));
        rounds.add(build(ARCHERY_GB, R.string.bray_i, CAT_INDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 0, 6, 20, 40, 5));
        rounds.add(build(ARCHERY_GB, R.string.bray_ii, CAT_INDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 0, 6, 25, 60, 5));
        rounds.add(build(ARCHERY_GB, R.string.portsmouth, CAT_INDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 0, 3, 20, 60, 20));
        rounds.add(build(ARCHERY_GB, R.string.worcester, CAT_INDOOR,
                YARDS, INCH,
                Worcester.ID, 0, 5, 20, 16, 12));
        rounds.add(build(ARCHERY_GB, R.string.vegas_300, CAT_INDOOR,
                YARDS, CENTIMETER,
                WA5Ring.ID, 0, 3, 20, 40, 20));
        rounds.add(build(IFAA, R.string.ifaa_150_indoor, CAT_INDOOR,
                YARDS, CENTIMETER,
                WA5Ring.ID, 0, 5, 20, 40, 6));
        rounds.add(build(IFAA, R.string.ifaa_150_indoor_cub, CAT_INDOOR,
                YARDS, CENTIMETER,
                WA5Ring.ID, 0, 5, 10, 40, 6));
        rounds.add(build(NASP, R.string.nasp_300, CAT_INDOOR,
                METER, CENTIMETER,
                WA5Ring.ID, 0, 5, 10, 80, 3, 15, 80, 3));
        rounds.add(build(NFAA, R.string.nfaa_420, CAT_INDOOR,
                YARDS, CENTIMETER,
                NFAAIndoor.ID, 2, 5, 10, 40, 12));
        rounds.add(build(NFAA, R.string.nfaa_300_cub, CAT_INDOOR,
                YARDS, CENTIMETER,
                NFAAIndoor.ID, 2, 5, 20, 40, 12));
        rounds.add(build(NFAA, R.string.flint_bowman_indoor, CAT_INDOOR,
                YARDS, CENTIMETER,
                NFAAField.ID, 0, 4, -1, -1, 7));

        // WA
        rounds.add(build(WA, R.string.wa_50, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 50, 80, 12));
        rounds.add(build(WA, R.string.wa_60, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 12));
        rounds.add(build(WA, R.string.wa_70, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 12));
        rounds.add(build(WA, R.string.wa_900, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 5, 50, 122, 5,
                40, 122, 5));
        rounds.add(build(WA, R.string.wa_bowman, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 30, 122, 6, 25, 122, 6, 25, 80, 6, 20, 80, 6));
        rounds.add(build(WA, R.string.wa_1440_cadet_men, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 6, 60, 122, 6,
                50, 80, 6, 30, 80, 6));
        rounds.add(build(WA, R.string.wa_1440_cadet_women, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 6,
                50, 122, 6, 40, 80, 6, 30, 80, 6));
        rounds.add(build(WA, R.string.wa_compound_individual, CAT_OUTDOOR,
                METER, CENTIMETER,
                WA6Ring.ID, 0, 3, 50, 80, 5));
        rounds.add(build(WA, R.string.wa_compound_qualification, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 50, 80, 12));
        rounds.add(build(WA, R.string.wa_cub, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 50, 122, 6, 40, 122, 6, 30, 80,
                6, 20, 80, 6));
        rounds.add(build(WA, R.string.wa_standard, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 50, 122, 6, 30, 122, 6));
        rounds.add(build(WA, R.string.olympic_round, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 3, 70, 122, 4));

        rounds.add(build(WA, R.string.wa_1440_junior_men, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 90, 122, 6, 70, 122, 6, 50, 80, 6, 30, 80, 6));
        rounds.add(build(WA, R.string.wa_1440_junior_women, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 6, 60, 122, 6, 50, 80, 6, 30, 80, 6));

        rounds.add(build(WA, R.string.wa_1440_men_master_50, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 6, 60, 122, 6, 50, 80, 6, 30, 80, 6));
        rounds.add(build(WA, R.string.wa_1440_men_master_60, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 6, 60, 122, 6, 50, 80, 6, 30, 80, 6));
        rounds.add(build(WA, R.string.wa_1440_men_master_70, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 6, 50, 122, 6, 40, 80, 6, 30, 80, 6));
        rounds.add(build(WA, R.string.wa_1440_men_senior, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 90, 122, 6, 70, 122, 6, 50, 80, 6, 30, 80, 6));
        rounds.add(build(WA, R.string.wa_1440_women_master_50, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 6, 50, 122, 6, 40, 80, 6, 30, 80, 6));
        rounds.add(build(WA, R.string.wa_1440_women_master_60, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 6, 50, 122, 6, 40, 80, 6, 30, 80, 6));
        rounds.add(build(WA, R.string.wa_1440_women_master_70, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 6, 50, 122, 6, 40, 80, 6, 30, 80, 6));
        rounds.add(build(WA, R.string.wa_1440_women_senior, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 6, 60, 122, 6, 50, 80, 6, 30, 80, 6));
        rounds.add(build(WA, R.string.half_wa_1440_cadet_men, CAT_OUTDOOR,
                METER, METER,
                WAFull.ID, 0, 6, 70, 122, 3, 60, 122, 3,
                50, 80, 3, 30, 80, 3));
        rounds.add(build(WA, R.string.half_wa_1440_cadet_women, CAT_OUTDOOR,
                METER, METER,
                WAFull.ID, 0, 6, 60, 122, 3, 50, 122, 3,
                40, 80, 3, 30, 80, 3));
        rounds.add(build(WA, R.string.half_wa_1440_junior_men, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 90, 122, 3,
                70, 122, 3, 50, 80, 3, 30, 80, 3));
        rounds.add(build(WA, R.string.half_wa_1440_junior_women, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 3,
                60, 122, 3, 50, 80, 3, 30, 80, 3));
        rounds.add(build(WA, R.string.half_wa_1440_men_master_50, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 3,
                60, 122, 3, 50, 80, 3, 30, 80, 3));
        rounds.add(build(WA, R.string.half_wa_1440_men_master_60, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 3,
                60, 122, 3, 50, 80, 3, 30, 80, 3));
        rounds.add(build(WA, R.string.half_wa_1440_men_master_70, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 3,
                50, 122, 3, 40, 80, 3, 30, 80, 3));
        rounds.add(build(WA, R.string.half_wa_1440_men_senior, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 90, 122, 3,
                70, 122, 3, 50, 80, 3, 30, 80, 3));
        rounds.add(build(WA, R.string.half_wa_1440_women_master_50, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 3, 50, 122, 3, 40, 80, 3, 30, 80, 3));
        rounds.add(build(WA, R.string.half_wa_1440_women_master_60, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 3, 50, 122, 3, 40, 80, 3, 30, 80, 3));
        rounds.add(build(WA, R.string.half_wa_1440_women_master_70, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 3, 50, 122, 3, 40, 80, 3, 30, 80, 3));
        rounds.add(build(WA, R.string.half_wa_1440_women_senior, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 3, 60, 122, 3, 50, 80, 3, 30, 80, 3));
        rounds.add(build(WA, R.string.wa_double_720_30_80cm, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 30, 80, 12, 30, 80, 12));
        rounds.add(build(WA, R.string.wa_double_720_40_80cm, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 40, 80, 12, 40, 80, 12));
        rounds.add(build(WA, R.string.wa_double_720_40_122cm, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 40, 122, 12, 40, 122, 12));
        rounds.add(build(WA, R.string.wa_double_720_50_80cm, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 50, 80, 12, 50, 80, 12));
        rounds.add(build(WA, R.string.wa_double_720_50_122cm, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 50, 122, 12, 50, 122, 12));
        rounds.add(build(WA, R.string.wa_double_720_60_122cm, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 12, 60, 122, 12));
        rounds.add(build(WA, R.string.wa_double_720_70_122cm, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 12, 70, 122, 12));
        rounds.add(build(WA, R.string.wa_individual_compound_eleminiation_18, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 3, 18, 40, 5));
        rounds.add(build(WA, R.string.wa_individual_compound_eleminiation_40, CAT_OUTDOOR,
                METER, CENTIMETER,
                WA6Ring.ID, 0, 3, 40, 80, 5));
        rounds.add(build(WA, R.string.wa_individual_compound_eleminiation_50, CAT_OUTDOOR,
                METER, CENTIMETER,
                WA6Ring.ID, 0, 3, 50, 80, 5));
        rounds.add(build(WA, R.string.wa_recurve_elimination_18, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 3, 18, 40, 5));
        rounds.add(build(WA, R.string.wa_recurve_elimination_50, CAT_OUTDOOR,
                METER, CENTIMETER,
                WA6Ring.ID, 0, 3, 50, 122, 5));
        rounds.add(build(WA, R.string.wa_recurve_elimination_60, CAT_OUTDOOR,
                METER, CENTIMETER,
                WA6Ring.ID, 0, 3, 60, 122, 5));
        rounds.add(build(WA, R.string.wa_recurve_elimination_70, CAT_OUTDOOR,
                METER, CENTIMETER,
                WA6Ring.ID, 0, 3, 70, 122, 5));

        // ARCHERY_GB Imperial
        rounds.add(build(ARCHERY_GB, R.string.albion, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 6, 80, 122, 6, 60, 122, 6, 50, 122, 6));
        rounds.add(build(ARCHERY_GB, R.string.american, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 6, 60, 122, 5, 50, 122, 5, 40, 122, 5));

        rounds.add(build(ARCHERY_GB, R.string.bristol_i, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 6, 80, 122, 12, 60, 122, 8, 50, 122, 4));
        rounds.add(build(ARCHERY_GB, R.string.bristol_ii, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 6, 60, 122, 12, 50, 122, 8, 40, 122, 4));
        rounds.add(build(ARCHERY_GB, R.string.bristol_iii, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 6, 50, 122, 12, 40, 122, 8, 30, 122, 4));
        rounds.add(build(ARCHERY_GB, R.string.bristol_iv, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 6, 40, 122, 12, 30, 122, 8, 20, 122, 4));
        rounds.add(build(ARCHERY_GB, R.string.bristol_v, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 6, 30, 122, 12, 20, 122, 8, 10, 122, 4));

        rounds.add(build(ARCHERY_GB, R.string.hereford, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 6, 80, 122, 12, 60, 122, 8, 50, 122, 4));

        rounds.add(build(ARCHERY_GB, R.string.short_junior_national, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 6, 30, 122, 8, 20, 122, 4));
        rounds.add(build(ARCHERY_GB, R.string.short_junior_warwick, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 6, 30, 122, 4, 20, 122, 4));
        rounds.add(build(ARCHERY_GB, R.string.short_junior_western, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 6, 30, 122, 8, 20, 122, 8));
        rounds.add(build(ARCHERY_GB, R.string.short_junior_windsor, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 6, 30, 122, 6, 20,
                122, 6, 10, 122, 6));

        rounds.add(build(ARCHERY_GB, R.string.junior_national, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 6, 40, 122, 8, 30, 122, 4));
        rounds.add(build(ARCHERY_GB, R.string.junior_warwick, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 6, 40, 122, 4, 30, 122, 4));
        rounds.add(build(ARCHERY_GB, R.string.junior_western, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 6, 40, 122, 8, 30, 122, 8));
        rounds.add(build(ARCHERY_GB, R.string.junior_windsor, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 6, 40, 122, 6, 30, 122, 6, 20, 122, 6));

        rounds.add(build(ARCHERY_GB, R.string.short_national, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 6, 50, 122, 8, 40, 122, 4));
        rounds.add(build(ARCHERY_GB, R.string.short_warwick, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 6, 50, 122, 4, 40, 122, 4));
        rounds.add(build(ARCHERY_GB, R.string.short_western, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 6, 50, 122, 8, 40, 122, 8));
        rounds.add(build(ARCHERY_GB, R.string.short_windsor, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 6, 50, 122, 6, 40, 122, 6, 30, 122, 6));

        rounds.add(build(ARCHERY_GB, R.string.national, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 6, 60, 122, 8, 50, 122, 4));
        rounds.add(build(ARCHERY_GB, R.string.st_george, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 6, 100, 122, 6, 80, 122, 6, 60, 122, 6));
        rounds.add(build(ARCHERY_GB, R.string.st_nicholas, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 6, 40, 122, 8, 30, 122, 6));
        rounds.add(build(ARCHERY_GB, R.string.warwick, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 6, 60, 122, 4, 50, 122, 4));
        rounds.add(build(ARCHERY_GB, R.string.western, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 6, 60, 122, 8, 50, 122, 8));
        rounds.add(build(ARCHERY_GB, R.string.windsor, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 6, 60, 122, 6, 50, 122, 6, 40, 122, 6));
        rounds.add(build(ARCHERY_GB, R.string.york, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 6, 100, 122, 12, 80, 122, 8, 60, 122, 4));

        rounds.add(build(ARCHERY_GB, R.string.long_national, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 6, 80, 122, 8, 60, 122, 4));
        rounds.add(build(ARCHERY_GB, R.string.long_warwick, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 6, 80, 122, 4, 60, 122, 4));
        rounds.add(build(ARCHERY_GB, R.string.long_western, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 6, 80, 122, 8, 60, 122, 8));

        rounds.add(build(ARCHERY_GB, R.string.new_national, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 6, 100, 122, 8, 80, 122, 4));
        rounds.add(build(ARCHERY_GB, R.string.new_warwick, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 6, 100, 122, 4, 80, 122, 4));
        rounds.add(build(ARCHERY_GB, R.string.new_western, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 6, 100, 122, 8, 80, 122, 8));

        // ARCHERY_GB Metric
        rounds.add(build(ARCHERY_GB, R.string.frostbite, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 3, 30, 80, 12));
        rounds.add(build(ARCHERY_GB, R.string.half_metric_i, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 3, 60,
                122, 3, 50, 80, 3, 30, 80, 3));
        rounds.add(build(ARCHERY_GB, R.string.half_metric_ii, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 3, 50, 122, 3, 40, 80, 3, 30, 80, 3));
        rounds.add(build(ARCHERY_GB, R.string.half_metric_iii, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 50, 122, 3, 40, 122, 3, 30, 80, 3, 20, 80, 3));
        rounds.add(build(ARCHERY_GB, R.string.half_metric_iv, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 40, 122, 3, 30, 122, 3, 20, 80, 3, 10, 80, 3));
        rounds.add(build(ARCHERY_GB, R.string.half_metric_v, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 30, 122, 3, 20, 122, 3, 15, 80, 3, 10, 80, 3));

        rounds.add(build(ARCHERY_GB, R.string.short_metric_i, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 50, 80, 6, 30, 80, 6));
        rounds.add(build(ARCHERY_GB, R.string.short_metric_ii, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 40, 80, 6, 30, 80, 6));
        rounds.add(build(ARCHERY_GB, R.string.short_metric_iii, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 30, 80, 6, 20, 80, 6));
        rounds.add(build(ARCHERY_GB, R.string.short_metric_iv, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 20, 80, 6, 10, 80, 6));
        rounds.add(build(ARCHERY_GB, R.string.short_metric_v, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 15, 80, 6, 10, 80, 6));

        rounds.add(build(ARCHERY_GB, R.string.metric_i, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 6, 60, 122, 6, 50, 80, 6, 30, 80, 6));
        rounds.add(build(ARCHERY_GB, R.string.metric_ii, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 6, 50, 122, 6, 40, 80, 6, 30, 80, 6));
        rounds.add(build(ARCHERY_GB, R.string.metric_iii, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 50, 122, 6, 40, 122, 6, 30, 80, 6, 20, 80, 6));
        rounds.add(build(ARCHERY_GB, R.string.metric_iv, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 40, 122, 6, 30, 122, 6, 20, 80, 6, 10, 80, 6));
        rounds.add(build(ARCHERY_GB, R.string.metric_v, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 30, 122, 6, 20, 122, 6, 15, 80, 6, 10, 80, 6));

        rounds.add(build(ARCHERY_GB, R.string.long_metric_i, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 6, 60, 122, 6));
        rounds.add(build(ARCHERY_GB, R.string.long_metric_ii, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 6, 50, 122, 6));
        rounds.add(build(ARCHERY_GB, R.string.long_metric_iii, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 50, 122, 6, 40, 122, 6));
        rounds.add(build(ARCHERY_GB, R.string.long_metric_iv, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 40, 122, 6, 30, 122, 6));
        rounds.add(build(ARCHERY_GB, R.string.long_metric_v, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 30, 122, 6, 20, 122, 6));
        rounds.add(build(ARCHERY_GB, R.string.long_metric_gents, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 90, 122, 6, 70, 122, 6));
        rounds.add(build(ARCHERY_GB, R.string.long_metric_ladies, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 6, 60, 122, 6));

        // Australia
        rounds.add(build(AUSTRALIAN, R.string.adelaide, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 5, 50, 122, 5, 40, 80, 5, 30, 80, 5));
        rounds.add(build(AUSTRALIAN, R.string.brisbane, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 5, 60, 122, 5, 50, 80, 5, 40, 80, 5));
        rounds.add(build(AUSTRALIAN, R.string.canberra, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 5, 50, 122, 5, 40, 122, 5));
        rounds.add(build(AUSTRALIAN, R.string.darwin, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 40, 122, 15));
        rounds.add(build(AUSTRALIAN, R.string.drake, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 30, 80, 15));
        rounds.add(build(AUSTRALIAN, R.string.fremantle, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 6, 50, 122, 6, 40, 80, 6, 30, 80, 6));
        rounds.add(build(AUSTRALIAN, R.string.geelong, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 30, 122, 15));
        rounds.add(build(AUSTRALIAN, R.string.grange, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 15));
        rounds.add(build(AUSTRALIAN, R.string.hobart, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 90, 122, 5, 70, 122, 5, 50, 122, 5));
        rounds.add(build(AUSTRALIAN, R.string.holt, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 50, 80, 15));
        rounds.add(build(AUSTRALIAN, R.string.horsham, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 40, 122, 6, 35, 122, 6, 30, 80, 6, 25, 80, 6));
        rounds.add(build(AUSTRALIAN, R.string.intermediate, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 55, 122, 6, 45, 122, 6, 35, 80, 6, 25, 80, 6));
        rounds.add(build(AUSTRALIAN, R.string.junior_canberra, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 40, 122, 5, 30, 122, 5, 20, 122, 5));
        rounds.add(build(AUSTRALIAN, R.string.launcheston, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 50, 80, 6, 30, 80, 6));
        rounds.add(build(AUSTRALIAN, R.string.long_brisbane, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 90, 122, 5, 70, 122, 5, 60, 80, 5, 50, 80, 5));
        rounds.add(build(AUSTRALIAN, R.string.long_sydney, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 90, 122, 5, 70, 122, 5, 60, 122, 5, 50, 122, 5));
        rounds.add(build(AUSTRALIAN, R.string.melbourne, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 90, 122, 15));
        rounds.add(build(AUSTRALIAN, R.string.newcastle, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 20, 122, 15));
        rounds.add(build(AUSTRALIAN, R.string.perth, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 5, 60, 122, 5, 50, 122, 5));
        rounds.add(build(AUSTRALIAN, R.string.short_adelaide, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 50, 122, 5, 40, 122, 5, 30, 80, 5, 20, 80, 5));
        rounds.add(build(AUSTRALIAN, R.string.short_canberra, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 50, 122, 5, 40, 122, 5, 30, 122, 5));
        rounds.add(build(AUSTRALIAN, R.string.sydney, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 5, 60, 122, 5, 50, 122, 5, 40, 122, 5));
        rounds.add(build(AUSTRALIAN, R.string.townsville, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 6, 50, 122, 6));
        rounds.add(build(AUSTRALIAN, R.string.wollongong, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 90, 122, 6, 70, 122, 6));

        // NFAA
        rounds.add(build(NFAA, R.string.nfaa_600, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 0, 5, 60, 122, 4, 50, 122, 4, 40, 122, 4));
        rounds.add(build(NFAA, R.string.nfaa_600_classic, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 0, 5, 40, 92, 4, 50, 92, 4, 60, 92, 4));
        rounds.add(build(NFAA, R.string.nfaa_600_classic_cub, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 0, 5, 10, 92, 4, 20, 92, 4, 30, 92, 4));
        rounds.add(build(NFAA, R.string.nfaa_600_classic_junior, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 0, 5, 30, 92, 4, 40, 92, 4, 50, 92, 4));
        rounds.add(build(NFAA, R.string.nfaa_600_cub, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 0, 5, 30, 122, 4, 20, 122, 4, 10, 122, 4));
        rounds.add(build(NFAA, R.string.nfaa_600_junior, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 0, 5, 50, 122, 4, 40, 122, 4, 30, 122, 4));
        rounds.add(build(NFAA, R.string.nfaa_810, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 5, 60, 122, 6, 50, 122, 6, 40, 122, 6));
        rounds.add(build(NFAA, R.string.nfaa_810_cub, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 5, 30, 122, 6, 20, 122, 6, 10, 122, 6));
        rounds.add(build(NFAA, R.string.nfaa_810_junior, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 4, 5, 50, 122, 6, 40, 122, 6, 30, 122, 6));
        rounds.add(build(NFAA, R.string.nfaa_900, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 0, 5, 60, 122, 6, 50, 122, 6, 40, 122, 6));
        rounds.add(build(NFAA, R.string.nfaa_900_cub, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 0, 5, 30, 122, 6, 20, 122, 6, 10, 122, 6));
        rounds.add(build(NFAA, R.string.nfaa_900_junior, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 0, 5, 50, 122, 6, 40, 122, 6, 30, 122, 6));

        // Other
        rounds.add(build(NFAA, R.string.canadian_900, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 0, 6, 55, 122, 5, 45, 122, 5, 35, 122, 5));
        rounds.add(build(NFAA, R.string.t2s_900, CAT_OUTDOOR,
                YARDS, CENTIMETER,
                WAFull.ID, 0, 6, 35, 80, 5, 30, 80, 5, 25, 80, 5));

        // Field
        rounds.add(build(IFAA, R.string.ifaa_animal_280, CAT_OUTDOOR,
                METER, CENTIMETER,
                IFAAAnimal.ID, 0, 3, -1, -1, 14));
        rounds.add(build(IFAA, R.string.ifaa_animal_300, CAT_OUTDOOR,
                METER, CENTIMETER,
                IFAAAnimal.ID, 0, 3, -1, -1, 15));
        rounds.add(build(IFAA, R.string.ifaa_animal_560, CAT_OUTDOOR,
                METER, CENTIMETER,
                IFAAAnimal.ID, 0, 3, -1, -1, 14, -1, -1, 14));
        rounds.add(build(NFAA, R.string.nfaa_international_150, CAT_OUTDOOR,
                METER, CENTIMETER,
                NFAAHunter.ID, 0, 3, -1, -1, 10));
        rounds.add(build(NFAA, R.string.nfaa_international_300, CAT_OUTDOOR,
                METER, CENTIMETER,
                NFAAHunter.ID, 0, 3, -1, -1, 10, -1, -1, 10));
        rounds.add(build(NFAA, R.string.nfaa_animal_280, CAT_OUTDOOR,
                METER, CENTIMETER,
                NFAAAnimal.ID, 0, 3, -1, -1, 14));
        rounds.add(build(NFAA, R.string.nfaa_animal_300, CAT_OUTDOOR,
                METER, CENTIMETER,
                NFAAAnimal.ID, 0, 3, -1, -1, 15));
        rounds.add(build(NFAA, R.string.nfaa_animal_588, CAT_OUTDOOR,
                METER, CENTIMETER,
                NFAAAnimal.ID, 0, 3, -1, -1, 14, -1, -1, 14));
        rounds.add(build(NFAA, R.string.nfaa_lake_of_woods, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAFull.ID, 3, 3, -1, -1, 10));
        rounds.add(build(NFAA, R.string.nfaa_expert_field_300, CAT_OUTDOOR,
                METER, CENTIMETER,
                NFAAExpertField.ID, 0, 4, -1, -1, 15));
        rounds.add(build(NFAA, R.string.nfaa_expert_field_only_560, CAT_OUTDOOR,
                METER, CENTIMETER,
                NFAAExpertField.ID, 0, 4, -1, -1, 14, -1, -1, 14));
        rounds.add(build(NFAA, R.string.nfaa_field_280, CAT_OUTDOOR,
                METER, CENTIMETER,
                NFAAField.ID, 0, 4, -1, -1, 14));
        rounds.add(build(NFAA, R.string.nfaa_field_300, CAT_OUTDOOR,
                METER, CENTIMETER,
                NFAAField.ID, 0, 4, -1, -1, 15));
        rounds.add(build(NFAA, R.string.nfaa_field_560, CAT_OUTDOOR,
                METER, CENTIMETER,
                NFAAField.ID, 0, 4, -1, -1, 14, -1, -1, 14));
        rounds.add(build(NFAA, R.string.nfaa_hunter_280, CAT_OUTDOOR,
                METER, CENTIMETER,
                NFAAHunter.ID, 0, 4, -1, -1, 14));
        rounds.add(build(NFAA, R.string.nfaa_hunter_300, CAT_OUTDOOR,
                METER, CENTIMETER,
                NFAAHunter.ID, 0, 4, -1, -1, 15));
        rounds.add(build(NFAA, R.string.nfaa_hunter_560, CAT_OUTDOOR,
                METER, CENTIMETER,
                NFAAHunter.ID, 0, 4, -1, -1, 14, -1, -1, 14));
        rounds.add(build(NFAA, R.string.nfaa_field_hunter_560, CAT_OUTDOOR,
                METER, CENTIMETER,
                NFAAField.ID, 0, new Target(NFAAHunter.ID, 0, null), 4, -1, -1,
                14, -1, -1, 14));
        rounds.add(build(WA, R.string.wa_field_unmarked_marked_red, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAField.ID, 0, 3, -1, -1, 12, -1, -1, 12));
        rounds.add(build(WA, R.string.wa_field_marked_12_red, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAField.ID, 0, 3, -1, -1, 12));
        rounds.add(build(WA, R.string.wa_field_marked_24_red, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAField.ID, 0, 3, -1, -1, 24));
        rounds.add(build(WA, R.string.wa_field_unmarked_12_red, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAField.ID, 0, 3, -1, -1, 12));
        rounds.add(build(WA, R.string.wa_field_unmarked_24_red, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAField.ID, 0, 3, -1, -1, 24));
        rounds.add(build(WA, R.string.wa_field_unmarked_marked_blue, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAField.ID, 0, 3, -1, -1, 12, -1, -1, 12));
        rounds.add(build(WA, R.string.wa_field_marked_12_blue, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAField.ID, 0, 3, -1, -1, 12));
        rounds.add(build(WA, R.string.wa_field_marked_24_blue, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAField.ID, 0, 3, -1, -1, 24));
        rounds.add(build(WA, R.string.wa_field_unmarked_12_blue, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAField.ID, 0, 3, -1, -1, 12));
        rounds.add(build(WA, R.string.wa_field_unmarked_24_blue, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAField.ID, 0, 3, -1, -1, 24));
        rounds.add(build(WA, R.string.wa_field_unmarked_marked_yellow, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAField.ID, 0, 3, -1, -1, 12, -1, -1, 12));
        rounds.add(build(WA, R.string.wa_field_marked_12_yellow, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAField.ID, 0, 3, -1, -1, 12));
        rounds.add(build(WA, R.string.wa_field_marked_24_yellow, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAField.ID, 0, 3, -1, -1, 24));
        rounds.add(build(WA, R.string.wa_field_unmarked_12_yellow, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAField.ID, 0, 3, -1, -1, 12));
        rounds.add(build(WA, R.string.wa_field_unmarked_24_yellow, CAT_OUTDOOR,
                METER, CENTIMETER,
                WAField.ID, 0, 3, -1, -1, 24));
        rounds.add(build(NFAS, R.string.big_game_36, CAT_OUTDOOR,
                METER, CENTIMETER,
                NFASField.ID, 0, 3, -1, -1, 24));
        rounds.add(build(NFAS, R.string.big_game_40, CAT_OUTDOOR,
                METER, CENTIMETER,
                NFASField.ID, 0, 3, -1, -1, 40));

        return rounds;
    }

    /**
     * Builds a new standard round instance
     *
     * @param institution    Institution that specified the round (ARCHERY_GB or FITA)
     * @param name           Name of the round
     * @param indoor         CAT_INDOOR or CAT_OUTDOOR
     * @param distanceUnit   Unit of the distance specified in round Details
     * @param targetUnit     Unit of the target size specified in roundDetails
     * @param target         Index of the target that is used for shooting
     * @param arrowsPerPasse Number of arrows that are shot per end
     * @param roundDetails   Per round distance, targetSize and number of passes are expected
     * @return The standard round with the specified properties
     */
    private static StandardRound build(int institution, int name, boolean indoor, Dimension.Unit distanceUnit, Dimension.Unit targetUnit, int target, int scoringStyle, int arrowsPerPasse, int... roundDetails) {
        StandardRound standardRound = new StandardRound();
        idCounter++;
        standardRound.setId(idCounter);
        standardRound.name = SharedApplicationInstance.getContext().getString(name);
        standardRound.indoor = indoor;
        standardRound.club = institution;
        for (int i = 0; i < roundDetails.length; i += 3) {
            roundCounter++;
            RoundTemplate roundTemplate = new RoundTemplate();
            roundTemplate.setId(roundCounter);
            roundTemplate.arrowsPerEnd = arrowsPerPasse;
            roundTemplate.distance = new Dimension(roundDetails[i], distanceUnit);
            roundTemplate.setTargetTemplate(
                    new Target(target, scoringStyle, new Dimension(roundDetails[i + 1], targetUnit)));
            roundTemplate.endCount = roundDetails[i + 2];
            standardRound.insert(roundTemplate);
        }
        return standardRound;
    }

    private static StandardRound build(int institution, int name, boolean indoor, Dimension.Unit distanceUnit, Dimension.Unit targetUnit, int target, int scoringStyle, Target target2, int arrowsPerPasse, int... roundDetails) {
        StandardRound standardRound = build(institution, name, indoor, distanceUnit,
                targetUnit, target, scoringStyle, arrowsPerPasse, roundDetails);
        RoundTemplate round2 = standardRound.getRounds().get(1);
        target2.size = round2.getTargetTemplate().size;
        round2.setTargetTemplate(target2);
        return standardRound;
    }
}
