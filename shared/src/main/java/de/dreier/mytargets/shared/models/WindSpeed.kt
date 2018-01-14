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
data class WindSpeed(
        override var id: Long,
        var name: String,
        var drawable: Int
) : IIdProvider, Comparable<WindSpeed>, Parcelable {

    override fun compareTo(other: WindSpeed) = compareBy(WindSpeed::id).compare(this, other)

    companion object {
        fun getList(context: Context): List<WindSpeed> {
            val list = ArrayList<WindSpeed>()
            list.add(WindSpeed(0, context.getString(R.string.bft_0),
                    R.drawable.ic_bft_0_black_24dp))
            list.add(WindSpeed(1, context.getString(R.string.bft_1),
                    R.drawable.ic_bft_1_black_24dp))
            list.add(WindSpeed(2, context.getString(R.string.bft_2),
                    R.drawable.ic_bft_2_black_24dp))
            list.add(WindSpeed(3, context.getString(R.string.bft_3),
                    R.drawable.ic_bft_3_black_24dp))
            list.add(WindSpeed(4, context.getString(R.string.bft_4),
                    R.drawable.ic_bft_4_black_24dp))
            list.add(WindSpeed(5, context.getString(R.string.bft_5),
                    R.drawable.ic_bft_5_black_24dp))
            list.add(WindSpeed(6, context.getString(R.string.bft_6),
                    R.drawable.ic_bft_6_black_24dp))
            list.add(WindSpeed(7, context.getString(R.string.bft_7),
                    R.drawable.ic_bft_7_black_24dp))
            list.add(WindSpeed(8, context.getString(R.string.bft_8),
                    R.drawable.ic_bft_8_black_24dp))
            list.add(WindSpeed(9, context.getString(R.string.bft_9),
                    R.drawable.ic_bft_9_black_24dp))
            return list
        }
    }
}
