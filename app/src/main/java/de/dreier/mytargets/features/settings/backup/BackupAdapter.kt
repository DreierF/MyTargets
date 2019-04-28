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

package de.dreier.mytargets.features.settings.backup

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.dreier.mytargets.R
import de.dreier.mytargets.databinding.ItemDetailsSecondaryActionBinding
import org.threeten.bp.ZoneOffset
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class BackupAdapter(
    context: Context,
    private val primaryActionListener: OnItemClickListener<BackupEntry>,
    private val secondaryActionListener: OnItemClickListener<BackupEntry>
) : RecyclerView.Adapter<BackupAdapter.BackupViewHolder>() {
    private val inflater = LayoutInflater.from(context)
    private val formatDateTime = SimpleDateFormat.getDateInstance(DateFormat.LONG)
    private var backupEntries: MutableList<BackupEntry> = ArrayList()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BackupViewHolder {
        val v = inflater.inflate(R.layout.item_details_secondary_action, parent, false)
        return BackupViewHolder(v)
    }

    override fun onBindViewHolder(holder: BackupViewHolder, position: Int) {
        val p = backupEntries[position]
        val modified = formatDateTime.format(p.lastModifiedAt)
        holder.binding.name.text = modified
        holder.binding.details.text = p.humanReadableSize
        holder.binding.primaryAction
            .setOnClickListener { primaryActionListener.onItemClicked(p) }
        holder.binding.secondaryAction
            .setOnClickListener { secondaryActionListener.onItemClicked(p) }
    }

    override fun getItemId(position: Int): Long {
        return backupEntries[position].lastModifiedAt
    }

    override fun getItemCount(): Int {
        return backupEntries.size
    }

    fun setList(list: MutableList<BackupEntry>) {
        this.backupEntries = list
        notifyDataSetChanged()
    }

    fun remove(backupEntry: BackupEntry) {
        val index = backupEntries.indexOf(backupEntry)
        if (index > -1) {
            backupEntries.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    class BackupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemDetailsSecondaryActionBinding.bind(itemView)
    }
}

interface OnItemClickListener<in T> {
    fun onItemClicked(item: T)
}
