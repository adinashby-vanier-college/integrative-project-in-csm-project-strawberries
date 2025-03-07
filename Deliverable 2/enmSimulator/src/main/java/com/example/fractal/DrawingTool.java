package com.example.fractal;

import javafx.scene.effect.ColorAdjust;
import javafx.scene.paint.Color;

public class DrawingTool {

    protected ColorAdjust defaultColor = new ColorAdjust();
    private String currentAction;
    private boolean pencilDown;

    public DrawingTool() {
        currentAction = "";
        setColor(Color.BLACK);
    }

    public void setCurrentAction(String currentAction) {
        this.currentAction = currentAction;
    }

    public String getCurrentAction() {
        return currentAction;
    }

    public void setColor(Color target) {
        double hue = -1+((target.getHue()+ (target.getHue()>180 ? -180 : 180))*2)/360;
        double brightness = target.getBrightness()-1;
        double saturation = target.getSaturation();

        defaultColor.setHue(hue);
        defaultColor.setBrightness(brightness);
        defaultColor.setSaturation(saturation);
    }

    public void setPencilDown(boolean down) {
        pencilDown = down;
    }

    public boolean isPencilDown() {
        return pencilDown;
    }
}
