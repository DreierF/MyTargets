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

package de.dreier.mytargets.features.training.details

import android.content.Context
import android.text.TextUtils
import de.dreier.mytargets.R
import de.dreier.mytargets.shared.models.db.Round
import de.dreier.mytargets.shared.models.db.Training
import de.dreier.mytargets.shared.utils.SharedUtils

object HtmlUtils {

    fun getTrainingInfoHTML(context: Context, training: Training, rounds: List<Round>, equals: BooleanArray): String {
        val info = HtmlInfoBuilder()
        addStaticTrainingHeaderInfo(context, info, training)
        addDynamicTrainingHeaderInfo(rounds, equals, info)
        return info.toString()
    }

    private fun addStaticTrainingHeaderInfo(context: Context, info: HtmlInfoBuilder, training: Training) {
        if (training.indoor) {
            info.addLine(R.string.environment, context.getString(R.string.indoor))
        } else {
            info.addLine(R.string.weather, training.environment.weather.getName())
            info.addLine(R.string.wind,
                    training.environment.getWindSpeed(context))
            if (!TextUtils.isEmpty(training.environment.location)) {
                info.addLine(R.string.location, training.environment.location)
            }
        }

        val bow = training.bow
        if (bow != null) {
            info.addLine(R.string.bow, bow.name)
        }

        val arrow = training.arrow
        if (arrow != null) {
            info.addLine(R.string.arrow, arrow.name)
        }

        val standardRound = training.standardRound
        if (standardRound != null) {
            info.addLine(R.string.standard_round, standardRound.name)
        }
    }

    private fun addDynamicTrainingHeaderInfo(rounds: List<Round>, equals: BooleanArray, info: HtmlInfoBuilder) {
        if (rounds.isNotEmpty()) {
            getEqualValues(rounds, equals)
            val round = rounds[0]
            if (equals[0]) {
                info.addLine(R.string.distance, round.distance)
            }
            if (equals[1]) {
                info.addLine(R.string.target_face, round.target.name)
            }
        }
    }

    private fun getEqualValues(rounds: List<Round>, equals: BooleanArray) {
        // Aggregate round information
        equals[0] = true
        equals[1] = true
        val round = rounds[0]
        for (r in rounds) {
            equals[0] = SharedUtils.equals(r.distance, round.distance) && equals[0]
            equals[1] = SharedUtils.equals(r.target, round.target) && equals[1]
        }
    }

    fun getRoundInfo(round: Round, equals: BooleanArray): String {
        val info = HtmlInfoBuilder()
        if (!equals[0]) {
            info.addLine(R.string.distance, round.distance)
        }
        if (!equals[1]) {
            info.addLine(R.string.target_face, round.target.name)
        }
        if (!round.comment.isEmpty()) {
            info.addLine(R.string.comment, round.comment)
        }
        return info.toString()
    }
}
