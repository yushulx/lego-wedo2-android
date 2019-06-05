package dk.lego.demo.discovery;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import dk.lego.demo.R;
import dk.lego.demo.base.BaseFragment;
import dk.lego.demo.base.DemoApplication;
import dk.lego.demo.helpers.ActivityHelper;
import dk.lego.demo.helpers.DialogHelper;
import dk.lego.demo.helpers.LogHelper;
import dk.lego.devicesdk.LDSDKError;
import dk.lego.devicesdk.bluetooth.LegoBluetoothDevice;
import dk.lego.devicesdk.device.DeviceManagerCallbackListener;
import dk.lego.devicesdk.device.LegoDevice;
import dk.lego.devicesdk.device.LegoDeviceManager;
import dk.lego.devicesdk.device.LegoDeviceManagerImpl;
import dk.lego.devicesdk.logging.LDSDKLogger;
import timber.log.Timber;

public class DiscoveryFragment extends BaseFragment implements DeviceManagerCallbackListener, SwipeRefreshLayout.OnRefreshListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int REQUEST_ENABLE_BT = 100;
    private static final int SWIPE_REFRESH_DELAY_MILLIS = 500;
    private static final int TOUCH_REFRESH_DELAY_MILLIS = 1000;
    private static final int REFRESH_LIST_INTERVAL_MILLIS = 1000;

    private DeviceListAdapter deviceListAdapter;
    private LegoDeviceManager deviceManager;
    private SwipeRefreshLayout swipeLayout;

    private Handler handler = new Handler();
    private Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
        deviceListAdapter.notifyDataSetChanged();
        handler.postDelayed(refreshRunnable, REFRESH_LIST_INTERVAL_MILLIS);
        }
    };

    public static DiscoveryFragment newInstance() {
        return new DiscoveryFragment();
    }

    public DiscoveryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflate(inflater, R.layout.da_fragment_discovery, container, false);

        deviceManager = LegoDeviceManagerImpl.getInstance();

        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.da_content_frame);
        swipeLayout.setOnRefreshListener(this);

        ListView listView = (ListView) rootView.findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LegoBluetoothDevice selectedDevice = deviceListAdapter.getItem(i);
                if (selectedDevice.getConnectState() != LegoDevice.DeviceState.DEVICE_CONNECTION_STATE_DISCONNECTED_NOT_ADVERTISING) {
                    if (selectedDevice.getConnectState() != LegoDevice.DeviceState.DEVICE_CONNECTION_STATE_INTERROGATION_FINISHED) {
                        deviceManager.connectToDevice(getActivity(), selectedDevice);
                    } else {
                        ActivityHelper.startDeviceConnectedActivity(getActivity(), selectedDevice);
                    }
                }
            }
        });

        deviceListAdapter = new DeviceListAdapter(getActivity());
        listView.setAdapter(deviceListAdapter);

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.da_device_discovery, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.discovery_refresh:
                swipeLayout.setRefreshing(true);
                refreshDiscoveredDevices(TOUCH_REFRESH_DELAY_MILLIS);
                return true;
            case R.id.discovery_settings:
                ActivityHelper.startDeviceDiscoverySettings(getActivity());
                return true;
            case R.id.discovery_about:
                DialogHelper.showAboutDialog(getActivity());
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 456;
    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
        }
        boolean automaticReconnectEnabled = DemoApplication.getInstance().getSharedPreferences().getBoolean(getString(R.string.da_discovery_settings_auto_reconnect_key), false);

        deviceManager.setAutomaticReconnectOnConnectionLostEnabled(automaticReconnectEnabled);
        deviceManager.registerCallbackListener(this);

        // Customize SDK logging
        LDSDKLogger.getInstance().setCustomLogger(LogHelper.getCustomizedLogger());
        LDSDKLogger.getInstance().setLogLevel(LDSDKLogger.LoggerLevel.LOGGER_LEVEL_DEBUG);

        deviceListAdapter.clearAndAddItems(deviceManager.allDevices());

        try {
            deviceManager.scan(getActivity());
        } catch (IllegalStateException e) {
            deviceManager.unregisterCallbackListener(this);
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        showContent();

        handler.post(refreshRunnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(refreshRunnable);
        deviceManager.unregisterCallbackListener(this);
        deviceManager.stopScanning();
    }

    @Override
    public void onWillStartConnectingToDevice(LegoDevice device) {
        if (device instanceof LegoBluetoothDevice) {
            deviceListAdapter.updateItem((LegoBluetoothDevice) device);
        } else {
            Timber.i("Received onWillStartConnectingToDevice callback for a non-Bluetooth device");
        }
    }

    @Override
    public void onDidStartInterrogatingDevice(LegoDevice device) {
        if (device instanceof LegoBluetoothDevice) {
            deviceListAdapter.updateItem((LegoBluetoothDevice) device);
        } else {
            Timber.i("Received onDidStartInterrogatingDevice callback for a non-Bluetooth device");
        }
    }

    @Override
    public void onDidFinishInterrogatingDevice(LegoDevice device) {
        if (device instanceof LegoBluetoothDevice) {
            ActivityHelper.startDeviceConnectedActivity(getActivity(), (LegoBluetoothDevice) device);
        } else {
            Timber.i("Received onDidFinishInterrogatingDevice callback for a non-Bluetooth device");
        }
    }

    @Override
    public void onDidDisconnectFromDevice(LegoDevice device, boolean autoReconnect, LDSDKError error) {
        if (device instanceof LegoBluetoothDevice) {
            if (!swipeLayout.isRefreshing()) {
                deviceListAdapter.updateItem((LegoBluetoothDevice) device);
            }
        } else {
            Timber.i("Received onDidDisconnectFromDevice callback for a non-Bluetooth device");
        }
    }

    @Override
    public void onDidFailToConnectToDevice(LegoDevice device, boolean autoReconnect, LDSDKError error) {
        if (device instanceof LegoBluetoothDevice) {
            deviceListAdapter.updateItem((LegoBluetoothDevice) device);
        } else {
            Timber.i("Received onDidFailToConnectToDevice callback for a non-Bluetooth device");
        }
    }

    @Override
    public void onDeviceAppeared(final LegoDevice device) {
        LegoBluetoothDevice legoBluetoothDevice = (LegoBluetoothDevice) device;
        if (legoBluetoothDevice != null && legoBluetoothDevice.getAndroidBluetoothDevice() != null) {
            Timber.i(createDeviceDescription(legoBluetoothDevice) + " appeared");
            deviceListAdapter.addItem(legoBluetoothDevice);
        }
    }

    @Override
    public void onDeviceDisappeared(final LegoDevice device) {
        LegoBluetoothDevice legoBluetoothDevice = (LegoBluetoothDevice) device;
        if (legoBluetoothDevice != null && legoBluetoothDevice.getAndroidBluetoothDevice() != null) {
            Timber.i(createDeviceDescription(legoBluetoothDevice) + " disappeared");
            deviceListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            getActivity().finish();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private String createDeviceDescription(LegoBluetoothDevice device) {
        return "Device: " + device.getAndroidBluetoothDevice().getName() + " (@" + device.getAndroidBluetoothDevice().getAddress() + ")";
    }

    @Override
    public void onRefresh() {
        refreshDiscoveredDevices(SWIPE_REFRESH_DELAY_MILLIS);
    }

    private void refreshDiscoveredDevices(int timeout) {
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                swipeLayout.setRefreshing(false);
                deviceListAdapter.clearAndAddItems(deviceManager.allDevices());
            }
        }, timeout);
    }
}
