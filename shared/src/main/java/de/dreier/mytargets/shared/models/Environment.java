/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models;

public class Environment extends IdProvider {
    static final long serialVersionUID = 60L;
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
}
