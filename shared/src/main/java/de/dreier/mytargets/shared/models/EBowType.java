package de.dreier.mytargets.shared.models;

import android.support.annotation.StringRes;

import de.dreier.mytargets.shared.SharedApplicationInstance;
import de.dreier.mytargets.shared.R;

public enum EBowType {
    RECURVE_BOW(0, R.string.recurve_bow),
    COMPOUND_BOW(1, R.string.compound_bow),
    LONG_BOW(2, R.string.long_bow),
    BARE_BOW(3, R.string.bare_bow),
    HORSE_BOW(4, R.string.horse_bow),
    YUMI(5, R.string.yumi);

    private final int id;
    private final int name;

    EBowType(int id, @StringRes int name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return SharedApplicationInstance.getContext().getString(name);
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
}
