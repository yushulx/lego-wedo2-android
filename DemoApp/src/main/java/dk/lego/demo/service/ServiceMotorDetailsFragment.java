package dk.lego.demo.service;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import dk.lego.demo.R;
import dk.lego.devicesdk.services.Motor;
import dk.lego.devicesdk.utils.ByteUtils;

public class ServiceMotorDetailsFragment extends ServiceBaseFragment {

    private Motor.MotorDirection direction;
    private int motorPower;
    private Button buttonRight;
    private Button buttonLeft;
    private Button buttonBrake;
    private Button buttonDrift;
    private TextView textMotorStatus;
    private SeekBar seekBarPower;

    public static Fragment newInstance() {
        return new ServiceMotorDetailsFragment();
    }

    public ServiceMotorDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Add motor specific details
        ViewGroup content = (ViewGroup) rootView.findViewById(R.id.da_content_frame);
        content.addView(inflater.inflate(R.layout.da_fragment_service_motor_details, container, false));

        textMotorStatus = (TextView) rootView.findViewById(R.id.motor_power_status);
        seekBarPower = (SeekBar) rootView.findViewById(R.id.motor_seek_bar_power);
        buttonRight = (Button) rootView.findViewById(R.id.button_right);
        buttonLeft = (Button) rootView.findViewById(R.id.button_left);
        buttonBrake = (Button) rootView.findViewById(R.id.button_brake);
        buttonDrift = (Button) rootView.findViewById(R.id.button_drift);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        seekBarPower.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateUIAccordingToMotorChange(direction, progress, true);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateMotorPower(seekBar.getProgress());
            }
        });

        buttonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMotorDirection(Motor.MotorDirection.MOTOR_DIRECTION_RIGHT);
            }
        });

        buttonRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    buttonLeft.setPressed(false);
                    view.setPressed(true);
                    view.performClick();
                }
                return true;

            }
        });

        buttonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateMotorDirection(Motor.MotorDirection.MOTOR_DIRECTION_LEFT);
            }
        });

        buttonLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    buttonRight.setPressed(false);
                    view.setPressed(true);
                    view.performClick();
                }
                return true;
            }
        });

        buttonBrake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUIAccordingToMotorChange(direction, Motor.MOTOR_POWER_BRAKE, false);
                getMotorService().brake();
            }
        });

        buttonDrift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUIAccordingToMotorChange(direction, Motor.MOTOR_POWER_DRIFT, false);
                getMotorService().drift();
            }
        });
    }

    @Override
    protected void didUpdateValueData(byte[] newValue) {
        textServiceValue.setText(ByteUtils.toHexString(newValue));
    }

    @Override
    public void onResume() {
        super.onResume();

        // Get current motor values
        direction = (getMotorService().getDirection() == null)
                ? Motor.MotorDirection.MOTOR_DIRECTION_RIGHT : getMotorService().getDirection();
        motorPower = getMotorService().getPower();

        updateUIAccordingToMotorChange(direction, motorPower, false);
    }

    private Motor getMotorService() {
        return ((Motor) service);
    }

    private void updateMotorDirectionAndPower(Motor.MotorDirection direction, int power) {
        this.motorPower = power;
        this.direction = direction;
        updateUIAccordingToMotorChange(this.direction, this.motorPower, false);
        getMotorService().run(direction, power);
    }

    private void updateMotorPower(int newMotorPower) {
        updateMotorDirectionAndPower(direction, newMotorPower);
    }

    private void updateMotorDirection(Motor.MotorDirection newDirection) {
        if (motorPower == Motor.MOTOR_POWER_BRAKE) {
            motorPower = Motor.MOTOR_POWER_DRIFT;
        }
        updateMotorDirectionAndPower(newDirection, motorPower);
    }

    private void updateUIAccordingToMotorChange(Motor.MotorDirection direction, int motorPower, boolean fromSeekBar) {
        if (!fromSeekBar) {
            if (motorPower == Motor.MOTOR_POWER_DRIFT || motorPower == Motor.MOTOR_POWER_BRAKE) {
                seekBarPower.setProgress(0);
            } else {
                seekBarPower.setProgress(motorPower);
            }
        }

        if (direction == Motor.MotorDirection.MOTOR_DIRECTION_RIGHT) {
            buttonRight.setPressed(true);
            buttonLeft.setPressed(false);
        } else {
            buttonRight.setPressed(false);
            buttonLeft.setPressed(true);
        }

        if (motorPower >= Motor.MOTOR_MIN_SPEED && motorPower <= Motor.MOTOR_MAX_SPEED) {
            if (direction == Motor.MotorDirection.MOTOR_DIRECTION_RIGHT) {
                textMotorStatus.setText(String.format(
                        getString(R.string.da_service_details_motor_power_status_right),
                        motorPower));
            } else {
                textMotorStatus.setText(String.format(
                        getString(R.string.da_service_details_motor_power_status_left),
                        motorPower));
            }
        } else if (motorPower == Motor.MOTOR_POWER_DRIFT) {
            textMotorStatus.setText(getString(R.string.da_service_details_motor_power_status_drifting));
        } else if (motorPower == Motor.MOTOR_POWER_BRAKE) {
            textMotorStatus.setText(getString(R.string.da_service_details_motor_power_status_braking));
        }
    }
}