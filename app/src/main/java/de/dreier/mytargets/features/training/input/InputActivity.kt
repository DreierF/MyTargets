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

package de.dreier.mytargets.features.training.input

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.LoaderManager
import android.support.v4.content.AsyncTaskLoader
import android.support.v4.content.ContextCompat
import android.support.v4.content.Loader
import android.support.v4.content.LocalBroadcastManager
import android.text.InputType
import android.transition.Transition
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import com.afollestad.materialdialogs.MaterialDialog
import com.evernote.android.state.State
import de.dreier.mytargets.R
import de.dreier.mytargets.app.ApplicationInstance
import de.dreier.mytargets.base.activities.ChildActivityBase
import de.dreier.mytargets.base.db.EndRepository
import de.dreier.mytargets.base.db.RoundRepository
import de.dreier.mytargets.base.db.TrainingRepository
import de.dreier.mytargets.base.db.dao.ArrowDAO
import de.dreier.mytargets.base.db.dao.BowDAO
import de.dreier.mytargets.base.db.dao.StandardRoundDAO
import de.dreier.mytargets.base.gallery.GalleryActivity
import de.dreier.mytargets.databinding.ActivityInputBinding
import de.dreier.mytargets.features.settings.ESettingsScreens
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.shared.models.db.End
import de.dreier.mytargets.shared.models.db.Round
import de.dreier.mytargets.shared.models.db.Shot
import de.dreier.mytargets.shared.models.sum
import de.dreier.mytargets.shared.views.TargetViewBase
import de.dreier.mytargets.shared.views.TargetViewBase.EInputMethod
import de.dreier.mytargets.shared.wearable.WearableClientBase.Companion.BROADCAST_TIMER_SETTINGS_FROM_REMOTE
import de.dreier.mytargets.utils.*
import de.dreier.mytargets.utils.MobileWearableClient.Companion.BROADCAST_UPDATE_TRAINING_FROM_REMOTE
import de.dreier.mytargets.utils.Utils.getCurrentLocale
import de.dreier.mytargets.utils.transitions.FabTransform
import de.dreier.mytargets.utils.transitions.TransitionAdapter
import org.threeten.bp.LocalTime
import java.io.File

