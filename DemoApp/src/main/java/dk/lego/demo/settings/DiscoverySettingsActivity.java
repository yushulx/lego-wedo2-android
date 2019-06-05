package dk.lego.demo.settings;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import dk.lego.demo.base.BaseActivity;

public class DiscoverySettingsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getFragmentManager();
        Fragment mainSettingsFragment = fm.findFragmentById(android.R.id.content);
        if (mainSettingsFragment == null) {
            mainSettingsFragment = DiscoverySettingsFragment.newInstance();
        }

        replaceFragmentNoBackStack(android.R.id.content, mainSettingsFragment);
    }
}
