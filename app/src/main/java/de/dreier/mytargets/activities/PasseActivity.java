package de.dreier.mytargets.activities;

import android.support.v4.app.Fragment;

import de.dreier.mytargets.fragments.PasseFragment;

/**
 * Created by Florian on 15.03.2015.
 */
public class PasseActivity extends SimpleFragmentActivity {

    @Override
    public Fragment instantiateFragment() {
        return new PasseFragment();
    }
}
