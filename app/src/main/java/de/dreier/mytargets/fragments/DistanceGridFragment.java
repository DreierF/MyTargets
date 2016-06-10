/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.parceler.Parcels;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.managers.dao.DistanceDataSource;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Dimension.Unit;
import de.dreier.mytargets.utils.SelectableViewHolder;
import de.dreier.mytargets.views.CardItemDecorator;

import static de.dreier.mytargets.activities.ItemSelectActivity.ITEM;

public class DistanceGridFragment extends SelectItemFragment<Dimension> {

    private static final String DISTANCE_UNIT = "distance_unit";
    private Dimension distance;
    private Unit unit;

    public static DistanceGridFragment newInstance(Dimension distance, Unit unit) {
        DistanceGridFragment fragment = new DistanceGridFragment();
        Bundle args = new Bundle();
        args.putParcelable(ITEM, Parcels.wrap(distance));
        args.putString(DISTANCE_UNIT, unit.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        distance = Parcels.unwrap(bundle.getParcelable(ITEM));
        unit = Unit.from(bundle.getString(DISTANCE_UNIT));
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.addItemDecoration(new CardItemDecorator(getActivity(), 3));
    }

    @Override
    public void onResume() {
        super.onResume();
        DistanceDataSource dataSource = new DistanceDataSource();
        setList(dataSource.getAll(distance, unit), new DistanceAdapter());
    }

    @Override
    protected void updateFabButton(List list) {
    }

    @Override
    public void onLongClick(SelectableViewHolder holder) {
        onClick(holder, (Dimension) holder.getItem());
    }

    protected class DistanceAdapter extends NowListAdapter<Dimension> {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_distance, parent, false);
            return new ViewHolder(itemView);
        }
    }

    class ViewHolder extends SelectableViewHolder<Dimension> {
        @Bind(android.R.id.text1)
        TextView mName;

        public ViewHolder(View itemView) {
            super(itemView, mSelector, DistanceGridFragment.this);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bindCursor() {
            mName.setText(mItem.toString());
        }
    }

}
