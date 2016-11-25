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
import de.dreier.mytargets.managers.dao.RoundDataSource;
import de.dreier.mytargets.managers.dao.StandardRoundDataSource;
import de.dreier.mytargets.managers.dao.TrainingDataSource;
import de.dreier.mytargets.shared.models.Arrow;
import de.dreier.mytargets.shared.models.Bow;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.EBowType;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.Training;
import de.dreier.mytargets.shared.targets.models.WA3Ring3Spot;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.transitions.FabTransformUtil;

import static de.dreier.mytargets.fragments.DatePickerFragment.ARG_CURRENT_DATE;
import static de.dreier.mytargets.fragments.EditableListFragmentBase.ITEM_ID;

public class EditTrainingFragment extends EditFragmentBase implements DatePickerDialog.OnDateSetListener {
    public static final String CREATE_FREE_TRAINING_ACTION = "free_training";
    public static final String CREATE_TRAINING_WITH_STANDARD_ROUND_ACTION = "with_standard_round";

    private static final int FREE_TRAINING = 0;
    private static final int TRAINING_WITH_STANDARD_ROUND = 1;
    private static final int COMPETITION = 2;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQ_SELECTED_DATE = 2;
    private static final int SR_TARGET_REQUEST_CODE = 11;

    private long trainingId = -1;
    private int trainingType = FREE_TRAINING;
    private LocalDate date = new LocalDate();
    private FragmentEditTrainingBinding binding;

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
        if (arguments != null) {
            trainingId = arguments.getLong(ITEM_ID, -1);
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
        binding.arrow.setOnUpdateListener(this::updateArrowNumbers);

        if (trainingId == -1) {
            ToolbarUtils.setTitle(this, R.string.new_training);
            binding.training.setText(getString(
                    trainingType == COMPETITION ? R.string.competition : R.string.training));
            setTrainingDate();
            loadRoundDefaultValues();
            binding.bow.setItemId(SettingsManager.getBow());
            binding.arrow.setItemId(SettingsManager.getArrow());
            binding.standardRound.setItemId(SettingsManager.getStandardRound());
            binding.numberArrows.setChecked(SettingsManager.getArrowNumbersEnabled());
            binding.timer.setChecked(SettingsManager.getTimerEnabled());
            binding.indoor.setChecked(SettingsManager.getIndoor());
            binding.outdoor.setChecked(!SettingsManager.getIndoor());
            binding.environment.queryWeather(this, REQUEST_LOCATION_PERMISSION);
        } else {
            ToolbarUtils.setTitle(this, R.string.edit_training);
            Training train = new TrainingDataSource().get(trainingId);
            binding.training.setText(train.title);
            date = train.date;
            binding.bow.setItemId(train.bow);
            binding.arrow.setItemId(train.arrow);
            binding.standardRound.setItemId(train.standardRoundId);
            binding.environment.setItem(train.environment);
            setTrainingDate();
            binding.notEditable.setVisibility(View.GONE);
            binding.timer.setChecked(train.timePerPasse != -1);
        }
        binding.standardRound.setOnActivityResultContext(this);
        binding.standardRound.setOnUpdateListener(this::updateChangeTargetFaceVisibility);
        binding.changeTargetFace.setOnClickListener(v -> {
            final StandardRound item = binding.standardRound.getSelectedItem();
            Target target = item.rounds.get(0).targetTemplate;
            TargetListFragment.getIntent(target)
                    .withContext(this)
                    .forResult(SR_TARGET_REQUEST_CODE)
                    .start();
        });
        updateChangeTargetFaceVisibility(binding.standardRound.getSelectedItem());
        binding.arrow.setOnActivityResultContext(this);
        binding.bow.setOnActivityResultContext(this);
        binding.environment.setOnActivityResultContext(this);
        binding.trainingDate.setOnClickListener(view -> onDateClick());
        applyTrainingType();
        updateArrowsLabel();
        updateArrowNumbers(binding.arrow.getSelectedItem());

        return binding.getRoot();
    }

