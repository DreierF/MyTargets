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

package de.dreier.mytargets.adapters;

import java.util.Comparator;

public class ExpandableHeaderHolder<HEADER, CHILD> extends HeaderListAdapterBase.HeaderHolder<HEADER, CHILD> {
    boolean expanded = false;

    ExpandableHeaderHolder(HEADER parent, Comparator<? super CHILD> childComparator) {
        super(parent, childComparator);
    }

    @Override
    int getTotalItemCount() {
        if (children.size() < 1) {
            return 0;
        }
        return expanded ? 1 + children.size() : 1;
    }
}
