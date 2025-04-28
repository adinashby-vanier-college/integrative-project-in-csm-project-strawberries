package edu.vanier.strawberries.Models;

import edu.vanier.strawberries.ui.MainApp;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class DrawingTool {

    public Color defaultWireColor, defaultLightbulbColor;
    private Color currentColor;
    private String currentAction;
    private boolean pencilDown;

    public DrawingTool() {
        currentAction = "";
        defaultWireColor = Color.BLACK;
        defaultLightbulbColor = Color.YELLOW;
    }

    public void setCurrentAction(String currentAction) {
        this.currentAction = currentAction;

        Cursor updatedCursor = Cursor.DEFAULT;
        if(currentAction.contains("-")) {
            String component_placed = currentAction.substring(currentAction.indexOf('-')+1);
            switch (component_placed) {
                case "wire" -> {
                    Image pencil =  new Image(String.valueOf(getClass().getResource("/images/pencil.png")));
                    updatedCursor = new ImageCursor(pencil);
                }
                case "battery","capacitor","fuse","lightbulb","resistor" -> {
                    Image img = new Image(String.valueOf(getClass().getResource("/images/"+component_placed+"_diagram.png")));
                    updatedCursor = new ImageCursor(img, -img.getWidth(),img.getHeight()/2);
                }
                case "switch" -> {
                    Image img = new Image(String.valueOf(getClass().getResource("/images/open_switch_diagram.png")));
                    updatedCursor = new ImageCursor(img, -img.getWidth(),img.getHeight()/2);
                }
            }
        }
        MainApp.mainAppFXMLController.setCursor(updatedCursor);
    }

    public String getCurrentAction() {
        return currentAction;
    }

    public void setPencilDown(boolean down) {
        pencilDown = down;
    }

    public boolean isPencilDown() {
        return pencilDown;
    }

    public void setCurrentColor(Color color) {
        currentColor = color;
    }

    public Color getCurrentColor() {
        return currentColor;
    }
}
