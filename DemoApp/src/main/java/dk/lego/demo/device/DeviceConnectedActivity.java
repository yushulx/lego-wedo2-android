package dk.lego.demo.device;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import dk.lego.demo.base.BaseActivity;

public class DeviceConnectedActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getFragmentManager();
        Fragment deviceConnectedFragment = fm.findFragmentById(android.R.id.content);
        if (deviceConnectedFragment == null) {
            deviceConnectedFragment = DeviceConnectedFragment.newInstance();
        }

        replaceFragmentNoBackStack(android.R.id.content, deviceConnectedFragment);
    }
}
