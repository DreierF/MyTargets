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

package androidx.core.content

import android.accessibilityservice.AccessibilityService
import android.accounts.AccountManager
import android.app.*
import android.app.admin.DevicePolicyManager
import android.app.job.JobScheduler
import android.app.usage.UsageStatsManager
import android.appwidget.AppWidgetManager
import android.bluetooth.BluetoothManager
import android.content.ClipboardManager
import android.content.Context
import android.content.RestrictionsManager
import android.content.pm.LauncherApps
import android.hardware.ConsumerIrManager
import android.hardware.SensorManager
import android.hardware.camera2.CameraManager
import android.hardware.display.DisplayManager
import android.hardware.input.InputManager
import android.hardware.usb.UsbManager
import android.location.LocationManager
import android.media.AudioManager
import android.media.MediaRouter
import android.media.projection.MediaProjectionManager
import android.media.session.MediaSessionManager
import android.media.tv.TvInputManager
import android.net.ConnectivityManager
import android.net.nsd.NsdManager
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pManager
import android.nfc.NfcManager
import android.os.*
import android.os.storage.StorageManager
import android.print.PrintManager
import android.telecom.TelecomManager
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.accessibility.CaptioningManager
import android.view.inputmethod.InputMethodManager
import android.view.textservice.TextServicesManager
import java.util.*

inline fun <reified T> Context.systemService(): T = getSystemService(this, T::class.java)!!

val ACCESSIBILITY_SERVICE = "accessibility"
val ACCOUNT_SERVICE = "account"
val ACTIVITY_SERVICE = "activity"
val ALARM_SERVICE = "alarm"
val APPWIDGET_SERVICE = "appwidget"
val APP_OPS_SERVICE = "appops"
val AUDIO_SERVICE = "audio"
val BATTERY_SERVICE = "batterymanager"
val BLUETOOTH_SERVICE = "bluetooth"
val CAMERA_SERVICE = "camera"
val CAPTIONING_SERVICE = "captioning"
val CARRIER_CONFIG_SERVICE = "carrier_config"
val CLIPBOARD_SERVICE = "clipboard"
val COMPANION_DEVICE_SERVICE = "companiondevice"
val CONNECTIVITY_SERVICE = "connectivity"
val CONSUMER_IR_SERVICE = "consumer_ir"
val CONTEXT_IGNORE_SECURITY = 2
val CONTEXT_INCLUDE_CODE = 1
val CONTEXT_RESTRICTED = 4
val CROSS_PROFILE_APPS_SERVICE = "crossprofileapps"
val DEVICE_POLICY_SERVICE = "device_policy"
val DISPLAY_SERVICE = "display"
val DOWNLOAD_SERVICE = "download"
val DROPBOX_SERVICE = "dropbox"
val EUICC_SERVICE = "euicc"
val FINGERPRINT_SERVICE = "fingerprint"
val HARDWARE_PROPERTIES_SERVICE = "hardware_properties"
val INPUT_METHOD_SERVICE = "input_method"
val INPUT_SERVICE = "input"
val IPSEC_SERVICE = "ipsec"
val JOB_SCHEDULER_SERVICE = "jobscheduler"
val KEYGUARD_SERVICE = "keyguard"
val LAUNCHER_APPS_SERVICE = "launcherapps"
val LAYOUT_INFLATER_SERVICE = "layout_inflater"
val LOCATION_SERVICE = "location"
val MEDIA_PROJECTION_SERVICE = "media_projection"
val MEDIA_ROUTER_SERVICE = "media_router"
val MEDIA_SESSION_SERVICE = "media_session"
val MIDI_SERVICE = "midi"
val NETWORK_STATS_SERVICE = "netstats"
val NFC_SERVICE = "nfc"
val NOTIFICATION_SERVICE = "notification"
val NSD_SERVICE = "servicediscovery"
val POWER_SERVICE = "power"
val PRINT_SERVICE = "print"
val RESTRICTIONS_SERVICE = "restrictions"
val SEARCH_SERVICE = "search"
val SENSOR_SERVICE = "sensor"
val SHORTCUT_SERVICE = "shortcut"
val STORAGE_SERVICE = "storage"
val STORAGE_STATS_SERVICE = "storagestats"
val SYSTEM_HEALTH_SERVICE = "systemhealth"
val TELECOM_SERVICE = "telecom"
val TELEPHONY_SERVICE = "phone"
val TELEPHONY_SUBSCRIPTION_SERVICE = "telephony_subscription_service"
val TEXT_CLASSIFICATION_SERVICE = "textclassification"
val TEXT_SERVICES_MANAGER_SERVICE = "textservices"
val TV_INPUT_SERVICE = "tv_input"
val UI_MODE_SERVICE = "uimode"
val USAGE_STATS_SERVICE = "usagestats"
val USB_SERVICE = "usb"
val USER_SERVICE = "user"
val VIBRATOR_SERVICE = "vibrator"
val WALLPAPER_SERVICE = "wallpaper"
val WIFI_AWARE_SERVICE = "wifiaware"
val WIFI_P2P_SERVICE = "wifip2p"
val WIFI_RTT_RANGING_SERVICE = "wifirtt"
val WIFI_SERVICE = "wifi"
val WINDOW_SERVICE = "window"
/**
 * Return the handle to a system-level service by class.
 *
 * @param context Context to retrieve service from.
 * @param serviceClass The class of the desired service.
 * @return The service or null if the class is not a supported system service.
 *
 * @see Context.getSystemService
 */