class InputActivity : ChildActivityBase(), TargetViewBase.OnEndFinishedListener,
    TargetView.OnEndUpdatedListener, LoaderManager.LoaderCallbacks<LoaderResult> {

    @State
    var data: LoaderResult? = null

    private lateinit var binding: ActivityInputBinding
    private var transitionFinished = true
    private var summaryShowScope = ETrainingScope.END
    private var targetView: TargetView? = null

    private val database = ApplicationInstance.db
    private val trainingDAO = database.trainingDAO()
    private val roundDAO = database.roundDAO()
    private val endDAO = database.endDAO()
    private val bowDAO = database.bowDAO()
    private val arrowDAO = database.arrowDAO()
    private val standardRoundDAO = database.standardRoundDAO()
    private val roundRepository = RoundRepository(database)
    private val trainingRepository = TrainingRepository(
        database,
        trainingDAO,
        roundDAO,
        roundRepository,
        database.signatureDAO()
    )

    private val updateReceiver = object : MobileWearableClient.EndUpdateReceiver() {

        override fun onUpdate(trainingId: Long, roundId: Long, end: End) {
            val extras = intent.extras
            extras.putLong(TRAINING_ID, trainingId)
            extras.putLong(ROUND_ID, roundId)
            extras.putInt(END_INDEX, end.index)
            supportLoaderManager.restartLoader(0, extras, this@InputActivity).forceLoad()
        }
    }

    private val timerReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            invalidateOptionsMenu()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_input)
        setSupportActionBar(binding.toolbar)
        ToolbarUtils.showHomeAsUp(this)
        Utils.setupFabTransform(this, binding.root)

        if (Utils.isLollipop) {
            setupTransitionListener()
        }

        updateSummaryVisibility()

        if (data == null) {
            supportLoaderManager.initLoader(0, intent.extras, this).forceLoad()
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(
            updateReceiver,
            IntentFilter(BROADCAST_UPDATE_TRAINING_FROM_REMOTE)
        )
        LocalBroadcastManager.getInstance(this).registerReceiver(
            timerReceiver,
            IntentFilter(BROADCAST_TIMER_SETTINGS_FROM_REMOTE)
        )
    }

    override fun onResume() {
        super.onResume()
        if (data != null) {
            onDataLoadFinished()
            updateEnd()
        }
        Utils.setShowWhenLocked(this, SettingsManager.inputKeepAboveLockscreen)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val imageList = GalleryActivity.getResult(data)
            val currentEnd = this.data!!.currentEnd
            currentEnd.images = imageList.toEndImageList()
            for (image in imageList.removedImages) {
                File(filesDir, image).delete()
            }
            endDAO.replaceImages(currentEnd.end, currentEnd.images)
            updateEnd()
            invalidateOptionsMenu()
        }
    }

    private fun saveCurrentEnd() {
        val currentEnd = data!!.currentEnd
        if (currentEnd.end.saveTime == null) {
            currentEnd.end.saveTime = LocalTime.now()
        }
        currentEnd.end.score = data!!.currentRound.round.target.getReachedScore(currentEnd.shots)
        endDAO.updateEnd(currentEnd.end)
        endDAO.updateShots(currentEnd.shots)
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(updateReceiver)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(timerReceiver)
        super.onDestroy()
    }

    private fun updateSummaryVisibility() {
        val (showEnd, showRound, showTraining, showAverage, averageScope) = SettingsManager.inputSummaryConfiguration
        binding.endSummary.visibility = if (showEnd) VISIBLE else GONE
        binding.roundSummary.visibility = if (showRound) VISIBLE else GONE
        binding.trainingSummary.visibility = if (showTraining) VISIBLE else GONE
        binding.averageSummary.visibility = if (showAverage) VISIBLE else GONE
        summaryShowScope = averageScope
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun setupTransitionListener() {
        val sharedElementEnterTransition = window
            .sharedElementEnterTransition
        if (sharedElementEnterTransition != null && sharedElementEnterTransition is FabTransform) {
            transitionFinished = false
            window.sharedElementEnterTransition.addListener(object : TransitionAdapter() {
                override fun onTransitionEnd(transition: Transition) {
                    transitionFinished = true
                    window.sharedElementEnterTransition.removeListener(this)
                }
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.input_end, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val timer = menu.findItem(R.id.action_timer)
        val newRound = menu.findItem(R.id.action_new_round)
        val takePicture = menu.findItem(R.id.action_photo)
        if (targetView == null || data!!.ends.size == 0) {
            takePicture.isVisible = false
            timer.isVisible = false
            newRound.isVisible = false
        } else {
            takePicture.isVisible = Utils.hasCameraHardware(this)
            timer.setIcon(
                if (SettingsManager.timerEnabled)
                    R.drawable.ic_timer_off_white_24dp
                else
                    R.drawable.ic_timer_white_24dp
            )
            timer.isVisible = true
            timer.isChecked = SettingsManager.timerEnabled
            newRound.isVisible = data!!.training.training.standardRoundId == null
            takePicture.isVisible = Utils.hasCameraHardware(this)
            takePicture.setIcon(
                if (data!!.currentEnd.images.isEmpty())
                    R.drawable.ic_photo_camera_white_24dp
                else
                    R.drawable.ic_image_white_24dp
            )
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_photo -> {
                val imageList = ImageList(data!!.currentEnd.images)
                val title = getString(R.string.end_n, data!!.endIndex + 1)
                navigationController.navigateToGallery(imageList, title, GALLERY_REQUEST_CODE)
            }
            R.id.action_comment -> {
                MaterialDialog.Builder(this)
                    .title(R.string.comment)
                    .inputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                    .input("", data!!.currentEnd.end.comment) { _, input ->
                        data!!.currentEnd.end.comment = input.toString()
                        endDAO.updateEnd(data!!.currentEnd.end)
                    }
                    .negativeText(android.R.string.cancel)
                    .show()
            }
            R.id.action_timer -> {
                val timerEnabled = !SettingsManager.timerEnabled
                SettingsManager.timerEnabled = timerEnabled
                ApplicationInstance.wearableClient.sendTimerSettingsFromLocal(SettingsManager.timerSettings)
                openTimer()
                item.isChecked = timerEnabled
                invalidateOptionsMenu()
            }
            R.id.action_settings -> navigationController.navigateToSettings(ESettingsScreens.INPUT)
            R.id.action_new_round -> navigationController.navigateToCreateRound(trainingId = data!!.training.training.id)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<LoaderResult> {
        if (args == null) {
            throw IllegalArgumentException("Bundle expected")
        }
        val trainingId = args.getLong(TRAINING_ID)
        val roundId = args.getLong(ROUND_ID)
        val endIndex = args.getInt(END_INDEX)
        return UITaskAsyncTaskLoader(
            this,
            trainingId,
            roundId,
            endIndex,
            trainingRepository,
            standardRoundDAO,
            arrowDAO,
            bowDAO
        )
    }

    override fun onLoadFinished(loader: Loader<LoaderResult>, data: LoaderResult) {
        this.data = data
        onDataLoadFinished()
        showEnd(data.endIndex)
    }

    private fun onDataLoadFinished() {
        title = data!!.training.training.title
        if (!binding.targetViewStub.isInflated) {
            binding.targetViewStub.viewStub?.inflate()
        }
        targetView = binding.targetViewStub.binding?.root as TargetView
        targetView!!.initWithTarget(data!!.currentRound.round.target)
        targetView!!.setArrow(
            data!!.arrowDiameter, data!!.training.training.arrowNumbering, data!!
                .maxArrowNumber
        )
        targetView!!.setOnTargetSetListener(this@InputActivity)
        targetView!!.setUpdateListener(this@InputActivity)
        targetView!!.reloadSettings()
        targetView!!.setAggregationStrategy(SettingsManager.aggregationStrategy)
        targetView!!.inputMethod = SettingsManager.inputMethod
        updateOldShoots()
    }

    override fun onLoaderReset(loader: Loader<LoaderResult>) {

    }

    private fun showEnd(endIndex: Int) {
        // Create a new end
        data!!.setAdjustEndIndex(endIndex)
        if (endIndex >= data!!.ends.size) {
            val end = data!!.currentRound.addEnd()
            end.end.exact = SettingsManager.inputMethod === EInputMethod.PLOTTING
            updateOldShoots()
        }

        // Open timer if end has not been saved yet
        openTimer()
        updateEnd()
        invalidateOptionsMenu()
    }

    private fun updateOldShoots() {
        val currentEnd = data!!.currentEnd
        val currentRoundId = data!!.currentRound.round.id
        val currentEndId = currentEnd.end.id
        val shotShowScope = SettingsManager.showMode
        val data = this.data
        val shots = data!!.training.rounds
            .filter { r -> shouldShowRound(r.round, shotShowScope, currentRoundId) }
            .flatMap { r -> r.ends }
            .filter { end -> shouldShowEnd(end.end, currentEndId) }
            .flatMap { (_, shots) -> shots }
        targetView!!.setTransparentShots(shots)
    }

    private fun openTimer() {
        if (data!!.currentEnd.isEmpty && SettingsManager.timerEnabled) {
            if (transitionFinished) {
                navigationController.navigateToTimer(true)
            } else if (Utils.isLollipop) {
                startTimerDelayed()
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun startTimerDelayed() {
        window.sharedElementEnterTransition.addListener(object : TransitionAdapter() {
            override fun onTransitionEnd(transition: Transition) {
                navigationController.navigateToTimer(true)
                window.sharedElementEnterTransition.removeListener(this)
            }
        })
    }

    private fun updateEnd() {
        targetView?.replaceWithEnd(data!!.currentEnd.shots, data!!.currentEnd.end.exact)
        val totalEnds = if (data!!.currentRound.round.maxEndCount == null)
            data!!.ends.size
        else
            data!!.currentRound.round.maxEndCount
        binding.endTitle.text = getString(R.string.end_x_of_y, data!!.endIndex + 1, totalEnds)
        binding.roundTitle.text = getString(
            R.string.round_x_of_y,
            data!!.currentRound.round.index + 1,
            data!!.training.rounds.size
        )
        updateNavigationButtons()
        updateWearNotification()
    }

    private fun updateWearNotification() {
        ApplicationInstance.wearableClient.sendUpdateTrainingFromLocalBroadcast(data!!.training)
    }

    private fun updateNavigationButtons() {
        updatePreviousButton()
        updateNextButton()
    }

    private fun updatePreviousButton() {
        val isFirstEnd = data!!.endIndex == 0
        val isFirstRound = data!!.roundIndex == 0
        val showPreviousRound = isFirstEnd && !isFirstRound
        val isEnabled = !isFirstEnd || !isFirstRound
        val color: Int
        if (showPreviousRound) {
            val round = data!!.training.rounds[data!!.roundIndex - 1]
            binding.prev.setOnClickListener { openRound(round.round, round.ends.size - 1) }
            binding.prev.setText(R.string.previous_round)
            color = ContextCompat.getColor(this, R.color.colorPrimary)
        } else {
            binding.prev.setOnClickListener { showEnd(data!!.endIndex - 1) }
            binding.prev.setText(R.string.prev)
            color = Color.BLACK
        }
        binding.prev.setTextColor(Utils.argb(if (isEnabled) 0xFF else 0x42, color))
        binding.prev.isEnabled = isEnabled
    }

    private fun updateNextButton() {
        val dataLoaded = data != null
        val isLastEnd = dataLoaded &&
                data!!.currentRound.round.maxEndCount != null &&
                data!!.endIndex + 1 == data!!.currentRound.round.maxEndCount
        val hasOneMoreRound = dataLoaded && data!!.roundIndex + 1 < data!!.training.rounds.size
        val showNextRound = isLastEnd && hasOneMoreRound
        val isEnabled = dataLoaded && (!isLastEnd || hasOneMoreRound)
        val color: Int
        if (showNextRound) {
            val round = data!!.training.rounds[data!!.roundIndex + 1].round
            binding.next.setOnClickListener { openRound(round, 0) }
            binding.next.setText(R.string.next_round)
            color = ContextCompat.getColor(this, R.color.colorPrimary)
        } else {
            binding.next.setOnClickListener { showEnd(data!!.endIndex + 1) }
            binding.next.setText(R.string.next)
            color = Color.BLACK
        }
        binding.next.setTextColor(Utils.argb(if (isEnabled) 0xFF else 0x42, color))
        binding.next.isEnabled = isEnabled
    }

    private fun openRound(round: Round, endIndex: Int) {
        finish()
        navigationController.navigateToRound(round)
            .noAnimation()
            .start()
        navigationController.navigateToEditEnd(round, endIndex)
            .start()
    }

    override fun onEndUpdated(shots: List<Shot>) {
        data!!.currentEnd.shots = shots.toMutableList()
        saveCurrentEnd()

        // Set current end score
        val reachedEndScore = data!!.currentRound.round.target
            .getReachedScore(data!!.currentEnd.shots)
        binding.endScore.text = reachedEndScore.toString()

        // Set current round score
        val reachedRoundScore = data!!.ends
            .map { end -> data!!.currentRound.round.target.getReachedScore(end.shots) }
            .sum()
        binding.roundScore.text = reachedRoundScore.toString()

        // Set current training score
        val reachedTrainingScore = data!!.training.rounds
            .flatMap { r -> r.ends.map { end -> r.round.target.getReachedScore(end.shots) } }
            .sum()
        binding.trainingScore.text = reachedTrainingScore.toString()

        when (summaryShowScope) {
            ETrainingScope.END -> binding.averageScore.text =
                    reachedEndScore.getShotAverageFormatted(getCurrentLocale(this))
            ETrainingScope.ROUND -> binding.averageScore.text =
                    reachedRoundScore.getShotAverageFormatted(getCurrentLocale(this))
            ETrainingScope.TRAINING -> binding.averageScore.text = reachedTrainingScore
                .getShotAverageFormatted(getCurrentLocale(this))
        }
    }

    override fun onEndFinished(shotList: List<Shot>) {
        data!!.currentEnd.shots = shotList.toMutableList()
        data!!.currentEnd.end.exact = targetView!!.inputMode === EInputMethod.PLOTTING
        saveCurrentEnd()

        updateWearNotification()
        updateNavigationButtons()
        invalidateOptionsMenu()
    }

    private class UITaskAsyncTaskLoader(
        context: Context,
        private val trainingId: Long,
        private val roundId: Long,
        private val endIndex: Int,
        val trainingRepository: TrainingRepository,
        val standardRoundDAO: StandardRoundDAO,
        val arrowDAO: ArrowDAO,
        val bowDAO: BowDAO
    ) : AsyncTaskLoader<LoaderResult>(context) {

        override fun loadInBackground(): LoaderResult? {
            val training = trainingRepository.loadAugmentedTraining(trainingId)
            val standardRound =
                if (training.training.standardRoundId == null) null else standardRoundDAO.loadStandardRound(
                    training.training.standardRoundId!!
                )
            val result = LoaderResult(training, standardRound)
            result.setRoundId(roundId)
            result.setAdjustEndIndex(endIndex)

            if (training.training.arrowId != null) {
                val arrow = arrowDAO.loadArrow(training.training.arrowId!!)
                result.maxArrowNumber = arrow.maxArrowNumber
                result.arrowDiameter = arrow.diameter
            }
            if (training.training.bowId != null) {
                result.sightMark = bowDAO.loadSightMarks(training.training.bowId!!)
                    .firstOrNull { it.distance == result.distance!! }
            }
            return result
        }
    }

    companion object {
        internal const val TRAINING_ID = "training_id"
        internal const val ROUND_ID = "round_id"
        internal const val END_INDEX = "end_ind"
        internal const val GALLERY_REQUEST_CODE = 1

        private fun shouldShowRound(
            r: Round,
            shotShowScope: ETrainingScope,
            roundId: Long?
        ): Boolean {
            return shotShowScope !== ETrainingScope.END && (shotShowScope === ETrainingScope.TRAINING || r.id == roundId)
        }

        private fun shouldShowEnd(end: End, currentEndId: Long?): Boolean {
            return end.id != currentEndId && end.exact
        }
    }
}
