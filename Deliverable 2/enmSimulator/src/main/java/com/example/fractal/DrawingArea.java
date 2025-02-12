package com.example.fractal;

import com.example.fractal.Components.Wire;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class DrawingArea extends Pane {
    private boolean pencilDown;
    private Component selection;

    public DrawingArea() {
        setPadding(new Insets(50));
        setBackground(Background.fill(Color.ANTIQUEWHITE));
        // TITLE
        Label title = new Label("EnM  :P");
        title.setStyle("-fx-font-size: 50");
        title.setAlignment(Pos.TOP_LEFT);
        getChildren().add(title);

        // SETTINGS
//        setAlignment(Pos.CENTER);
//        setSpacing(10);
        setPadding(new Insets(10, 10, 10, 10));

        // SET UP EVENT LISTENERS
        addEventHandler(MouseEvent.MOUSE_PRESSED, this::mousePressed);
        addEventHandler(MouseEvent.MOUSE_DRAGGED, this::mouseDragged);
        addEventHandler(MouseEvent.MOUSE_RELEASED, this::mouseReleased);


        setPrefHeight(400); //temporary

        // INITIALIZING VARIABLES
        pencilDown = false;
    }

    private void mousePressed(MouseEvent event) {
        if(!pencilDown) {
            pencilDown = true;
            selection = new Wire(new Node(event.getX(), event.getY()),new Node(event.getX(), event.getY()));
            getChildren().add(selection);
            selection.draw();
        }
    }

    private void mouseDragged(MouseEvent event) {
        if(pencilDown && selection!=null) {
            selection.moveNode(selection.end, event.getX(), event.getY());
            selection.draw();
        }
    }

    private void mouseReleased(MouseEvent event) {
        if(pencilDown) {
            pencilDown = false;
            selection = null;
        }
    }

}
