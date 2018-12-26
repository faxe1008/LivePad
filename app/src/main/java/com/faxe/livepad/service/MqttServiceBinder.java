package com.faxe.livepad.service;

import android.os.Binder;

public class MqttServiceBinder extends Binder {
    private MqttConnectionManagerService mqttConnectionManagerService;

    public MqttServiceBinder(MqttConnectionManagerService mqttConnectionManagerService) {
        this.mqttConnectionManagerService = mqttConnectionManagerService;
    }

    MqttConnectionManagerService getService(){
        return this.mqttConnectionManagerService;
    }
}