package dk.lego.demo.discovery;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import dk.lego.demo.base.BaseActivity;


public class DiscoveryActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getFragmentManager();
        Fragment mainFragment = fm.findFragmentById(android.R.id.content);
        if (mainFragment == null) {
            mainFragment = DiscoveryFragment.newInstance();
        }

        replaceFragmentNoBackStack(android.R.id.content, mainFragment);
    }
}
