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

package de.dreier.mytargets.utils.multiselector

import android.view.View

/**
 * Constructor for header items
 *
 * @param itemView Header view
 */
abstract class HeaderBindingHolder<T>(itemView: View) : ItemBindingHolder<T>(itemView) {

    override fun onClick(v: View) {}

    @Suppress("RedundantSetter")
    override var isSelectable: Boolean
        get() = false
        set(value) {}

    override fun onRebind() {}

    override var isActivated: Boolean
        get() = itemView.isActivated
        set(value) {
            itemView.isActivated = value
        }
}
