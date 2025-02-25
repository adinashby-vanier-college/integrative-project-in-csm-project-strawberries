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
        double hue = map( target.getHue(), 0, 360, -1, 1);
        double brightness = map(target.getBrightness(), 0,1,-1,1);
        double saturation = map(target.getSaturation(),0,1,-1,1);
        System.out.println(hue+"   "+saturation+"   "+brightness);
        defaultColor.setHue(hue);
        defaultColor.setBrightness(brightness);
        defaultColor.setSaturation(saturation);
    }

    // Link: https://stackoverflow.com/questions/31587092/how-to-use-coloradjust-to-set-a-target-color
    // TODO make sure this works in our context
    public static double map(double value, double start, double stop, double targetStart, double targetStop) {
        return targetStart + (targetStop - targetStart) * ((value - start) / (stop - start));
    }

    public void setPencilDown(boolean down) {
        pencilDown = down;
    }

    public boolean isPencilDown() {
        return pencilDown;
    }
}
