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
import de.dreier.mytargets.activities.EditArrowActivity;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.shared.models.Arrow;

public class ArrowFragment extends NowListFragment<Arrow> {

    @Override
    protected void init(Bundle intent, Bundle savedInstanceState) {
        itemTypeRes = R.plurals.arrow_selected;
        newStringRes = R.string.new_arrow;
        mEditable = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        setList(db.getArrows(), new ArrowAdapter());
    }

    @Override
    protected void onNew(Intent i) {
        i.setClass(getActivity(), EditArrowActivity.class);
    }

    @Override
    protected void onEdit(Arrow item) {
        Intent i = new Intent(getActivity(), EditArrowActivity.class);
        i.putExtra(EditArrowActivity.ARROW_ID, item.getId());
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    protected class ArrowAdapter extends NowListAdapter<Arrow> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.image_card, parent, false);
            return new ViewHolder(itemView);
        }
    }

    public class ViewHolder extends CardViewHolder<Arrow> {
        private final TextView mName;
        private final ImageView mImg;

        public ViewHolder(View itemView) {
            super(itemView, mMultiSelector, ArrowFragment.this);
            mName = (TextView) itemView.findViewById(R.id.name);
            mImg = (ImageView) itemView.findViewById(R.id.image);
        }

        @Override
        public void bindCursor() {
            mName.setText(mItem.name);
            mImg.setImageBitmap(mItem.image);
        }
    }
}

