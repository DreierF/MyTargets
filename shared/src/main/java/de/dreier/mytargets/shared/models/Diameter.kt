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

package de.dreier.mytargets.shared.models

object Diameter {
    internal const val MINI_VALUE = -6
    val MINI = Dimension(MINI_VALUE.toFloat(), null as Dimension.Unit?)
    internal const val SMALL_VALUE = -5
    val SMALL = Dimension(SMALL_VALUE.toFloat(), null as Dimension.Unit?)
    internal const val MEDIUM_VALUE = -4
    val MEDIUM = Dimension(MEDIUM_VALUE.toFloat(), null as Dimension.Unit?)
    internal const val LARGE_VALUE = -3
    val LARGE = Dimension(LARGE_VALUE.toFloat(), null as Dimension.Unit?)
    internal const val XLARGE_VALUE = -2
    val XLARGE = Dimension(XLARGE_VALUE.toFloat(), null as Dimension.Unit?)
}
