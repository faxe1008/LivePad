package com.faxe.livepad.activities;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.faxe.livepad.service.MqttConnectionManagerService;
import com.faxe.livepad.service.MqttServiceConnection;

public abstract class BasicServiceActivity extends AppCompatActivity implements ServiceAttachedListener  {
    protected MqttServiceConnection mqttServiceConnection;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mqttServiceConnection = new MqttServiceConnection(this);
        attachService();

    }

    private void attachService() {
        Intent service = new Intent(this, MqttConnectionManagerService.class);
        startService(service);
        bindService(service, mqttServiceConnection, Service.BIND_AUTO_CREATE);
    }


    private void detachService() {
        unbindService(mqttServiceConnection);
    }

    @Override
    protected void onDestroy() {
        detachService();
        super.onDestroy();
    }

}
