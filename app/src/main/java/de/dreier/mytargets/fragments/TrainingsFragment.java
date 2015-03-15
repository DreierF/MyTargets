package de.dreier.mytargets.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.android.recyclerviewchoicemode.CardViewHolder;

import java.text.DateFormat;
import java.util.ArrayList;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.EditRoundActivity;
import de.dreier.mytargets.activities.RoundActivity;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.models.Training;

/**
 * Shows an overview over all trying days
 */
public class TrainingsFragment extends NowListFragment<Training> {

    @Override
    protected void init(Bundle intent, Bundle savedInstanceState) {
        itemTypeRes = R.plurals.training;
        newStringRes = R.string.new_training;
    }

    @Override
    public void onResume() {
        super.onResume();
        ArrayList<Training> list = db.getTrainings();
        setList(list, new TrainingAdapter());
    }

    @Override
    protected void onNew(Intent i) {
        i.setClass(getActivity(), EditRoundActivity.class);
    }

    @Override
    public void onSelected(Training item) {
        Intent i = new Intent(getActivity(), RoundActivity.class);
        i.putExtra(TRAINING_ID, item.getId());
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    @Override
    protected void onEdit(Training item) {
        //TODO
    }

    public class TrainingAdapter extends NowListAdapter<Training> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.training_card, parent, false);
            return new ViewHolder(itemView);
        }
    }

    public class ViewHolder extends CardViewHolder<Training> {
        public TextView mTitle;
        public TextView mSubtitle;
        public TextView mGes;

        public ViewHolder(View itemView) {
            super(itemView, mMultiSelector, TrainingsFragment.this);
            mTitle = (TextView) itemView.findViewById(R.id.training);
            mSubtitle = (TextView) itemView.findViewById(R.id.training_date);
            mGes = (TextView) itemView.findViewById(R.id.gesTraining);
        }

        @Override
        public void bindCursor() {
            mTitle.setText(mItem.title);
            mSubtitle.setText(DateFormat.getDateInstance().format(mItem.date));
            mGes.setText(mItem.reachedPoints + "/" + mItem.maxPoints);
        }
    }
}