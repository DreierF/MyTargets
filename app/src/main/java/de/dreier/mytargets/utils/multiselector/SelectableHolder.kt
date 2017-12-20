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

package de.dreier.mytargets.utils.multiselector

/**
 *
 * Public interface used by selectable items connected to [MultiSelector].
 */
interface SelectableHolder {

    /**
     *
     * Current selection mode state.
     *
     * @return True if selection mode is on.
     */
    /**
     *
     * Turn selection mode on for this holder.
     *
     * @param selectable True if selection mode is on.
     */
    var isSelectable: Boolean

    /**
     *
     * Return true if the item is selected/activated.
     *
     * @return True if selected/activated.
     */
    /**
     *
     * Set this item to be selected (the activated state, for Views and Drawables)
     *
     * @param activated True if selected/activated.
     */
    var isActivated: Boolean

    /**
     *
     * Returns the adapter position this item is currently bound to.
     * This can (and often will) change; if attached to a [MultiSelector],
     * [MultiSelector.bindHolder]
     * should be called whenever this value changes.
     *
     * @return Position this holder is currently bound to.
     */
    //val adapterPosition: Int //TODO

    /**
     *
     * Return the item id this item is currently bound to.
     * This can (and often will) change; if attached to a [MultiSelector],
     * [MultiSelector.bindHolder]
     * should be called whenever this value changes.
     *
     * @return Item id this holder is currently bound to.
     */
    val itemIdentifier: Long
}
