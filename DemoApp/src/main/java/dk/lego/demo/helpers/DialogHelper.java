package dk.lego.demo.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import dk.lego.demo.BuildConfig;
import dk.lego.demo.R;
import dk.lego.devicesdk.device.LegoDevice;
import dk.lego.devicesdk.device.LegoDeviceManagerImpl;

public class DialogHelper {
    public static void showConnectFailedDialog(final Activity activity, LegoDevice device, boolean autoReconnect) {
        if (autoReconnect) {
            activity.finish();
        } else {
            String message = String.format(activity.getString(R.string.da_alert_dialog_failed_to_connect_message), device.getName());
            showAlertDialog(activity, message);
        }
    }

    public static void showDisconnectedDialog(final Activity activity, LegoDevice device, boolean autoReconnect) {
        if (autoReconnect) {
            activity.finish();
        } else {
            String message = String.format(activity.getString(R.string.da_alert_dialog_lost_connection_message), device.getName());
            showAlertDialog(activity, message);
        }
    }

    private static void showAlertDialog(final Activity activity, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle(activity.getString(R.string.da_common_alert_dialog_title));
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, activity.getString(R.string.da_common_alert_dialog_neutral_button), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
                ActivityHelper.startDiscoveryActivity(activity);
            }
        });
        alertDialog.show();
    }

    public static void showAboutDialog(final Activity activity) {
        final AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle(activity.getString(R.string.da_about_dialog_title));
        alertDialog.setMessage(String.format(activity.getResources().getString(R.string.da_about_dialog_message),
                BuildConfig.VERSION,
                LegoDeviceManagerImpl.getInstance().getSDKVersion(),
                BuildConfig.TESTED_WITH_FIRMWARE_VERSION));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, activity.getString(R.string.da_common_alert_dialog_neutral_button), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
