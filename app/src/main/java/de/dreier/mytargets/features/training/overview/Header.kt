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
package de.dreier.mytargets.features.training.overview

import de.dreier.mytargets.shared.models.IIdProvider

data class Header(
        override val id: Long?,
        val title: String
) : IIdProvider, Comparable<Header> {

    override fun toString(): String {
        return title
    }

    override fun compareTo(other: Header): Int {
        return (id!! - other.id!!).toInt()
    }
}
