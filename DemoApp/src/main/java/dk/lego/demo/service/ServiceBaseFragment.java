package dk.lego.demo.service;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import dk.lego.demo.R;
import dk.lego.demo.base.BaseFragment;
import dk.lego.demo.helpers.ActivityHelper;
import dk.lego.demo.utils.ConnectedDeviceHelper;
import dk.lego.devicesdk.input_output.InputFormat;
import dk.lego.devicesdk.services.LegoService;
import dk.lego.devicesdk.services.ServiceCallbackListener;

public abstract class ServiceBaseFragment extends BaseFragment implements ServiceCallbackListener {

    private TextView textServiceHardwareFirmware;
    protected TextView textServiceValue;
    private TextView textServiceMode;
    private TextView textServiceName;
    private LinearLayout textServiceFormatWrapper;
    private TextView textServiceFormat;
    private Button buttonSendReadRequest;
    private Button buttonSendResetSensorState;

    protected LegoService service;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflate(inflater, R.layout.da_include_service_base_details, container, false);

        textServiceName = (TextView) rootView.findViewById(R.id.service_name);

        textServiceHardwareFirmware = (TextView) rootView.findViewById(R.id.service_hardware_firmware);
        textServiceValue = (TextView) rootView.findViewById(R.id.service_value);
        textServiceMode = (TextView) rootView.findViewById(R.id.service_mode);
        textServiceFormatWrapper = (LinearLayout) rootView.findViewById(R.id.service_format_wrapper);
        textServiceFormat = (TextView) rootView.findViewById(R.id.service_format);
        buttonSendReadRequest = (Button) rootView.findViewById(R.id.button_send_read_request);
        buttonSendResetSensorState = (Button) rootView.findViewById(R.id.button_reset_sensor_state);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buttonSendReadRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service.sendReadValueRequest();
            }
        });

        buttonSendResetSensorState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service.sendResetStateRequest();
            }
        });
    }

    private void updateUI() {
        textServiceName.setText(String.format(getString(R.string.da_service_base_header), service.getServiceName()));

        textServiceHardwareFirmware.setText(String.format(getString(R.string.da_service_details_base_hw_sw),
                service.getConnectInfo().getHardwareVersion().toString(),
                service.getConnectInfo().getFirmwareVersion().toString()));

        if (service.getInputFormat() != null) {
            textServiceMode.setText(String.valueOf(service.getInputFormat().getMode()));
        } else {
            textServiceMode.setText(getString(R.string.da_common_not_applicable));
        }

        textServiceFormatWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityHelper.startServiceDetailsFormatDetailsActivity(getActivity());
            }
        });


        InputFormat inputFormat = service.getInputFormat();
        String unitDescription = getString(R.string.da_common_null);
        long deltaInterval = 0;
        if (inputFormat != null) {
            unitDescription = inputFormat.getUnit().name();
            deltaInterval = service.getInputFormat().getDeltaInterval();
        }

        textServiceFormat.setText(String.format(getString(R.string.da_service_base_details_format), unitDescription, deltaInterval));

        if (service.getValueData() != null) {
            didUpdateValueData(service.getValueData());
        }
    }

    protected void updatedValue(float value) {
        textServiceValue.setText(String.valueOf(value));
    }

    protected abstract void didUpdateValueData(byte[] newValue);

    @Override
    public void onResume() {
        super.onResume();
        service = ConnectedDeviceHelper.getInstance().getService();
        service.registerCallbackListener(this);
        updateUI();
    }

    @Override
    public void onPause() {
        super.onPause();
        service.unregisterCallbackListener(this);
    }

    @Override
    public void didUpdateValueData(LegoService service, byte[] oldValue, byte[] newValue) {
        didUpdateValueData(newValue);
    }

    @Override
    public void didUpdateInputFormat(LegoService service, InputFormat oldFormat, InputFormat newFormat) {
        updateUI();
    }
}
