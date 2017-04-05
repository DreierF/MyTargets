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

package de.dreier.mytargets.views.selector;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import de.dreier.mytargets.R;
import de.dreier.mytargets.features.settings.SettingsManager;
import de.dreier.mytargets.features.training.environment.CurrentWeather;
import de.dreier.mytargets.features.training.environment.EnvironmentActivity;
import de.dreier.mytargets.features.training.environment.Locator;
import de.dreier.mytargets.features.training.environment.WeatherService;
import de.dreier.mytargets.shared.models.Environment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class EnvironmentSelector extends ImageSelectorBase<Environment> {

    private static final int ENVIRONMENT_REQUEST_CODE = 9;

    public EnvironmentSelector(Context context) {
        this(context, null);
    }

    public EnvironmentSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        defaultActivity = EnvironmentActivity.class;
        requestCode = ENVIRONMENT_REQUEST_CODE;
    }

    @Override
    protected void bindView() {
        super.bindView();
        setTitle(R.string.environment);
    }

    private static boolean isTestMode() {
        boolean result;
        try {
            Class.forName("de.dreier.mytargets.test.base.InstrumentedTestBase");
            result = true;
        } catch (final Exception e) {
            result = false;
        }
        return result;
    }

    public void queryWeather(Fragment fragment, int request_code) {
        if (isTestMode()) {
            setDefaultWeather();
            return;
        }
        if (ContextCompat.checkSelfPermission(fragment.getContext(), ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            setDefaultWeather();
            fragment.requestPermissions(new String[]{ACCESS_FINE_LOCATION},
                    request_code);
        } else {
            queryWeatherInfo(fragment.getContext());
        }
    }

    public void onPermissionResult(Activity activity, int[] grantResult) {
        if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
            //noinspection MissingPermission
            queryWeatherInfo(activity);
        } else {
            setDefaultWeather();
        }
    }

    // Start getting weather for current location
    @SuppressWarnings("MissingPermission")
    @RequiresPermission(anyOf = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    private void queryWeatherInfo(Context context) {
        setItem(null);
        new Locator(context).getLocation(Locator.Method.NETWORK_THEN_GPS, new Locator.Listener() {
            @Override
            public void onLocationFound(Location location) {
                final WeatherService weatherService = new WeatherService();
                final Call<CurrentWeather> weatherCall = weatherService
                        .fetchCurrentWeather(location.getLongitude(), location.getLatitude());
                weatherCall.enqueue(new Callback<CurrentWeather>() {
                    @Override
                    public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                        if (response.isSuccessful() && response.body().httpCode == 200) {
                            setItem(response.body().toEnvironment());
                        } else {
                            setDefaultWeather();
                        }
                    }

                    @Override
                    public void onFailure(Call<CurrentWeather> call, Throwable t) {
                        setDefaultWeather();
                    }
                });
            }

            @Override
            public void onLocationNotFound() {
                setDefaultWeather();
            }
        });
    }

    private void setDefaultWeather() {
        setItem(Environment.getDefault(SettingsManager.getIndoor()));
    }

    @Override
    public Environment getSelectedItem() {
        if (item == null) {
            return Environment.getDefault(SettingsManager.getIndoor());
        }
        return item;
    }

}
