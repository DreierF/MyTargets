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

import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import de.dreier.mytargets.R


abstract class DynamicItemAdapter<T>(
    private val fragment: Fragment,
    private var list: MutableList<T>,
    @StringRes private val undoString: Int
) : RecyclerView.Adapter<DynamicItemHolder<T>>() {

    protected val inflater = LayoutInflater.from(fragment.context)!!

    override fun onBindViewHolder(holder: DynamicItemHolder<T>, position: Int) {
        val item = list[position]
        holder.onBind(item, position, fragment, View.OnClickListener {
            // Get the current position of our item
            // It may have changed due to a simultaneous remove operation on a previous list item,
            // which did not yet cause an item rebind
            val currentPosition = list.indexOf(item)
            list.remove(item)
            notifyItemRemoved(currentPosition)
            notifyDataSetChanged()

            Snackbar.make(fragment.view!!, undoString, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo) {
                    list.add(currentPosition, item)
                    notifyItemInserted(position)
                }.show()
        })
    }

    fun setList(list: MutableList<T>) {
        this.list = list
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
