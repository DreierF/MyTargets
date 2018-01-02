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

package de.dreier.mytargets.features.scoreboard.builder.model

import java.util.*

sealed class Cell {
    var columnSpan = 1
}

data class TextCell(val content: String, var bold: Boolean) : Cell()

data class EndCell(val score: String, val fillColor: Int, val textColor: Int, val arrowNumber: String?) : Cell()

data class Table(val wrapContent: Boolean) : Cell() {

    var rows: MutableList<Row> = ArrayList()

    inner class Row {

        var cells: MutableList<Cell> = ArrayList()

        fun addCell(cell: Cell) {
            cells.add(cell)
        }

        fun addCell(content: String): Row {
            cells.add(TextCell(content, false))
            return this
        }

        fun addCell(number: Int): Row {
            return addCell(number.toString())
        }

        fun addBoldCell(content: String): Row {
            cells.add(TextCell(content, true))
            return this
        }

        fun addBoldCell(content: String, columnSpan: Int): Row {
            val textCell = TextCell(content, true)
            textCell.columnSpan = columnSpan
            cells.add(textCell)
            return this
        }

        fun addEndCell(content: String, fillColor: Int, textColor: Int, arrowNumber: String?) {
            cells.add(EndCell(content, fillColor, textColor, arrowNumber))
        }
    }

    fun startRow(): Row {
        val row = Row()
        rows.add(row)
        return row
    }
}
