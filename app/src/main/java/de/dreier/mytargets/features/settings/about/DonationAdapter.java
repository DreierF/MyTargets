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

package de.dreier.mytargets.features.settings.about;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.dreier.mytargets.R;
import de.dreier.mytargets.features.settings.DonateActivity;
import de.dreier.mytargets.databinding.ItemDonationBinding;

public class DonationAdapter extends RecyclerView.Adapter<DonationAdapter.DonationViewHolder> {

    private final LayoutInflater inflater;
    private final OnItemClickListener listener;

    public DonationAdapter(Context context, OnItemClickListener listener) {
        inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @Override
    public DonationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = inflater.inflate(R.layout.item_donation, parent, false);
        return new DonationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DonationViewHolder holder, int position) {
        holder.itemView.setEnabled(position < 4);
        holder.itemView.setOnClickListener(view -> listener.onItemClicked(position));

        switch (position) {
            case 0:
                holder.binding.desc.setText(R.string.donate_2);
                break;
            case 1:
                holder.binding.desc.setText(R.string.donate_5);
                break;
            case 2:
                holder.binding.desc.setText(R.string.donate_10);
                break;
            case 3:
                holder.binding.desc.setText(R.string.donate_20);
                break;
            case 4:
                holder.binding.desc.setText(R.string.donate_text);
                break;
        }

        if (position < 4) {
            String sku = DonateActivity.donations.get(position);
            holder.binding.price.setText(DonateActivity.prices.get(sku));
        } else {
            holder.binding.price.setText("");
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    static class DonationViewHolder extends RecyclerView.ViewHolder {
        ItemDonationBinding binding;

        public DonationViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }


    }

    public interface OnItemClickListener {
        void onItemClicked(int position);
    }
}
