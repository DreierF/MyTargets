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

package de.dreier.mytargets.test.utils

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiSelector
import androidx.core.content.ContextCompat

object PermissionGranter {

    private const val PERMISSIONS_DIALOG_DELAY = 1000
    private const val GRANT_BUTTON_INDEX = 1

    fun allowPermissionsIfNeeded(activity: Activity, permissionNeeded: String) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !hasNeededPermission(activity,
                    permissionNeeded)) {
                sleep(PERMISSIONS_DIALOG_DELAY.toLong())
                val device = UiDevice.getInstance(getInstrumentation())
                val allowPermissions = device
                        .findObject(UiSelector().clickable(true).index(GRANT_BUTTON_INDEX))
                if (allowPermissions.exists()) {
                    allowPermissions.click()
                }
            }
        } catch (e: UiObjectNotFoundException) {
            println("There is no permissions dialog to interact with")
        }

    }

    private fun hasNeededPermission(activity: Activity, permissionNeeded: String): Boolean {
        val permissionStatus = ContextCompat.checkSelfPermission(activity, permissionNeeded)
        return permissionStatus == PackageManager.PERMISSION_GRANTED
    }

    private fun sleep(millis: Long) {
        try {
            Thread.sleep(millis)
        } catch (e: InterruptedException) {
            throw RuntimeException("Cannot execute Thread.sleep()")
        }

    }
}
