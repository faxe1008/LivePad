package com.faxe.livepad.model.canvas;

public class CanvasWhiteboardShapeOptions {

    private boolean shouldFillShape;
    private String fillStyle;
    private String strokeStyle;
    private float lineWidth;
    private String lineJoin;
    private String lineCap;
    public CanvasWhiteboardShapeOptions(){}
    public CanvasWhiteboardShapeOptions(boolean shouldFillShape, String fillStyle, String strokeStyle, float lineWidth, String lineJoin, String lineCap) {
        this.shouldFillShape = shouldFillShape;
        this.fillStyle = fillStyle;
        this.strokeStyle = strokeStyle;
        this.lineWidth = lineWidth;
        this.lineJoin = lineJoin;
        this.lineCap = lineCap;
    }

    public boolean isShouldFillShape() {
        return shouldFillShape;
    }

    public String getFillStyle() {
        return fillStyle;
    }

    public String getStrokeStyle() {
        return strokeStyle;
    }

    public float getLineWidth() {
        return lineWidth;
    }

    public String getLineJoin() {
        return lineJoin;
    }

    public String getLineCap() {
        return lineCap;
    }
}
