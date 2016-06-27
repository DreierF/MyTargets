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
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.SimpleFragmentActivityBase;
import de.dreier.mytargets.adapters.NowListAdapter;
import de.dreier.mytargets.databinding.ItemImageDetailsBinding;
import de.dreier.mytargets.managers.dao.ArrowDataSource;
import de.dreier.mytargets.shared.models.Arrow;
import de.dreier.mytargets.utils.ActivityUtils;
import de.dreier.mytargets.utils.DataLoader;
import de.dreier.mytargets.utils.SelectableViewHolder;

import static de.dreier.mytargets.fragments.EditArrowFragment.ARROW_ID;

public class ArrowFragment extends EditableFragment<Arrow> implements View.OnClickListener {

    private ArrowDataSource arrowDataSource;

    public ArrowFragment() {
        itemTypeSelRes = R.plurals.arrow_selected;
        itemTypeDelRes = R.plurals.arrow_deleted;
        newStringRes = R.string.new_arrow;
    }

    @Override
    public Loader<List<Arrow>> onCreateLoader(int id, Bundle args) {
        arrowDataSource = new ArrowDataSource();
        return new DataLoader<>(getContext(), arrowDataSource, arrowDataSource::getAll);
    }

    @Override
    public void onLoadFinished(Loader<List<Arrow>> loader, List<Arrow> data) {
        setList(arrowDataSource, data, new ArrowAdapter(getContext()));
    }

    @Override
    protected void onEdit(Arrow item) {
        ActivityUtils.startActivityAnimated(getActivity(),
                SimpleFragmentActivityBase.EditArrowActivity.class, ARROW_ID, item.getId());
    }

    @Override
    public void onClick(View v) {
        ActivityUtils.startActivityAnimated(getActivity(),
                SimpleFragmentActivityBase.EditArrowActivity.class);
    }

    private class ArrowAdapter extends NowListAdapter<Arrow> {
        ArrowAdapter(Context context) {
            super(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_image_details, parent, false);
            return new ViewHolder(itemView);
        }
    }

    class ViewHolder extends SelectableViewHolder<Arrow> {
        private final ItemImageDetailsBinding binding;

        public ViewHolder(View itemView) {
            super(itemView, mSelector, ArrowFragment.this);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void bindCursor() {
            binding.name.setText(mItem.name);
            binding.image.setImageDrawable(mItem.getDrawable());
        }
    }
}

