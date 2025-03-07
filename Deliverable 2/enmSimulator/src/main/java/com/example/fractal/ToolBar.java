package com.example.fractal;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

public class ToolBar extends ScrollPane {

    DrawingTool tool;

    public ToolBar(DrawingTool tool) {
        // initialization and settings
        HBox toolBar = new HBox();
        this.setContent(toolBar);
        this.tool = tool;
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
        addWire.setOnAction(_->tool.setCurrentAction("place-wire"));

        Button addResistor = new Button("Add Resistor");
        addResistor.setOnAction(_->tool.setCurrentAction("place-resistor"));

        Button addBattery = new Button("Add Battery");
        addBattery.setOnAction(_->tool.setCurrentAction("place-battery"));

        Button addCapacitor = new Button("Add Capacitor");
        addCapacitor.setOnAction(_->tool.setCurrentAction("place-capacitor"));

        Button addSwitch = new Button("Add Switch");
        addSwitch.setOnAction(_->tool.setCurrentAction("place-switch"));

        //ADD ALL BUTTONS
        toolBar.getChildren().addAll(zoomIn, zoomOut, undo, redo, copy, paste, addWire, addResistor, addCapacitor, addBattery, addSwitch);

        // SETTINGS
        toolBar.setAlignment(Pos.TOP_LEFT);
    }
}
