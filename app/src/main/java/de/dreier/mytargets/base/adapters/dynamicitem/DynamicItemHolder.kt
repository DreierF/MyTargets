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

package de.dreier.mytargets.base.adapters.dynamicitem

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.View

abstract class DynamicItemHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    protected var item: T? = null

    abstract fun onBind(
        item: T,
        position: Int,
        fragment: Fragment,
        removeListener: View.OnClickListener
    )
}
