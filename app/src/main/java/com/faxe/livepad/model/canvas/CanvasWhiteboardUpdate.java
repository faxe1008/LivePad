package com.faxe.livepad.model.canvas;

import java.util.UUID;

public class CanvasWhiteboardUpdate {

    private float x;
    private float y;
    private CanvasWhiteboardUpdateType type;
    private UUID uuid;

    private String selectedShape;
    private CanvasWhiteboardShapeOptions selectedShapeOptions;
    public CanvasWhiteboardUpdate(){}
    public CanvasWhiteboardUpdate(float x, float y, CanvasWhiteboardUpdateType type, UUID uuid) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.uuid = uuid;
    }

    public CanvasWhiteboardUpdate(float x, float y, CanvasWhiteboardUpdateType type, UUID uuid, String selectedShape, CanvasWhiteboardShapeOptions selectedShapeOptions) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.uuid = uuid;
        this.selectedShape = selectedShape;
        this.selectedShapeOptions = selectedShapeOptions;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public CanvasWhiteboardUpdateType getType() {
        return type;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getSelectedShape() {
        return selectedShape;
    }

    public CanvasWhiteboardShapeOptions getSelectedShapeOptions() {
        return selectedShapeOptions;
    }
}
