package dk.lego.demo.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;

import dk.lego.demo.R;
import dk.lego.demo.base.DemoApplication;

public class DiscoverySettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static DiscoverySettingsFragment newInstance() {
        return new DiscoverySettingsFragment();
    }

    public DiscoverySettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Define the settings file to use by this settings fragment
        this.getPreferenceManager().setSharedPreferencesName(getString(R.string.da_preference_file_key));

        // Load the da_preferences from an XML resource
        addPreferencesFromResource(R.xml.da_preferences);
    }

    @Override
    public void onResume() {
        super.onResume();

        DemoApplication.getInstance().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        initSummary(getPreferenceScreen());
    }

    @Override
    public void onPause() {
        super.onPause();

        DemoApplication.getInstance().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }


    private void initSummary(Preference preference) {
        if (preference instanceof PreferenceGroup) {
            PreferenceGroup preferenceGroup = (PreferenceGroup) preference;
            for (int i = 0; i < preferenceGroup.getPreferenceCount(); i++) {
                initSummary(preferenceGroup.getPreference(i));
            }
        } else {
            updatePrefSummary(preference);
        }
    }

    private void updatePrefSummary(Preference preference) {
        if (preference instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) preference;
            preference.setSummary(editTextPref.getText());
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        initSummary(getPreferenceScreen());
    }
}