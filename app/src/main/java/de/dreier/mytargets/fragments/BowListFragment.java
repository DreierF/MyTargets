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
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.adapters.ListAdapterBase;
import de.dreier.mytargets.databinding.FragmentListBinding;
import de.dreier.mytargets.databinding.ItemImageDetailsBinding;
import de.dreier.mytargets.shared.models.db.Bow;
import de.dreier.mytargets.shared.models.db.SightSetting;
import de.dreier.mytargets.utils.DividerItemDecoration;
import de.dreier.mytargets.utils.HtmlInfoBuilder;
import de.dreier.mytargets.utils.HtmlUtils;
import de.dreier.mytargets.utils.SlideInItemAnimator;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;

public class BowListFragment extends EditableListFragment<Bow> {

    protected FragmentListBinding binding;

    public BowListFragment() {
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
        binding.fab.setOnClickListener(
                view1 -> EditBowFragment.createIntent(this)
                        .fromFab(binding.fab)
                        .start());
        mAdapter = new BowAdapter(getContext());
        binding.recyclerView.setItemAnimator(new SlideInItemAnimator());
        binding.recyclerView.setAdapter(mAdapter);
        return binding.getRoot();
    }

    @NonNull
    @Override
    protected LoaderUICallback onLoad(Bundle args) {
        List<Bow> bows = Bow.getAll();
        return () -> mAdapter.setList(bows);
    }

    @Override
    protected void onEdit(Bow item) {
        EditBowFragment.editIntent(this, item).start();
    }

    @Override
    protected void onItemSelected(Bow item) {
        EditBowFragment.editIntent(this, item).start();
    }

    private class BowAdapter extends ListAdapterBase<Bow> {
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

        final ItemImageDetailsBinding binding;

        public ViewHolder(View itemView) {
            super(itemView, mSelector, BowListFragment.this);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void bindItem() {
            binding.name.setText(mItem.name);
            binding.image.setImageDrawable(mItem.getDrawable());
            binding.details.setVisibility(View.VISIBLE);

            HtmlInfoBuilder info = new HtmlInfoBuilder();
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
