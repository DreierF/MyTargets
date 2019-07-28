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

package de.dreier.mytargets.features.settings

import androidx.collection.LongSparseArray
import androidx.test.filters.SmallTest
import androidx.test.runner.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import de.dreier.mytargets.app.ApplicationInstance
import de.dreier.mytargets.features.settings.backup.EBackupInterval
import de.dreier.mytargets.features.settings.backup.provider.EBackupLocation
import de.dreier.mytargets.features.training.input.ETrainingScope
import de.dreier.mytargets.features.training.input.SummaryConfiguration
import de.dreier.mytargets.features.training.input.TargetView
import de.dreier.mytargets.shared.SharedApplicationInstance
import de.dreier.mytargets.shared.analysis.aggregation.EAggregationStrategy
import de.dreier.mytargets.shared.models.*
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.targets.models.NFAAAnimal
import de.dreier.mytargets.shared.targets.models.WAFull
import de.dreier.mytargets.shared.views.TargetViewBase.EInputMethod.KEYBOARD
import de.dreier.mytargets.test.base.InstrumentedTestBase
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDate
import org.threeten.bp.Period
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

@SmallTest
@RunWith(AndroidJUnit4::class)
class SettingsManagerTest : InstrumentedTestBase() {

    @Before
    fun setUp() {
        SharedApplicationInstance.sharedPreferences.edit().clear().apply()
        ApplicationInstance.lastSharedPreferences.edit().clear().apply()
    }

    @Test
    fun setStandardRound() {
        SettingsManager.standardRound = 34
        assertThat(SettingsManager.standardRound).isEqualTo(34)
    }

    @Test
    fun setArrow() {
        SettingsManager.arrow = null
        Assert.assertEquals(null, SettingsManager.arrow)
        SettingsManager.arrow = 1L
        assertThat(SettingsManager.arrow).isEqualTo(1L)
    }

    @Test
    fun setBow() {
        SettingsManager.bow = null
        Assert.assertEquals(null, SettingsManager.bow)
        SettingsManager.bow = 2L
        assertThat(SettingsManager.bow).isEqualTo(2L)
    }

    @Test
    fun setDistance() {
        SettingsManager.distance = Dimension(30f, Dimension.Unit.METER)
        assertThat(SettingsManager.distance)
                .isEqualTo(Dimension(30f, Dimension.Unit.METER))
        SettingsManager.distance = Dimension.UNKNOWN
        assertThat(SettingsManager.distance)
                .isEqualTo(Dimension.UNKNOWN)
    }

    @Test
    fun setShotsPerEnd() {
        SettingsManager.shotsPerEnd = 12
        assertThat(SettingsManager.shotsPerEnd).isEqualTo(12)
    }

    @Test
    fun setTarget() {
        val targetWA = Target(WAFull.ID, 0,
                Dimension(40f, Dimension.Unit.CENTIMETER))
        SettingsManager.target = targetWA
        assertThat(SettingsManager.target).isEqualTo(targetWA)
        val target3d = Target(NFAAAnimal.ID, 0, Diameter.LARGE)
        SettingsManager.target = target3d
        assertThat(SettingsManager.target).isEqualTo(target3d)
    }

    @Test
    fun setTimerEnabled() {
        SettingsManager.timerEnabled = true
        assertThat(SettingsManager.timerEnabled).isEqualTo(true)
    }

    @Test
    fun setArrowNumbersEnabled() {
        SettingsManager.arrowNumbersEnabled = true
        assertThat(SettingsManager.arrowNumbersEnabled).isEqualTo(true)
    }

    @Test
    fun setIndoor() {
        SettingsManager.indoor = false
        assertThat(SettingsManager.indoor).isEqualTo(false)
    }

    @Test
    fun setEndCount() {
        SettingsManager.endCount = 10
        assertThat(SettingsManager.endCount).isEqualTo(10)
    }

    @Test
    fun setInputMethod() {
        SettingsManager.inputMethod = KEYBOARD
        assertThat(SettingsManager.inputMethod).isEqualTo(KEYBOARD)
    }

    @Test
    fun setDonated() {
        SettingsManager.donated = true
        assertThat(SettingsManager.donated).isEqualTo(true)
    }

    @Test
    fun setShowMode() {
        SettingsManager.showMode = ETrainingScope.TRAINING
        assertThat(SettingsManager.showMode).isEqualTo(ETrainingScope.TRAINING)
    }

    @Test
    fun setAggregationStrategy() {
        SettingsManager.aggregationStrategy = EAggregationStrategy.CLUSTER
        assertThat(SettingsManager.aggregationStrategy)
                .isEqualTo(EAggregationStrategy.CLUSTER)
    }

