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
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.android.recyclerviewchoicemode.CardViewHolder;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.EditBowActivity;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.shared.models.Target;

public class TargetFragment extends NowListFragment<Target> {

    @Override
    protected void init(Bundle intent, Bundle savedInstanceState) {
        mEditable = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        setList(Target.list, new TargetAdapter());
    }

    @Override
    public void onLongClick(CardViewHolder holder) {
        onClick(holder, (Target) holder.getItem());
    }

    @Override
    protected void onEdit(Target item) {
        Intent i = new Intent(getActivity(), EditBowActivity.class);
        i.putExtra(EditBowActivity.BOW_ID, item.getId());
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    protected class TargetAdapter extends NowListAdapter<Target> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.image_card, parent, false);
            return new ViewHolder(itemView);
        }
    }

    public class ViewHolder extends CardViewHolder<Target> {
        private final TextView mName;
        private final ImageView mImg;

        public ViewHolder(View itemView) {
            super(itemView, mMultiSelector, TargetFragment.this);
            mName = (TextView) itemView.findViewById(R.id.name);
            mImg = (ImageView) itemView.findViewById(R.id.image);
        }

        @Override
        public void bindCursor() {
            mName.setText(mItem.name);
            mImg.setImageResource(mItem.drawableRes);
        }
    }
}
