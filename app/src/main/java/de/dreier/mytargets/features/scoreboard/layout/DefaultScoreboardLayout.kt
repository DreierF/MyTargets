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

package de.dreier.mytargets.features.scoreboard.layout

import android.content.Context
import android.text.TextUtils
import de.dreier.mytargets.R
import de.dreier.mytargets.features.scoreboard.ScoreboardBuilder
import de.dreier.mytargets.features.scoreboard.ScoreboardConfiguration
import de.dreier.mytargets.features.scoreboard.builder.model.Table
import de.dreier.mytargets.features.scoreboard.builder.model.TextCell
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.shared.models.SelectableZone
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.models.dao.EndDAO
import de.dreier.mytargets.shared.models.db.Round
import de.dreier.mytargets.shared.models.db.Shot
import de.dreier.mytargets.shared.models.db.Training
import de.dreier.mytargets.shared.targets.scoringstyle.ScoringStyle
import de.dreier.mytargets.shared.utils.ScoreUtils
import java.util.*

class DefaultScoreboardLayout(private val context: Context, private val locale: Locale, private val configuration: ScoreboardConfiguration) {
    private lateinit var builder: ScoreboardBuilder

    fun generateWithBuilder(builder: ScoreboardBuilder, training: Training, rounds: List<Round>) {
        this.builder = builder

        if (configuration.showTitle) {
            builder.title(training.title)
        }

        val equals = BooleanArray(2)
        if (configuration.showProperties) {
            builder.table(getTrainingInfoTable(training, rounds, equals))
        }

        if (configuration.showTable) {
            for (round in rounds) {
                builder.openSection()
                builder.subtitle(context.resources.getQuantityString(R.plurals.rounds, round
                        .index + 1, round.index + 1))
                if (configuration.showProperties) {
                    builder.table(getRoundInfo(round, equals))
                }
                builder.table(getRoundTable(round))
                builder.closeSection()
            }
        }

        if (configuration.showStatistics) {
            appendStatistics(rounds)
        }

        if (configuration.showComments) {
            appendComments(rounds)
        }

        if (configuration.showSignature) {
            appendSignature(training)
        }
    }

    private fun getTrainingInfoTable(training: Training, rounds: List<Round>, equals: BooleanArray): Table {
        val info = InfoTableBuilder()
        addStaticTrainingHeaderInfo(info, training, rounds)
        addDynamicTrainingHeaderInfo(rounds, equals, info)
        return info.info
    }

    private fun addStaticTrainingHeaderInfo(info: InfoTableBuilder, training: Training, rounds: List<Round>) {
        getScoreboardOnlyHeaderInfo(info, training, rounds)

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
            info.addLine(R.string.bow_type, bow.type!!)
        }

        val arrow = training.arrow
        if (arrow != null) {
            info.addLine(R.string.arrow, arrow.name)
        }

