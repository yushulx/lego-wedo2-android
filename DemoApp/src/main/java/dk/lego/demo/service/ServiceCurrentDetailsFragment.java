package dk.lego.demo.service;

import android.app.Fragment;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import dk.lego.devicesdk.services.CurrentSensor;
import dk.lego.devicesdk.services.CurrentSensorCallbackListener;

public class ServiceCurrentDetailsFragment extends ServiceBaseFragment implements CurrentSensorCallbackListener {
    public static Fragment newInstance() {
        return new ServiceCurrentDetailsFragment();
    }

    public ServiceCurrentDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    protected void didUpdateValueData(byte[] newValue) {
        textServiceValue.setText(String.valueOf(ByteBuffer.wrap(newValue).order(ByteOrder.LITTLE_ENDIAN).getFloat()));
    }

    @Override
    public void didUpdateMilliAmps(CurrentSensor sensor, float milliAmps) {
        textServiceValue.setText(String.valueOf(milliAmps));
    }
}