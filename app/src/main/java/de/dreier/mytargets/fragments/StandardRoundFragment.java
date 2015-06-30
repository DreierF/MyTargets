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

import com.bignerdranch.android.recyclerviewchoicemode.SelectableViewHolder;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.SimpleFragmentActivity;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.shared.models.StandardRound;

public class StandardRoundFragment extends NowListFragment<StandardRound> {

    @Override
    protected void init(Bundle intent, Bundle savedInstanceState) {
        mEditable = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        List<StandardRound> list = DatabaseManager.getInstance(getActivity()).getStandardRounds();
        setList(list, new StandardRoundAdapter());
    }

    @Override
    public void onLongClick(SelectableViewHolder holder) {
        onClick(holder, (StandardRound) holder.getItem());
    }

    @Override
    protected void onEdit(StandardRound item) {
        Intent i = new Intent(getActivity(), SimpleFragmentActivity.EditRoundActivity.class);
        i.putExtra(EditStandardRoundFragment.STANDARD_ROUND_ID, item.getId());
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void onClick(View v) {
        startActivity(new Intent(getActivity(), SimpleFragmentActivity.EditRoundActivity.class));
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    protected class StandardRoundAdapter extends NowListAdapter<StandardRound> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.text_card, parent, false);
            return new ViewHolder(itemView);
        }
    }

    public class ViewHolder extends SelectableViewHolder<StandardRound> {
        private final TextView mName;

        public ViewHolder(View itemView) {
            super(itemView, mMultiSelector, StandardRoundFragment.this);
            mName = (TextView) itemView.findViewById(R.id.name);
        }

        @Override
        public void bindCursor() {
            mName.setText(mItem.name);
        }
    }
}
