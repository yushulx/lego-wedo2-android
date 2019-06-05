package dk.lego.demo.windmill;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

import dk.lego.demo.R;
import dk.lego.demo.base.BaseFragment;
import dk.lego.demo.utils.ConnectedDeviceHelper;
import dk.lego.devicesdk.input_output.InputFormat;
import dk.lego.devicesdk.services.LegoService;
import dk.lego.devicesdk.services.MotionSensor;
import dk.lego.devicesdk.services.MotionSensorCallbackListener;
import dk.lego.devicesdk.services.Motor;
import dk.lego.devicesdk.services.RGBLight;

public class WindmillTestFragment extends BaseFragment implements MotionSensorCallbackListener {

    private static final int DEFAULT_MOTION_SENSOR_TRIGGER_LEVEL_HIGH = 8;
    private static final int DEFAULT_MOTION_SENSOR_TRIGGER_LEVEL_LOW = 3;

    private boolean isShiftDirectionEnabled = false;
    private int shiftCount = 0;
    private int triggerLevelHigh = DEFAULT_MOTION_SENSOR_TRIGGER_LEVEL_HIGH;
    private int triggerLevelLow = DEFAULT_MOTION_SENSOR_TRIGGER_LEVEL_LOW;

    private TextView textMotionSensorTriggerLevelHigh;
    private TextView textMotionSensorTriggerLevelLow;
    private TextView textMotorPowerStatus;
    private TextView textShiftCount;
    private TextView textMotionSensorCurrentValue;

    private CheckBox checkBoxShiftDirection;

    private Motor motor = null;
    private MotionSensor motionSensor = null;
    private RGBLight rgbLight = null;
    private int motorPower;
    private float motionSensorDistance;
    private SeekBar seekBarMotorPower;
    private Button buttonMotorFullSpeed;
    private Button buttonMotorStop;
    private Button buttonMotionSensorTriggerLevelHighDecrease;
    private Button buttonMotionSensorTriggerLevelHighIncrease;
    private Button buttonMotionSensorTriggerLevelLowDecrease;
    private Button buttonMotionSensorTriggerLevelLowIncrease;
    private boolean isLowTriggerLevelHit = false;

    public static Fragment newInstance() {
        return new WindmillTestFragment();
    }

    public WindmillTestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        inflate(inflater, R.layout.da_fragment_windmill_test, container, false);

        textMotorPowerStatus = (TextView) rootView.findViewById(R.id.motor_power_status);
        buttonMotorFullSpeed = (Button) rootView.findViewById(R.id.motor_button_full_speed);
        buttonMotorStop = (Button) rootView.findViewById(R.id.motor_button_stop);

        textMotionSensorCurrentValue = (TextView) rootView.findViewById(R.id.motion_sensor_current_value);
        textMotionSensorTriggerLevelHigh = (TextView) rootView.findViewById(R.id.motion_sensor_trigger_level_high_status);
        buttonMotionSensorTriggerLevelHighDecrease = (Button) rootView.findViewById(R.id.button_high_decrease);
        buttonMotionSensorTriggerLevelHighIncrease = (Button) rootView.findViewById(R.id.button_high_increase);
        buttonMotionSensorTriggerLevelLowDecrease = (Button) rootView.findViewById(R.id.button_low_decrease);
        buttonMotionSensorTriggerLevelLowIncrease = (Button) rootView.findViewById(R.id.button_low_increase);

        textMotionSensorTriggerLevelLow = (TextView) rootView.findViewById(R.id.motion_sensor_trigger_level_low_status);
        textShiftCount = (TextView) rootView.findViewById(R.id.motion_sensor_shift_count);
        checkBoxShiftDirection = (CheckBox) rootView.findViewById(R.id.motion_sensor_shift_direction_enabled_checkbox);
        seekBarMotorPower = (SeekBar) rootView.findViewById(R.id.motor_seek_bar_power);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (verifyRequiredServicesAvailability()) {
            initializeMotorControls();
            initializeMotionSensorControls();

            resetValuesToDefault();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (motionSensor != null) {
            motionSensor.registerCallbackListener(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (motionSensor != null) {
            motionSensor.unregisterCallbackListener(this);
        }
    }

    private void initializeMotionSensorControls() {
        buttonMotionSensorTriggerLevelHighDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (triggerLevelHigh > MotionSensor.MIN_DISTANCE) {
                    updateTriggerLevel(true, false);
                }
            }
        });

        buttonMotionSensorTriggerLevelHighIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (triggerLevelHigh < MotionSensor.MAX_DISTANCE) {
                    updateTriggerLevel(true, true);
                }
            }
        });


        buttonMotionSensorTriggerLevelLowDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (triggerLevelLow > MotionSensor.MIN_DISTANCE) {
                    updateTriggerLevel(false, false);
                }
            }
        });


        buttonMotionSensorTriggerLevelLowIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (triggerLevelLow < MotionSensor.MAX_DISTANCE) {
                    updateTriggerLevel(false, true);
                }
            }
        });

        checkBoxShiftDirection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean value) {
                isShiftDirectionEnabled = value;
            }
        });
    }

    private void updateTriggerLevel(boolean high, boolean increase) {
        if (high) {
            triggerLevelHigh = (increase) ? ++triggerLevelHigh : --triggerLevelHigh;
            updateTriggerLevelHighUI();
        } else {
            triggerLevelLow = (increase) ? ++triggerLevelLow : --triggerLevelLow;
            updateTriggerLevelLowUI();
        }

    }

    private void initializeMotorControls() {
        seekBarMotorPower.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateMotorPowerUI(progress, true);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setMotorPower(seekBar.getProgress());
            }
        });

        buttonMotorFullSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMotorPower(Motor.MOTOR_MAX_SPEED);
            }
        });

        buttonMotorStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMotorPower(Motor.MOTOR_POWER_BRAKE);
            }
        });
    }

    private boolean verifyRequiredServicesAvailability() {
        String dialogMsg = "";

        List<LegoService> services = ConnectedDeviceHelper.getInstance().getDevice().getServices();
        for (LegoService service : services) {
            if (service instanceof MotionSensor) {
                motionSensor = (MotionSensor) service;
            }

            if (service instanceof Motor) {
                motor = (Motor) service;
            }

            if (service instanceof RGBLight) {
                rgbLight = (RGBLight) service;
            }
        }

        if (motor == null && motionSensor == null) {
            dialogMsg = "Both a motor and a motion sensor must be connected to the Hub!";
        } else if (motor == null) {
            dialogMsg = "A motor must be connected to the Hub!";
        } else if (motionSensor == null) {
            dialogMsg = "A motion sensor must be connected to the Hub!";
        }

        if (!dialogMsg.isEmpty()) {
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle(getString(R.string.da_common_alert_dialog_title));
            alertDialog.setMessage(dialogMsg);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.da_common_alert_dialog_neutral_button), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    getActivity().finish();
                }
            });
            alertDialog.show();
            return false;
        } else {
            return true;
        }
    }

    private void resetValuesToDefault() {
        isShiftDirectionEnabled = false;
        shiftCount = 0;
        triggerLevelHigh = DEFAULT_MOTION_SENSOR_TRIGGER_LEVEL_HIGH;
        triggerLevelLow = DEFAULT_MOTION_SENSOR_TRIGGER_LEVEL_LOW;

        updateMotorPowerUI(motor.getPower(), false);
        updateMotionSensorDistance(motionSensor.getCount());
        updateTriggerLevelHighUI();
        updateTriggerLevelLowUI();
        updateTriggerShiftCountUI();
        updateTriggerShiftDirectionEnabledUI();
    }

    private void toggleMotorDirection() {
        if (motor.getDirection() == Motor.MotorDirection.MOTOR_DIRECTION_RIGHT) {
            motor.run(Motor.MotorDirection.MOTOR_DIRECTION_LEFT, motorPower);
            rgbLight.setColorIndex(6);
        } else {
            motor.run(Motor.MotorDirection.MOTOR_DIRECTION_RIGHT, motorPower);
            rgbLight.setColorIndex(2);
        }
    }

    private void setMotorPower(int motorPower) {
        if (motorPower == Motor.MOTOR_POWER_BRAKE || motorPower == Motor.MOTOR_POWER_DRIFT) {
            motorPower = 0;
        }
        this.motorPower = motorPower;
        motor.run(motor.getDirection(), motorPower);
        updateMotorPowerUI(motorPower, false);
    }

    private void updateMotorPowerUI(int motorPower, boolean fromSeekBar) {
        if (!fromSeekBar) {
            seekBarMotorPower.setProgress(motorPower);
        }
        textMotorPowerStatus.setText(String.format(getString(R.string.da_windmill_motor_power_status), motorPower));
    }

    private void updateTriggerLevelHighUI() {
        textMotionSensorTriggerLevelHigh.setText(String.format(getString(R.string.da_windmill_motion_sensor_trigger_high_status), triggerLevelHigh));
        textShiftCount.setText(String.valueOf(shiftCount));
        checkBoxShiftDirection.setChecked(isShiftDirectionEnabled);
    }

    private void updateTriggerLevelLowUI() {
        textMotionSensorTriggerLevelLow.setText(String.format(getString(R.string.da_windmill_motion_sensor_trigger_low_status), triggerLevelLow));
    }

    private void updateTriggerShiftDirectionEnabledUI() {
        checkBoxShiftDirection.setChecked(isShiftDirectionEnabled);
    }

    private void increaseTriggerShiftCount() {
        shiftCount++;
        updateTriggerShiftCountUI();

        if (isShiftDirectionEnabled) {
            toggleMotorDirection();
        }
    }

    private void updateTriggerShiftCountUI() {
        textShiftCount.setText(String.valueOf(shiftCount));
    }

    private void updateMotionSensorDistance(float distance) {
        motionSensorDistance = distance;
        textMotionSensorCurrentValue.setText(String.valueOf(motionSensorDistance));

        if (motionSensorDistance >= triggerLevelHigh && isLowTriggerLevelHit) {
            isLowTriggerLevelHit = false;
            increaseTriggerShiftCount();
        }

        if (motionSensorDistance <= triggerLevelLow) {
            isLowTriggerLevelHit = true;
        }
    }

    @Override
    public void didUpdateValueData(LegoService service, byte[] oldValue, byte[] newValue) {
        // Do nothing
    }

    @Override
    public void didUpdateInputFormat(LegoService service, InputFormat oldFormat, InputFormat newFormat) {
        // Do nothing
    }

    @Override
    public void didUpdateDistance(MotionSensor sensor, float oldDistance, float newDistance) {
        updateMotionSensorDistance(newDistance);
    }

    @Override
    public void didUpdateCount(MotionSensor sensor, int count) {
        // Do nothing
    }
}
