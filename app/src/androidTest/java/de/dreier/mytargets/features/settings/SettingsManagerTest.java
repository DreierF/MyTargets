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

package de.dreier.mytargets.features.settings;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.util.LongSparseArray;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.threeten.bp.LocalDate;
import org.threeten.bp.Period;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import de.dreier.mytargets.app.ApplicationInstance;
import de.dreier.mytargets.features.settings.backup.EBackupInterval;
import de.dreier.mytargets.features.settings.backup.provider.EBackupLocation;
import de.dreier.mytargets.features.training.input.ETrainingScope;
import de.dreier.mytargets.features.training.input.SummaryConfiguration;
import de.dreier.mytargets.features.training.input.TargetView;
import de.dreier.mytargets.shared.analysis.aggregation.EAggregationStrategy;
import de.dreier.mytargets.shared.models.Diameter;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Score;
import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.models.TimerSettings;
import de.dreier.mytargets.shared.targets.models.NFAAAnimal;
import de.dreier.mytargets.shared.targets.models.WAFull;
import de.dreier.mytargets.test.base.InstrumentedTestBase;

import static com.google.common.truth.Truth.assertThat;
import static de.dreier.mytargets.shared.views.TargetViewBase.EInputMethod.KEYBOARD;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class SettingsManagerTest extends InstrumentedTestBase {

    @Before
    public void setUp() {
        ApplicationInstance.Companion.getSharedPreferences().edit().clear().apply();
        ApplicationInstance.getLastSharedPreferences().edit().clear().apply();
    }

    @Test
    public void setStandardRound() {
        SettingsManager.INSTANCE.setStandardRound(34);
        assertThat(SettingsManager.INSTANCE.getStandardRound()).isEqualTo(34);
    }

    @Test
    public void setArrow() {
        SettingsManager.INSTANCE.setArrow(null);
        assertThat(SettingsManager.INSTANCE.getArrow()).isEqualTo(null);
        SettingsManager.INSTANCE.setArrow(1L);
        assertThat(SettingsManager.INSTANCE.getArrow()).isEqualTo(1L);
    }

    @Test
    public void setBow() {
        SettingsManager.INSTANCE.setBow(null);
        assertThat(SettingsManager.INSTANCE.getBow()).isEqualTo(null);
        SettingsManager.INSTANCE.setBow(2L);
        assertThat(SettingsManager.INSTANCE.getBow()).isEqualTo(2L);
    }

    @Test
    public void setDistance() {
        SettingsManager.INSTANCE.setDistance(new Dimension(30, Dimension.Unit.METER));
        assertThat(SettingsManager.INSTANCE.getDistance())
                .isEqualTo(new Dimension(30, Dimension.Unit.METER));
        SettingsManager.INSTANCE.setDistance(Dimension.Companion.getUNKNOWN());
        assertThat(SettingsManager.INSTANCE.getDistance())
                .isEqualTo(Dimension.Companion.getUNKNOWN());
    }

    @Test
    public void setShotsPerEnd() {
        SettingsManager.INSTANCE.setShotsPerEnd(12);
        assertThat(SettingsManager.INSTANCE.getShotsPerEnd()).isEqualTo(12);
    }

    @Test
    public void setTarget() {
        final Target targetWA = new Target(WAFull.ID, 0,
                new Dimension(40, Dimension.Unit.CENTIMETER));
        SettingsManager.INSTANCE.setTarget(targetWA);
        assertThat(SettingsManager.INSTANCE.getTarget()).isEqualTo(targetWA);
        final Target target3d = new Target(NFAAAnimal.ID, 0, Diameter.INSTANCE.getLARGE());
        SettingsManager.INSTANCE.setTarget(target3d);
        assertThat(SettingsManager.INSTANCE.getTarget()).isEqualTo(target3d);
    }

    @Test
    public void setTimerEnabled() {
        SettingsManager.INSTANCE.setTimerEnabled(true);
        assertThat(SettingsManager.INSTANCE.getTimerEnabled()).isEqualTo(true);
    }

    @Test
    public void setArrowNumbersEnabled() {
        SettingsManager.INSTANCE.setArrowNumbersEnabled(true);
        assertThat(SettingsManager.INSTANCE.getArrowNumbersEnabled()).isEqualTo(true);
    }

    @Test
    public void setIndoor() {
        SettingsManager.INSTANCE.setIndoor(false);
        assertThat(SettingsManager.INSTANCE.getIndoor()).isEqualTo(false);
    }

    @Test
    public void setEndCount() {
        SettingsManager.INSTANCE.setEndCount(10);
        assertThat(SettingsManager.INSTANCE.getEndCount()).isEqualTo(10);
    }

    @Test
    public void setInputMethod() {
        SettingsManager.INSTANCE.setInputMethod(KEYBOARD);
        assertThat(SettingsManager.INSTANCE.getInputMethod()).isEqualTo(KEYBOARD);
    }

    @Test
    public void setDonated() {
        SettingsManager.INSTANCE.setDonated(true);
        assertThat(SettingsManager.INSTANCE.getDonated()).isEqualTo(true);
    }

    @Test
    public void setShowMode() {
        SettingsManager.INSTANCE.setShowMode(ETrainingScope.TRAINING);
        assertThat(SettingsManager.INSTANCE.getShowMode()).isEqualTo(ETrainingScope.TRAINING);
    }

    @Test
    public void setAggregationStrategy() {
        SettingsManager.INSTANCE.setAggregationStrategy(EAggregationStrategy.CLUSTER);
        assertThat(SettingsManager.INSTANCE.getAggregationStrategy())
                .isEqualTo(EAggregationStrategy.CLUSTER);
    }

    @Test
    public void getTimerSettings() {
        TimerSettings settings = new TimerSettings();
        settings.enabled = false;
        settings.sound = true;
        settings.vibrate = true;
        settings.waitTime = 5;
        settings.shootTime = 90;
        settings.warnTime = 30;
        SettingsManager.INSTANCE.setTimerSettings(settings);
        assertThat(SettingsManager.INSTANCE.getTimerSettings()).isEqualTo(settings);
        ApplicationInstance.Companion.getSharedPreferences()
                .edit()
                .putString(SettingsManager.KEY_TIMER_WAIT_TIME, "")
                .apply();
        assertThat(SettingsManager.INSTANCE.getTimerSettings().waitTime).isEqualTo(10);
    }

    @Test
    public void setProfileFullName() {
        SettingsManager.INSTANCE.setProfileFirstName("Joe");
        SettingsManager.INSTANCE.setProfileLastName("Doe");
        assertThat(SettingsManager.INSTANCE.getProfileFirstName()).isEqualTo("Joe");
        assertThat(SettingsManager.INSTANCE.getProfileLastName()).isEqualTo("Doe");
        assertThat(SettingsManager.INSTANCE.getProfileFullName()).isEqualTo("Joe Doe");
    }

    @Test
    public void setProfileClub() {
        SettingsManager.INSTANCE.setProfileClub("My Club");
        assertThat(SettingsManager.INSTANCE.getProfileClub()).isEqualTo("My Club");
    }

    @Test
    public void setProfileLicenceNumber() {
        SettingsManager.INSTANCE.setProfileLicenceNumber("12345");
        assertThat(SettingsManager.INSTANCE.getProfileLicenceNumber()).isEqualTo("12345");
    }

    @Test
    public void setProfileBirthDay() {
        assertThat(SettingsManager.INSTANCE.getProfileBirthDay()).isEqualTo(null);
        assertThat(SettingsManager.INSTANCE.getProfileBirthDayFormatted()).isEqualTo(null);
        assertThat(SettingsManager.INSTANCE.getProfileAge()).isEqualTo(null);

        final LocalDate birthDay = LocalDate.of(1993, 5, 4);
        SettingsManager.INSTANCE.setProfileBirthDay(birthDay);
        assertThat(SettingsManager.INSTANCE.getProfileBirthDay()).isEqualTo(birthDay);
        assertThat(SettingsManager.INSTANCE.getProfileBirthDayFormatted()).isEqualTo(
                DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).format(birthDay));
        assertThat(SettingsManager.INSTANCE.getProfileAge())
                .isEqualTo(Period.between(birthDay, LocalDate.now()).getYears());
    }

    @Test
    public void setInputArrowDiameterScale() {
        SettingsManager.INSTANCE.setInputArrowDiameterScale(2.5f);
        assertThat(SettingsManager.INSTANCE.getInputArrowDiameterScale()).isWithin(0f).of(2.5f);
    }

    @Test
    public void setInputTargetZoom() {
        SettingsManager.INSTANCE.setInputTargetZoom(4.5f);
        assertThat(SettingsManager.INSTANCE.getInputTargetZoom()).isWithin(0f).of(4.5f);
    }

    @Test
    public void setInputKeyboardType() {
        SettingsManager.INSTANCE.setInputKeyboardType(TargetView.EKeyboardType.LEFT);
        assertThat(SettingsManager.INSTANCE.getInputKeyboardType())
                .isEqualTo(TargetView.EKeyboardType.LEFT);
    }

    @Test
    public void setBackupLocation() {
        SettingsManager.INSTANCE.setBackupLocation(EBackupLocation.GOOGLE_DRIVE);
        assertThat(SettingsManager.INSTANCE.getBackupLocation())
                .isEqualTo(EBackupLocation.GOOGLE_DRIVE);
    }

    @Test
    public void setBackupInterval() {
        SettingsManager.INSTANCE.setBackupInterval(EBackupInterval.MONTHLY);
        assertThat(SettingsManager.INSTANCE.getBackupInterval()).isEqualTo(EBackupInterval.MONTHLY);
    }

    @Test
    public void setStandardRoundsLastUsedEmpty() {
        SettingsManager.INSTANCE.setStandardRoundsLastUsed(new LongSparseArray<>());
        assertThat(SettingsManager.INSTANCE.getStandardRoundsLastUsed().toString())
                .isEqualTo(new LongSparseArray<>().toString());
    }

    @Test
    public void setStandardRoundsLastUsed() {
        LongSparseArray<Integer> map = new LongSparseArray<>();
        map.put(2L, 3);
        SettingsManager.INSTANCE.setStandardRoundsLastUsed(map);
        assertThat(SettingsManager.INSTANCE.getStandardRoundsLastUsed().toString())
                .isEqualTo(map.toString());
        map.put(4L, 5);
        SettingsManager.INSTANCE.setStandardRoundsLastUsed(map);
        assertThat(SettingsManager.INSTANCE.getStandardRoundsLastUsed().toString())
                .isEqualTo(map.toString());
    }

    @Test
    public void setShouldShowIntroActivity() {
        SettingsManager.INSTANCE.setShouldShowIntroActivity(false);
        assertThat(SettingsManager.INSTANCE.getShouldShowIntroActivity()).isEqualTo(false);
    }

    @Test
    public void setInputSummaryConfiguration() {
        SummaryConfiguration config = new SummaryConfiguration();
        config.showEnd = false;
        config.showRound = true;
        config.showTraining = true;
        config.showAverage = true;
        config.averageScope = ETrainingScope.TRAINING;
        SettingsManager.INSTANCE.setInputSummaryConfiguration(config);
        assertThat(SettingsManager.INSTANCE.getInputSummaryConfiguration()).isEqualTo(config);
    }

    @Test
    public void setScoreConfiguration() {
        Score.Configuration config = new Score.Configuration();
        config.showReachedScore = true;
        config.showTotalScore = true;
        config.showPercentage = false;
        config.showAverage = true;
        SettingsManager.INSTANCE.setScoreConfiguration(config);
        assertThat(SettingsManager.INSTANCE.getScoreConfiguration()).isEqualTo(config);
    }
}
