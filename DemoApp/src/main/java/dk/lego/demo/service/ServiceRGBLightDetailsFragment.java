package dk.lego.demo.service;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import dk.lego.demo.R;
import dk.lego.demo.utils.ConnectedDeviceHelper;
import dk.lego.devicesdk.services.RGBLight;
import dk.lego.devicesdk.services.RGBLightCallbackListener;
import dk.lego.devicesdk.utils.ByteUtils;

public class ServiceRGBLightDetailsFragment extends ServiceBaseFragment implements RGBLightCallbackListener {

    private LinearLayout layoutDiscreteMode;
    private LinearLayout layoutAbsoluteMode;

    private Button buttonWriteColor;
    private Button buttonSwitchOff;
    private Button buttonSwitchToDefault;
    private Button buttonPreviousColorIndex;
    private Button buttonNextColorIndex;

    private SeekBar seekBarRed;
    private SeekBar seekBarGreen;
    private SeekBar seekBarBlue;
    private TextView textColorSample;
    private TextView textColorIndex;

    public static Fragment newInstance() {
        return new ServiceRGBLightDetailsFragment();
    }

    public ServiceRGBLightDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Add motor specific details
        ViewGroup content = (ViewGroup) rootView.findViewById(R.id.da_content_frame);
        content.addView(inflater.inflate(R.layout.da_fragment_service_rgb_details, container, false));

        layoutDiscreteMode = (LinearLayout) rootView.findViewById(R.id.da_service_details_rgb_button_discrete_mode);
        layoutAbsoluteMode = (LinearLayout) rootView.findViewById(R.id.da_service_details_rgb_button_absolute_mode);

        textColorSample = (TextView) rootView.findViewById(R.id.service_details_rgb_color_sample);
        textColorIndex = (TextView) rootView.findViewById(R.id.service_details_rgb_color_index);

        seekBarRed = (SeekBar) rootView.findViewById(R.id.service_details_rgb_seek_bar_red);
        seekBarGreen = (SeekBar) rootView.findViewById(R.id.service_details_rgb_seek_bar_green);
        seekBarBlue = (SeekBar) rootView.findViewById(R.id.service_details_rgb_seek_bar_blue);

        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                updateSampleColor();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            private void updateSampleColor() {
                String rgbInHex = String.format("#%02x%02x%02x", seekBarRed.getProgress(), seekBarGreen.getProgress(), seekBarBlue.getProgress());
                textColorSample.setText(String.format(getString(R.string.da_service_details_rgb_color_sample), rgbInHex));
                textColorSample.setBackgroundColor(getRGBColorFromSeekBars());
            }
        };
        seekBarRed.setOnSeekBarChangeListener(seekBarChangeListener);
        seekBarGreen.setOnSeekBarChangeListener(seekBarChangeListener);
        seekBarBlue.setOnSeekBarChangeListener(seekBarChangeListener);

        buttonWriteColor = (Button) rootView.findViewById(R.id.da_service_details_rgb_button_write_color);
        buttonSwitchOff = (Button) rootView.findViewById(R.id.da_service_details_rgb_button_switch_off);
        buttonSwitchToDefault = (Button) rootView.findViewById(R.id.da_service_details_rgb_button_switch_to_default);
        buttonPreviousColorIndex = (Button) rootView.findViewById(R.id.service_details_rgb_color_index_decrease);
        buttonNextColorIndex = (Button) rootView.findViewById(R.id.service_details_rgb_color_index_increase);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final RGBLight rgbLight = (RGBLight) ConnectedDeviceHelper.getInstance().getService();

        buttonWriteColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rgbLight.setColor(getRGBColorFromSeekBars());
            }
        });

        buttonSwitchOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rgbLight.switchOff();
            }
        });

        buttonSwitchToDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rgbLight.switchToDefaultColor();
            }
        });

        buttonPreviousColorIndex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rgbLight.getColorIndex() > 0) {
                    rgbLight.setColorIndex(rgbLight.getColorIndex() - 1);
                }
            }
        });

        buttonNextColorIndex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rgbLight.setColorIndex(rgbLight.getColorIndex() + 1);
            }
        });
    }

    @Override
    protected void didUpdateValueData(byte[] newValue) {
        final RGBLight rgbLight = (RGBLight) ConnectedDeviceHelper.getInstance().getService();
        if (rgbLight.getRGBMode() == RGBLight.RGBLightMode.RGB_LIGHT_MODE_DISCRETE) {
            textServiceValue.setText(String.valueOf(rgbLight.getIntegerFromData(newValue)));
        } else {
            textServiceValue.setText(ByteUtils.toHexString(newValue));
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        final RGBLight rgbLight = (RGBLight) ConnectedDeviceHelper.getInstance().getService();

        showUIForMode(rgbLight.getInputFormatMode());
        if (rgbLight.getRGBMode() == RGBLight.RGBLightMode.RGB_LIGHT_MODE_DISCRETE) {
            updateDiscreteModeUI(rgbLight.getColorIndex());
        }
    }

    private int getRGBColorFromSeekBars() {
        return Color.rgb(seekBarRed.getProgress(), seekBarGreen.getProgress(), seekBarBlue.getProgress());
    }

    private void updateDiscreteModeUI(int colorIndex) {
        textColorIndex.setText(String.format(getString(R.string.da_service_details_rgb_color_index_label), colorIndex));
    }

    private void showUIForMode(int mode) {
        switch (RGBLight.RGBLightMode.fromInteger(mode)) {
            case RGB_LIGHT_MODE_DISCRETE:
                layoutDiscreteMode.setVisibility(View.VISIBLE);
                layoutAbsoluteMode.setVisibility(View.GONE);
                break;
            case RGB_LIGHT_MODE_ABSOLUTE:
                layoutDiscreteMode.setVisibility(View.GONE);
                layoutAbsoluteMode.setVisibility(View.VISIBLE);
                break;
            default:
                layoutDiscreteMode.setVisibility(View.GONE);
                layoutAbsoluteMode.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void didReadInitialColor(RGBLight rgbLight) {
    }

    @Override
    public void didUpdateRGBValue(RGBLight rgbLight, Integer oldRGBColor, Integer newRGBColor) {
    }

    @Override
    public void didUpdateRGBColorIndex(RGBLight rgbLight, int oldColorIndex, int newColorIndex) {
        updateDiscreteModeUI(newColorIndex);
    }
}