/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.android.recyclerviewchoicemode.SelectableViewHolder;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Distance;
import de.dreier.mytargets.utils.TextInputDialog;
import de.dreier.mytargets.views.CardItemDecorator;

public class DistanceFragment extends NowListFragment<Distance>
        implements TextInputDialog.OnClickListener, View.OnClickListener {

    public static final String CUR_DISTANCE = "distance";
    private long distance;

    @Override
    protected void init(Bundle intent, Bundle savedInstanceState) {
        mEditable = false;
        Bundle bundle = getArguments();
        distance = bundle.getLong(CUR_DISTANCE);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mRecyclerView.addItemDecoration(new CardItemDecorator(getActivity(), 3));
    }

    @Override
    public void onResume() {
        super.onResume();
        setList(db.getDistances(distance), new DistanceAdapter());
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

    @Override
    public void onCancelClickListener() {

    }

    @Override
    public void onOkClickListener(String input) {
        try {
            int distanceVal = Integer.parseInt(input.replaceAll("[^0-9]", ""));
            String unit;
            if (input.endsWith(Dimension.METER)) {
                unit = Dimension.METER;
            } else {
                unit = Dimension.YARDS;
            }
            distance = new Distance(distanceVal, unit).getId();
        } catch (NumberFormatException e) {
            // leave distance as it is
        }
        listener.onItemSelected(distance, Distance.class);
    }

    @Override
    public void onClick(View v) {
        new TextInputDialog.Builder(getActivity())
                .setTitle(R.string.distance)
                .setInputType(InputType.TYPE_CLASS_NUMBER)
                .setSpinnerItems(new String[]{Dimension.METER, Dimension.YARDS})
                .setOnClickListener(this)
                .show();
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
            super(itemView, mMultiSelector, DistanceFragment.this);
            mName = (TextView) itemView.findViewById(android.R.id.text1);
        }

        @Override
        public void bindCursor() {
            mName.setText(mItem.toString(getActivity()));
        }
    }

}
