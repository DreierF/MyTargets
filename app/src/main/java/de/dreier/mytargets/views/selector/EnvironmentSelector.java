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
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.dreier.mytargets.R;
import de.dreier.mytargets.activities.ItemSelectActivity;
import de.dreier.mytargets.shared.models.EWeather;
import de.dreier.mytargets.shared.models.Environment;
import zh.wang.android.apis.yweathergetter4a.WeatherInfo;
import zh.wang.android.apis.yweathergetter4a.YahooWeather;
import zh.wang.android.apis.yweathergetter4a.YahooWeatherExceptionListener;
import zh.wang.android.apis.yweathergetter4a.YahooWeatherInfoListener;

public class EnvironmentSelector extends SelectorBase<Environment> implements
        YahooWeatherExceptionListener, YahooWeatherInfoListener {

    public EnvironmentSelector(Context context) {
        this(context, null);
    }

    public EnvironmentSelector(Context context, AttributeSet attrs) {
        super(context, attrs, R.layout.item_image);
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

    // Start getting weather for current location
    protected void queryWeatherInfo(Context context) {
        try {
            YahooWeather weather = new YahooWeather();
            weather.setExceptionListener(this);
            weather.queryYahooWeatherByGPS(context, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onPermissionResult(Activity activity, int[] grantResult) {
        if (grantResult[0] == PackageManager.PERMISSION_GRANTED) {
            queryWeatherInfo(activity);
        } else {
            setDefaultWeather();
        }
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
        setItem(e);
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
        post(EnvironmentSelector.this::setDefaultWeather);
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
