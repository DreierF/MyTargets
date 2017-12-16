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

package de.dreier.mytargets.shared.models;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import de.dreier.mytargets.shared.R;
import de.dreier.mytargets.shared.SharedApplicationInstance;

public enum EBowType {
    RECURVE_BOW(0, R.string.recurve_bow, R.drawable.ic_compat_bow_recurve_white_24dp),
    COMPOUND_BOW(1, R.string.compound_bow, R.drawable.ic_compat_bow_compound_white_24dp),
    LONG_BOW(2, R.string.long_bow, R.drawable.ic_compat_bow_long_white_24dp),
    BARE_BOW(3, R.string.bare_bow, R.drawable.ic_compat_bow_bare_white_24dp),
    HORSE_BOW(4, R.string.horse_bow, R.drawable.ic_compat_bow_horse_white_24dp),
    YUMI(5, R.string.yumi, R.drawable.ic_compat_bow_yumi_white_24dp);

    private final int id;
    private final int name;
    @DrawableRes
    private final int drawable;

    EBowType(int id, @StringRes int name, @DrawableRes int drawable) {
        this.id = id;
        this.name = name;
        this.drawable = drawable;
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

    @Override
    public String toString() {
        return SharedApplicationInstance.Companion.getStr(name);
    }

    public int getId() {
        return id;
    }

    public int getDrawable() {
        return drawable;
    }

    public boolean showSize() {
        return true;
    }

    public boolean showBraceHeight() {
        return this != COMPOUND_BOW;
    }

    public boolean showTiller() {
        return this == RECURVE_BOW || this == BARE_BOW;
    }

    public boolean showLimbs() {
        return this == RECURVE_BOW || this == BARE_BOW;
    }

    public boolean showSight() {
        return this == RECURVE_BOW || this == COMPOUND_BOW;
    }

    public boolean showDrawWeight() {
        return true;
    }

    public boolean showStabilizer() {
        return this == RECURVE_BOW || this == COMPOUND_BOW;
    }

    public boolean showClicker() {
        return this == RECURVE_BOW;
    }

    public boolean showButton() {
        return this == RECURVE_BOW;
    }

    public boolean showNockingPoint() {
        return this == RECURVE_BOW || this == BARE_BOW;
    }

    public boolean showString() {
        return true;
    }

    public boolean showLetoffWeight() {
        return this == COMPOUND_BOW;
    }

    public boolean showArrowRest() {
        return this == RECURVE_BOW || this == COMPOUND_BOW;
    }

    public boolean showCamSetting() {
        return this == COMPOUND_BOW;
    }

    public boolean showScopeMagnification() {
        return this == COMPOUND_BOW;
    }
}
