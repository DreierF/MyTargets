/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.ListAdapterBase;
import de.dreier.mytargets.databinding.FragmentListBinding;
import de.dreier.mytargets.databinding.ItemImageDetailsBinding;
import de.dreier.mytargets.managers.dao.ArrowDataSource;
import de.dreier.mytargets.shared.models.Arrow;
import de.dreier.mytargets.utils.DataLoader;
import de.dreier.mytargets.utils.DividerItemDecoration;
import de.dreier.mytargets.utils.SlideInItemAnimator;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;

public class ArrowListFragment extends EditableListFragment<Arrow> {

    protected FragmentListBinding binding;
    private ArrowDataSource arrowDataSource;

    public ArrowListFragment() {
        itemTypeSelRes = R.plurals.arrow_selected;
        itemTypeDelRes = R.plurals.arrow_deleted;
        newStringRes = R.string.new_arrow;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.fab.setOnClickListener(
                view1 -> EditArrowFragment.createIntent(this)
                        .fromFab(binding.fab)
                        .start());
    }

    @Override
    @CallSuper
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.addItemDecoration(
                new DividerItemDecoration(getContext(), R.drawable.inset_divider));
        mAdapter = new ArrowAdapter(getContext());
        binding.recyclerView.setItemAnimator(new SlideInItemAnimator());
        binding.recyclerView.setAdapter(mAdapter);
        return binding.getRoot();
    }

    @Override
    public Loader<List<Arrow>> onCreateLoader(int id, Bundle args) {
        arrowDataSource = new ArrowDataSource();
        return new DataLoader<>(getContext(), arrowDataSource, arrowDataSource::getAll);
    }

    @Override
    public void onLoadFinished(Loader<List<Arrow>> loader, List<Arrow> data) {
        this.dataSource = arrowDataSource;
        mAdapter.setList(data);
    }

    @Override
    protected void onEdit(Arrow item) {
        EditArrowFragment.editIntent(this, item)
                .start();
    }

    @Override
    protected void onItemSelected(Arrow item) {
        EditArrowFragment.editIntent(this, item)
                .start();
    }

    private class ArrowAdapter extends ListAdapterBase<Arrow> {
        ArrowAdapter(Context context) {
            super(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_image_details, parent, false);
            return new ViewHolder(itemView);
        }
    }

    class ViewHolder extends SelectableViewHolder<Arrow> {
        private final ItemImageDetailsBinding binding;

        public ViewHolder(View itemView) {
            super(itemView, mSelector, ArrowListFragment.this);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void bindCursor() {
            binding.name.setText(mItem.name);
            binding.image.setImageDrawable(mItem.getDrawable());
        }
    }
}

