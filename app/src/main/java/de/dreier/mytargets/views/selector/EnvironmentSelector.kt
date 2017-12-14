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

package de.dreier.mytargets.views.selector

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.support.annotation.RequiresPermission
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import de.dreier.mytargets.R
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.features.training.environment.CurrentWeather
import de.dreier.mytargets.features.training.environment.EnvironmentActivity
import de.dreier.mytargets.features.training.environment.Locator
import de.dreier.mytargets.features.training.environment.WeatherService
import de.dreier.mytargets.shared.models.Environment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EnvironmentSelector @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null
) : ImageSelectorBase<Environment>(context, attrs, R.string.environment) {

    override var selectedItem: Environment?
        get() = if (super.selectedItem == null) {
            Environment.getDefault(SettingsManager.indoor)
        } else super.selectedItem
        set(value) {
            super.selectedItem = value
        }

    init {
        defaultActivity = EnvironmentActivity::class.java
        requestCode = ENVIRONMENT_REQUEST_CODE
    }

    fun queryWeather(fragment: Fragment, request_code: Int) {
        if (isTestMode) {
            setDefaultWeather()
            return
        }
        if (ContextCompat.checkSelfPermission(fragment.context!!, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            setDefaultWeather()
            fragment.requestPermissions(arrayOf(ACCESS_FINE_LOCATION),
                    request_code)
        } else {
            queryWeatherInfo(fragment.context)
        }
    }

    @SuppressLint("MissingPermission")
    fun onPermissionResult(activity: Activity, grantResult: IntArray) {
        if (grantResult.isNotEmpty() && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
            queryWeatherInfo(activity)
        } else {
            setDefaultWeather()
        }
    }

    // Start getting weather for current location
    @SuppressLint("MissingPermission")
    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    private fun queryWeatherInfo(context: Context?) {
        setItem(null)
        Locator(context!!).getLocation(Locator.Method.NETWORK_THEN_GPS, object : Locator.Listener {
            override fun onLocationFound(location: Location?) {
                val weatherService = WeatherService()
                val weatherCall = weatherService
                        .fetchCurrentWeather(location!!.longitude, location.latitude)
                weatherCall.enqueue(object : Callback<CurrentWeather> {
                    override fun onResponse(call: Call<CurrentWeather>, response: Response<CurrentWeather>) {
                        if (response.isSuccessful && response.body()!!.httpCode == 200) {
                            setItem(response.body()!!.toEnvironment())
                        } else {
                            setDefaultWeather()
                        }
                    }

                    override fun onFailure(call: Call<CurrentWeather>, t: Throwable) {
                        setDefaultWeather()
                    }
                })
            }

            override fun onLocationNotFound() {
                setDefaultWeather()
            }
        })
    }

    private fun setDefaultWeather() {
        setItem(Environment.getDefault(SettingsManager.indoor))
    }

    companion object {
        const val ENVIRONMENT_REQUEST_CODE = 9

        private val isTestMode: Boolean
            get() {
                return try {
                    Class.forName("de.dreier.mytargets.test.base.InstrumentedTestBase")
                    true
                } catch (e: Exception) {
                    false
                }
            }
    }
}
