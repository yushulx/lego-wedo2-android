package dk.lego.demo.service;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import dk.lego.demo.R;
import dk.lego.devicesdk.input_output.InputFormat;
import dk.lego.devicesdk.services.LegoService;
import dk.lego.devicesdk.services.TiltSensor;
import dk.lego.devicesdk.services.TiltSensorCallbackListener;
import dk.lego.devicesdk.utils.ByteUtils;

public class ServiceTiltSensorDetailsFragment extends ServiceBaseFragment implements TiltSensorCallbackListener {

    private TextView textMode;
    private TextView textValue;

    public static Fragment newInstance() {
        return new ServiceTiltSensorDetailsFragment();
    }

    public ServiceTiltSensorDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Add motor specific details
        ViewGroup content = (ViewGroup) rootView.findViewById(R.id.da_content_frame);
        content.addView(inflater.inflate(R.layout.da_fragment_service_tilt_details, container, false));

        textMode = (TextView) rootView.findViewById(R.id.da_service_details_tilt_mode);
        textValue = (TextView) rootView.findViewById(R.id.da_service_details_tilt_value);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateMode(service.getInputFormat().getMode());
    }

    @Override
    public void didUpdateDirection(TiltSensor sensor, TiltSensor.TiltSensorDirection oldDirection, TiltSensor.TiltSensorDirection newDirection) {
        textValue.setText(newDirection.toString());
    }

    @Override
    public void didUpdateAngle(TiltSensor sensor, TiltSensor.TiltSensorAngle oldAngle, TiltSensor.TiltSensorAngle newAngle) {
        textValue.setText(newAngle.toString());
    }

    @Override
    public void didUpdateCrash(TiltSensor sensor, TiltSensor.TiltSensorCrash oldCrashValue, TiltSensor.TiltSensorCrash newCrashValue) {
        textValue.setText(newCrashValue.toString());
    }

    @Override
    protected void didUpdateValueData(byte[] newValue) {
        textServiceValue.setText(ByteUtils.toHexString(newValue));
    }

    @Override
    public void didUpdateInputFormat(LegoService service, InputFormat oldFormat, InputFormat newFormat) {
        super.didUpdateInputFormat(service, oldFormat, newFormat);

        updateMode(newFormat.getMode());
    }

    private void updateMode(int mode) {
        TiltSensor.TiltSensorMode tiltMode = TiltSensor.TiltSensorMode.fromInteger(mode);

        switch (tiltMode) {
            case TILT_SENSOR_MODE_ANGLE:
                textMode.setText("Angle");
                break;
            case TILT_SENSOR_MODE_TILT:
                textMode.setText("Tilt");
                break;
            case TILT_SENSOR_MODE_CRASH:
                textMode.setText("Crash");
                break;
            default:
                textMode.setText("Unknown");
        }
    }
}