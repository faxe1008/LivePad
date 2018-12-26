package com.faxe.livepad.model;

import java.util.UUID;

public class LivePadSession {

    private UUID uuid;
    private String encryptionKey;
    private User user;
    private boolean accepted = false;

    public LivePadSession(UUID uuid, String encryptionKey, User user) {
        this.uuid = uuid;
        this.encryptionKey = encryptionKey;
        this.user = user;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public User getUser() {
        return user;
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

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isAccepted() {
        return accepted;
    }
}