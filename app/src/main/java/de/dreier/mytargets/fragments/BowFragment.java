/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.fragments;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.SimpleFragmentActivityBase;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.databinding.ItemImageDetailsBinding;
import de.dreier.mytargets.managers.dao.BowDataSource;
import de.dreier.mytargets.shared.models.Bow;
import de.dreier.mytargets.shared.models.SightSetting;
import de.dreier.mytargets.utils.ActivityUtils;
import de.dreier.mytargets.utils.DataLoader;
import de.dreier.mytargets.utils.HTMLInfoBuilder;
import de.dreier.mytargets.utils.SelectableViewHolder;

import static de.dreier.mytargets.fragments.EditBowFragment.BOW_ID;

public class BowFragment extends EditableFragment<Bow> implements View.OnClickListener {

    private BowDataSource bowDataSource;

    public BowFragment() {
        itemTypeSelRes = R.plurals.bow_selected;
        itemTypeDelRes = R.plurals.bow_deleted;
        newStringRes = R.string.new_bow;
    }

    @Override
    public Loader<List<Bow>> onCreateLoader(int id, Bundle args) {
        bowDataSource = new BowDataSource();
        return new DataLoader<>(getContext(), bowDataSource, bowDataSource::getAll);
    }

    @Override
    public void onLoadFinished(Loader<List<Bow>> loader, List<Bow> data) {
        setList(bowDataSource, data, new BowAdapter());
    }

    @Override
    protected void onEdit(Bow item) {
        ActivityUtils.startActivityAnimated(getActivity(),
                SimpleFragmentActivityBase.EditBowActivity.class, BOW_ID, item.getId());
    }

    @Override
    public void onClick(View v) {
        ActivityUtils.startActivityAnimated(getActivity(),
                SimpleFragmentActivityBase.EditBowActivity.class);
    }

    private class BowAdapter extends NowListAdapter<Bow> {
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
            binding = ItemImageDetailsBinding.bind(itemView);
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
            binding.details.setText(Html.fromHtml(info.toString()));
        }
    }
}
