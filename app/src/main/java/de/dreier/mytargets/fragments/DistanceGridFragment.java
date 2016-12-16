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
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import junit.framework.Assert;

import org.parceler.Parcels;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.ListAdapterBase;
import de.dreier.mytargets.databinding.FragmentListBinding;
import de.dreier.mytargets.databinding.ItemDistanceBinding;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Dimension.Unit;
import de.dreier.mytargets.utils.DistanceInputDialog;
import de.dreier.mytargets.utils.SlideInItemAnimator;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;
import de.dreier.mytargets.views.CardItemDecorator;

import static de.dreier.mytargets.activities.ItemSelectActivity.ITEM;

public class DistanceGridFragment extends SelectItemFragmentBase<Dimension> implements DistanceInputDialog.OnClickListener {

    private static final String DISTANCE_UNIT = "distance_unit";
    protected FragmentListBinding binding;
    private Dimension distance;
    private Unit unit;

    public static DistanceGridFragment newInstance(Dimension distance, Unit unit) {
        DistanceGridFragment fragment = new DistanceGridFragment();
        Bundle args = new Bundle();
        args.putParcelable(ITEM, Parcels.wrap(distance));
        args.putString(DISTANCE_UNIT, unit.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false);
        binding.recyclerView.setHasFixedSize(true);
        Bundle bundle = getArguments();
        distance = Parcels.unwrap(bundle.getParcelable(ITEM));
        unit = Unit.from(bundle.getString(DISTANCE_UNIT));
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        binding.recyclerView.addItemDecoration(new CardItemDecorator(getActivity(), 3));
        mAdapter = new DistanceAdapter(getContext());
        binding.recyclerView.setItemAnimator(new SlideInItemAnimator());
        binding.recyclerView.setAdapter(mAdapter);

        binding.fab.setOnClickListener(view -> new DistanceInputDialog.Builder(getContext())
                .setUnit(unit.toString())
                .setOnClickListener(DistanceGridFragment.this)
                .show());
        return binding.getRoot();
    }

    @Override
    public void onOkClickListener(String input) {
        Dimension distance = this.distance;
        try {
            int distanceVal = Integer.parseInt(input.replaceAll("[^0-9]", ""));
            distance = new Dimension(distanceVal, unit);
        } catch (NumberFormatException e) {
            // leave distance as it is
        }
        listener.onItemSelected(Parcels.wrap(distance));
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        if (activity instanceof OnItemSelectedListener) {
            this.listener = (OnItemSelectedListener) activity;
        }
        Assert.assertNotNull(listener);
        // TODO check if this works or is necessary
    }

    @Override
    public void onLongClick(SelectableViewHolder<Dimension> holder) {
        onClick(holder, holder.getItem());
    }

    @NonNull
    @Override
    protected LoaderUICallback onLoad(Bundle args) {
        final List<Dimension> distances = Dimension.getAll(distance, unit);
        return new LoaderUICallback() {
            @Override
            public void applyData() {
                mAdapter.setList(distances);
                selectItem(binding.recyclerView, distance);
            }
        };
    }

    private class DistanceAdapter extends ListAdapterBase<Dimension> {
        DistanceAdapter(Context context) {
            super(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_distance, parent, false);
            return new ViewHolder(itemView);
        }
    }

    class ViewHolder extends SelectableViewHolder<Dimension> {

        private final ItemDistanceBinding binding;

        public ViewHolder(View itemView) {
            super(itemView, mSelector, DistanceGridFragment.this);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void bindItem() {
            binding.distance.setText(item.toString());
        }
    }

}
