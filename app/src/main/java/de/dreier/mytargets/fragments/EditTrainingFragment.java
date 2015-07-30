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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.EditArrowActivity;
import de.dreier.mytargets.activities.EditBowActivity;
import de.dreier.mytargets.activities.InputActivity;
import de.dreier.mytargets.activities.ItemSelectActivity;
import de.dreier.mytargets.activities.SimpleFragmentActivity;
import de.dreier.mytargets.adapters.ArrowItemAdapter;
import de.dreier.mytargets.adapters.BowItemAdapter;
import de.dreier.mytargets.adapters.EnvironmentItemAdapter;
import de.dreier.mytargets.adapters.StandardRoundsItemAdapter;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.shared.models.Diameter;
import de.dreier.mytargets.shared.models.EWeather;
import de.dreier.mytargets.shared.models.Environment;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.RoundTemplate;
import de.dreier.mytargets.shared.models.StandardRound;
import de.dreier.mytargets.shared.models.Training;
import de.dreier.mytargets.shared.models.target.Target;
import de.dreier.mytargets.shared.models.target.TargetFactory;
import de.dreier.mytargets.utils.MyBackupAgent;
import de.dreier.mytargets.views.DialogSpinner;
import zh.wang.android.apis.yweathergetter4a.WeatherInfo;
import zh.wang.android.apis.yweathergetter4a.YahooWeather;
import zh.wang.android.apis.yweathergetter4a.YahooWeatherInfoListener;

