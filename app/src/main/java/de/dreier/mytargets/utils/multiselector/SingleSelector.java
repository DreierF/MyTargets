package de.dreier.mytargets.utils.multiselector;

/**
 * <p>A MultiSelector that only allows for one position at a time to be selected. </p>
 * <p>Any time {@link #setSelected(int, long, boolean)} is called, all other selected positions are set to false.</p>
 */
public class SingleSelector extends MultiSelector {

    private int selectedPosition = -1;

    @Override
    public void setSelected(int position, long id, boolean isSelected) {
        if (isSelected) {
            for (Long selectedId : getSelectedIds()) {
                if (selectedId != position) {
                    super.setSelected(selectedPosition, selectedId, false);
                }
            }
            selectedPosition = position;
        } else {
            selectedPosition = -1;
        }
        super.setSelected(position, id, isSelected);
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }
}