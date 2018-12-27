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
    private ArrayList<CanvasWhiteboardUpdate> updates = new ArrayList<>();
    private CanvasWhiteBoardUpdateListener updateListener;
    private UUID currentShapeID;

    public CanvasWhiteboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setupPaint();
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
        canvas.drawPath(path, drawPaint);
    }

    public void applyUpdates(List<CanvasWhiteboardUpdate> updates){
        this.updates.addAll(updates);
        Map<UUID, List<CanvasWhiteboardUpdate>> updateByUuid = new HashMap<>();
        for(CanvasWhiteboardUpdate update: updates){

            if(!updateByUuid.containsKey(update.getUuid())){
                updateByUuid.put(update.getUuid(), new ArrayList<CanvasWhiteboardUpdate>());
            }
            updateByUuid.get(update.getUuid()).add(update);

        }
        updateByUuid.get(UUID.randomUUID());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float pointX = event.getX();
        float pointY = event.getY();


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentShapeID = UUID.randomUUID();
                path.moveTo(pointX, pointY);
                CanvasWhiteboardShapeOptions option = new CanvasWhiteboardShapeOptions(true, this.colorRGB, this.colorRGB, 2, "round", "round");
                CanvasWhiteboardUpdate downUpdate = new CanvasWhiteboardUpdate(pointX/this.getWidth(), pointY/getHeight(), CanvasWhiteboardUpdateType.START , currentShapeID, "FreeHandShape", option);
                this.updates.add(downUpdate);
                this.updateListener.onUpdate(new CanvasWhiteboardUpdate[]{downUpdate});

                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(pointX, pointY);
                CanvasWhiteboardUpdate moveUpdate = new CanvasWhiteboardUpdate(pointX/this.getWidth(), pointY/getHeight(), CanvasWhiteboardUpdateType.DRAG , currentShapeID);
                this.updates.add(moveUpdate);
                this.updateListener.onUpdate(new CanvasWhiteboardUpdate[]{moveUpdate});


                break;

             case MotionEvent.ACTION_UP:
                 CanvasWhiteboardUpdate upUpdate = new CanvasWhiteboardUpdate(pointX/this.getWidth(), pointY/getHeight(), CanvasWhiteboardUpdateType.STOP , currentShapeID);
                 this.updates.add(upUpdate);
                 this.updateListener.onUpdate(new CanvasWhiteboardUpdate[]{upUpdate});

                 break;
            default:
                return false;
        }
        // Force a view to draw again
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
