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

import android.support.annotation.StringRes
import de.dreier.mytargets.features.scoreboard.builder.model.Table
import de.dreier.mytargets.shared.SharedApplicationInstance

class InfoTableBuilder {
    val info = Table(true)

    fun addLine(key: Int, value: Any) {
        getKeyValueLine(info.startRow(), key, value)
    }

    fun addLine(key: String, value: Any) {
        getKeyValueLine(info.startRow(), key, value)
    }

    private fun getKeyValueLine(row: Table.Row, key: String, value: Any) {
        row.addCell(key).addBoldCell(value.toString())
    }

    private fun getKeyValueLine(row: Table.Row, @StringRes key: Int, value: Any) {
        getKeyValueLine(row, SharedApplicationInstance.getStr(key), value)
    }

    override fun toString(): String {
        return info.toString()
    }
}
