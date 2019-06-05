package dk.lego.demo.discovery;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dk.lego.demo.R;
import dk.lego.demo.base.DemoApplication;
import dk.lego.devicesdk.LDSDKError;
import dk.lego.devicesdk.bluetooth.LegoBluetoothDevice;
import dk.lego.devicesdk.device.DeviceCallbackListener;
import dk.lego.devicesdk.device.DeviceInfo;
import dk.lego.devicesdk.device.LegoDevice;
import dk.lego.devicesdk.services.LegoService;
import timber.log.Timber;

public class DeviceListAdapter extends BaseAdapter implements DeviceCallbackListener {

    private final LayoutInflater layoutInflater;
    private final Activity context;
    private List<LegoBluetoothDevice> devices = new ArrayList<>();
    private int rssiThresholdValue;

    @Override
    public void didUpdateDeviceInfo(LegoDevice device, DeviceInfo deviceInfo, LDSDKError error) {
        // Do nothing
    }

    @Override
    public void didChangeNameFrom(LegoDevice device, String oldName, String newName) {
        notifyDataSetChanged();
    }

    @Override
    public void didChangeButtonState(LegoDevice device, boolean pressed) {
        notifyDataSetChanged();
    }

    @Override
    public void didUpdateBatteryLevel(LegoDevice device, int newLevel) {
        // Do nothing
    }

    @Override
    public void didUpdateLowVoltageState(LegoDevice device, boolean lowVoltage) {
        notifyDataSetChanged();
    }

    @Override
    public void didAddService(LegoDevice device, LegoService service) {
        // Do nothing
    }

    @Override
    public void didRemoveService(LegoDevice device, LegoService service) {
        // Do nothing
    }

    @Override
    public void didFailToAddServiceWithError(LegoDevice device, LDSDKError error) {
        // Do nothing
    }

    private static class ViewHolder {
        TextView textDeviceName;
        TextView textDeviceStatus;
        ImageView imageViewLowBattery;
        ImageView imageViewButtonState;
    }

    public DeviceListAdapter(final Activity context) {
        super();
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public void clearAndAddItems(List<? extends LegoDevice> knownDevices) {
        devices.clear();
        for (LegoDevice knownDevice : knownDevices) {
            if (knownDevice.getConnectState() != LegoDevice.DeviceState.DEVICE_CONNECTION_STATE_DISCONNECTED_NOT_ADVERTISING) {
                addItem((LegoBluetoothDevice) knownDevice);
            }
        }
        this.notifyDataSetChanged();
    }

    public void addItem(LegoBluetoothDevice device) {
        if (!devices.contains(device)) {
            rssiThresholdValue = DemoApplication.getInstance().getSharedPreferences().getInt(
                    context.getResources().getString(R.string.da_discovery_settings_rssi_threshold_key),
                    context.getResources().getInteger(R.integer.da_pref_default_rssi_threshold));

            if (device.getRSSIValue() >= rssiThresholdValue) {
                device.registerCallbackListener(this);
                devices.add(device);
                this.notifyDataSetChanged();
            }
        } else {
            updateItem(device);
        }
    }

    public void updateItem(LegoBluetoothDevice device) {
        if (devices.contains(device)) {
            devices.set(devices.indexOf(device), device);
            notifyDataSetChanged();
        }
    }

    private void removeDevice(LegoBluetoothDevice device) {
        devices.get(devices.indexOf(device)).unregisterCallbackListener(this);
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public LegoBluetoothDevice getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.da_list_item_devices, parent, false);

            vh = new ViewHolder();
            vh.textDeviceName = (TextView) convertView.findViewById(R.id.name);
            vh.textDeviceStatus = (TextView) convertView.findViewById(R.id.rssi);
            vh.imageViewLowBattery = (ImageView) convertView.findViewById(R.id.low_battery);
            vh.imageViewButtonState = (ImageView) convertView.findViewById(R.id.button_state);

            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        LegoBluetoothDevice device = getItem(position);

        if (device != null) {
            vh.textDeviceName.setText(device.getName());

            String deviceStatus = "";
            switch (device.getConnectState()) {
                case DEVICE_CONNECTION_STATE_DISCONNECTED_ADVERTISING:
                    setTextColorEnabled(vh.textDeviceName, vh.textDeviceStatus);
                    deviceStatus = String.format(DemoApplication.getInstance().getResources().getString(R.string.da_device_status_average_rssi), device.getRSSIValue());
                    break;
                case DEVICE_CONNECTION_STATE_DISCONNECTED_NOT_ADVERTISING:
                    setTextColorDisabled(vh.textDeviceName, vh.textDeviceStatus);
                    deviceStatus = DemoApplication.getInstance().getString(R.string.da_device_status_no_longer_advertising);
                    break;
                case DEVICE_CONNECTION_STATE_CONNECTING:
                    setTextColorEnabled(vh.textDeviceName, vh.textDeviceStatus);
                    deviceStatus = DemoApplication.getInstance().getString(R.string.da_device_status_connecting);
                    break;
                case DEVICE_CONNECTION_STATE_INTERROGATING:
                    setTextColorEnabled(vh.textDeviceName, vh.textDeviceStatus);
                    deviceStatus = DemoApplication.getInstance().getString(R.string.da_device_status_interrogating);
                    break;
                case DEVICE_CONNECTION_STATE_INTERROGATION_FINISHED:
                    setTextColorEnabled(vh.textDeviceName, vh.textDeviceStatus);
                    deviceStatus = DemoApplication.getInstance().getString(R.string.da_device_status_connected);
                    break;
                default:
                    Timber.e("Reached default case when trying to determine connection state: " + device.getConnectState() + " for device name: " + device.getName());
            }

            vh.textDeviceStatus.setText(deviceStatus);

            vh.imageViewLowBattery.setVisibility(device.isLowVoltage() ? View.VISIBLE : View.GONE);

            if (device.isButtonPressed() && device.getConnectState() != LegoDevice.DeviceState.DEVICE_CONNECTION_STATE_DISCONNECTED_NOT_ADVERTISING) {
                vh.imageViewButtonState.setVisibility(View.VISIBLE);
            } else {
                vh.imageViewButtonState.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    private void setTextColorEnabled(TextView textDeviceName, TextView textDeviceStatus) {
        textDeviceName.setTextColor(context.getResources().getColor(R.color.da_black));
        textDeviceStatus.setTextColor(context.getResources().getColor(R.color.da_black));
    }

    private void setTextColorDisabled(TextView textDeviceName, TextView textDeviceStatus) {
        textDeviceName.setTextColor(context.getResources().getColor(R.color.da_grey_medium));
        textDeviceStatus.setTextColor(context.getResources().getColor(R.color.da_grey_medium));
    }
}