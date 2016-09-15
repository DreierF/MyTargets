/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.dreier.mytargets.interfaces.ItemAdapter;
import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;

/**
 *
 * The list is automatically sorted in natural order.
 * */
public abstract class ListAdapterBase<T extends IIdProvider & Comparable<T>>
        extends RecyclerView.Adapter<SelectableViewHolder<T>> implements ItemAdapter<T> {

    protected final LayoutInflater inflater;
    private List<T> list = new ArrayList<>();

    public ListAdapterBase(Context context) {
        inflater = LayoutInflater.from(context);
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
        viewHolder.bindCursor(list.get(position));
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