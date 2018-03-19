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

package de.dreier.mytargets.base.navigation

import android.app.Activity
import android.content.Intent
import android.os.Parcelable
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceFragmentCompat
import de.dreier.mytargets.R
import de.dreier.mytargets.base.fragments.EditableListFragmentBase
import de.dreier.mytargets.base.gallery.GalleryActivity
import de.dreier.mytargets.features.arrows.ArrowListActivity
import de.dreier.mytargets.features.arrows.EditArrowActivity
import de.dreier.mytargets.features.arrows.EditArrowFragment
import de.dreier.mytargets.features.bows.BowListActivity
import de.dreier.mytargets.features.bows.EditBowActivity
import de.dreier.mytargets.features.bows.EditBowFragment
import de.dreier.mytargets.features.distance.DistanceActivity
import de.dreier.mytargets.features.help.HelpActivity
import de.dreier.mytargets.features.rounds.EditRoundFragment
import de.dreier.mytargets.features.scoreboard.ScoreboardActivity
import de.dreier.mytargets.features.settings.ESettingsScreens
import de.dreier.mytargets.features.settings.SettingsActivity
import de.dreier.mytargets.features.settings.SettingsManager
import de.dreier.mytargets.features.settings.about.AboutActivity
import de.dreier.mytargets.features.statistics.ArrowStatistic
import de.dreier.mytargets.features.statistics.DispersionPatternActivity
import de.dreier.mytargets.features.statistics.StatisticsActivity
import de.dreier.mytargets.features.timer.TimerActivity
import de.dreier.mytargets.features.training.EditRoundActivity
import de.dreier.mytargets.features.training.RoundActivity
import de.dreier.mytargets.features.training.RoundFragment
import de.dreier.mytargets.features.training.TrainingActivity
import de.dreier.mytargets.features.training.edit.EditTrainingActivity
import de.dreier.mytargets.features.training.environment.EnvironmentActivity
import de.dreier.mytargets.features.training.environment.WindDirectionActivity
import de.dreier.mytargets.features.training.environment.WindSpeedActivity
import de.dreier.mytargets.features.training.input.InputActivity
import de.dreier.mytargets.features.training.standardround.EditStandardRoundActivity
import de.dreier.mytargets.features.training.standardround.StandardRoundActivity
import de.dreier.mytargets.features.training.target.TargetActivity
import de.dreier.mytargets.features.training.target.TargetListFragment
import de.dreier.mytargets.shared.base.fragment.TimerFragmentBase
import de.dreier.mytargets.shared.models.*
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.models.augmented.AugmentedStandardRound
import de.dreier.mytargets.shared.models.db.Arrow
import de.dreier.mytargets.shared.models.db.Bow
import de.dreier.mytargets.shared.models.db.Round
import de.dreier.mytargets.shared.models.db.Training
import de.dreier.mytargets.utils.ImageList
import de.dreier.mytargets.utils.IntentWrapper
import de.dreier.mytargets.utils.Utils
import de.dreier.mytargets.views.selector.*

