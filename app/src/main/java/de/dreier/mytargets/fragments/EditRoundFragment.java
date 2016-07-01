/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
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
import de.dreier.mytargets.databinding.FragmentEditRoundBinding;
import de.dreier.mytargets.managers.SettingsManager;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.RoundTemplate;
import de.dreier.mytargets.shared.models.db.StandardRound;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;
import de.dreier.mytargets.utils.ActivityUtils;
import de.dreier.mytargets.utils.ToolbarUtils;

public class EditRoundFragment extends EditFragmentBase {
    static final String ROUND_ID = "round_id";
    private long trainingId = -1;
    private long roundId = -1;
    private FragmentEditRoundBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_edit_round, container, false);

        Bundle arguments = getArguments();
        if (arguments != null) {
            trainingId = arguments.getLong(FragmentBase.ITEM_ID, -1);
            roundId = arguments.getLong(ROUND_ID, -1);
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
        binding.targetSpinner.setOnActivityResultContext(this);
        binding.distanceSpinner.setOnActivityResultContext(this);


        if (roundId == -1) {
            ToolbarUtils.setTitle(this, R.string.new_round);
            loadRoundDefaultValues();
            binding.comment.setText("");
            binding.removeButton.setVisibility(View.GONE);
        } else {
            ToolbarUtils.setTitle(this, R.string.edit_round);
            Round round = Round.get(roundId);
            binding.distanceSpinner.setItem(round.info.distance);
            binding.comment.setText(round.comment);
            binding.notEditable.setVisibility(View.GONE);
            StandardRound standardRound = StandardRound.get(round.info.standardRound);
            if (standardRound.club != StandardRoundFactory.CUSTOM_PRACTICE) {
                binding.distanceLayout.setVisibility(View.GONE);
            } else if (standardRound.rounds.size() > 1) {
                binding.removeButton.setOnClickListener(v -> {
                    round.delete();
                    getActivity().finish();
                });
            } else {
                binding.removeButton.setVisibility(View.GONE);
            }
        }
        return binding.getRoot();
    }

    @Override
    protected void onSave() {
        getActivity().finish();
        if (roundId == -1) {
            Round round = onSaveRound();
            ActivityUtils.openPasseForNewRound(getActivity(), -1, round.getId());
        } else {
            onSaveRound();
            getActivity().overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }
    }

    private Round onSaveRound() {
        Training training = Training.get(trainingId);
        StandardRound standardRound = StandardRound.get(training.standardRoundId);

        Round round;
        if (roundId != -1) {
            round = Round.get(roundId);
        } else {
            round = new Round();
            round.trainingId = trainingId;
            round.info = getRoundTemplate();
            round.info.standardRound = standardRound.getId();
        }

        round.comment = binding.comment.getText().toString();

        if (standardRound.club == StandardRoundFactory.CUSTOM_PRACTICE) {
            round.info.distance = binding.distanceSpinner.getSelectedItem();
            round.info.index = standardRound.rounds.size();
            round.info.save();
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
        binding.distanceSpinner.setItem(SettingsManager.getDistance());
        binding.arrows.setProgress(SettingsManager.getArrowsPerPasse());
        binding.targetSpinner.setItem(SettingsManager.getTarget());
    }

    @NonNull
    private RoundTemplate getRoundTemplate() {
        RoundTemplate roundTemplate = new RoundTemplate();
        roundTemplate.target = binding.targetSpinner.getSelectedItem();
        roundTemplate.setTargetTemplate(roundTemplate.target);
        roundTemplate.arrowsPerPasse = binding.arrows.getProgress();
        roundTemplate.passes = 1;
        roundTemplate.distance = binding.distanceSpinner.getSelectedItem();

        SettingsManager.setTarget(roundTemplate.target);
        SettingsManager.setDistance(roundTemplate.distance);
        SettingsManager.setArrowsPerPasse(roundTemplate.arrowsPerPasse);
        return roundTemplate;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        binding.targetSpinner.onActivityResult(requestCode, resultCode, data);
        binding.distanceSpinner.onActivityResult(requestCode, resultCode, data);
    }
}