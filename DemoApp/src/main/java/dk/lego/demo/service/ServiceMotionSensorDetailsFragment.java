package dk.lego.demo.service;

import android.app.Fragment;

import dk.lego.devicesdk.services.MotionSensor;
import dk.lego.devicesdk.services.MotionSensorCallbackListener;

public class ServiceMotionSensorDetailsFragment extends ServiceBaseFragment implements MotionSensorCallbackListener {

    public static Fragment newInstance() {
        return new ServiceMotionSensorDetailsFragment();
    }

    public ServiceMotionSensorDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void didUpdateDistance(MotionSensor sensor, float oldDistance, float newDistance) {
        updatedValue(newDistance);
    }

    @Override
    public void didUpdateCount(MotionSensor sensor, int count) {
        updatedValue(count);
    }

    @Override
    protected void didUpdateValueData(byte[] newValue) {
        // Do nothing
    }
}