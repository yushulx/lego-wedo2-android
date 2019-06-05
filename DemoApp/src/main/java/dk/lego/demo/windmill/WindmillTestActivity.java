package dk.lego.demo.windmill;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import dk.lego.demo.base.BaseActivity;

public class WindmillTestActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getFragmentManager();
        Fragment windmillTestFragment = fm.findFragmentById(android.R.id.content);
        if (windmillTestFragment == null) {
            windmillTestFragment = WindmillTestFragment.newInstance();
        }

        replaceFragmentNoBackStack(android.R.id.content, windmillTestFragment);
    }
}
