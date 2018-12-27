package com.faxe.livepad.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.faxe.livepad.R;
import com.faxe.livepad.model.LivePadSession;
import com.faxe.livepad.model.canvas.CanvasWhiteboardUpdate;
import com.faxe.livepad.service.MqttClientNotConnectedException;
import com.faxe.livepad.service.MqttConnectionManagerService;
import com.faxe.livepad.service.MqttServiceConnection;
import com.faxe.livepad.views.CanvasWhiteBoardUpdateListener;
import com.faxe.livepad.views.CanvasWhiteboard;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.List;

public class DrawingActivity extends AppCompatActivity implements CanvasWhiteBoardUpdateListener {

    private LivePadSession livePadSession;
    private MqttServiceConnection mqttServiceConnection;
    private CanvasWhiteboard canvasWhiteboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        this.canvasWhiteboard = findViewById(R.id.canvasWhiteboard);
        this.canvasWhiteboard.setUpdateListener(this);
        Bundle extras = getIntent().getExtras();
        if(extras!=null ){
            this.livePadSession = (LivePadSession) extras.getSerializable("livePadSession");
            this.canvasWhiteboard.setDrawColor(livePadSession.getUser().getColor());
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

    @Override
    public void onUpdate(CanvasWhiteboardUpdate[] update) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.enable(SerializationFeature.WRITE_ENUMS_USING_INDEX);

            this.mqttServiceConnection.getService().publish(this.livePadSession.getDrawingTopic(), mapper.writeValueAsString(update) );
        } catch (MqttException | MqttClientNotConnectedException | JsonProcessingException e) {
            e.printStackTrace();
        }
    }



}
