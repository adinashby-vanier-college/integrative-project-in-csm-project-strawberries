package com.example.fractal;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DisplayPane {

    public VBox ButtonsAndGraphs() {
        VBox ButtonPane = new VBox();

        // Button
        Button toggleGeneratorButton = new Button("Button");
        toggleGeneratorButton.setOnAction(e -> {
            System.out.println(":P");
        });
        ButtonPane.getChildren().add(toggleGeneratorButton);

        ButtonPane.setAlignment(Pos.CENTER);
        ButtonPane.setSpacing(10);
        ButtonPane.setPadding(new Insets(10, 10, 10, 10));

        return ButtonPane;
    }
}
