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

package de.dreier.mytargets.features.distance

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

import de.dreier.mytargets.R


class DistanceItemDecorator(context: Context, private val gridSize: Int) : RecyclerView.ItemDecoration() {
    private val spaceHorizontal: Int = context.resources
            .getDimension(R.dimen.card_padding_horizontal).toInt()
    private val spaceVertical: Int = context.resources
            .getDimension(R.dimen.card_padding_vertical).toInt()

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        val position = parent.getChildAdapterPosition(view)
        if (position % gridSize == 0) {
            outRect.left = spaceHorizontal
        } else {
            outRect.left = spaceHorizontal / 4
        }
        if ((position + 1) % gridSize == 0) {
            outRect.right = spaceHorizontal
        } else {
            outRect.right = spaceHorizontal / 4
        }
        outRect.bottom = spaceVertical

        // Add top margin only for the first item to avoid double space between items
        if (position < gridSize) {
            outRect.top = spaceVertical
        }
    }
}
