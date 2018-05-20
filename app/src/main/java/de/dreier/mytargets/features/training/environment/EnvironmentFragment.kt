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

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.SwitchCompat
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import com.evernote.android.state.State
import de.dreier.mytargets.R
import de.dreier.mytargets.base.fragments.FragmentBase
import de.dreier.mytargets.base.navigation.NavigationController.Companion.ITEM
import de.dreier.mytargets.databinding.FragmentEnvironmentBinding
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.shared.models.EWeather
import de.dreier.mytargets.shared.models.Environment
import de.dreier.mytargets.utils.ToolbarUtils

class EnvironmentFragment : FragmentBase() {

    @State
    var environment: Environment? = null
    private lateinit var binding: FragmentEnvironmentBinding
    private var switchView: SwitchCompat? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_environment, container, false)

        ToolbarUtils.setSupportActionBar(this, binding.toolbar)
        ToolbarUtils.showHomeAsUp(this)
        setHasOptionsMenu(true)

        // Weather
        binding.sunny.setOnClickListener { setWeather(EWeather.SUNNY) }
        binding.partlyCloudy.setOnClickListener { setWeather(EWeather.PARTLY_CLOUDY) }
        binding.cloudy.setOnClickListener { setWeather(EWeather.CLOUDY) }
        binding.lightRain.setOnClickListener { setWeather(EWeather.LIGHT_RAIN) }
        binding.rain.setOnClickListener { setWeather(EWeather.RAIN) }

        if (savedInstanceState == null) {
            environment = arguments!!.getParcelable(ITEM)
        }
        setWeather(environment!!.weather)
        binding.windSpeed.setItemId(environment!!.windSpeed.toLong())
        binding.windDirection.setItemId(environment!!.windDirection.toLong())
        binding.location.setText(environment!!.location)

        binding.windDirection.setOnClickListener { selectedItem, _ ->
            navigationController.navigateToWindDirection(selectedItem!!)
        }
        binding.windSpeed.setOnClickListener { selectedItem, _ ->
            navigationController.navigateToWindSpeed(selectedItem!!)
        }

        return binding.root
    }

    private fun setWeather(weather: EWeather) {
        environment!!.weather = weather
        binding.sunny.setImageResource(EWeather.SUNNY.getDrawable(weather))
        binding.partlyCloudy.setImageResource(EWeather.PARTLY_CLOUDY.getDrawable(weather))
        binding.cloudy.setImageResource(EWeather.CLOUDY.getDrawable(weather))
        binding.lightRain.setImageResource(EWeather.LIGHT_RAIN.getDrawable(weather))
        binding.rain.setImageResource(EWeather.RAIN.getDrawable(weather))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.environment_switch, menu)
        val item = menu.findItem(R.id.action_switch)
        switchView = item.actionView.findViewById(R.id.action_switch_control)
        switchView!!.setOnCheckedChangeListener { _, checked -> setOutdoor(checked) }
        setOutdoor(!environment!!.indoor)
        switchView!!.isChecked = !environment!!.indoor
    }

    private fun setOutdoor(checked: Boolean) {
        switchView!!.setText(if (checked) R.string.outdoor else R.string.indoor)
        binding.indoorPlaceholder.visibility = if (checked) GONE else VISIBLE
        binding.weatherLayout.visibility = if (checked) VISIBLE else GONE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        environment = saveItem()
        super.onSaveInstanceState(outState)
    }

    fun onSave() {
        val e = saveItem()
        SettingsManager.indoor = e.indoor
        navigationController.setResultSuccess(e)
        navigationController.finish()
    }

    private fun saveItem(): Environment {
        val e = Environment()
        e.indoor = !switchView!!.isChecked
        e.weather = environment!!.weather
        e.windSpeed = binding.windSpeed.selectedItem!!.id.toInt()
        e.windDirection = binding.windDirection.selectedItem!!.id.toInt()
        e.location = binding.location.text.toString()
        return e
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        binding.windSpeed.onActivityResult(requestCode, resultCode, data)
        binding.windDirection.onActivityResult(requestCode, resultCode, data)
    }
}