public class EditTrainingFragment extends Fragment implements DatePickerDialog.OnDateSetListener,
        YahooWeatherInfoListener {
    public static final String TRAINING_ID = "training_id";
    private static final int REQ_SELECTED_ARROW = 1;
    private static final int REQ_SELECTED_BOW = 2;
    private static final int REQ_SELECTED_ENVIRONMENT = 3;
    private static final int REQ_SELECTED_DATE = 4;
    private static final int REQ_SELECTED_STANDARD_ROUND = 5;
    private static final int REQ_SELECTED_SPECIFIC_TARGET = 6;

    private long mTraining = -1;

    private DialogSpinner bow;
    private DialogSpinner arrow;
    private final int mBowId = 0;
    private EditText training;
    private EditText comment;
    private Button training_date;
    private Date date = new Date();
    private DialogSpinner environment;
    private DialogSpinner standardRoundSpinner;

    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_edit_training, container, false);

        try {
            new YahooWeather().queryYahooWeatherByGPS(getActivity(), this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        setHasOptionsMenu(true);

        Bundle i = getArguments();
        if (i != null) {
            mTraining = i.getLong(TRAINING_ID, -1);
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

        View scrollView = rootView.findViewById(R.id.scrollView);

        // Format / Standard round
        standardRoundSpinner = (DialogSpinner) rootView.findViewById(R.id.standard_round);
        final StandardRoundsItemAdapter standardRoundsItemAdapter = new StandardRoundsItemAdapter(
                activity);
        standardRoundSpinner.setAdapter(standardRoundsItemAdapter);
        standardRoundSpinner.setOnClickListener(R.id.content, v -> {
            Intent i1 = new Intent(activity,
                    ItemSelectActivity.StandardRound.class);
            StandardRound standardRound = (StandardRound) standardRoundSpinner
                    .getSelectedItem();
            i1.putExtra("item", standardRound);
            startActivityForResult(i1, REQ_SELECTED_STANDARD_ROUND);
        });
        standardRoundSpinner.setOnClickListener(R.id.image, v -> {
            StandardRound standardRound = (StandardRound) standardRoundSpinner
                    .getSelectedItem();
            Target target = standardRound.getRounds().get(0).targetTemplate;
            if (target.id < 7 || target.id == 10 || target.id == 11) {
                Intent i1 = new Intent(activity,
                        ItemSelectActivity.Target.class);
                i1.putExtra("item", target);
                i1.putExtra(TargetFragment.TYPE_FIXED, true);
                startActivityForResult(i1, REQ_SELECTED_SPECIFIC_TARGET);
            } else {
                Intent i1 = new Intent(activity,
                        ItemSelectActivity.StandardRound.class);
                i1.putExtra("item", standardRound);
                startActivityForResult(i1, REQ_SELECTED_STANDARD_ROUND);
            }
        });

        // Bow
        bow = (DialogSpinner) rootView.findViewById(R.id.bow);
        bow.setAdapter(new BowItemAdapter(activity));
        Button addBow = (Button) rootView.findViewById(R.id.add_bow);
        bow.setAddButton(addBow, v -> startActivity(new Intent(activity, EditBowActivity.class)));
        bow.setOnClickListener(v -> {
            Intent i1 = new Intent(activity,
                    ItemSelectActivity.Bow.class);
            startActivityForResult(i1, REQ_SELECTED_BOW);

        });

        // Arrow
        arrow = (DialogSpinner) rootView.findViewById(R.id.arrow);
        arrow.setAdapter(new ArrowItemAdapter(activity));
        Button addArrow = (Button) rootView.findViewById(R.id.add_arrow);
        arrow.setAddButton(addArrow,
                v -> startActivity(new Intent(activity, EditArrowActivity.class)));
        arrow.setOnClickListener(v -> {
            Intent i1 = new Intent(activity,
                    ItemSelectActivity.Arrow.class);
            startActivityForResult(i1, REQ_SELECTED_ARROW);
        });

        // Environment
        environment = (DialogSpinner) rootView.findViewById(R.id.environment_spinner);
        environment.setItemId(0);
        environment.setOnClickListener(v -> {
            Intent i1 = new Intent(activity,
                    ItemSelectActivity.Environment.class);
            i1.putExtra(EnvironmentFragment.ENVIRONMENT,
                    ((EnvironmentItemAdapter) environment.getAdapter()).getEnvironment());
            startActivityForResult(i1, REQ_SELECTED_ENVIRONMENT);
        });
        environment.setAdapter(new EnvironmentItemAdapter(activity));

        // Comment
        comment = (EditText) rootView.findViewById(R.id.comment);

        if (mTraining == -1) {
            training.setText(getString(R.string.training));
            setTrainingDate();
            activity.getSupportActionBar().setTitle(R.string.new_training);
            bow.setItemId(prefs.getInt("bow", -1));
            arrow.setItemId(prefs.getInt("arrow", -1));
            standardRoundSpinner.setItemId(prefs.getInt("standard_round", 0));
        } else {
            DatabaseManager db = DatabaseManager.getInstance(activity);
            Training train = db.getTraining(mTraining);
            training.setText(train.title);
            date = train.date;
            bow.setItemId(train.bow);
            arrow.setItemId(train.arrow);
            standardRoundSpinner.setItemId(train.standardRoundId);
            ((EnvironmentItemAdapter) environment.getAdapter()).setEnvironment(train.environment);
            setTrainingDate();
            activity.getSupportActionBar().setTitle(R.string.new_training);
            scrollView.setVisibility(View.GONE);
        }
        return rootView;
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            long id = data.getLongExtra("id", 0);
            if (requestCode == REQ_SELECTED_ARROW) {
                arrow.setItemId(id);
                return;
            } else if (requestCode == REQ_SELECTED_BOW) {
                bow.setItemId(id);
                return;
            } else if (requestCode == REQ_SELECTED_ENVIRONMENT) {
                Environment env = (Environment) data.getSerializableExtra("item");
                ((EnvironmentItemAdapter) environment.getAdapter()).setEnvironment(env);
                return;
            } else if (requestCode == REQ_SELECTED_STANDARD_ROUND) {
                StandardRound sr = (StandardRound) data.getSerializableExtra("item");
                if (sr != null) {
                    ((StandardRoundsItemAdapter) standardRoundSpinner.getAdapter())
                            .setStandardRound(sr);
                    standardRoundSpinner.setItemId(0);
                } else {
                    ((StandardRoundsItemAdapter) standardRoundSpinner.getAdapter())
                            .setStandardRound(null);
                    standardRoundSpinner.setItemId(id);
                }
                return;
            } else if (requestCode == REQ_SELECTED_SPECIFIC_TARGET) {
                Target st = (Target) data.getSerializableExtra("item");
                StandardRound standardRound = (StandardRound) standardRoundSpinner
                        .getSelectedItem();
                for (RoundTemplate template : standardRound.getRounds()) {
                    Diameter size = template.target.size;
                    template.target = TargetFactory
                            .createTarget(getActivity(), st.getId(), st.scoringStyle);
                    template.target.size = size;
                }
                ((StandardRoundsItemAdapter) standardRoundSpinner.getAdapter())
                        .setStandardRound(standardRound);
                standardRoundSpinner.setItemId(0);
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        bow.setAdapter(new BowItemAdapter(getActivity()));
        arrow.setAdapter(new ArrowItemAdapter(getActivity()));
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

    void onSaveTraining() {
        DatabaseManager db = DatabaseManager.getInstance(getActivity());
        String title = training.getText().toString();
        Training training = new Training();
        training.setId(mTraining);
        training.title = title;
        training.date = date;
        training.environment = ((EnvironmentItemAdapter) environment.getAdapter()).getEnvironment();
        training.bow = bow.getSelectedItemId();
        training.arrow = arrow.getSelectedItemId();
        if (training.bow == 0) {
            training.bow = mBowId;
        }

        StandardRound standardRound = (StandardRound) standardRoundSpinner.getSelectedItem();
        db.update(standardRound);
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

        SharedPreferences prefs = getActivity().getSharedPreferences(MyBackupAgent.PREFS, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("standard_round", (int) standardRound.getId());
        editor.putInt("bow", (int) bow.getSelectedItemId());
        editor.putInt("arrow", (int) arrow.getSelectedItemId());
        editor.apply();
    }

        /*if (bow.getAdapter().getCount() == 0 && mBowId == 0 && round.info.target == 3) { TODO
            new AlertDialog.Builder(getActivity()).setTitle(R.string.title_compound)
                    .setMessage(R.string.msg_compound_type)
                    .setPositiveButton(R.string.compound_bow,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mBowId = -2;
                                    onSaveRound();
                                }
                            })
                    .setNegativeButton(R.string.other_bow,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mBowId = -1;
                                    onSaveRound();
                                }
                            }).setCancelable(false)
                    .show();*/


    @Override
    public void gotWeatherInfo(WeatherInfo weatherInfo) {
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
        e.location = weatherInfo.getWOEIDneighborhood();
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
        ((EnvironmentItemAdapter) environment.getAdapter()).setEnvironment(e);
        environment.setItemId(0);
    }
}