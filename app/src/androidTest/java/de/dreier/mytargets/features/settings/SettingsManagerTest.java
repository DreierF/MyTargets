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

import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.dreier.mytargets.app.ApplicationInstance;
import de.dreier.mytargets.features.settings.backup.EBackupInterval;
import de.dreier.mytargets.features.settings.backup.provider.EBackupLocation;
import de.dreier.mytargets.features.training.input.ETrainingScope;
import de.dreier.mytargets.features.training.input.SummaryConfiguration;
import de.dreier.mytargets.features.training.input.TargetView;
import de.dreier.mytargets.shared.analysis.aggregation.EAggregationStrategy;
import de.dreier.mytargets.shared.models.Dimension;
import de.dreier.mytargets.shared.models.Score;
import de.dreier.mytargets.shared.models.Target;
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
        ApplicationInstance.getSharedPreferences().edit().clear().apply();
        ApplicationInstance.getLastSharedPreferences().edit().clear().apply();
    }

    @Test
    public void setStandardRound() {
        SettingsManager.setStandardRound(34);
        assertThat(SettingsManager.getStandardRound()).isEqualTo(34);
    }

    @Test
    public void setArrow() {
        SettingsManager.setArrow(null);
        assertThat(SettingsManager.getArrow()).isEqualTo(null);
        SettingsManager.setArrow(1L);
        assertThat(SettingsManager.getArrow()).isEqualTo(1L);
    }

    @Test
    public void setBow() {
        SettingsManager.setBow(null);
        assertThat(SettingsManager.getBow()).isEqualTo(null);
        SettingsManager.setBow(2L);
        assertThat(SettingsManager.getBow()).isEqualTo(2L);
    }

    @Test
    public void setDistance() {
        SettingsManager.setDistance(new Dimension(30, Dimension.Unit.METER));
        assertThat(SettingsManager.getDistance())
                .isEqualTo(new Dimension(30, Dimension.Unit.METER));
        SettingsManager.setDistance(new Dimension(-1, Dimension.Unit.METER));
        assertThat(SettingsManager.getDistance())
                .isEqualTo(new Dimension(-1, Dimension.Unit.METER));
    }

    @Test
    public void setShotsPerEnd() {
        SettingsManager.setShotsPerEnd(12);
        assertThat(SettingsManager.getShotsPerEnd()).isEqualTo(12);
    }

    @Test
    public void setTarget() {
        final Target targetWA = new Target(WAFull.ID, 0,
                new Dimension(40, Dimension.Unit.CENTIMETER));
        SettingsManager.setTarget(targetWA);
        assertThat(SettingsManager.getTarget()).isEqualTo(targetWA);
        final Target target3d = new Target(NFAAAnimal.ID, 0, Dimension.LARGE);
        SettingsManager.setTarget(target3d);
        assertThat(SettingsManager.getTarget()).isEqualTo(target3d);
    }

    @Test
    public void setTimerEnabled() {
        SettingsManager.setTimerEnabled(true);
        assertThat(SettingsManager.getTimerEnabled()).isEqualTo(true);
    }

    @Test
    public void setArrowNumbersEnabled() {
        SettingsManager.setArrowNumbersEnabled(true);
        assertThat(SettingsManager.getArrowNumbersEnabled()).isEqualTo(true);
    }

    @Test
    public void setIndoor() {
        SettingsManager.setIndoor(false);
        assertThat(SettingsManager.getIndoor()).isEqualTo(false);
    }

    @Test
    public void setEndCount() {
        SettingsManager.setEndCount(10);
        assertThat(SettingsManager.getEndCount()).isEqualTo(10);
    }

    @Test
    public void setTranslationDialogWasShown() {
        SettingsManager.setTranslationDialogShown(true);
        assertThat(SettingsManager.isTranslationDialogShown()).isEqualTo(true);
    }

    @Test
    public void setInputMethod() {
        SettingsManager.setInputMethod(KEYBOARD);
        assertThat(SettingsManager.getInputMethod()).isEqualTo(KEYBOARD);
    }

    @Test
    public void setDonated() {
        SettingsManager.setDonated(true);
        assertThat(SettingsManager.hasDonated()).isEqualTo(true);
    }

    @Test
    public void setShowMode() {
        SettingsManager.setShowMode(ETrainingScope.TRAINING);
        assertThat(SettingsManager.getShowMode()).isEqualTo(ETrainingScope.TRAINING);
    }

    @Test
    public void setAggregationStrategy() {
        SettingsManager.setAggregationStrategy(EAggregationStrategy.CLUSTER);
        assertThat(SettingsManager.getAggregationStrategy())
                .isEqualTo(EAggregationStrategy.CLUSTER);
    }

    @Test
    public void getTimerVibrate() {
        SettingsManager.setTimerVibrate(true);
        assertThat(SettingsManager.getTimerVibrate()).isEqualTo(true);
    }

    @Test
    public void getTimerSoundEnabled() {
        SettingsManager.setTimerSoundEnabled(false);
        assertThat(SettingsManager.getTimerSoundEnabled()).isEqualTo(false);
    }

    @Test
    public void getTimerWaitTime() {
        SettingsManager.setTimerWaitTime(5);
        assertThat(SettingsManager.getTimerWaitTime()).isEqualTo(5);
        ApplicationInstance.getSharedPreferences()
                .edit()
                .putString(SettingsManager.KEY_TIMER_WAIT_TIME, "")
                .apply();
        assertThat(SettingsManager.getTimerWaitTime()).isEqualTo(10);
    }

    @Test
    public void getTimerShootTime() {
        SettingsManager.setTimerShootTime(90);
        assertThat(SettingsManager.getTimerShootTime()).isEqualTo(90);
    }

    @Test
    public void getTimerWarnTime() {
        SettingsManager.setTimerWarnTime(30);
        assertThat(SettingsManager.getTimerWarnTime()).isEqualTo(30);
    }

    @Test
    public void setProfileFullName() {
        SettingsManager.setProfileFirstName("Joe");
        SettingsManager.setProfileLastName("Doe");
        assertThat(SettingsManager.getProfileFirstName()).isEqualTo("Joe");
        assertThat(SettingsManager.getProfileLastName()).isEqualTo("Doe");
        assertThat(SettingsManager.getProfileFullName()).isEqualTo("Joe Doe");
    }

    @Test
    public void setProfileClub() {
        SettingsManager.setProfileClub("My Club");
        assertThat(SettingsManager.getProfileClub()).isEqualTo("My Club");
    }

    @Test
    public void setProfileBirthDay() {
        assertThat(SettingsManager.getProfileBirthDay()).isEqualTo(null);
        assertThat(SettingsManager.getProfileBirthDayFormatted()).isEqualTo(null);
        assertThat(SettingsManager.getProfileAge()).isEqualTo(-1);

        final LocalDate birthDay = new LocalDate(1993, 5, 4);
        SettingsManager.setProfileBirthDay(birthDay);
        assertThat(SettingsManager.getProfileBirthDay()).isEqualTo(birthDay);
        assertThat(SettingsManager.getProfileBirthDayFormatted()).isEqualTo(
                DateTimeFormat.mediumDate().print(birthDay));
        assertThat(SettingsManager.getProfileAge())
                .isEqualTo(Years.yearsBetween(birthDay, LocalDate.now()).getYears());
    }

    @Test
    public void setInputArrowDiameterScale() {
        SettingsManager.setInputArrowDiameterScale(2.5f);
        assertThat(SettingsManager.getInputArrowDiameterScale()).isWithin(0f).of(2.5f);
    }

    @Test
    public void setInputTargetZoom() {
        SettingsManager.setInputTargetZoom(4.5f);
        assertThat(SettingsManager.getInputTargetZoom()).isWithin(0f).of(4.5f);
    }

    @Test
    public void setInputKeyboardType() {
        SettingsManager.setInputKeyboardType(TargetView.EKeyboardType.LEFT);
        assertThat(SettingsManager.getInputKeyboardType()).isEqualTo(TargetView.EKeyboardType.LEFT);
    }

    @Test
    public void setBackupLocation() {
        SettingsManager.setBackupLocation(EBackupLocation.GOOGLE_DRIVE);
        assertThat(SettingsManager.getBackupLocation()).isEqualTo(EBackupLocation.GOOGLE_DRIVE);
    }

    @Test
    public void setBackupInterval() {
        SettingsManager.setBackupInterval(EBackupInterval.MONTHLY);
        assertThat(SettingsManager.getBackupInterval()).isEqualTo(EBackupInterval.MONTHLY);
    }

    @Test
    public void setStandardRoundsLastUsedEmpty() {
        SettingsManager.setStandardRoundsLastUsed(Collections.emptyMap());
        assertThat(SettingsManager.getStandardRoundsLastUsed()).isEqualTo(Collections.emptyMap());
    }

    @Test
    public void setStandardRoundsLastUsed() {
        Map<Long, Integer> map = new HashMap<>();
        map.put(2L, 3);
        SettingsManager.setStandardRoundsLastUsed(map);
        assertThat(SettingsManager.getStandardRoundsLastUsed()).isEqualTo(map);
        map.put(4L, 5);
        SettingsManager.setStandardRoundsLastUsed(map);
        assertThat(SettingsManager.getStandardRoundsLastUsed()).isEqualTo(map);
    }

    @Test
    public void setShouldShowIntroActivity() {
        SettingsManager.setShouldShowIntroActivity(false);
        assertThat(SettingsManager.shouldShowIntroActivity()).isEqualTo(false);
    }

    @Test
    public void setInputSummaryConfiguration() {
        SummaryConfiguration config = new SummaryConfiguration();
        config.showEnd = false;
        config.showRound = true;
        config.showTraining = true;
        config.showAverage = true;
        config.averageScope = ETrainingScope.TRAINING;
        SettingsManager.setInputSummaryConfiguration(config);
        assertThat(SettingsManager.getInputSummaryConfiguration()).isEqualTo(config);
    }

    @Test
    public void setScoreConfiguration() {
        Score.Configuration config = new Score.Configuration();
        config.showReachedScore = true;
        config.showTotalScore = true;
        config.showPercentage = false;
        config.showAverage = true;
        SettingsManager.setScoreConfiguration(config);
        assertThat(SettingsManager.getScoreConfiguration()).isEqualTo(config);
    }
}