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

package de.dreier.mytargets.features.statistics

import android.content.Context
import de.dreier.mytargets.R
import de.dreier.mytargets.shared.models.dao.EndDAO
import de.dreier.mytargets.shared.models.dao.RoundDAO
import de.dreier.mytargets.shared.models.dao.TrainingDAO
import de.dreier.mytargets.shared.models.db.Round
import de.dreier.mytargets.shared.models.db.Training
import org.threeten.bp.format.DateTimeFormatter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.Writer
import java.util.*

class CsvExporter(private val context: Context) {

    @Throws(IOException::class)
    fun exportAll(file: File, roundIds: List<Long>) {
        val writer = FileWriter(file)
        writeExportData(writer, roundIds)
    }

    @Throws(IOException::class)
    fun writeExportData(writer: Writer, roundIds: List<Long>) {
        val csv = CsvBuilder(writer)
        csv.enterScope()
        csv.add(context.getString(R.string.title))
        csv.add(context.getString(R.string.date))
        csv.add(context.getString(R.string.standard_round))
        csv.add(context.getString(R.string.indoor))
        csv.add(context.getString(R.string.bow))
        csv.add(context.getString(R.string.arrow))
        csv.add(context.getString(R.string.round))
        csv.add(context.getString(R.string.distance))
        csv.add(context.getString(R.string.target))
        csv.add(context.getString(R.string.passe))
        csv.add(context.getString(R.string.timestamp))
        csv.add(context.getString(R.string.points))
        csv.add("x")
        csv.add("y")
        csv.newLine()
        csv.exitScope()
        for (t in TrainingDAO.loadTrainings()) {
            addTraining(csv, t, roundIds)
        }

        writer.flush()
        writer.close()
    }

    @Throws(IOException::class)
    private fun addTraining(csv: CsvBuilder, t: Training, roundIds: List<Long>) {
        csv.enterScope()
        // Title
        csv.add(t.title)
        // Date
        csv.add(t.date.format(DateTimeFormatter.ISO_LOCAL_DATE))
        // StandardRound
        csv.add(t.standardRound?.name ?: context.getString(R.string.practice))
        // Indoor
        csv.add(if (t.indoor) context.getString(R.string.indoor) else context.getString(R.string.outdoor))
        // Bow
        csv.add(if (t.bow == null) "" else t.bow!!.name)
        // Arrow
        csv.add(if (t.arrow == null) "" else t.arrow!!.name)
        t.loadRounds()
                .filter { roundIds.contains(it.id) }
                .forEach { addRound(csv, it) }
        csv.exitScope()
    }

    @Throws(IOException::class)
    private fun addRound(csv: CsvBuilder, r: Round) {
        csv.enterScope()
        // Round
        csv.add((r.index + 1).toString())
        // Distance
        csv.add(r.distance.toString())
        // Target
        val target = r.target
        csv.add("${target.model} (${target.diameter})")
        for (e in RoundDAO.loadEnds(r.id)) {
            csv.enterScope()
            // End
            csv.add((e.index + 1).toString())
            // Timestamp
            csv.add(e.saveTime!!.format(DateTimeFormatter.ISO_LOCAL_TIME))
            for ((_, index, _, x, y, scoringRing) in EndDAO.loadShots(e.id)) {
                csv.enterScope()
                // Score
                csv.add(target.zoneToString(scoringRing, index))

                // Coordinates (X, Y)
                csv.add(x.toString())
                csv.add(y.toString())

                csv.newLine()
                csv.exitScope()
            }
            csv.exitScope()
        }
        csv.exitScope()
    }

    private class CsvBuilder(private val writer: Writer) {
        private val scopeStack = Stack<String>()

        init {
            scopeStack.push("")
        }

        fun add(text: String?) {
            var line = scopeStack.pop()
            if (!line.isEmpty()) {
                line += ";"
            }
            line += "\"" + text + "\""
            scopeStack.push(line)
        }

        @Throws(IOException::class)
        fun newLine() {
            writer.append(scopeStack.peek())
            writer.append("\n")
        }

        fun enterScope() {
            scopeStack.push(scopeStack.peek())
        }

        fun exitScope() {
            scopeStack.pop()
        }
    }
}
