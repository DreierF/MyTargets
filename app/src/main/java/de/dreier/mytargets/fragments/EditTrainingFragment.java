/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.InputActivity;
import de.dreier.mytargets.activities.SimpleFragmentActivity;
import de.dreier.mytargets.managers.dao.RoundDataSource;
import de.dreier.mytargets.managers.dao.StandardRoundDataSource;
import de.dreier.mytargets.managers.dao.TrainingDataSource;
import de.dreier.mytargets.shared.models.Arrow;
import de.dreier.mytargets.shared.models.Diameter;
import de.dreier.mytargets.shared.models.Distance;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.models.Training;
import de.dreier.mytargets.shared.models.target.Target;
import de.dreier.mytargets.shared.models.target.TargetFactory;
import de.dreier.mytargets.views.selector.ArrowSelector;
import de.dreier.mytargets.views.selector.BowSelector;
import de.dreier.mytargets.views.selector.DistanceSelector;
import de.dreier.mytargets.views.selector.EnvironmentSelector;
import de.dreier.mytargets.views.selector.StandardRoundSelector;
import de.dreier.mytargets.views.selector.TargetSelector;

import static de.dreier.mytargets.fragments.DatePickerFragment.ARG_CURRENT_DATE;


public class EditTrainingFragment extends EditFragmentBase implements DatePickerDialog.OnDateSetListener {
    public static final String TRAINING_ID = "training_id";
    public static final String TRAINING_TYPE = "training_type";

    public static final int FREE_TRAINING = 0;
    public static final int TRAINING_WITH_STANDARD_ROUND = 1;
    public static final int COMPETITION = 2;

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQ_SELECTED_DATE = 2;

    private long trainingId = -1;
    private int trainingType = 0;
    private Date date = new Date();

    @Bind(R.id.bow)
    BowSelector bow;

    @Bind(R.id.arrow)
    ArrowSelector arrow;

    @Bind(R.id.training)
    EditText trainingTitle;

    @Bind(R.id.trainingDate)
    Button training_date;

    @Bind(R.id.environmentSpinner)
    EnvironmentSelector environment;

    @Bind(R.id.standardRound)
    StandardRoundSelector standardRoundSpinner;

    @Bind(R.id.numberArrows)
    CheckBox numberArrows;

    @Bind(R.id.timer)
    CheckBox timer;

    @Bind(R.id.practiceLayout)
    View practice;

    @Bind(R.id.indoor)
    RadioButton indoor;

    @Bind(R.id.outdoor)
    RadioButton outdoor;

    @Bind(R.id.targetSpinner)
    TargetSelector targetSpinner;

    @Bind(R.id.distanceSpinner)
    DistanceSelector distanceSpinner;

    @Bind(R.id.arrows)
    DiscreteSeekBar arrows;

    @Bind(R.id.notEditable)
    View notEditable;

