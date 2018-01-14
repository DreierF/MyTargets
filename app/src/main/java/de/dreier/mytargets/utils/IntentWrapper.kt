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

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.ColorRes
import android.support.v4.app.Fragment
import android.view.View
import de.dreier.mytargets.R
import de.dreier.mytargets.utils.transitions.FabTransform

class IntentWrapper(
        private var activity: Activity,
        var fragment: Fragment? = null,
        intentTargetClass: Class<*>
) {

    private val intent = Intent(activity, intentTargetClass)
    private var options: Bundle? = null
    private var requestCode: Int? = null
    private var animate = true

    fun with(key: String, value: Long): IntentWrapper {
        intent.putExtra(key, value)
        return this
    }

    fun with(key: String, value: Int): IntentWrapper {
        intent.putExtra(key, value)
        return this
    }

    fun with(key: String, value: Boolean): IntentWrapper {
        intent.putExtra(key, value)
        return this
    }

    fun with(key: String, value: String): IntentWrapper {
        intent.putExtra(key, value)
        return this
    }

    fun <T : Parcelable> with(key: String, value: T): IntentWrapper {
        intent.putExtra(key, value)
        return this
    }

    fun with(key: String, values: LongArray): IntentWrapper {
        intent.putExtra(key, values)
        return this
    }

    fun action(action: String): IntentWrapper {
        intent.action = action
        return this
    }

    @SuppressLint("NewApi")
    @JvmOverloads
    fun fromFab(fab: View, @ColorRes color: Int = R.color.colorAccent, icon: Int = R.drawable.ic_add_white_24dp): IntentWrapper {
        if (Utils.isLollipop) {
            fab.transitionName = fab.context.getString(R.string.transition_root_view)
            FabTransform.addExtras(intent, color, icon)
            val options = ActivityOptions
                    .makeSceneTransitionAnimation(getActivity(fab), fab,
                            fab.context.getString(R.string.transition_root_view))
            this.options = options.toBundle()
        }
        return this
    }

    private fun getActivity(view: View): Activity? {
        var context = view.context
        while (context is ContextWrapper) {
            if (context is Activity) {
                return context
            }
            context = context.baseContext
        }
        return null
    }

    fun forResult(requestCode: Int): IntentWrapper {
        this.requestCode = requestCode
        return this
    }

    fun noAnimation(): IntentWrapper {
        intent.flags = intent.flags or Intent.FLAG_ACTIVITY_NO_ANIMATION
        animate = false
        return this
    }

    fun clearTopSingleTop(): IntentWrapper {
        intent.addFlags(intent.flags or Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP)
        return this
    }

    fun start() {
        if (fragment == null) {
            start(activity)
        } else {
            start(fragment!!)
        }
        animate(activity)
    }

    private fun start(fragment: Fragment) {
        if (requestCode == null) {
            fragment.startActivity(intent, options)
        } else {
            fragment.startActivityForResult(intent, requestCode!!, options)
        }
    }

    private fun start(activity: Activity) {
        if (Utils.isLollipop) {
            if (requestCode == null) {
                activity.startActivity(intent, options)
            } else {
                activity.startActivityForResult(intent, requestCode!!, options)
            }
        } else {
            if (requestCode == null) {
                activity.startActivity(intent)
            } else {
                activity.startActivityForResult(intent, requestCode!!)
            }
        }
    }

    private fun animate(activity: Activity) {
        if (!Utils.isLollipop && animate) {
            activity.overridePendingTransition(R.anim.right_in, R.anim.left_out)
        }
    }

    fun build(): Intent {
        return intent
    }
}
