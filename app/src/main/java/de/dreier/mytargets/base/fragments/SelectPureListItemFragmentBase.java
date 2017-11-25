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

package de.dreier.mytargets.base.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.adapters.SimpleListAdapterBase;
import de.dreier.mytargets.databinding.FragmentListBinding;
import de.dreier.mytargets.databinding.ItemImageSimpleBinding;
import de.dreier.mytargets.shared.models.IIdProvider;
import de.dreier.mytargets.shared.models.IImageProvider;
import de.dreier.mytargets.utils.SlideInItemAnimator;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;

/**
 *
 * */
public abstract class SelectPureListItemFragmentBase<T extends IIdProvider & IImageProvider & Comparable<T>>
        extends SelectItemFragmentBase<T, SimpleListAdapterBase<T>> {

    protected FragmentListBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemAnimator(new SlideInItemAnimator());
        adapter = new ListAdapter();
        binding.recyclerView.setAdapter(adapter);
        binding.fab.setVisibility(View.GONE);
        ToolbarUtils.showUpAsX(this);
        return binding.getRoot();
    }

    private class ListAdapter extends SimpleListAdapterBase<T> {

        @NonNull
        @Override
        public SelectableViewHolder<T> onCreateViewHolder(@NonNull ViewGroup parent) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return SelectPureListItemFragmentBase.this.onCreateViewHolder(inflater, parent);
        }
    }

    @NonNull
    protected SelectableViewHolder<T> onCreateViewHolder(@NonNull LayoutInflater inflater, ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.item_image_simple, parent, false));
    }

    private class ViewHolder extends SelectableViewHolder<T> {
        ItemImageSimpleBinding binding;

        public ViewHolder(View itemView) {
            super(itemView, selector, SelectPureListItemFragmentBase.this);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void bindItem() {
            binding.name.setText(item.getName());
            binding.image.setImageDrawable(item.getDrawable(getContext()));
        }
    }
}
