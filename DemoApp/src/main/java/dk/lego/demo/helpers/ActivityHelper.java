package dk.lego.demo.helpers;

import android.app.Activity;
import android.content.Intent;

import dk.lego.demo.device.DeviceConnectedActivity;
import dk.lego.demo.device.DeviceDetailsActivity;
import dk.lego.demo.discovery.DiscoveryActivity;
import dk.lego.demo.service.ServiceBaseInputFormatDetailsActivity;
import dk.lego.demo.service.ServiceCurrentDetailsActivity;
import dk.lego.demo.service.ServiceGenericDetailsActivity;
import dk.lego.demo.service.ServiceMotionSensorDetailsActivity;
import dk.lego.demo.service.ServiceMotorDetailsActivity;
import dk.lego.demo.service.ServicePiezoDetailsActivity;
import dk.lego.demo.service.ServiceRGBLightDetailsActivity;
import dk.lego.demo.service.ServiceTiltSensorDetailsActivity;
import dk.lego.demo.service.ServiceVoltageDetailsActivity;
import dk.lego.demo.settings.DiscoverySettingsActivity;
import dk.lego.demo.utils.ConnectedDeviceHelper;
import dk.lego.demo.windmill.WindmillTestActivity;
import dk.lego.devicesdk.bluetooth.LegoBluetoothDevice;
import dk.lego.devicesdk.services.CurrentSensor;
import dk.lego.devicesdk.services.GenericService;
import dk.lego.devicesdk.services.LegoService;
import dk.lego.devicesdk.services.MotionSensor;
import dk.lego.devicesdk.services.Motor;
import dk.lego.devicesdk.services.PiezoTonePlayer;
import dk.lego.devicesdk.services.RGBLight;
import dk.lego.devicesdk.services.TiltSensor;
import dk.lego.devicesdk.services.VoltageSensor;

public class ActivityHelper {

    public static void startDiscoveryActivity(Activity activity) {
        Intent intent = new Intent(activity, DiscoveryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }

    public static void startDeviceConnectedActivity(Activity activity, LegoBluetoothDevice selectedDevice) {
        ConnectedDeviceHelper.getInstance().setDevice(selectedDevice);
        Intent intent = new Intent(activity, DeviceConnectedActivity.class);
        activity.startActivity(intent);
    }

    public static void startDeviceDetailsActivity(Activity activity) {
        Intent intent = new Intent(activity, DeviceDetailsActivity.class);
        activity.startActivity(intent);
    }

    public static void startServiceDetailsActivity(LegoService service, Activity activity) {
        Intent intent;
        if (service instanceof Motor) {
            ConnectedDeviceHelper.getInstance().setService(service);
            intent = new Intent(activity, ServiceMotorDetailsActivity.class);
            activity.startActivity(intent);
        } else if (service instanceof MotionSensor) {
            ConnectedDeviceHelper.getInstance().setService(service);
            intent = new Intent(activity, ServiceMotionSensorDetailsActivity.class);
            activity.startActivity(intent);
        } else if (service instanceof PiezoTonePlayer) {
            ConnectedDeviceHelper.getInstance().setService(service);
            intent = new Intent(activity, ServicePiezoDetailsActivity.class);
            activity.startActivity(intent);
        } else if (service instanceof RGBLight) {
            ConnectedDeviceHelper.getInstance().setService(service);
            intent = new Intent(activity, ServiceRGBLightDetailsActivity.class);
            activity.startActivity(intent);
        } else if (service instanceof TiltSensor) {
            ConnectedDeviceHelper.getInstance().setService(service);
            intent = new Intent(activity, ServiceTiltSensorDetailsActivity.class);
            activity.startActivity(intent);
        } else if (service instanceof VoltageSensor) {
            ConnectedDeviceHelper.getInstance().setService(service);
            intent = new Intent(activity, ServiceVoltageDetailsActivity.class);
            activity.startActivity(intent);
        } else if (service instanceof CurrentSensor) {
            ConnectedDeviceHelper.getInstance().setService(service);
            intent = new Intent(activity, ServiceCurrentDetailsActivity.class);
            activity.startActivity(intent);
        } else if (service instanceof GenericService) {
            ConnectedDeviceHelper.getInstance().setService(service);
            intent = new Intent(activity, ServiceGenericDetailsActivity.class);
            activity.startActivity(intent);
        }
    }

    public static void startWindmillTestActivity(Activity activity) {
        Intent intent = new Intent(activity, WindmillTestActivity.class);
        activity.startActivity(intent);
    }

    public static void startServiceDetailsFormatDetailsActivity(Activity activity) {
        Intent intent = new Intent(activity, ServiceBaseInputFormatDetailsActivity.class);
        activity.startActivity(intent);
    }

    public static void startDeviceDiscoverySettings(Activity activity) {
        Intent intent = new Intent(activity, DiscoverySettingsActivity.class);
        activity.startActivity(intent);
    }
}
