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

import android.support.v4.util.LongSparseArray
import de.dreier.mytargets.app.ApplicationInstance
import de.dreier.mytargets.features.scoreboard.EFileType
import de.dreier.mytargets.features.scoreboard.ScoreboardConfiguration
import de.dreier.mytargets.features.settings.backup.EBackupInterval
import de.dreier.mytargets.features.settings.backup.provider.EBackupLocation
import de.dreier.mytargets.features.training.input.ETrainingScope
import de.dreier.mytargets.features.training.input.SummaryConfiguration
import de.dreier.mytargets.features.training.input.TargetView.EKeyboardType
import de.dreier.mytargets.shared.SharedApplicationInstance
import de.dreier.mytargets.shared.analysis.aggregation.EAggregationStrategy
import de.dreier.mytargets.shared.models.Dimension
import de.dreier.mytargets.shared.models.Dimension.Unit.CENTIMETER
import de.dreier.mytargets.shared.models.Score
import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.models.TimerSettings
import de.dreier.mytargets.shared.targets.scoringstyle.ArrowAwareScoringStyle
import de.dreier.mytargets.shared.views.TargetViewBase
import de.dreier.mytargets.shared.views.TargetViewBase.EInputMethod.KEYBOARD
import de.dreier.mytargets.shared.views.TargetViewBase.EInputMethod.PLOTTING
import de.dreier.mytargets.utils.map
import de.dreier.mytargets.utils.toSparseArray
import org.threeten.bp.LocalDate
import org.threeten.bp.Period
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

object SettingsManager {
    private val lastUsed = ApplicationInstance
        .lastSharedPreferences
    private val preferences = SharedApplicationInstance.sharedPreferences

    const val KEY_TIMER_WARN_TIME = "timer_warn_time"
    const val KEY_TIMER_WAIT_TIME = "timer_wait_time"
    const val KEY_TIMER_SHOOT_TIME = "timer_shoot_time"
    const val KEY_PROFILE_FIRST_NAME = "profile_first_name"
    const val KEY_PROFILE_LAST_NAME = "profile_last_name"
    const val KEY_PROFILE_BIRTHDAY = "profile_birthday"
    const val KEY_PROFILE_CLUB = "profile_club"
    const val KEY_PROFILE_LICENCE_NUMBER = "profile_licence_number"
    const val KEY_INPUT_SUMMARY_AVERAGE_OF = "input_summary_average_of"
    const val KEY_INPUT_ARROW_DIAMETER_SCALE = "input_arrow_diameter_scale"
    const val KEY_INPUT_TARGET_ZOOM = "input_target_zoom"
    const val KEY_INPUT_KEYBOARD_TYPE = "input_keyboard_type"
    private const val KEY_INPUT_KEEP_ABOVE_LOCKSCREEN = "input_keep_above_lockscreen"
    private const val KEY_INPUT_SUMMARY_SHOW_END = "input_summary_show_end"
    private const val KEY_INPUT_SUMMARY_SHOW_ROUND = "input_summary_show_round"
    private const val KEY_INPUT_SUMMARY_SHOW_TRAINING = "input_summary_show_training"
    private const val KEY_INPUT_SUMMARY_SHOW_AVERAGE = "input_summary_show_average"
    const val KEY_SCOREBOARD_SHARE_FILE_TYPE = "scoreboard_share_file_type"
    private const val KEY_BACKUP_INTERVAL = "backup_interval"
    private const val KEY_DONATED = "donated"
    private const val KEY_TIMER_KEEP_ABOVE_LOCKSCREEN = "timer_keep_above_lockscreen"
    private const val KEY_TIMER_VIBRATE = "timer_vibrate"
    private const val KEY_TIMER_SOUND = "timer_sound"
    private const val KEY_STANDARD_ROUND = "standard_round"
    private const val KEY_ARROW = "arrow"
    private const val KEY_BOW = "bow"
    private const val KEY_DISTANCE_VALUE = "distance"
    private const val KEY_DISTANCE_UNIT = "unit"
    private const val KEY_ARROWS_PER_END = "ppp"
    private const val KEY_TARGET = "target"
    private const val KEY_SCORING_STYLE = "scoring_style"
    private const val KEY_TARGET_DIAMETER_VALUE = "size_target"
    private const val KEY_TARGET_DIAMETER_UNIT = "unit_target"
    private const val KEY_TIMER = "timer"
    private const val KEY_NUMBERING_ENABLED = "numbering"
    private const val KEY_INDOOR = "indoor"
    private const val KEY_END_COUNT = "rounds"
    private const val KEY_INPUT_MODE = "target_mode"
    const val KEY_SHOW_MODE = "show_mode"
    private const val KEY_BACKUP_LOCATION = "backup_location"
    const val KEY_AGGREGATION_STRATEGY = "aggregation_strategy"
    private const val KEY_STANDARD_ROUNDS_LAST_USED = "standard_round_last_used"
    private const val KEY_INTRO_SHOWED = "intro_showed"
    private const val KEY_OVERVIEW_SHOW_REACHED_SCORE = "overview_show_reached_score"
    private const val KEY_OVERVIEW_SHOW_TOTAL_SCORE = "overview_show_total_score"
    private const val KEY_OVERVIEW_SHOW_PERCENTAGE = "overview_show_percentage"
    private const val KEY_OVERVIEW_SHOW_ARROW_AVERAGE = "overview_show_arrow_average"
    private const val KEY_OVERVIEW_SHOT_SORTING = "overview_shot_sorting"
    private const val KEY_OVERVIEW_SHOT_SORTING_SPOT = "overview_shot_sorting_spot"
    const val KEY_LANGUAGE = "language"
    const val KEY_STATISTICS_DISPERSION_PATTERN_FILE_TYPE =
        "statistics_dispersion_pattern_file_type"
    const val KEY_STATISTICS_DISPERSION_PATTERN_AGGREGATION_STRATEGY =
        "statistics_dispersion_pattern_aggregation_strategy"
    private const val KEY_STATISTICS_DISPERSION_PATTERN_MERGE_SPOT =
        "statistics_dispersion_pattern_merge_spot"

