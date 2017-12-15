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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.evernote.android.state.State;

import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.activities.ItemSelectActivity;
import de.dreier.mytargets.base.adapters.dynamicitem.DynamicItemAdapter;
import de.dreier.mytargets.base.adapters.dynamicitem.DynamicItemHolder;
import de.dreier.mytargets.base.fragments.EditFragmentBase;
import de.dreier.mytargets.databinding.FragmentEditStandardRoundBinding;
import de.dreier.mytargets.databinding.ItemRoundTemplateBinding;
import de.dreier.mytargets.features.settings.SettingsManager;
import de.dreier.mytargets.shared.models.db.RoundTemplate;
import de.dreier.mytargets.shared.models.db.StandardRound;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.transitions.FabTransformUtil;
import de.dreier.mytargets.views.selector.DistanceSelector;
import de.dreier.mytargets.views.selector.SelectorBase;
import de.dreier.mytargets.views.selector.TargetSelector;

import static de.dreier.mytargets.base.activities.ItemSelectActivity.ITEM;

public class EditStandardRoundFragment extends EditFragmentBase {

    public static final int RESULT_STANDARD_ROUND_DELETED = Activity.RESULT_FIRST_USER;

    @State
    StandardRound standardRound;
    private RoundTemplateAdapter adapter;
    private FragmentEditStandardRoundBinding binding;

    @NonNull
    public static IntentWrapper createIntent() {
        return new IntentWrapper(EditStandardRoundActivity.class);
    }

    @NonNull
    public static IntentWrapper editIntent(StandardRound item) {
        return new IntentWrapper(EditStandardRoundActivity.class)
                .with(ITEM, item);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_edit_standard_round, container, false);

        ToolbarUtils.setSupportActionBar(this, binding.toolbar);
        ToolbarUtils.showUpAsX(this);
        setHasOptionsMenu(true);

        if (savedInstanceState == null) {
            if (getArguments() != null) {
                standardRound = getArguments().getParcelable(ITEM);
            }
            if (standardRound == null) {
                standardRound = new StandardRound();
                ToolbarUtils.setTitle(this, R.string.new_round_template);
                binding.name.setText(R.string.custom_round);
                // Initialize with default values
                addDefaultRound();
            } else {
                ToolbarUtils.setTitle(this, R.string.edit_standard_round);
                // Load saved values
                if (standardRound.getClub() == StandardRoundFactory.CUSTOM) {
                    binding.name.setText(standardRound.getName());
                } else {
                    standardRound.setId(null);
                    binding.name.setText(
                            String.format("%s %s", getString(R.string.custom), standardRound
                                    .getName()));
                    // When copying an existing standard round make sure
                    // we don't overwrite the other rounds templates
                    for (RoundTemplate round : standardRound.loadRounds()) {
                        round.setId(null);
                    }
                }
            }
        }

        adapter = new RoundTemplateAdapter(this, standardRound.loadRounds());
        binding.rounds.setAdapter(adapter);
        binding.addButton.setOnClickListener((view) -> onAddRound());
        binding.deleteStandardRound.setOnClickListener((view) -> onDeleteStandardRound());