class NavigationController(
        private val activity: AppCompatActivity,
        private val fragment: Fragment? = null//,
//        private val fragmentManager: FragmentManager,
//        private val containerId: Int = R.id.container
) {

    constructor(fragment: Fragment) : this(fragment.activity as AppCompatActivity, fragment)

    fun navigateToHelp() {
        return IntentWrapper(activity, fragment, HelpActivity::class.java)
                .start()
    }

    fun navigateToAbout() {
        IntentWrapper(activity, fragment, AboutActivity::class.java)
                .start()
    }

    fun navigateToGallery(images: ImageList, title: String, requestCode: Int) {
        IntentWrapper(activity, fragment, GalleryActivity::class.java)
                .with(GalleryActivity.EXTRA_TITLE, title)
                .with(GalleryActivity.EXTRA_IMAGES, images)
                .forResult(requestCode)
                .start()
    }

    fun navigateToSettings(subScreen: ESettingsScreens) {
        IntentWrapper(activity, fragment, SettingsActivity::class.java)
                .with(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, subScreen.key)
                .start()
    }

    fun navigateToCreateArrow(): IntentWrapper {
        return IntentWrapper(activity, fragment, EditArrowActivity::class.java)
    }

    fun navigateToEditArrow(arrowId: Long): IntentWrapper {
        return IntentWrapper(activity, fragment, EditArrowActivity::class.java)
                .with(EditArrowFragment.ARROW_ID, arrowId)
    }

    fun navigateToCreateBow(bowType: EBowType): IntentWrapper {
        return IntentWrapper(activity, fragment, EditBowActivity::class.java)
                .with(EditBowFragment.BOW_TYPE, bowType.name)
    }

    fun navigateToEditBow(bowId: Long) {
        IntentWrapper(activity, fragment, EditBowActivity::class.java)
                .with(EditBowFragment.BOW_ID, bowId)
                .start()
    }

    fun navigateToCreateTraining(trainingTypeAction: String): IntentWrapper {
        return IntentWrapper(activity, fragment, EditTrainingActivity::class.java)
                .action(trainingTypeAction)
    }

    fun navigateToEditTraining(trainingId: Long) {
        IntentWrapper(activity, fragment, EditTrainingActivity::class.java)
                .with(EditableListFragmentBase.ITEM_ID, trainingId)
                .start()
    }

    fun navigateToCreateRound(training: Training, fab: FloatingActionButton? = null) {
        val intentWrapper = IntentWrapper(activity, fragment, EditRoundActivity::class.java)
                .with(EditableListFragmentBase.ITEM_ID, training.id)
        if (fab != null) {
            intentWrapper.fromFab(fab)
        }
        intentWrapper.start()
    }

    fun navigateToEditRound(training: Training, roundId: Long) {
        IntentWrapper(activity, fragment, EditRoundActivity::class.java)
                .with(EditableListFragmentBase.ITEM_ID, training.id)
                .with(EditRoundFragment.ROUND_ID, roundId)
                .start()
    }

    fun navigateToScoreboard(trainingId: Long, roundId: Long = -1) {
        IntentWrapper(activity, fragment, ScoreboardActivity::class.java)
                .with(ScoreboardActivity.TRAINING_ID, trainingId)
                .with(ScoreboardActivity.ROUND_ID, roundId)
                .start()
    }

    fun navigateToDispersionPattern(statistics: ArrowStatistic) {
        IntentWrapper(activity, fragment, DispersionPatternActivity::class.java)
                .with(DispersionPatternActivity.ITEM, statistics)
                .start()
    }

    fun navigateToStatistics(roundIds: List<Long>) {
        IntentWrapper(activity, fragment, StatisticsActivity::class.java)
                .with(StatisticsActivity.ROUND_IDS, roundIds.toLongArray())
                .start()
    }

    fun navigateToTimer(exitAfterStop: Boolean) {
        IntentWrapper(activity, fragment, TimerActivity::class.java)
                .with(TimerFragmentBase.ARG_EXIT_AFTER_STOP, exitAfterStop)
                .with(TimerFragmentBase.ARG_TIMER_SETTINGS, SettingsManager.timerSettings)
                .start()
    }

    fun navigateToRound(round: Round): IntentWrapper {
        return IntentWrapper(activity, fragment, RoundActivity::class.java)
                .with(RoundFragment.ROUND_ID, round.id)
                .clearTopSingleTop()
    }

    fun navigateToTraining(training: Training): IntentWrapper {
        return IntentWrapper(activity, fragment, TrainingActivity::class.java)
                .with(EditableListFragmentBase.ITEM_ID, training.id)
    }

    fun navigateToCreateEnd(round: Round) {
        navigateToEditEnd(round, 0)
                .start()
    }

    fun navigateToEditEnd(round: Round, endIndex: Int): IntentWrapper {
        return IntentWrapper(activity, fragment, InputActivity::class.java)
                .with(InputActivity.TRAINING_ID, round.trainingId!!)
                .with(InputActivity.ROUND_ID, round.id)
                .with(InputActivity.END_INDEX, endIndex)
    }

    fun navigateToCreateStandardRound(): IntentWrapper {
        return IntentWrapper(activity, fragment, EditStandardRoundActivity::class.java)
    }

    fun navigateToEditStandardRound(item: AugmentedStandardRound): IntentWrapper {
        return IntentWrapper(activity, fragment, EditStandardRoundActivity::class.java)
                .with(ITEM, item)
    }

    fun navigateToDistance(item: Dimension, index: Int, requestCode: Int = DistanceSelector.DISTANCE_REQUEST_CODE) {
        IntentWrapper(activity, fragment, DistanceActivity::class.java)
                .with(ITEM, item)
                .with(SelectorBase.INDEX, index)
                .forResult(requestCode)
                .start()
    }

    fun navigateToTarget(item: Target, index: Int = -1, requestCode: Int = TargetSelector.TARGET_REQUEST_CODE, fixedType: TargetListFragment.EFixedType = TargetListFragment.EFixedType.NONE) {
        IntentWrapper(activity, fragment, TargetActivity::class.java)
                .with(ITEM, item)
                .with(SelectorBase.INDEX, index)
                .with(TargetListFragment.FIXED_TYPE, fixedType.name)
                .forResult(requestCode)
                .start()
    }

    fun navigateToStandardRoundList(currentSelection: AugmentedStandardRound, requestCode: Int = StandardRoundSelector.STANDARD_ROUND_REQUEST_CODE) {
        IntentWrapper(activity, fragment, StandardRoundActivity::class.java)
                .with(ITEM, currentSelection)
                .forResult(requestCode)
                .start()
    }

    fun navigateToArrowList(currentSelection: Arrow, requestCode: Int = ArrowSelector.ARROW_REQUEST_CODE) {
        IntentWrapper(activity, fragment, ArrowListActivity::class.java)
                .with(ITEM, currentSelection)
                .forResult(requestCode)
                .start()
    }

    fun navigateToBowList(currentSelection: Bow, requestCode: Int = BowSelector.BOW_REQUEST_CODE) {
        IntentWrapper(activity, fragment, BowListActivity::class.java)
                .with(ITEM, currentSelection)
                .forResult(requestCode)
                .start()
    }

    fun navigateToEnvironment(item: Environment, requestCode: Int = EnvironmentSelector.ENVIRONMENT_REQUEST_CODE) {
        IntentWrapper(activity, fragment, EnvironmentActivity::class.java)
                .with(ITEM, item)
                .forResult(requestCode)
                .start()
    }

    fun navigateToWindDirection(selectedItem: WindDirection, requestCode: Int = WindDirectionSelector.WIND_DIRECTION_REQUEST_CODE) {
        IntentWrapper(activity, fragment, WindDirectionActivity::class.java)
                .with(ITEM, selectedItem)
                .forResult(requestCode)
                .start()
    }

    fun navigateToWindSpeed(selectedItem: WindSpeed, requestCode: Int = WindSpeedSelector.WIND_SPEED_REQUEST_CODE) {
        IntentWrapper(activity, fragment, WindSpeedActivity::class.java)
                .with(ITEM, selectedItem)
                .forResult(requestCode)
                .start()
    }

    fun setResultSuccess() {
        activity.setResult(Activity.RESULT_OK)
    }

    fun setResultSuccess(intent: Intent) {
        activity.setResult(Activity.RESULT_OK, intent)
    }

    fun setResultSuccess(data: Parcelable) {
        val intent = Intent()
        intent.putExtra(ITEM, data)
        intent.putExtra(INTENT, activity.intent?.extras)
        setResultSuccess(intent)
    }

    fun setResult(resultCode: Int) {
        activity.setResult(resultCode)
    }

    fun finish(animate: Boolean = true) {
        if (fragment != null || !activity.supportFragmentManager.popBackStackImmediate()) {
            if (Utils.isLollipop) {
                if (animate) {
                    activity.finishAfterTransition()
                } else {
                    activity.finish()
                }
            } else {
                activity.finish()
                if(animate) {
                    activity.overridePendingTransition(R.anim.left_in, R.anim.right_out)
                }
            }
        }
    }

//    fun navigateToSearch() {
//        val searchFragment = SearchFragment()
//        fragmentManager.beginTransaction()
//                .replace(containerId, searchFragment)
//                .commitAllowingStateLoss()
//    }
//
//    fun navigateToRepo(owner: String, name: String) {
//        val fragment = RepoFragment.create(owner, name)
//        val tag = "repo/$owner/$name"
//        fragmentManager.beginTransaction()
//                .replace(containerId, fragment, tag)
//                .addToBackStack(null)
//                .commitAllowingStateLoss()
//    }
//
//    fun navigateToUser(login: String) {
//        val tag = "user" + "/" + login
//        val userFragment = UserFragment.create(login)
//        fragmentManager.beginTransaction()
//                .replace(containerId, userFragment, tag)
//                .addToBackStack(null)
//                .commitAllowingStateLoss()
//    }

    companion object {
        const val ITEM = "item"
        const val INTENT = "intent"
    }
}
