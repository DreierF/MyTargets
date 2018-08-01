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

package androidx.core.graphics

import android.graphics.Rect
import android.graphics.RectF

/**
 * Returns a [Rect] representation of this rectangle. The resulting rect will be sized such
 * that this rect can fit within it.
 */
inline fun RectF.toRect(): Rect {
    val r = Rect()
    roundOut(r)
    return r
}