    private void updateChangeTargetFaceVisibility(StandardRound item) {
        Target target = item.rounds.get(0).targetTemplate;
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

    private void updateArrowNumbers(Arrow item) {
        if (item == null || item.numbers.isEmpty()) {
            binding.numberArrows.setVisibility(View.GONE);
        } else {
            binding.numberArrows.setVisibility(View.VISIBLE);
        }
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

        TrainingDataSource trainingDataSource = new TrainingDataSource();
        if (trainingId == -1) {
            StandardRound standardRound;
            if (trainingType == 0) {
                standardRound = getCustomRound();
            } else {
                standardRound = binding.standardRound.getSelectedItem();
                SettingsManager.setStandardRound(standardRound.getId());
            }
            new StandardRoundDataSource().update(standardRound);
            training.standardRoundId = standardRound.getId();

            trainingDataSource.update(training);
            Round round = createRoundsFromTemplate(standardRound, training).get(0);

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
            trainingDataSource.update(training);
            getActivity().overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }
    }

    @NonNull
    private Training getTraining() {
        Training training;
        if (trainingId == -1) {
            training = new Training();
        } else {
            training = new TrainingDataSource().get(trainingId);
        }
        training.title = binding.training.getText().toString();
        training.date = date;
        training.environment = binding.environment.getSelectedItem();
        training.bow = binding.bow.getSelectedItem() == null ? 0 : binding.bow.getSelectedItem()
                .getId();
        training.arrow = binding.arrow.getSelectedItem() == null ? 0 : binding.arrow
                .getSelectedItem().getId();
        training.timePerPasse = binding.timer.isChecked() ? SettingsManager
                .getTimerShootTime() : -1;
        Arrow selectedItem = binding.arrow.getSelectedItem();
        training.arrowNumbering = !(selectedItem == null || selectedItem.numbers.isEmpty()) &&
                binding.numberArrows.isChecked();

        SettingsManager.setBow(training.bow);
        SettingsManager.setArrow(training.arrow);
        SettingsManager.setTimerEnabled(binding.timer.isChecked());
        SettingsManager.setArrowNumbersEnabled(training.arrowNumbering);
        return training;
    }

    @NonNull
    private ArrayList<Round> createRoundsFromTemplate(StandardRound standardRound, Training training) {
        ArrayList<Round> rounds = new ArrayList<>();
        RoundDataSource roundDataSource = new RoundDataSource();
        for (RoundTemplate template : standardRound.rounds) {
            Round round = new Round();
            round.trainingId = training.getId();
            round.info = template;
            round.comment = "";
            roundDataSource.update(round);
            rounds.add(round);
        }
        return rounds;
    }

    @NonNull
    private StandardRound getCustomRound() {
        StandardRound standardRound; // Generate and save standard round template for practice
        standardRound = new StandardRound();
        standardRound.club = StandardRoundFactory.CUSTOM_PRACTICE;
        standardRound.name = getString(R.string.practice);
        standardRound.indoor = binding.indoor.isChecked();
        ArrayList<RoundTemplate> rounds = new ArrayList<>();
        rounds.add(getRoundTemplate());
        standardRound.rounds = rounds;

        SettingsManager.setIndoor(standardRound.indoor);
        return standardRound;
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
            final Target st = Parcels.unwrap(parcelable);
            StandardRound item = binding.standardRound.getSelectedItem();
            for (RoundTemplate template : item.rounds) {
                Dimension size = template.target.size;
                template.target = new Target(st.id, st.scoringStyle, size);
            }
            binding.standardRound.setItem(item);
        }
    }

    private void updateArrowsLabel() {
        binding.arrowsLabel.setText(getResources()
                .getQuantityString(R.plurals.arrow, binding.arrows.getProgress(),
                        binding.arrows.getProgress()));
    }

    private void loadRoundDefaultValues() {
        binding.distance.setItem(SettingsManager.getDistance());
        binding.arrows.setProgress(SettingsManager.getArrowsPerPasse());
        binding.target.setItem(SettingsManager.getTarget());
    }

    @NonNull
    private RoundTemplate getRoundTemplate() {
        RoundTemplate roundTemplate = new RoundTemplate();
        roundTemplate.target = binding.target.getSelectedItem();
        roundTemplate.targetTemplate = roundTemplate.target;
        roundTemplate.arrowsPerEnd = binding.arrows.getProgress();
        roundTemplate.endCount = 1;
        roundTemplate.distance = binding.distance.getSelectedItem();

        SettingsManager.setTarget(roundTemplate.target);
        SettingsManager.setDistance(roundTemplate.distance);
        SettingsManager.setArrowsPerEnd(roundTemplate.arrowsPerEnd);
        return roundTemplate;
    }
}
