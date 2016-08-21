package de.dreier.mytargets.utils;

import android.os.Bundle;

import de.dreier.mytargets.utils.multiselector.MultiSelector;
import icepick.Bundler;

public class SelectorBundler implements Bundler<MultiSelector> {
    public void put(String key, MultiSelector value, Bundle bundle) {
        bundle.putBundle(key, value.saveSelectionStates());
    }

    public MultiSelector get(String key, Bundle bundle) {
        MultiSelector selector = new MultiSelector();
        selector.restoreSelectionStates(bundle.getBundle(key));
        return selector;
    }
}