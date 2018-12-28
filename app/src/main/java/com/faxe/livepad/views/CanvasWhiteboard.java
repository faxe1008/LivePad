package com.faxe.livepad.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.faxe.livepad.model.canvas.*;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CanvasWhiteboard extends View {

    private int paintColor = Color.BLACK;
    private String colorRGB = "rgb(0,0,0)";
    private Paint drawPaint;
    private Path path = new Path();
    private CanvasWhiteBoardUpdateListener updateListener;
    private Map<UUID, List<CanvasWhiteboardUpdate>> updates;
    private UUID currentShapeID;

    public CanvasWhiteboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setupPaint();
        this.updates = new HashMap<>();
    }

    public void setUpdateListener(CanvasWhiteBoardUpdateListener listener){
        this.updateListener = listener;
    }

    public void setDrawColor(String color){
        this.colorRGB = color;
        this.paintColor = parseColorString(color);
        drawPaint.setColor(paintColor);
    }

    private void setupPaint() {
        // Setup paint with color and stroke styles
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(5);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //draw own stuff
        canvas.drawPath(path, drawPaint);
        applyUpdates(canvas);
    }

    public void applyUpdates(Canvas canvas){
        for (UUID shapeId : this.updates.keySet()){
            drawSingleShape(canvas, this.updates.get(shapeId));
        }
    }

    public void drawSingleShape(Canvas canvas, List<CanvasWhiteboardUpdate> shapeData){
        Path shapePath = new Path();
        Paint shapePaint = new Paint();
        shapePaint.setAntiAlias(true);
        shapePaint.setStyle(Paint.Style.STROKE);
        shapePaint.setStrokeJoin(Paint.Join.ROUND);
        shapePaint.setStrokeCap(Paint.Cap.ROUND);

        for (CanvasWhiteboardUpdate drawingUpdate : shapeData){

            float xPos = drawingUpdate.getX() * getWidth();
            float yPos = drawingUpdate.getY() * getHeight();

            if(drawingUpdate.getType() == CanvasWhiteboardUpdateType.START){
                shapePath.moveTo(xPos, yPos);
                shapePaint.setColor(parseColorString(drawingUpdate.getSelectedShapeOptions().getStrokeStyle()));
                shapePaint.setStrokeWidth(drawingUpdate.getSelectedShapeOptions().getLineWidth());
            }else if(drawingUpdate.getType() == CanvasWhiteboardUpdateType.DRAG){
                shapePath.lineTo(xPos, yPos);
            }

        }

        canvas.drawPath(shapePath, shapePaint);
    }

    public void processUpdates(List<CanvasWhiteboardUpdate> updates){
        for(CanvasWhiteboardUpdate update: updates){
            if(!this.updates.containsKey(update.getUuid())){
                this.updates.put(update.getUuid(), new ArrayList<CanvasWhiteboardUpdate>());
            }
            this.updates.get(update.getUuid()).add(update);
        }
        postInvalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float pointX = event.getX();
        float pointY = event.getY();


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentShapeID = UUID.randomUUID();
                path.moveTo(pointX, pointY);
                CanvasWhiteboardShapeOptions option = new CanvasWhiteboardShapeOptions(true, this.colorRGB, this.colorRGB, drawPaint.getStrokeWidth(), "round", "round");
                CanvasWhiteboardUpdate downUpdate = new CanvasWhiteboardUpdate(pointX/this.getWidth(), pointY/getHeight(), CanvasWhiteboardUpdateType.START , currentShapeID, "FreeHandShape", option);
                this.updateListener.onUpdate(new CanvasWhiteboardUpdate[]{downUpdate});

                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(pointX, pointY);
                CanvasWhiteboardUpdate moveUpdate = new CanvasWhiteboardUpdate(pointX/this.getWidth(), pointY/getHeight(), CanvasWhiteboardUpdateType.DRAG , currentShapeID);
                this.updateListener.onUpdate(new CanvasWhiteboardUpdate[]{moveUpdate});


                break;

             case MotionEvent.ACTION_UP:
                 CanvasWhiteboardUpdate upUpdate = new CanvasWhiteboardUpdate(pointX/this.getWidth(), pointY/getHeight(), CanvasWhiteboardUpdateType.STOP , currentShapeID);
                 this.updateListener.onUpdate(new CanvasWhiteboardUpdate[]{upUpdate});

                 break;
            default:
                return false;
        }
        postInvalidate();
        return true;
    }

    private int parseColorString(String colorStr){
        String splitStr = colorStr.substring(colorStr.indexOf('(') + 1, colorStr.indexOf(')'));
        String[] splitString = splitStr.split(",");

        int colorValues[] = new int[splitString.length];
        for (int i = 0; i < splitString.length; i++) {
            colorValues[i] = Integer.parseInt(splitString[i].trim());
        }

        return Color.rgb(colorValues[0], colorValues[1],colorValues[2]);
    }
}
