/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models;

public class Environment implements IIdSettable {
    public static final String ID = "_id";
    static final long serialVersionUID = 60L;
    public EWeather weather;
    public int windSpeed;
    public int windDirection;
    public String location;
    protected long id;

    public Environment() {

    }

    public Environment(EWeather weather, int windSpeed, int windDirection) {
        this.weather = weather;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Environment &&
                getClass().equals(another.getClass()) &&
                id == ((Environment) another).id;
    }
}
