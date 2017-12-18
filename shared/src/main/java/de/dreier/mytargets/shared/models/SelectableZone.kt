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

package de.dreier.mytargets.shared.models

import de.dreier.mytargets.shared.targets.zone.ZoneBase

class SelectableZone(
        val index: Int,
        val zone: ZoneBase,
        val text: String,
        val points: Int
) : Comparable<SelectableZone> {

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }

        val that = o as SelectableZone?
        return points == that!!.points && text == that.text
    }

    override fun hashCode(): Int {
        return 31 * points + text.hashCode()
    }

    override fun compareTo(other: SelectableZone): Int {
        return if (other.index == index) {
            0
        } else if (other.index >= 0 && index >= 0) {
            index - other.index
        } else {
            other.index - index
        }
    }
}
