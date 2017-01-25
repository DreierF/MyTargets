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

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package de.dreier.mytargets.utils.multiselector;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.dreier.mytargets.shared.utils.LongUtils;

public class MultiSelector extends SelectorBase {
    private static final String SELECTION_IDS = "ids";
    private Set<Long> selections = new HashSet<>();

    @Override
    public void setSelected(long id, boolean isSelected) {
        if (isSelected) {
            selections.add(id);
        } else {
            selections.remove(id);
        }
        refreshHolder(tracker.getHolder(id));
    }

    @Override
    protected boolean isSelected(long id) {
        return selections.contains(id);
    }

    public void clearSelections() {
        selections.clear();
        refreshAllHolders();
    }

    public ArrayList<Long> getSelectedIds() {
        return new ArrayList<>(selections);
    }

    @Override
    protected void saveSelectionStates(Bundle bundle) {
        bundle.putLongArray(SELECTION_IDS, LongUtils.toArray(getSelectedIds()));
    }

    @Override
    public void restoreSelectionStates(Bundle savedStates) {
        super.restoreSelectionStates(savedStates);
        long[] selectedIds = savedStates.getLongArray(SELECTION_IDS);
        restoreSelections(LongUtils.toList(selectedIds));
    }

    private void restoreSelections(List<Long> selected) {
        if (selected != null) {
            selections.clear();
            selections.addAll(selected);
            refreshAllHolders();
        }
    }
}
