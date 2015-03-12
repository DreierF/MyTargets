package de.dreier.mytargets.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.android.recyclerviewchoicemode.SwappingHolder;

import java.text.DateFormat;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.EditRoundActivity;
import de.dreier.mytargets.activities.TrainingActivity;
import de.dreier.mytargets.models.Training;

/**
 * Shows an overview over all trying days
 */
public class TrainingsFragment extends NowListFragment<Training> {

    private TrainingAdapter mAdapter;

    @Override
    protected void init(Bundle intent, Bundle savedInstanceState) {
        itemTypeRes = R.plurals.training;
    }

    @Override
    protected void onEdit(int pos) {
        //TODO
    }

    @Override
    public void onResume() {
        super.onResume();
        mList = db.getTrainings();
        if (mRecyclerView.getAdapter() == null) {
            mAdapter = new TrainingAdapter();
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onDelete(List<Integer> positions) {
        for (int pos : positions) {
            db.deleteTraining(mAdapter.getItemId(pos));
            mAdapter.notifyItemRemoved(pos);
        }
    }

    public void selectTraining(Training mItem) {
        Intent i = new Intent(getActivity(), TrainingActivity.class);
        i.putExtra(TrainingActivity.TRAINING_ID, mItem.id);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    @Override
    protected void onNewClick(Intent i) {
        i.setClass(getActivity(), EditRoundActivity.class);
    }

    public class TrainingAdapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.training_card, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int pos) {
            viewHolder.bindCursor(mList.get(pos));
        }

        @Override
        public long getItemId(int position) {
            return mList.get(position).id;
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    public class ViewHolder extends SwappingHolder implements View.OnClickListener, View.OnLongClickListener {
        private final CardView mCard;
        public TextView mTitle;
        public TextView mSubtitle;
        public TextView mGes;
        private Training mItem;

        public ViewHolder(View itemView) {
            super(itemView, mMultiSelector);
            mCard = (CardView) itemView.findViewById(R.id.card);
            mTitle = (TextView) itemView.findViewById(R.id.training);
            mSubtitle = (TextView) itemView.findViewById(R.id.training_date);
            mGes = (TextView) itemView.findViewById(R.id.gesTraining);
            itemView.setLongClickable(true);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bindCursor(Training t) {
            mItem = t;
            mTitle.setText(t.title);
            mSubtitle.setText(DateFormat.getDateInstance().format(t.date));
            mGes.setText(t.reachedPoints + "/" + t.maxPoints);
        }

        @Override
        public void setActivated(boolean isActivated) {
            mCard.setCardBackgroundColor(isActivated ? 0xFFDDDDDD : 0xffffffff);
        }

        @Override
        public void onClick(View v) {
            if (mItem == null) {
                return;
            }
            Log.d("fragment", "onClick id=" + this.getItemId() + " pos=" + getPosition());
            if (!mMultiSelector.tapSelection(this)) {
                Log.d("fragment", "-> select id=" + this.getItemId() + " pos=" + getPosition());
                selectTraining(mItem);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public boolean onLongClick(View v) {
            Log.d("fragment", "onLongClick id=" + this.getItemId() + " pos=" + getPosition());
            if (actionMode == null) {
                ActionBarActivity activity = (ActionBarActivity) getActivity();
                activity.startSupportActionMode(mDeleteMode);
                mMultiSelector.setSelectable(true);
            }
            mMultiSelector.setSelected(this, true);
            return true;
        }
    }
}