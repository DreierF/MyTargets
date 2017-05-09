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

/**
 * <p>Public interface used by selectable items connected to {@link MultiSelector}.</p>
 */
public interface SelectableHolder {
    /**
     * <p>Turn selection mode on for this holder.</p>
     *
     * @param selectable True if selection mode is on.
     */
    void setSelectable(boolean selectable);

    /**
     * <p>Current selection mode state.</p>
     *
     * @return True if selection mode is on.
     */
    boolean isSelectable();

    /**
     * <p>Set this item to be selected (the activated state, for Views and Drawables)</p>
     *
     * @param activated True if selected/activated.
     */
    void setActivated(boolean activated);

    /**
     * <p>Return true if the item is selected/activated.</p>
     *
     * @return True if selected/activated.
     */
    boolean isActivated();

    /**
     * <p>Returns the adapter position this item is currently bound to.
     * This can (and often will) change; if attached to a {@link MultiSelector},
     * {@link MultiSelector#bindHolder(SelectableHolder, long)}
     * should be called whenever this value changes.</p>
     *
     * @return Position this holder is currently bound to.
     */
    int getAdapterPosition();

    /**
     * <p>Return the item id this item is currently bound to.
     * This can (and often will) change; if attached to a {@link MultiSelector},
     * {@link MultiSelector#bindHolder(SelectableHolder, long)}
     * should be called whenever this value changes.</p>
     *
     * @return Item id this holder is currently bound to.
     */
    long getItemId();
}
