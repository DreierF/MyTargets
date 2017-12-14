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

package de.dreier.mytargets.features.bows;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.adapters.SimpleListAdapterBase;
import de.dreier.mytargets.databinding.ItemImageDetailsBinding;
import de.dreier.mytargets.features.training.details.HtmlInfoBuilder;
import de.dreier.mytargets.shared.models.db.Bow;
import de.dreier.mytargets.shared.models.db.SightMark;
import de.dreier.mytargets.utils.Utils;
import de.dreier.mytargets.utils.multiselector.MultiSelector;
import de.dreier.mytargets.utils.multiselector.OnItemClickListener;
import de.dreier.mytargets.utils.multiselector.OnItemLongClickListener;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;

class BowAdapter extends SimpleListAdapterBase<Bow> {

    private final OnItemClickListener<Bow> clickListener;
    private final OnItemLongClickListener<Bow> longClickListener;
    private final MultiSelector selector;

    public BowAdapter(MultiSelector selector, OnItemClickListener<Bow> clickListener, OnItemLongClickListener<Bow> longClickListener) {
        this.selector = selector;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image_details, parent, false);
        return new ViewHolder(itemView);
    }

    class ViewHolder extends SelectableViewHolder<Bow> {

        final ItemImageDetailsBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView, selector, clickListener, longClickListener);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void bindItem() {
            binding.name.setText(item.getName());
            binding.image.setImageDrawable(item.getDrawable());
            binding.details.setVisibility(View.VISIBLE);

            HtmlInfoBuilder info = new HtmlInfoBuilder();
            info.addLine(R.string.bow_type, item.getType());
            if (!item.getBrand().trim().isEmpty()) {
                info.addLine(R.string.brand, item.getBrand());
            }
            if (!item.getSize().trim().isEmpty()) {
                info.addLine(R.string.size, item.getSize());
            }
            for (SightMark s : item.getSightMarks()) {
                info.addLine(s.distance.toString(), s.value);
            }
            binding.details.setText(Utils.fromHtml(info.toString()));
        }
    }
}
