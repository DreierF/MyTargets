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

package de.dreier.mytargets.utils.multiselector;

import android.util.LongSparseArray;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

class WeakHolderTracker {
    private LongSparseArray<WeakReference<SelectableHolder>> holdersById =
            new LongSparseArray<>();

    /**
     * Returns the holder with a given id.
     *
     * @param id
     * @return
     */
    public SelectableHolder getHolder(long id) {
        WeakReference<SelectableHolder> holderRef = holdersById.get(id);
        if (holderRef == null) {
            return null;
        }
        return holderRef.get();
    }

    public void bindHolder(SelectableHolder holder, long id) {
        holdersById.put(id, new WeakReference<>(holder));
    }

    public List<SelectableHolder> getTrackedHolders() {
        List<SelectableHolder> holders = new ArrayList<>();

        for (int i = 0; i < holdersById.size(); i++) {
            long key = holdersById.keyAt(i);
            SelectableHolder holder = getHolder(key);

            if (holder != null) {
                holders.add(holder);
            }
        }

        return holders;
    }


}