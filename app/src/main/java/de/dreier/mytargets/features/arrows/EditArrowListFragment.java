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

package de.dreier.mytargets.features.arrows;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.adapters.SimpleListAdapterBase;
import de.dreier.mytargets.base.fragments.EditableListFragment;
import de.dreier.mytargets.base.fragments.ItemActionModeCallback;
import de.dreier.mytargets.databinding.FragmentArrowsBinding;
import de.dreier.mytargets.databinding.ItemImageDetailsBinding;
import de.dreier.mytargets.shared.models.db.Arrow;
import de.dreier.mytargets.utils.DividerItemDecoration;
import de.dreier.mytargets.utils.SlideInItemAnimator;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;

public class EditArrowListFragment extends EditableListFragment<Arrow> {

    protected FragmentArrowsBinding binding;

    public EditArrowListFragment() {
        itemTypeDelRes = R.plurals.arrow_deleted;
        actionModeCallback = new ItemActionModeCallback(this, selector, R.plurals.arrow_selected);
        actionModeCallback.setEditCallback(this::onEdit);
        actionModeCallback.setDeleteCallback(this::onDelete);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.fab.setOnClickListener(
                view1 -> EditArrowFragment.createIntent()
                        .withContext(this)
                        .fromFab(binding.fab)
                        .start());
    }

    @Override
    @CallSuper
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_arrows, container, false);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.addItemDecoration(
                new DividerItemDecoration(getContext(), R.drawable.full_divider));
        adapter = new ArrowAdapter();
        binding.recyclerView.setItemAnimator(new SlideInItemAnimator());
        binding.recyclerView.setAdapter(adapter);
        return binding.getRoot();
    }

    @NonNull
    @Override
    protected LoaderUICallback onLoad(Bundle args) {
        List<Arrow> arrows = Arrow.getAll();
        return () -> {
            adapter.setList(arrows);
            binding.emptyState.getRoot().setVisibility(arrows.isEmpty() ? View.VISIBLE : View.GONE);
        };
    }

    protected void onEdit(long itemId) {
        EditArrowFragment.editIntent(itemId)
                .withContext(this)
                .start();
    }

    @Override
    protected void onItemSelected(@NonNull Arrow item) {
        EditArrowFragment.editIntent(item.getId())
                .withContext(this)
                .start();
    }

    private class ArrowAdapter extends SimpleListAdapterBase<Arrow> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_image_details, parent, false);
            return new ViewHolder(itemView);
        }
    }

    class ViewHolder extends SelectableViewHolder<Arrow> {
        private final ItemImageDetailsBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView, selector, EditArrowListFragment.this, EditArrowListFragment.this);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void bindItem() {
            binding.name.setText(item.name);
            binding.image.setImageDrawable(item.getDrawable());
        }
    }
}

