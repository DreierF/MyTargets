/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.SharedApplicationInstance;

public enum EWeather {
    SUNNY(0, R.string.sunny, R.drawable.ic_sun_48dp, R.drawable.ic_sun_outline_48dp, R.drawable.ic_sun_outline_color_48dp),
    PARTLY_CLOUDY(1, R.string.partly_cloudy, R.drawable.ic_partly_cloudy_48dp, R.drawable.ic_partly_cloudy_outline_48dp, R.drawable.ic_partly_cloudy_outline_color_48dp),
    CLOUDY(2, R.string.cloudy, R.drawable.ic_cloudy_48dp, R.drawable.ic_cloudy_outline_48dp, R.drawable.ic_cloudy_outline_color_48dp),
    LIGHT_RAIN(3, R.string.light_rain, R.drawable.ic_light_rain_48dp, R.drawable.ic_light_rain_outline_48dp, R.drawable.ic_light_rain_outline_color_48dp),
    RAIN(4, R.string.rain, R.drawable.ic_rain_48dp, R.drawable.ic_rain_outline_48dp, R.drawable.ic_rain_outline_color_48dp);

    private final int value;
    private final int name;
    private final int colorDrawable;
    private final int outlineDrawable;
    private final int outlineColorDrawable;

    EWeather(int value, int name, int colorDrawable, int outlineDrawable, int outlineColorDrawable) {
        this.value = value;
        this.name = name;
        this.colorDrawable = colorDrawable;
        this.outlineDrawable = outlineDrawable;
        this.outlineColorDrawable = outlineColorDrawable;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return SharedApplicationInstance.getContext().getString(name);
    }

    public int getDrawable() {
        return outlineColorDrawable;
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

    public int getColorDrawable() {
        return colorDrawable;
    }

    public int getDrawable(EWeather selected) {
        return selected == this ? outlineColorDrawable : outlineDrawable;
    }
}
