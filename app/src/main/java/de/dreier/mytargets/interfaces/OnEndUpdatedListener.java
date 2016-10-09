package de.dreier.mytargets.interfaces;

import java.util.List;

import de.dreier.mytargets.shared.models.Passe;

public interface OnEndUpdatedListener {
    void onEndUpdated(Passe p, List<Passe> old);
}
