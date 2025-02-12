package com.example.fractal;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

public class ToolBar extends ScrollPane {

    public ToolBar() {
        // initialization and settings
        HBox toolBar = new HBox();
        this.setContent(toolBar);

        this.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        this.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

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
    }
}
