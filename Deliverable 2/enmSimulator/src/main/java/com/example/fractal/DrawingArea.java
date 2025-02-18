package com.example.fractal;

import com.example.fractal.Components.Wire;
import com.example.fractal.Components.Battery;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class DrawingArea extends Pane {
    private boolean pencilDown;
    private Component selection;
    private boolean isPlacingBattery;  // Flag for battery placement
    private Battery currentBattery;    // To store the current battery being placed

    public DrawingArea() {
        setPadding(new Insets(50));
        setBackground(Background.fill(Color.ANTIQUEWHITE));

        // TITLE
        Label title = new Label("EnM  :P");
        title.setStyle("-fx-font-size: 50");
        title.setAlignment(Pos.TOP_LEFT);
        getChildren().add(title);

        setPadding(new Insets(10, 10, 10, 10));

        // SET UP EVENT LISTENERS
        addEventHandler(MouseEvent.MOUSE_PRESSED, this::mousePressed);
        addEventHandler(MouseEvent.MOUSE_DRAGGED, this::mouseDragged);
        addEventHandler(MouseEvent.MOUSE_RELEASED, this::mouseReleased);

        setPrefHeight(400); // temporary
        pencilDown = false;
        isPlacingBattery = false;  // Initially not placing a battery
    }

    // Toggle placing battery or wire
    public void startPlacingBattery() {
        isPlacingBattery = true;
        System.out.println("Click to place the battery.");
    }

    private void mousePressed(MouseEvent event) {
        if (isPlacingBattery) {
            // Place the battery on mouse press
            if (currentBattery == null) {
                // First click - set the start node
                Node startNode = new Node(event.getX(), event.getY());
                currentBattery = new Battery(startNode, startNode, 9.0);  // Add battery with 9V (you can change this value)
                getChildren().add(currentBattery);
                currentBattery.draw();
            } else {
                // Second click - set the end node (you can decide the end node behavior based on your requirements)
                Node endNode = new Node(event.getX(), event.getY());
                currentBattery.end = endNode;  // Update the battery's end node (you can change this logic)
                currentBattery.draw();
                isPlacingBattery = false;  // Finish placing the battery
                currentBattery = null;  // Reset the battery placement state
            }
        } else {
            // Wire placement logic
            if (!pencilDown) {
                pencilDown = true;
                selection = new Wire(new Node(event.getX(), event.getY()), new Node(event.getX(), event.getY()), null, 0, 0);
                selection.setX(selection.begin.getX());
                selection.setY(selection.begin.getY());
                getChildren().add(selection);
                selection.draw();
            }
        }
    }

    private void mouseDragged(MouseEvent event) {
        if (pencilDown && selection != null) {
            selection.moveNode(selection.end, event.getX(), event.getY());
            selection.draw();
        }
    }

    private void mouseReleased(MouseEvent event) {
        if (pencilDown) {
            pencilDown = false;
            selection = null;
        }
    }
}
