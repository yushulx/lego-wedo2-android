package dk.lego.demo.device;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import dk.lego.demo.R;
import dk.lego.demo.base.BaseFragment;
import dk.lego.demo.helpers.ActivityHelper;
import dk.lego.demo.utils.ConnectedDeviceHelper;
import dk.lego.demo.utils.UIHelper;
import dk.lego.devicesdk.LDSDKError;
import dk.lego.devicesdk.bluetooth.LegoBluetoothDevice;
import dk.lego.devicesdk.device.DeviceCallbackListener;
import dk.lego.devicesdk.device.DeviceInfo;
import dk.lego.devicesdk.device.DeviceManagerCallbackListener;
import dk.lego.devicesdk.device.LegoDevice;
import dk.lego.devicesdk.device.LegoDeviceManagerImpl;
import dk.lego.devicesdk.services.LegoService;
import timber.log.Timber;

public class DeviceConnectedFragment extends BaseFragment implements DeviceCallbackListener, DeviceManagerCallbackListener {

    private LegoBluetoothDevice device;
    private TextView title;
    private ServiceListAdapter serviceListAdapter;
    private TextView deviceFirmwareRevisionText;
    private TextView deviceHardwareRevisionText;
    private TextView deviceSoftwareRevisionText;
    private TextView deviceManufacturerNameText;
    private TextView deviceBatteryLevelTextText;
    private TextView deviceNameText;

    public static Fragment newInstance() {
        return new DeviceConnectedFragment();
    }

    public DeviceConnectedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        updateDeviceName(device.getName());
        updateRevisions(device.getDeviceInfo());
        updateBatteryLevel(device.getBatteryLevel());

        device.registerCallbackListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        device.unregisterCallbackListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflate(inflater, R.layout.da_fragment_device_connected, container, false);

        device = ConnectedDeviceHelper.getInstance().getDevice();

        title = (TextView) rootView.findViewById(R.id.da_device_connection_title);

        final ListView listView = (ListView) rootView.findViewById(android.R.id.list);
        View headerViewDevice = inflater.inflate(R.layout.da_fragment_device_connected_list_header_device, listView, false);
        View headerViewBattery = inflater.inflate(R.layout.da_fragment_device_connected_list_header_battery, listView, false);
        View footerViewWindmillTest = inflater.inflate(R.layout.da_fragment_device_connected_list_footer_windmill_test, listView, false);
        listView.addHeaderView(headerViewDevice, null, true);
        listView.addHeaderView(headerViewBattery, null, false);
        listView.addFooterView(footerViewWindmillTest, null, true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ActivityHelper.startServiceDetailsActivity((LegoService) listView.getAdapter().getItem(position), getActivity());
            }
        });

        LinearLayout deviceWrapperLayout = (LinearLayout) headerViewDevice.findViewById(R.id.device_wrapper);
        deviceWrapperLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityHelper.startDeviceDetailsActivity(getActivity());
            }
        });

        Button startWindmillTestButton = (Button) footerViewWindmillTest.findViewById(R.id.button_perform_windmill_test);
        startWindmillTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityHelper.startWindmillTestActivity(getActivity());
            }
        });

        deviceNameText = (TextView) headerViewDevice.findViewById(R.id.device_name);
        deviceFirmwareRevisionText = (TextView) headerViewDevice.findViewById(R.id.device_fw_value);
        deviceHardwareRevisionText = (TextView) headerViewDevice.findViewById(R.id.device_hw_value);
        deviceSoftwareRevisionText = (TextView) headerViewDevice.findViewById(R.id.device_sw_value);
        deviceManufacturerNameText = (TextView) headerViewDevice.findViewById(R.id.device_manufacturer_value);
        deviceBatteryLevelTextText = (TextView) headerViewBattery.findViewById(R.id.battery_level_value);

        serviceListAdapter = new ServiceListAdapter(getActivity());
        listView.setAdapter(serviceListAdapter);

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (device != null) {
            serviceListAdapter.addAll(device.getServices());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.da_device_connected, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.disconnect:
                LegoDeviceManagerImpl.getInstance().cancelDeviceConnection(device);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
    }

    @Override
    public void didUpdateBatteryLevel(LegoDevice device, int newLevel) {
        Timber.i("DeviceCallback: didUpdateBatteryLevel");
        updateBatteryLevel(newLevel);
    }

    @Override
    public void didUpdateLowVoltageState(LegoDevice device, boolean lowVoltage) {
        Timber.i("DeviceCallback: didUpdateLowVoltageState");
    }

    @Override
    public void didAddService(LegoDevice device, LegoService service) {
        Timber.i("DeviceCallback: didAddService: " + service.getServiceName());
        serviceListAdapter.addAll(device.getServices());
    }

    @Override
    public void didRemoveService(LegoDevice device, LegoService service) {
        Timber.i("DeviceCallback: didRemoveService");
        serviceListAdapter.removeItem(service);
    }

    @Override
    public void didFailToAddServiceWithError(LegoDevice device, LDSDKError error) {
        Timber.e("DeviceCallback: didFailToAddServiceWithError");
    }

    private void updateDeviceName(String newName) {
        title.setText(String.format(getString(R.string.da_device_connected_to_title), newName));
        deviceNameText.setText(newName);
    }

    private void updateRevisions(DeviceInfo deviceInfo) {
        UIHelper.updateRevisions(deviceFirmwareRevisionText,
                deviceHardwareRevisionText,
                deviceSoftwareRevisionText,
                deviceManufacturerNameText,
                deviceInfo);
    }

    private void updateBatteryLevel(Integer newLevel) {
        UIHelper.updateBatteryLevel(getActivity(), deviceBatteryLevelTextText, newLevel);
    }
}
