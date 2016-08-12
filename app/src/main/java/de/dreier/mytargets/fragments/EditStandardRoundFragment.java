/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.fragments;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.ItemSelectActivity;
import de.dreier.mytargets.adapters.DynamicItemHolder;
import de.dreier.mytargets.databinding.DynamicitemRoundTemplateBinding;
import de.dreier.mytargets.databinding.FragmentEditStandardRoundBinding;
import de.dreier.mytargets.managers.SettingsManager;
import de.dreier.mytargets.managers.dao.StandardRoundDataSource;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.utils.ParcelsBundler;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.views.selector.DistanceSelector;
import de.dreier.mytargets.views.selector.SelectorBase;
import de.dreier.mytargets.views.selector.TargetSelector;
import icepick.State;

import static de.dreier.mytargets.activities.ItemSelectActivity.ITEM;

public class EditStandardRoundFragment extends EditFragmentBase {

    @State(ParcelsBundler.class)
    List<RoundTemplate> roundTemplateList = new ArrayList<>();
    private long standardRoundId = -1;
    private RoundTemplateAdapter adapter;
    private FragmentEditStandardRoundBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_edit_standard_round, container, false);

        ToolbarUtils.setSupportActionBar(this, binding.toolbar);
        ToolbarUtils.showUpAsX(this);
        setHasOptionsMenu(true);

        StandardRound standardRound = null;
        if (getArguments() != null) {
            standardRound = Parcels.unwrap(getArguments().getParcelable(ITEM));
        }

        if (savedInstanceState == null) {
            if (standardRound == null) {
                ToolbarUtils.setTitle(this, R.string.new_round_template);
                binding.name.setText(R.string.custom_round);
                // Initialise with default values
                binding.indoor.setChecked(SettingsManager.getIndoor());
                binding.outdoor.setChecked(!SettingsManager.getIndoor());

                RoundTemplate round = new RoundTemplate();
                round.arrowsPerEnd = SettingsManager.getArrowsPerPasse();
                round.endCount = SettingsManager.getPasses();
                round.target = SettingsManager.getTarget();
                round.targetTemplate = round.target;
                round.distance = SettingsManager.getDistance();
                roundTemplateList.add(round);
            } else {
                ToolbarUtils.setTitle(this, R.string.edit_standard_round);
                // Load saved values
                binding.indoor.setChecked(standardRound.indoor);
                binding.outdoor.setChecked(!standardRound.indoor);
                roundTemplateList = standardRound.rounds;
                if (standardRound.club == StandardRoundFactory.CUSTOM) {
                    binding.name.setText(standardRound.name);
                    standardRoundId = standardRound.getId();
                } else {
                    binding.name.setText(
                            String.format("%s %s", getString(R.string.custom), standardRound.name));
                    for (RoundTemplate round : roundTemplateList) {
                        round.setId(-1);
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

    private void onAddRound() {
        RoundTemplate r = roundTemplateList.get(roundTemplateList.size() - 1);
        RoundTemplate roundTemplate = new RoundTemplate();
        roundTemplate.endCount = r.endCount;
        roundTemplate.arrowsPerEnd = r.arrowsPerEnd;
        roundTemplate.distance = r.distance;
        roundTemplate.target = r.target;
        roundTemplate.target.size = r.target.size;
        roundTemplate.targetTemplate = r.targetTemplate;
        roundTemplateList.add(roundTemplate);
        adapter.notifyItemInserted(roundTemplateList.size() - 1);
    }

    private void onDeleteStandardRound() {
        new StandardRoundDataSource().delete(standardRoundId);
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    @Override
    protected void onSave() {
        StandardRound standardRound = new StandardRound();
        standardRound.setId(standardRoundId);
        standardRound.club = StandardRoundFactory.CUSTOM;
        standardRound.name = binding.name.getText().toString();
        standardRound.indoor = binding.indoor.isChecked();
        standardRound.rounds = roundTemplateList;
        new StandardRoundDataSource().update(standardRound);

        SettingsManager.setIndoor(standardRound.indoor);

        RoundTemplate round = roundTemplateList.get(0);
        SettingsManager.setArrowsPerEnd(round.arrowsPerEnd);
        SettingsManager.setPasses(round.endCount);
        SettingsManager.setTarget(round.target);
        SettingsManager.setDistance(round.distance);

        Intent data = new Intent();
        data.putExtra(ITEM, Parcels.wrap(standardRound));
        getActivity().setResult(Activity.RESULT_OK, data);
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Bundle intentData = data.getBundleExtra(ItemSelectActivity.INTENT);
            final int index = intentData.getInt(SelectorBase.INDEX);
            final Parcelable parcelable = data.getParcelableExtra(ItemSelectActivity.ITEM);
            switch (requestCode) {
                case DistanceSelector.DISTANCE_REQUEST_CODE:
                    roundTemplateList.get(index).distance = Parcels.unwrap(parcelable);
                    adapter.notifyItemChanged(index);
                    break;
                case TargetSelector.TARGET_REQUEST_CODE:
                    roundTemplateList.get(index).target = Parcels.unwrap(parcelable);
                    roundTemplateList.get(index).targetTemplate = Parcels.unwrap(parcelable);
                    adapter.notifyItemChanged(index);
                    break;
            }
        }
    }

    private static class RoundTemplateHolder extends DynamicItemHolder<RoundTemplate> {

        DynamicitemRoundTemplateBinding binding;

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

            binding.distanceSpinner.setOnActivityResultContext(fragment);
            binding.distanceSpinner.setItemIndex(position);
            binding.distanceSpinner.setItem(item.distance);

            // Target round
            binding.targetSpinner.setOnActivityResultContext(fragment);
            binding.targetSpinner.setItemIndex(position);
            binding.targetSpinner.setItem(item.target);

            // Passes
            binding.passes.setTextPattern(R.plurals.passe);
            binding.passes.setOnValueChangedListener(val -> item.endCount = val);
            binding.passes.setValue(item.endCount);

            // Arrows per end
            binding.arrows.setTextPattern(R.plurals.arrow);
            binding.arrows.setMinimum(1);
            binding.arrows.setMaximum(12);
            binding.arrows.setOnValueChangedListener(val -> item.arrowsPerEnd = val);
            binding.arrows.setValue(item.arrowsPerEnd);

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
            View v = inflater.inflate(R.layout.dynamicitem_round_template, parent, false);
            return new RoundTemplateHolder(v);
        }
    }
}
