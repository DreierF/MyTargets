package de.dreier.mytargets.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import de.dreier.mytargets.R;
import de.dreier.mytargets.fragments.SettingsFragment;
import de.dreier.mytargets.utils.IntentWrapper;

public class SettingsActivity extends SimpleFragmentActivityBase implements
        PreferenceFragmentCompat.OnPreferenceStartScreenCallback {

    public static IntentWrapper getIntent(String subScreenKey) {
        return new IntentWrapper(SettingsActivity.class)
                .with(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, subScreenKey);
    }

    @Override
    public Fragment instantiateFragment() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().addOnBackStackChangedListener(
                () -> {
                    FragmentManager manager = getSupportFragmentManager();
                    if (manager != null) {
                        SettingsFragment currFrag = (SettingsFragment) manager
                                .findFragmentById(R.id.content);

                        currFrag.onFragmentResume();
                    }
                });
    }

    @Override
    public boolean onPreferenceStartScreen(PreferenceFragmentCompat preferenceFragmentCompat,
                                           PreferenceScreen preferenceScreen) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, preferenceScreen.getKey());
        fragment.setArguments(args);
        ft.add(R.id.content, fragment, preferenceScreen.getKey());
        ft.addToBackStack(preferenceScreen.getKey());
        ft.commit();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }
}
