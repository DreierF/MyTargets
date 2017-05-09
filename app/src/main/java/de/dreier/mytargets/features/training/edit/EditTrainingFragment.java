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
package de.dreier.mytargets.features.training.edit;

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

import com.annimon.stream.Stream;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.parceler.Parcels;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import java.util.ArrayList;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.activities.ItemSelectActivity;
import de.dreier.mytargets.base.fragments.EditFragmentBase;
import de.dreier.mytargets.databinding.FragmentEditTrainingBinding;
import de.dreier.mytargets.features.settings.SettingsManager;
import de.dreier.mytargets.features.training.ETrainingType;
import de.dreier.mytargets.features.training.RoundFragment;
import de.dreier.mytargets.features.training.TrainingFragment;
import de.dreier.mytargets.features.training.input.InputActivity;
import de.dreier.mytargets.features.training.target.TargetListFragment;
import de.dreier.mytargets.shared.models.EBowType;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.db.Bow;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.StandardRound;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.shared.targets.models.WA3Ring3Spot;
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.transitions.FabTransformUtil;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static de.dreier.mytargets.base.fragments.EditableListFragmentBase.ITEM_ID;
import static de.dreier.mytargets.features.training.ETrainingType.FREE_TRAINING;
import static de.dreier.mytargets.features.training.ETrainingType.TRAINING_WITH_STANDARD_ROUND;

public class EditTrainingFragment extends EditFragmentBase implements DatePickerDialog.OnDateSetListener {
    public static final String CREATE_FREE_TRAINING_ACTION = "free_training";
    public static final String CREATE_TRAINING_WITH_STANDARD_ROUND_ACTION = "with_standard_round";

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQ_SELECTED_DATE = 2;
    private static final int SR_TARGET_REQUEST_CODE = 11;

    private Long trainingId = null;
    private ETrainingType trainingType = FREE_TRAINING;
    private LocalDate date = LocalDate.now();
    private FragmentEditTrainingBinding binding;
    private Target roundTarget;

    @NonNull
    public static IntentWrapper createIntent(String trainingTypeAction) {
        return new IntentWrapper(EditTrainingActivity.class)
                .action(trainingTypeAction);
    }

    @NonNull
    public static IntentWrapper editIntent(Training training) {
        return new IntentWrapper(EditTrainingActivity.class)
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
        binding.standardRound.setOnActivityResultContext(this);
        binding.standardRound.setOnUpdateListener(
                item -> roundTarget = item.getRounds().get(0).getTargetTemplate());
        binding.changeTargetFace.setOnClickListener(v ->
                TargetListFragment.getIntent(roundTarget)
                        .withContext(this)
                        .forResult(SR_TARGET_REQUEST_CODE)
                        .start());
        binding.arrow.setOnActivityResultContext(this);
        binding.bow.setOnActivityResultContext(this);
        binding.bow.setOnUpdateListener(this::setScoringStyleForCompoundBow);
        binding.environment.setOnActivityResultContext(this);
        binding.trainingDate.setOnClickListener(view -> onDateClick());

        if (trainingId == null) {
            ToolbarUtils.setTitle(this, R.string.new_training);
            binding.training.setText(getString(
                    trainingType == ETrainingType.COMPETITION ? R.string.competition : R.string.training));
            setTrainingDate();
            loadRoundDefaultValues();
            binding.bow.setItemId(SettingsManager.getBow());
            binding.arrow.setItemId(SettingsManager.getArrow());
            binding.standardRound.setItemId(SettingsManager.getStandardRound());
            binding.numberArrows.setChecked(SettingsManager.getArrowNumbersEnabled());
            if(savedInstanceState == null) {
                binding.environment.queryWeather(this, REQUEST_LOCATION_PERMISSION);
            }
            binding.changeTargetFace.setVisibility(trainingType == TRAINING_WITH_STANDARD_ROUND
                    ? VISIBLE : GONE);
        } else {
            ToolbarUtils.setTitle(this, R.string.edit_training);
            Training train = Training.get(trainingId);
            binding.training.setText(train.title);
            date = train.date;
            binding.bow.setItemId(train.bowId);
            binding.arrow.setItemId(train.arrowId);
            binding.environment.setItem(train.getEnvironment());
            setTrainingDate();
            binding.notEditable.setVisibility(GONE);
            binding.changeTargetFace.setVisibility(train.standardRoundId != null ? VISIBLE : GONE);
        }
        applyTrainingType();
        updateArrowsLabel();

        return binding.getRoot();
    }

    private void updateArrowsLabel() {
        binding.arrowsLabel.setText(getResources()
                .getQuantityString(R.plurals.arrow, binding.arrows.getProgress(),
                        binding.arrows.getProgress()));
    }

    protected void setScoringStyleForCompoundBow(Bow bow) {
        final Target target = binding.target.getSelectedItem();
        if (bow != null && target != null && target.id <= WA3Ring3Spot.ID) {
            if (bow.type == EBowType.COMPOUND_BOW && target.scoringStyle == 0) {
                target.scoringStyle = 2;
                binding.target.setItem(target);
            } else if (bow.type != EBowType.COMPOUND_BOW && target.scoringStyle == 2) {
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
        in.setVisibility(VISIBLE);
        out.setVisibility(GONE);
    }

    private void onDateClick() {
        DatePickerFragment datePickerDialog = DatePickerFragment.newInstance(date);
        datePickerDialog.setTargetFragment(this, REQ_SELECTED_DATE);
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
        date = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
        setTrainingDate();
    }

    private void setTrainingDate() {
        binding.trainingDate.setText(date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
    }

    @Override
    protected void onSave() {
        Training training = getTraining();
        finish();

        if (trainingId == null) {
            if (trainingType == FREE_TRAINING) {
                training.standardRoundId = null;
                training.rounds = new ArrayList<>();
                training.rounds.add(getRound());
            } else {
                StandardRound standardRound = binding.standardRound.getSelectedItem();
                SettingsManager.setStandardRound(standardRound.getId());
                if (standardRound.getId() == null) {
                    standardRound.save();
                }
                training.standardRoundId = standardRound.getId();
                training.initRoundsFromTemplate(standardRound);
                for (Round round : training.getRounds()) {
                    round.setTarget(roundTarget);
                }
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
        training.bowId = binding.bow.getSelectedItem() == null ? null : binding.bow
                .getSelectedItem()
                .getId();
        training.arrowId = binding.arrow.getSelectedItem() == null ? null : binding.arrow
                .getSelectedItem().getId();
        training.arrowNumbering = binding.numberArrows.isChecked();

        SettingsManager.setBow(training.bowId);
        SettingsManager.setArrow(training.arrowId);
        SettingsManager.setArrowNumbersEnabled(training.arrowNumbering);
        SettingsManager.setIndoor(training.indoor);
        return training;
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
            Target target = Parcels.unwrap(parcelable);
            final StandardRound item = binding.standardRound.getSelectedItem();
            Stream.of(item.getRounds())
                    .forEach(r -> r.setTargetTemplate(target));
            binding.standardRound.setItem(item);
        }
    }

    private void loadRoundDefaultValues() {
        binding.distance.setItem(SettingsManager.getDistance());
        binding.arrows.setProgress(SettingsManager.getShotsPerEnd());
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
