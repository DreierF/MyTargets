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

package de.dreier.mytargets.views.selector

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.location.Geocoder
import android.location.Location
import android.support.annotation.RequiresPermission
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import de.dreier.mytargets.R
import de.dreier.mytargets.databinding.SelectorItemImageDetailsBinding
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.features.training.environment.CurrentWeather
import de.dreier.mytargets.features.training.environment.Locator
import de.dreier.mytargets.features.training.environment.WeatherService
import de.dreier.mytargets.shared.models.Environment
import de.dreier.mytargets.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class EnvironmentSelector @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null
) : SelectorBase<Environment>(context, attrs, R.layout.selector_item_image_details, ENVIRONMENT_REQUEST_CODE) {

    private lateinit var binding: SelectorItemImageDetailsBinding

    override var selectedItem: Environment?
        get() = if (super.selectedItem == null) {
            Environment.getDefault(SettingsManager.indoor)
        } else super.selectedItem
        set(value) {
            super.selectedItem = value
        }

    override fun bindView(item: Environment) {
        binding = DataBindingUtil.bind(view)

        if (item.indoor) {
            binding.name.setText(de.dreier.mytargets.shared.R.string.indoor)
            binding.image.setImageResource(R.drawable.ic_house_24dp)
        } else {
            binding.name.text = item.weather.getName()
            binding.image.setImageResource(item.weather.drawable)
        }
        binding.details.visibility = View.VISIBLE
        binding.details.text = getDetails(context, item)
        binding.title.visibility = View.VISIBLE
        binding.title.setText(R.string.environment)
    }

    private fun getDetails(context: Context, item: Environment): String {
        var description: String
        if (item.indoor) {
            description = ""
            if (!item.location.isEmpty()) {
                description += "${context.getString(de.dreier.mytargets.shared.R.string.location)}: ${item.location}"
            }
        } else {
            description = "${context.getString(de.dreier.mytargets.shared.R.string.wind)}: ${item.getWindSpeed(context)}"
            if (!item.location.isEmpty()) {
                description += "\n${context.getString(de.dreier.mytargets.shared.R.string.location)}: ${item.location}"
            }
        }
        return description
    }

    fun queryWeather(fragment: Fragment, requestCode: Int) {
        if (isTestMode) {
            setDefaultWeather()
            return
        }
        if (ContextCompat.checkSelfPermission(fragment.context!!, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            setDefaultWeather()
            fragment.requestPermissions(arrayOf(ACCESS_FINE_LOCATION), requestCode)
        } else {
            queryWeatherInfo(fragment.context!!)
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
    @SuppressLint("MissingPermission", "SupportAnnotationUsage")
    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    private fun queryWeatherInfo(context: Context) {
        setItem(null)
        Locator(context).getLocation(Locator.Method.NETWORK_THEN_GPS, object : Locator.Listener {
            override fun onLocationFound(location: Location) {
                val locationStr = getAddressFromLocation(location.latitude, location.longitude)
                val weatherService = WeatherService()
                val weatherCall = weatherService
                        .fetchCurrentWeather(location.longitude, location.latitude)
                weatherCall.enqueue(object : Callback<CurrentWeather> {
                    override fun onResponse(call: Call<CurrentWeather>, response: Response<CurrentWeather>) {
                        if (response.isSuccessful && response.body()!!.httpCode == 200) {
                            val toEnvironment = response.body()!!.toEnvironment()
                            setItem(toEnvironment.copy(location = locationStr
                                    ?: toEnvironment.location))
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

    private fun getAddressFromLocation(latitude: Double, longitude: Double): String? {
        val geoCoder = Geocoder(context, Utils.getCurrentLocale(context))

        return try {
            val addresses = geoCoder.getFromLocation(latitude, longitude, 1)

            if (addresses.size > 0) {
                val fetchedAddress = addresses[0]
                var address = fetchedAddress.locality
                if(fetchedAddress.subLocality != null) {
                    address += ", ${fetchedAddress.subLocality}"
                }
                address
            } else {
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }

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
