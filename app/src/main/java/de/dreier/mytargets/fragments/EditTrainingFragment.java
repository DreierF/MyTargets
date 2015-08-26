/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.shared.models.Arrow;
import de.dreier.mytargets.shared.models.Diameter;
import de.dreier.mytargets.shared.models.Distance;
import de.dreier.mytargets.shared.models.EWeather;
import de.dreier.mytargets.shared.models.Environment;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.models.Training;
import de.dreier.mytargets.shared.models.target.Target;
import de.dreier.mytargets.shared.models.target.TargetFactory;
import de.dreier.mytargets.utils.MyBackupAgent;
import de.dreier.mytargets.views.selector.ArrowSelector;
import de.dreier.mytargets.views.selector.BowSelector;
import de.dreier.mytargets.views.selector.DistanceSelector;
import de.dreier.mytargets.views.selector.EnvironmentSelector;
import de.dreier.mytargets.views.NumberPicker;
import de.dreier.mytargets.views.selector.StandardRoundSelector;
import de.dreier.mytargets.views.selector.TargetSelector;
import zh.wang.android.apis.yweathergetter4a.WeatherInfo;
import zh.wang.android.apis.yweathergetter4a.YahooWeather;
import zh.wang.android.apis.yweathergetter4a.YahooWeatherExceptionListener;
import zh.wang.android.apis.yweathergetter4a.YahooWeatherInfoListener;


