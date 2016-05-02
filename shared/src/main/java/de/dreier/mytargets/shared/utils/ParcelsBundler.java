package de.dreier.mytargets.shared.utils;

import android.os.Bundle;

import org.parceler.Parcels;

public class ParcelsBundler<T> implements icepick.Bundler<T> {
    @Override
    public void put(String s, T example, Bundle bundle) {
        bundle.putParcelable(s, Parcels.wrap(example));
    }

    @Override
    public T get(String s, Bundle bundle) {
        return Parcels.unwrap(bundle.getParcelable(s));
    }
}
