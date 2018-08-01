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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import android.support.wearable.activity.WearableActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.evernote.android.state.State
import com.evernote.android.state.StateSaver
import de.dreier.mytargets.databinding.ActivityRoundBinding
import de.dreier.mytargets.shared.models.TrainingInfo
import de.dreier.mytargets.shared.models.augmented.AugmentedEnd
import de.dreier.mytargets.shared.models.augmented.AugmentedRound
import de.dreier.mytargets.shared.views.EndView
import de.dreier.mytargets.shared.wearable.WearableClientBase.Companion.BROADCAST_TIMER_SETTINGS_FROM_REMOTE
import de.dreier.mytargets.utils.WearSettingsManager
import de.dreier.mytargets.utils.WearWearableClient
import de.dreier.mytargets.utils.WearWearableClient.Companion.BROADCAST_TRAINING_UPDATED
import java.text.DateFormat
import java.util.*

class RoundActivity : WearableActivity() {

    private lateinit var binding: ActivityRoundBinding

    @State
    internal lateinit var round: AugmentedRound

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BROADCAST_TRAINING_UPDATED -> {
                    val (_, _, round1) = intent.getParcelableExtra<TrainingInfo>(WearWearableClient.EXTRA_INFO)
                    round = round1
                    showRoundData()
                }
                BROADCAST_TIMER_SETTINGS_FROM_REMOTE -> applyTimerState()
                else -> {
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_round)

        setAmbientEnabled()

        StateSaver.restoreInstanceState(this, savedInstanceState)
        if (savedInstanceState == null) {
            val intent = intent
            if (intent != null && intent.extras != null) {
                round = intent.getParcelableExtra(EXTRA_ROUND)
            }
        }

        showRoundData()

        binding.wearableDrawerView.controller.peekDrawer()

        // Replaces the on click behaviour that open the (empty) drawer
        val peekView = binding.primaryActionTimer.parent as LinearLayout
        val peekContainer = peekView.parent as ViewGroup
        peekContainer.setOnClickListener { toggleTimer() }
        applyTimerState()

        val filter = IntentFilter()
        filter.addAction(BROADCAST_TRAINING_UPDATED)
        filter.addAction(BROADCAST_TIMER_SETTINGS_FROM_REMOTE)
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter)
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        StateSaver.saveInstanceState(this, outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }

    override fun onEnterAmbient(ambientDetails: Bundle?) {
        super.onEnterAmbient(ambientDetails)
        binding.drawerLayout.setBackgroundResource(R.color.md_black_1000)
        binding.recyclerViewEnds.adapter.notifyDataSetChanged()
        binding.wearableDrawerView.visibility = View.INVISIBLE
        binding.clock!!.time.visibility = View.VISIBLE
        binding.clock!!.time.text = DateFormat.getTimeInstance(DateFormat.SHORT).format(Date())
    }

    override fun onUpdateAmbient() {
        super.onUpdateAmbient()
        binding.clock!!.time.text = DateFormat.getTimeInstance(DateFormat.SHORT).format(Date())
    }

    override fun onExitAmbient() {
        super.onExitAmbient()
        binding.drawerLayout.setBackgroundResource(R.color.md_wear_green_dark_background)
        binding.recyclerViewEnds.adapter.notifyDataSetChanged()
        binding.wearableDrawerView.visibility = View.VISIBLE
        binding.clock!!.time.visibility = View.GONE
    }

    private fun showRoundData() {
        val showAddEnd = round.round.maxEndCount == null || round.round.maxEndCount!! > round.ends.size
        binding.recyclerViewEnds.adapter = EndAdapter(round.ends, showAddEnd)
        binding.recyclerViewEnds.scrollToPosition(round.ends.size)
    }

    private fun addEnd() {
        val intent = Intent(this, InputActivity::class.java)
        intent.putExtra(InputActivity.EXTRA_ROUND, round)
        intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        startActivity(intent)
        val timerSettings = WearSettingsManager.timerSettings
        if (timerSettings.enabled) {
            val intentTimer = Intent(this, TimerActivity::class.java)
            intentTimer.putExtra(TimerActivity.EXTRA_TIMER_SETTINGS, timerSettings)
            startActivity(intentTimer)
        }
    }

    private fun toggleTimer() {
        val timerSettings = WearSettingsManager.timerSettings
        timerSettings.enabled = !timerSettings.enabled
        ApplicationInstance.wearableClient.sendTimerSettingsFromLocal(timerSettings)
        applyTimerState()
    }

    private fun applyTimerState() {
        val timerSettings = WearSettingsManager.timerSettings
        binding.primaryActionTimer.setImageResource(
                if (timerSettings.enabled)
                    R.drawable.ic_traffic_white_24dp
                else
                    R.drawable.ic_timer_off_white_24dp)
    }

    private inner class EndAdapter(private val ends: List<AugmentedEnd>, private val showAddEnd: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return if (viewType == 0) {
                val view = inflater.inflate(R.layout.item_end, parent, false)
                ViewHolder(view)
            } else {
                val view = inflater.inflate(R.layout.item_inline_button, parent, false)
                InlineButtonViewHolder(view)
            }
        }

        override fun getItemViewType(position: Int): Int {
            return if (position == ends.size) 1 else 0
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is ViewHolder) {
                val end = ends[position]
                holder.end.text = getString(R.string.end_n, end.end.index + 1)
                holder.shots.setShots(round.round.target, end.shots)

                holder.end.setTextColor(ContextCompat.getColor(this@RoundActivity,
                        if (isAmbient)
                            R.color.md_white_1000
                        else
                            R.color.md_wear_green_active_ui_element))
                holder.shots.setAmbientMode(isAmbient)
                holder.itemView.setBackgroundColor(ContextCompat.getColor(this@RoundActivity,
                        if (isAmbient)
                            R.color.md_black_1000
                        else
                            R.color.md_wear_green_lighter_background))
            } else if (holder is InlineButtonViewHolder) {
                holder.itemView.visibility = if (isAmbient) View.INVISIBLE else View.VISIBLE
            }
        }

        override fun getItemCount(): Int {
            return ends.size + if (showAddEnd) 1 else 0
        }
    }

    private inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val end: TextView = itemView.findViewById(R.id.end)
        val shots: EndView = itemView.findViewById(R.id.shoots)
    }

    private inner class InlineButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener { addEnd() }
        }
    }

    companion object {
        const val EXTRA_ROUND = "round"
    }
}
