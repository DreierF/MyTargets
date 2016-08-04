/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.SimpleFragmentActivityBase.EditBowActivity;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.databinding.FragmentListBinding;
import de.dreier.mytargets.databinding.ItemImageDetailsBinding;
import de.dreier.mytargets.shared.models.db.Bow;
import de.dreier.mytargets.shared.models.db.SightSetting;
import de.dreier.mytargets.utils.DividerItemDecoration;
import de.dreier.mytargets.utils.FlowDataLoader;
import de.dreier.mytargets.utils.HTMLInfoBuilder;
import de.dreier.mytargets.utils.HtmlUtils;
import de.dreier.mytargets.utils.SelectableViewHolder;

import static de.dreier.mytargets.fragments.EditBowFragment.BOW_ID;
import static de.dreier.mytargets.utils.ActivityUtils.startActivityAnimated;

public class BowFragment extends EditableFragment<Bow> {

    protected FragmentListBinding binding;

    public BowFragment() {
        itemTypeSelRes = R.plurals.bow_selected;
        itemTypeDelRes = R.plurals.bow_deleted;
        newStringRes = R.string.new_bow;
    }

    @Override
    @CallSuper
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.addItemDecoration(
                new DividerItemDecoration(getContext(), R.drawable.inset_divider));
        binding.fab.setOnClickListener(view1 -> startActivityAnimated(getActivity(), EditBowActivity.class));
        mAdapter = new BowAdapter(getContext());
        binding.recyclerView.setAdapter(mAdapter);
        return binding.getRoot();
    }

    @Override
    public Loader<List<Bow>> onCreateLoader(int id, Bundle args) {
        return new FlowDataLoader<>(getContext(), Bow::getAll);
    }

    @Override
    public void onLoadFinished(Loader<List<Bow>> loader, List<Bow> data) {
        mAdapter.setList(data);
    }

    @Override
    protected void onEdit(Bow item) {
        startActivityAnimated(getActivity(), EditBowActivity.class, BOW_ID, item.getId());
    }

    @Override
    protected void onItemSelected(Bow item) {
        startActivityAnimated(getActivity(), EditBowActivity.class, BOW_ID, item.getId());
    }

    private class BowAdapter extends NowListAdapter<Bow> {
        public BowAdapter(Context context) {
            super(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_image_details, parent, false);
            return new ViewHolder(itemView);
        }
    }

    class ViewHolder extends SelectableViewHolder<Bow> {

        ItemImageDetailsBinding binding;

        public ViewHolder(View itemView) {
            super(itemView, mSelector, BowFragment.this);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void bindCursor() {
            binding.name.setText(mItem.name);
            binding.image.setImageDrawable(mItem.getDrawable());

            HTMLInfoBuilder info = new HTMLInfoBuilder();
            info.addLine(R.string.bow_type, mItem.type);
            if (!mItem.brand.trim().isEmpty()) {
                info.addLine(R.string.brand, mItem.brand);
            }
            if (!mItem.size.trim().isEmpty()) {
                info.addLine(R.string.size, mItem.size);
            }
            for (SightSetting s : mItem.sightSettings) {
                info.addLine(s.distance.toString(), s.value);
            }
            binding.details.setText(HtmlUtils.fromHtml(info.toString()));
        }
    }
}
