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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.Bind;
import butterknife.OnClick;
import de.dreier.mytargets.R;
import de.dreier.mytargets.managers.dao.RoundDataSource;
import de.dreier.mytargets.managers.dao.StandardRoundDataSource;
import de.dreier.mytargets.managers.dao.TrainingDataSource;
import de.dreier.mytargets.shared.models.Arrow;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.models.Training;
import de.dreier.mytargets.shared.utils.StandardRoundFactory;
import de.dreier.mytargets.views.selector.ArrowSelector;
import de.dreier.mytargets.views.selector.BowSelector;
import de.dreier.mytargets.views.selector.EnvironmentSelector;
import de.dreier.mytargets.views.selector.StandardRoundSelector;

import static de.dreier.mytargets.fragments.DatePickerFragment.ARG_CURRENT_DATE;


public class EditTrainingFragment extends EditRoundPropertiesFragmentBase implements DatePickerDialog.OnDateSetListener {
    public static final String TRAINING_TYPE = "training_type";

    public static final int FREE_TRAINING = 0;
    public static final int TRAINING_WITH_STANDARD_ROUND = 1;
    private static final int COMPETITION = 2;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQ_SELECTED_DATE = 2;

    @Bind(R.id.practiceLayout)
    View practice;
    @Bind(R.id.indoor)
    RadioButton indoor;
    @Bind(R.id.outdoor)
    RadioButton outdoor;
    @Bind(R.id.bow)
    BowSelector bow;
    @Bind(R.id.arrow)
    ArrowSelector arrow;
    @Bind(R.id.training)
    EditText trainingTitle;
    @Bind(R.id.trainingDate)
    Button trainingDate;
    @Bind(R.id.environmentSpinner)
    EnvironmentSelector environment;
    @Bind(R.id.standardRound)
    StandardRoundSelector standardRoundSpinner;
    @Bind(R.id.numberArrows)
    CheckBox numberArrows;
    @Bind(R.id.timer)
    CheckBox timer;
    private int trainingType = 0;
    private Date date = new Date();

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_edit_training;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            trainingId = arguments.getLong(TRAINING_ID, -1);
            trainingType = arguments.getInt(TRAINING_TYPE, FREE_TRAINING);
        }

        arrow.setOnUpdateListener(this::updateArrowNumbers);

        if (trainingId == -1) {
            setTitle(R.string.new_training);
            trainingTitle.setText(getString(trainingType == COMPETITION ? R.string.competition : R.string.training));
            setTrainingDate();
            bow.setItemId(prefs.getInt("bow", -1));
            arrow.setItemId(prefs.getInt("arrow", -1));
            standardRoundSpinner.setItemId(prefs.getInt("standard_round", 32));
            numberArrows.setChecked(prefs.getBoolean("numbering", numberArrows.isChecked()));
            timer.setChecked(prefs.getBoolean("timer", false));
            indoor.setChecked(prefs.getBoolean("indoor", false));
            outdoor.setChecked(!prefs.getBoolean("indoor", false));
            environment.queryWeather(this, REQUEST_LOCATION_PERMISSION);
            loadRoundDefaultValues();
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
        standardRoundSpinner.setOnActivityResultContext(this);
        arrow.setOnActivityResultContext(this);
        bow.setOnActivityResultContext(this);
        environment.setOnActivityResultContext(this);
        applyTrainingType();
        updateArrowsLabel();
        updateArrowNumbers(arrow.getSelectedItem());
        return rootView;
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
        trainingDate.setText(SimpleDateFormat.getDateInstance().format(date));
    }

    @Override
    protected void onSave() {
        Training training = getTraining();

        getActivity().finish();

        TrainingDataSource trainingDataSource = new TrainingDataSource(getContext());
        if (trainingId == -1) {
            StandardRound standardRound;
            if (trainingType == 0) {
                standardRound = getCustomRound();
            } else {
                standardRound = standardRoundSpinner.getSelectedItem();
                prefs.edit().putInt("standard_round", (int) standardRound.getId()).apply();
            }
            new StandardRoundDataSource(getContext()).update(standardRound);
            training.standardRoundId = standardRound.getId();

            trainingDataSource.update(training);
            long roundId = createRoundsFromTemplate(standardRound, training).get(0).getId();

            openPasseForNewRound(training.getId(), roundId);
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
            training = new TrainingDataSource(getContext()).get(trainingId);
        }
        training.title = trainingTitle.getText().toString();
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

        prefs.edit().putInt("bow", (int) training.bow)
                .putInt("arrow", (int) training.arrow)
                .putBoolean("timer", timer.isChecked())
                .putBoolean("numbering", training.arrowNumbering)
                .apply();

        return training;
    }

    @NonNull
    private ArrayList<Round> createRoundsFromTemplate(StandardRound standardRound, Training training) {
        ArrayList<Round> rounds = new ArrayList<>();
        RoundDataSource roundDataSource = new RoundDataSource(getContext());
        for (RoundTemplate template : standardRound.getRounds()) {
            Round round = new Round();
            round.training = training.getId();
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
        standardRound.indoor = indoor.isChecked();
        ArrayList<RoundTemplate> rounds = new ArrayList<>();
        rounds.add(getRoundTemplate());
        standardRound.setRounds(rounds);

        prefs.edit().putBoolean("indoor", standardRound.indoor).apply();
        return standardRound;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        standardRoundSpinner.onActivityResult(requestCode, resultCode, data);
        arrow.onActivityResult(requestCode, resultCode, data);
        bow.onActivityResult(requestCode, resultCode, data);
        environment.onActivityResult(requestCode, resultCode, data);
    }
}
