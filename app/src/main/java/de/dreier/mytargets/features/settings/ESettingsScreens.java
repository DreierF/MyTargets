package de.dreier.mytargets.features.settings;

import de.dreier.mytargets.features.settings.backup.BackupSettingsFragment;

public enum ESettingsScreens {
    MAIN(MainSettingsFragment.class),
    INPUT(InputSettingsFragment.class),
    TIMER(TimerSettingsFragment.class),
    SCOREBOARD(ScoreboardSettingsFragment.class),
    BACKUP(BackupSettingsFragment.class);

    private final Class<? extends SettingsFragmentBase> settingsFragment;

    ESettingsScreens(Class<? extends SettingsFragmentBase> settingsFragment) {
        this.settingsFragment = settingsFragment;
    }

    public static ESettingsScreens from(String key) {
        switch (key) {
            case "input":
                return INPUT;
            case "timer":
                return TIMER;
            case "scoreboard":
                return SCOREBOARD;
            case "backup":
                return BACKUP;
            default:
                return MAIN;
        }
    }

    public SettingsFragmentBase create() {
        try {
            return settingsFragment.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
            // Should never happen, because Fragments should
            // always have a zero argument constructor.
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            // Should never happen, because Fragments should
            // always have a public constructor.
        }
        // Otherwise just show main fragment
        return new MainSettingsFragment();
    }

    public String getKey() {
        return name().toLowerCase();
    }
}
