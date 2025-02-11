package com.example.fractal;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class DrawingArea {
    public VBox displayDrawArea() {
        VBox parameterPane = new VBox();
        parameterPane.setStyle("-fx-padding: 50;");

        // TITLE
        Label title = new Label("EnM  :P");
        title.setStyle("-fx-font-size: 50");
        title.setAlignment(Pos.TOP_LEFT);
        parameterPane.getChildren().add(title);

        // SETTINGS
        parameterPane.setAlignment(Pos.CENTER);
        parameterPane.setSpacing(10);
        parameterPane.setPadding(new Insets(10, 10, 10, 10));

        return parameterPane;
    }
}
