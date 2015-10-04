/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models;

import de.dreier.mytargets.shared.R;

public enum EWeather {
    SUNNY(0, R.string.sunny, R.drawable.ic_sun_48dp),
    PARTLY_CLOUDY(1, R.string.partly_cloudy, R.drawable.ic_partly_cloudy_48dp),
    CLOUDY(2, R.string.cloudy, R.drawable.ic_cloudy_48dp),
    LIGHT_RAIN(3, R.string.light_rain, R.drawable.ic_light_rain_48dp),
    RAIN(4, R.string.rain, R.drawable.ic_rain_48dp);

    private final int value;
    private final int name;
    private final int drawable;

    EWeather(int value, int name, int drawable) {
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

    public static EWeather getOfValue(int value) {
        for (EWeather e : EWeather.values()) {
            if (e.getValue() == value) {
                return e;
            }
        }
        throw new IllegalArgumentException(
                "No enum const " + EWeather.class.getName() + " for code \'" + value + "\'");
    }
}
