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

import android.support.annotation.IdRes
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

/**
 * Constructor for header items
 *
 * @param itemView        Header view
 * @param expand_collapse Expand/Collapse ImageView's resource id
 */
abstract class ExpandableHeaderBindingHolder<T>(
        itemView: View,
        @IdRes expand_collapse: Int
) : HeaderBindingHolder<T>(itemView) {

    private var expandCollapseView: View
    private var expandListener: View.OnClickListener? = null
    private var expanded = false

    init {
        itemView.setOnClickListener(this)
        expandCollapseView = itemView.findViewById(expand_collapse)!!
    }

    override fun onClick(v: View) {
        if (expandListener != null) {
            expandListener!!.onClick(v)
            expandCollapseView.animate()
                    .rotation(if (expanded) 0f else 180f)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()
            expanded = !expanded
        }
    }

    override fun onRebind() {}

    fun setExpandOnClickListener(onClickListener: View.OnClickListener, expanded: Boolean) {
        expandListener = onClickListener
        this.expanded = expanded
        expandCollapseView.rotation = if (expanded) 180f else 0f
    }
}