        return binding.getRoot();
    }

    private void addDefaultRound() {
        RoundTemplate round = new RoundTemplate();
        round.setShotsPerEnd(SettingsManager.INSTANCE.getShotsPerEnd());
        round.setEndCount(SettingsManager.INSTANCE.getEndCount());
        round.setTargetTemplate(SettingsManager.INSTANCE.getTarget());
        round.setDistance(SettingsManager.INSTANCE.getDistance());
        standardRound.loadRounds().add(round);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FabTransformUtil.setup(getActivity(), binding.getRoot());
    }

    private void onAddRound() {
        int newItemIndex = standardRound.loadRounds().size();
        if (newItemIndex > 0) {
            RoundTemplate r = standardRound.loadRounds().get(newItemIndex - 1);
            RoundTemplate roundTemplate = new RoundTemplate();
            roundTemplate.setEndCount(r.getEndCount());
            roundTemplate.setShotsPerEnd(r.getShotsPerEnd());
            roundTemplate.setDistance(r.getDistance());
            roundTemplate.setTargetTemplate(r.getTargetTemplate());
            standardRound.loadRounds().add(roundTemplate);
        } else {
            addDefaultRound();
        }
        adapter.notifyItemInserted(newItemIndex);
    }

    private void onDeleteStandardRound() {
        standardRound.delete();
        getActivity().setResult(RESULT_STANDARD_ROUND_DELETED, null);
        finish();
    }

    @Override
    protected void onSave() {
        standardRound.setClub(StandardRoundFactory.CUSTOM);
        standardRound.setName(binding.name.getText().toString());
        standardRound.save();

        RoundTemplate round = standardRound.loadRounds().get(0); //FIXME how is this possible?
        SettingsManager.INSTANCE.setShotsPerEnd(round.getShotsPerEnd());
        SettingsManager.INSTANCE.setEndCount(round.getEndCount());
        SettingsManager.INSTANCE.setTarget(round.getTargetTemplate());
        SettingsManager.INSTANCE.setDistance(round.getDistance());

        Intent data = new Intent();
        data.putExtra(ITEM, standardRound);
        getActivity().setResult(Activity.RESULT_OK, data);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Bundle intentData = data.getBundleExtra(ItemSelectActivity.INTENT);
            final int index = intentData.getInt(SelectorBase.INDEX);
            switch (requestCode) {
                case DistanceSelector.DISTANCE_REQUEST_CODE:
                    standardRound.loadRounds().get(index).setDistance(data.getParcelableExtra(ITEM));
                    adapter.notifyItemChanged(index);
                    break;
                case TargetSelector.TARGET_REQUEST_CODE:
                    standardRound.loadRounds().get(index)
                            .setTargetTemplate(data.getParcelableExtra(ITEM));
                    adapter.notifyItemChanged(index);
                    break;
            }
        }
    }

    private static class RoundTemplateHolder extends DynamicItemHolder<RoundTemplate> {

        ItemRoundTemplateBinding binding;

        RoundTemplateHolder(@NonNull View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
        }

        @Override
        public void onBind(RoundTemplate roundTemplate, int position, @NonNull Fragment fragment, View.OnClickListener removeListener) {
            item = roundTemplate;

            // Set title of round
            binding.roundNumber.setText(fragment.getResources()
                    .getQuantityString(R.plurals.rounds, position + 1, position + 1));
            item.setIndex(position);

            binding.distance.setOnActivityResultContext(fragment);
            binding.distance.setItemIndex(position);
            binding.distance.setItem(item.getDistance());

            // Target round
            binding.target.setOnActivityResultContext(fragment);
            binding.target.setItemIndex(position);
            binding.target.setItem(item.getTargetTemplate());

            // Ends
            binding.endCount.setTextPattern(R.plurals.passe);
            binding.endCount.setOnValueChangedListener(val -> item.setEndCount(val));
            binding.endCount.setValue(item.getEndCount());

            // Shots per end
            binding.shotCount.setTextPattern(R.plurals.arrow);
            binding.shotCount.setMinimum(1);
            binding.shotCount.setMaximum(12);
            binding.shotCount.setOnValueChangedListener(val -> item.setShotsPerEnd(val));
            binding.shotCount.setValue(item.getShotsPerEnd());

            if (position == 0) {
                binding.remove.setVisibility(View.GONE);
            } else {
                binding.remove.setVisibility(View.VISIBLE);
                binding.remove.setOnClickListener(removeListener);
            }
        }
    }

    private class RoundTemplateAdapter extends DynamicItemAdapter<RoundTemplate> {
        RoundTemplateAdapter(@NonNull Fragment fragment, List<RoundTemplate> list) {
            super(fragment, list, R.string.round_removed);
        }

        @NonNull
        @Override
        public DynamicItemHolder<RoundTemplate> onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = inflater.inflate(R.layout.item_round_template, parent, false);
            return new RoundTemplateHolder(v);
        }
    }
}
