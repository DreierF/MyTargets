/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.ArrowRankingDetailsActivity;
import de.dreier.mytargets.managers.dao.ArrowStatisticDataSource;
import de.dreier.mytargets.managers.dao.DataSourceBase;
import de.dreier.mytargets.models.ArrowStatistic;
import de.dreier.mytargets.utils.DataLoaderBase;
import de.dreier.mytargets.utils.RoundedTextDrawable;

public class ArrowRankingFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<ArrowStatistic>> {

    private ArrowStatisticDataSource arrowStatisticDataSource;
    private RecyclerView recyclerView;
    private List<ArrowStatistic> data;
    private ArrowStatisticAdapter adapter;

    public ArrowRankingFragment() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(android.R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        arrowStatisticDataSource = new ArrowStatisticDataSource(getContext());
        return rootView;
    }

    @Override
    public Loader<List<ArrowStatistic>> onCreateLoader(int id, Bundle args) {
        arrowStatisticDataSource = new ArrowStatisticDataSource(getContext());
        return new DataLoaderBase<ArrowStatistic, DataSourceBase>(getContext(), arrowStatisticDataSource, arrowStatisticDataSource::getAll);
    }

    @Override
    public void onLoadFinished(Loader<List<ArrowStatistic>> loader, List<ArrowStatistic> data) {
        this.data = data;
        Collections.sort(data);
        if (recyclerView.getAdapter() == null) {
            adapter = new ArrowStatisticAdapter();
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<ArrowStatistic>> loader) {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(0, null, this);
    }

    private class ArrowStatisticAdapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_image_simple, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindItem(data.get(position));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mName;
        private final ImageView mImg;
        private ArrowStatistic mItem;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            itemView.setOnClickListener(v -> {
                Intent i = new Intent(getContext(), ArrowRankingDetailsActivity.class);
                i.putExtra(ArrowRankingDetailsActivity.ITEM, mItem);
                startActivity(i);
            });
            mName = (TextView) itemView.findViewById(R.id.name);
            mImg = (ImageView) itemView.findViewById(R.id.image);
        }

        public void bindItem(ArrowStatistic item) {
            mItem = item;
            mName.setText(getString(R.string.arrow_x_of_set_of_arrows, item.arrowNumber, item.arrowName));
            mImg.setImageDrawable(new RoundedTextDrawable(item));
        }
    }
}
