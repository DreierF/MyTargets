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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.SimpleFragmentActivityBase.EditArrowActivity;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.databinding.FragmentListBinding;
import de.dreier.mytargets.databinding.ItemImageDetailsBinding;
import de.dreier.mytargets.shared.models.db.Arrow;
import de.dreier.mytargets.utils.DividerItemDecoration;
import de.dreier.mytargets.utils.SelectableViewHolder;

import static de.dreier.mytargets.fragments.EditArrowFragment.ARROW_ID;
import static de.dreier.mytargets.utils.ActivityUtils.startActivityAnimated;

public class ArrowFragment extends EditableFragment<Arrow> {

    protected FragmentListBinding binding;

    public ArrowFragment() {
        itemTypeSelRes = R.plurals.arrow_selected;
        itemTypeDelRes = R.plurals.arrow_deleted;
        newStringRes = R.string.new_arrow;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.fab.setOnClickListener(view1 -> startActivityAnimated(getActivity(), EditArrowActivity.class));
    }

    @Override
    @CallSuper
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.addItemDecoration(
                new DividerItemDecoration(getContext(), R.drawable.inset_divider));
        mAdapter = new ArrowAdapter(getContext());
        binding.recyclerView.setAdapter(mAdapter);
        return binding.getRoot();
    }

    @NonNull
    @Override
    protected LoaderUICallback onLoad() {
        List<Arrow> arrows = Arrow.getAll();
        return () -> mAdapter.setList(arrows);
    }

    @Override
    protected void onEdit(Arrow item) {
        startActivityAnimated(getActivity(),
                EditArrowActivity.class, ARROW_ID, item.getId());
    }

    @Override
    protected void onItemSelected(Arrow item) {
        startActivityAnimated(getActivity(), EditArrowActivity.class, ARROW_ID, item.getId());
    }

    private class ArrowAdapter extends NowListAdapter<Arrow> {
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
            super(itemView, mSelector, ArrowFragment.this);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void bindCursor() {
            binding.name.setText(mItem.name);
            binding.image.setImageDrawable(mItem.getDrawable());
        }
    }
}

