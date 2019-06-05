package dk.lego.demo.base;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;

import dk.lego.demo.BuildConfig;
import dk.lego.demo.R;
import timber.log.Timber;

public class DemoApplication extends Application {

    private static DemoApplication instance;

    private SharedPreferences sharedPreferences;

    public static DemoApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());

            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
        }

        sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.da_preference_file_key), Context.MODE_PRIVATE);

        super.onCreate();

        instance = this;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }
}
