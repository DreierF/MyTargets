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

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.InputActivity;
import de.dreier.mytargets.activities.SimpleFragmentActivityBase;
import de.dreier.mytargets.databinding.FragmentEditRoundBinding;
import de.dreier.mytargets.managers.SettingsManager;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.transitions.FabTransformUtil;

import static de.dreier.mytargets.fragments.EditableListFragmentBase.ITEM_ID;

public class EditRoundFragment extends EditFragmentBase {
    private static final String ROUND_ID = "round_id";
    private Long trainingId = null;
    private Long roundId = null;
    private FragmentEditRoundBinding binding;

    @NonNull
    protected static IntentWrapper createIntent(Training training) {
        return new IntentWrapper(SimpleFragmentActivityBase.EditRoundActivity.class)
                .with(ITEM_ID, training.getId());
    }

    @NonNull
    protected static IntentWrapper editIntent(Training training, Round round) {
        return new IntentWrapper(SimpleFragmentActivityBase.EditRoundActivity.class)
                .with(ITEM_ID, training.getId())
                .with(ROUND_ID, round.getId());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_edit_round, container, false);

        Bundle arguments = getArguments();
        trainingId = arguments.getLong(ITEM_ID);
        if (arguments.containsKey(ROUND_ID)) {
            roundId = arguments.getLong(ROUND_ID);
        }

        ToolbarUtils.setSupportActionBar(this, binding.toolbar);
        ToolbarUtils.showUpAsX(this);
        setHasOptionsMenu(true);

        binding.arrows.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                updateArrowsLabel();
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
            }
        });
        binding.target.setOnActivityResultContext(this);
        binding.distance.setOnActivityResultContext(this);

        if (roundId == null) {
            ToolbarUtils.setTitle(this, R.string.new_round);
            loadRoundDefaultValues();
            binding.comment.setText("");
            binding.removeButton.setVisibility(View.GONE);
        } else {
            ToolbarUtils.setTitle(this, R.string.edit_round);
            Round round = Round.get(roundId);
            binding.distance.setItem(round.distance);
            binding.comment.setText(round.comment);
            binding.notEditable.setVisibility(View.GONE);
            if (round.getTraining().standardRoundId != null) {
                binding.distanceLayout.setVisibility(View.GONE);
            } else {
                binding.removeButton.setOnClickListener(v -> {
                    round.delete();
                    finish();
                });
            }
        }
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FabTransformUtil.setup(getActivity(), binding.getRoot());
    }

    @Override
    protected void onSave() {
        finish();
        if (roundId == null) {
            Round round = onSaveRound();
            RoundFragment.getIntent(round)
                    .withContext(this)
                    .noAnimation()
                    .start();
            InputActivity.createIntent(round)
                    .withContext(this)
                    .start();
        } else {
            onSaveRound();
            getActivity().overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }
    }

    private Round onSaveRound() {
        Training training = Training.get(trainingId);

        Round round;
        if (roundId == null) {
            round = new Round();
            round.trainingId = trainingId;
            round.setTarget(binding.target.getSelectedItem());
            round.shotsPerEnd = binding.arrows.getProgress();
            round.maxEndCount = null;
            round.comment = binding.comment.getText().toString();
            round.distance = binding.distance.getSelectedItem();
            round.index = training.getRounds().size();
        } else {
            round = Round.get(roundId);
            round.comment = binding.comment.getText().toString();
        }
        round.save();
        return round;
    }

    private void updateArrowsLabel() {
        binding.arrowsLabel.setText(getResources()
                .getQuantityString(R.plurals.arrow, binding.arrows.getProgress(),
                        binding.arrows.getProgress()));
    }

    private void loadRoundDefaultValues() {
        binding.distance.setItem(SettingsManager.getDistance());
        binding.arrows.setProgress(SettingsManager.getShotsPerEnd());
        binding.target.setItem(SettingsManager.getTarget());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        binding.target.onActivityResult(requestCode, resultCode, data);
        binding.distance.onActivityResult(requestCode, resultCode, data);
    }
}
