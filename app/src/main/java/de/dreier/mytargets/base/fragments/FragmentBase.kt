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

package de.dreier.mytargets.base.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.UiThread
import android.support.annotation.WorkerThread
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.AsyncTaskLoader
import android.support.v4.content.Loader
import com.evernote.android.state.StateSaver
import de.dreier.mytargets.R
import de.dreier.mytargets.base.navigation.NavigationController
import de.dreier.mytargets.utils.Utils


typealias LoaderUICallback = () -> Unit

/**
 * Generic fragment class used as base for most fragments.
 * Has Icepick build in to save state on orientation change
 * and animates activity when #finish gets called.
 */
abstract class FragmentBase : Fragment(), LoaderManager.LoaderCallbacks<FragmentBase.LoaderUICallbackHelper> {

    protected lateinit var navigationController: NavigationController

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StateSaver.restoreInstanceState(this, savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        StateSaver.saveInstanceState(this, outState)
    }

    protected fun finish() {
        val activity = activity
        if (activity != null) {
            if (Utils.isLollipop) {
                activity.finishAfterTransition()
            } else {
                activity.finish()
                activity.overridePendingTransition(R.anim.left_in, R.anim.right_out)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navigationController = NavigationController(this)
        reloadData()
    }

    @SuppressLint("StaticFieldLeak")
    override fun onCreateLoader(id: Int, args: Bundle?): Loader<LoaderUICallbackHelper>? {
        val callback = onLoad(args)
        return  object : AsyncTaskLoader<LoaderUICallbackHelper>(context!!) {
            override fun loadInBackground(): LoaderUICallbackHelper? {
                return object : LoaderUICallbackHelper {
                    override fun applyData() {
                        callback.invoke()
                    }
                }
            }
        }
    }

    @WorkerThread
    protected open fun onLoad(args: Bundle?): LoaderUICallback {
        return { }
    }

    override fun onLoadFinished(loader: Loader<LoaderUICallbackHelper>, callback: LoaderUICallbackHelper) {
        callback.applyData()
    }

    override fun onLoaderReset(loader: Loader<LoaderUICallbackHelper>) {

    }

    protected fun reloadData() {
        if (loaderManager.getLoader<Any>(LOADER_ID) != null) {
            loaderManager.destroyLoader(LOADER_ID)
        }
        loaderManager.restartLoader(LOADER_ID, null, this).forceLoad()
    }

    protected fun reloadData(args: Bundle) {
        if (loaderManager.getLoader<Any>(LOADER_ID) != null) {
            loaderManager.destroyLoader(LOADER_ID)
        }
        loaderManager.restartLoader(LOADER_ID, args, this).forceLoad()
    }

    companion object {
        private const val LOADER_ID = 0
    }

    interface LoaderUICallbackHelper {
        @UiThread
        fun applyData()
    }
}
