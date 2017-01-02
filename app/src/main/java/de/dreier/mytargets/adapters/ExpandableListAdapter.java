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

package de.dreier.mytargets.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.Comparator;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.databinding.ItemHeaderExpandableBinding;
import de.dreier.mytargets.interfaces.PartitionDelegate;
import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.utils.multiselector.ExpandableHeaderBindingHolder;
import de.dreier.mytargets.utils.multiselector.ItemBindingHolder;

public abstract class ExpandableListAdapter<P extends IIdProvider, C extends IIdProvider>
        extends HeaderListAdapterBase<P, C, ExpandableHeaderHolder<P, C>> {

    public ExpandableListAdapter(PartitionDelegate<P, C> partitionDelegate, Comparator<P> headerComparator, Comparator<C> childComparator) {
        super(partitionDelegate, headerComparator, childComparator);
    }

    @Override
    public final void onBindViewHolder(ItemBindingHolder<IIdProvider> viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        if (position == -1) {
            return;
        }
        if (viewHolder instanceof ExpandableHeaderBindingHolder) {
            ExpandableHeaderHolder<P, C> header = getHeaderForPosition(position);
            ((ExpandableHeaderBindingHolder) viewHolder)
                    .setExpandOnClickListener(v -> expandOrCollapse(header), header.expanded);
        }
    }

    @Override
    public int getItemPosition(C item) {
        int pos = 0;
        for (HeaderHolder<P, C> header : headersList) {
            if (header.getTotalItemCount() < 1) {
                continue;
            }
            pos++;
            if (header.getTotalItemCount() == 1) {
                continue;
            }
            for (C child : header.children) {
                if (child.equals(item)) {
                    return pos;
                }
                pos++;
            }
        }
        return -1;
    }

    private void expandOrCollapse(ExpandableHeaderHolder<P, C> header) {
        int childLength = header.children.size();
        if (!header.expanded) {
            notifyItemRangeInserted(getAbsolutePosition(header) + 1, childLength);
        } else {
            notifyItemRangeRemoved(getAbsolutePosition(header) + 1, childLength);
        }
        header.expanded = !header.expanded;
    }

    @Override
    public void setList(List<C> children) {
        List<Long> oldExpanded = getExpandedIds();
        fillChildMap(children);
        setExpandedIds(oldExpanded);
        notifyDataSetChanged();
    }

    public void setList(List<C> children, boolean opened) {
        fillChildMap(children);
        expandAll(opened);
        notifyDataSetChanged();
    }

    public List<Long> getExpandedIds() {
        return Stream.of(headersList)
                .filter(h -> h.expanded)
                .map(h -> h.item.getId())
                .collect(Collectors.toList());
    }

    public void setExpandedIds(List<Long> expanded) {
        for (int i = 0; i < headersList.size(); i++) {
            final ExpandableHeaderHolder<P, C> header = headersList.get(i);
            header.expanded = expanded.contains(header.item.getId());
        }
    }

    private void expandAll(boolean expanded) {
        for (ExpandableHeaderHolder header : headersList) {
            header.expanded = expanded;
        }
    }

    public void expandFirst() {
        if (!headersList.get(0).expanded) {
            expandOrCollapse(headersList.get(0));
        }
    }

    private int getAbsolutePosition(ExpandableHeaderHolder<P, C> h) {
        int headerIndex = getHeaderIndex(h);
        int pos = 0;
        for (int i = 0; i < headerIndex; i++) {
            pos += headersList.get(i).getTotalItemCount();
        }
        return pos;
    }

    @NonNull
    @Override
    protected ExpandableHeaderHolder<P, C> getHeaderHolder(P parent, Comparator<C> childComparator) {
        return new ExpandableHeaderHolder<>(parent, childComparator);
    }

    @Override
    protected HeaderViewHolder<P> getTopLevelViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_header_expandable, parent, false);
        return new HeaderViewHolder<>(itemView);
    }

    private static class HeaderViewHolder<HEADER> extends ExpandableHeaderBindingHolder<HEADER> {
        private final ItemHeaderExpandableBinding binding;

        HeaderViewHolder(View itemView) {
            super(itemView, R.id.expand_collapse);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void bindItem() {
            binding.header.setText(item.toString());
        }
    }

}