    @Bind(R.id.arrowsLabel)
    TextView arrowsLabel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_training, container, false);
        ButterKnife.bind(this, rootView);

        setUpToolbar(rootView);

        Bundle arguments = getArguments();
        if (arguments != null) {
            trainingId = arguments.getLong(TRAINING_ID, -1);
            trainingType = arguments.getInt(TRAINING_TYPE, FREE_TRAINING);
        }

        arrow.setOnUpdateListener(this::updateArrowNumbers);
        arrows.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                updateArrowsLabel();
            }
        });

        if (trainingId == -1) {
            setTitle(R.string.new_training);
            trainingTitle.setText(getString(trainingType == COMPETITION ? R.string.training : R.string.competition));
            setTrainingDate();
            bow.setItemId(prefs.getInt("bow", -1));
            arrow.setItemId(prefs.getInt("arrow", -1));
            standardRoundSpinner.setItemId(prefs.getInt("standard_round", 32));
            numberArrows.setChecked(prefs.getBoolean("numbering", numberArrows.isChecked()));
            timer.setChecked(prefs.getBoolean("timer", false));
            int distance = prefs.getInt("distance", 10);
            String unit = prefs.getString("unit", "m");
            distanceSpinner.setItem(new Distance(distance, unit));
            indoor.setChecked(prefs.getBoolean("indoor", false));
            outdoor.setChecked(!prefs.getBoolean("indoor", false));
            arrows.setProgress(prefs.getInt("ppp", 3));
            Target target = TargetFactory.createTarget(activity, prefs.getInt("target", 0),
                    prefs.getInt("scoring_style", 0));
            target.size = new Diameter(prefs.getInt("size_target", 60),
                    prefs.getString("unit_target", Diameter.CENTIMETER));
            targetSpinner.setItem(target);
            environment.queryWeather(this, REQUEST_LOCATION_PERMISSION);
        } else {
            setTitle(R.string.edit_training);
            Training train = new TrainingDataSource(getContext()).get(trainingId);
            trainingTitle.setText(train.title);
            date = train.date;
            bow.setItemId(train.bow);
            arrow.setItemId(train.arrow);
            standardRoundSpinner.setItemId(train.standardRoundId);
            environment.setItem(train.environment);
            setTrainingDate();
            notEditable.setVisibility(View.GONE);
        }
        applyTrainingType();
        updateArrowsLabel();
        updateArrowNumbers(arrow.getSelectedItem());
        return rootView;
    }

    private void updateArrowsLabel() {
        arrowsLabel.setText(getResources().getQuantityString(R.plurals.arrow, arrows.getProgress(), arrows.getProgress()));
    }

    private void applyTrainingType() {
        View in, out;
        if (trainingType == 0) {
            in = practice;
            out = standardRoundSpinner;
        } else {
            out = practice;
            in = standardRoundSpinner;
        }
        in.setVisibility(View.VISIBLE);
        out.setVisibility(View.GONE);
    }

    @OnClick(R.id.trainingDate)
    void onDateClick() {
        // Package bundle with fragment arguments
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_CURRENT_DATE, date);

        // Create and show date picker
        DatePickerFragment datePickerDialog = new DatePickerFragment();
        datePickerDialog.setTargetFragment(EditTrainingFragment.this, REQ_SELECTED_DATE);
        datePickerDialog.setArguments(bundle);
        datePickerDialog.show(activity.getSupportFragmentManager(), "date_picker");
    }

    private void updateArrowNumbers(Arrow item) {
        if (item == null || item.numbers.isEmpty()) {
            numberArrows.setVisibility(View.GONE);
        } else {
            numberArrows.setVisibility(View.VISIBLE);
            numberArrows.setChecked(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                environment.onPermissionResult(activity, grantResults);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        date = new Date(new GregorianCalendar(year, monthOfYear, dayOfMonth).getTimeInMillis());
        setTrainingDate();
    }

    private void setTrainingDate() {
        training_date.setText(SimpleDateFormat.getDateInstance().format(date));
    }

    @Override
    protected void onSave() {
        boolean newTraining = trainingId == -1;
        String title = trainingTitle.getText().toString();
        Training training = new Training();
        training.setId(trainingId);
        training.title = title;
        training.date = date;
        training.environment = environment.getSelectedItem();
        training.bow = bow.getSelectedItem() == null ? 0 : bow.getSelectedItem().getId();
        training.arrow = arrow.getSelectedItem() == null ? 0 : arrow.getSelectedItem().getId();
        int time = 120;
        try {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());
            time = Integer.parseInt(prefs.getString("timer_shoot_time", "120"));
        } catch (NumberFormatException ignored) {
        }
        training.timePerPasse = timer.isChecked() ? time : -1;
        Arrow selectedItem = arrow.getSelectedItem();
        training.arrowNumbering = !(selectedItem == null || selectedItem.numbers.isEmpty()) &&
                numberArrows.isChecked();

        getActivity().finish();
        SharedPreferences.Editor editor = prefs.edit();
        StandardRound standardRound;
        TrainingDataSource trainingDataSource = new TrainingDataSource(getContext());
        StandardRoundDataSource standardRoundDataSource = new StandardRoundDataSource(getContext());
        if (newTraining) {
            getActivity().setTitle(R.string.edit_training);
            if (trainingType == 0) {
                // Generate and save standard round template for practice
                standardRound = new StandardRound();
                standardRound.club = StandardRound.CUSTOM_PRACTICE;
                standardRound.name = getString(R.string.practice);
                standardRound.indoor = indoor.isChecked();
                ArrayList<RoundTemplate> rounds = new ArrayList<>();
                RoundTemplate round = new RoundTemplate();
                round.target = targetSpinner.getSelectedItem();
                round.targetTemplate = round.target;
                round.arrowsPerPasse = arrows.getProgress();
                round.passes = 1;
                round.distance = distanceSpinner.getSelectedItem();
                rounds.add(round);
                standardRound.setRounds(rounds);
                standardRoundDataSource.update(standardRound);

                editor.putBoolean("indoor", standardRound.indoor);
                editor.putInt("ppp", round.arrowsPerPasse);
                editor.putInt("rounds", round.passes);
                editor.putInt("distance", round.distance.value);
                editor.putString("unit", round.distance.unit);
                editor.putInt("target", (int) round.target.getId());
                editor.putInt("scoring_style", round.target.scoringStyle);
                editor.putInt("size_target", round.target.size.value);
                editor.putString("unit_target", round.target.size.unit);
            } else {
                standardRound = standardRoundSpinner.getSelectedItem();
                standardRoundDataSource.update(standardRound);
                editor.putInt("standard_round", (int) standardRound.getId());
            }
            training.standardRoundId = standardRound.getId();

            trainingDataSource.update(training);
            trainingId = training.getId();
            ArrayList<Round> rounds = new ArrayList<>();
            RoundDataSource roundDataSource = new RoundDataSource(getContext());
            for (RoundTemplate template : standardRound.getRounds()) {
                Round round = new Round();
                round.training = trainingId;
                round.info = template;
                round.comment = "";
                roundDataSource.update(round);
                rounds.add(round);
            }
            Intent i = new Intent(getActivity(), SimpleFragmentActivity.TrainingActivity.class);
            i.putExtra(TrainingFragment.ITEM_ID, trainingId);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);

            i = new Intent(getActivity(), InputActivity.class);
            i.putExtra(InputActivity.ROUND_ID, rounds.get(0).getId());
            i.putExtra(InputActivity.PASSE_IND, 0);
            startActivity(i);
            getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
        } else {
            // Edit training
            Training train = trainingDataSource.get(trainingId);
            training.standardRoundId = train.standardRoundId;
            trainingDataSource.update(training);
            getActivity().overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }

        editor.putInt("bow", (int) training.bow);
        editor.putInt("arrow", (int) training.arrow);
        editor.putBoolean("timer", timer.isChecked());
        editor.putBoolean("numbering", training.arrowNumbering);
        editor.apply();
    }
}