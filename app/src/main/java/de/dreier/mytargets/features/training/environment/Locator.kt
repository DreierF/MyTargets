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

package de.dreier.mytargets.features.training.environment;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;

import timber.log.Timber;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * Get device location using various methods
 *
 * @author emil http://stackoverflow.com/users/220710/emil
 */
public class Locator implements LocationListener {

    static private final int TIME_INTERVAL = 100; // minimum time between updates in milliseconds
    static private final int DISTANCE_INTERVAL = 1; // minimum distance between updates in meters

    public enum Method {
        NETWORK,
        GPS,
        NETWORK_THEN_GPS
    }

    @NonNull
    private final Context context;
    @Nullable
    private final LocationManager locationManager;
    private Locator.Method method;
    private Locator.Listener callback;

    public Locator(@NonNull Context context) {
        super();
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @SuppressWarnings("MissingPermission")
    @RequiresPermission(anyOf = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION})
    public void getLocation(Locator.Method method, Locator.Listener callback) {
        this.method = method;
        this.callback = callback;
        switch (this.method) {
            case NETWORK:
            case NETWORK_THEN_GPS:
                Location networkLocation = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (networkLocation != null) {
                    Timber.d("Last known location found for network provider : %s", networkLocation
                            .toString());
                    this.callback.onLocationFound(networkLocation);
                } else {
                    Timber.d("Request updates from network provider.");
                    this.requestUpdates(LocationManager.NETWORK_PROVIDER);
                }
                break;
            case GPS:
                Location gpsLocation = this.locationManager
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (gpsLocation != null) {
                    Timber.d("Last known location found for GPS provider : %s", gpsLocation
                            .toString());
                    this.callback.onLocationFound(gpsLocation);
                } else {
                    Timber.d("Request updates from GPS provider.");
                    this.requestUpdates(LocationManager.GPS_PROVIDER);
                }
                break;
        }
    }

    @SuppressWarnings("MissingPermission")
    private void requestUpdates(@NonNull String provider) {
        if (this.locationManager.isProviderEnabled(provider)) {
            if (provider.contentEquals(LocationManager.NETWORK_PROVIDER)
                    && Connectivity.isConnected(this.context)) {
                Timber.d("Network connected, start listening : %s", provider);
                this.locationManager
                        .requestLocationUpdates(provider, TIME_INTERVAL, DISTANCE_INTERVAL, this);
            } else if (provider.contentEquals(LocationManager.GPS_PROVIDER)
                    && Connectivity.isConnectedMobile(this.context)) {
                Timber.d("Mobile network connected, start listening : %s", provider);
                this.locationManager
                        .requestLocationUpdates(provider, TIME_INTERVAL, DISTANCE_INTERVAL, this);
            } else {
                Timber.d("Proper network not connected for provider : %s", provider);
                this.onProviderDisabled(provider);
            }
        } else {
            this.onProviderDisabled(provider);
        }
    }

    @SuppressWarnings("MissingPermission")
    public void cancel() {
        Timber.d("Locating canceled.");
        locationManager.removeUpdates(this);
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onLocationChanged(@NonNull Location location) {
        Timber.d("Location found : " + location.getLatitude() + ", " + location
                .getLongitude() + (location.hasAccuracy() ? " : +- " + location
                .getAccuracy() + " meters" : ""));
        locationManager.removeUpdates(this);
        callback.onLocationFound(location);
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Timber.d("Provider disabled : %s", provider);
        if (this.method == Locator.Method.NETWORK_THEN_GPS
                && provider.contentEquals(LocationManager.NETWORK_PROVIDER)) {
            // Network provider disabled, try GPS
            Timber.d("Request updates from GPS provider, network provider disabled.");
            this.requestUpdates(LocationManager.GPS_PROVIDER);
        } else {
            this.locationManager.removeUpdates(this);
            this.callback.onLocationNotFound();
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        Timber.d("Provider enabled : %s", provider);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Timber.d("Provided status changed : " + provider + " : status : " + status);
    }

    public interface Listener {
        void onLocationFound(Location location);

        void onLocationNotFound();
    }

}
