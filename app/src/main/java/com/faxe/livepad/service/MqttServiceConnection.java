package com.faxe.livepad.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.faxe.livepad.activities.ServiceAttachedListener;

public class MqttServiceConnection implements ServiceConnection {

    private ServiceAttachedListener attachedListener;
    private MqttConnectionManagerService mqttConnectionManagerService;

    public MqttServiceConnection(ServiceAttachedListener attachedListener) {
        this.attachedListener = attachedListener;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        this.mqttConnectionManagerService = ((MqttServiceBinder) service).getService();
        this.attachedListener.onAttach(this.mqttConnectionManagerService);

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        this.attachedListener.onDetach();
    }

    public MqttConnectionManagerService getService() {
        return mqttConnectionManagerService;
    }
}
