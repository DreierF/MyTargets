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

package de.dreier.mytargets.base.gallery.adapters

import android.app.Activity
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import de.dreier.mytargets.R
import de.dreier.mytargets.base.gallery.HorizontalImageViewHolder
import de.dreier.mytargets.utils.ImageList
import java.io.File

typealias OnItemClickListener = (Int) -> Unit

class HorizontalListAdapters(
    private val activity: Activity,
    private val images: ImageList,
    private val clickListener: OnItemClickListener
) : RecyclerView.Adapter<HorizontalImageViewHolder>() {

    private var selectedItem = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalImageViewHolder {
        return HorizontalImageViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_image_horizontal, parent, false)
        )
    }

    override fun onBindViewHolder(holder: HorizontalImageViewHolder, position: Int) {
        if (position == images.size()) {
            holder.image.visibility = View.GONE
            holder.camera.visibility = View.VISIBLE
        } else {
            holder.camera.visibility = View.GONE
            holder.image.visibility = View.VISIBLE
            Picasso.with(activity)
                .load(File(activity.filesDir, images[position].fileName))
                .fit()
                .into(holder.image)
            val matrix = ColorMatrix()
            if (selectedItem != position) {
                matrix.setSaturation(0f)
                holder.image.alpha = 0.5f
            } else {
                matrix.setSaturation(1f)
                holder.image.alpha = 1f
            }
            val filter = ColorMatrixColorFilter(matrix)
            holder.image.colorFilter = filter
        }

        holder.itemView.setOnClickListener { clickListener.invoke(position) }
    }

    override fun getItemCount(): Int {
        return images.size() + 1
    }

    fun setSelectedItem(position: Int) {
        selectedItem = position
        notifyDataSetChanged()
    }
}
