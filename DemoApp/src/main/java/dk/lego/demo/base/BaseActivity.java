package dk.lego.demo.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;

@SuppressLint("Registered")
public class BaseActivity extends Activity {

    private void replaceFragment(int id, Fragment newFragment, int transition, String backStackName) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(id, newFragment);
        if (transition != -1) {
            transaction.setTransition(transition);
        } else {
            transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
        }
        if (backStackName != null) {
            transaction.addToBackStack(backStackName);
        }
        transaction.commit();
    }

    public void replaceFragmentNoBackStack(int id, Fragment newFragment) {
        replaceFragment(id, newFragment, -1, null);
    }
}
