/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
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
import android.widget.RadioButton;

import com.iangclifton.android.floatlabel.FloatLabel;

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
import de.dreier.mytargets.adapters.TargetItemAdapter;
import de.dreier.mytargets.managers.DatabaseManager;
import de.dreier.mytargets.models.Environment;
import de.dreier.mytargets.shared.models.Round;
import de.dreier.mytargets.shared.models.Training;
import de.dreier.mytargets.utils.MyBackupAgent;
import de.dreier.mytargets.views.DialogSpinner;
import de.dreier.mytargets.views.DistanceDialogSpinner;
import de.dreier.mytargets.views.NumberPicker;
import zh.wang.android.apis.yweathergetter4a.WeatherInfo;
import zh.wang.android.apis.yweathergetter4a.YahooWeather;
import zh.wang.android.apis.yweathergetter4a.YahooWeatherInfoListener;

public class EditRoundFragment extends Fragment implements DatePickerDialog.OnDateSetListener,
        YahooWeatherInfoListener {
    public static final String TRAINING_ID = "training_id";
    public static final String ROUND_ID = "round_id";
    public static final String EDIT_TRAINING = "edit_training";
    private static final int REQ_SELECTED_ARROW = 1;
    private static final int REQ_SELECTED_BOW = 2;
    private static final int REQ_SELECTED_TARGET = 3;
    private static final int REQ_SELECTED_DISTANCE = 4;
    private static final int REQ_SELECTED_ENVIRONMENT = 5;
    private static final int REQ_SELECTED_DATE = 6;

    private long mTraining = -1, mRound = -1;

    private DistanceDialogSpinner distance;
    private RadioButton indoor;
    private DialogSpinner bow;
    private DialogSpinner arrow;
    private DialogSpinner target;
    private int mBowId = 0;
    private EditText training;
    private FloatLabel comment;
    private NumberPicker rounds, arrows;
    private Button training_date;
    private Date date = new Date();
    private boolean editTraining;
    private DialogSpinner environment;

    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_edit_round, container, false);

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

        Bundle i = getArguments();
        if (i != null) {
            mTraining = i.getLong(TRAINING_ID, -1);
            mRound = i.getLong(ROUND_ID, -1);
            editTraining = i.getBoolean(EDIT_TRAINING, false);
        }
        SharedPreferences prefs = activity.getSharedPreferences(MyBackupAgent.PREFS, 0);

        training = (EditText) rootView.findViewById(R.id.training);
        training_date = (Button) rootView.findViewById(R.id.training_date);
        training_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Package bundle with fragment arguments
                Bundle bundle = new Bundle();
                bundle.putSerializable(DatePickerFragment.ARG_CURRENT_DATE, date);

                // Create and show date picker
                DatePickerFragment datePickerDialog = new DatePickerFragment();
                datePickerDialog.setTargetFragment(EditRoundFragment.this, REQ_SELECTED_DATE);
                datePickerDialog.setArguments(bundle);
                datePickerDialog.show(activity.getSupportFragmentManager(), "datepicker");
            }
        });

        View scrollView = rootView.findViewById(R.id.scrollView);

        // Distance
        distance = (DistanceDialogSpinner) rootView.findViewById(R.id.distance_spinner);
        distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, ItemSelectActivity.Distance.class);
                i.putExtra("title", R.string.distance);
                i.putExtra(DistanceFragment.CUR_DISTANCE, distance.getSelectedItemId());
                startActivityForResult(i, REQ_SELECTED_DISTANCE);
            }
        });

        // Indoor / outdoor
        RadioButton outdoor = (RadioButton) rootView.findViewById(R.id.outdoor);
        indoor = (RadioButton) rootView.findViewById(R.id.indoor);

        // Show scoreboard
        rounds = (NumberPicker) rootView.findViewById(R.id.rounds);
        rounds.setTextPattern(R.plurals.passe);

        // Points per passe
        arrows = (NumberPicker) rootView.findViewById(R.id.ppp);
        arrows.setTextPattern(R.plurals.arrow);
        arrows.setMinimum(1);
        arrows.setMaximum(10);

        // Bow
        bow = (DialogSpinner) rootView.findViewById(R.id.bow);
        bow.setAdapter(new BowItemAdapter(activity));
        Button addBow = (Button) rootView.findViewById(R.id.add_bow);
        bow.setAddButton(addBow, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, EditBowActivity.class));
            }
        });
        bow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity,
                        ItemSelectActivity.Bow.class);
                i.putExtra("title", R.string.bow);
                startActivityForResult(i, REQ_SELECTED_BOW);

            }
        });

        // Arrow
        arrow = (DialogSpinner) rootView.findViewById(R.id.arrow);
        arrow.setAdapter(new ArrowItemAdapter(activity));
        Button addArrow = (Button) rootView.findViewById(R.id.add_arrow);
        arrow.setAddButton(addArrow, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, EditArrowActivity.class));
            }
        });
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity,
                        ItemSelectActivity.Arrow.class);
                i.putExtra("title", R.string.bow);
                startActivityForResult(i, REQ_SELECTED_ARROW);
            }
        });

        // Target round
        target = (DialogSpinner) rootView.findViewById(R.id.target_spinner);
        target.setAdapter(new TargetItemAdapter(activity));
        target.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity,
                        ItemSelectActivity.Target.class);
                i.putExtra("title", R.string.target_round);
                startActivityForResult(i, REQ_SELECTED_TARGET);
            }
        });

        // Environment
        environment = (DialogSpinner) rootView.findViewById(R.id.environment_spinner);
        environment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity,
                        ItemSelectActivity.Environment.class);
                i.putExtra("title", R.string.environment);
                i.putExtra(EnvironmentFragment.ENVIRONMENT,
                        ((EnvironmentItemAdapter) environment.getAdapter()).getEnvironment());
                startActivityForResult(i, REQ_SELECTED_ENVIRONMENT);
            }
        });
        environment.setAdapter(new EnvironmentItemAdapter(activity));

        // Comment
        comment = (FloatLabel) rootView.findViewById(R.id.comment);

        if (mRound == -1) {
            // Initialise with default values
            int dist = prefs.getInt("distance", 10);
            String unit = prefs.getString("unit", "m");
            distance.setItemId(new de.dreier.mytargets.shared.models.Distance(dist, unit).getId());
            indoor.setChecked(prefs.getBoolean("indoor", false));
            outdoor.setChecked(!prefs.getBoolean("indoor", false));
            arrows.setValue(prefs.getInt("ppp", 3));
            rounds.setValue(prefs.getInt("rounds", 10));
            bow.setItemId(prefs.getInt("bow", 0));
            arrow.setItemId(prefs.getInt("arrow", 0));
            target.setItemId(prefs.getInt("target", 2));
            comment.setText("");
        } else {
            // Load saved values
            DatabaseManager db = DatabaseManager.getInstance(activity);
            Round r = db.getRound(mRound);
            distance.setItemId(r.distance.getId());
            indoor.setChecked(r.indoor);
            outdoor.setChecked(!r.indoor);
            arrows.setValue(r.ppp);
            bow.setItemId(r.bow);
            arrow.setItemId(r.arrow);
            target.setItemId(r.target);
            comment.setText(r.comment);

            View not_editable = rootView.findViewById(R.id.not_editable);
            not_editable.setVisibility(View.GONE);
        }

        if (mTraining == -1) {
            training.setText(getString(R.string.training));
            setTrainingDate();
            activity.getSupportActionBar().setTitle(R.string.new_training);
        } else if (editTraining) {
            DatabaseManager db = DatabaseManager.getInstance(activity);
            Training train = db.getTraining(mTraining);
            training.setText(train.title);
            date = train.date;
            setTrainingDate();
            activity.getSupportActionBar().setTitle(R.string.new_training);
            scrollView.setVisibility(View.GONE);
        } else {
            View training_container = rootView.findViewById(R.id.training_container);
            training_container.setVisibility(View.GONE);
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
            } else if (requestCode == REQ_SELECTED_TARGET) {
                target.setItemId(id);
                return;
            } else if (requestCode == REQ_SELECTED_DISTANCE) {
                distance.setItemId(id);
                return;
            } else if (requestCode == REQ_SELECTED_ENVIRONMENT) {
                Environment env = (Environment) data.getSerializableExtra("item");
                ((EnvironmentItemAdapter) environment.getAdapter()).setEnvironment(env);
                environment.setItemId(0);
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
            if (!editTraining) {
                if (mTraining == -1) {
                    onSaveTraining();
                }
                onSaveRound();
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
        training.id = mTraining;
        training.title = title;
        training.date = date;
        db.updateTraining(training);
        mTraining = training.id;
    }

    void onSaveRound() {
        Round round = new Round();
        round.target = (int) target.getSelectedItemId();

        if (bow.getAdapter().getCount() == 0 && mBowId == 0 && round.target == 3) {
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
                    .show();
            return;
        }

        DatabaseManager db = DatabaseManager.getInstance(getActivity());

        round.id = mRound;
        round.training = mTraining;
        round.bow = bow.getSelectedItemId();
        round.arrow = arrow.getSelectedItemId();
        if (round.bow == 0) {
            round.bow = mBowId;
        }

        round.distance = de.dreier.mytargets.shared.models.Distance
                .fromId(distance.getSelectedItemId());

        int after_rounds = rounds.getValue();
        round.ppp = arrows.getValue();
        round.indoor = indoor.isChecked();
        round.comment = comment.getTextString();
        db.updateRound(round);

        SharedPreferences prefs = getActivity().getSharedPreferences(MyBackupAgent.PREFS, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("bow", (int) bow.getSelectedItemId());
        editor.putInt("arrow", (int) arrow.getSelectedItemId());
        editor.putInt("distance", round.distance.distance);
        editor.putString("unit", round.distance.unit);
        editor.putInt("ppp", round.ppp);
        editor.putInt("rounds", after_rounds);
        editor.putInt("target", round.target);
        editor.putBoolean("indoor", round.indoor);
        editor.apply();

        if (mRound == -1) {
            Intent i = new Intent(getActivity(), SimpleFragmentActivity.TrainingActivity.class);
            i.putExtra(PasseFragment.TRAINING_ID, mTraining);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(i);

            i = new Intent(getActivity(), InputActivity.class);
            i.putExtra(InputActivity.ROUND_ID, round.id);
            i.putExtra(InputActivity.STOP_AFTER, after_rounds);
            startActivity(i);
        }
    }

    @Override
    public void gotWeatherInfo(WeatherInfo weatherInfo) {
        Environment e = new Environment();
        int code = weatherInfo.getCurrentCode();
        if (code == 8 || code == 9) {
            e.weather = Environment.WEATHER.LIGHT_RAIN;
        } else if (code < 19) {
            e.weather = Environment.WEATHER.RAIN;
        } else if (code < 27) {
            e.weather = Environment.WEATHER.CLOUDY;
        } else if (code < 31) {
            e.weather = Environment.WEATHER.PARTLY_CLOUDY;
        } else {
            e.weather = Environment.WEATHER.SUNNY;
        }
        e.windDirection = 0;
        e.location = weatherInfo.getWOEIDneighborhood();
        String speed = weatherInfo.getWindSpeed();
        String unit = weatherInfo.getSpeedUnit();
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
        ((EnvironmentItemAdapter) environment.getAdapter()).setEnvironment(e);
        environment.setItemId(0);
    }
}