/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.android.recyclerviewchoicemode.CardViewHolder;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.EditBowActivity;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.models.Distance;
import de.dreier.mytargets.utils.TextInputDialog;

public class DistanceFragment extends NowListFragment<Distance>
        implements TextInputDialog.OnClickListener {

    public static final String CUR_DISTANCE = "distance";
    private long distance;

    @Override
    protected void init(Bundle intent, Bundle savedInstanceState) {
        mEditable = false;
        Bundle bundle = getArguments();
        distance = bundle.getLong(CUR_DISTANCE);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            new TextInputDialog.Builder(getActivity())
                    .setTitle(R.string.distance)
                    .setInputType(InputType.TYPE_CLASS_NUMBER)
                    .setSpinnerItems(new String[]{"m", "yd"})
                    .setOnClickListener(this)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        setList(db.getDistances(distance), new DistanceAdapter());
    }

    @Override
    protected void onNew(Intent i) {
        i.setClass(getActivity(), EditBowActivity.class);
    }

    @Override
    protected void onEdit(Distance item) {

    }

    @Override
    public void onLongClick(CardViewHolder holder) {
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
            if (input.endsWith("m")) {
                unit = "m";
            } else {
                unit = "yd";
            }
            distance = new Distance(distanceVal, unit).getId();
        } catch (NumberFormatException e) {
        }
        listener.onItemSelected(distance, Distance.class);
    }

    protected class DistanceAdapter extends NowListAdapter<Distance> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.text_card, parent, false);
            return new ViewHolder(itemView);
        }
    }

    public class ViewHolder extends CardViewHolder<Distance> {
        private final TextView mName;

        public ViewHolder(View itemView) {
            super(itemView, mMultiSelector, DistanceFragment.this);
            mName = (TextView) itemView.findViewById(R.id.name);
        }

        @Override
        public void bindCursor() {
            mName.setText(mItem.toString());
        }
    }

}
