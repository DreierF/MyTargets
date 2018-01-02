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

package de.dreier.mytargets.base.adapters.header

import java.util.*

class ExpandableHeaderHolder<P, C> internal constructor(parent: P, childComparator: Comparator<in C>) : HeaderListAdapterBase.HeaderHolder<P, C>(parent, childComparator) {
    internal var expanded = false

    override val totalItemCount: Int
        get() {
            if (children.size < 1) {
                return 0
            }
            return if (expanded) 1 + children.size else 1
        }
}
