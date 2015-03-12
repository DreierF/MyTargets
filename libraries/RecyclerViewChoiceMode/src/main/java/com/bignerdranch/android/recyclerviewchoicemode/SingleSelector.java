package com.bignerdranch.android.recyclerviewchoicemode;

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
}
