/*
 * Copyright (C) 2016 Florian Dreier
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
package de.dreier.mytargets.shared.models;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import de.dreier.mytargets.shared.R;

public class Environment implements IImageProvider, IDetailProvider {
    public EWeather weather;
    public int windSpeed;
    public int windDirection;
    public String location;

    public Environment() {
    }

    public Environment(EWeather weather, int windSpeed, int windDirection) {
        this.weather = weather;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
    }

    @Override
    public String getName() {
        return weather.getName();
    }

    @Override
    public String getDetails(Context context) {
        String description = context.getString(R.string.wind) + ": " + getWindSpeed(context);
        if (!TextUtils.isEmpty(location)) {
            description += "\n" + context.getString(R.string.location) + ": " + location;
        }
        return description;
    }

    @NonNull
    public String getWindSpeed(Context context) {
        return windSpeed + " Btf " + WindDirection.getList(context).get(windDirection).getName();
    }

    @Override
    public Drawable getDrawable(Context context) {
        return context.getResources().getDrawable(weather.getDrawable());
    }
}
