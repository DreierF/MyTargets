/*
 * MyTargets Archery
 *
 * Copyright (C) 2015 Florian Dreier
 * All rights reserved
 */
package de.dreier.mytargets.shared.models.target;

import java.util.ArrayList;
import java.util.List;

public class TargetFactory {

    private static List<TargetDrawable> list;

    public static TargetDrawable createTarget(long id, int scoringStyle) {
        TargetDrawable t;
        switch ((int)id) {
            case 0: // WA Full
                t = new WAFullTarget();
                break;
            case 1: // WA 6 Ring
                t = new WA6RingTarget();
                break;
            case 2: // WA 5 Ring
                t = new WA5RingTarget();
                break;
            case 3: // WA 3 Ring
                t = new WA3RingTarget();
                break;
            case 4: // Vegas 3 Spot
                t = new Vegas3Spot();
                break;
            case 5: // Vertical 3 Spot
                t = new Vertical3Spot();
                break;
            case 6: // WA 3 Ring 3 Spot
                t = new WA3Ring3Spot();
                break;
            case 7: // NFAA Field
                t = new NFAAField();
                break;
            case 8: // NFAA Expert Field
                t = new NFAAExpertField();
                break;
            case 9: // NFAA Hunter
                t = new NFAAHunter();
                break;
            case 10: // NFAA Indoor
                t = new NFAAIndoor();
                break;
            case 11: // NFAA Indoor 5 Spot
                t = new NFAAIndoor5Spot();
                break;
            case 12: // Worcester
                t = new Worcester();
                break;
            case 13: // WA Field
                t = new WAField();
                break;
            case 14: // Hit or miss
                t = new HitOrMiss();
                break;
            case 15: // 3D ASA
                t = new ASA3D();
                break;
            case 16: // 3D ASA 14
                t = new ASA3D14();
                break;
            case 17: // 3D IBO
                t = new IBO3D();
                break;
            case 18: // 3D NFAS
                t = new NFAS3D();
                break;
            case 19: // 3D Dair
                t = new DAIR3D();
                break;
            case 20: // IFAA Animal
                t = new IFAAAnimal();
                break;
            case 21: // NFAA Animal
                t = new NFAAAnimal();
                break;
            case 22: // NFAS Field
                t = new NFASField();
                break;
            case 23: // Beursault
                t = new Beursault();
                break;
            case 24: // SCA Period
                t = new SCAPeriodTarget();
                break;
            default:
                throw new IllegalArgumentException("id out of range");
        }
        t.target.scoringStyle = scoringStyle;
        t.target.size = t.getDiameters()[0];
        return t;
    }

    public static List<TargetDrawable> getList() {
        if (list == null) {
            list = new ArrayList<>();
            for (int i = 0; i < 25; i++) {
                list.add(createTarget(i, 0));
            }
        }
        return list;
    }

    public static List<TargetDrawable> getList(TargetDrawable t) {
        getList();
        List<TargetDrawable> out = new ArrayList<>();
        if (t.target.id < 7) {
            int til = t.target.size.value <= 60 ? 7 : 4;
            for (int i = 0; i < til; i++) {
                out.add(list.get(i));
            }
        } else if (t.target.id == 10 || t.target.id == 11) {
            out.add(list.get(10));
            out.add(list.get(11));
        } else {
            out.add(list.get((int) t.target.id));
        }
        return out;
    }
}
