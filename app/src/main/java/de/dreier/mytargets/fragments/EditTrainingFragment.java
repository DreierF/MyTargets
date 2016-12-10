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

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.joda.time.LocalDate;
import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.InputActivity;
import de.dreier.mytargets.activities.ItemSelectActivity;
import de.dreier.mytargets.activities.SimpleFragmentActivityBase;
import de.dreier.mytargets.databinding.FragmentEditTrainingBinding;
import de.dreier.mytargets.managers.SettingsManager;
import de.dreier.mytargets.models.ETrainingType;
import de.dreier.mytargets.shared.models.EBowType;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.db.Bow;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.RoundTemplate;
import de.dreier.mytargets.shared.models.db.StandardRound;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.shared.targets.models.WA3Ring3Spot;
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.transitions.FabTransformUtil;

import static de.dreier.mytargets.fragments.DatePickerFragment.ARG_CURRENT_DATE;
import static de.dreier.mytargets.fragments.EditableListFragmentBase.ITEM_ID;
import static de.dreier.mytargets.models.ETrainingType.FREE_TRAINING;
import static de.dreier.mytargets.models.ETrainingType.TRAINING_WITH_STANDARD_ROUND;

public class EditTrainingFragment extends EditFragmentBase implements DatePickerDialog.OnDateSetListener {
    public static final String CREATE_FREE_TRAINING_ACTION = "free_training";
    public static final String CREATE_TRAINING_WITH_STANDARD_ROUND_ACTION = "with_standard_round";

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQ_SELECTED_DATE = 2;
    private static final int SR_TARGET_REQUEST_CODE = 11;

    private Long trainingId = null;
    private ETrainingType trainingType = FREE_TRAINING;
    private LocalDate date = new LocalDate();
    private FragmentEditTrainingBinding binding;
    private Target roundTarget;

    @NonNull
    public static IntentWrapper createIntent(String trainingTypeAction) {
        return new IntentWrapper(SimpleFragmentActivityBase.EditTrainingActivity.class)
                .action(trainingTypeAction);
    }

