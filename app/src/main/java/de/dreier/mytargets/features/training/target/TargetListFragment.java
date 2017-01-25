/*
 * Copyright (C) 2017 Florian Dreier
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

package de.dreier.mytargets.features.training.target;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import junit.framework.Assert;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.adapters.header.ExpandableListAdapter;
import de.dreier.mytargets.base.adapters.header.HeaderListAdapter;
import de.dreier.mytargets.base.fragments.SelectItemFragmentBase;
import de.dreier.mytargets.databinding.FragmentTargetSelectBinding;
import de.dreier.mytargets.databinding.ItemImageSimpleBinding;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.ETargetType;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.targets.TargetFactory;
import de.dreier.mytargets.shared.targets.models.TargetModelBase;
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.SlideInItemAnimator;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.multiselector.SelectableViewHolder;

import static de.dreier.mytargets.base.activities.ItemSelectActivity.ITEM;
import static de.dreier.mytargets.features.training.target.TargetListFragment.EFixedType.GROUP;
import static de.dreier.mytargets.features.training.target.TargetListFragment.EFixedType.NONE;
import static de.dreier.mytargets.features.training.target.TargetListFragment.EFixedType.TARGET;

public class TargetListFragment extends SelectItemFragmentBase<Target,
        ExpandableListAdapter<HeaderListAdapter.SimpleHeader, Target>>
        implements AdapterView.OnItemSelectedListener {

    public static final String FIXED_TYPE = "fixed_type";
    private FragmentTargetSelectBinding binding;
    private ArrayAdapter<String> scoringStyleAdapter;
    private ArrayAdapter<String> targetSizeAdapter;

    @NonNull
    public static IntentWrapper getIntent(Target target) {
        return new IntentWrapper(TargetActivity.class)
                .with(ITEM, Parcels.wrap(target))
                .with(FIXED_TYPE, GROUP.name());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_target_select, container, false);
        adapter = new TargetAdapter();
        binding.recyclerView.setItemAnimator(new SlideInItemAnimator());
        binding.recyclerView.setAdapter(adapter);

        useDoubleClickSelection = true;
        ToolbarUtils.setSupportActionBar(this, binding.toolbar);
        ToolbarUtils.showHomeAsUp(this);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Needs activity context
        scoringStyleAdapter = getThemedSpinnerAdapter();
        binding.scoringStyle.setAdapter(scoringStyleAdapter);
        targetSizeAdapter = getThemedSpinnerAdapter();
        binding.targetSize.setAdapter(targetSizeAdapter);

        // Process passed arguments
        Target target = Parcels.unwrap(getArguments().getParcelable(ITEM));
        EFixedType fixedType = EFixedType
                .valueOf(getArguments().getString(FIXED_TYPE, NONE.name()));
        List<TargetModelBase> list;
        if (fixedType == TARGET) {
            list = Collections.singletonList(target.getModel());
        } else if (fixedType == GROUP) {
            list = TargetFactory.getList(target);
        } else {
            list = TargetFactory.getList();
        }
        List<Target> targets = Stream.of(list)
                .map(value -> new Target((int) (long) value.getId(), 0))
                .collect(Collectors.toList());
        adapter.setList(targets);
        selectItem(binding.recyclerView, target);

        updateSettings();

        // Set initial target size
        int diameterIndex = -1;
        Dimension[] diameters = target.getModel().getDiameters();
        for (int i = 0; i < diameters.length; i++) {
            if (diameters[i].equals(target.size)) {
                diameterIndex = i;
                break;
            }
        }

        setSelectionWithoutEvent(binding.scoringStyle, target.scoringStyle);
        setSelectionWithoutEvent(binding.targetSize, diameterIndex);
    }

    @Override
    protected void selectItem(RecyclerView recyclerView, Target item) {
        adapter.ensureItemIsExpanded(item);
        super.selectItem(recyclerView, item);
    }

    @Override
    public void onClick(SelectableViewHolder<Target> holder, Target item) {
        super.onClick(holder, item);
        if (item == null) {
            return;
        }
        updateSettings();
        saveItem();
    }

    private void updateSettings() {
        // Init scoring styles
        Target target = adapter.getItemById(selector.getSelectedId());
        List<String> styles = target.getModel().getScoringStyles();
        updateAdapter(binding.scoringStyle, scoringStyleAdapter, styles);

        // Init target size spinner
        ArrayList<String> diameters = diameterToList(target.getModel().getDiameters());
        updateAdapter(binding.targetSize, targetSizeAdapter, diameters);
        if (diameters.size() > 1) {
            binding.targetSize.setVisibility(View.VISIBLE);
        } else {
            binding.targetSize.setVisibility(View.GONE);
        }
    }

    private void updateAdapter(Spinner spinner, ArrayAdapter<String> spinnerAdapter, List<String> strings) {
        int lastSelection = spinner.getSelectedItemPosition();
        spinnerAdapter.clear();
        spinnerAdapter.addAll(strings);
        final int position = lastSelection < strings.size() ? lastSelection : strings.size() - 1;
        setSelectionWithoutEvent(spinner, position);
    }

    private void setSelectionWithoutEvent(Spinner spinner, int position) {
        spinner.setOnItemSelectedListener(null);
        spinner.setSelection(position, false);
        spinner.setOnItemSelectedListener(this);
    }

    @NonNull
    private ArrayAdapter<String> getThemedSpinnerAdapter() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        Assert.assertNotNull(actionBar);
        Context themedContext = actionBar.getThemedContext();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(themedContext,
                android.R.layout.simple_spinner_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return spinnerAdapter;
    }

    private ArrayList<String> diameterToList(Dimension[] diameters) {
        ArrayList<String> list = new ArrayList<>();
        for (Dimension diameter : diameters) {
            list.add(diameter.toString());
        }
        return list;
    }

    @Override
    public void onLongClick(SelectableViewHolder<Target> holder) {
        onClick(holder, holder.getItem());
    }

    @Override
    protected Target onSave() {
        Target target = super.onSave();
        target.scoringStyle = binding.scoringStyle.getSelectedItemPosition();
        Dimension[] diameters = target.getModel().getDiameters();
        target.size = diameters[binding.targetSize.getSelectedItemPosition()];
        getArguments().putParcelable(ITEM, Parcels.wrap(target));
        return target;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
        updateSettings();
        saveItem();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public enum EFixedType {
        /**
         * The user has completely free choice from all target faces.
         */
        NONE,

        /**
         * The user can change the selection within a group of target faces
         * like between the WA target faces.
         */
        GROUP,

        /**
         * The user cannot change the selected target face, but e.g. scoring style or diameter.
         */
        TARGET
    }

    private class TargetAdapter extends ExpandableListAdapter<HeaderListAdapter.SimpleHeader, Target> {
        TargetAdapter() {
            super(child -> {
                final ETargetType type = child.getModel().getType();
                return new HeaderListAdapter.SimpleHeader((long) type.ordinal(), type.toString());
            }, HeaderListAdapter.SimpleHeader::compareTo, TargetFactory.getComparator());
        }

        @Override
        protected ViewHolder getSecondLevelViewHolder(ViewGroup parent) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_image_simple, parent, false);
            return new ViewHolder(itemView);
        }
    }

    private class ViewHolder extends SelectableViewHolder<Target> {
        private ItemImageSimpleBinding binding;

        public ViewHolder(View itemView) {
            super(itemView, selector, TargetListFragment.this);
            binding = DataBindingUtil.bind(itemView);
        }

        @Override
        public void bindItem() {
            binding.name.setText(item.getModel().toString());
            binding.image.setImageDrawable(item.getDrawable());
        }
    }
}
