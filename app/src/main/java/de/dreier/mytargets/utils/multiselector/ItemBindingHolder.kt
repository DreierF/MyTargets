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

import androidx.recyclerview.widget.RebindReportingHolder
import android.view.View

abstract class ItemBindingHolder<T> internal constructor(itemView: View) :
    RebindReportingHolder(itemView), SelectableHolder, View.OnClickListener,
    View.OnLongClickListener {
    var item: T? = null
        protected set

    override fun onLongClick(v: View): Boolean {
        return true
    }

    open fun internalBindItem(t: T) {
        item = t
        bindItem(t)
    }

    fun bindItem() {
        bindItem(item!!)
    }

    abstract fun bindItem(item: T)

    override val itemIdentifier: Long
        get() = super.getItemId()
}
