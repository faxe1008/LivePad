package com.faxe.livepad.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

public class LivePadSession implements Serializable {

    private UUID uuid;
    private String encryptionKey;
    private User user;

    public LivePadSession(){}

    public LivePadSession(UUID uuid, String encryptionKey, User user) {
        this.uuid = uuid;
        this.encryptionKey = encryptionKey;
        this.user = user;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getJoinTopic(){
        return this.uuid + "/join/"+this.getUser().getName();
    }

    public String getJoinAcceptedTopic(){
        return this.getJoinTopic() + "/accepted";
    }

    public String getStartTopic(){
        return this.uuid + "/start";
    }

    public String getUserDrawingTopic(){ return this.uuid + "/draw/" + this.getUser().getName();}

    public boolean isListenableDrawingTopic(String topic){
        String[] topicSegment = topic.split("\\/");
        if(topicSegment[0].equals(this.uuid.toString()) && topicSegment[1].equals("draw") && !topicSegment[2].equals(this.user.getName())){
            return true;
        }
        return false;
    }

    public String getDrawingTopic(){
        return this.uuid + "/draw/#";
    }


    public String getHistoryReceivalTopic(){
        return this.uuid + "/history/" + this.user.getName() + "/get/accepted";
    }

}
