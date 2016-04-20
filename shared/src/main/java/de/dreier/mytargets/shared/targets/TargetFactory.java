/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.targets;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.models.Target;

public class TargetFactory {

    private static List<TargetModelBase> list;

    static {
        list = new ArrayList<>();
        list.add(new WAFullTarget());
        list.add(new WA6RingTarget());
        list.add(new WA5RingTarget());
        list.add(new WA3RingTarget());
        list.add(new Vegas3Spot());
        list.add(new Vertical3Spot());
        list.add(new WA3Ring3Spot());
        list.add(new NFAAField());
        list.add(new NFAAExpertField());
        list.add(new NFAAHunter());
        list.add(new NFAAIndoor());
        list.add(new NFAAIndoor5Spot());
        list.add(new Worcester());
        list.add(new WAField());
        list.add(new HitOrMiss());
        list.add(new ASA3D());
        list.add(new ASA3D14());
        list.add(new IBO3D());
        list.add(new NFAS3D());
        list.add(new DAIR3D());
        list.add(new IFAAAnimal());
        list.add(new NFASField());
        list.add(new Beursault());
        list.add(new SCAPeriodTarget());
    }

    public static List<TargetModelBase> getList() {
        return list;
    }

    public static List<TargetModelBase> getList(Target target) {
        List<TargetModelBase> out = new ArrayList<>();
        if (target.id < 7) {
            int til = target.size.value <= 60 ? 7 : 4;
            for (int i = 0; i < til; i++) {
                out.add(list.get(i));
            }
        } else if (target.id == 10 || target.id == 11) {
            out.add(list.get(10));
            out.add(list.get(11));
        } else {
            out.add(list.get((int) target.id));
        }
        return out;
    }

    public static TargetModelBase getTarget(int id) {
        return list.get(id);
    }
}