    var standardRound: Long
        get() = lastUsed[KEY_STANDARD_ROUND, 32].toLong()
        set(value) = lastUsed.set(KEY_STANDARD_ROUND, value.toInt())

    var arrow: Long?
        get() = lastUsed.get<Int>(KEY_ARROW)?.toLong()
        set(value) = lastUsed.set(KEY_ARROW, value?.toInt())

    var bow: Long?
        get() = lastUsed.get<Int>(KEY_BOW)?.toLong()
        set(value) = lastUsed.set(KEY_BOW, value?.toInt())

    var distance: Dimension
        get() {
            val distance = lastUsed.getInt(KEY_DISTANCE_VALUE, 10)
            val unit = lastUsed.getString(KEY_DISTANCE_UNIT, "m")
            return Dimension.from(distance.toFloat(), unit)
        }
        set(value) = lastUsed.edit()
            .putInt(KEY_DISTANCE_VALUE, value.value.toInt())
            .putString(KEY_DISTANCE_UNIT, value.unit?.toString())
            .apply()

    var shotsPerEnd: Int
        get() = lastUsed[KEY_ARROWS_PER_END, 3]
        set(value) = lastUsed.set(KEY_ARROWS_PER_END, value)

    var target: Target
        get() {
            val targetId = lastUsed[KEY_TARGET, 0]
            val scoringStyle = lastUsed[KEY_SCORING_STYLE, 0]
            val diameterValue = lastUsed[KEY_TARGET_DIAMETER_VALUE, 60]
            val diameterUnit = lastUsed[KEY_TARGET_DIAMETER_UNIT, CENTIMETER.toString()]
            val diameter = Dimension.from(diameterValue.toFloat(), diameterUnit)
            return Target(targetId.toLong(), scoringStyle, diameter)
        }
        set(value) = lastUsed.edit()
            .putInt(KEY_TARGET, value.id.toInt())
            .putInt(KEY_SCORING_STYLE, value.scoringStyleIndex)
            .putInt(KEY_TARGET_DIAMETER_VALUE, value.diameter.value.toInt())
            .putString(KEY_TARGET_DIAMETER_UNIT, value.diameter.unit?.toString())
            .apply()

