/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.utils.SelectableViewHolder;


public abstract class NowListAdapter<T extends IIdProvider>
        extends RecyclerView.Adapter<SelectableViewHolder<T>> {

    private List<T> mList = new ArrayList<>();

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

    public void add(int pos, T item) {
        mList.add(pos, item);
        notifyItemInserted(pos);
    }

    public void remove(int pos) {
        mList.remove(pos);
        notifyItemRemoved(pos);
    }

    public void setList(List<T> list) {
        mList = list;
    }

}
