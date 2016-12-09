package de.dreier.mytargets.interfaces;

import java.util.List;

import de.dreier.mytargets.shared.models.db.End;


public interface OnEndUpdatedListener {
    void onEndUpdated(End p, List<End> old);
}
