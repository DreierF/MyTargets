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

import android.os.Bundle;

/**
 * <p>A Selector that only allows for one position at a time to be selected. </p>
 * <p>Any time {@link SelectorBase#setSelected(long, boolean)} is called, all other selected positions are set to false.</p>
 */
public class SingleSelector extends SelectorBase {

    private static final String SELECTION_ID = "selection_id";
    private long selectedId = -1L;

    @Override
    public void setSelected(long id, boolean isSelected) {
        long oldId = selectedId;
        if (isSelected) {
            selectedId = id;
        } else {
            selectedId = -1;
        }
        refreshHolder(tracker.getHolder(oldId));
        refreshHolder(tracker.getHolder(selectedId));
    }

    @Override
    public boolean isSelected(long id) {
        return id == selectedId && selectedId != -1;
    }

    @Override
    protected void saveSelectionStates(Bundle bundle) {
        bundle.putLong(SELECTION_ID, selectedId);
    }

    @Override
    public void restoreSelectionStates(Bundle savedStates) {
        super.restoreSelectionStates(savedStates);
        selectedId = savedStates.getLong(SELECTION_ID);
    }

    public Long getSelectedId() {
        return selectedId == -1 ? null : selectedId;
    }
}