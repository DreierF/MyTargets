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

package de.dreier.mytargets.features.training.environment

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

internal object Connectivity {

    /**
     * Get the network info
     */
    private fun getNetworkInfo(context: Context): NetworkInfo? {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo
    }

    /**
     * Check if there is any connectivity
     */
    fun isConnected(context: Context): Boolean {
        return getNetworkInfo(context)?.isConnected ?: false
    }

    /**
     * Check if there is any connectivity to a mobile network
     */
    fun isConnectedMobile(context: Context): Boolean {
        val info = Connectivity.getNetworkInfo(context)
        return info != null && info.isConnected && info
                .type == ConnectivityManager.TYPE_MOBILE
    }

}
