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

package de.dreier.mytargets.base.navigation

import android.app.Activity
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.preference.PreferenceFragmentCompat
import de.dreier.mytargets.base.fragments.EditableListFragmentBase
import de.dreier.mytargets.base.gallery.GalleryActivity
import de.dreier.mytargets.features.arrows.EditArrowActivity
import de.dreier.mytargets.features.help.HelpActivity
import de.dreier.mytargets.features.settings.ESettingsScreens
import de.dreier.mytargets.features.settings.SettingsActivity
import de.dreier.mytargets.features.settings.about.AboutActivity
import de.dreier.mytargets.features.training.EditRoundActivity
import de.dreier.mytargets.shared.models.db.Training
import de.dreier.mytargets.shared.utils.ImageList
import de.dreier.mytargets.utils.IntentWrapper

class NavigationController(
        private val activity: Activity//,
//        private val fragmentManager: FragmentManager,
//        private val containerId: Int = R.id.container
) {

    constructor(fragment: Fragment) : this(fragment.activity!!)

    fun navigateToHelp(): IntentWrapper {
        return IntentWrapper(HelpActivity::class.java)
                .withContext(activity)
    }

    fun navigateToAbout() {
        IntentWrapper(AboutActivity::class.java)
                .withContext(activity)
                .start()
    }

    fun navigateToGallery(images: ImageList, title: String, requestCode: Int) {
        IntentWrapper(GalleryActivity::class.java)
                .with(GalleryActivity.EXTRA_TITLE, title)
                .with(GalleryActivity.EXTRA_IMAGES, images)
                .withContext(activity)
                .forResult(requestCode)
                .start()
    }

    fun navigateToSettings(subScreen: ESettingsScreens) {
        IntentWrapper(SettingsActivity::class.java)
                .with(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, subScreen.key)
                .withContext(activity)
                .start()
    }

    fun navigateToCreateRound(training: Training, fab: FloatingActionButton? = null) {
        val intentWrapper = IntentWrapper(EditRoundActivity::class.java)
                .with(EditableListFragmentBase.ITEM_ID, training.id)
                .withContext(activity)
        if(fab != null) {
            intentWrapper.fromFab(fab)
        }
        intentWrapper.start()
    }

    fun navigateToCreateArrow(): IntentWrapper {
        return IntentWrapper(EditArrowActivity::class.java)
    }

//
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
}
