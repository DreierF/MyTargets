/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.android.recyclerviewchoicemode.CardViewHolder;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.EditBowActivity;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.models.WindSpeed;

public class WindSpeedFragment extends NowListFragment<WindSpeed> {

    @Override
    protected void init(Bundle intent, Bundle savedInstanceState) {
        mEditable = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        setList(WindSpeed.getList(getActivity()), new WindSpeedAdapter());
    }

    @Override
    public void onLongClick(CardViewHolder holder) {
        onClick(holder, (WindSpeed) holder.getItem());
    }

    @Override
    protected void onEdit(WindSpeed item) {
        Intent i = new Intent(getActivity(), EditBowActivity.class);
        i.putExtra(EditBowActivity.BOW_ID, item.getId());
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    protected class WindSpeedAdapter extends NowListAdapter<WindSpeed> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.text_card, parent, false);
            return new ViewHolder(itemView);
        }
    }

    public class ViewHolder extends CardViewHolder<WindSpeed> {
        private final TextView mName;

        public ViewHolder(View itemView) {
            super(itemView, mMultiSelector, WindSpeedFragment.this);
            mName = (TextView) itemView.findViewById(R.id.name);
        }

        @Override
        public void bindCursor() {
            mName.setText(mItem.name);
        }
    }
}
