/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.managers.dao.DistanceDataSource;
import de.dreier.mytargets.shared.models.Distance;
import de.dreier.mytargets.utils.SelectableViewHolder;
import de.dreier.mytargets.views.CardItemDecorator;

import static de.dreier.mytargets.activities.ItemSelectActivity.ITEM;

public class DistanceGridFragment extends SelectItemFragment<Distance> {

    private static final String DISTANCE_UNIT = "distance_unit";
    private Distance distance;
    private String unit;

    public static DistanceGridFragment newInstance(Distance distance, String unit) {
        DistanceGridFragment fragment = new DistanceGridFragment();
        Bundle args = new Bundle();
        args.putSerializable(ITEM, distance);
        args.putString(DISTANCE_UNIT, unit);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        distance = (Distance)bundle.getSerializable(ITEM);
        unit = bundle.getString(DISTANCE_UNIT);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mRecyclerView.addItemDecoration(new CardItemDecorator(getActivity(), 3));
    }

    @Override
    public void onResume() {
        super.onResume();
        DistanceDataSource dataSource = new DistanceDataSource(getContext());
        setList(dataSource.getAll(distance, unit), new DistanceAdapter());
    }

    @Override
    protected void updateFabButton(List list) {
    }

    @Override
    public void onLongClick(SelectableViewHolder holder) {
        onClick(holder, (Distance) holder.getItem());
    }

    protected class DistanceAdapter extends NowListAdapter<Distance> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_distance, parent, false);
            return new ViewHolder(itemView);
        }
    }

    private class ViewHolder extends SelectableViewHolder<Distance> {
        private final TextView mName;

        public ViewHolder(View itemView) {
            super(itemView, mSelector, DistanceGridFragment.this);
            mName = (TextView) itemView.findViewById(android.R.id.text1);
        }

        @Override
        public void bindCursor() {
            mName.setText(mItem.toString(getActivity()));
        }
    }

}
