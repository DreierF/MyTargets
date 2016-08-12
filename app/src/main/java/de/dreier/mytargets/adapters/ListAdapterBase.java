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
import java.util.Comparator;
import java.util.List;

import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;


public abstract class ListAdapterBase<T extends IIdProvider>
        extends RecyclerView.Adapter<SelectableViewHolder<T>> {

    protected final LayoutInflater inflater;
    private final Comparator<T> comparator;
    private List<T> mList = new ArrayList<>();

    public ListAdapterBase(Context context) {
        this(context, (l, r) -> (int) (l.getId() - r.getId()));
    }

    public ListAdapterBase(Context context, Comparator<T> comparator) {
        this.comparator = comparator;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public long getItemId(int position) {
        return mList.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return mList.size();
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
        viewHolder.bindCursor(mList.get(position));
    }

    public T getItem(int pos) {
        return mList.get(pos);
    }

    public void add(T item) {
        int pos = Collections.binarySearch(mList, item, comparator);
        if (pos < 0) {
            mList.add(-pos - 1, item);
            notifyItemInserted(-pos - 1);
        } else {
            throw new IllegalArgumentException("Item must not be inserted twice!");
        }
    }

    public void remove(T item) {
        int pos = Collections.binarySearch(mList, item, comparator);
        if (pos < 0) {
            throw new IllegalArgumentException("Item must not be inserted twice!");
        }
        mList.remove(pos);
        notifyItemRemoved(pos);
    }

    public void setList(List<T> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public T getItemById(long id) {
        for (T item : mList) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }
}
