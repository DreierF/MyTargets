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

package de.dreier.mytargets.features.training.input

import android.annotation.SuppressLint
import android.os.Parcelable
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.augmented.AugmentedEnd
import de.dreier.mytargets.shared.models.augmented.AugmentedRound
import de.dreier.mytargets.shared.models.augmented.AugmentedTraining
import de.dreier.mytargets.shared.models.db.Arrow
import de.dreier.mytargets.shared.models.db.SightMark
import de.dreier.mytargets.shared.models.db.StandardRound
import de.dreier.mytargets.shared.views.TargetViewBase
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
class LoaderResult @JvmOverloads constructor(
        val training: AugmentedTraining,
        var standardRound: StandardRound? = null,
        var arrowDiameter: Dimension? = Dimension(5f, Dimension.Unit.MILLIMETER),
        var sightMark: SightMark? = null,
        var roundIndex: Int = 0,
        var endIndex: Int = 0,
        var maxArrowNumber: Int = 12) : Parcelable {

    val currentRound: AugmentedRound
        get() = training.rounds[roundIndex]

    val distance: Dimension?
        get() = currentRound.round.distance

    val ends: MutableList<AugmentedEnd>
        get() = currentRound.ends

    val currentEnd: AugmentedEnd
        get() {
            if (ends.size <= endIndex || endIndex < 0 || ends.isEmpty()) {
                endIndex = ends.size
                val end = currentRound.addEnd()
                end.end.exact = SettingsManager.inputMethod == TargetViewBase.EInputMethod.PLOTTING
            }
            return ends[endIndex]
        }

    init {
        this.standardRound = training.training.standardRound
    }

    fun setRoundId(roundId: Long) {
        val rounds = training.training.loadRounds()
        roundIndex = 0
        for (i in rounds!!.indices) {
            if (rounds[i].id == roundId) {
                roundIndex = i
                break
            }
        }
    }

    fun setAdjustEndIndex(endIndex: Int) {
        this.endIndex = Math.min(endIndex, currentRound.ends.size)
    }

    fun setArrow(arrow: Arrow) {
        maxArrowNumber = arrow.maxArrowNumber
        arrowDiameter = arrow.diameter
    }
}
