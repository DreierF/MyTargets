package com.bignerdranch.android.recyclerviewchoicemode;

import java.util.List;

public class SingleSelector extends MultiSelector {

    @Override
    public void setSelected(int position, long id, boolean isSelected) {
        if (isSelected) {
            for (Integer selectedPosition : getSelectedPositions()) {
                if (selectedPosition != position) {
                    super.setSelected(selectedPosition, 0, false);
                }
            }
        }
        super.setSelected(position, id, isSelected);
    }

    public int getSelectedPosition() {
        List<Integer> pos = getSelectedPositions();
        return pos.isEmpty() ? -1 : pos.get(0);
    }
}
