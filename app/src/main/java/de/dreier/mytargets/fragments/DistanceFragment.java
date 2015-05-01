/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.android.recyclerviewchoicemode.CardViewHolder;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.EditBowActivity;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.models.IdProvider;

public class DistanceFragment extends NowListFragment<DistanceFragment.Distance> {

    public static final String CUR_DISTANCE = "distance";
    private int distance;

    @Override
    protected void init(Bundle intent, Bundle savedInstanceState) {
        mEditable = false;
        Bundle bundle = getArguments();
        distance = bundle.getInt(CUR_DISTANCE);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
    }

    @Override
    public void onResume() {
        super.onResume();

        /*input = input.replaceAll("[^0-9]", "");
        int dist = Integer.parseInt(input);
        new TextInputDialog.Builder(getContext())
                .setTitle(R.string.distance)
                .setInputType(InputType.TYPE_CLASS_NUMBER)
                .setOnClickListener(DistanceDialogSpinner.this)
                .show();*/
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

    public static class Distance extends IdProvider implements Comparable<Distance> {

        public Distance(int dist) {
            id = dist;
        }

        @Override
        public int compareTo(@NonNull Distance another) {
            return (int) (id - another.id);
        }

        @Override
        public String toString() {
            return id + "m";
        }
    }
}
