package com.example.fractal;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class SidePanel extends VBox {

    public SidePanel() {

        // Name of circuit
        TextField circuitName = new TextField("Circuit #1");

        // Export Button
        Button exportButton = new Button("Export");
        exportButton.setOnAction(_-> {
            System.out.println(circuitName.getText());
        });
        Label s = new Label("");

        // Title
        Label viewChoose = new Label("View:");
        // Dropdown menu
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Realistic",
                        "Simple"
                );
        ComboBox comboBox = new ComboBox(options);
        comboBox.setOnAction(e -> {
            System.out.println(comboBox.getValue());
        });
        Label s1 = new Label("");

        // Title
        Label colorChoose = new Label("Default Wire Colour:");
        // Colour chooser
        ColorPicker picker = new ColorPicker();
        picker.setValue(Color.BLACK);
        picker.setOnAction(e -> {
            System.out.println(picker.getValue());
        });
        picker.setMinHeight(30);
        Label s2 = new Label("");
        s2.setPadding(new Insets(15, 0, 0, 0));

        // Toggle Switch
        CheckBox checkbox = new CheckBox("Show Component Polarity");
        checkbox.setOnAction(e -> {
            if (checkbox.isSelected()) {
                System.out.println("Clicked");
            } else {
                System.out.println("Unclicked");
            }
        });
        Label s3 = new Label("");

        // Formulas and more info


        // Run/stop, Reset, Clear
        HBox mini = new HBox();
        Button runStop = new Button("Run/Stop");

        Button reset = new Button("Reset");
        reset.setOnAction(e -> {
            System.out.println("Resetted");
        });

        Button clear = new Button("Clear");
        clear.setOnAction(e -> {
            System.out.println("Cleared");
        });

        // Running Status
        Label status = new Label("Click below to run!");
        final boolean[] running = {false};
        runStop.setOnAction(e -> {
            running[0] = !running[0];
            if (running[0]) {
                status.setText("Running");
            } else {
                status.setText("Stopped");
            }
        });

        clear.setStyle("-fx-background-color: #ff645c; -fx-font-weight: bold;");
        mini.setSpacing(5);
        mini.getChildren().addAll(runStop, reset, clear);
        mini.setPadding(new Insets(10, 10, 10 ,0));

        // SETTINGS
        this.getChildren().addAll(circuitName, exportButton, s, viewChoose, comboBox, s1, colorChoose, picker, s2, checkbox, s3, status, mini);

        this.setAlignment(Pos.BASELINE_LEFT);
        this.setSpacing(0);
        this.setPadding(new Insets(10, 10, 10, 10));
        this.setMinWidth(200);
    }
}
