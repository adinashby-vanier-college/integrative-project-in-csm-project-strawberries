package com.example.fractal;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.util.Objects;

public class ToolBar {
    public ScrollPane displayToolBar() {

        HBox toolBar = new HBox();

        // Scrollpane init and settings
        ScrollPane toolz = new ScrollPane(toolBar);
        toolz.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        toolz.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);


        //BUTTONS
        Button zoomIn = new Button("Zoom in");

        Button zoomOut = new Button("Zoom out");

        Button undo = new Button("Undo");

        Button redo = new Button("Redo");

        Button copy = new Button("Copy");

        Button paste = new Button("Paste");

        // add components buttons
        Button addWire = new Button("Add Wire");

        Button addResistor = new Button("Add Resistor");

        Button addBattery = new Button("Add Battery");

        Button addCapacitor = new Button("Add Capacitor");

        Button addSwitch = new Button("Add Switch");

        //ADD ALL BUTTONS
        toolBar.getChildren().addAll(zoomIn, zoomOut, undo, redo, copy, paste, addWire, addResistor, addCapacitor, addBattery, addSwitch);

        // SETTINGS
        toolBar.setAlignment(Pos.TOP_LEFT);

        return toolz;
    }
}