    var timerEnabled: Boolean
        get() = lastUsed[KEY_TIMER, false]
        set(value) = lastUsed.set(KEY_TIMER, value)

    var timerKeepAboveLockscreen: Boolean
        get() = preferences[KEY_TIMER_KEEP_ABOVE_LOCKSCREEN, true]
        set(value) = preferences.set(KEY_TIMER_KEEP_ABOVE_LOCKSCREEN, value)

    var timerSettings: TimerSettings
        get() {
            fun getPrefTime(key: String, def: Int): Int {
                return preferences.getString(key, def.toString()).toIntOrNull() ?: def
            }

            val settings = TimerSettings()
            settings.enabled = lastUsed.getBoolean(KEY_TIMER, false)
            settings.vibrate = preferences.getBoolean(KEY_TIMER_VIBRATE, false)
            settings.sound = preferences.getBoolean(KEY_TIMER_SOUND, true)
            settings.waitTime = getPrefTime(KEY_TIMER_WAIT_TIME, 10)
            settings.shootTime = getPrefTime(KEY_TIMER_SHOOT_TIME, 120)
            settings.warnTime = getPrefTime(KEY_TIMER_WARN_TIME, 30)
            return settings
        }
        set(value) {
            lastUsed[KEY_TIMER] = value.enabled
            preferences
                .edit()
                .putBoolean(KEY_TIMER_VIBRATE, value.vibrate)
                .putBoolean(KEY_TIMER_SOUND, value.sound)
                .putString(KEY_TIMER_WAIT_TIME, value.waitTime.toString())
                .putString(KEY_TIMER_SHOOT_TIME, value.shootTime.toString())
                .putString(KEY_TIMER_WARN_TIME, value.warnTime.toString())
                .apply()
        }

    var arrowNumbersEnabled: Boolean
        get() = lastUsed[KEY_NUMBERING_ENABLED, false]
        set(value) = lastUsed.set(KEY_NUMBERING_ENABLED, value)

    var indoor: Boolean
        get() = lastUsed[KEY_INDOOR, false]
        set(value) = lastUsed.set(KEY_INDOOR, value)

    var endCount: Int
        get() = lastUsed[KEY_END_COUNT, 10]
        set(value) = lastUsed.set(KEY_END_COUNT, value)

    var inputMethod: TargetViewBase.EInputMethod
        get() = if (preferences[KEY_INPUT_MODE, false]) KEYBOARD else PLOTTING
        set(value) = preferences.set(KEY_INPUT_MODE, value == KEYBOARD)

    var showMode: ETrainingScope
        get() = ETrainingScope.valueOf(
            preferences
                .getString(KEY_SHOW_MODE, ETrainingScope.END.toString())
        )
        set(value) = preferences.set(KEY_SHOW_MODE, value.toString())

    var aggregationStrategy: EAggregationStrategy
        get() = EAggregationStrategy.valueOf(
            preferences
                .getString(KEY_AGGREGATION_STRATEGY, EAggregationStrategy.AVERAGE.toString())
        )
        set(value) = preferences.set(KEY_AGGREGATION_STRATEGY, value.toString())

    var profileFirstName: String
        get() = preferences[KEY_PROFILE_FIRST_NAME, ""]
        set(value) = preferences.set(KEY_PROFILE_FIRST_NAME, value)

    var profileLastName: String
        get() = preferences[KEY_PROFILE_LAST_NAME, ""]
        set(value) = preferences.set(KEY_PROFILE_LAST_NAME, value)

    val profileFullName: String
        get() = "%s %s".format(profileFirstName, profileLastName).trim()

    var profileClub: String
        get() = preferences[KEY_PROFILE_CLUB, ""]
        set(value) = preferences.set(KEY_PROFILE_CLUB, value)

