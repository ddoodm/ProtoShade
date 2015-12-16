package com.id11688025.majorassignment;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

/**
 * The activity that displays the application's preferences
 */
public class MainPreferenceActivity extends PreferenceActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add preferences from XML layout
        addPreferencesFromResource(R.xml.preferences);

        // Notify the sender when the model preference changes
        findPreference(Constants.PREFERENCE_MODEL).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                setResult(Constants.RESULT_CODE_CHANGED);
                return true;
            }
        });
    }
}
