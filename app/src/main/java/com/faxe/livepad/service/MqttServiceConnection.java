package com.faxe.livepad.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public class MqttServiceConnection implements ServiceConnection {

    private MqttConnectionManagerService mqttConnectionManagerService;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        this.mqttConnectionManagerService = ((MqttServiceBinder) service).getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        this.mqttConnectionManagerService = null;
    }

    public MqttConnectionManagerService getMqttConnectionManagerService() {
        return mqttConnectionManagerService;
    }
}
