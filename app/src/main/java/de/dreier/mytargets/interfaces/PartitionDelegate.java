package de.dreier.mytargets.interfaces;

public interface PartitionDelegate<PARENT, CHILD> {
    PARENT getParent(CHILD child);
}
