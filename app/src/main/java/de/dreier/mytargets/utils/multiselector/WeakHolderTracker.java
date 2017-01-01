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

import android.util.SparseArray;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

class WeakHolderTracker {
    private SparseArray<WeakReference<SelectableHolder>> mHoldersByPosition =
            new SparseArray<>();

    /**
     * Returns the holder with a given position. If non-null, the returned
     * holder is guaranteed to have getPosition() == position.
     *
     * @param position
     * @return
     */
    public SelectableHolder getHolder(int position) {
        WeakReference<SelectableHolder> holderRef = mHoldersByPosition.get(position);
        if (holderRef == null) {
            return null;
        }

        SelectableHolder holder = holderRef.get();
        if (holder == null || holder.getAdapterPosition() != position) {
            mHoldersByPosition.remove(position);
            return null;
        }

        return holder;
    }

    public void bindHolder(SelectableHolder holder, int position) {
        mHoldersByPosition.put(position, new WeakReference<>(holder));
    }

    public List<SelectableHolder> getTrackedHolders() {
        List<SelectableHolder> holders = new ArrayList<>();

        for (int i = 0; i < mHoldersByPosition.size(); i++) {
            int key = mHoldersByPosition.keyAt(i);
            SelectableHolder holder = getHolder(key);

            if (holder != null) {
                holders.add(holder);
            }
        }

        return holders;
    }


}