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

package de.dreier.mytargets.base.adapters;

import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;

/**
 * The list is automatically sorted in natural order.
 */
public abstract class SimpleListAdapterBase<T extends IIdProvider & Comparable<T>>
        extends ListAdapterBase<SelectableViewHolder<T>, T> {

    private List<T> list = new ArrayList<>();

    public SimpleListAdapterBase() {
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position) + 1;
    }

    @Override
    public final SelectableViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
        return onCreateViewHolder(parent);
    }

    protected abstract SelectableViewHolder<T> onCreateViewHolder(ViewGroup parent);

    @Override
    public final void onBindViewHolder(SelectableViewHolder<T> viewHolder, int position) {
        viewHolder.bindItem(list.get(position));
    }

    public void setList(List<T> list) {
        Collections.sort(list);
        this.list = list;
        notifyDataSetChanged();
    }

    public T getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public void addItem(T item) {
        int pos = Collections.binarySearch(list, item);
        if (pos < 0) {
            list.add(-pos - 1, item);
            notifyItemInserted(-pos - 1);
        } else {
            throw new IllegalArgumentException("Item must not be inserted twice!");
        }
    }

    @Override
    public int getItemPosition(T item) {
        int pos = Collections.binarySearch(list, item);
        if (pos >= 0) {
            return pos;
        } else {
            return -1;
        }
    }

    @Override
    public void removeItem(T item) {
        int pos = Collections.binarySearch(list, item);
        if (pos < 0) {
            throw new IllegalArgumentException("Item has already been removed!");
        }
        list.remove(pos);
        notifyItemRemoved(pos);
    }

    @Override
    public T getItemById(long id) {
        for (T item : list) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }
}