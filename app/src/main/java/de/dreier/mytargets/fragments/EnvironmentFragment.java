/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.fragments;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import de.dreier.mytargets.databinding.FragmentEnvironmentBinding;
import de.dreier.mytargets.shared.models.EWeather;
import de.dreier.mytargets.shared.models.Environment;
import de.dreier.mytargets.utils.ToolbarUtils;

import static de.dreier.mytargets.activities.ItemSelectActivity.ITEM;

public class EnvironmentFragment extends Fragment {

    private SelectItemFragment.OnItemSelectedListener listener;
    private Environment mEnvironment;
    private EWeather weather;
    private FragmentEnvironmentBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_environment, container, false);

        Bundle i = getArguments();
        if (i != null) {
            mEnvironment = Parcels.unwrap(i.getParcelable(ITEM));
        }

        // Weather
        setOnClickWeather(binding.sunny, EWeather.SUNNY);
        setOnClickWeather(binding.partlyCloudy, EWeather.PARTLY_CLOUDY);
        setOnClickWeather(binding.cloudy, EWeather.CLOUDY);
        setOnClickWeather(binding.lightRain, EWeather.LIGHT_RAIN);
        setOnClickWeather(binding.rain, EWeather.RAIN);

        setWeather(mEnvironment.weather);
        binding.windSpeed.setItemId(mEnvironment.windSpeed);
        binding.windDirection.setItemId(mEnvironment.windDirection);
        binding.location.setText(mEnvironment.location);
        setHasOptionsMenu(true);

        binding.windDirection.setOnActivityResultContext(this);
        binding.windSpeed.setOnActivityResultContext(this);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ToolbarUtils.showUpAsX(this);
    }

    private void setOnClickWeather(ImageButton b, final EWeather w) {
        b.setOnClickListener(v -> setWeather(w));
    }

    private void setWeather(EWeather weather) {
        this.weather = weather;
        binding.sunny.setImageResource(weather == EWeather.SUNNY ? R.drawable.ic_sun_48dp :
                R.drawable.ic_sun_outline_48dp);
        binding.partlyCloudy.setImageResource(
                weather == EWeather.PARTLY_CLOUDY ? R.drawable.ic_partly_cloudy_48dp :
                        R.drawable.ic_partly_cloudy_outline_48dp);
        binding.cloudy.setImageResource(weather == EWeather.CLOUDY ? R.drawable.ic_cloudy_48dp :
                R.drawable.ic_cloudy_outline_48dp);
        binding.lightRain.setImageResource(
                weather == EWeather.LIGHT_RAIN ? R.drawable.ic_light_rain_48dp :
                        R.drawable.ic_light_rain_outline_48dp);
        binding.rain.setImageResource(weather == EWeather.RAIN ? R.drawable.ic_rain_48dp :
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
        e.windSpeed = (int) binding.windSpeed.getSelectedItem().getId();
        e.windDirection = (int) binding.windDirection.getSelectedItem().getId();
        e.location = binding.location.getText().toString();
        listener.onItemSelected(Parcels.wrap(e));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        binding.windSpeed.onActivityResult(requestCode, resultCode, data);
        binding.windDirection.onActivityResult(requestCode, resultCode, data);
    }
}