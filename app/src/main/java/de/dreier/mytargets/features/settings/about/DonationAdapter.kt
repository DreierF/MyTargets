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

package de.dreier.mytargets.features.settings.about

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.dreier.mytargets.R
import de.dreier.mytargets.databinding.ItemDonationBinding

class DonationAdapter(context: Context, private val listener: (Int) -> Unit) : RecyclerView.Adapter<DonationAdapter.DonationViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonationViewHolder {
        val itemView = inflater.inflate(R.layout.item_donation, parent, false)
        return DonationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DonationViewHolder, position: Int) {
        holder.itemView.isEnabled = position < 4
        holder.itemView.setOnClickListener { listener.invoke(position) }

        when (position) {
            0 -> holder.binding.desc.setText(R.string.donate_2)
            1 -> holder.binding.desc.setText(R.string.donate_5)
            2 -> holder.binding.desc.setText(R.string.donate_10)
            3 -> holder.binding.desc.setText(R.string.donate_20)
            4 -> holder.binding.desc.setText(R.string.donate_text)
        }

        if (position < 4) {
            val sku = DonateActivity.donations[position]
            holder.binding.price.text = DonateActivity.prices[sku]
        } else {
            holder.binding.price.text = ""
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return 5
    }

    class DonationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding = ItemDonationBinding.bind(itemView)
    }
}
