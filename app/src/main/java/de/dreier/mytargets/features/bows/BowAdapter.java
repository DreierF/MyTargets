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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.adapters.SimpleListAdapterBase;
import de.dreier.mytargets.databinding.ItemImageDetailsBinding;
import de.dreier.mytargets.features.scoreboard.HtmlInfoBuilder;
import de.dreier.mytargets.shared.models.db.Bow;
import de.dreier.mytargets.shared.models.db.SightMark;
import de.dreier.mytargets.utils.Utils;
import de.dreier.mytargets.utils.multiselector.OnItemClickListener;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;
import de.dreier.mytargets.utils.multiselector.SelectorBase;

class BowAdapter extends SimpleListAdapterBase<Bow> {

    private final OnItemClickListener<Bow> clickListener;
    private final SelectorBase selector;

    public BowAdapter(SelectorBase selector, OnItemClickListener<Bow> clickListener) {
        this.selector = selector;
        this.clickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image_details, parent, false);
        return new ViewHolder(itemView);
    }

    class ViewHolder extends SelectableViewHolder<Bow> {

        final ItemImageDetailsBinding binding;

        public ViewHolder(View itemView) {
            super(itemView, selector, clickListener);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void bindItem() {
            binding.name.setText(item.name);
            binding.image.setImageDrawable(item.getDrawable());
            binding.details.setVisibility(View.VISIBLE);

            HtmlInfoBuilder info = new HtmlInfoBuilder();
            info.addLine(R.string.bow_type, item.type);
            if (!item.brand.trim().isEmpty()) {
                info.addLine(R.string.brand, item.brand);
            }
            if (!item.size.trim().isEmpty()) {
                info.addLine(R.string.size, item.size);
            }
            for (SightMark s : item.getSightMarks()) {
                info.addLine(s.distance.toString(), s.value);
            }
            binding.details.setText(Utils.fromHtml(info.toString()));
        }
    }
}
