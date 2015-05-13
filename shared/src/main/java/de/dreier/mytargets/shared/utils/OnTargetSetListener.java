package de.dreier.mytargets.shared.utils;

import de.dreier.mytargets.shared.models.Passe;

public interface OnTargetSetListener {
    long onTargetSet(Passe passe, boolean remote);
}