    @NonNull
    public static IntentWrapper editIntent(Training training) {
        return new IntentWrapper(SimpleFragmentActivityBase.EditTrainingActivity.class)
                .with(ITEM_ID, training.getId());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_edit_training, container, false);

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(ITEM_ID)) {
            trainingId = arguments.getLong(ITEM_ID);
        }
        Intent i = getActivity().getIntent();
        if (i != null && CREATE_TRAINING_WITH_STANDARD_ROUND_ACTION.equals(i.getAction())) {
            trainingType = TRAINING_WITH_STANDARD_ROUND;
        } else {
            trainingType = FREE_TRAINING;
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

        binding.bow.setOnUpdateListener(this::setScoringStyleForCompoundBow);

        if (trainingId == null) {
            ToolbarUtils.setTitle(this, R.string.new_training);
            binding.training.setText(getString(
                    trainingType == ETrainingType.COMPETITION ? R.string.competition : R.string.training));
            setTrainingDate();
            loadRoundDefaultValues();
            binding.bow.setItemId(SettingsManager.getBow());
            binding.arrow.setItemId(SettingsManager.getArrow());
            binding.standardRound.setItemId(SettingsManager.getStandardRound());
            binding.standardRound.setOnUpdateListener(
                    item -> roundTarget = item.getRounds().get(0).getTargetTemplate());
            binding.numberArrows.setChecked(SettingsManager.getArrowNumbersEnabled());
            binding.timer.setChecked(SettingsManager.getTimerEnabled());
            binding.indoor.setChecked(SettingsManager.getIndoor());
            binding.outdoor.setChecked(!SettingsManager.getIndoor());
            binding.environment.queryWeather(this, REQUEST_LOCATION_PERMISSION);

            final StandardRound item = binding.standardRound.getSelectedItem();
            roundTarget = item.getRounds().get(0).getTargetTemplate();
        } else {
            ToolbarUtils.setTitle(this, R.string.edit_training);
            Training train = Training.get(trainingId);
            binding.training.setText(train.title);
            date = train.date;
            binding.bow.setItemId(train.bow);
            binding.arrow.setItemId(train.arrow);
            binding.standardRound.setItemId(train.standardRoundId);
            binding.environment.setItem(train.getEnvironment());
            setTrainingDate();
            binding.notEditable.setVisibility(View.GONE);
            binding.timer.setChecked(train.timePerEnd != -1);
        }
        binding.standardRound.setOnActivityResultContext(this);
        binding.standardRound.setOnUpdateListener(this::updateChangeTargetFaceVisibility);
        binding.changeTargetFace.setOnClickListener(v -> TargetListFragment.getIntent(roundTarget)
                .withContext(this)
                .forResult(SR_TARGET_REQUEST_CODE)
                .start());
        updateChangeTargetFaceVisibility(binding.standardRound.getSelectedItem());
        binding.arrow.setOnActivityResultContext(this);
        binding.bow.setOnActivityResultContext(this);
        binding.environment.setOnActivityResultContext(this);
        binding.trainingDate.setOnClickListener(view -> onDateClick());
        applyTrainingType();
        updateArrowsLabel();

        return binding.getRoot();
    }

    private void updateChangeTargetFaceVisibility(StandardRound item) {
        Target target = item.getRounds().get(0).getTargetTemplate();
        final boolean canBeChanged = (target.id < 7 || target.id == 10 || target.id == 11)
                && trainingType == TRAINING_WITH_STANDARD_ROUND;
        binding.changeTargetFace.setVisibility(canBeChanged ? View.VISIBLE : View.GONE);
    }

    protected void setScoringStyleForCompoundBow(Bow bow) {
        final Target target = binding.target.getSelectedItem();
        if (bow != null && target != null && target.id <= WA3Ring3Spot.ID) {
            if (bow.type == EBowType.COMPOUND_BOW && target.scoringStyle == 0) {
                target.scoringStyle = 1;
                binding.target.setItem(target);
            } else if (bow.type != EBowType.COMPOUND_BOW && target.scoringStyle == 1) {
                target.scoringStyle = 0;
                binding.target.setItem(target);
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FabTransformUtil.setup(getActivity(), binding.getRoot());
    }

    private void applyTrainingType() {
        View in;
        View out;
        if (trainingType == FREE_TRAINING) {
            in = binding.practiceLayout;
            out = binding.standardRound;
        } else {
            out = binding.practiceLayout;
            in = binding.standardRound;
        }
        in.setVisibility(View.VISIBLE);
        out.setVisibility(View.GONE);
    }

    private void onDateClick() {
        // Package bundle with fragment arguments
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_CURRENT_DATE, date);

        // Create and show date picker
        DatePickerFragment datePickerDialog = new DatePickerFragment();
        datePickerDialog.setTargetFragment(EditTrainingFragment.this, REQ_SELECTED_DATE);
        datePickerDialog.setArguments(bundle);
        datePickerDialog.show(getActivity().getSupportFragmentManager(), "date_picker");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            binding.environment.onPermissionResult(getActivity(), grantResults);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        date = new LocalDate(year, monthOfYear + 1, dayOfMonth);
        setTrainingDate();
    }

    private void setTrainingDate() {
        binding.trainingDate.setText(SimpleDateFormat.getDateInstance().format(date.toDate()));
    }

    @Override
    protected void onSave() {
        Training training = getTraining();
        finish();

        if (trainingId == null) {
            if (trainingType == FREE_TRAINING) {
                training.standardRoundId = null;
                training.rounds.add(getRound());
            } else {
                StandardRound standardRound = binding.standardRound.getSelectedItem();
                SettingsManager.setStandardRound(standardRound.getId());
                standardRound.save();
                training.standardRoundId = standardRound.getId();
                training.rounds.addAll(createRoundsFromTemplate(standardRound, training));
            }
            training.save();

            Round round = training.getRounds().get(0);

            TrainingFragment.getIntent(training)
                    .withContext(this)
                    .noAnimation()
                    .start();
            RoundFragment.getIntent(round)
                    .withContext(this)
                    .noAnimation()
                    .start();
            InputActivity.createIntent(round)
                    .withContext(this)
                    .start();
        } else {
            // Edit training
            training.update();
            getActivity().overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }
    }

    @NonNull
    private Training getTraining() {
        Training training;
        if (trainingId == null) {
            training = new Training();
        } else {
            training = Training.get(trainingId);
        }
        training.title = binding.training.getText().toString();
        training.date = date;
        training.setEnvironment(binding.environment.getSelectedItem());
        training.bow = binding.bow.getSelectedItem() == null ? 0 : binding.bow.getSelectedItem()
                .getId();
        training.arrow = binding.arrow.getSelectedItem() == null ? 0 : binding.arrow
                .getSelectedItem().getId();
        training.timePerEnd = binding.timer.isChecked() ? SettingsManager
                .getTimerShootTime() : -1;
        training.arrowNumbering = binding.numberArrows.isChecked();
        training.indoor = binding.indoor.isChecked();

        SettingsManager.setBow(training.bow);
        SettingsManager.setArrow(training.arrow);
        SettingsManager.setTimerEnabled(binding.timer.isChecked());
        SettingsManager.setArrowNumbersEnabled(training.arrowNumbering);
        SettingsManager.setIndoor(training.indoor);
        return training;
    }

    @NonNull
    private ArrayList<Round> createRoundsFromTemplate(StandardRound standardRound, Training training) {
        ArrayList<Round> rounds = new ArrayList<>();
        for (RoundTemplate template : standardRound.getRounds()) {
            Round round = new Round(template);
            round.trainingId = training.getId();
            if(trainingType == FREE_TRAINING) {
                round.setTarget(binding.target.getSelectedItem());
            } else {
                round.setTarget(roundTarget);
            }
            round.comment = "";
            round.save();
            rounds.add(round);
        }
        return rounds;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        binding.target.onActivityResult(requestCode, resultCode, data);
        binding.distance.onActivityResult(requestCode, resultCode, data);
        binding.standardRound.onActivityResult(requestCode, resultCode, data);
        binding.arrow.onActivityResult(requestCode, resultCode, data);
        binding.bow.onActivityResult(requestCode, resultCode, data);
        binding.environment.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == SR_TARGET_REQUEST_CODE) {
            final Parcelable parcelable = data.getParcelableExtra(ItemSelectActivity.ITEM);
            roundTarget = Parcels.unwrap(parcelable);
        }
    }

    private void updateArrowsLabel() {
        binding.arrowsLabel.setText(getResources()
                .getQuantityString(R.plurals.arrow, binding.arrows.getProgress(),
                        binding.arrows.getProgress()));
    }

    private void loadRoundDefaultValues() {
        binding.distance.setItem(SettingsManager.getDistance());
        binding.arrows.setProgress(SettingsManager.getArrowsPerEnd());
        binding.target.setItem(SettingsManager.getTarget());
    }

    @NonNull
    private Round getRound() {
        Round round = new Round();
        round.setTarget(binding.target.getSelectedItem());
        round.shotsPerEnd = binding.arrows.getProgress();
        round.maxEndCount = null;
        round.distance = binding.distance.getSelectedItem();

        SettingsManager.setTarget(binding.target.getSelectedItem());
        SettingsManager.setDistance(round.distance);
        SettingsManager.setShotsPerEnd(round.shotsPerEnd);
        return round;
    }
}
