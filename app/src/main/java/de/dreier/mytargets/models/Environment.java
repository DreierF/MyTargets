/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.models;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import de.dreier.mytargets.R;
import de.dreier.mytargets.shared.models.IdProvider;

public class Environment extends IdProvider {
    static final long serialVersionUID = 60L;
    public WEATHER weather;
    public int windSpeed;
    public int windDirection;
    public String location;

    public Environment() {

    }

    public Environment(WEATHER weather, int windSpeed, int windDirection) {
        this.weather = weather;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
    }

    public enum WEATHER {
        SUNNY(0, R.string.sunny, R.drawable.ic_sun),
        PARTLY_CLOUDY(1, R.string.partly_cloudy, R.drawable.ic_partly_cloudy),
        CLOUDY(2, R.string.cloudy, R.drawable.ic_cloudy),
        LIGHT_RAIN(3, R.string.light_rain, R.drawable.ic_light_rain),
        RAIN(4, R.string.rain, R.drawable.ic_rain);

        private final int value;
        private final int name;
        private final int drawable;

        WEATHER(int value, @StringRes int name, @DrawableRes int drawable) {
            this.value = value;
            this.name = name;
            this.drawable = drawable;
        }

        public int getValue() {
            return value;
        }

        public int getName() {
            return name;
        }

        public int getDrawable() {
            return drawable;
        }
    }
}
