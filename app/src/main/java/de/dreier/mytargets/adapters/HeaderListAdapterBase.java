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

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.dreier.mytargets.interfaces.PartitionDelegate;
import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.utils.multiselector.HeaderBindingHolder;
import de.dreier.mytargets.utils.multiselector.ItemBindingHolder;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;

public abstract class HeaderListAdapterBase<HEADER extends IIdProvider, CHILD extends IIdProvider,
        HOLDER extends HeaderListAdapterBase.HeaderHolder<HEADER, CHILD>>
        extends ListAdapterBase<ItemBindingHolder<IIdProvider>, CHILD> {

    private static final int ITEM_TYPE = 2;
    private static final int HEADER_TYPE = 1;
    protected List<HOLDER> headersList = new ArrayList<>();
    private PartitionDelegate<HEADER, CHILD> partitionDelegate;
    private Comparator<HEADER> headerComparator;
    private Comparator<CHILD> childComparator;

    public HeaderListAdapterBase(PartitionDelegate<HEADER, CHILD> partitionDelegate, Comparator<HEADER> headerComparator, Comparator<CHILD> childComparator) {
        this.partitionDelegate = partitionDelegate;
        this.headerComparator = headerComparator;
        this.childComparator = childComparator;
        setHasStableIds(true);
    }

    @Override
    public CHILD getItem(int position) {
        HOLDER header = getHeaderForPosition(position);
        int pos = getHeaderRelativePosition(position);
        if (pos != 0) {
            return header.children.get(pos - 1);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        HOLDER header = getHeaderForPosition(position);
        int pos = getHeaderRelativePosition(position);
        if (pos == 0) {
            return header.item.getId();
        } else {
            return header.children.get(pos - 1).getId();
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        for (HeaderHolder header : headersList) {
            count += header.getTotalItemCount();
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        return getHeaderRelativePosition(position) == 0 ? HEADER_TYPE : ITEM_TYPE;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ItemBindingHolder<IIdProvider> onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER_TYPE) {
            return (ItemBindingHolder<IIdProvider>) getTopLevelViewHolder(parent);
        } else {
            return (ItemBindingHolder<IIdProvider>) getSecondLevelViewHolder(parent);
        }
    }

    protected abstract HeaderBindingHolder<HEADER> getTopLevelViewHolder(ViewGroup parent);

    protected abstract SelectableViewHolder<CHILD> getSecondLevelViewHolder(ViewGroup parent);

    @Override
    public void onBindViewHolder(ItemBindingHolder<IIdProvider> viewHolder, int position) {
        if (position == -1) {
            return;
        }
        HOLDER header = getHeaderForPosition(position);
        int pos = getHeaderRelativePosition(position);
        if (pos == 0) {
            viewHolder.bindItem(header.item);
        } else {
            viewHolder.bindItem(header.children.get(pos - 1));
        }
    }

    protected HOLDER getHeaderForPosition(int position) {
        int items = 0;
        for (HOLDER header : headersList) {
            if (header.getTotalItemCount() > position - items) {
                return header;
            }
            items += header.getTotalItemCount();
        }
        throw new IllegalStateException("Position is not in list!");
    }

    private int getHeaderRelativePosition(int position) {
        int items = 0;
        for (HeaderHolder header : headersList) {
            final int relativePos = position - items;
            if (header.getTotalItemCount() > relativePos) {
                return relativePos;
            }
            items += header.getTotalItemCount();
        }
        throw new IllegalStateException("Position is not in list!");
    }

    @Override
    public void addItem(CHILD item) {
        addChildToMap(item);
        notifyDataSetChanged();
    }

    @Override
    public void removeItem(CHILD item) {
        HEADER parent = partitionDelegate.getParent(item);
        int headerIndex = getHeaderIndex(getHeaderHolder(parent, childComparator));
        if (headerIndex < 0) {
            return;
        }
        HOLDER header = headersList.get(headerIndex);
        header.remove(item);
    }

    public void setList(List<CHILD> children) {
        fillChildMap(children);
        notifyDataSetChanged();
    }

    protected void fillChildMap(List<CHILD> children) {
        headersList.clear();
        for (CHILD child : children) {
            addChildToMap(child);
        }
    }

    private void addChildToMap(CHILD child) {
        HEADER parent = partitionDelegate.getParent(child);
        HOLDER parentHolder = getHeaderHolder(parent, childComparator);
        int pos = getHeaderIndex(parentHolder);
        if (pos < 0) {
            parentHolder.add(child);
            headersList.add(-pos - 1, parentHolder);
        } else {
            headersList.get(pos).add(child);
        }
    }

    @NonNull
    protected abstract HOLDER getHeaderHolder(HEADER parent, Comparator<CHILD> childComparator);

    @Override
    public CHILD getItemById(long id) {
        for (HOLDER header : headersList) {
            for (CHILD child : header.children) {
                if (child.getId() == id) {
                    return child;
                }
            }
        }
        return null;
    }


    @Override
    public int getItemPosition(CHILD item) {
        //TODO
        return 0;
    }

    int getHeaderIndex(HOLDER h) {
        return Collections.binarySearch(headersList, h,
                (holder1, holder2) -> headerComparator.compare(holder1.item, holder2.item));
    }

    protected static class HeaderHolder<HEADER , CHILD> {
        HEADER item;
        List<CHILD> children;
        private Comparator<? super CHILD> childComparator;

        HeaderHolder(HEADER parent, Comparator<? super CHILD> childComparator) {
            item = parent;
            this.childComparator = childComparator;
            children = new ArrayList<>();
        }

        public void add(CHILD item) {
            int pos = Collections.binarySearch(children, item, childComparator);
            if (pos < 0) {
                children.add(-pos - 1, item);
            } else {
                throw new IllegalArgumentException("Item must not be inserted twice!");
            }
        }

        public void remove(CHILD item) {
            children.remove(item);
        }

        int getTotalItemCount() {
            if (children.size() < 1) {
                return 0;
            }
            return 1 + children.size();
        }
    }
}