//TODO Refactor a EditFragment superclass
public class EditTrainingFragment extends Fragment implements DatePickerDialog.OnDateSetListener,
        YahooWeatherInfoListener, TabLayout.OnTabSelectedListener, YahooWeatherExceptionListener {
    public static final String TRAINING_ID = "training_id";
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQ_SELECTED_DATE = 2;

    private long mTraining = -1;

    private BowSelector bow;
    private ArrowSelector arrow;
    private EditText training;
    //private EditText comment;
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

    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_edit_training, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        setHasOptionsMenu(true);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mTraining = arguments.getLong(TRAINING_ID, -1);
        }
        SharedPreferences prefs = activity.getSharedPreferences(MyBackupAgent.PREFS, 0);

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

        // Comment
        //comment = (EditText) rootView.findViewById(R.id.comment);

        if (mTraining == -1) {
            training.setText(getString(R.string.training));
            setTrainingDate();
            activity.getSupportActionBar().setTitle(R.string.new_training);
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

            if (ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                        REQUEST_LOCATION_PERMISSION);
            } else {
                queryWeatherInfo();
            }

        } else {
            DatabaseManager db = DatabaseManager.getInstance(activity);
            Training train = db.getTraining(mTraining);
            training.setText(train.title);
            date = train.date;
            bow.setItemId(train.bow);
            arrow.setItemId(train.arrow);
            standardRoundSpinner.setItemId(train.standardRoundId);
            environment.setItem(train.environment);
            setTrainingDate();
            activity.getSupportActionBar().setTitle(R.string.new_training);
            not_editable.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
        }
        updateMode(tabLayout.getTabAt(0));
        updateArrowNumbers(arrow.getSelectedItem());
        return rootView;
    }

    private void queryWeatherInfo() {
        // Start getting weather for current location
        try {
            YahooWeather weather = new YahooWeather();
            weather.setExceptionListener(this);
            weather.queryYahooWeatherByGPS(getActivity(), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    queryWeatherInfo();
                } else {
                    setDefaultWeather();
                }
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.save, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            getActivity().finish();
            if (mTraining == -1) {
                onSaveTraining();
                Intent i = new Intent(getActivity(), SimpleFragmentActivity.TrainingActivity.class);
                i.putExtra(PasseFragment.TRAINING_ID, mTraining);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);

                i = new Intent(getActivity(), InputActivity.class);
                i.putExtra(InputActivity.TRAINING_ID, mTraining);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
            } else {
                onSaveTraining();
                getActivity().overridePendingTransition(R.anim.left_in, R.anim.right_out);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onSaveTraining() {
        DatabaseManager db = DatabaseManager.getInstance(getActivity());
        String title = training.getText().toString();
        Training training = new Training();
        training.setId(mTraining);
        training.title = title;
        training.date = date;
        //TODO move this logic incl. getting weather to {@link EnvironmentDialogSpinner}
        training.environment = environment.getSelectedItem();
        if (training.environment == null) {
            training.environment = new Environment(EWeather.SUNNY, 0, 0);
        }

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
                number_arrows.isChecked();

        SharedPreferences prefs = getActivity().getSharedPreferences(MyBackupAgent.PREFS, 0);
        SharedPreferences.Editor editor = prefs.edit();
        StandardRound standardRound;
        if (mTraining != -1) {
            Training train = db.getTraining(mTraining);
            training.standardRoundId = train.standardRoundId;
            db.update(training);
        } else {
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
                db.update(standardRound);

                editor.putInt("tab", tabLayout.getSelectedTabPosition());
                editor.putBoolean("indoor", standardRound.indoor);
                editor.putInt("ppp", round.arrowsPerPasse);
                editor.putInt("rounds", round.passes);
                editor.putInt("distance", round.distance.value);
                editor.putString("unit", round.distance.unit);
                editor.putInt("size_target", round.target.size.value);
                editor.putString("unit_target", round.target.size.unit);
            } else {
                standardRound = standardRoundSpinner.getSelectedItem();
                db.update(standardRound);
                editor.putInt("standard_round", (int) standardRound.getId());
            }
            training.standardRoundId = standardRound.getId();

            db.update(training);
            mTraining = training.getId();
            for (RoundTemplate template : standardRound.getRounds()) {
                Round round = new Round();
                round.training = mTraining;
                round.info = template;
                round.comment = "";
                db.update(round);
            }
        }

        editor.putInt("bow", (int) bow.getSelectedItem().getId());
        editor.putInt("arrow", (int) arrow.getSelectedItem().getId());
        editor.putBoolean("timer", timer.isChecked());
        editor.putBoolean("numbering", number_arrows.isChecked());
        editor.apply();
    }

    @Override
    public void gotWeatherInfo(WeatherInfo weatherInfo) {
        if (weatherInfo == null) {
            return;
        }
        Environment e = new Environment();
        int code = weatherInfo.getCurrentCode();
        if (code == 8 || code == 9) {
            e.weather = EWeather.LIGHT_RAIN;
        } else if (code < 19) {
            e.weather = EWeather.RAIN;
        } else if (code < 27) {
            e.weather = EWeather.CLOUDY;
        } else if (code < 31) {
            e.weather = EWeather.PARTLY_CLOUDY;
        } else {
            e.weather = EWeather.SUNNY;
        }
        e.windDirection = 0;
        e.location = weatherInfo.getLocationCity();
        String speed = weatherInfo.getWindSpeed();
        String unit = weatherInfo.getSpeedUnit();
        try {
            float sp = Float.parseFloat(speed);
            if (unit.equals("km/h") || unit.equals("kph")) {
                sp *= 0.621371192f;
            }
            if (sp < 1.2f) {
                e.windSpeed = 0;
            } else if (sp < 4.6) {
                e.windSpeed = 1;
            } else if (sp < 8.1) {
                e.windSpeed = 2;
            } else if (sp < 12.7) {
                e.windSpeed = 3;
            } else if (sp < 18.4) {
                e.windSpeed = 4;
            } else if (sp < 25.3) {
                e.windSpeed = 5;
            } else if (sp < 32.2) {
                e.windSpeed = 6;
            } else if (sp < 39.1) {
                e.windSpeed = 7;
            } else if (sp < 47.2) {
                e.windSpeed = 8;
            } else if (sp < 55.2) {
                e.windSpeed = 9;
            }
        } catch (NumberFormatException nfe) {
            e.windSpeed = 0;
        }
        environment.setItem(e);
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

    @Override
    public void onFailConnection(Exception e) {
        setDefaultWeather();
    }

    @Override
    public void onFailParsing(Exception e) {
        setDefaultWeather();
    }

    @Override
    public void onFailFindLocation(Exception e) {
        setDefaultWeather();
    }

    private void setDefaultWeather() {
        environment.setItem(new Environment(EWeather.SUNNY, 0, 0));
    }
}