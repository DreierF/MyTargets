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
import android.support.design.widget.TabLayout;
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
import de.dreier.mytargets.views.NumberPicker;
import de.dreier.mytargets.views.selector.ArrowSelector;
import de.dreier.mytargets.views.selector.BowSelector;
import de.dreier.mytargets.views.selector.DistanceSelector;
import de.dreier.mytargets.views.selector.EnvironmentSelector;
import de.dreier.mytargets.views.selector.StandardRoundSelector;
import de.dreier.mytargets.views.selector.TargetSelector;


public class EditTrainingFragment extends EditFragmentBase implements DatePickerDialog.OnDateSetListener,
        TabLayout.OnTabSelectedListener {
    public static final String TRAINING_ID = "training_id";

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQ_SELECTED_DATE = 2;

    private long mTraining = -1;

    private BowSelector bow;
    private ArrowSelector arrow;
    private EditText training;
    private Button training_date;
    private Date date = new Date();
    private EnvironmentSelector environment;
    private StandardRoundSelector standardRoundSpinner;
    private CheckBox number_arrows;
    private CheckBox timer;
    private View practice, roundLayout;
    private RadioButton indoor;
    private TabLayout tabLayout;
    private TargetSelector targetSpinner;
    private DistanceSelector distanceSpinner;
    private NumberPicker passes, arrows;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_training, container, false);

        setUpToolbar(rootView);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mTraining = arguments.getLong(TRAINING_ID, -1);
        }

        training = (EditText) rootView.findViewById(R.id.training);
        training_date = (Button) rootView.findViewById(R.id.training_date);
        training_date.setOnClickListener(v -> {
            // Package bundle with fragment arguments
            Bundle bundle = new Bundle();
            bundle.putSerializable(DatePickerFragment.ARG_CURRENT_DATE, date);

            // Create and show date picker
            DatePickerFragment datePickerDialog = new DatePickerFragment();
            datePickerDialog.setTargetFragment(EditTrainingFragment.this, REQ_SELECTED_DATE);
            datePickerDialog.setArguments(bundle);
            datePickerDialog.show(activity.getSupportFragmentManager(), "date_picker");
        });

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.practice));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.standard_round));
        tabLayout.setOnTabSelectedListener(this);

        practice = rootView.findViewById(R.id.practice_layout);
        roundLayout = rootView.findViewById(R.id.standard_round_layout);

        View not_editable = rootView.findViewById(R.id.not_editable);

        standardRoundSpinner = (StandardRoundSelector) rootView
                .findViewById(R.id.standard_round);
        distanceSpinner = (DistanceSelector) rootView.findViewById(R.id.distance_spinner);
        targetSpinner = (TargetSelector) rootView.findViewById(R.id.target_spinner);
        bow = (BowSelector) rootView.findViewById(R.id.bow);
        arrow = (ArrowSelector) rootView.findViewById(R.id.arrow);
        environment = (EnvironmentSelector) rootView.findViewById(R.id.environment_spinner);

        arrow.setOnUpdateListener(this::updateArrowNumbers);
        number_arrows = (CheckBox) rootView.findViewById(R.id.number_arrows);

        // Indoor / outdoor
        RadioButton outdoor = (RadioButton) rootView.findViewById(R.id.outdoor);
        indoor = (RadioButton) rootView.findViewById(R.id.indoor);

        // Passes
        timer = (CheckBox) rootView.findViewById(R.id.timer);
        passes = (NumberPicker) rootView.findViewById(R.id.passes);
        passes.setTextPattern(R.plurals.passe);

        // Arrows per passe
        arrows = (NumberPicker) rootView.findViewById(R.id.ppp);
        arrows.setTextPattern(R.plurals.arrow);
        arrows.setMinimum(1);
        arrows.setMaximum(10);

        if (mTraining == -1) {
            setTitle(R.string.new_training);
            training.setText(getString(R.string.training));
            setTrainingDate();
            tabLayout.getTabAt(prefs.getInt("tab", 0)).select();
            bow.setItemId(prefs.getInt("bow", -1));
            arrow.setItemId(prefs.getInt("arrow", -1));
            standardRoundSpinner.setItemId(prefs.getInt("standard_round", 32));
            number_arrows.setChecked(prefs.getBoolean("numbering", number_arrows.isChecked()));
            timer.setChecked(prefs.getBoolean("timer", false));
            int distance = prefs.getInt("distance", 10);
            String unit = prefs.getString("unit", "m");
            distanceSpinner.setItem(new Distance(distance, unit));
            indoor.setChecked(prefs.getBoolean("indoor", false));
            outdoor.setChecked(!prefs.getBoolean("indoor", false));
            arrows.setValue(prefs.getInt("ppp", 3));
            passes.setValue(prefs.getInt("rounds", 10));
            Target target = TargetFactory.createTarget(activity, prefs.getInt("target", 0),
                    prefs.getInt("scoring_style", 0));
            target.size = new Diameter(prefs.getInt("size_target", 60),
                    prefs.getString("unit_target", Diameter.CENTIMETER));
            targetSpinner.setItem(target);
            environment.queryWeather(this, REQUEST_LOCATION_PERMISSION);
        } else {
            setTitle(R.string.edit_training);
            Training train = new TrainingDataSource(getContext()).get(mTraining);
            training.setText(train.title);
            date = train.date;
            bow.setItemId(train.bow);
            arrow.setItemId(train.arrow);
            standardRoundSpinner.setItemId(train.standardRoundId);
            environment.setItem(train.environment);
            setTrainingDate();
            not_editable.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
        }
        updateMode(tabLayout.getTabAt(0));
        updateArrowNumbers(arrow.getSelectedItem());
        return rootView;
    }

    private void updateArrowNumbers(Arrow item) {
        if (item == null || item.numbers.isEmpty()) {
            number_arrows.setVisibility(View.GONE);
        } else {
            number_arrows.setVisibility(View.VISIBLE);
            number_arrows.setChecked(true);
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
        boolean newTraining = mTraining == -1;
        String title = training.getText().toString();
        Training training1 = new Training();
        training1.setId(mTraining);
        training1.title = title;
        training1.date = date;
        training1.environment = environment.getSelectedItem();
        training1.bow = bow.getSelectedItem() == null ? 0 : bow.getSelectedItem().getId();
        training1.arrow = arrow.getSelectedItem() == null ? 0 : arrow.getSelectedItem().getId();
        int time = 120;
        try {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());
            time = Integer.parseInt(prefs.getString("timer_shoot_time", "120"));
        } catch (NumberFormatException ignored) {
        }
        training1.timePerPasse = timer.isChecked() ? time : -1;
        Arrow selectedItem = arrow.getSelectedItem();
        training1.arrowNumbering = !(selectedItem == null || selectedItem.numbers.isEmpty()) &&
                number_arrows.isChecked();

        getActivity().finish();
        SharedPreferences.Editor editor = prefs.edit();
        StandardRound standardRound;
        TrainingDataSource trainingDataSource = new TrainingDataSource(getContext());
        StandardRoundDataSource standardRoundDataSource = new StandardRoundDataSource(getContext());
        if (newTraining) {
            getActivity().setTitle(R.string.edit_training);
            if (tabLayout.getSelectedTabPosition() == 0) {
                // Generate and save standard round template for practice
                standardRound = new StandardRound();
                standardRound.club = StandardRound.CUSTOM_PRACTICE;
                standardRound.name = getString(R.string.practice);
                standardRound.indoor = indoor.isChecked();
                ArrayList<RoundTemplate> rounds = new ArrayList<>();
                RoundTemplate round = new RoundTemplate();
                round.target = targetSpinner.getSelectedItem();
                round.targetTemplate = round.target;
                round.arrowsPerPasse = arrows.getValue();
                round.passes = passes.getValue();
                round.distance = distanceSpinner.getSelectedItem();
                rounds.add(round);
                standardRound.setRounds(rounds);
                standardRoundDataSource.update(standardRound);

                editor.putInt("tab", tabLayout.getSelectedTabPosition());
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
            training1.standardRoundId = standardRound.getId();

            trainingDataSource.update(training1);
            mTraining = training1.getId();
            ArrayList<Round> rounds = new ArrayList<>();
            RoundDataSource roundDataSource = new RoundDataSource(getContext());
            for (RoundTemplate template : standardRound.getRounds()) {
                Round round = new Round();
                round.training = mTraining;
                round.info = template;
                round.comment = "";
                roundDataSource.update(round);
                rounds.add(round);
            }
            Intent i = new Intent(getActivity(), SimpleFragmentActivity.TrainingActivity.class);
            i.putExtra(TrainingFragment.ITEM_ID, mTraining);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);

            i = new Intent(getActivity(), InputActivity.class);
            i.putExtra(InputActivity.ROUND_ID, rounds.get(0).getId());
            i.putExtra(InputActivity.PASSE_IND, 0);
            startActivity(i);
            getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
        } else {
            // Edit training
            Training train = trainingDataSource.get(mTraining);
            training1.standardRoundId = train.standardRoundId;
            trainingDataSource.update(training1);
            getActivity().overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }

        editor.putInt("bow", (int) training1.bow);
        editor.putInt("arrow", (int) training1.arrow);
        editor.putBoolean("timer", timer.isChecked());
        editor.putBoolean("numbering", training1.arrowNumbering);
        editor.apply();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        updateMode(tab);
    }

    private void updateMode(TabLayout.Tab tab) {
        View in, out;
        if (tab.getPosition() == 0) {
            in = practice;
            out = roundLayout;
        } else {
            out = practice;
            in = roundLayout;
        }
        in.setVisibility(View.VISIBLE);
        out.setVisibility(View.GONE);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}