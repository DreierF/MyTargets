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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.databinding.FragmentListBinding;
import de.dreier.mytargets.databinding.ItemTextBinding;
import de.dreier.mytargets.models.WindSpeed;
import de.dreier.mytargets.utils.SelectableViewHolder;
import de.dreier.mytargets.utils.ToolbarUtils;

public class WindSpeedFragment extends SelectItemFragment<WindSpeed> {

    protected FragmentListBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false);
        binding.recyclerView.setHasFixedSize(true);
        mAdapter = new WindSpeedAdapter(getContext());
        binding.recyclerView.setAdapter(mAdapter);
        usesDoubleClickSelection = false;
        ToolbarUtils.showUpAsX(this);
        return binding.getRoot();
    }

    @Override
    public void onLongClick(SelectableViewHolder holder) {
        onClick(holder, (WindSpeed) holder.getItem());
    }

    private class WindSpeedAdapter extends NowListAdapter<WindSpeed> {
        WindSpeedAdapter(Context context) {
            super(context);
            setList(WindSpeed.getList(getContext()));
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_text, parent, false);
            return new ViewHolder(itemView);
        }
    }

    private class ViewHolder extends SelectableViewHolder<WindSpeed> {

        ItemTextBinding binding;

        public ViewHolder(View itemView) {
            super(itemView, mSelector, WindSpeedFragment.this);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void bindCursor() {
            binding.name.setText(mItem.name);
        }
    }
}
