package dk.lego.demo.device;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dk.lego.demo.R;
import dk.lego.devicesdk.device.ConnectInfo;
import dk.lego.devicesdk.input_output.InputFormat;
import dk.lego.devicesdk.services.LegoService;
import dk.lego.devicesdk.services.ServiceCallbackListener;
import dk.lego.devicesdk.utils.ByteUtils;

public class ServiceListAdapter extends BaseAdapter implements ServiceCallbackListener {

    private final LayoutInflater layoutInflater;
    private final Activity context;
    private List<LegoService> data = new ArrayList<>();

    @Override
    public void didUpdateValueData(LegoService service, byte[] oldValue, byte[] newValue) {
        updateItem(service);
    }

    @Override
    public void didUpdateInputFormat(LegoService service, InputFormat oldFormat, InputFormat newFormat) {
        updateItem(service);
    }

    private static class ViewHolder {
        protected TextView textServiceName;
        protected TextView textServiceUnit;
        protected TextView textServiceValue;
        protected TextView textServiceId;
        protected TextView textServiceHub;
        protected TextView textServiceType;
    }

    public ServiceListAdapter(final Activity context) {
        super();
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public void addAll(List<LegoService> services) {
        data.clear();
        for (LegoService service : services) {
            service.registerCallbackListener(this);
            data.add(service);
        }
        this.notifyDataSetChanged();
    }

    public void updateItem(LegoService service) {
        if (data.contains(service)) {
            data.set(data.indexOf(service), service);
            this.notifyDataSetChanged();
        }
    }

    public void removeItem(LegoService service) {
        service.unregisterCallbackListener(this);
        data.remove(service);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public LegoService getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.da_list_item_services, parent, false);

            vh = new ViewHolder();
            vh.textServiceName = (TextView) convertView.findViewById(R.id.name);
            vh.textServiceUnit = (TextView) convertView.findViewById(R.id.unit);
            vh.textServiceValue = (TextView) convertView.findViewById(R.id.value);
            vh.textServiceId = (TextView) convertView.findViewById(R.id.id_value);
            vh.textServiceHub = (TextView) convertView.findViewById(R.id.hub_value);
            vh.textServiceType = (TextView) convertView.findViewById(R.id.type_value);

            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        LegoService service = data.get(position);
        vh.textServiceName.setText(service.getServiceName());

        String value = context.getString(R.string.da_common_null);
        if (service.getInputFormat() != null) {
            value = service.getInputFormat().getUnit().toString();
        }
        vh.textServiceUnit.setText(String.format(context.getString(R.string.da_service_overview_unit), value));

        value = context.getString(R.string.da_common_null);
        if (service.getValueData() != null) {
            value = ByteUtils.toHexString(service.getValueData());
        }
        vh.textServiceValue.setText(String.format(context.getString(R.string.da_service_overview_value), value));

        ConnectInfo connectInfo = service.getConnectInfo();
        vh.textServiceId.setText(String.valueOf(addLeadingZeroes(String.valueOf(connectInfo.getConnectId()))));
        vh.textServiceHub.setText(String.valueOf(addLeadingZeroes(String.valueOf(connectInfo.getHubIndex()))));
        vh.textServiceType.setText(String.format(context.getString(R.string.da_service_overview_type), addLeadingZeroes(String.valueOf(connectInfo.getType())), connectInfo.getTypeString()));

        return convertView;
    }

    private String addLeadingZeroes(String string) {
        return ("00" + string).substring(string.length());
    }
}