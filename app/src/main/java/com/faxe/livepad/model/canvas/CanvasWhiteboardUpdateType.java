package com.faxe.livepad.model.canvas;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CanvasWhiteboardUpdateType {
    START , DRAG , STOP;

    @JsonValue
    public int toValue() {
        return ordinal();
    }
}
