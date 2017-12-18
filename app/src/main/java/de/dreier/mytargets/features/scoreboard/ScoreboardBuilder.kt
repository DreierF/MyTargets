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

package de.dreier.mytargets.features.scoreboard

import de.dreier.mytargets.features.scoreboard.builder.model.Table
import de.dreier.mytargets.shared.models.db.Signature

interface ScoreboardBuilder {
    fun title(title: String)
    fun openSection()
    fun closeSection()
    fun subtitle(subtitle: String)
    fun table(table: Table)
    fun signature(archerSignature: Signature, witnessSignature: Signature)
}
