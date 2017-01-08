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
package de.dreier.mytargets.features.training.standardround;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.activities.ItemSelectActivity;
import de.dreier.mytargets.base.adapters.dynamicitem.DynamicItemAdapter;
import de.dreier.mytargets.base.adapters.dynamicitem.DynamicItemHolder;
import de.dreier.mytargets.databinding.FragmentEditStandardRoundBinding;
import de.dreier.mytargets.databinding.ItemRoundTemplateBinding;
import de.dreier.mytargets.features.settings.SettingsManager;
import de.dreier.mytargets.base.fragments.EditFragmentBase;
import de.dreier.mytargets.shared.models.db.RoundTemplate;
import de.dreier.mytargets.shared.models.db.StandardRound;
import de.dreier.mytargets.shared.utils.ParcelsBundler;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.transitions.FabTransformUtil;
import de.dreier.mytargets.views.selector.DistanceSelector;
import de.dreier.mytargets.views.selector.SelectorBase;
import de.dreier.mytargets.views.selector.TargetSelector;
import icepick.State;

import static de.dreier.mytargets.base.activities.ItemSelectActivity.ITEM;

public class EditStandardRoundFragment extends EditFragmentBase {

    public static final int RESULT_STANDARD_ROUND_DELETED = Activity.RESULT_FIRST_USER;

    @State(ParcelsBundler.class)
    List<RoundTemplate> roundTemplateList = new ArrayList<>();
    private StandardRound standardRound;
    private RoundTemplateAdapter adapter;
    private FragmentEditStandardRoundBinding binding;

    @NonNull
    public static IntentWrapper createIntent() {
        return new IntentWrapper(EditStandardRoundActivity.class);
    }

    @NonNull
    public static IntentWrapper editIntent(StandardRound item) {
        return new IntentWrapper(EditStandardRoundActivity.class)
                .with(ITEM, Parcels.wrap(item));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_edit_standard_round, container, false);

        ToolbarUtils.setSupportActionBar(this, binding.toolbar);
        ToolbarUtils.showUpAsX(this);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            standardRound = Parcels.unwrap(getArguments().getParcelable(ITEM));
        }

        if (savedInstanceState == null) {
            if (standardRound == null) {
                standardRound = new StandardRound();
                ToolbarUtils.setTitle(this, R.string.new_round_template);
                binding.name.setText(R.string.custom_round);
                // Initialize with default values
                RoundTemplate round = new RoundTemplate();
                round.shotsPerEnd = SettingsManager.getShotsPerEnd();
                round.endCount = SettingsManager.getEndCount();
                round.setTargetTemplate(SettingsManager.getTarget());
                round.distance = SettingsManager.getDistance();
                roundTemplateList.add(round);
            } else {
                ToolbarUtils.setTitle(this, R.string.edit_standard_round);
                // Load saved values
                roundTemplateList = standardRound.getRounds();
                if (standardRound.club == StandardRoundFactory.CUSTOM) {
                    binding.name.setText(standardRound.name);
                } else {
                    binding.name.setText(
                            String.format("%s %s", getString(R.string.custom), standardRound.name));
                    for (RoundTemplate round : roundTemplateList) {
                        round.setId(-1L);
                    }
                }
            }
        }

        adapter = new RoundTemplateAdapter(this, roundTemplateList);
        binding.rounds.setAdapter(adapter);
        binding.addButton.setOnClickListener((view) -> onAddRound());
        binding.deleteStandardRound.setOnClickListener((view) -> onDeleteStandardRound());

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FabTransformUtil.setup(getActivity(), binding.getRoot());
    }

    private void onAddRound() {
        RoundTemplate r = roundTemplateList.get(roundTemplateList.size() - 1);
        RoundTemplate roundTemplate = new RoundTemplate();
        roundTemplate.endCount = r.endCount;
        roundTemplate.shotsPerEnd = r.shotsPerEnd;
        roundTemplate.distance = r.distance;
        roundTemplate.setTargetTemplate(r.getTargetTemplate());
        roundTemplateList.add(roundTemplate);
        adapter.notifyItemInserted(roundTemplateList.size() - 1);
    }

    private void onDeleteStandardRound() {
        standardRound.delete();
        getActivity().setResult(RESULT_STANDARD_ROUND_DELETED, null);
        finish();
    }

    @Override
    protected void onSave() {
        standardRound.club = StandardRoundFactory.CUSTOM;
        standardRound.name = binding.name.getText().toString();
        standardRound.setRounds(roundTemplateList);
        standardRound.save();

        RoundTemplate round = roundTemplateList.get(0);
        SettingsManager.setShotsPerEnd(round.shotsPerEnd);
        SettingsManager.setEndCount(round.endCount);
        SettingsManager.setTarget(round.getTargetTemplate());
        SettingsManager.setDistance(round.distance);

        Intent data = new Intent();
        data.putExtra(ITEM, Parcels.wrap(standardRound));
        getActivity().setResult(Activity.RESULT_OK, data);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Bundle intentData = data.getBundleExtra(ItemSelectActivity.INTENT);
            final int index = intentData.getInt(SelectorBase.INDEX);
            final Parcelable parcelable = data.getParcelableExtra(ITEM);
            switch (requestCode) {
                case DistanceSelector.DISTANCE_REQUEST_CODE:
                    roundTemplateList.get(index).distance = Parcels.unwrap(parcelable);
                    adapter.notifyItemChanged(index);
                    break;
                case TargetSelector.TARGET_REQUEST_CODE:
                    roundTemplateList.get(index).setTargetTemplate(Parcels.unwrap(parcelable));
                    adapter.notifyItemChanged(index);
                    break;
            }
        }
    }

    private static class RoundTemplateHolder extends DynamicItemHolder<RoundTemplate> {

        ItemRoundTemplateBinding binding;

        RoundTemplateHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
        }

        @Override
        public void onBind(RoundTemplate roundTemplate, int position, Fragment fragment, View.OnClickListener removeListener) {
            item = roundTemplate;

            // Set title of round
            binding.roundNumber.setText(fragment.getContext().getResources()
                    .getQuantityString(R.plurals.rounds, position + 1, position + 1));
            item.index = position;

            binding.distance.setOnActivityResultContext(fragment);
            binding.distance.setItemIndex(position);
            binding.distance.setItem(item.distance);

            // Target round
            binding.target.setOnActivityResultContext(fragment);
            binding.target.setItemIndex(position);
            binding.target.setItem(item.getTargetTemplate());

            // Ends
            binding.endCount.setTextPattern(R.plurals.passe);
            binding.endCount.setOnValueChangedListener(val -> item.endCount = val);
            binding.endCount.setValue(item.endCount);

            // Shots per end
            binding.shotCount.setTextPattern(R.plurals.arrow);
            binding.shotCount.setMinimum(1);
            binding.shotCount.setMaximum(12);
            binding.shotCount.setOnValueChangedListener(val -> item.shotsPerEnd = val);
            binding.shotCount.setValue(item.shotsPerEnd);

            if (position == 0) {
                binding.remove.setVisibility(View.GONE);
            } else {
                binding.remove.setVisibility(View.VISIBLE);
                binding.remove.setOnClickListener(removeListener);
            }
        }
    }

    private class RoundTemplateAdapter extends DynamicItemAdapter<RoundTemplate> {
        RoundTemplateAdapter(Fragment fragment, List<RoundTemplate> list) {
            super(fragment, list, R.string.round_removed);
        }

        @Override
        public DynamicItemHolder<RoundTemplate> onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = inflater.inflate(R.layout.item_round_template, parent, false);
            return new RoundTemplateHolder(v);
        }
    }
}
