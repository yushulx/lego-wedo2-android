package dk.lego.demo.service;

import android.app.Fragment;

import dk.lego.devicesdk.utils.ByteUtils;

public class ServiceGenericDetailsFragment extends ServiceBaseFragment {
    public static Fragment newInstance() {
        return new ServiceGenericDetailsFragment();
    }

    public ServiceGenericDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    protected void didUpdateValueData(byte[] newValue) {
        textServiceValue.setText(ByteUtils.toHexString(newValue));
    }
}