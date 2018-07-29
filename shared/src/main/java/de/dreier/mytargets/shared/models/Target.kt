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

package de.dreier.mytargets.shared.models

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Ignore
import android.os.Parcelable
import de.dreier.mytargets.shared.models.db.Shot
import de.dreier.mytargets.shared.targets.TargetFactory
import de.dreier.mytargets.shared.targets.drawable.TargetDrawable
import de.dreier.mytargets.shared.targets.models.TargetModelBase
import de.dreier.mytargets.shared.targets.scoringstyle.ScoringStyle
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

/**
 * Represents a target face, which is in contrast to a [TargetModelBase] bound to a specific
 * scoring style and diameter.
 */
@Parcelize
data class Target(
        @ColumnInfo(name = "targetId")
        override var id: Long = 0,
        @ColumnInfo(name = "targetScoringStyleIndex")
        var scoringStyleIndex: Int = 0,
        @ColumnInfo(name = "targetDiameter")
        var diameter: Dimension = Dimension.UNKNOWN
) : IIdProvider, Comparable<Target>, Parcelable {

    @IgnoredOnParcel
    @delegate:Ignore
    val model: TargetModelBase by lazy { TargetFactory.getTarget(id) }
    @IgnoredOnParcel
    @delegate:Ignore
    val drawable: TargetDrawable by lazy { TargetDrawable(this) }

    @Ignore
    constructor(target: Long, scoringStyle: Int) : this(target, scoringStyle, Dimension.UNKNOWN) {
        this.diameter = model.diameters[0]
    }

    val name: String
        get() = String.format("%s (%s)", toString(), diameter.toString())

    fun zoneToString(zone: Int, arrow: Int): String {
        return getScoringStyle().zoneToString(zone, arrow)
    }

    fun getScoreByZone(zone: Int, arrow: Int): Int {
        return getScoringStyle().getPointsByScoringRing(zone, arrow)
    }

    fun getDetails(): String {
        return model.scoringStyles[scoringStyleIndex].toString()
    }

    fun getSelectableZoneList(arrow: Int): List<SelectableZone> {
        return model.getSelectableZoneList(scoringStyleIndex, arrow)
    }

    fun getScoringStyle(): ScoringStyle {
        return model.getScoringStyle(scoringStyleIndex)
    }

    fun getReachedScore(shots: List<Shot>): Score {
        return getScoringStyle().getReachedScore(shots)
    }

    override fun toString(): String {
        return model.toString()
    }

    override fun compareTo(other: Target) = compareBy(Target::id).compare(this, other)

    companion object {
        fun singleSpotTargetFrom(spotTarget: Target): Target {
            if (spotTarget.model.faceCount == 1) {
                return spotTarget
            }
            val singleSpotTargetId = spotTarget.model.singleSpotTargetId.toInt()
            return Target(singleSpotTargetId.toLong(), spotTarget.scoringStyleIndex, spotTarget.diameter)
        }
    }
}