        if (training.standardRoundId != null) {
            val standardRound = training.standardRound
            info.addLine(R.string.standard_round, standardRound!!.name)
        }
        if (!training.comment.isEmpty() && configuration.showComments) {
            info.addWrappedLine(R.string.comment, training.comment)
        }
    }

    private fun addDynamicTrainingHeaderInfo(rounds: List<Round>, equals: BooleanArray, info: InfoTableBuilder) {
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

    private fun appendStatistics(rounds: List<Round>) {
        if (rounds.size == 1) {
            builder.table(getStatisticsForRound(rounds))
        } else if (rounds.size > 1) {
            for (round in rounds) {
                builder.openSection()
                builder.subtitle(context.resources.getQuantityString(R.plurals.rounds, round
                        .index + 1, round.index + 1))
                builder.table(getStatisticsForRound(listOf(round)))
                builder.closeSection()
            }
            builder.openSection()
            builder.subtitle(context.getString(R.string.scoreboard_title_all_rounds))
            builder.table(getStatisticsForRound(rounds))
            builder.closeSection()
        }
    }

    private fun getRoundInfo(round: Round, equals: BooleanArray): Table {
        val info = InfoTableBuilder()
        if (!equals[0]) {
            info.addLine(R.string.distance, round.distance)
        }
        if (!equals[1]) {
            info.addLine(R.string.target_face, round.target.name)
        }
        if (!round.comment.isEmpty() && configuration.showComments) {
            info.addWrappedLine(R.string.comment, round.comment)
        }
        return info.info
    }

    private fun getStatisticsForRound(rounds: List<Round>): Table {
        val scoreDistribution = ScoreUtils.getSortedScoreDistribution(rounds)
        var hits = 0
        var total = 0
        for ((key, value) in scoreDistribution) {
            if (key.text != ScoringStyle.MISS_SYMBOL) {
                hits += value
            }
            total += value
        }

        val topScores = ScoreUtils.getTopScoreDistribution(scoreDistribution)

        val table = Table(false)
        var row: Table.Row = table.startRow()
        for (topScore in topScores) {
            row.addBoldCell(topScore.first!!)
        }
        row.addBoldCell(context.getString(R.string.hits))
        row.addBoldCell(context.getString(R.string.average))
        row = table.startRow()

        for (topScore in topScores) {
            row.addCell(topScore.second!!)
        }
        row.addCell("$hits/$total")
        row.addCell(getAverageScore(scoreDistribution))
        return table
    }

    private fun getAverageScore(scoreDistribution: List<Map.Entry<SelectableZone, Int>>): String {
        var sum = 0
        var count = 0
        for (entry in scoreDistribution) {
            sum += entry.value * entry.key.points
            count += entry.value
        }
        return if (count == 0) {
            "-"
        } else {
            String.format(locale, "%.2f", sum * 1.0f / count)
        }
    }

    private fun getRoundTable(round: Round): Table {
        val table = Table(false)
        appendTableHeader(table, round.shotsPerEnd)
        var carry = 0
        for (end in round.loadEnds()) {
            val row = table.startRow()
            row.addCell(end.index + 1)
            var sum = 0
            val shots = ArrayList(EndDAO.loadShots(end.id))
            if (SettingsManager.shouldSortTarget(round.target)) {
                shots.sort()
            }
            for (shot in shots) {
                appendPointsCell(row, shot, round.target)
                val points = round.target.getScoreByZone(shot.scoringRing, shot.index)
                sum += points
                carry += points
            }
            row.addCell(sum)
            row.addCell(carry)
        }
        return table
    }

    private fun appendTableHeader(table: Table, arrowsPerEnd: Int) {
        val row = table.startRow()
        row.addBoldCell(context.getString(R.string.passe))
        val sectioned = Table(false)
        sectioned.startRow().addBoldCell(context.getString(R.string.arrows), arrowsPerEnd)
        val sectionedRow = sectioned.startRow()
        for (i in 1..arrowsPerEnd) {
            sectionedRow.addBoldCell(i.toString())
        }
        sectioned.columnSpan = arrowsPerEnd
        row.addCell(sectioned)
        row.addBoldCell(context.getString(R.string.sum))
        row.addBoldCell(context.getString(R.string.carry))
    }

    private fun appendPointsCell(row: Table.Row, shot: Shot, target: Target) {
        if (shot.scoringRing == Shot.NOTHING_SELECTED) {
            row.addCell("")
            return
        }
        val points = target.zoneToString(shot.scoringRing, shot.index)
        if (configuration.showPointsColored) {
            val fillColor = target.model.getZone(shot.scoringRing).fillColor
            val color = target.model.getZone(shot.scoringRing).textColor
            row.addEndCell(points, fillColor, color, shot.arrowNumber)
        } else {
            row.addCell(points)
        }
    }

    private fun appendComments(rounds: List<Round>) {
        val comments = Table(false)
        comments.startRow().addBoldCell(context.getString(R.string.round))
                .addBoldCell(context.getString(R.string.passe))
                .addBoldCell(context.getString(R.string.comment))

        var commentsCount = 0
        for (round in rounds) {
            val ends = round.loadEnds()
            for ((_, index, _, _, _, comment) in ends) {
                if (!TextUtils.isEmpty(comment)) {
                    comments.startRow()
                            .addCell(round.index + 1)
                            .addCell(index + 1)
                            .addCell(TextCell(comment, wrapText = true))
                    commentsCount++
                }
            }
        }

        // If a minimum of one comment is present show comments table
        if (commentsCount > 0) {
            builder.table(comments)
        }
    }

    private fun getScoreboardOnlyHeaderInfo(info: InfoTableBuilder, training: Training, rounds: List<Round>) {
        val fullName = SettingsManager.profileFullName
        if (!fullName.trim { it <= ' ' }.isEmpty()) {
            info.addLine(R.string.name, fullName)
        }
        val age = SettingsManager.profileAge
        if (age != null && age < 18) {
            info.addLine(R.string.age, age)
        }
        val club = SettingsManager.profileClub
        if (!TextUtils.isEmpty(club)) {
            info.addLine(R.string.club, club)
        }
        val licenceNumber = SettingsManager.profileLicenceNumber
        if (!TextUtils.isEmpty(licenceNumber)) {
            info.addLine(R.string.licence_number, licenceNumber)
        }
        if (rounds.size > 1) {
            info.addLine(R.string.points, training.reachedScore
                    .format(locale, SettingsManager.scoreConfiguration))
        }
        info.addLine(R.string.date, training.formattedDate)
    }

    private fun appendSignature(training: Training) {
        builder.signature(training.orCreateArcherSignature, training.orCreateWitnessSignature)
    }
}
