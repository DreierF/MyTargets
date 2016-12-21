/*
 * Copyright (C) 2016 Florian Dreier
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

package de.dreier.mytargets.shared.models;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.SharedApplicationInstance;

public enum EBowType {
    RECURVE_BOW(0, R.string.recurve_bow, R.drawable.ic_compat_bow_recurve_grey600_24dp),
    COMPOUND_BOW(1, R.string.compound_bow, R.drawable.ic_compat_bow_compound_grey600_24dp),
    LONG_BOW(2, R.string.long_bow, R.drawable.ic_compat_bow_long_grey600_24dp),
    BARE_BOW(3, R.string.bare_bow, R.drawable.ic_compat_bow_bare_grey600_24dp),
    HORSE_BOW(4, R.string.horse_bow, R.drawable.ic_compat_bow_horse_grey_600_24dp),
    YUMI(5, R.string.yumi, R.drawable.ic_compat_bow_yumi_grey600_24dp);

    private final int id;
    private final int name;
    @DrawableRes
    private final int drawable;

    EBowType(int id, @StringRes int name, @DrawableRes int drawable) {
        this.id = id;
        this.name = name;
        this.drawable = drawable;
    }

    @Override
    public String toString() {
        return SharedApplicationInstance.get(name);
    }

    public int getId() {
        return id;
    }

    public static EBowType fromId(int id) {
        switch (id) {
            case 0:
                return RECURVE_BOW;
            case 1:
                return COMPOUND_BOW;
            case 2:
                return LONG_BOW;
            case 3:
                return BARE_BOW;
            case 4:
                return HORSE_BOW;
            case 5:
                return YUMI;
            default:
                return null;
        }
    }

    public int getDrawable() {
        return drawable;
    }
}
