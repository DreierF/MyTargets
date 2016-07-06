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
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.parceler.Parcels;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.databinding.FragmentListBinding;
import de.dreier.mytargets.databinding.ItemDistanceBinding;
import de.dreier.mytargets.managers.dao.DistanceDataSource;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Dimension.Unit;
import de.dreier.mytargets.utils.SelectableViewHolder;
import de.dreier.mytargets.views.CardItemDecorator;

import static de.dreier.mytargets.activities.ItemSelectActivity.ITEM;

public class DistanceGridFragment extends SelectItemFragment<Dimension> {

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
        binding.recyclerView.setAdapter(mAdapter);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.setList(new DistanceDataSource().getAll(distance, unit));
    }

    @Override
    public void onLongClick(SelectableViewHolder holder) {
        onClick(holder, (Dimension) holder.getItem());
    }

    private class DistanceAdapter extends NowListAdapter<Dimension> {
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
            binding = ItemDistanceBinding.bind(itemView);
        }

        @Override
        public void bindCursor() {
            binding.distance.setText(mItem.toString());
        }
    }

}
