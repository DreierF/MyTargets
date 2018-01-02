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

package de.dreier.mytargets.shared.analysis.aggregation

import android.support.annotation.ColorInt

import de.dreier.mytargets.shared.targets.drawable.CanvasWrapper

class NOPResultRenderer : IAggregationResultRenderer {
    override fun onPrepareDraw() {}

    override fun onDraw(canvas: CanvasWrapper) {}

    override fun setColor(@ColorInt color: Int) {}
}
