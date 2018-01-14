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
package de.dreier.mytargets.shared.models

import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcelable
import de.dreier.mytargets.shared.R
import kotlinx.android.parcel.Parcelize
import java.util.*

@SuppressLint("ParcelCreator")
@Parcelize
data class WindDirection internal constructor(
        override var id: Long,
        val name: String,
        var drawable: Int
) : IIdProvider, Comparable<WindDirection>, Parcelable {

    override fun compareTo(other: WindDirection) = compareBy(WindDirection::id).compare(this, other)

    companion object {
        fun getList(context: Context): List<WindDirection> {
            val list = ArrayList<WindDirection>()
            list.add(WindDirection(0, context.getString(R.string.front),
                    R.drawable.ic_arrow_downward_black_24dp))
            list.add(WindDirection(1, context.getString(R.string.back),
                    R.drawable.ic_arrow_upward_black_24dp))
            list.add(WindDirection(2, context.getString(R.string.left),
                    R.drawable.ic_arrow_right_black_24px))
            list.add(WindDirection(3, context.getString(R.string.right),
                    R.drawable.ic_arrow_left_black_24dp))
            list.add(WindDirection(4, context.getString(R.string.left_front),
                    R.drawable.ic_arrow_bottom_right_black_24dp))
            list.add(WindDirection(5, context.getString(R.string.right_front),
                    R.drawable.ic_arrow_bottom_left_black_24dp))
            list.add(WindDirection(6, context.getString(R.string.left_back),
                    R.drawable.ic_arrow_up_right_black_24dp))
            list.add(WindDirection(7, context.getString(R.string.right_back),
                    R.drawable.ic_arrow_up_left_black_24dp))
            return list
        }
    }
}
