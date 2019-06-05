package dk.lego.demo.service;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import dk.lego.demo.base.BaseActivity;

public class ServiceBaseInputFormatDetailsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getFragmentManager();
        Fragment serviceInputFormatEditingFragment = fm.findFragmentById(android.R.id.content);
        if (serviceInputFormatEditingFragment == null) {
            serviceInputFormatEditingFragment = ServiceBaseInputFormatDetailsFragment.newInstance();
        }

        replaceFragmentNoBackStack(android.R.id.content, serviceInputFormatEditingFragment);
    }
}
