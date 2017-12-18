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

package de.dreier.mytargets.features.settings.backup;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.databinding.ItemDetailsSecondaryActionBinding;

public class BackupAdapter extends RecyclerView.Adapter<BackupAdapter.BackupViewHolder> {

    private final OnItemClickListener<BackupEntry> primaryActionListener;
    private final OnItemClickListener<BackupEntry> secondaryActionListener;
    private LayoutInflater inflater;
    private DateFormat formatDateTime;
    private List<BackupEntry> backupEntries = new ArrayList<>();

    public BackupAdapter(Context context, OnItemClickListener<BackupEntry> primaryActionListener, OnItemClickListener<BackupEntry> secondaryActionListener) {
        this.inflater = LayoutInflater.from(context);
        this.primaryActionListener = primaryActionListener;
        this.secondaryActionListener = secondaryActionListener;
        this.formatDateTime = SimpleDateFormat.getDateInstance(DateFormat.LONG);
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public BackupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_details_secondary_action, parent, false);
        return new BackupViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BackupViewHolder holder, int position) {
        BackupEntry p = backupEntries.get(position);
        final String modified = formatDateTime.format(p.getModifiedDate());
        holder.binding.name.setText(modified);
        holder.binding.details.setText(p.getHumanReadableSize());
        holder.binding.primaryAction
                .setOnClickListener(view -> primaryActionListener.onItemClicked(p));
        holder.binding.secondaryAction
                .setOnClickListener(view -> secondaryActionListener.onItemClicked(p));
    }

    @Override
    public long getItemId(int position) {
        return backupEntries.get(position).getModifiedDate().getTime();
    }

    @Override
    public int getItemCount() {
        return backupEntries.size();
    }

    public void setList(List<BackupEntry> list) {
        this.backupEntries = list;
        notifyDataSetChanged();
    }

    public void remove(BackupEntry backupEntry) {
        int index = backupEntries.indexOf(backupEntry);
        if (index > -1) {
            backupEntries.remove(index);
            notifyItemRemoved(index);
        }
    }

    public interface OnItemClickListener<T> {
        void onItemClicked(T item);
    }

    public static class BackupViewHolder extends RecyclerView.ViewHolder {

        public final ItemDetailsSecondaryActionBinding binding;

        public BackupViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
