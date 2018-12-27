package com.faxe.livepad.activities;

import com.faxe.livepad.service.MqttConnectionManagerService;

public interface ServiceAttachedListener {
    void onAttach(MqttConnectionManagerService service);
    void onDetach();
}
