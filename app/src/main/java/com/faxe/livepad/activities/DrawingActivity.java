package com.faxe.livepad.activities;

import android.os.Bundle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.faxe.livepad.R;
import com.faxe.livepad.model.LivePadSession;
import com.faxe.livepad.model.canvas.CanvasWhiteboardUpdate;
import com.faxe.livepad.service.MqttClientNotConnectedException;
import com.faxe.livepad.service.MqttConnectionManagerService;
import com.faxe.livepad.views.CanvasWhiteBoardUpdateListener;
import com.faxe.livepad.views.CanvasWhiteboard;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;

public class DrawingActivity extends BasicServiceActivity implements CanvasWhiteBoardUpdateListener {

    private LivePadSession livePadSession;
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
    public void onUpdate(CanvasWhiteboardUpdate[] update) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            this.mqttServiceConnection.getService().publish(this.livePadSession.getUserDrawingTopic(), mapper.writeValueAsString(update) );
        } catch (MqttException | MqttClientNotConnectedException | JsonProcessingException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onAttach(MqttConnectionManagerService service) {

        service.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if(livePadSession.isListenableDrawingTopic(topic) || topic.equals(livePadSession.getHistoryReceivalTopic())){
                    ObjectMapper mapper = new ObjectMapper();
                    List<CanvasWhiteboardUpdate> updates = new ArrayList<>();
                    updates = mapper.readValue(message.toString(),  new TypeReference<ArrayList<CanvasWhiteboardUpdate>>() {});
                    canvasWhiteboard.processUpdates(updates);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) { }
        });

        try {
            service.subscribe(this.livePadSession.getDrawingTopic());
            service.subscribe(this.livePadSession.getHistoryReceivalTopic());
            service.publish(livePadSession.getHistoryRequestTopic(), "");
        } catch (MqttException | MqttClientNotConnectedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDetach() {

    }
}
