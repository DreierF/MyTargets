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

package de.dreier.mytargets.features.bows

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.dreier.mytargets.R
import de.dreier.mytargets.app.ApplicationInstance
import de.dreier.mytargets.base.adapters.SimpleListAdapterBase
import de.dreier.mytargets.databinding.ItemImageDetailsBinding
import de.dreier.mytargets.features.training.details.HtmlInfoBuilder
import de.dreier.mytargets.shared.models.db.Bow
import de.dreier.mytargets.utils.Utils
import de.dreier.mytargets.utils.multiselector.MultiSelector
import de.dreier.mytargets.utils.multiselector.OnItemClickListener
import de.dreier.mytargets.utils.multiselector.OnItemLongClickListener
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder

internal class BowAdapter(
        private val selector: MultiSelector,
        private val clickListener: OnItemClickListener<Bow>,
        private val longClickListener: OnItemLongClickListener<Bow>
) : SimpleListAdapterBase<Bow>(compareBy(Bow::name, Bow::id)) {

    val bowDAO = ApplicationInstance.db.bowDAO()

    public override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_image_details, parent, false)
        return ViewHolder(itemView)
    }

    internal inner class ViewHolder(itemView: View) : SelectableViewHolder<Bow>(itemView, selector, clickListener, longClickListener) {

        val binding = ItemImageDetailsBinding.bind(itemView)

        override fun bindItem(item: Bow) {
            binding.name.text = item.name
            binding.image.setImageDrawable(item.thumbnail!!.roundDrawable)
            binding.details.visibility = View.VISIBLE

            val info = HtmlInfoBuilder()
            info.addLine(R.string.bow_type, item.type!!)
            if (!item.brand!!.trim { it <= ' ' }.isEmpty()) {
                info.addLine(R.string.brand, item.brand!!)
            }
            if (!item.size!!.trim { it <= ' ' }.isEmpty()) {
                info.addLine(R.string.size, item.size!!)
            }
            val sightMarks = bowDAO.loadSightMarks(item.id).sortedBy { it.distance }
            for ((_, _, distance, value) in sightMarks) {
                info.addLine(distance.toString(), value!!)
            }
            binding.details.text = Utils.fromHtml(info.toString())
        }
    }
}
