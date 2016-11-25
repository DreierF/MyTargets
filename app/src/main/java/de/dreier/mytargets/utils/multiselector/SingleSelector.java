/*
 * Copyright (C) 2016 Florian Dreier
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

/**
 * <p>A MultiSelector that only allows for one position at a time to be selected. </p>
 * <p>Any time {@link #setSelected(int, long, boolean)} is called, all other selected positions are set to false.</p>
 */
public class SingleSelector extends MultiSelector {

    private int selectedPosition = -1;

    @Override
    public void setSelected(int position, long id, boolean isSelected) {
        if (isSelected) {
            for (Long selectedId : getSelectedIds()) {
                if (selectedId != position) {
                    super.setSelected(selectedPosition, selectedId, false);
                }
            }
            selectedPosition = position;
        } else {
            selectedPosition = -1;
        }
        super.setSelected(position, id, isSelected);
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }
}