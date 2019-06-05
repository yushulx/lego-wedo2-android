package dk.lego.demo.service;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import dk.lego.demo.R;
import dk.lego.demo.base.BaseFragment;
import dk.lego.demo.utils.ConnectedDeviceHelper;
import dk.lego.devicesdk.device.ConnectInfo;
import dk.lego.devicesdk.input_output.InputFormat;
import dk.lego.devicesdk.services.LegoService;
import dk.lego.devicesdk.services.ServiceCallbackListener;

public class ServiceBaseInputFormatDetailsFragment extends BaseFragment implements ServiceCallbackListener {

    private TextView textRevision;
    private TextView textConnectId;
    private TextView textTypeId;
    private EditText editTextMode;
    private EditText editTextDeltaInterval;
    private EditText editTextUnit;
    private CheckBox checkboxNotificationsEnabled;
    private TextView textNoOfBytes;
    private Button buttonSendUpdate;
    private Button buttonReset;
    private LegoService selectedService;

    public static Fragment newInstance() {
        return new ServiceBaseInputFormatDetailsFragment();
    }

    public ServiceBaseInputFormatDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        inflate(inflater, R.layout.da_fragment_service_base_input_format_details, container, false);

        textRevision = (TextView) rootView.findViewById(R.id.service_details_base_format_revision);
        textConnectId = (TextView) rootView.findViewById(R.id.service_details_base_format_connect_id);
        textTypeId = (TextView) rootView.findViewById(R.id.service_details_base_format_type_id);
        editTextMode = (EditText) rootView.findViewById(R.id.service_details_base_format_mode);
        editTextDeltaInterval = (EditText) rootView.findViewById(R.id.service_details_base_format_delta_interval);
        editTextUnit = (EditText) rootView.findViewById(R.id.service_details_base_format_unit);
        checkboxNotificationsEnabled = (CheckBox) rootView.findViewById(R.id.service_details_base_format_notifications_enabled_checkbox);
        textNoOfBytes = (TextView) rootView.findViewById(R.id.service_details_base_format_no_of_bytes);
        buttonSendUpdate = (Button) rootView.findViewById(R.id.service_details_base_format_send_update_button);
        buttonReset = (Button) rootView.findViewById(R.id.service_details_base_format_reset_button);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        selectedService = ConnectedDeviceHelper.getInstance().getService();
        selectedService.registerCallbackListener(this);

        buttonSendUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedService.updateInputFormat(createNewInputFormat());
            }

            private InputFormat createNewInputFormat() {
                return InputFormat.inputFormat(
                        Integer.parseInt(getStringFromTextView(textConnectId)),
                        ConnectInfo.IOType.fromInteger(Integer.parseInt(getStringFromTextView(textTypeId))),
                        Integer.parseInt(getStringFromTextView(editTextMode)),
                        Integer.parseInt(getStringFromTextView(editTextDeltaInterval)),
                        InputFormat.InputFormatUnit.fromInteger(Integer.parseInt(getStringFromTextView(editTextUnit))),
                        checkboxNotificationsEnabled.isChecked());
            }

            private String getStringFromTextView(TextView view) {
                if (view.getText() == null) {
                    return "0";
                } else {
                    return view.getText().toString();
                }
            }
        });


        if (selectedService.getDefaultInputFormat() != null) {
            buttonReset.setEnabled(true);
            buttonReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedService.updateInputFormat(selectedService.getDefaultInputFormat());
                }
            });
        } else {
            buttonReset.setEnabled(false);
        }

        updateUI(selectedService.getInputFormat());
    }

    @Override
    public void onPause() {
        super.onPause();
        selectedService.unregisterCallbackListener(this);
    }

    @Override
    public void didUpdateValueData(LegoService service, byte[] oldValue, byte[] newValue) {
        // Do nothing
    }

    @Override
    public void didUpdateInputFormat(LegoService service, InputFormat oldFormat, InputFormat newFormat) {
        if (newFormat.getConnectId() == selectedService.getConnectInfo().getConnectId()) {
            updateUI(newFormat);
        }
    }

    private void updateUI(InputFormat inputFormat) {
        textConnectId.setText(String.valueOf(selectedService.getConnectInfo().getConnectId()));
        textTypeId.setText(String.valueOf(selectedService.getConnectInfo().getType().getValue()));

        if (inputFormat != null) {
            textRevision.setText(String.valueOf(inputFormat.getRevision()));
            editTextMode.setText(String.valueOf(inputFormat.getMode()));
            editTextDeltaInterval.setText(String.valueOf(inputFormat.getDeltaInterval()));
            editTextUnit.setText(String.valueOf(inputFormat.getUnit().getValue()));
            checkboxNotificationsEnabled.setChecked(inputFormat.isNotificationsEnabled());
            textNoOfBytes.setText(String.valueOf(inputFormat.getNumberOfBytes()));
        } else {
            textRevision.setText("0");
            editTextMode.setText("0");
            editTextDeltaInterval.setText("0");
            editTextUnit.setText("0");
            checkboxNotificationsEnabled.setChecked(false);
            textNoOfBytes.setText("0");
        }
    }
}