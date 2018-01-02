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
package de.dreier.mytargets.shared.models

import android.annotation.SuppressLint
import android.os.Parcelable
import com.raizlabs.android.dbflow.sql.language.SQLite
import de.dreier.mytargets.shared.R
import de.dreier.mytargets.shared.SharedApplicationInstance
import de.dreier.mytargets.shared.models.db.*
import kotlinx.android.parcel.Parcelize
import java.util.*

@SuppressLint("ParcelCreator")
@Parcelize
data class Dimension(val value: Float, val unit: Unit?) : IIdProvider, Comparable<Dimension>, Parcelable {

    override fun compareTo(other: Dimension) =
            compareBy({ unit?.abbreviation }, Dimension::value).compare(this, other)

    override fun toString(): String {
        val context = SharedApplicationInstance.context
        if (value == -1f) {
            return context.getString(R.string.unknown)
        } else if (unit == null) {
            return when (value.toInt()) {
                Diameter.MINI_VALUE -> context.getString(R.string.mini)
                Diameter.SMALL_VALUE -> context.getString(R.string.small)
                Diameter.MEDIUM_VALUE -> context.getString(R.string.medium)
                Diameter.LARGE_VALUE -> context.getString(R.string.large)
                Diameter.XLARGE_VALUE -> context.getString(R.string.xlarge)
                else -> ""
            }
        }
        return Integer.toString(value.toInt()) + unit.toString()
    }

    override val id: Long
        get() = hashCode().toLong()

    fun convertTo(unit: Unit): Dimension {
        if (this.unit == null) {
            return Dimension((8f - this.value) * 4f, Unit.CENTIMETER).convertTo(unit)
        }
        val newValue = value * unit.factor / this.unit.factor
        return Dimension(newValue, unit)
    }

    enum class Unit(internal val abbreviation: String,
            /* factor <units> = 1 meter */
                    internal val factor: Float) {
        CENTIMETER("cm", 100f),
        INCH("in", 39.3701f),
        METER("m", 1f),
        YARDS("yd", 1.09361f),
        FEET("ft", 3.28084f),
        MILLIMETER("mm", 1000f);

        override fun toString(): String {
            return abbreviation
        }

        companion object {

            fun from(unit: String?): Unit? {
                return when (unit) {
                    "cm" -> CENTIMETER
                    "in" -> INCH
                    "m" -> METER
                    "yd" -> YARDS
                    "ft" -> FEET
                    "mm" -> MILLIMETER
                    else -> null
                }
            }

            fun toStringHandleNull(unit: Unit?): String? {
                return unit?.toString()
            }
        }
    }

    companion object {
        val UNKNOWN = Dimension(-1f, null as Dimension.Unit?)

        fun from(value: Float, unit: Unit?): Dimension {
            return Dimension(value, if (value < 0f) null else unit)
        }

        fun from(value: Float, unit: String?): Dimension {
            return from(value, Unit.from(unit))
        }

        /**
         * Returns a list of all distances that are either default values or used somewhere in the app
         *
         * @param distance Distance to add to the list (current selected value)
         * @param unit     Distances are only returned which match the specified unit
         * @return List of distances
         */
        fun getAll(distance: Dimension, unit: Unit): List<Dimension> {
            val distances = HashSet<Dimension>()

            distances.add(Dimension.UNKNOWN)

            // Add currently selected distance to list
            if (distance.unit == unit) {
                distances.add(distance)
            }

            // Get all distances used in Round or SightMark table
            distances.addAll(SQLite
                    .select(SightMark_Table.distance)
                    .from(SightMark::class.java)
                    .queryList()
                    .map { it.distance }
                    .filter { it.unit == unit }
                    .toSet())

            distances.addAll(SQLite
                    .select(RoundTemplate_Table.distance)
                    .from(RoundTemplate::class.java)
                    .queryList()
                    .map { it.distance }
                    .filter { it.unit == unit }
                    .toSet())

            distances.addAll(SQLite
                    .select(Round_Table.distance)
                    .from(Round::class.java)
                    .queryList()
                    .map { it.distance }
                    .filter { it.unit == unit }
                    .toSet())

            return ArrayList(distances)
        }
    }
}

