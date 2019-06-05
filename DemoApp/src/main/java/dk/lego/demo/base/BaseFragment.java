package dk.lego.demo.base;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import dk.lego.demo.R;
import dk.lego.demo.helpers.DialogHelper;
import dk.lego.devicesdk.LDSDKError;
import dk.lego.devicesdk.device.DeviceManagerCallbackListener;
import dk.lego.devicesdk.device.LegoDevice;
import dk.lego.devicesdk.device.LegoDeviceManager;
import dk.lego.devicesdk.device.LegoDeviceManagerImpl;

public class BaseFragment extends Fragment implements DeviceManagerCallbackListener {

    protected LegoDeviceManager deviceManager;

    protected View rootView;
    protected View contentView;
    protected View loadingView;
    protected View spinnerView;
    protected View progressSpinner;
    protected View noDataView;
    protected TextView noDataText;
    protected Button retryButton;

    private View progressSpinnerLoader;

    public void inflate(LayoutInflater inflater, int layoutResource, ViewGroup container, boolean attachToRoot) {
        initRootViews(inflater.inflate(layoutResource, container, attachToRoot));
    }

    public void initRootViews(View rootView) {
        this.rootView = rootView;
        this.contentView = this.rootView.findViewById(R.id.da_content_frame);
        this.loadingView = this.rootView.findViewById(R.id.da_loader_frame);
        this.spinnerView = this.rootView.findViewById(R.id.da_spinner_frame);
        this.progressSpinner = this.rootView.findViewById(R.id.progress_spinner);
        this.noDataView = this.rootView.findViewById(R.id.da_no_data_frame);
        this.noDataText = (TextView) this.rootView.findViewById(R.id.no_data_desc);

        if (loadingView != null) {
            progressSpinnerLoader = loadingView.findViewById(R.id.progress_spinner);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        deviceManager = LegoDeviceManagerImpl.getInstance();
    }

    @Override
    public void onResume() {
        super.onResume();

        deviceManager.registerCallbackListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        deviceManager.unregisterCallbackListener(this);
    }

    public void showLoader() {
        if (contentView != null) {
            contentView.setVisibility(View.GONE);
        }

        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
            spinnerView.setVisibility(View.VISIBLE);
            noDataView.setVisibility(View.GONE);
        }
    }

    public void showContent() {
        if (progressSpinnerLoader != null) {
            progressSpinnerLoader.clearAnimation();
        }

        if (contentView != null) {
            contentView.setVisibility(View.VISIBLE);
        }

        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
    }

    public void showNoData() {
        if (noDataView != null) {
            if (progressSpinnerLoader != null) {
                progressSpinnerLoader.clearAnimation();
            }

            if (contentView != null) {
                contentView.setVisibility(View.GONE);
            }

            if (loadingView != null) {
                loadingView.setVisibility(View.VISIBLE);
                spinnerView.setVisibility(View.GONE);
                noDataView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void showNoData(String noDataText) {
        if (this.noDataText != null && retryButton != null) {
            this.noDataText.setText(noDataText);
            showNoData();
        }
    }


    @Override
    public void onDeviceAppeared(LegoDevice device) {
        // Do nothing
    }

    @Override
    public void onDeviceDisappeared(LegoDevice device) {
        // Do nothing
    }

    @Override
    public void onWillStartConnectingToDevice(LegoDevice device) {
        // Do nothing
    }

    @Override
    public void onDidFailToConnectToDevice(LegoDevice device, boolean autoReconnect, LDSDKError error) {
        DialogHelper.showConnectFailedDialog(getActivity(), device, autoReconnect);
    }

    @Override
    public void onDidDisconnectFromDevice(LegoDevice device, boolean autoReconnect, LDSDKError error) {
        DialogHelper.showDisconnectedDialog(getActivity(), device, autoReconnect);
    }

    @Override
    public void onDidStartInterrogatingDevice(LegoDevice device) {
        // Do nothing
    }

    @Override
    public void onDidFinishInterrogatingDevice(LegoDevice device) {
        // Do nothing
    }
}
