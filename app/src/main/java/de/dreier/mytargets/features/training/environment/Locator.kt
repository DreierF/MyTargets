/*
 * Copyright (C) 2018 Florian Dreier
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

package de.dreier.mytargets.features.training.environment

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.annotation.RequiresPermission
import androidx.core.content.getSystemService
import timber.log.Timber

/**
 * Get device location using various methods
 *
 * @author emil http://stackoverflow.com/users/220710/emil
 */
class Locator(private val context: Context) : LocationListener {
    private val locationManager = context.getSystemService<LocationManager>()!!
    private var method: Locator.Method? = null
    private var callback: Locator.Listener? = null

    enum class Method {
        NETWORK,
        GPS,
        NETWORK_THEN_GPS
    }

    @SuppressLint("MissingPermission", "SupportAnnotationUsage")
    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    fun getLocation(method: Locator.Method, callback: Locator.Listener) {
        this.method = method
        this.callback = callback
        when (this.method) {
            Locator.Method.NETWORK, Locator.Method.NETWORK_THEN_GPS -> {
                val networkLocation =
                    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (networkLocation != null) {
                    Timber.d(
                        "Last known location found for network provider : %s", networkLocation
                            .toString()
                    )
                    this.callback!!.onLocationFound(networkLocation)
                } else {
                    Timber.d("Request updates from network provider.")
                    this.requestUpdates(LocationManager.NETWORK_PROVIDER)
                }
            }
            Locator.Method.GPS -> {
                val gpsLocation = this.locationManager
                    .getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (gpsLocation != null) {
                    Timber.d(
                        "Last known location found for GPS provider : %s", gpsLocation
                            .toString()
                    )
                    this.callback!!.onLocationFound(gpsLocation)
                } else {
                    Timber.d("Request updates from GPS provider.")
                    this.requestUpdates(LocationManager.GPS_PROVIDER)
                }
            }
        }
    }

    @SuppressLint("MissingPermission", "SupportAnnotationUsage")
    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    private fun requestUpdates(provider: String) {
        if (locationManager.isProviderEnabled(provider)) {
            if (provider.contentEquals(LocationManager.NETWORK_PROVIDER) && Connectivity.isConnected(
                    this.context
                )
            ) {
                Timber.d("Network connected, start listening : %s", provider)
                locationManager
                    .requestLocationUpdates(
                        provider,
                        TIME_INTERVAL.toLong(),
                        DISTANCE_INTERVAL.toFloat(),
                        this
                    )
            } else if (provider.contentEquals(LocationManager.GPS_PROVIDER) && Connectivity.isConnectedMobile(
                    this.context
                )
            ) {
                Timber.d("Mobile network connected, start listening : %s", provider)
                locationManager
                    .requestLocationUpdates(
                        provider,
                        TIME_INTERVAL.toLong(),
                        DISTANCE_INTERVAL.toFloat(),
                        this
                    )
            } else {
                Timber.d("Proper network not connected for provider : %s", provider)
                onProviderDisabled(provider)
            }
        } else {
            onProviderDisabled(provider)
        }
    }

    fun cancel() {
        Timber.d("Locating canceled.")
        locationManager.removeUpdates(this)
    }

    override fun onLocationChanged(location: Location) {
        Timber.d(
            "Location found : %f, %f%s", location.latitude, location
                .longitude, if (location.hasAccuracy())
                " : +- ${location.accuracy} meters"
            else
                ""
        )
        locationManager.removeUpdates(this)
        callback!!.onLocationFound(location)
    }

    override fun onProviderDisabled(provider: String) {
        Timber.d("Provider disabled : %s", provider)
        if (this.method == Locator.Method.NETWORK_THEN_GPS && provider.contentEquals(LocationManager.NETWORK_PROVIDER)) {
            // Network provider disabled, try GPS
            Timber.d("Request updates from GPS provider, network provider disabled.")
            this.requestUpdates(LocationManager.GPS_PROVIDER)
        } else {
            this.locationManager.removeUpdates(this)
            this.callback!!.onLocationNotFound()
        }
    }

    override fun onProviderEnabled(provider: String) {
        Timber.d("Provider enabled : %s", provider)
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        Timber.d("Provided status changed : $provider : status : $status")
    }

    interface Listener {
        fun onLocationFound(location: Location)
        fun onLocationNotFound()
    }

    companion object {
        private const val TIME_INTERVAL = 100 // minimum time between updates in milliseconds
        private const val DISTANCE_INTERVAL = 1 // minimum distance between updates in meters
    }

}
