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

package de.dreier.mytargets.base.adapters

import androidx.recyclerview.widget.RecyclerView

abstract class ListAdapterBase<S : RecyclerView.ViewHolder, T> : RecyclerView.Adapter<S>() {
    abstract fun removeItem(item: T)

    abstract fun addItem(item: T)

    abstract fun getItem(position: Int): T?

    abstract fun getItemById(id: Long): T?

    abstract fun getItemPosition(item: T): Int

    abstract fun setList(list: List<T>)
}
