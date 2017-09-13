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
package de.dreier.mytargets.features.training.environment;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import junit.framework.Assert;

import org.parceler.Parcels;

import de.dreier.mytargets.R;
import de.dreier.mytargets.base.fragments.FragmentBase;
import de.dreier.mytargets.base.fragments.ListFragmentBase;
import de.dreier.mytargets.databinding.FragmentEnvironmentBinding;
import de.dreier.mytargets.features.settings.SettingsManager;
import de.dreier.mytargets.shared.models.EWeather;
import de.dreier.mytargets.shared.models.Environment;
import de.dreier.mytargets.shared.utils.ParcelsBundler;
import de.dreier.mytargets.utils.ToolbarUtils;
import icepick.State;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static de.dreier.mytargets.base.activities.ItemSelectActivity.ITEM;

public class EnvironmentFragment extends FragmentBase {

    private ListFragmentBase.OnItemSelectedListener listener;
    @State(ParcelsBundler.class)
    Environment environment;
    private FragmentEnvironmentBinding binding;
    private SwitchCompat switchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_environment, container, false);

        ToolbarUtils.setSupportActionBar(this, binding.toolbar);
        ToolbarUtils.showHomeAsUp(this);
        setHasOptionsMenu(true);

        // Weather
        setOnClickWeather(binding.sunny, EWeather.SUNNY);
        setOnClickWeather(binding.partlyCloudy, EWeather.PARTLY_CLOUDY);
        setOnClickWeather(binding.cloudy, EWeather.CLOUDY);
        setOnClickWeather(binding.lightRain, EWeather.LIGHT_RAIN);
        setOnClickWeather(binding.rain, EWeather.RAIN);

        if (savedInstanceState == null) {
            Bundle i = getArguments();
            assert i!= null;
            environment = Parcels.unwrap(i.getParcelable(ITEM));
        }
        setWeather(environment.weather);
        binding.windSpeed.setItemId(environment.windSpeed);
        binding.windDirection.setItemId(environment.windDirection);
        binding.location.setText(environment.location);

        binding.windDirection.setOnActivityResultContext(this);
        binding.windSpeed.setOnActivityResultContext(this);

        return binding.getRoot();
    }

    private void setOnClickWeather(ImageButton b, final EWeather w) {
        b.setOnClickListener(v -> setWeather(w));
    }

    private void setWeather(EWeather weather) {
        environment.weather = weather;
        binding.sunny.setImageResource(EWeather.SUNNY.getDrawable(weather));
        binding.partlyCloudy.setImageResource(EWeather.PARTLY_CLOUDY.getDrawable(weather));
        binding.cloudy.setImageResource(EWeather.CLOUDY.getDrawable(weather));
        binding.lightRain.setImageResource(EWeather.LIGHT_RAIN.getDrawable(weather));
        binding.rain.setImageResource(EWeather.RAIN.getDrawable(weather));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.environment_switch, menu);
        MenuItem item = menu.findItem(R.id.action_switch);
        switchView = item.getActionView().findViewById(R.id.action_switch_control);
        switchView.setOnCheckedChangeListener((compoundButton, checked) -> setOutdoor(checked));
        setOutdoor(!environment.indoor);
        switchView.setChecked(!environment.indoor);
    }

    protected void setOutdoor(boolean checked) {
        switchView.setText(checked ? R.string.outdoor : R.string.indoor);
        binding.indoorPlaceholder.setVisibility(checked ? GONE : VISIBLE);
        binding.weatherLayout.setVisibility(checked ? VISIBLE : GONE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        environment = saveItem();
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        activity = getActivity();
        if (activity instanceof ListFragmentBase.OnItemSelectedListener) {
            listener = (ListFragmentBase.OnItemSelectedListener) activity;
        }
        Assert.assertNotNull(listener);
    }

    public void onSave() {
        Environment e = saveItem();
        listener.onItemSelected(Parcels.wrap(e));
        finish();
        SettingsManager.setIndoor(e.indoor);
    }

    @NonNull
    public Environment saveItem() {
        Environment e = new Environment();
        e.indoor = !switchView.isChecked();
        e.weather = environment.weather;
        e.windSpeed = (int) (long) binding.windSpeed.getSelectedItem().getId();
        e.windDirection = (int) (long) binding.windDirection.getSelectedItem().getId();
        e.location = binding.location.getText().toString();
        return e;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        binding.windSpeed.onActivityResult(requestCode, resultCode, data);
        binding.windDirection.onActivityResult(requestCode, resultCode, data);
    }
}
