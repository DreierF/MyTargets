/*
 * Copyright (C) 2018 Florian Dreier
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

package de.dreier.mytargets.base.db

import de.dreier.mytargets.shared.R
import de.dreier.mytargets.shared.SharedApplicationInstance
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.Dimension.Unit.*
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.models.augmented.AugmentedStandardRound
import de.dreier.mytargets.shared.models.db.RoundTemplate
import de.dreier.mytargets.shared.models.db.StandardRound
import de.dreier.mytargets.shared.targets.models.*

object StandardRoundFactory {
    const val IFAA = 8
    const val CUSTOM = 256
    private const val ASA = 1
    private const val AUSTRALIAN = 2
    private const val ARCHERY_GB = 4
    private const val NASP = 16
    private const val NFAA = 32
    private const val NFAS = 64
    private const val WA = 128
    private var idCounter: Long = 0
    private var roundCounter: Long = 0

    fun initTable(): List<AugmentedStandardRound> {
        val rounds = mutableListOf<AugmentedStandardRound>()

        /*
            * 3 arrows = 2 min
            * 4 arrows = 2 min
            * 5 arrows, indoor, gnas = 2 min
            * 5 arrows, indoor, i/nfaa, nasp = 4 min
            * 6 arrows = 4 min
            * */

        idCounter = 0
        roundCounter = 0

        // Indoor
        rounds.add(
            build(
                AUSTRALIAN, R.string.australian_combined_indoor,
                METER, CENTIMETER,
                WA5Ring.ID, 0, 3, 18, 40, 10, 25, 60, 10
            )
        )
        rounds.add(
            build(
                AUSTRALIAN, R.string.australian_indoor_1,
                METER, CENTIMETER,
                WA5Ring.ID, 0, 3, 18, 40, 10
            )
        )
        rounds.add(
            build(
                AUSTRALIAN, R.string.australian_indoor_2,
                METER, CENTIMETER,
                WA5Ring.ID, 0, 3, 25, 60, 10
            )
        )
        rounds.add(
            build(
                ASA, R.string.dair_380,
                METER, CENTIMETER,
                DAIR3D.ID, 0, 3, 25, 60, 10
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_18_40cm,
                METER, CENTIMETER,
                WA5Ring.ID, 0, 3, 18, 40, 20
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_18_60cm,
                METER, CENTIMETER,
                WA5Ring.ID, 0, 3, 18, 60, 20
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_25,
                METER, CENTIMETER,
                WAFull.ID, 0, 3, 25, 60, 20
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_combined,
                METER, CENTIMETER,
                WAFull.ID, 0, 3, 25, 60, 20, 18, 40, 20
            )
        )
        rounds.add(
            build(
                WA, R.string.match_round,
                METER, CENTIMETER,
                WAVertical3Spot.ID, 0, 3, 18, 40, 4
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.stafford,
                METER, CENTIMETER,
                WAFull.ID, 0, 3, 30, 80, 24
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.bray_i,
                YARDS, CENTIMETER,
                WAFull.ID, 0, 3, 20, 40, 10
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.bray_ii,
                YARDS, CENTIMETER,
                WAFull.ID, 0, 3, 25, 60, 10
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.portsmouth,
                YARDS, CENTIMETER,
                WAFull.ID, 0, 3, 20, 60, 20
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.worcester,
                YARDS, INCH,
                Worcester.ID, 0, 5, 20, 16, 12
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.vegas_300,
                YARDS, CENTIMETER,
                WA5Ring.ID, 0, 3, 20, 40, 20
            )
        )
        rounds.add(
            build(
                IFAA, R.string.ifaa_150_indoor,
                YARDS, CENTIMETER,
                WA5Ring.ID, 0, 5, 20, 40, 6
            )
        )
        rounds.add(
            build(
                IFAA, R.string.ifaa_150_indoor_cub,
                YARDS, CENTIMETER,
                WA5Ring.ID, 0, 5, 10, 40, 6
            )
        )
        rounds.add(
            build(
                NASP, R.string.nasp_300,
                METER, CENTIMETER,
                WA5Ring.ID, 0, 5, 10, 80, 3, 15, 80, 3
            )
        )
        rounds.add(
            build(
                NFAA, R.string.nfaa_420,
                YARDS, CENTIMETER,
                NFAAIndoor.ID, 2, 5, 10, 40, 12
            )
        )
        rounds.add(
            build(
                NFAA, R.string.nfaa_300_cub,
                YARDS, CENTIMETER,
                NFAAIndoor.ID, 2, 5, 20, 40, 12
            )
        )
        rounds.add(
            build(
                NFAA, R.string.flint_bowman_indoor,
                YARDS, CENTIMETER,
                NFAAField.ID, 0, 4, -1, -1, 7
            )
        )

        // WA
        rounds.add(
            build(
                WA, R.string.wa_50,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 50, 80, 12
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_60,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 12
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_70,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 12
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_900,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 5, 50, 122, 5,
                40, 122, 5
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_bowman,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 30, 122, 6, 25, 122, 6, 25, 80, 6, 20, 80, 6
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_1440_cadet_men,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 6, 60, 122, 6,
                50, 80, 6, 30, 80, 6
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_1440_cadet_women,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 6,
                50, 122, 6, 40, 80, 6, 30, 80, 6
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_compound_individual,
                METER, CENTIMETER,
                WA6Ring.ID, 0, 3, 50, 80, 5
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_compound_qualification,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 50, 80, 12
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_cub,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 50, 122, 6, 40, 122, 6, 30, 80,
                6, 20, 80, 6
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_standard,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 50, 122, 6, 30, 122, 6
            )
        )
        rounds.add(
            build(
                WA, R.string.olympic_round,
                METER, CENTIMETER,
                WAFull.ID, 0, 3, 70, 122, 4
            )
        )

        rounds.add(
            build(
                WA, R.string.wa_1440_junior_men,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 90, 122, 6, 70, 122, 6, 50, 80, 6, 30, 80, 6
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_1440_junior_women,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 6, 60, 122, 6, 50, 80, 6, 30, 80, 6
            )
        )

        rounds.add(
            build(
                WA, R.string.wa_1440_men_master_50,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 6, 60, 122, 6, 50, 80, 6, 30, 80, 6
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_1440_men_master_60,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 6, 60, 122, 6, 50, 80, 6, 30, 80, 6
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_1440_men_master_70,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 6, 50, 122, 6, 40, 80, 6, 30, 80, 6
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_1440_men_senior,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 90, 122, 6, 70, 122, 6, 50, 80, 6, 30, 80, 6
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_1440_women_master_50,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 6, 50, 122, 6, 40, 80, 6, 30, 80, 6
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_1440_women_master_60,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 6, 50, 122, 6, 40, 80, 6, 30, 80, 6
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_1440_women_master_70,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 6, 50, 122, 6, 40, 80, 6, 30, 80, 6
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_1440_women_senior,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 6, 60, 122, 6, 50, 80, 6, 30, 80, 6
            )
        )
        rounds.add(
            build(
                WA, R.string.half_wa_1440_cadet_men,
                METER, METER,
                WAFull.ID, 0, 6, 70, 122, 3, 60, 122, 3,
                50, 80, 3, 30, 80, 3
            )
        )
        rounds.add(
            build(
                WA, R.string.half_wa_1440_cadet_women,
                METER, METER,
                WAFull.ID, 0, 6, 60, 122, 3, 50, 122, 3,
                40, 80, 3, 30, 80, 3
            )
        )
        rounds.add(
            build(
                WA, R.string.half_wa_1440_junior_men,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 90, 122, 3,
                70, 122, 3, 50, 80, 3, 30, 80, 3
            )
        )
        rounds.add(
            build(
                WA, R.string.half_wa_1440_junior_women,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 3,
                60, 122, 3, 50, 80, 3, 30, 80, 3
            )
        )
        rounds.add(
            build(
                WA, R.string.half_wa_1440_men_master_50,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 3,
                60, 122, 3, 50, 80, 3, 30, 80, 3
            )
        )
        rounds.add(
            build(
                WA, R.string.half_wa_1440_men_master_60,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 3,
                60, 122, 3, 50, 80, 3, 30, 80, 3
            )
        )
        rounds.add(
            build(
                WA, R.string.half_wa_1440_men_master_70,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 3,
                50, 122, 3, 40, 80, 3, 30, 80, 3
            )
        )
        rounds.add(
            build(
                WA, R.string.half_wa_1440_men_senior,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 90, 122, 3,
                70, 122, 3, 50, 80, 3, 30, 80, 3
            )
        )
        rounds.add(
            build(
                WA, R.string.half_wa_1440_women_master_50,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 3, 50, 122, 3, 40, 80, 3, 30, 80, 3
            )
        )
        rounds.add(
            build(
                WA, R.string.half_wa_1440_women_master_60,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 3, 50, 122, 3, 40, 80, 3, 30, 80, 3
            )
        )
        rounds.add(
            build(
                WA, R.string.half_wa_1440_women_master_70,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 3, 50, 122, 3, 40, 80, 3, 30, 80, 3
            )
        )
        rounds.add(
            build(
                WA, R.string.half_wa_1440_women_senior,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 3, 60, 122, 3, 50, 80, 3, 30, 80, 3
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_double_720_30_80cm,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 30, 80, 12, 30, 80, 12
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_double_720_40_80cm,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 40, 80, 12, 40, 80, 12
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_double_720_40_122cm,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 40, 122, 12, 40, 122, 12
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_double_720_50_80cm,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 50, 80, 12, 50, 80, 12
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_double_720_50_122cm,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 50, 122, 12, 50, 122, 12
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_double_720_60_122cm,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 12, 60, 122, 12
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_double_720_70_122cm,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 12, 70, 122, 12
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_individual_compound_eleminiation_18,
                METER, CENTIMETER,
                WAFull.ID, 0, 3, 18, 40, 5
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_individual_compound_eleminiation_40,
                METER, CENTIMETER,
                WA6Ring.ID, 0, 3, 40, 80, 5
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_individual_compound_eleminiation_50,
                METER, CENTIMETER,
                WA6Ring.ID, 0, 3, 50, 80, 5
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_recurve_elimination_18,
                METER, CENTIMETER,
                WAFull.ID, 0, 3, 18, 40, 5
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_recurve_elimination_50,
                METER, CENTIMETER,
                WA6Ring.ID, 0, 3, 50, 122, 5
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_recurve_elimination_60,
                METER, CENTIMETER,
                WA6Ring.ID, 0, 3, 60, 122, 5
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_recurve_elimination_70,
                METER, CENTIMETER,
                WA6Ring.ID, 0, 3, 70, 122, 5
            )
        )

        // ARCHERY_GB Imperial
        rounds.add(
            build(
                ARCHERY_GB, R.string.albion,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 6, 80, 122, 6, 60, 122, 6, 50, 122, 6
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.american,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 6, 60, 122, 5, 50, 122, 5, 40, 122, 5
            )
        )

        rounds.add(
            build(
                ARCHERY_GB, R.string.bristol_i,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 6, 80, 122, 12, 60, 122, 8, 50, 122, 4
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.bristol_ii,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 6, 60, 122, 12, 50, 122, 8, 40, 122, 4
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.bristol_iii,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 6, 50, 122, 12, 40, 122, 8, 30, 122, 4
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.bristol_iv,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 6, 40, 122, 12, 30, 122, 8, 20, 122, 4
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.bristol_v,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 6, 30, 122, 12, 20, 122, 8, 10, 122, 4
            )
        )

        rounds.add(
            build(
                ARCHERY_GB, R.string.hereford,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 6, 80, 122, 12, 60, 122, 8, 50, 122, 4
            )
        )

        rounds.add(
            build(
                ARCHERY_GB, R.string.short_junior_national,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 6, 30, 122, 8, 20, 122, 4
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.short_junior_warwick,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 6, 30, 122, 4, 20, 122, 4
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.short_junior_western,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 6, 30, 122, 8, 20, 122, 8
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.short_junior_windsor,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 6, 30, 122, 6, 20,
                122, 6, 10, 122, 6
            )
        )

        rounds.add(
            build(
                ARCHERY_GB, R.string.junior_national,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 6, 40, 122, 8, 30, 122, 4
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.junior_warwick,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 6, 40, 122, 4, 30, 122, 4
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.junior_western,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 6, 40, 122, 8, 30, 122, 8
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.junior_windsor,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 6, 40, 122, 6, 30, 122, 6, 20, 122, 6
            )
        )

        rounds.add(
            build(
                ARCHERY_GB, R.string.short_national,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 6, 50, 122, 8, 40, 122, 4
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.short_warwick,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 6, 50, 122, 4, 40, 122, 4
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.short_western,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 6, 50, 122, 8, 40, 122, 8
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.short_windsor,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 6, 50, 122, 6, 40, 122, 6, 30, 122, 6
            )
        )

        rounds.add(
            build(
                ARCHERY_GB, R.string.national,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 6, 60, 122, 8, 50, 122, 4
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.st_george,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 6, 100, 122, 6, 80, 122, 6, 60, 122, 6
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.st_nicholas,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 6, 40, 122, 8, 30, 122, 6
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.warwick,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 6, 60, 122, 4, 50, 122, 4
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.western,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 6, 60, 122, 8, 50, 122, 8
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.windsor,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 6, 60, 122, 6, 50, 122, 6, 40, 122, 6
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.york,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 6, 100, 122, 12, 80, 122, 8, 60, 122, 4
            )
        )

        rounds.add(
            build(
                ARCHERY_GB, R.string.long_national,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 6, 80, 122, 8, 60, 122, 4
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.long_warwick,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 6, 80, 122, 4, 60, 122, 4
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.long_western,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 6, 80, 122, 8, 60, 122, 8
            )
        )

        rounds.add(
            build(
                ARCHERY_GB, R.string.new_national,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 6, 100, 122, 8, 80, 122, 4
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.new_warwick,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 6, 100, 122, 4, 80, 122, 4
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.new_western,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 6, 100, 122, 8, 80, 122, 8
            )
        )

        // ARCHERY_GB Metric
        rounds.add(
            build(
                ARCHERY_GB, R.string.frostbite,
                METER, CENTIMETER,
                WAFull.ID, 0, 3, 30, 80, 12
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.half_metric_i,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 3, 60,
                122, 3, 50, 80, 3, 30, 80, 3
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.half_metric_ii,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 3, 50, 122, 3, 40, 80, 3, 30, 80, 3
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.half_metric_iii,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 50, 122, 3, 40, 122, 3, 30, 80, 3, 20, 80, 3
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.half_metric_iv,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 40, 122, 3, 30, 122, 3, 20, 80, 3, 10, 80, 3
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.half_metric_v,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 30, 122, 3, 20, 122, 3, 15, 80, 3, 10, 80, 3
            )
        )

        rounds.add(
            build(
                ARCHERY_GB, R.string.short_metric_i,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 50, 80, 6, 30, 80, 6
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.short_metric_ii,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 40, 80, 6, 30, 80, 6
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.short_metric_iii,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 30, 80, 6, 20, 80, 6
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.short_metric_iv,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 20, 80, 6, 10, 80, 6
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.short_metric_v,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 15, 80, 6, 10, 80, 6
            )
        )

        rounds.add(
            build(
                ARCHERY_GB, R.string.metric_i,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 6, 60, 122, 6, 50, 80, 6, 30, 80, 6
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.metric_ii,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 6, 50, 122, 6, 40, 80, 6, 30, 80, 6
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.metric_iii,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 50, 122, 6, 40, 122, 6, 30, 80, 6, 20, 80, 6
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.metric_iv,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 40, 122, 6, 30, 122, 6, 20, 80, 6, 10, 80, 6
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.metric_v,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 30, 122, 6, 20, 122, 6, 15, 80, 6, 10, 80, 6
            )
        )

        rounds.add(
            build(
                ARCHERY_GB, R.string.long_metric_i,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 6, 60, 122, 6
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.long_metric_ii,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 6, 50, 122, 6
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.long_metric_iii,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 50, 122, 6, 40, 122, 6
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.long_metric_iv,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 40, 122, 6, 30, 122, 6
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.long_metric_v,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 30, 122, 6, 20, 122, 6
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.long_metric_gents,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 90, 122, 6, 70, 122, 6
            )
        )
        rounds.add(
            build(
                ARCHERY_GB, R.string.long_metric_ladies,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 6, 60, 122, 6
            )
        )

        // Australia
        rounds.add(
            build(
                AUSTRALIAN, R.string.adelaide,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 5, 50, 122, 5, 40, 80, 5, 30, 80, 5
            )
        )
        rounds.add(
            build(
                AUSTRALIAN, R.string.brisbane,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 5, 60, 122, 5, 50, 80, 5, 40, 80, 5
            )
        )
        rounds.add(
            build(
                AUSTRALIAN, R.string.canberra,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 5, 50, 122, 5, 40, 122, 5
            )
        )
        rounds.add(
            build(
                AUSTRALIAN, R.string.darwin,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 40, 122, 15
            )
        )
        rounds.add(
            build(
                AUSTRALIAN, R.string.drake,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 30, 80, 15
            )
        )
        rounds.add(
            build(
                AUSTRALIAN, R.string.fremantle,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 6, 50, 122, 6, 40, 80, 6, 30, 80, 6
            )
        )
        rounds.add(
            build(
                AUSTRALIAN, R.string.geelong,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 30, 122, 15
            )
        )
        rounds.add(
            build(
                AUSTRALIAN, R.string.grange,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 15
            )
        )
        rounds.add(
            build(
                AUSTRALIAN, R.string.hobart,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 90, 122, 5, 70, 122, 5, 50, 122, 5
            )
        )
        rounds.add(
            build(
                AUSTRALIAN, R.string.holt,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 50, 80, 15
            )
        )
        rounds.add(
            build(
                AUSTRALIAN, R.string.horsham,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 40, 122, 6, 35, 122, 6, 30, 80, 6, 25, 80, 6
            )
        )
        rounds.add(
            build(
                AUSTRALIAN, R.string.intermediate,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 55, 122, 6, 45, 122, 6, 35, 80, 6, 25, 80, 6
            )
        )
        rounds.add(
            build(
                AUSTRALIAN, R.string.junior_canberra,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 40, 122, 5, 30, 122, 5, 20, 122, 5
            )
        )
        rounds.add(
            build(
                AUSTRALIAN, R.string.launcheston,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 50, 80, 6, 30, 80, 6
            )
        )
        rounds.add(
            build(
                AUSTRALIAN, R.string.long_brisbane,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 90, 122, 5, 70, 122, 5, 60, 80, 5, 50, 80, 5
            )
        )
        rounds.add(
            build(
                AUSTRALIAN, R.string.long_sydney,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 90, 122, 5, 70, 122, 5, 60, 122, 5, 50, 122, 5
            )
        )
        rounds.add(
            build(
                AUSTRALIAN, R.string.melbourne,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 90, 122, 15
            )
        )
        rounds.add(
            build(
                AUSTRALIAN, R.string.newcastle,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 20, 122, 15
            )
        )
        rounds.add(
            build(
                AUSTRALIAN, R.string.perth,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 5, 60, 122, 5, 50, 122, 5
            )
        )
        rounds.add(
            build(
                AUSTRALIAN, R.string.short_adelaide,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 50, 122, 5, 40, 122, 5, 30, 80, 5, 20, 80, 5
            )
        )
        rounds.add(
            build(
                AUSTRALIAN, R.string.short_canberra,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 50, 122, 5, 40, 122, 5, 30, 122, 5
            )
        )
        rounds.add(
            build(
                AUSTRALIAN, R.string.sydney,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 70, 122, 5, 60, 122, 5, 50, 122, 5, 40, 122, 5
            )
        )
        rounds.add(
            build(
                AUSTRALIAN, R.string.townsville,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 60, 122, 6, 50, 122, 6
            )
        )
        rounds.add(
            build(
                AUSTRALIAN, R.string.wollongong,
                METER, CENTIMETER,
                WAFull.ID, 0, 6, 90, 122, 6, 70, 122, 6
            )
        )

        // NFAA
        rounds.add(
            build(
                NFAA, R.string.nfaa_600,
                YARDS, CENTIMETER,
                WAFull.ID, 0, 5, 60, 122, 4, 50, 122, 4, 40, 122, 4
            )
        )
        rounds.add(
            build(
                NFAA, R.string.nfaa_600_classic,
                YARDS, CENTIMETER,
                WAFull.ID, 0, 5, 40, 92, 4, 50, 92, 4, 60, 92, 4
            )
        )
        rounds.add(
            build(
                NFAA, R.string.nfaa_600_classic_cub,
                YARDS, CENTIMETER,
                WAFull.ID, 0, 5, 10, 92, 4, 20, 92, 4, 30, 92, 4
            )
        )
        rounds.add(
            build(
                NFAA, R.string.nfaa_600_classic_junior,
                YARDS, CENTIMETER,
                WAFull.ID, 0, 5, 30, 92, 4, 40, 92, 4, 50, 92, 4
            )
        )
        rounds.add(
            build(
                NFAA, R.string.nfaa_600_cub,
                YARDS, CENTIMETER,
                WAFull.ID, 0, 5, 30, 122, 4, 20, 122, 4, 10, 122, 4
            )
        )
        rounds.add(
            build(
                NFAA, R.string.nfaa_600_junior,
                YARDS, CENTIMETER,
                WAFull.ID, 0, 5, 50, 122, 4, 40, 122, 4, 30, 122, 4
            )
        )
        rounds.add(
            build(
                NFAA, R.string.nfaa_810,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 5, 60, 122, 6, 50, 122, 6, 40, 122, 6
            )
        )
        rounds.add(
            build(
                NFAA, R.string.nfaa_810_cub,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 5, 30, 122, 6, 20, 122, 6, 10, 122, 6
            )
        )
        rounds.add(
            build(
                NFAA, R.string.nfaa_810_junior,
                YARDS, CENTIMETER,
                WAFull.ID, 5, 5, 50, 122, 6, 40, 122, 6, 30, 122, 6
            )
        )
        rounds.add(
            build(
                NFAA, R.string.nfaa_900,
                YARDS, CENTIMETER,
                WAFull.ID, 0, 5, 60, 122, 6, 50, 122, 6, 40, 122, 6
            )
        )
        rounds.add(
            build(
                NFAA, R.string.nfaa_900_cub,
                YARDS, CENTIMETER,
                WAFull.ID, 0, 5, 30, 122, 6, 20, 122, 6, 10, 122, 6
            )
        )
        rounds.add(
            build(
                NFAA, R.string.nfaa_900_junior,
                YARDS, CENTIMETER,
                WAFull.ID, 0, 5, 50, 122, 6, 40, 122, 6, 30, 122, 6
            )
        )

        // Other
        rounds.add(
            build(
                NFAA, R.string.canadian_900,
                YARDS, CENTIMETER,
                WAFull.ID, 0, 6, 55, 122, 5, 45, 122, 5, 35, 122, 5
            )
        )
        rounds.add(
            build(
                NFAA, R.string.t2s_900,
                YARDS, CENTIMETER,
                WAFull.ID, 0, 6, 35, 80, 5, 30, 80, 5, 25, 80, 5
            )
        )

        // Field
        rounds.add(
            build(
                IFAA, R.string.ifaa_animal_280,
                METER, CENTIMETER,
                IFAAAnimal.ID, 0, 3, -1, -1, 14
            )
        )
        rounds.add(
            build(
                IFAA, R.string.ifaa_animal_300,
                METER, CENTIMETER,
                IFAAAnimal.ID, 0, 3, -1, -1, 15
            )
        )
        rounds.add(
            build(
                IFAA, R.string.ifaa_animal_560,
                METER, CENTIMETER,
                IFAAAnimal.ID, 0, 3, -1, -1, 14, -1, -1, 14
            )
        )
        rounds.add(
            build(
                NFAA, R.string.nfaa_international_150,
                METER, CENTIMETER,
                NFAAHunter.ID, 0, 3, -1, -1, 10
            )
        )
        rounds.add(
            build(
                NFAA, R.string.nfaa_international_300,
                METER, CENTIMETER,
                NFAAHunter.ID, 0, 3, -1, -1, 10, -1, -1, 10
            )
        )
        rounds.add(
            build(
                NFAA, R.string.nfaa_animal_280,
                METER, CENTIMETER,
                NFAAAnimal.ID, 0, 3, -1, -1, 14
            )
        )
        rounds.add(
            build(
                NFAA, R.string.nfaa_animal_300,
                METER, CENTIMETER,
                NFAAAnimal.ID, 0, 3, -1, -1, 15
            )
        )
        rounds.add(
            build(
                NFAA, R.string.nfaa_animal_588,
                METER, CENTIMETER,
                NFAAAnimal.ID, 0, 3, -1, -1, 14, -1, -1, 14
            )
        )
        rounds.add(
            build(
                NFAA, R.string.nfaa_lake_of_woods,
                METER, CENTIMETER,
                WAFull.ID, 4, 3, -1, -1, 10
            )
        )
        rounds.add(
            build(
                NFAA, R.string.nfaa_expert_field_300,
                METER, CENTIMETER,
                NFAAExpertField.ID, 0, 4, -1, -1, 15
            )
        )
        rounds.add(
            build(
                NFAA, R.string.nfaa_expert_field_only_560,
                METER, CENTIMETER,
                NFAAExpertField.ID, 0, 4, -1, -1, 14, -1, -1, 14
            )
        )
        rounds.add(
            build(
                NFAA, R.string.nfaa_field_280,
                METER, CENTIMETER,
                NFAAField.ID, 0, 4, -1, -1, 14
            )
        )
        rounds.add(
            build(
                NFAA, R.string.nfaa_field_300,
                METER, CENTIMETER,
                NFAAField.ID, 0, 4, -1, -1, 15
            )
        )
        rounds.add(
            build(
                NFAA, R.string.nfaa_field_560,
                METER, CENTIMETER,
                NFAAField.ID, 0, 4, -1, -1, 14, -1, -1, 14
            )
        )
        rounds.add(
            build(
                NFAA, R.string.nfaa_hunter_280,
                METER, CENTIMETER,
                NFAAHunter.ID, 0, 4, -1, -1, 14
            )
        )
        rounds.add(
            build(
                NFAA, R.string.nfaa_hunter_300,
                METER, CENTIMETER,
                NFAAHunter.ID, 0, 4, -1, -1, 15
            )
        )
        rounds.add(
            build(
                NFAA, R.string.nfaa_hunter_560,
                METER, CENTIMETER,
                NFAAHunter.ID, 0, 4, -1, -1, 14, -1, -1, 14
            )
        )
        rounds.add(
            build(
                NFAA, R.string.nfaa_field_hunter_560,
                METER, CENTIMETER,
                NFAAField.ID, 0, Target(NFAAHunter.ID, 0, Dimension.UNKNOWN), 4, -1, -1,
                14, -1, -1, 14
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_field_unmarked_marked_red,
                METER, CENTIMETER,
                WAField.ID, 1, 3, -1, -1, 12, -1, -1, 12
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_field_marked_12_red,
                METER, CENTIMETER,
                WAField.ID, 1, 3, -1, -1, 12
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_field_marked_24_red,
                METER, CENTIMETER,
                WAField.ID, 1, 3, -1, -1, 24
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_field_unmarked_12_red,
                METER, CENTIMETER,
                WAField.ID, 1, 3, -1, -1, 12
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_field_unmarked_24_red,
                METER, CENTIMETER,
                WAField.ID, 1, 3, -1, -1, 24
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_field_unmarked_marked_blue,
                METER, CENTIMETER,
                WAField.ID, 1, 3, -1, -1, 12, -1, -1, 12
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_field_marked_12_blue,
                METER, CENTIMETER,
                WAField.ID, 1, 3, -1, -1, 12
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_field_marked_24_blue,
                METER, CENTIMETER,
                WAField.ID, 1, 3, -1, -1, 24
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_field_unmarked_12_blue,
                METER, CENTIMETER,
                WAField.ID, 1, 3, -1, -1, 12
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_field_unmarked_24_blue,
                METER, CENTIMETER,
                WAField.ID, 1, 3, -1, -1, 24
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_field_unmarked_marked_yellow,
                METER, CENTIMETER,
                WAField.ID, 1, 3, -1, -1, 12, -1, -1, 12
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_field_marked_12_yellow,
                METER, CENTIMETER,
                WAField.ID, 1, 3, -1, -1, 12
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_field_marked_24_yellow,
                METER, CENTIMETER,
                WAField.ID, 1, 3, -1, -1, 24
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_field_unmarked_12_yellow,
                METER, CENTIMETER,
                WAField.ID, 1, 3, -1, -1, 12
            )
        )
        rounds.add(
            build(
                WA, R.string.wa_field_unmarked_24_yellow,
                METER, CENTIMETER,
                WAField.ID, 1, 3, -1, -1, 24
            )
        )
        rounds.add(
            build(
                NFAS, R.string.big_game_36,
                METER, CENTIMETER,
                NFASField.ID, 0, 3, -1, -1, 24
            )
        )
        rounds.add(
            build(
                NFAS, R.string.big_game_40,
                METER, CENTIMETER,
                NFASField.ID, 0, 3, -1, -1, 40
            )
        )

        return rounds
    }

    /**
     * Builds a new standard round instance
     *
     * @param institution  Institution that specified the round (ARCHERY_GB or FITA)
     * @param name         Name of the round
     * @param distanceUnit Unit of the distance specified in round Details
     * @param targetUnit   Unit of the target size specified in roundDetails
     * @param target       Index of the target that is used for shooting
     * @param shotsPerEnd  Number of arrows that are shot per end
     * @param roundDetails Per round distance, targetSize and number of ends are expected
     * @return The standard round with the specified properties
     */
    private fun build(
        institution: Int,
        name: Int,
        distanceUnit: Dimension.Unit,
        targetUnit: Dimension.Unit,
        target: Long,
        scoringStyle: Int,
        shotsPerEnd: Int,
        vararg roundDetails: Int
    ): AugmentedStandardRound {
        val standardRound = StandardRound()
        idCounter++
        standardRound.id = idCounter
        standardRound.name = SharedApplicationInstance.context.getString(name)
        standardRound.club = institution
        val rounds = mutableListOf<RoundTemplate>()
        var i = 0
        while (i < roundDetails.size) {
            roundCounter++
            val roundTemplate = RoundTemplate()
            roundTemplate.id = roundCounter
            roundTemplate.shotsPerEnd = shotsPerEnd
            roundTemplate.distance = Dimension(roundDetails[i].toFloat(), distanceUnit)
            roundTemplate.targetTemplate = Target(
                target,
                scoringStyle,
                Dimension(roundDetails[i + 1].toFloat(), targetUnit)
            )
            roundTemplate.endCount = roundDetails[i + 2]
            roundTemplate.index = rounds.size
            roundTemplate.standardRoundId = standardRound.id
            rounds.add(roundTemplate)
            i += 3
        }
        return AugmentedStandardRound(standardRound, rounds)
    }

    private fun build(
        institution: Int,
        name: Int,
        distanceUnit: Dimension.Unit,
        targetUnit: Dimension.Unit,
        target: Long,
        scoringStyle: Int,
        target2: Target,
        shotsPerEnd: Int,
        vararg roundDetails: Int
    ): AugmentedStandardRound {
        val standardRound = build(
            institution, name, distanceUnit,
            targetUnit, target, scoringStyle, shotsPerEnd, *roundDetails
        )
        val round2 = standardRound.roundTemplates[1]
        target2.diameter = round2.targetTemplate.diameter
        round2.targetTemplate = target2
        return standardRound
    }
}
