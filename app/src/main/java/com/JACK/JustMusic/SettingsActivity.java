package com.JACK.JustMusicWW;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if ( key.equals(getString( R.string.setting_lp_loop_mode_list)))
            updateListLoopModesSummary();
        else if ( key.equals(getString( R.string.setting_lp_wake_lock_mode_list)))
            updateListWakeLockModesSummary();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        prefs.registerOnSharedPreferenceChangeListener(this);

        updateListLoopModesSummary();
        updateListWakeLockModesSummary();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences( getApplicationContext());
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    private void updateListLoopModesSummary() {
        ListPreference listLoopMode =
                (ListPreference) findPreference(getString( R.string.setting_lp_loop_mode_list));
        listLoopMode.setSummary(
                getString(R.string.setting_lp_loop_mode_list_summary)
                        + " "
                        + listLoopMode.getEntry());
    }

    private void updateListWakeLockModesSummary() {
        ListPreference listWakeLockMode =
                (ListPreference) findPreference(getString( R.string.setting_lp_wake_lock_mode_list));
        listWakeLockMode.setSummary(
                getString(R.string.setting_lp_wake_lock_mode_list_summary)
                        + " "
                        + listWakeLockMode.getEntry());
    }
}