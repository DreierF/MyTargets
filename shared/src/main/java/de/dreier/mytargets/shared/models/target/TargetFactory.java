/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models.target;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class TargetFactory {

    private static List<Target> list;

    public static Target createTarget(Context context, long id, int scoringStyle) {
        Target t;
        switch ((int)id) {
            case 0: // WA Full
                t = new WAFullTarget(context);
                break;
            case 1: // WA 6 Ring
                t = new WA6RingTarget(context);
                break;
            case 2: // WA 5 Ring
                t = new WA5RingTarget(context);
                break;
            case 3: // WA 3 Ring
                t = new WA3RingTarget(context);
                break;
            case 4: // Vegas 3 Spot
                t = new Vegas3Spot(context);
                break;
            case 5: // Vertical 3 Spot
                t = new Vertical3Spot(context);
                break;
            case 6: // WA 3 Ring 3 Spot
                t = new WA3Ring3Spot(context);
                break;
            case 7: // NFAA Field
                t = new NFAAField(context);
                break;
            case 8: // NFAA Expert Field
                t = new NFAAExpertField(context);
                break;
            case 9: // NFAA Hunter
                t = new NFAAHunter(context);
                break;
            case 10: // NFAA Indoor
                t = new NFAAIndoor(context);
                break;
            case 11: // NFAA Indoor 5 Spot
                t = new NFAAIndoor5Spot(context);
                break;
            case 12: // Worcester
                t = new Worcester(context);
                break;
            case 13: // WA Field
                t = new WAField(context);
                break;
            case 14: // Hit or miss
                t = new HitOrMiss(context);
                break;
            case 15: // 3D ASA
                t = new ASA3D(context);
                break;
            case 16: // 3D ASA 14
                t = new ASA3D14(context);
                break;
            case 17: // 3D IBO
                t = new IBO3D(context);
                break;
            case 18: // 3D NFAS
                t = new NFAS3D(context);
                break;
            case 19: // 3D Dair
                t = new DAIR3D(context);
                break;
            case 20: // IFAA Animal
                t = new IFAAAnimal(context);
                break;
            case 21: // NFAA Animal
                t = new NFAAAnimal(context);
                break;
            case 22: // NFAS Field
                t = new NFASField(context);
                break;
            case 23: // Beursault
                t = new Beursault(context);
                break;
            default:
                throw new IllegalArgumentException("id out of range");
        }
        t.scoringStyle = scoringStyle;
        t.size = t.getDiameters()[0];
        return t;
    }

    public static List<Target> getList(Context context) {
        if (list == null) {
            list = new ArrayList<>();
            for (int i = 0; i < 24; i++) {
                list.add(createTarget(context, i, 0));
            }
        }
        return list;
    }

    public static List<Target> getList(Context context, Target t) {
        getList(context);
        List<Target> out = new ArrayList<>();
        if (t.id < 7) {
            int til = t.size.value <= 60 ? 7 : 4;
            for (int i = 0; i < til; i++) {
                out.add(list.get(i));
            }
        } else if (t.id == 10 || t.id == 11) {
            out.add(list.get(10));
            out.add(list.get(11));
        } else {
            out.add(list.get((int) t.id));
        }
        return out;
    }
}
