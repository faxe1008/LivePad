package com.faxe.livepad.model;

import java.io.Serializable;

public class User implements Serializable {

    private String name;
    private String color;

    public User(){}

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
