package com.faxe.livepad.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttConnectionManagerService extends Service {
    private final IBinder mBinder = new MqttServiceBinder(this);
    private MqttAndroidClient client;
    private MqttConnectOptions options;

    @Override
    public void onCreate() {
        super.onCreate();
        options = createMqttConnectOptions();
        client = createMqttAndroidClient();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.connect(client, options);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private MqttConnectOptions createMqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);
        options.setKeepAliveInterval(20);
        options.setServerURIs(new String[]{"wss://livepad.ddns.net:9001"});
        return options;
    }

    private MqttAndroidClient createMqttAndroidClient() {
        return new MqttAndroidClient(this.getApplicationContext(), "wss://livepad.ddns.net:9001",MqttClient.generateClientId());
    }

    public void connect(final MqttAndroidClient client, MqttConnectOptions options) {
        try {
            if (!client.isConnected()) {
                IMqttToken token = client.connect(options, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.e("CONNECT!!!!!!!!!!!!!!1", "");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.e("DISCONNECT!!!!!!!!!!!1", "");
                    }
                });
            }
        } catch (MqttException e) {
            Log.e("MQTTConnectionManager", "Connection error occured", e);
        }
    }

    public void setCallback(MqttCallback callback){
        this.client.setCallback(callback);
    }

    public void publish(String topic, String message) throws MqttException, MqttClientNotConnectedException {
        if(client.isConnected()){
            client.publish(topic,new MqttMessage(message.getBytes()));
        }else{
            throw new MqttClientNotConnectedException("Client is not connected");
        }
    }

    public IMqttToken subscribe(String topic) throws MqttException, MqttClientNotConnectedException {
        if(client.isConnected()){
            return client.subscribe(topic, 0);
        }
        throw new MqttClientNotConnectedException("Client is not connected");
    }


}