    var profileLicenceNumber: String
        get() = preferences[KEY_PROFILE_LICENCE_NUMBER, ""]
        set(value) = preferences.set(KEY_PROFILE_LICENCE_NUMBER, value)

    var profileBirthDay: LocalDate?
        get() {
            val date = preferences.get<String>(KEY_PROFILE_BIRTHDAY) ?: return null
            return LocalDate.parse(date)
        }
        set(value) = preferences.set(KEY_PROFILE_BIRTHDAY, value.toString())

    val profileBirthDayFormatted: String?
        get() {
            val birthDay = profileBirthDay ?: return null
            return DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).format(birthDay)
        }

    val profileAge: Int?
        get() {
            val birthDay = profileBirthDay ?: return null
            return Period.between(birthDay, LocalDate.now()).years
        }

    var inputArrowDiameterScale: Float
        get() = preferences[KEY_INPUT_ARROW_DIAMETER_SCALE, "1.0"].toFloat()
        set(value) = preferences.set(KEY_INPUT_ARROW_DIAMETER_SCALE, value.toString())

    var inputTargetZoom: Float
        get() = preferences[KEY_INPUT_TARGET_ZOOM, "3.0"].toFloat()
        set(value) = preferences.set(KEY_INPUT_TARGET_ZOOM, value.toString())

    var inputKeyboardType: EKeyboardType
        get() = EKeyboardType.valueOf(preferences[KEY_INPUT_KEYBOARD_TYPE, EKeyboardType.RIGHT.name])
        set(value) = preferences.set(KEY_INPUT_KEYBOARD_TYPE, value.name)

    var scoreboardShareFileType: EFileType
        get() = EFileType.valueOf(preferences[KEY_SCOREBOARD_SHARE_FILE_TYPE, EFileType.PDF.name])
        set(value) = preferences.set(KEY_SCOREBOARD_SHARE_FILE_TYPE, value.name)

    var statisticsDispersionPatternFileType: EFileType
        get() = EFileType.valueOf(preferences[KEY_STATISTICS_DISPERSION_PATTERN_FILE_TYPE, EFileType.JPG.name])
        set(value) = preferences.set(KEY_STATISTICS_DISPERSION_PATTERN_FILE_TYPE, value.name)

    var statisticsDispersionPatternAggregationStrategy: EAggregationStrategy
        get() = EAggregationStrategy.valueOf(preferences[KEY_STATISTICS_DISPERSION_PATTERN_AGGREGATION_STRATEGY, EAggregationStrategy.AVERAGE.toString()])
        set(value) = preferences.set(
            KEY_STATISTICS_DISPERSION_PATTERN_AGGREGATION_STRATEGY,
            value.toString()
        )

    var statisticsDispersionPatternMergeSpot: Boolean
        get() = preferences[KEY_STATISTICS_DISPERSION_PATTERN_MERGE_SPOT, false]
        set(value) = preferences.set(KEY_STATISTICS_DISPERSION_PATTERN_MERGE_SPOT, value)

    var backupLocation: EBackupLocation
        get() = EBackupLocation.valueOf(preferences[KEY_BACKUP_LOCATION, EBackupLocation.INTERNAL_STORAGE.name])
        set(value) = preferences.set(KEY_BACKUP_LOCATION, value.name)

    var backupInterval: EBackupInterval
        get() = EBackupInterval.valueOf(preferences[KEY_BACKUP_INTERVAL, EBackupInterval.WEEKLY.name])
        set(value) = preferences.set(KEY_BACKUP_INTERVAL, value.name)

    var standardRoundsLastUsed: LongSparseArray<Int>
        get() {
            return lastUsed[KEY_STANDARD_ROUNDS_LAST_USED, ""]
                .split(",")
                .filterNot { it.isEmpty() }
                .map { it.split(":") }
                .map { (a, b) -> Pair(a.toLong(), b.toInt()) }
                .toSparseArray()
        }
        set(value) = lastUsed.set(KEY_STANDARD_ROUNDS_LAST_USED, value
            .map { (key, value) -> "$key:$value" }
            .joinToString(","))

    var inputSummaryConfiguration: SummaryConfiguration
        get() {
            val config = SummaryConfiguration()
            config.showEnd = preferences[KEY_INPUT_SUMMARY_SHOW_END, true]
            config.showRound = preferences[KEY_INPUT_SUMMARY_SHOW_ROUND, true]
            config.showTraining = preferences[KEY_INPUT_SUMMARY_SHOW_TRAINING, false]
            config.showAverage = preferences[KEY_INPUT_SUMMARY_SHOW_AVERAGE, true]
            config.averageScope =
                    ETrainingScope.valueOf(preferences[KEY_INPUT_SUMMARY_AVERAGE_OF, "ROUND"])
            return config
        }
        set(value) {
            preferences.edit()
                .putBoolean(KEY_INPUT_SUMMARY_SHOW_END, value.showEnd)
                .putBoolean(KEY_INPUT_SUMMARY_SHOW_ROUND, value.showRound)
                .putBoolean(KEY_INPUT_SUMMARY_SHOW_TRAINING, value.showTraining)
                .putBoolean(KEY_INPUT_SUMMARY_SHOW_AVERAGE, value.showAverage)
                .putString(KEY_INPUT_SUMMARY_AVERAGE_OF, value.averageScope.name)
                .apply()
        }

    var scoreConfiguration: Score.Configuration
        get() {
            val config = Score.Configuration()
            config.showReachedScore = preferences[KEY_OVERVIEW_SHOW_REACHED_SCORE, true]
            config.showTotalScore = preferences[KEY_OVERVIEW_SHOW_TOTAL_SCORE, true]
            config.showPercentage = preferences[KEY_OVERVIEW_SHOW_PERCENTAGE, false]
            config.showAverage = preferences[KEY_OVERVIEW_SHOW_ARROW_AVERAGE, false]
            return config
        }
        set(value) {
            preferences.edit()
                .putBoolean(KEY_OVERVIEW_SHOW_REACHED_SCORE, value.showReachedScore)
                .putBoolean(KEY_OVERVIEW_SHOW_TOTAL_SCORE, value.showTotalScore)
                .putBoolean(KEY_OVERVIEW_SHOW_PERCENTAGE, value.showPercentage)
                .putBoolean(KEY_OVERVIEW_SHOW_ARROW_AVERAGE, value.showAverage)
                .apply()
        }

    val scoreboardConfiguration: ScoreboardConfiguration
        get() {
            val config = ScoreboardConfiguration()
            config.showTitle = preferences["scoreboard_title", true]
            config.showProperties = preferences["scoreboard_properties", true]
            config.showTable = preferences["scoreboard_table", true]
            config.showStatistics = preferences["scoreboard_statistics", true]
            config.showComments = preferences["scoreboard_comments", true]
            config.showPointsColored = preferences["scoreboard_points_colored", true]
            config.showSignature = preferences["scoreboard_signature", true]
            return config
        }

    var inputKeepAboveLockscreen: Boolean
        get() = preferences[KEY_INPUT_KEEP_ABOVE_LOCKSCREEN, true]
        set(value) = preferences.set(KEY_INPUT_KEEP_ABOVE_LOCKSCREEN, value)

    var donated: Boolean
        get() = preferences[KEY_DONATED, false]
        set(value) = preferences.set(KEY_DONATED, value)

    var language: String
        get() = preferences[KEY_LANGUAGE, ""]
        set(value) = preferences.set(KEY_LANGUAGE, value)

    var shouldShowIntroActivity: Boolean
        get() = preferences[KEY_INTRO_SHOWED, true]
        set(value) = preferences.set(KEY_INTRO_SHOWED, value)

    fun shouldSortTarget(target: Target): Boolean {
        return preferences.getBoolean(KEY_OVERVIEW_SHOT_SORTING, true)
                && (target.model.faceCount == 1 || preferences.getBoolean(
            KEY_OVERVIEW_SHOT_SORTING_SPOT,
            false
        ))
                && target.getScoringStyle() !is ArrowAwareScoringStyle
    }
}
