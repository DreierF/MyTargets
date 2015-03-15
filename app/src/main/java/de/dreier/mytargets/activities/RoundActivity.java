package de.dreier.mytargets.activities;

import android.support.v4.app.Fragment;

import de.dreier.mytargets.fragments.RoundFragment;

/**
 * Created by Florian on 15.03.2015.
 */
public class RoundActivity extends SimpleFragmentActivity {

    @Override
    public Fragment instantiateFragment() {
        return new RoundFragment();
    }
}
