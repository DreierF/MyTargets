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
package de.dreier.mytargets.shared.models;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.SharedApplicationInstance;

public class Environment implements IImageProvider, IDetailProvider {
    public boolean indoor;
    public EWeather weather;
    public int windSpeed;
    public int windDirection;
    public String location;

    public Environment() {
    }

    public Environment(boolean indoor, EWeather weather, int windSpeed, int windDirection, String location) {
        this.indoor = indoor;
        this.weather = weather;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.location = location;
    }

    @Override
    public String getName() {
        return indoor ? SharedApplicationInstance.get(R.string.indoor) : weather.getName();
    }

    @Override
    public String getDetails(Context context) {
        String description;
        if(indoor) {
            description = "";
            if (!TextUtils.isEmpty(location)) {
                description += context.getString(R.string.location) + ": " + location;
            }
        } else {
            description = context.getString(R.string.wind) + ": " + getWindSpeed(context);
            if (!TextUtils.isEmpty(location)) {
                description += "\n" + context.getString(R.string.location) + ": " + location;
            }
        }
        return description;
    }

    @NonNull
    public String getWindSpeed(Context context) {
        return windSpeed + " Bft " + WindDirection.getList(context).get(windDirection).getName();
    }

    @Override
    public Drawable getDrawable(Context context) {
        if (indoor) {
            return context.getResources().getDrawable(R.drawable.ic_house_24dp);
        } else {
            return context.getResources().getDrawable(weather.getDrawable());
        }
    }

    @DrawableRes
    public int getColorDrawable() {
        if (indoor) {
            return R.drawable.ic_house_24dp;
        } else {
            return weather.getColorDrawable();
        }
    }
}
