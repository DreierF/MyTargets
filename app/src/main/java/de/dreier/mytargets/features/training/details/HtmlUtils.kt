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

package de.dreier.mytargets.features.training.details

import android.content.Context
import android.text.Spanned
import android.text.TextUtils
import de.dreier.mytargets.R
import de.dreier.mytargets.app.ApplicationInstance
import de.dreier.mytargets.shared.models.db.Round
import de.dreier.mytargets.shared.models.db.Training
import de.dreier.mytargets.utils.Utils

object HtmlUtils {

    fun getTrainingInfoHTML(context: Context, training: Training, rounds: List<Round>, equals: BooleanArray): Spanned {
        val info = HtmlInfoBuilder()
        addStaticTrainingHeaderInfo(context, info, training)
        addDynamicTrainingHeaderInfo(rounds, equals, info)
        return Utils.fromHtml(info.toString())
    }

    private fun addStaticTrainingHeaderInfo(context: Context, info: HtmlInfoBuilder, training: Training) {
        if (training.environment.indoor) {
            info.addLine(R.string.environment, context.getString(R.string.indoor))
        } else {
            info.addLine(R.string.weather, training.environment.weather.getName())
            info.addLine(R.string.wind,
                    training.environment.getWindSpeed(context))
            if (!TextUtils.isEmpty(training.environment.location)) {
                info.addLine(R.string.location, training.environment.location)
            }
        }

        if (training.bowId != null) {
            val bow = ApplicationInstance.db.bowDAO().loadBow(training.bowId!!)
            info.addLine(R.string.bow, bow.name)
        }

        if (training.arrowId != null) {
            val arrow = ApplicationInstance.db.arrowDAO().loadArrow(training.arrowId!!)
            info.addLine(R.string.arrow, arrow.name)
        }

        if (training.standardRoundId != null) {
            val standardRound = ApplicationInstance.db.standardRoundDAO().loadStandardRound(training.standardRoundId!!)
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
            equals[0] = r.distance == round.distance && equals[0]
            equals[1] = r.target == round.target && equals[1]
        }
    }

    fun getRoundInfo(round: Round, equals: BooleanArray): Spanned {
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
        return Utils.fromHtml(info.toString())
    }
}
