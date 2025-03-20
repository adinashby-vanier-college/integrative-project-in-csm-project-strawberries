package edu.vanier.strawberries;


import edu.vanier.strawberries.Components.*;
import edu.vanier.strawberries.ui.MainApp;
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
    Pane pane;

    public DrawingArea(Pane pane) {
        setPadding(new Insets(10, 10, 10, 10));
        // SET UP EVENT LISTENERS
        pane.addEventHandler(MouseEvent.MOUSE_PRESSED, this::mousePressed);
        pane.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::mouseDragged);
        pane.addEventHandler(MouseEvent.MOUSE_RELEASED, this::mouseReleased);
        this.pane = pane;

        setPrefHeight(400); // temporary
        drawingTool.setCurrentAction("");

//        drawingTool.setColor(app.getSidePanel().pickedColor); //TODO fix this
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
            pane.getChildren().add(selection);
            selection.draw();
        }
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
