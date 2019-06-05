package dk.lego.demo.service;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import dk.lego.demo.base.BaseActivity;

public class ServiceTiltSensorDetailsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getFragmentManager();
        Fragment serviceDetailsFragment = fm.findFragmentById(android.R.id.content);
        if (serviceDetailsFragment == null) {
            serviceDetailsFragment = ServiceTiltSensorDetailsFragment.newInstance();
        }

        replaceFragmentNoBackStack(android.R.id.content, serviceDetailsFragment);
    }
}
