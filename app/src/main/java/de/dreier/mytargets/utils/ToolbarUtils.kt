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

package de.dreier.mytargets.utils

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import de.dreier.mytargets.R

object ToolbarUtils {

    fun showUpAsX(fragment: Fragment) {
        showUpAsX((fragment.activity as AppCompatActivity?)!!)
    }

    private fun showUpAsX(activity: AppCompatActivity) {
        val supportActionBar = activity.supportActionBar!!
        supportActionBar.setDisplayHomeAsUpEnabled(true)
        supportActionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp)
    }

    fun showHomeAsUp(fragment: Fragment) {
        showHomeAsUp((fragment.activity as AppCompatActivity?)!!)
    }

    fun showHomeAsUp(activity: AppCompatActivity) {
        val supportActionBar = activity.supportActionBar!!
        supportActionBar.setDisplayHomeAsUpEnabled(true)
    }

    fun setSupportActionBar(fragment: Fragment, toolbar: Toolbar) {
        val activity = fragment.activity as AppCompatActivity?
        activity!!.setSupportActionBar(toolbar)
    }

    fun setTitle(fragment: Fragment, @StringRes title: Int) {
        setTitle((fragment.activity as AppCompatActivity?)!!, title)
    }

    fun setTitle(fragment: Fragment, title: String) {
        setTitle((fragment.activity as AppCompatActivity?)!!, title)
    }

    fun setTitle(activity: AppCompatActivity, @StringRes title: Int) {
        assert(activity.supportActionBar != null)
        activity.supportActionBar!!.setTitle(title)
    }

    fun setTitle(activity: AppCompatActivity, title: String) {
        assert(activity.supportActionBar != null)
        activity.supportActionBar!!.title = title
    }

    fun setSubtitle(fragment: Fragment, subtitle: String) {
        val activity = fragment.activity as AppCompatActivity?
        setSubtitle(activity!!, subtitle)
    }

    fun setSubtitle(activity: AppCompatActivity, subtitle: String) {
        assert(activity.supportActionBar != null)
        activity.supportActionBar!!.subtitle = subtitle
    }
}
