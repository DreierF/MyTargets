/*
 * Copyright (C) 2016 Florian Dreier
 *
 * This file is part of MyTargets.
 *
 * MyTargets is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * MyTargets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package de.dreier.mytargets.fragments;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.ItemSelectActivity;
import de.dreier.mytargets.adapters.ListAdapterBase;
import de.dreier.mytargets.databinding.FragmentTargetSelectBinding;
import de.dreier.mytargets.databinding.ItemImageSimpleBinding;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.targets.TargetFactory;
import de.dreier.mytargets.shared.targets.models.TargetModelBase;
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.SlideInItemAnimator;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;

import static de.dreier.mytargets.activities.ItemSelectActivity.ITEM;

public class TargetListFragment extends SelectItemFragment<Target>
        implements SeekBar.OnSeekBarChangeListener {

    private static final String TYPE_FIXED = "type_fixed";
    private FragmentTargetSelectBinding binding;
    private boolean typeFixed = false;

    @NonNull
    public static IntentWrapper getIntent(Fragment fragment, Target target) {
        Intent i = new Intent(fragment.getContext(), ItemSelectActivity.TargetActivity.class);
        i.putExtra(ITEM, Parcels.wrap(target));
        i.putExtra(TYPE_FIXED, true);
        return new IntentWrapper(fragment, i);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_target_select, container, false);
        binding.recyclerView.setHasFixedSize(true);
        mAdapter = new TargetAdapter(getContext());
        binding.recyclerView.setItemAnimator(new SlideInItemAnimator());
        binding.recyclerView.setAdapter(mAdapter);
        useDoubleClickSelection = true;
        ToolbarUtils.setSupportActionBar(this, binding.toolbar);
        ToolbarUtils.showHomeAsUp(this);
        ToolbarUtils.showUpAsX(this);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Process passed arguments
        Target t = Parcels.unwrap(getArguments().getParcelable(ITEM));
        typeFixed = getArguments().getBoolean(TYPE_FIXED);
        List<TargetModelBase> list;
        if (typeFixed) {
            list = TargetFactory.getList(t);
        } else {
            list = TargetFactory.getList();
        }
        List<Target> targets = Stream.of(list)
                .map(value -> new Target((int) value.getId(), 0))
                .collect(Collectors.toList());
        mAdapter.setList(targets);

        int position = targets.indexOf(t);
        mSelector.setSelected(position, t.getId(), true);
        binding.recyclerView.scrollToPosition(position);
        updateSettings();

        // Set initial target size
        int diameterIndex = -1;
        Dimension[] diameters = t.getModel().getDiameters();
        for (int i = 0; i < diameters.length; i++) {
            if (diameters[i].equals(t.size)) {
                diameterIndex = i;
                break;
            }
        }
        binding.scoringStyle.setSelection(t.scoringStyle);
        binding.targetSize.setSelection(diameterIndex);
    }

    @Override
    public void onClick(SelectableViewHolder holder, Target mItem) {
        super.onClick(holder, mItem);
        if (mItem == null) {
            return;
        }
        updateSettings();
    }

    private void updateSettings() {
        // Init scoring styles
        Target target = mAdapter.getItem(mSelector.getSelectedPosition());
        List<String> styles = target.getModel().getScoringStyles();
        setThemedAdapter(binding.scoringStyle, styles);

        // Init target size spinner
        ArrayList<String> diameters = diameterToList(target.getModel().getDiameters());
        setThemedAdapter(binding.targetSize, diameters);
        if (!typeFixed && diameters.size() > 1) {
            binding.targetSize.setVisibility(View.VISIBLE);
        } else {
            binding.targetSize.setVisibility(View.GONE);
        }
    }

    private void setThemedAdapter(Spinner spinner, List<String> strings) {
        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        Context themedContext = activity.getSupportActionBar().getThemedContext();
        int lastSelection = spinner.getSelectedItemPosition();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(themedContext,
                android.R.layout.simple_spinner_item, strings);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(lastSelection < strings.size() ? lastSelection : strings.size() - 1,
                false);
    }

    private ArrayList<String> diameterToList(Dimension[] diameters) {
        ArrayList<String> list = new ArrayList<>();
        for (Dimension diameter : diameters) {
            list.add(diameter.toString());
        }
        return list;
    }

    @Override
    public void onLongClick(SelectableViewHolder holder) {
        onClick(holder, (Target) holder.getItem());
    }

    @Override
    protected Target onSave() {
        Target target = super.onSave();
        target.scoringStyle = binding.scoringStyle.getSelectedItemPosition();
        Dimension[] diameters = target.getModel().getDiameters();
        target.size = diameters[binding.targetSize.getSelectedItemPosition()];
        return target;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        updateSettings();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private class TargetAdapter extends ListAdapterBase<Target> {
        TargetAdapter(Context context) {
            super(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_image_simple, parent, false);
            return new ViewHolder(itemView);
        }
    }

    private class ViewHolder extends SelectableViewHolder<Target> {
        private ItemImageSimpleBinding binding;

        public ViewHolder(View itemView) {
            super(itemView, mSelector, TargetListFragment.this);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void bindItem() {
            binding.name.setText(mItem.getModel().toString());
            binding.image.setImageDrawable(mItem.getDrawable());
        }
    }
}
