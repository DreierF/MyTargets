/*
 * Copyright (C) 2017 Florian Dreier
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

package de.dreier.mytargets.shared.targets.models

import android.graphics.PointF
import android.support.annotation.StringRes
import de.dreier.mytargets.shared.SharedApplicationInstance
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.ETargetType
import de.dreier.mytargets.shared.models.IIdProvider
import de.dreier.mytargets.shared.models.SelectableZone
import de.dreier.mytargets.shared.models.db.Shot
import de.dreier.mytargets.shared.targets.decoration.TargetDecorator
import de.dreier.mytargets.shared.targets.scoringstyle.ScoringStyle
import de.dreier.mytargets.shared.targets.zone.CircularZone
import de.dreier.mytargets.shared.targets.zone.ZoneBase
import de.dreier.mytargets.shared.utils.Color
import de.dreier.mytargets.shared.utils.Color.BLACK
import java.util.*

open class TargetModelBase protected constructor(
        override val id: Long,
        @param:StringRes private val nameRes: Int,
        val diameters: Array<Dimension>,
        val type: ETargetType = ETargetType.TARGET,
        val zones: Array<ZoneBase>,
        val scoringStyles: Array<ScoringStyle>
) : IIdProvider {
    var faceRadius: Float = 0f
    var facePositions: Array<PointF>
    var decorator: TargetDecorator? = null
        protected set

    /**
     * Factor that needs to be applied to the target's diameter to get the real target size.
     * e.g. 5 Ring 40cm is half the size of a full 40cm, therefore the target is 20cm in reality,
     * hence the factor is 0.5f.
     */
    protected var realSizeFactor = 1f

    open val singleSpotTargetId: Long
        get() = id

    val zoneCount: Int
        get() = zones.size

    val faceCount: Int
        get() = facePositions.size

    init {
        this.faceRadius = 1f
        this.facePositions = arrayOf(PointF(0f, 0f))
    }

    override fun toString(): String {
        return SharedApplicationInstance.getStr(nameRes)
    }

    fun getZone(zone: Int): ZoneBase {
        return if (isOutOfRange(zone)) {
            CircularZone(0f, BLACK, BLACK, 0)
        } else zones[zone]
    }

    fun getRealSize(diameter: Dimension): Dimension {
        return Dimension(realSizeFactor * diameter.value, diameter.unit)
    }

    fun getScoringStyle(scoringStyle: Int): ScoringStyle {
        return scoringStyles[scoringStyle]
    }

    open fun dependsOnArrowIndex(): Boolean {
        return false
    }

    open fun shouldDrawZone(zone: Int, scoringStyle: Int): Boolean {
        return true
    }

    fun getContrastColor(zone: Int): Int {
        // Handle Miss-shots
        return if (isOutOfRange(zone)) BLACK else Color.getContrast(getZone(zone).fillColor)
    }

    private fun isOutOfRange(zone: Int): Boolean {
        return zone < 0 || zone >= zoneCount
    }

    fun getZoneFromPoint(ax: Float, ay: Float, arrowRadius: Float): Int {
        return zones.indices.firstOrNull { getZone(it).isInZone(ax, ay, arrowRadius) } ?: Shot.MISS
    }

    /**
     * Lists all zones that can be selected for the given scoringStyleIndex and arrow index.
     * Consecutive zones with the same text are excluded.
     *
     * @param scoringStyleIndex Index of the scoring style for this target.
     * @param arrow             Shot index, describes whether it is the first arrow(0), the second one, ...
     * This has an impact on the yielded score for some animal target faces.
     */
    fun getSelectableZoneList(scoringStyleIndex: Int, arrow: Int): List<SelectableZone> {
        val scoringStyle = getScoringStyle(scoringStyleIndex)
        val list = ArrayList<SelectableZone>()
        var last = ""
        for (i in 0..zones.size) {
            val zoneText = scoringStyle.zoneToString(i, arrow)
            if (last != zoneText) {
                val index = if (i == zones.size) -1 else i
                val score = scoringStyle.getScoreByScoringRing(i, arrow)
                list.add(SelectableZone(index, getZone(i), zoneText, score))
                last = zoneText
            }
        }
        return list
    }
}
