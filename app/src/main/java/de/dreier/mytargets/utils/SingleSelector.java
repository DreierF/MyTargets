package de.dreier.mytargets.utils;

import java.util.List;

public class SingleSelector extends com.bignerdranch.android.multiselector.SingleSelector {

    public int getSelectedPosition() {
        List<Integer> pos = getSelectedPositions();
        return pos.isEmpty() ? -1 : pos.get(0);
    }
}
