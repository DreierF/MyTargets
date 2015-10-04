/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import de.dreier.mytargets.views.selector.WindDirectionSelector;
import de.dreier.mytargets.views.selector.WindSpeedSelector;

import static de.dreier.mytargets.activities.ItemSelectActivity.ITEM;

public class EnvironmentFragment extends Fragment {

    private WindSpeedSelector windSpeed;
    private WindDirectionSelector windDirection;
    private SelectItemFragment.OnItemSelectedListener listener;
    private Environment mEnvironment;
    private EWeather weather;
    private ImageButton sunny, partlyCloudy, cloudy, lightRain, rain;
    private EditText location;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_environment, container, false);

        Bundle i = getArguments();
        if (i != null) {
            mEnvironment = (Environment) i.getSerializable(ITEM);
        }

        // Weather
        sunny = (ImageButton) rootView.findViewById(R.id.sunny);
        partlyCloudy = (ImageButton) rootView.findViewById(R.id.partly_cloudy);
        cloudy = (ImageButton) rootView.findViewById(R.id.clouds);
        lightRain = (ImageButton) rootView.findViewById(R.id.light_rain);
        rain = (ImageButton) rootView.findViewById(R.id.rain);
        setOnClickWeather(sunny, EWeather.SUNNY);
        setOnClickWeather(partlyCloudy, EWeather.PARTLY_CLOUDY);
        setOnClickWeather(cloudy, EWeather.CLOUDY);
        setOnClickWeather(lightRain, EWeather.LIGHT_RAIN);
        setOnClickWeather(rain, EWeather.RAIN);

        windSpeed = (WindSpeedSelector) rootView.findViewById(R.id.wind_speed);
        windDirection = (WindDirectionSelector) rootView.findViewById(R.id.wind_direction);
        location = (EditText) rootView.findViewById(R.id.location);

        setWeather(mEnvironment.weather);
        windSpeed.setItemId(mEnvironment.windSpeed);
        windDirection.setItemId(mEnvironment.windDirection);
        location.setText(mEnvironment.location);
        setHasOptionsMenu(true);
        return rootView;
    }

    private void setOnClickWeather(ImageButton b, final EWeather w) {
        b.setOnClickListener(v -> setWeather(w));
    }

    private void setWeather(EWeather weather) {
        this.weather = weather;
        sunny.setImageResource(weather == EWeather.SUNNY ? R.drawable.ic_sun_48dp :
                R.drawable.ic_sun_outline_48dp);
        partlyCloudy.setImageResource(
                weather == EWeather.PARTLY_CLOUDY ? R.drawable.ic_partly_cloudy_48dp :
                        R.drawable.ic_partly_cloudy_outline_48dp);
        cloudy.setImageResource(weather == EWeather.CLOUDY ? R.drawable.ic_cloudy_48dp :
                R.drawable.ic_cloudy_outline_48dp);
        lightRain.setImageResource(
                weather == EWeather.LIGHT_RAIN ? R.drawable.ic_light_rain_48dp :
                        R.drawable.ic_light_rain_outline_48dp);
        rain.setImageResource(weather == EWeather.RAIN ? R.drawable.ic_rain_48dp :
                R.drawable.ic_rain_outline_48dp);
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
        if (activity instanceof SelectItemFragment.OnItemSelectedListener) {
            listener = (SelectItemFragment.OnItemSelectedListener) activity;
        }
        Assert.assertNotNull(listener);
    }

    private void onSave() {
        Environment e = new Environment();
        e.weather = weather;
        e.windSpeed = (int) windSpeed.getSelectedItem().getId();
        e.windDirection = (int) windDirection.getSelectedItem().getId();
        e.location = location.getText().toString();
        listener.onItemSelected(e);
    }
}