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

package de.dreier.mytargets.shared.models.db

import android.annotation.SuppressLint
import android.os.Parcelable
import com.raizlabs.android.dbflow.annotation.*
import de.dreier.mytargets.shared.AppDatabase
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.IIdSettable
import de.dreier.mytargets.shared.models.Score
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.utils.typeconverters.DimensionConverter
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
@Table(database = AppDatabase::class)
data class Round(
        @Column(name = "_id")
        @PrimaryKey(autoincrement = true)
        override var id: Long = 0,

        @ForeignKey(tableClass = Training::class, references = [(ForeignKeyReference(columnName = "training", foreignKeyColumnName = "_id"))], onDelete = ForeignKeyAction.CASCADE)
        var trainingId: Long? = null,

        @Column
        var index: Int = 0,

        @Column
        var shotsPerEnd: Int = 0,

        @Column
        var maxEndCount: Int? = null,

        @Column(typeConverter = DimensionConverter::class)
        var distance: Dimension = Dimension.UNKNOWN,

        @Column
        var comment: String = "",

        @Column
        var targetId: Int = 0,

        @Column
        var targetScoringStyle: Int = 0,

        @Column(typeConverter = DimensionConverter::class)
        var targetDiameter: Dimension = Dimension.UNKNOWN,

        @Column
        var scoreReachedPoints: Int = 0,

        @Column
        var scoreTotalPoints: Int = 0,

        @Column
        var scoreShotCount: Int = 0
) : IIdSettable, Parcelable {

    constructor(info: RoundTemplate) : this(
            distance = info.distance,
            shotsPerEnd = info.shotsPerEnd,
            maxEndCount = info.endCount,
            index = info.index,
            targetId = info.targetId,
            targetScoringStyle = info.targetScoringStyle,
            targetDiameter = info.targetDiameter
    )

    var target: Target
        get() = Target(targetId.toLong(), targetScoringStyle, targetDiameter)
        set(targetTemplate) {
            targetId = targetTemplate.id.toInt()
            targetScoringStyle = targetTemplate.scoringStyleIndex
            targetDiameter = targetTemplate.diameter
        }

    val score: Score
        get() = Score(scoreReachedPoints, scoreTotalPoints, scoreShotCount)
}
