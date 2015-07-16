/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.android.recyclerviewchoicemode.SelectableViewHolder;
import com.bignerdranch.android.recyclerviewchoicemode.SingleSelector;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.SimpleFragmentActivity;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.shared.models.StandardRound;

public class StandardRoundFragment extends NowListFragment<StandardRound> {

    private static final int NEW_STANDARD_ROUND = 1;
    private SingleSelector mSingleSelector = new SingleSelector();

    @Override
    protected void init(Bundle intent, Bundle savedInstanceState) {
        mEditable = false;
        StandardRound sr = (StandardRound) getArguments().getSerializable("item");
        List<StandardRound> list = DatabaseManager.getInstance(getActivity()).getStandardRounds();
        if(!list.contains(sr)) {
            list.add(sr);
        }
        setList(list, new StandardRoundAdapter());
        mSingleSelector.setSelected(list.indexOf(sr), sr.getId(), true);
    }

    @Override
    public void onLongClick(SelectableViewHolder holder) {
        onClick(holder, (StandardRound) holder.getItem());
    }

    @Override
    protected void onEdit(StandardRound item) {
        Intent i = new Intent(getActivity(),
                SimpleFragmentActivity.EditStandardRoundActivity.class);
        i.putExtra(EditStandardRoundFragment.STANDARD_ROUND_ID, item.getId());
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void onClick(View v) {
        startActivityForResult(
                new Intent(getActivity(), SimpleFragmentActivity.EditStandardRoundActivity.class),
                NEW_STANDARD_ROUND);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == NEW_STANDARD_ROUND) {
            getActivity().setResult(resultCode, data);
            getActivity().onBackPressed();
        }
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
            super(itemView, mSingleSelector, StandardRoundFragment.this);
            mName = (TextView) itemView.findViewById(R.id.name);
        }

        @Override
        public void bindCursor() {
            mName.setText(mItem.name);
        }
    }
}
