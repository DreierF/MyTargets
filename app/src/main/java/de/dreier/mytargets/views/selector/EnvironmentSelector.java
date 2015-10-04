/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */

package de.dreier.mytargets.views.selector;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.ItemSelectActivity;
import de.dreier.mytargets.network.weather.WeatherService;
import de.dreier.mytargets.shared.models.EWeather;
import de.dreier.mytargets.shared.models.Environment;
import de.dreier.mytargets.utils.Locator;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class EnvironmentSelector extends SelectorBase<Environment> {

    public EnvironmentSelector(Context context) {
        this(context, null);
    }

    public EnvironmentSelector(Context context, AttributeSet attrs) {
        super(context, attrs, R.layout.item_environment);
        setOnClickActivity(ItemSelectActivity.EnvironmentActivity.class);
    }

    @Override
    protected void bindView() {
        ImageView img = (ImageView) mView.findViewById(R.id.image);
        TextView desc = (TextView) mView.findViewById(R.id.name);
        TextView details = (TextView) mView.findViewById(R.id.details);

        img.setImageResource(item.weather.getDrawable());
        desc.setText(item.weather.getName());
        String direction = getContext().getResources()
                .getStringArray(R.array.wind_directions)[item.windDirection];
        String description =
                getContext().getString(R.string.wind) + ": " + item.windSpeed + " Btf " +
                        direction;
        if (!TextUtils.isEmpty(item.location)) {
            description +=
                    "\n" + getContext().getString(R.string.location) + ": " + item.location;
        }
        details.setText(description);
        details.setVisibility(View.VISIBLE);
    }


    public void queryWeather(Fragment fragment, int request_code) {
        if (ContextCompat.checkSelfPermission(fragment.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            fragment.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    request_code);
        } else {
            queryWeatherInfo(fragment.getContext());
        }
    }

    public void onPermissionResult(Activity activity, int[] grantResult) {
        if (grantResult[0] == PackageManager.PERMISSION_GRANTED) {
            //noinspection MissingPermission
            queryWeatherInfo(activity);
        } else {
            setDefaultWeather();
        }
    }

    // Start getting weather for current location
    @SuppressWarnings("MissingPermission")
    @RequiresPermission(anyOf = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    protected void queryWeatherInfo(Context context) {
        new Locator(context).getLocation(Locator.Method.NETWORK_THEN_GPS, new Locator.Listener() {
            @Override
            public void onLocationFound(Location location) {
                new WeatherService()
                        .fetchCurrentWeather(location.getLongitude(), location.getLatitude())
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(currentWeather -> setItem(currentWeather),
                                error -> {
                                    error.printStackTrace();
                                    setDefaultWeather();
                                });
            }

            @Override
            public void onLocationNotFound() {
                setDefaultWeather();
            }
        });
    }

    public void setDefaultWeather() {
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
