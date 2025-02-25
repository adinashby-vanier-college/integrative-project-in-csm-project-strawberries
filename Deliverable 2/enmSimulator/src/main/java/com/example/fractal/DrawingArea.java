package com.example.fractal;

import com.example.fractal.Components.*;
import javafx.geometry.Insets;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.Objects;

public class DrawingArea extends Pane {
    public DrawingTool drawingTool = new DrawingTool();
    private Component selection;
    private Battery currentBattery;    // To store the current battery being placed

    public DrawingArea(Main app) {
        setBackground(Background.fill(Color.ANTIQUEWHITE));
        setPadding(new Insets(10, 10, 10, 10));

        // SET UP EVENT LISTENERS
        addEventHandler(MouseEvent.MOUSE_PRESSED, this::mousePressed);
        addEventHandler(MouseEvent.MOUSE_DRAGGED, this::mouseDragged);
        addEventHandler(MouseEvent.MOUSE_RELEASED, this::mouseReleased);

        setPrefHeight(400); // temporary
        drawingTool.setCurrentAction("");
        drawingTool.setColor(app.getSidePanel().pickedColor);
    }

    // Toggle placing battery or wire
    public void startPlacingBattery() {
        drawingTool.setCurrentAction("place-battery");
    }

    private void mousePressed(MouseEvent event) {
        if(!Objects.equals(drawingTool.getCurrentAction(),"")) {
            drawingTool.setPencilDown(true);
            Node eventLocation = new Node(event.getX(), event.getY());
            Node tempEnd = new Node(event.getX(), event.getY());
            switch (drawingTool.getCurrentAction()) {
                case "place-wire" -> selection = new Wire(eventLocation, tempEnd, drawingTool.defaultColor, 0, 0);
                case "place-battery" -> selection = new Battery(eventLocation,tempEnd, 12);
                case "place-capacitor" -> selection = new Capacitor(eventLocation,tempEnd,0,true,false);
                case "place-fuse" -> selection = new Fuse(eventLocation,tempEnd);
                case "place-lightbulb" -> selection = new Lightbulb(eventLocation,tempEnd);
                case "place-resistor" -> selection = new Resistor(eventLocation,tempEnd,100);
                case "place-switch" -> selection = new Switch(eventLocation,tempEnd,false);
            }
            selection.setX(selection.begin.getX());
            selection.setY(selection.begin.getY());
            getChildren().add(selection);
            selection.draw();
        }

//        if (Objects.equals(drawingTool.getCurrentAction(), "place-battery")) {
//            // Place the battery on mouse press
//            if (currentBattery == null) {
//                // First click - set the start node
//                Node startNode = new Node(event.getX(), event.getY());
//                currentBattery = new Battery(startNode, startNode, 9.0);  // Add battery with 9V (you can change this value)
//                getChildren().add(currentBattery);
//                currentBattery.draw();
//            } else {
//                // Second click - set the end node (you can decide the end node behavior based on your requirements)
//                currentBattery.end = new Node(event.getX(), event.getY());  // Update the battery's end node (you can change this logic)
//                currentBattery.draw();
//                drawingTool.setPencilDown(false);  // Finish placing the battery
//                currentBattery = null;  // Reset the battery placement state
//            }
//        }
    }

    private void mouseDragged(MouseEvent event) {
        if (drawingTool.isPencilDown() && selection != null) {
            selection.moveNode(selection.end, event.getX(), event.getY());
            selection.draw();
        }
    }

    private void mouseReleased(MouseEvent event) {
        if (drawingTool.isPencilDown()) {
            drawingTool.setPencilDown(false);
            selection = null;
        }
    }
}
