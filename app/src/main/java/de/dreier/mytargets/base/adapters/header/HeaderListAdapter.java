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

package de.dreier.mytargets.base.adapters.header;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Comparator;

import de.dreier.mytargets.R;
import de.dreier.mytargets.databinding.ItemHeaderBinding;
import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.utils.multiselector.HeaderBindingHolder;

public abstract class HeaderListAdapter<C extends IIdProvider>
        extends HeaderListAdapterBase<HeaderListAdapter.SimpleHeader, C, HeaderListAdapterBase.HeaderHolder<HeaderListAdapter.SimpleHeader, C>> {

    public HeaderListAdapter(PartitionDelegate<SimpleHeader, C> parentPartition, Comparator<C> childComparator) {
        super(parentPartition, (h1, h2) -> h1.index.compareTo(h2.index), childComparator);
    }

    @NonNull
    @Override
    protected HeaderHolder<SimpleHeader, C> getHeaderHolder(SimpleHeader parent, Comparator<C> childComparator) {
        return new HeaderHolder<>(parent, childComparator);
    }

    @Override
    public int getItemPosition(C item) {
        int pos = 0;
        for (HeaderHolder<HeaderListAdapter.SimpleHeader, C> header : headersList) {
            if (header.getTotalItemCount() < 1) {
                continue;
            }
            pos++;
            for (C child : header.children) {
                if (child.equals(item)) {
                    return pos;
                }
                pos++;
            }
        }
        return -1;
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
