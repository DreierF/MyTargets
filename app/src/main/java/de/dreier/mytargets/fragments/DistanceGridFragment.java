/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.android.recyclerviewchoicemode.SelectableViewHolder;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.shared.models.Distance;
import de.dreier.mytargets.views.CardItemDecorator;

public class DistanceGridFragment extends NowListFragment<Distance> {

    public static final String CUR_DISTANCE = "distance";
    private static final String DISTANCE_UNIT = "distance_unit";
    private long distance;
    private String unit;

    public static DistanceGridFragment newInstance(long distance, String unit) {
        DistanceGridFragment fragment = new DistanceGridFragment();

        Bundle args = new Bundle();
        args.putLong(CUR_DISTANCE, distance);
        args.putString(DISTANCE_UNIT, unit);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    protected void init(Bundle intent, Bundle savedInstanceState) {
        mEditable = false;
        Bundle bundle = getArguments();
        distance = bundle.getLong(CUR_DISTANCE);
        unit = bundle.getString(DISTANCE_UNIT);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mRecyclerView.addItemDecoration(new CardItemDecorator(getActivity(), 3));
    }

    @Override
    public void onResume() {
        super.onResume();
        setList(db.getDistances(distance, unit), new DistanceAdapter());
    }

    @Override
    protected void updateFabButton(List list) {
    }

    @Override
    protected void onEdit(Distance item) {

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

    public class ViewHolder extends SelectableViewHolder<Distance> {
        private final TextView mName;

        public ViewHolder(View itemView) {
            super(itemView, mMultiSelector, DistanceGridFragment.this);
            mName = (TextView) itemView.findViewById(android.R.id.text1);
        }

        @Override
        public void bindCursor() {
            mName.setText(mItem.toString(getActivity()));
        }
    }

}
