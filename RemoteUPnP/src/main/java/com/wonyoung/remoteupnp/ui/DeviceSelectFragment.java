package com.wonyoung.remoteupnp.ui;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.wonyoung.remoteupnp.DeviceAdapter;
import com.wonyoung.remoteupnp.R;
import com.wonyoung.remoteupnp.UPnPService;

import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.ModelDetails;
import org.fourthline.cling.model.meta.Service;

/**
 * Created by wonyoungjang on 13. 10. 17..
 */
public class DeviceSelectFragment extends Fragment {
    private DeviceAdapter rendererAdapter;
    private DeviceAdapter mediaServerAdapter;
    private UPnPService service;

    private AdapterView.OnItemClickListener mediaServerOnItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final Device device = (Device) parent.getItemAtPosition(position);
            final Context context = view.getContext();
            openDeviceDetailDialog(context, device);
            service.setMediaServer(device);
        }
    };

    private AdapterView.OnItemClickListener rendererOnItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final Device device = (Device) parent.getItemAtPosition(position);
            final Context context = view.getContext();
            openDeviceDetailDialog(context, device);
            service.setRenderer(device);
        }
    };

    private void openDeviceDetailDialog(Context context, Device device) {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle(device.toString());

        dialog.setMessage(getDetailsMessage(device));
        dialog.setButton(
                "Close",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity activity = (MainActivity) getActivity();
//                            onMediaServerSelected(device);
                    }
                }
        );
        dialog.show();
        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
        textView.setTextSize(12);
    }

    private String getDetailsMessage(Device device) {
        StringBuilder sb = new StringBuilder();
        DeviceDetails detail = device.getDetails();
        sb.append("BaseURL : " + detail.getBaseURL() + "\n");
        sb.append("FriendlyName : " + detail.getFriendlyName() + "\n");
        sb.append("SerialNumber : " + detail.getSerialNumber() + "\n");
        sb.append("Upc : " + detail.getUpc() + "\n");
        sb.append("PresentationURI : " + detail.getPresentationURI() + "\n");

        ModelDetails modelDetails = detail.getModelDetails();
        sb.append("\n");
        sb.append("ModelDescription : " + modelDetails.getModelDescription() + "\n");
        sb.append("ModelName : " + modelDetails.getModelName() + "\n");
        sb.append("ModelNumber : " + modelDetails.getModelNumber() + "\n");
        sb.append("ModelURI : " + modelDetails.getModelURI() + "\n");

        sb.append("\n\n");
        if (device.isFullyHydrated()) {
            for (Service service : device.getServices()) {
                sb.append(service.getServiceType()).append("\n");
                for (Action action : service.getActions()) {
                    sb.append("(" + action.getName() + ")\n");
                }
                sb.append("\n");
            }
        } else {
            sb.append("Device Details not yet Available");
        }

        return sb.toString();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_device_select, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MainActivity activity = (MainActivity) getActivity();
        this.service = activity.getUPnPService();

        rendererAdapter = new DeviceAdapter(activity, DeviceAdapter.SERVICE_TYPE_RENDERER);
        ListView rendererListView = (ListView) getActivity().findViewById(R.id.renderer_list);
        rendererListView.setAdapter(rendererAdapter);
        rendererListView.setOnItemClickListener(rendererOnItemClick);
        service.addListener(rendererAdapter);

        mediaServerAdapter = new DeviceAdapter(activity, DeviceAdapter.SERVICE_TYPE_MEDIA_SERVER);
        ListView mediaServerListView = (ListView) getActivity().findViewById(R.id.media_server_list);
        mediaServerListView.setAdapter(mediaServerAdapter);
        mediaServerListView.setOnItemClickListener(mediaServerOnItemClick);
        service.addListener(mediaServerAdapter);
    }
}