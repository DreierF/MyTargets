package de.dreier.mytargets.interfaces;

public interface PartitionDelegate<T> {
    long getParentId(T child);
}
