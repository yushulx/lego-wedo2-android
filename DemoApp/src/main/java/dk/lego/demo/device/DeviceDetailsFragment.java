package dk.lego.demo.device;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import dk.lego.demo.R;
import dk.lego.demo.base.BaseFragment;
import dk.lego.demo.utils.ConnectedDeviceHelper;
import dk.lego.demo.utils.UIHelper;
import dk.lego.devicesdk.LDSDKError;
import dk.lego.devicesdk.bluetooth.LegoBluetoothDevice;
import dk.lego.devicesdk.device.DeviceCallbackListener;
import dk.lego.devicesdk.device.DeviceInfo;
import dk.lego.devicesdk.device.LegoDevice;
import dk.lego.devicesdk.device.LegoDeviceManager;
import dk.lego.devicesdk.device.LegoDeviceManagerImpl;
import dk.lego.devicesdk.services.LegoService;
import timber.log.Timber;


public class DeviceDetailsFragment extends BaseFragment implements DeviceCallbackListener {

    private LegoBluetoothDevice device;
    private LegoDeviceManager deviceManager;

    private TextView deviceFirmwareRevisionText;
    private TextView deviceHardwareRevisionText;
    private TextView deviceSoftwareRevisionText;
    private TextView deviceManufacturerNameText;
    private EditText deviceNameEditText;
    private TextView deviceBatteryLevelText;
    private TextView deviceButtonStateText;
    private Button deviceButtonUpdateName;

    public static Fragment newInstance() {
        return new DeviceDetailsFragment();
    }

    public DeviceDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();

        updateDeviceName(device.getName());
        updateBatteryLevel(device.getBatteryLevel());
        updateButtonState(device.isButtonPressed());

        DeviceInfo deviceInfo = device.getDeviceInfo();
        updateRevisions(deviceInfo);

        device.registerCallbackListener(this);
        deviceManager.registerCallbackListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        device.unregisterCallbackListener(this);
        deviceManager.unregisterCallbackListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflate(inflater, R.layout.da_fragment_device_details, container, false);

        device = ConnectedDeviceHelper.getInstance().getDevice();
        deviceManager = LegoDeviceManagerImpl.getInstance();

        deviceNameEditText = (EditText) rootView.findViewById(R.id.device_name_edit);

        deviceButtonUpdateName = (Button) rootView.findViewById(R.id.device_button_update_name);
        deviceButtonUpdateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                device.setName(deviceNameEditText.getText().toString());
            }
        });

        deviceFirmwareRevisionText = (TextView) rootView.findViewById(R.id.device_info_fw);
        deviceHardwareRevisionText = (TextView) rootView.findViewById(R.id.device_info_hw);
        deviceSoftwareRevisionText = (TextView) rootView.findViewById(R.id.device_info_sw);
        deviceManufacturerNameText = (TextView) rootView.findViewById(R.id.device_info_manufacturer);
        deviceBatteryLevelText = (TextView) rootView.findViewById(R.id.device_info_battery_level);
        deviceButtonStateText = (TextView) rootView.findViewById(R.id.device_info_button_state);

        return rootView;
    }

    @Override
    public void didUpdateDeviceInfo(LegoDevice device, DeviceInfo deviceInfo, LDSDKError error) {
        Timber.i("DeviceCallback: didUpdateDeviceInfo");
        updateRevisions(deviceInfo);
    }

    @Override
    public void didChangeNameFrom(LegoDevice device, String oldName, String newName) {
        Timber.i("DeviceCallback: didChangeNameFrom");
        updateDeviceName(newName);
    }

    @Override
    public void didChangeButtonState(LegoDevice device, boolean pressed) {
        Timber.i("DeviceCallback: didChangeButtonState");
        updateButtonState(pressed);
    }

    @Override
    public void didUpdateBatteryLevel(LegoDevice device, int newLevel) {
        Timber.i("DeviceCallback: didUpdateBatteryLevel");
    }

    @Override
    public void didUpdateLowVoltageState(LegoDevice device, boolean lowVoltage) {
        Timber.i("DeviceCallback: didUpdateLowVoltageState");
    }

    @Override
    public void didAddService(LegoDevice device, LegoService service) {
        Timber.i("DeviceCallback: didAddService: " + service.getServiceName());
    }

    @Override
    public void didRemoveService(LegoDevice device, LegoService service) {
        Timber.i("DeviceCallback: didRemoveService");
    }

    @Override
    public void didFailToAddServiceWithError(LegoDevice device, LDSDKError error) {
        Timber.e("DeviceCallback: didFailToAddServiceWithError");
    }

    private void updateDeviceName(String newName) {
        deviceNameEditText.setText(newName);
    }

    private void updateRevisions(DeviceInfo deviceInfo) {
        UIHelper.updateRevisions(deviceFirmwareRevisionText,
                deviceHardwareRevisionText,
                deviceSoftwareRevisionText,
                deviceManufacturerNameText,
                deviceInfo);
    }

    private void updateButtonState(boolean pressed) {
        String state = getString(R.string.da_button_state_not_pressed);
        if (pressed) {
            state = getString(R.string.da_button_state_pressed);
        }
        deviceButtonStateText.setText(state);
    }

    private void updateBatteryLevel(Integer newLevel) {
        UIHelper.updateBatteryLevel(getActivity(), deviceBatteryLevelText, newLevel);
    }
}
