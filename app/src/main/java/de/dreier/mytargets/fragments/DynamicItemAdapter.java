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

package de.dreier.mytargets.fragments;

import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.DynamicItemHolder;

abstract class DynamicItemAdapter<T> extends RecyclerView.Adapter<DynamicItemHolder<T>> {
    private final Fragment fragment;
    private final List<T> list;
    final LayoutInflater inflater;
    private final int undoString;

    public DynamicItemAdapter(Fragment fragment, List<T> list, @StringRes int undoString) {
        this.fragment = fragment;
        this.list = list;
        this.undoString = undoString;
        this.inflater = LayoutInflater.from(fragment.getContext());
    }

    @Override
    public void onBindViewHolder(DynamicItemHolder<T> holder, int position) {
        final T item = list.get(position);
        holder.onBind(item, position, fragment, view -> {
            list.remove(position);

            if (position + 1 <= list.size()) {
                notifyItemRangeChanged(position + 1, list.size() - position);
            }
            notifyItemRemoved(position);

            Snackbar.make(fragment.getView(), undoString, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo, v -> {
                        list.add(position, item);
                        notifyItemInserted(position);
                    }).show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
