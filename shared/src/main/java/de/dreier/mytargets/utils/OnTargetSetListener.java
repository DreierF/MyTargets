package de.dreier.mytargets.utils;

import de.dreier.mytargets.models.Passe;

public interface OnTargetSetListener {
    public long onTargetSet(Passe passe, boolean remote);
}
