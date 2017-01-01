/*
 * Copyright (C) 2016 Florian Dreier
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

package de.dreier.mytargets.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Comparator;

import de.dreier.mytargets.R;
import de.dreier.mytargets.databinding.ItemHeaderBinding;
import de.dreier.mytargets.interfaces.PartitionDelegate;
import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.utils.multiselector.HeaderBindingHolder;

public abstract class HeaderListAdapter<CHILD extends IIdProvider>
        extends HeaderListAdapterBase<HeaderListAdapter.SimpleHeader, CHILD, HeaderListAdapterBase.HeaderHolder<HeaderListAdapter.SimpleHeader, CHILD>> {

    public HeaderListAdapter(PartitionDelegate<SimpleHeader, CHILD> parentPartition, Comparator<CHILD> childComparator) {
        super(parentPartition, (h1, h2) -> h1.index.compareTo(h2.index), childComparator);
    }

    @NonNull
    @Override
    protected HeaderHolder<SimpleHeader, CHILD> getHeaderHolder(SimpleHeader parent, Comparator<CHILD> childComparator) {
        return new HeaderHolder<>(parent, childComparator);
    }

    @Override
    protected HeaderViewHolder getTopLevelViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_header, parent, false);
        return new HeaderViewHolder(itemView);
    }

    private class HeaderViewHolder extends HeaderBindingHolder<SimpleHeader> {
        private final ItemHeaderBinding binding;

        HeaderViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void bindItem() {
            binding.header.setText(item.title);
        }
    }

    public static class SimpleHeader implements IIdProvider {
        Long index;
        String title;

        public SimpleHeader(Long index, String title) {
            this.index = index;
            this.title = title;
        }

        @Override
        public Long getId() {
            return index;
        }
    }
}
