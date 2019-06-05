package dk.lego.demo.utils;

import android.app.Activity;
import android.widget.TextView;

import dk.lego.demo.R;
import dk.lego.devicesdk.device.DeviceInfo;

public class UIHelper {
    public static void updateBatteryLevel(Activity activity, TextView batteryLevelText, Integer batteryLevel) {
        if (batteryLevel != null) {
            batteryLevelText.setText(String.valueOf(batteryLevel) + activity.getString(R.string.da_device_connected_battery_level_value_unit));
        }
    }

    public static void updateRevisions(TextView firmwareRevisionText,
                                       TextView hardwareRevisionText,
                                       TextView softwareRevisionText,
                                       TextView manufacturerText,
                                       DeviceInfo deviceInfo) {
        if (deviceInfo != null) {
            updateFirmwareRevision(firmwareRevisionText, deviceInfo);
            updateHardwareRevision(hardwareRevisionText, deviceInfo);
            updateSoftwareRevision(softwareRevisionText, deviceInfo);
            updateManufacturerName(manufacturerText, deviceInfo);
        }
    }

    private static void updateFirmwareRevision(TextView firmwareRevisionText, DeviceInfo deviceInfo) {
        if (deviceInfo.getFirmwareRevision() != null) {
            firmwareRevisionText.setText(deviceInfo.getFirmwareRevision().toString());
        }
    }

    private static void updateHardwareRevision(TextView hardwareRevisionText, DeviceInfo deviceInfo) {
        if (deviceInfo.getHardwareRevision() != null) {
            hardwareRevisionText.setText(deviceInfo.getHardwareRevision().toString());
        }
    }

    private static void updateSoftwareRevision(TextView softwareRevisionText, DeviceInfo deviceInfo) {
        if (deviceInfo.getSoftwareRevision() != null) {
            softwareRevisionText.setText(deviceInfo.getSoftwareRevision().toString());
        }
    }

    private static void updateManufacturerName(TextView manufacturerText, DeviceInfo deviceInfo) {
        manufacturerText.setText(deviceInfo.getManufacturerName());
    }
}
