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

import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.support.wearable.activity.ConfirmationActivity
import android.support.wearable.activity.WearableActivity
import android.view.View
import androidx.core.content.systemService
import de.dreier.mytargets.databinding.ActivityInputBinding
import de.dreier.mytargets.shared.models.augmented.AugmentedEnd
import de.dreier.mytargets.shared.models.augmented.AugmentedRound
import de.dreier.mytargets.shared.models.db.End
import de.dreier.mytargets.shared.models.db.Shot
import de.dreier.mytargets.shared.utils.VibratorCompat
import de.dreier.mytargets.shared.views.TargetViewBase
import java.text.DateFormat
import java.util.*

class InputActivity : WearableActivity(), TargetViewBase.OnEndFinishedListener {
    private lateinit var round: AugmentedRound
    private lateinit var binding: ActivityInputBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_input)

        setAmbientEnabled()

        round = intent!!.getParcelableExtra(EXTRA_ROUND)

        // Workaround to avoid crash happening when setting invisible via xml layout
        binding.circularProgress.visibility = View.INVISIBLE

        // Set up target view
        binding.target.initWithTarget(round.round.target)
        binding.target.replaceWithEnd((0 until round.round.shotsPerEnd)
                .map { Shot(it) }.toMutableList(), false)
        binding.target.setOnTargetSetListener(this)

        // Ensure Moto 360 is not cut off at the bottom
        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            val chinHeight = insets.systemWindowInsetBottom
            binding.target.setChinHeight(chinHeight)
            insets
        }
    }

    override fun onEnterAmbient(ambientDetails: Bundle?) {
        super.onEnterAmbient(ambientDetails)
        binding.target.setBackgroundResource(R.color.md_black_1000)
        binding.target.setAmbientMode(true)
        binding.clock!!.time.visibility = View.VISIBLE
        binding.clock!!.time.text = DateFormat.getTimeInstance(DateFormat.SHORT).format(Date())
    }

    override fun onUpdateAmbient() {
        super.onUpdateAmbient()
        binding.clock!!.time.text = DateFormat.getTimeInstance(DateFormat.SHORT).format(Date())
    }

    override fun onExitAmbient() {
        super.onExitAmbient()
        binding.target.setBackgroundResource(R.color.md_wear_green_lighter_background)
        binding.target.setAmbientMode(false)
        binding.clock!!.time.visibility = View.GONE
    }

    override fun onEndFinished(shotList: List<Shot>) {
        binding.circularProgress.visibility = View.VISIBLE
        binding.circularProgress.totalTime = 2500
        binding.circularProgress.startTimer()
        binding.circularProgress.setOnClickListener {
            binding.circularProgress.visibility = View.INVISIBLE
            binding.circularProgress.stopTimer()
        }
        binding.circularProgress.setOnTimerFinishedListener {
            val intent = Intent(this@InputActivity, ConfirmationActivity::class.java)
            intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                    ConfirmationActivity.SUCCESS_ANIMATION)
            intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, this@InputActivity
                    .getString(R.string.saved))
            this@InputActivity.startActivity(intent)
            VibratorCompat.vibrate(systemService(), 200)
            this@InputActivity.finish()
            val end = End(index = 0, roundId = round.round.id)
            val ae = AugmentedEnd(end, shotList.toMutableList(), mutableListOf())
            ApplicationInstance.wearableClient.sendEndUpdate(ae)
        }
    }

    companion object {
        const val EXTRA_ROUND = "round"
    }
}
