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
package de.dreier.mytargets.shared.targets;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import de.dreier.mytargets.shared.models.Target;
import de.dreier.mytargets.shared.targets.models.ASA3D;
import de.dreier.mytargets.shared.targets.models.ASA3D14;
import de.dreier.mytargets.shared.targets.models.Beursault;
import de.dreier.mytargets.shared.targets.models.DAIR3D;
import de.dreier.mytargets.shared.targets.models.DBSCBlowpipe;
import de.dreier.mytargets.shared.targets.models.HitOrMiss;
import de.dreier.mytargets.shared.targets.models.IBO3D;
import de.dreier.mytargets.shared.targets.models.IFAAAnimal;
import de.dreier.mytargets.shared.targets.models.NFAAAnimal;
import de.dreier.mytargets.shared.targets.models.NFAAExpertField;
import de.dreier.mytargets.shared.targets.models.NFAAField;
import de.dreier.mytargets.shared.targets.models.NFAAHunter;
import de.dreier.mytargets.shared.targets.models.NFAAIndoor;
import de.dreier.mytargets.shared.targets.models.NFAAIndoor5Spot;
import de.dreier.mytargets.shared.targets.models.NFAS3D;
import de.dreier.mytargets.shared.targets.models.NFASField;
import de.dreier.mytargets.shared.targets.models.SCAPeriod;
import de.dreier.mytargets.shared.targets.models.TargetModelBase;
import de.dreier.mytargets.shared.targets.models.WA3Ring;
import de.dreier.mytargets.shared.targets.models.WA3Ring3Spot;
import de.dreier.mytargets.shared.targets.models.WA5Ring;
import de.dreier.mytargets.shared.targets.models.WA6Ring;
import de.dreier.mytargets.shared.targets.models.WADanage3Spot;
import de.dreier.mytargets.shared.targets.models.WADanage6Spot;
import de.dreier.mytargets.shared.targets.models.WAField;
import de.dreier.mytargets.shared.targets.models.WAField3Spot;
import de.dreier.mytargets.shared.targets.models.WAFull;
import de.dreier.mytargets.shared.targets.models.WAVegas3Spot;
import de.dreier.mytargets.shared.targets.models.WAVertical3Spot;
import de.dreier.mytargets.shared.targets.models.Worcester;

public class TargetFactory {

    @NonNull
    private static final List<TargetModelBase> list;

    static {
        list = new ArrayList<>();
        list.add(new WAFull());
        list.add(new WA6Ring());
        list.add(new WA5Ring());
        list.add(new WA3Ring());
        list.add(new WAVertical3Spot());
        list.add(new WAVegas3Spot());
        list.add(new WA3Ring3Spot());
        list.add(new WADanage3Spot());
        list.add(new WADanage6Spot());
        list.add(new NFAAIndoor());
        list.add(new NFAAIndoor5Spot());
        list.add(new HitOrMiss());
        list.add(new Beursault());
        list.add(new SCAPeriod());
        list.add(new Worcester());
        list.add(new DBSCBlowpipe());
        list.add(new WAField());
        list.add(new WAField3Spot());
        list.add(new NFAAField());
        list.add(new NFAAExpertField());
        list.add(new NFAAHunter());
        list.add(new IFAAAnimal());
        list.add(new NFAAAnimal());
        list.add(new NFASField());
        list.add(new ASA3D());
        list.add(new ASA3D14());
        list.add(new IBO3D());
        list.add(new NFAS3D());
        list.add(new DAIR3D());
    }

    private static Integer[] idIndexLookup;

    static {
        idIndexLookup = new Integer[list.size()];
        for (int i = 0; i < list.size(); i++) {
            idIndexLookup[(int) (long) list.get(i).getId()] = i;
        }
    }

    @NonNull
    public static List<TargetModelBase> getList() {
        return list;
    }

    @NonNull
    public static List<TargetModelBase> getList(@NonNull Target target) {
        List<TargetModelBase> out = new ArrayList<>();
        if (target.id < 7) {
            int til = target.size.value <= 60 ? 7 : 4;
            for (int i = 0; i < til; i++) {
                out.add(list.get(i));
            }
        } else if (target.id == 10 || target.id == 11) {
            out.add(new NFAAIndoor());
            out.add(new NFAAIndoor5Spot());
        } else {
            out.add(list.get(idIndexLookup[target.id]));
        }
        return out;
    }

    public static TargetModelBase getTarget(int id) {
        return list.get(idIndexLookup[id]);
    }

    public static Comparator<Target> getComparator() {
        return (t1, t2) -> idIndexLookup[t1.id].compareTo(idIndexLookup[t2.id]);
    }
}
