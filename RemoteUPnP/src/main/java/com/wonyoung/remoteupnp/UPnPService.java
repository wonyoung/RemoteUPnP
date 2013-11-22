package com.wonyoung.remoteupnp;

import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.registry.RegistryListener;

import java.util.ArrayList;

/**
 * Created by wonyoungjang on 13. 10. 19..
 */
public interface UPnPService {
    void setMediaServer(int position);
    void setRenderer(int position);

    void execute(ActionCallback action);

    public interface DeviceList {

        void addListener(DeviceSelectFragment.DeviceAdapter deviceAdapter);

        Device get(int position);

        int size();

        void clear();

        void add(Device device);

        void remove(Device device);
    }

    void destroy();

    DeviceList getMediaServers();
    DeviceList getRenderers();
}