    @Test
    fun getTimerSettings() {
        val settings = TimerSettings()
        settings.enabled = false
        settings.sound = true
        settings.vibrate = true
        settings.waitTime = 5
        settings.shootTime = 90
        settings.warnTime = 30
        SettingsManager.timerSettings = settings
        assertThat(SettingsManager.timerSettings).isEqualTo(settings)
        SharedApplicationInstance.sharedPreferences
                .edit()
                .putString(SettingsManager.KEY_TIMER_WAIT_TIME, "")
                .apply()
        assertThat(SettingsManager.timerSettings.waitTime).isEqualTo(10)
    }

    @Test
    fun setProfileFullName() {
        SettingsManager.profileFirstName = "Joe"
        SettingsManager.profileLastName = "Doe"
        assertThat(SettingsManager.profileFirstName).isEqualTo("Joe")
        assertThat(SettingsManager.profileLastName).isEqualTo("Doe")
        assertThat(SettingsManager.profileFullName).isEqualTo("Joe Doe")
    }

    @Test
    fun setProfileClub() {
        SettingsManager.profileClub = "My Club"
        assertThat(SettingsManager.profileClub).isEqualTo("My Club")
    }

    @Test
    fun setProfileLicenceNumber() {
        SettingsManager.profileLicenceNumber = "12345"
        assertThat(SettingsManager.profileLicenceNumber).isEqualTo("12345")
    }

    @Test
    fun setProfileBirthDay() {
        assertThat(SettingsManager.profileBirthDay).isEqualTo(null)
        assertThat(SettingsManager.profileBirthDayFormatted).isEqualTo(null)
        assertThat(SettingsManager.profileAge).isEqualTo(null)

        val birthDay = LocalDate.of(1993, 5, 4)
        SettingsManager.profileBirthDay = birthDay
        assertThat(SettingsManager.profileBirthDay).isEqualTo(birthDay)
        assertThat(SettingsManager.profileBirthDayFormatted).isEqualTo(
                DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).format(birthDay))
        assertThat(SettingsManager.profileAge)
                .isEqualTo(Period.between(birthDay, LocalDate.now()).years)
    }

    @Test
    fun setInputArrowDiameterScale() {
        SettingsManager.inputArrowDiameterScale = 2.5f
        assertThat(SettingsManager.inputArrowDiameterScale).isWithin(0f).of(2.5f)
    }

    @Test
    fun setInputTargetZoom() {
        SettingsManager.inputTargetZoom = 4.5f
        assertThat(SettingsManager.inputTargetZoom).isWithin(0f).of(4.5f)
    }

    @Test
    fun setInputKeyboardType() {
        SettingsManager.inputKeyboardType = TargetView.EKeyboardType.LEFT
        assertThat(SettingsManager.inputKeyboardType)
                .isEqualTo(TargetView.EKeyboardType.LEFT)
    }

    @Test
    fun setBackupLocation() {
        SettingsManager.backupLocation = EBackupLocation.GOOGLE_DRIVE
        assertThat(SettingsManager.backupLocation)
                .isEqualTo(EBackupLocation.GOOGLE_DRIVE)
    }

    @Test
    fun setBackupInterval() {
        SettingsManager.backupInterval = EBackupInterval.MONTHLY
        assertThat(SettingsManager.backupInterval).isEqualTo(EBackupInterval.MONTHLY)
    }

    @Test
    fun setStandardRoundsLastUsedEmpty() {
        SettingsManager.standardRoundsLastUsed = LongSparseArray()
        assertThat(SettingsManager.standardRoundsLastUsed.toString())
                .isEqualTo(LongSparseArray<Any>().toString())
    }

    @Test
    fun setStandardRoundsLastUsed() {
        val map = LongSparseArray<Int>()
        map.put(2L, 3)
        SettingsManager.standardRoundsLastUsed = map
        assertThat(SettingsManager.standardRoundsLastUsed.toString())
                .isEqualTo(map.toString())
        map.put(4L, 5)
        SettingsManager.standardRoundsLastUsed = map
        assertThat(SettingsManager.standardRoundsLastUsed.toString())
                .isEqualTo(map.toString())
    }

    @Test
    fun setShouldShowIntroActivity() {
        SettingsManager.shouldShowIntroActivity = false
        assertThat(SettingsManager.shouldShowIntroActivity).isEqualTo(false)
    }

    @Test
    fun setInputSummaryConfiguration() {
        val config = SummaryConfiguration()
        config.showEnd = false
        config.showRound = true
        config.showTraining = true
        config.showAverage = true
        config.averageScope = ETrainingScope.TRAINING
        SettingsManager.inputSummaryConfiguration = config
        assertThat(SettingsManager.inputSummaryConfiguration).isEqualTo(config)
    }

    @Test
    fun setScoreConfiguration() {
        val config = Score.Configuration()
        config.showReachedScore = true
        config.showTotalScore = true
        config.showPercentage = false
        config.showAverage = true
        SettingsManager.scoreConfiguration = config
        assertThat(SettingsManager.scoreConfiguration).isEqualTo(config)
    }
}
