/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import junit.framework.Assert;

import de.dreier.mytargets.R;
import de.dreier.mytargets.shared.models.EWeather;
import de.dreier.mytargets.shared.models.Environment;
import de.dreier.mytargets.utils.MyBackupAgent;
import de.dreier.mytargets.views.WindDirectionDialogSpinner;
import de.dreier.mytargets.views.WindSpeedDialogSpinner;

public class EnvironmentFragment extends Fragment {
    public static final String ENVIRONMENT = "environment";

    private WindSpeedDialogSpinner wind_speed;
    private WindDirectionDialogSpinner wind_direction;
    private NowListFragment.OnItemSelectedListener listener;
    private Environment mEnvironment;
    private EWeather weather;
    private ImageButton sunny, partly_cloudy, cloudy, light_rain, rain;
    private EditText location;

    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_environment, container, false);

        Bundle i = getArguments();
        if (i != null) {
            mEnvironment = (Environment) i.getSerializable(ENVIRONMENT);
        }
        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        SharedPreferences prefs = activity.getSharedPreferences(MyBackupAgent.PREFS, 0);

        // Weather
        sunny = (ImageButton) rootView.findViewById(R.id.sunny);
        partly_cloudy = (ImageButton) rootView.findViewById(R.id.partly_cloudy);
        cloudy = (ImageButton) rootView.findViewById(R.id.clouds);
        light_rain = (ImageButton) rootView.findViewById(R.id.light_rain);
        rain = (ImageButton) rootView.findViewById(R.id.rain);
        setOnClickWeather(sunny, EWeather.SUNNY);
        setOnClickWeather(partly_cloudy, EWeather.PARTLY_CLOUDY);
        setOnClickWeather(cloudy, EWeather.CLOUDY);
        setOnClickWeather(light_rain, EWeather.LIGHT_RAIN);
        setOnClickWeather(rain, EWeather.RAIN);

        wind_speed = (WindSpeedDialogSpinner) rootView.findViewById(R.id.wind_speed);
        wind_direction = (WindDirectionDialogSpinner) rootView.findViewById(R.id.wind_direction);
        location = (EditText) rootView.findViewById(R.id.location);

        setWeather(mEnvironment.weather);
        wind_speed.setItemId(mEnvironment.windSpeed);
        wind_direction.setItemId(mEnvironment.windDirection);
        location.setText(mEnvironment.location);
        setHasOptionsMenu(true);
        return rootView;
    }

    private void setOnClickWeather(ImageButton b, final EWeather w) {
        b.setOnClickListener(v -> setWeather(w));
    }

    private void setWeather(EWeather weather) {
        this.weather = weather;
        sunny.setImageResource(weather == EWeather.SUNNY ? R.drawable.ic_sun :
                R.drawable.ic_sun_outline);
        partly_cloudy.setImageResource(
                weather == EWeather.PARTLY_CLOUDY ? R.drawable.ic_partly_cloudy :
                        R.drawable.ic_partly_cloudy_outline);
        cloudy.setImageResource(weather == EWeather.CLOUDY ? R.drawable.ic_cloudy :
                R.drawable.ic_cloudy_outline);
        light_rain.setImageResource(
                weather == EWeather.LIGHT_RAIN ? R.drawable.ic_light_rain :
                        R.drawable.ic_light_rain_outline);
        rain.setImageResource(weather == EWeather.RAIN ? R.drawable.ic_rain :
                R.drawable.ic_rain_outline);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.save, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            onSave();
            getActivity().finish();
            getActivity().overridePendingTransition(R.anim.left_in, R.anim.right_out);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        activity = getActivity();
        if (activity instanceof NowListFragment.OnItemSelectedListener) {
            listener = (NowListFragment.OnItemSelectedListener) activity;
        }
        Assert.assertNotNull(listener);
    }

    private void onSave() {
        Environment e = new Environment();
        e.weather = weather;
        e.windSpeed = (int) wind_speed.getSelectedItem().getId();
        e.windDirection = (int) wind_direction.getSelectedItem().getId();
        e.location = location.getText().toString();
        listener.onItemSelected(e);
    }
}