package dk.lego.demo.helpers;

import dk.lego.devicesdk.logging.CustomLogger;
import timber.log.Timber;

public class LogHelper {
    public static CustomLogger getCustomizedLogger() {
        return new CustomLogger() {
            @Override
            public void verbose(String msg) {
                Timber.v(msg);
            }

            @Override
            public void debug(String msg) {
                Timber.d(msg);
            }

            @Override
            public void info(String msg) {
                Timber.i(msg);
            }

            @Override
            public void warn(String msg) {
                Timber.w(msg);
            }

            @Override
            public void error(String msg) {
                Timber.e(msg);
            }

            @Override
            public void error(Exception e) {
                Timber.e(e, "Exception!");
            }
        };
    }
}
