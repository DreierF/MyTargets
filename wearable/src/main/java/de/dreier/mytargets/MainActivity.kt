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

package de.dreier.mytargets

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.wearable.activity.WearableActivity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import de.dreier.mytargets.databinding.ActivityMainBinding
import de.dreier.mytargets.shared.models.TrainingInfo
import de.dreier.mytargets.shared.models.augmented.AugmentedTraining
import de.dreier.mytargets.utils.WearWearableClient
import de.dreier.mytargets.utils.WearWearableClient.BROADCAST_TRAINING_TEMPLATE
import de.dreier.mytargets.utils.WearWearableClient.BROADCAST_TRAINING_UPDATED
import java.text.DateFormat
import java.util.*

class MainActivity : WearableActivity() {

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BROADCAST_TRAINING_TEMPLATE -> {
                    val training = intent.getParcelableExtra<AugmentedTraining>(WearWearableClient.EXTRA_TRAINING)
                    trainingTemplateReceived(training)
                }
                BROADCAST_TRAINING_UPDATED -> {
                    val info = intent.getParcelableExtra<TrainingInfo>(WearWearableClient.EXTRA_INFO)
                    trainingUpdated(info)
                }
                else -> {}
            }
        }
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setAmbientEnabled()

        val filter = IntentFilter()
        filter.addAction(BROADCAST_TRAINING_TEMPLATE)
        filter.addAction(BROADCAST_TRAINING_UPDATED)
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, filter)
        ApplicationInstance.wearableClient.requestNewTrainingTemplate()
    }

    override fun onEnterAmbient(ambientDetails: Bundle?) {
        super.onEnterAmbient(ambientDetails)
        binding.drawerLayout.setBackgroundResource(R.color.md_black_1000)
        binding.wearableDrawerView.setBackgroundResource(R.color.md_black_1000)
        binding.date.setTextColor(ContextCompat.getColor(this, R.color.md_white_1000))
        binding.icon.visibility = View.INVISIBLE
        binding.clock.time.visibility = View.VISIBLE
        binding.clock.time.text = DateFormat.getTimeInstance(DateFormat.SHORT).format(Date())
    }

    override fun onUpdateAmbient() {
        super.onUpdateAmbient()
        binding.clock.time.text = DateFormat.getTimeInstance(DateFormat.SHORT).format(Date())
    }

    override fun onExitAmbient() {
        super.onExitAmbient()
        binding.drawerLayout.setBackgroundResource(R.color.md_wear_green_dark_background)
        binding.wearableDrawerView.setBackgroundResource(R.color.md_wear_green_lighter_ui_element)
        binding.date.setTextColor(ContextCompat
                .getColor(this, R.color.md_wear_green_lighter_ui_element))
        binding.icon.visibility = View.VISIBLE
        binding.clock.time.visibility = View.GONE
    }

    private fun trainingUpdated(info: TrainingInfo) {
        setTrainingInfo(info)
        binding.root.isClickable = true
        binding.root.setOnClickListener {
            val i = Intent(this@MainActivity, RoundActivity::class.java)
            i.putExtra(RoundActivity.EXTRA_ROUND, info.round)
            startActivity(i)
        }
        binding.wearableDrawerView.visibility = View.GONE
    }

    private fun trainingTemplateReceived(training: AugmentedTraining) {
        setTraining(training)
        binding.root.isClickable = false
        binding.wearableDrawerView.visibility = View.VISIBLE

        // Replaces the on click behaviour that open the (empty) drawer
        val peekView = binding.primaryActionAdd.parent as LinearLayout
        val peekContainer = peekView.parent as ViewGroup
        peekContainer.setOnClickListener {
            ApplicationInstance
                    .wearableClient
                    .sendCreateTraining(training)
        }
        binding.wearableDrawerView.controller.peekDrawer()
    }

    private fun setTrainingInfo(info: TrainingInfo) {
        setCommonTrainingInfo(info)
        binding.date.setText(R.string.today)
    }

    private fun setTraining(training: AugmentedTraining) {
        val round = training.rounds[0]
        val info = TrainingInfo(training, round)
        setCommonTrainingInfo(info)
        binding.date.text = ""
    }

    private fun setCommonTrainingInfo(info: TrainingInfo) {
        binding.title.text = info.title
        binding.rounds.text = info.getRoundDetails(this)
        binding.ends.text = info.getEndDetails(this)
        binding.distance.text = info.round.round.distance.toString()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }
}
