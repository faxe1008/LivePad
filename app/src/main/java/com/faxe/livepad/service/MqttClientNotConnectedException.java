package com.faxe.livepad.service;

public class MqttClientNotConnectedException extends Exception {
    public MqttClientNotConnectedException(String message) {
        super(message);
    }
}
