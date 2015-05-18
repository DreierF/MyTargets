/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.android.recyclerviewchoicemode.CardViewHolder;

import java.util.ArrayList;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.EditBowActivity;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.shared.models.Bow;

public class BowFragment extends NowListFragment<Bow> {

    @Override
    protected void init(Bundle intent, Bundle savedInstanceState) {
        itemTypeRes = R.plurals.bow_selected;
        itemTypeDelRes = R.plurals.bow_deleted;
        newStringRes = R.string.new_bow;
        mEditable = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        setList(db.getBows(), new BowAdapter());
    }

    @Override
    protected void onNew(Intent i) {
        i.setClass(getActivity(), EditBowActivity.class);
    }

    @Override
    protected void onEdit(Bow item) {
        Intent i = new Intent(getActivity(), EditBowActivity.class);
        i.putExtra(EditBowActivity.BOW_ID, item.getId());
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    protected class BowAdapter extends NowListAdapter<Bow> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.image_card, parent, false);
            return new ViewHolder(itemView);
        }
    }

    public class ViewHolder extends CardViewHolder<Bow> {
        private final TextView mName;
        private final TextView mDetails;
        private final ImageView mImg;

        public ViewHolder(View itemView) {
            super(itemView, mMultiSelector, BowFragment.this);
            mName = (TextView) itemView.findViewById(R.id.name);
            mDetails = (TextView) itemView.findViewById(R.id.details);
            mImg = (ImageView) itemView.findViewById(R.id.image);
        }

        @Override
        public void bindCursor() {
            mName.setText(mItem.name);
            mImg.setImageBitmap(mItem.image);
            String html = getString(R.string.bow_type) + ": <b>" +
                    getResources().getStringArray(R.array.bow_types)[mItem.type] + "</b>";
            if (!mItem.brand.trim().isEmpty()) {
                html += "<br>" + getString(R.string.brand) + ": <b>" + mItem.brand + "</b>";
            }
            if (!mItem.size.trim().isEmpty()) {
                html += "<br>" + getString(R.string.size) + ": <b>" + mItem.size + "</b>";
            }
            ArrayList<EditBowActivity.SightSetting> sight = db.getSettings(mItem.getId());
            for (EditBowActivity.SightSetting s : sight) {
                html += "<br>" + s.distanceVal + "m: <b>" + s.value + "</b>";
            }
            mDetails.setText(Html.fromHtml(html));
        }
    }
}
