package de.dreier.mytargets.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import de.dreier.mytargets.features.settings.ESettingsScreens;
import de.dreier.mytargets.features.settings.MainSettingsFragment;
import de.dreier.mytargets.features.settings.SettingsFragmentBase;
import de.dreier.mytargets.utils.IntentWrapper;

public class SettingsActivity extends SimpleFragmentActivityBase implements
        PreferenceFragmentCompat.OnPreferenceStartScreenCallback {

    public static IntentWrapper getIntent(ESettingsScreens subScreen) {
        return new IntentWrapper(SettingsActivity.class)
                .with(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, subScreen.getKey());
    }

    @Override
    public Fragment instantiateFragment() {
        return new MainSettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().addOnBackStackChangedListener(
                () -> {
                    FragmentManager manager = getSupportFragmentManager();
                    if (manager != null) {
                        SettingsFragmentBase currFrag = (SettingsFragmentBase) manager
                                .findFragmentById(android.R.id.content);

                        currFrag.onFragmentResume();
                    }
                });
    }

    @Override
    public boolean onPreferenceStartScreen(PreferenceFragmentCompat preferenceFragmentCompat,
                                           PreferenceScreen preferenceScreen) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ESettingsScreens screen = ESettingsScreens.from(preferenceScreen.getKey());
        SettingsFragmentBase fragment = screen.create();
        Bundle args = new Bundle();
        args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, preferenceScreen.getKey());
        fragment.setArguments(args);
        ft.add(android.R.id.content, fragment, preferenceScreen.getKey());
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
