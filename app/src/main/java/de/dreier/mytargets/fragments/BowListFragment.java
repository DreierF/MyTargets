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

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.ListAdapterBase;
import de.dreier.mytargets.databinding.FragmentListBinding;
import de.dreier.mytargets.databinding.ItemImageDetailsBinding;
import de.dreier.mytargets.managers.dao.BowDataSource;
import de.dreier.mytargets.shared.models.Bow;
import de.dreier.mytargets.shared.models.SightSetting;
import de.dreier.mytargets.utils.DataLoader;
import de.dreier.mytargets.utils.DividerItemDecoration;
import de.dreier.mytargets.utils.HTMLInfoBuilder;
import de.dreier.mytargets.utils.HtmlUtils;
import de.dreier.mytargets.utils.SlideInItemAnimator;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;

public class BowListFragment extends EditableListFragment<Bow> {

    protected FragmentListBinding binding;
    private BowDataSource bowDataSource;

    public BowListFragment() {
        itemTypeSelRes = R.plurals.bow_selected;
        itemTypeDelRes = R.plurals.bow_deleted;
        newStringRes = R.string.new_bow;
    }

    @Override
    @CallSuper
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.addItemDecoration(
                new DividerItemDecoration(getContext(), R.drawable.inset_divider));
        binding.fab.setOnClickListener(
                view1 -> EditBowFragment.createIntent(this)
                        .fromFab(binding.fab)
                        .start());
        mAdapter = new BowAdapter(getContext());
        binding.recyclerView.setItemAnimator(new SlideInItemAnimator());
        binding.recyclerView.setAdapter(mAdapter);
        return binding.getRoot();
    }

    @Override
    public Loader<List<Bow>> onCreateLoader(int id, Bundle args) {
        bowDataSource = new BowDataSource();
        return new DataLoader<>(getContext(), bowDataSource, bowDataSource::getAll);
    }

    @Override
    public void onLoadFinished(Loader<List<Bow>> loader, List<Bow> data) {
        this.dataSource = bowDataSource;
        mAdapter.setList(data);
    }

    @Override
    protected void onEdit(Bow item) {
        EditBowFragment.editIntent(this, item).start();
    }

    @Override
    protected void onItemSelected(Bow item) {
        EditBowFragment.editIntent(this, item).start();
    }

    private class BowAdapter extends ListAdapterBase<Bow> {
        public BowAdapter(Context context) {
            super(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_image_details, parent, false);
            return new ViewHolder(itemView);
        }
    }

    class ViewHolder extends SelectableViewHolder<Bow> {

        final ItemImageDetailsBinding binding;

        public ViewHolder(View itemView) {
            super(itemView, mSelector, BowListFragment.this);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void bindItem() {
            binding.name.setText(mItem.name);
            binding.image.setImageDrawable(mItem.getDrawable());
            binding.details.setVisibility(View.VISIBLE);

            HTMLInfoBuilder info = new HTMLInfoBuilder();
            info.addLine(R.string.bow_type, mItem.type);
            if (!mItem.brand.trim().isEmpty()) {
                info.addLine(R.string.brand, mItem.brand);
            }
            if (!mItem.size.trim().isEmpty()) {
                info.addLine(R.string.size, mItem.size);
            }
            for (SightSetting s : mItem.sightSettings) {
                info.addLine(s.distance.toString(), s.value);
            }
            binding.details.setText(HtmlUtils.fromHtml(info.toString()));
        }
    }
}
