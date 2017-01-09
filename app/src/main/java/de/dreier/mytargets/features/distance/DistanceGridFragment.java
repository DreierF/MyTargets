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

package de.dreier.mytargets.features.distance;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import org.parceler.Parcels;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.adapters.SimpleListAdapterBase;
import de.dreier.mytargets.base.fragments.SelectItemFragmentBase;
import de.dreier.mytargets.databinding.FragmentListBinding;
import de.dreier.mytargets.databinding.ItemDistanceBinding;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Dimension.Unit;
import de.dreier.mytargets.utils.SlideInItemAnimator;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;

import static de.dreier.mytargets.base.activities.ItemSelectActivity.ITEM;

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
        adapter = new DistanceAdapter(getContext());
        binding.recyclerView.setItemAnimator(new SlideInItemAnimator());
        binding.recyclerView.setAdapter(adapter);

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
        finish();
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
                adapter.setList(distances);
                selectItem(binding.recyclerView, distance);
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.save, menu);
    }

    private class DistanceAdapter extends SimpleListAdapterBase<Dimension> {
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
