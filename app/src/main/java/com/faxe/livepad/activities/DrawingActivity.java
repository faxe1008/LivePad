package com.faxe.livepad.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.faxe.livepad.R;
import com.faxe.livepad.model.LivePadSession;
import com.faxe.livepad.service.MqttConnectionManagerService;
import com.faxe.livepad.service.MqttServiceConnection;

public class DrawingActivity extends AppCompatActivity {

    private LivePadSession livePadSession;
    private MqttServiceConnection mqttServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        Bundle extras = getIntent().getExtras();
        if(extras!=null ){
            this.livePadSession = (LivePadSession) extras.getSerializable("livePadSession");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MqttConnectionManagerService.class);
        startService(intent);
        this.mqttServiceConnection = new MqttServiceConnection();
        bindService(intent, this.mqttServiceConnection, Context.BIND_AUTO_CREATE);
    }
}
