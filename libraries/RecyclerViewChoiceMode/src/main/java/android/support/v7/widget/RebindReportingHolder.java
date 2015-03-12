package android.support.v7.widget;

import android.view.View;

/**
 * ViewHolder with a callback for when it is rebound. Please use judiciously.
 */
public abstract class RebindReportingHolder extends RecyclerView.ViewHolder {

    public RebindReportingHolder(View itemView) {
        super(itemView);
    }

    /**
     * Called when the ViewHolder is rebound to another item.
     */
    protected abstract void onRebind();

    @Override
    void setFlags(int flags, int mask) {
        super.setFlags(flags, mask);
        int setFlags = mask & flags;
        checkFlags(setFlags);
    }

    @Override
    void addFlags(int flags) {
        super.addFlags(flags);
        checkFlags(flags);
    }

    private void checkFlags(int setFlags) {
        if (isRelevantFlagSet(setFlags)) {
            onRebind();
        }
    }

    private static boolean isRelevantFlagSet(int flag) {
        for (Integer value : new int[] { FLAG_BOUND, FLAG_CHANGED, FLAG_UPDATE, FLAG_RETURNED_FROM_SCRAP }) {
            if ((flag & value) == value) {
                return true;
            }
        }

        return false;
    }

    @Override
    void offsetPosition(int offset, boolean applyToPreLayout) {
        super.offsetPosition(offset, applyToPreLayout);
        onRebind();
    }
}
