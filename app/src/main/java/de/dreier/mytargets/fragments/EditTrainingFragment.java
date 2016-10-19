/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
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
import android.support.v4.app.Fragment;
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
import de.dreier.mytargets.shared.models.db.Arrow;
import de.dreier.mytargets.shared.models.db.Bow;
import de.dreier.mytargets.shared.models.db.Round;
import de.dreier.mytargets.shared.models.db.RoundTemplate;
import de.dreier.mytargets.shared.models.db.StandardRound;
import de.dreier.mytargets.shared.models.db.Training;
import de.dreier.mytargets.shared.targets.WA3Ring3Spot;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;
import de.dreier.mytargets.utils.IntentWrapper;
import de.dreier.mytargets.utils.ToolbarUtils;
import de.dreier.mytargets.utils.transitions.FabTransformUtil;
import de.dreier.mytargets.views.selector.SelectorBase;

import static de.dreier.mytargets.fragments.DatePickerFragment.ARG_CURRENT_DATE;
import static de.dreier.mytargets.fragments.ListFragmentBase.ITEM_ID;
import static de.dreier.mytargets.models.ETrainingType.TRAINING_WITH_STANDARD_ROUND;

public class EditTrainingFragment extends EditFragmentBase implements DatePickerDialog.OnDateSetListener {
    public static final String TRAINING_TYPE = "training_type";

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQ_SELECTED_DATE = 2;
    private static final int SR_TARGET_REQUEST_CODE = 11;

    private Long trainingId = null;
    private ETrainingType trainingType = ETrainingType.FREE_TRAINING;
    private LocalDate date = new LocalDate();
    private FragmentEditTrainingBinding binding;
    private Target roundTarget;

    @NonNull
    public static IntentWrapper createIntent(Fragment fragment, ETrainingType trainingType) {
        Intent i = new Intent(fragment.getContext(),
                SimpleFragmentActivityBase.EditTrainingActivity.class);
        i.putExtra(TRAINING_TYPE, trainingType);
        return new IntentWrapper(fragment, i);
    }

    @NonNull
    public static IntentWrapper editIntent(Fragment fragment, Training training) {
        Intent i = new Intent(fragment.getContext(),
                SimpleFragmentActivityBase.EditTrainingActivity.class);
        i.putExtra(ITEM_ID, training.getId());
        return new IntentWrapper(fragment, i);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_edit_training, container, false);

        Bundle arguments = getArguments();
        if (arguments != null) {
            trainingId = arguments.getLong(ITEM_ID, -1);
            trainingType = (ETrainingType) arguments.getSerializable(TRAINING_TYPE);
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
            binding.timer.setChecked(train.timePerPasse != -1);
        }
        binding.standardRound.setOnActivityResultContext(this);
        binding.standardRound.setOnUpdateListener(this::updateChangeTargetFaceVisibility);
        binding.changeTargetFace.setOnClickListener(v -> {
            TargetListFragment.getIntent(this, roundTarget)
                    .startForResult(SR_TARGET_REQUEST_CODE);
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
        if (trainingType == ETrainingType.FREE_TRAINING) {
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
        if (item == null || item.getArrowNumbers().isEmpty()) {
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

        if (trainingId == -1) {
            StandardRound standardRound;
            if (trainingType == ETrainingType.FREE_TRAINING) {
                standardRound = getCustomRound();
            } else {
                standardRound = binding.standardRound.getSelectedItem();
                SettingsManager.setStandardRound(standardRound.getId());
            }
            standardRound.save();
            training.standardRoundId = standardRound.getId();

            training.save();
            Round round = createRoundsFromTemplate(standardRound, training).get(0);
            round.setTarget(binding.target.getSelectedItem());

            TrainingFragment.getIntent(this, training).startWithoutAnimation();
            RoundFragment.getIntent(this, round).startWithoutAnimation();
            InputActivity.createIntent(this, round).start();
        } else {
            // Edit training
            training.update();
            getActivity().overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }
    }

    @NonNull
    private Training getTraining() {
        Training training;
        if (trainingId == -1) {
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
        training.timePerPasse = binding.timer.isChecked() ? SettingsManager
                .getTimerShootTime() : -1;
        Arrow selectedItem = binding.arrow.getSelectedItem();
        training.arrowNumbering = !(selectedItem == null || selectedItem.getArrowNumbers().isEmpty()) &&
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
        for (RoundTemplate template : standardRound.getRounds()) {
            Round round = new Round();
            round.trainingId = training.getId();
            round.info = template;
            round.comment = "";
            round.save();
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
        standardRound.setRounds(rounds);

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
        binding.arrows.setProgress(SettingsManager.getArrowsPerPasse());
        binding.target.setItem(SettingsManager.getTarget());
    }

    @NonNull
    private RoundTemplate getRoundTemplate() {
        RoundTemplate roundTemplate = new RoundTemplate();
        roundTemplate.setTargetTemplate(binding.target.getSelectedItem());
        roundTemplate.arrowsPerEnd = binding.arrows.getProgress();
        roundTemplate.endCount = 1;
        roundTemplate.distance = binding.distance.getSelectedItem();

        SettingsManager.setTarget(binding.target.getSelectedItem());
        SettingsManager.setDistance(roundTemplate.distance);
        SettingsManager.setArrowsPerEnd(roundTemplate.arrowsPerEnd);
        return roundTemplate;
    }
}
