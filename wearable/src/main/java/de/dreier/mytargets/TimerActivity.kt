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
package de.dreier.mytargets

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import de.dreier.mytargets.databinding.ActivityTimerBinding
import de.dreier.mytargets.databinding.FragmentTimerBinding
import de.dreier.mytargets.shared.base.fragment.ETimerState
import de.dreier.mytargets.shared.base.fragment.TimerFragmentBase
import de.dreier.mytargets.shared.models.TimerSettings

/**
 * Demonstrates use of Navigation and Action Drawers on Android Wear.
 */
class TimerActivity : WearableActivity(), MenuItem.OnMenuItemClickListener {

    private lateinit var timerFragment: TimerFragment
    private lateinit var binding: ActivityTimerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timer)
        setAmbientEnabled()

        val settings = intent.getParcelableExtra<TimerSettings>(EXTRA_TIMER_SETTINGS)
        timerFragment = TimerFragment.getInstance(settings)
        val fragmentManager = fragmentManager
        fragmentManager.beginTransaction().replace(R.id.content_frame, timerFragment).commit()

        binding.primaryActionPeek
                .setOnClickListener { binding.wearableDrawerView.controller.openDrawer() }
        binding.bottomActionDrawer.setOnMenuItemClickListener(this)
        binding.bottomActionDrawer.menu.findItem(R.id.menu_vibrate)
                .setIcon(if (settings.vibrate)
                    R.drawable.ic_vibration_white_24dp
                else
                    R.drawable.ic_vibration_off_white_24dp)
        binding.bottomActionDrawer.menu.findItem(R.id.menu_sound)
                .setIcon(if (settings.sound)
                    R.drawable.ic_volume_up_white_24dp
                else
                    R.drawable.ic_volume_off_white_24dp)
        binding.wearableDrawerView.controller.peekDrawer()
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.menu_stop -> {
                finish()
                return true
            }
            R.id.menu_vibrate -> {
                timerFragment.settings.vibrate = !timerFragment.settings.vibrate
                menuItem.setIcon(if (timerFragment.settings.vibrate)
                    R.drawable.ic_vibration_white_24dp
                else
                    R.drawable.ic_vibration_off_white_24dp)
                ApplicationInstance.wearableClient
                        .sendTimerSettingsFromLocal(timerFragment.settings)
                return true
            }
            R.id.menu_sound -> {
                timerFragment.settings.sound = !timerFragment.settings.sound
                menuItem.setIcon(if (timerFragment.settings.sound)
                    R.drawable.ic_volume_up_white_24dp
                else
                    R.drawable.ic_volume_off_white_24dp)
                ApplicationInstance.wearableClient
                        .sendTimerSettingsFromLocal(timerFragment.settings)
                return true
            }
            else -> return false
        }
    }

    fun applyStatus(status: ETimerState) {
        binding.primaryActionPeek.setImageResource(if (status === ETimerState.WAIT_FOR_START)
            R.drawable.ic_more_vert_white_24dp
        else
            R.drawable.ic_stop_white_24dp)
        binding.primaryActionPeek.setOnClickListener(if (status === ETimerState.WAIT_FOR_START)
            null else View.OnClickListener { finish() })
    }

    /**
     * Fragment that appears in the "content_frame".
     */
    class TimerFragment : TimerFragmentBase() {

        private lateinit var binding: FragmentTimerBinding

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            binding = FragmentTimerBinding.inflate(inflater, container, false)
            binding.startTimer.setOnClickListener(this)
            return binding.root
        }

        override fun applyTime(text: String) {
            binding.timerTime.text = text
        }

        override fun applyStatus(status: ETimerState) {
            (activity as TimerActivity?)?.applyStatus(status)
            binding.startTimer.visibility = if (status === ETimerState.WAIT_FOR_START) VISIBLE else GONE
            binding.timerTime.visibility = if (status !== ETimerState.WAIT_FOR_START) VISIBLE else GONE
            binding.root.setBackgroundResource(status.color)
        }

        companion object {
            fun getInstance(settings: TimerSettings): TimerFragment {
                val timer = TimerFragment()
                val bundle = Bundle()
                bundle.putParcelable(TimerFragmentBase.Companion.ARG_TIMER_SETTINGS, settings)
                timer.arguments = bundle
                return timer
            }
        }
    }

    companion object {
        const val EXTRA_TIMER_SETTINGS = "timer_settings"
    }
}
