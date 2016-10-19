package de.dreier.mytargets.shared.models;

import java.io.Serializable;

public interface IIdSettable extends IIdProvider, Serializable {
    void setId(Long id);
}