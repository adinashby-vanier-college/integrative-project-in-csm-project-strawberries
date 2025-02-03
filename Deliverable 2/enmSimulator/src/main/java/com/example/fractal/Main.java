package com.example.fractal;

import com.example.fractal.Components.Resistor;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    public void start(Stage primaryStage) {
        VBox vbox1 = new VBox();
        Scene scene = new Scene(vbox1);

        javafx.scene.control.MenuBar menuBar = new MenuBar(scene).make();
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("light-mode.css")).toExternalForm());
        vbox1.getChildren().addAll(menuBar);

        CircuitPane circuitPane = new CircuitPane();
        VBox paramPane = circuitPane.displayParamPane();

        ButtonPane displayPane = new ButtonPane();
        VBox display = displayPane.ButtonsAndGraphs();

        HBox pains = new HBox(paramPane, display);
        vbox1.getChildren().add(pains);

        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("strawberryy.jpg"))));
        primaryStage.setScene(scene);
        primaryStage.setTitle("Circuit Generator");
        primaryStage.show();
    }
}
