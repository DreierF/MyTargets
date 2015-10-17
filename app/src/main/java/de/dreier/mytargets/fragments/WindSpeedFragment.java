/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.models.WindSpeed;
import de.dreier.mytargets.utils.SelectableViewHolder;

public class WindSpeedFragment extends SelectItemFragment<WindSpeed> {

    @Override
    public void onResume() {
        super.onResume();
        setList(WindSpeed.getList(getActivity()), new WindSpeedAdapter());
    }

    @Override
    public void onLongClick(SelectableViewHolder holder) {
        onClick(holder, (WindSpeed) holder.getItem());
    }

    private class WindSpeedAdapter extends NowListAdapter<WindSpeed> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_text, parent, false);
            return new ViewHolder(itemView);
        }
    }

    private class ViewHolder extends SelectableViewHolder<WindSpeed> {
        private final TextView mName;

        public ViewHolder(View itemView) {
            super(itemView, mSelector, WindSpeedFragment.this);
            mName = (TextView) itemView.findViewById(R.id.name);
        }

        @Override
        public void bindCursor() {
            mName.setText(mItem.name);
        }
    }
}
