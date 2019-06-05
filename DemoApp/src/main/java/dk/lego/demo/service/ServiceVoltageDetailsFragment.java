package dk.lego.demo.service;

import android.app.Fragment;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import dk.lego.devicesdk.services.VoltageSensor;
import dk.lego.devicesdk.services.VoltageSensorCallbackListener;

public class ServiceVoltageDetailsFragment extends ServiceBaseFragment implements VoltageSensorCallbackListener {
    public static Fragment newInstance() {
        return new ServiceVoltageDetailsFragment();
    }

    public ServiceVoltageDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    protected void didUpdateValueData(byte[] newValue) {
        textServiceValue.setText(String.valueOf(ByteBuffer.wrap(newValue).order(ByteOrder.LITTLE_ENDIAN).getFloat()));
    }

    @Override
    public void didUpdateMilliVolts(VoltageSensor sensor, float milliVolts) {
        textServiceValue.setText(String.valueOf(milliVolts));
    }
}