package dk.lego.demo.utils;

import dk.lego.devicesdk.bluetooth.LegoBluetoothDevice;
import dk.lego.devicesdk.services.LegoService;

public class ConnectedDeviceHelper {
    private static ConnectedDeviceHelper instance;

    private LegoBluetoothDevice device;
    private LegoService service;

    public static ConnectedDeviceHelper getInstance() {
        if (instance == null) {
            instance = new ConnectedDeviceHelper();
        }
        return instance;
    }

    private ConnectedDeviceHelper() {
    }

    public synchronized LegoBluetoothDevice getDevice() {
        return device;
    }

    public synchronized void setDevice(LegoBluetoothDevice device) {
        this.device = device;
    }

    public synchronized LegoService getService() {
        return service;
    }

    public synchronized void setService(LegoService service) {
        this.service = service;
    }
}
