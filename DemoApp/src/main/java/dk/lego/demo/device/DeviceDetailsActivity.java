package dk.lego.demo.device;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import dk.lego.demo.base.BaseActivity;

public class DeviceDetailsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getFragmentManager();
        Fragment deviceDetailsFragment = fm.findFragmentById(android.R.id.content);
        if (deviceDetailsFragment == null) {
            deviceDetailsFragment = DeviceDetailsFragment.newInstance();
        }

        replaceFragmentNoBackStack(android.R.id.content, deviceDetailsFragment);
    }
}
