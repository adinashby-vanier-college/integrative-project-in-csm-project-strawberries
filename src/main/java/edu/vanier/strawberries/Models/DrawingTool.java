package edu.vanier.strawberries.Models;

import javafx.scene.effect.ColorAdjust;
import javafx.scene.paint.Color;

public class DrawingTool {

    public Color defaultColor;
    private String currentAction;
    private boolean pencilDown;

    public DrawingTool() {
        currentAction = "";
        defaultColor = Color.BLACK;
    }

    public void setCurrentAction(String currentAction) {
        this.currentAction = currentAction;
    }

    public String getCurrentAction() {
        return currentAction;
    }

    public ColorAdjust generateColorAdjust(Color target) {
        double hue = -1+((target.getHue()+ (target.getHue()>180 ? -180 : 180))*2)/360;
        double brightness = target.getBrightness()-1;
        double saturation = target.getSaturation();

        return new ColorAdjust(hue,saturation,brightness,1);
    }

    public void setPencilDown(boolean down) {
        pencilDown = down;
    }

    public boolean isPencilDown() {
        return pencilDown;
    }
}
