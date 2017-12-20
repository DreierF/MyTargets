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
package de.dreier.mytargets.shared.targets

import de.dreier.mytargets.shared.models.Target
import de.dreier.mytargets.shared.targets.models.*
import java.util.*

object TargetFactory {

    private val list: MutableList<TargetModelBase>

    private var idIndexLookup: Array<Int?>

    val comparator: Comparator<Target>
        get() = compareBy { idIndexLookup[(it.id).toInt()]!! }

    init {
        list = ArrayList()
        list.add(WAFull())
        list.add(WA6Ring())
        list.add(WA5Ring())
        list.add(WA3Ring())
        list.add(WAVertical3Spot())
        list.add(WAVegas3Spot())
        list.add(WA3Ring3Spot())
        list.add(WADanage3Spot())
        list.add(WADanage6Spot())
        list.add(NFAAIndoor())
        list.add(NFAAIndoor5Spot())
        list.add(HitOrMiss())
        list.add(Beursault())
        list.add(SCAPeriod())
        list.add(Worcester())
        list.add(DBSCBlowpipe())
        list.add(WAField())
        list.add(WAField3Spot())
        list.add(NFAAField())
        list.add(NFAAExpertField())
        list.add(NFAAHunter())
        list.add(IFAAAnimal())
        list.add(NFAAAnimal())
        list.add(NFASField())
        list.add(ASA3D())
        list.add(ASA3D14())
        list.add(IBO3D())
        list.add(NFAS3D())
        list.add(DAIR3D())
    }

    init {
        idIndexLookup = arrayOfNulls(list.size)
        for (i in list.indices) {
            idIndexLookup[list[i].id.toInt()] = i
        }
    }

    fun getList(): List<TargetModelBase> {
        return list
    }

    fun getList(target: Target): List<TargetModelBase> {
        val out = ArrayList<TargetModelBase>()
        if (target.id < 7L) {
            val til = if (target.diameter!!.value <= 60) 7L else 4L
            for (i in 0 until til) {
                out.add(list[i.toInt()])
            }
        } else if (target.id == 10L || target.id == 11L) {
            out.add(NFAAIndoor())
            out.add(NFAAIndoor5Spot())
        } else {
            out.add(list[idIndexLookup[(target.id).toInt()]!!])
        }
        return out
    }

    fun getTarget(id: Int): TargetModelBase {
        return list[idIndexLookup[id]!!]
    }
}
