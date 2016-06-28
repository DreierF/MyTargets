/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
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
import de.dreier.mytargets.activities.ItemSelectActivity;
import de.dreier.mytargets.models.CurrentWeather;
import de.dreier.mytargets.network.weather.WeatherService;
import de.dreier.mytargets.shared.models.EWeather;
import de.dreier.mytargets.shared.models.Environment;
import de.dreier.mytargets.utils.Locator;
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
        setTitle(R.string.environment);
        defaultActivity = ItemSelectActivity.EnvironmentActivity.class;
        requestCode = ENVIRONMENT_REQUEST_CODE;
    }

    public void queryWeather(Fragment fragment, int request_code) {
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
                        if (response.isSuccessful()) {
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
        setItem(new Environment(EWeather.SUNNY, 0, 0));
    }

    @Override
    public Environment getSelectedItem() {
        if (item == null) {
            return new Environment(EWeather.SUNNY, 0, 0);
        }
        return item;
    }
}