fun <T> getSystemService(context: Context, serviceClass: Class<T>): T? {
    if (Build.VERSION.SDK_INT >= 23) {
        return context.getSystemService(serviceClass)
    }

    val serviceName = getSystemServiceName(context, serviceClass)
    return if (serviceName != null) context.getSystemService(serviceName) as T else null
}

/**
 * Gets the name of the system-level service that is represented by the specified class.
 *
 * @param context Context to retrieve service name from.
 * @param serviceClass The class of the desired service.
 * @return The service name or null if the class is not a supported system service.
 *
 * @see Context.getSystemServiceName
 */
fun getSystemServiceName(
    context: Context,
    serviceClass: Class<*>
): String? {
    return if (Build.VERSION.SDK_INT >= 23) {
        context.getSystemServiceName(serviceClass)
    } else LegacyServiceMapHolder.SERVICES[serviceClass]
}

/** Nested class provides lazy initialization only when needed.  */
private object LegacyServiceMapHolder {
    internal val SERVICES = HashMap<Class<*>, String>()

    init {
        if (Build.VERSION.SDK_INT > 22) {
            SERVICES[SubscriptionManager::class.java] = TELEPHONY_SUBSCRIPTION_SERVICE
            SERVICES[UsageStatsManager::class.java] = USAGE_STATS_SERVICE
        }
        if (Build.VERSION.SDK_INT > 21) {
            SERVICES[AppWidgetManager::class.java] = APPWIDGET_SERVICE
            SERVICES[BatteryManager::class.java] = BATTERY_SERVICE
            SERVICES[CameraManager::class.java] = CAMERA_SERVICE
            SERVICES[JobScheduler::class.java] = JOB_SCHEDULER_SERVICE
            SERVICES[LauncherApps::class.java] = LAUNCHER_APPS_SERVICE
            SERVICES[MediaProjectionManager::class.java] = MEDIA_PROJECTION_SERVICE
            SERVICES[MediaSessionManager::class.java] = MEDIA_SESSION_SERVICE
            SERVICES[RestrictionsManager::class.java] = RESTRICTIONS_SERVICE
            SERVICES[TelecomManager::class.java] = TELECOM_SERVICE
            SERVICES[TvInputManager::class.java] = TV_INPUT_SERVICE
        }
        if (Build.VERSION.SDK_INT > 19) {
            SERVICES[AppOpsManager::class.java] = APP_OPS_SERVICE
            SERVICES[CaptioningManager::class.java] = CAPTIONING_SERVICE
            SERVICES[ConsumerIrManager::class.java] = CONSUMER_IR_SERVICE
            SERVICES[PrintManager::class.java] = PRINT_SERVICE
        }
        if (Build.VERSION.SDK_INT > 18) {
            SERVICES[BluetoothManager::class.java] = BLUETOOTH_SERVICE
        }
        if (Build.VERSION.SDK_INT > 17) {
            SERVICES[DisplayManager::class.java] = DISPLAY_SERVICE
            SERVICES[UserManager::class.java] = USER_SERVICE
        }
        if (Build.VERSION.SDK_INT > 16) {
            SERVICES[InputManager::class.java] = INPUT_SERVICE
            SERVICES[MediaRouter::class.java] = MEDIA_ROUTER_SERVICE
            SERVICES[NsdManager::class.java] = NSD_SERVICE
        }
        SERVICES[AccessibilityService::class.java] = ACCESSIBILITY_SERVICE
        SERVICES[AccountManager::class.java] = ACCOUNT_SERVICE
        SERVICES[ActivityManager::class.java] = ACTIVITY_SERVICE
        SERVICES[AlarmManager::class.java] = ALARM_SERVICE
        SERVICES[AudioManager::class.java] = AUDIO_SERVICE
        SERVICES[ClipboardManager::class.java] = CLIPBOARD_SERVICE
        SERVICES[ConnectivityManager::class.java] = CONNECTIVITY_SERVICE
        SERVICES[DevicePolicyManager::class.java] = DEVICE_POLICY_SERVICE
        SERVICES[DownloadManager::class.java] = DOWNLOAD_SERVICE
        SERVICES[DropBoxManager::class.java] = DROPBOX_SERVICE
        SERVICES[InputMethodManager::class.java] = INPUT_METHOD_SERVICE
        SERVICES[KeyguardManager::class.java] = KEYGUARD_SERVICE
        SERVICES[LayoutInflater::class.java] = LAYOUT_INFLATER_SERVICE
        SERVICES[LocationManager::class.java] = LOCATION_SERVICE
        SERVICES[NfcManager::class.java] = NFC_SERVICE
        SERVICES[NotificationManager::class.java] = NOTIFICATION_SERVICE
        SERVICES[PowerManager::class.java] = POWER_SERVICE
        SERVICES[SearchManager::class.java] = SEARCH_SERVICE
        SERVICES[SensorManager::class.java] = SENSOR_SERVICE
        SERVICES[StorageManager::class.java] = STORAGE_SERVICE
        SERVICES[TelephonyManager::class.java] = TELEPHONY_SERVICE
        SERVICES[TextServicesManager::class.java] = TEXT_SERVICES_MANAGER_SERVICE
        SERVICES[UiModeManager::class.java] = UI_MODE_SERVICE
        SERVICES[UsbManager::class.java] = USB_SERVICE
        SERVICES[Vibrator::class.java] = VIBRATOR_SERVICE
        SERVICES[WallpaperManager::class.java] = WALLPAPER_SERVICE
        SERVICES[WifiP2pManager::class.java] = WIFI_P2P_SERVICE
        SERVICES[WifiManager::class.java] = WIFI_SERVICE
        SERVICES[WindowManager::class.java] = WINDOW_SERVICE
    }
}
