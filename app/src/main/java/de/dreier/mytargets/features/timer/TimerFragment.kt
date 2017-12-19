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

package de.dreier.mytargets.features.timer

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import de.dreier.mytargets.R
import de.dreier.mytargets.databinding.FragmentTimerBinding
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.shared.base.fragment.ETimerState
import de.dreier.mytargets.shared.base.fragment.ETimerState.*
import de.dreier.mytargets.shared.base.fragment.TimerFragmentBase
import de.dreier.mytargets.utils.IntentWrapper
import de.dreier.mytargets.utils.ToolbarUtils
import de.dreier.mytargets.utils.Utils

/**
 * Shows the archery timer
 */
class TimerFragment : TimerFragmentBase() {

    private lateinit var binding: FragmentTimerBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_timer, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val activity = activity as AppCompatActivity
        activity.setSupportActionBar(binding.toolbar)
        ToolbarUtils.showHomeAsUp(activity)
    }

    override fun applyTime(text: String) {
        binding.timerTime.text = text
    }

    override fun applyStatus(status: ETimerState) {
        binding.root.setBackgroundResource(status.color)
        if (Utils.isLollipop() && activity != null) {
            val window = activity.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(activity, status.color)
        }
        binding.timerStatus.setText(getStatusText(status))

        if (status === FINISHED) {
            binding.timerStatus.text = ""
        }
    }

    private fun getStatusText(state: ETimerState): Int {
        return when (state) {
            WAIT_FOR_START -> R.string.touch_to_start
            PREPARATION -> R.string.preparation
            SHOOTING, COUNTDOWN -> R.string.shooting
            FINISHED, EXIT -> R.string.stop
            else -> R.string.stop
        }
    }

    companion object {
        fun getIntent(exitAfterStop: Boolean): IntentWrapper {
            return IntentWrapper(TimerActivity::class.java)
                    .with(TimerFragmentBase.ARG_EXIT_AFTER_STOP, exitAfterStop)
                    .with(TimerFragmentBase.ARG_TIMER_SETTINGS, SettingsManager.timerSettings)
        }
    }
}
