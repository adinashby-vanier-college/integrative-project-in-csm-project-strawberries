package com.example.fractal;

import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import javax.swing.text.Element;
import javax.swing.text.html.ImageView;
import java.util.Objects;

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

        // Formulas

        // More information button pop up
        Button popup = new Button("More information");
        popup.setOnAction(e -> {
            Stage codeStage = new Stage();
            codeStage.setTitle("More information");
            codeStage.setHeight(400);
            codeStage.setWidth(500);

            // Create text display, add dynamic variables
            TextFlow infoText = new TextFlow(
                    new Text("Total Resistance:\nBranch Voltage:\nBranch Current:\n")
            );
            infoText.setTextAlignment(TextAlignment.LEFT);

            // Graph title
            Label graphTitle = new Label("Sine Wave Graph");
            graphTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            // Placeholder graph: sine wave animation
            Canvas canvas = new Canvas(400, 200);
            GraphicsContext gc = canvas.getGraphicsContext2D();

            // Animation
            AnimationTimer timer = new AnimationTimer() {
                private double phase = 0;

                @Override
                public void handle(long now) {
                    gc.clearRect(0, 0, 400, 200);
                    gc.setStroke(Color.BLUE);
                    gc.setLineWidth(2);
                    double centerY = 100; // Half of canvas height

                    gc.beginPath();
                    gc.moveTo(0, centerY);

                    for (int x = 0; x < 400; x++) {
                        double y = centerY + 50 * Math.sin(0.05 * x + phase);
                        gc.lineTo(x, y);
                    }

                    gc.stroke();
                    phase += 0.1;
                }
            };
            timer.start();

            // Elements
            VBox content = new VBox(10, infoText, graphTitle, canvas);
            content.setPadding(new Insets(10, 10, 10, 10));
            content.setAlignment(Pos.CENTER_LEFT);
            Scene scene = new Scene(content);
            codeStage.setScene(scene);

            // Close button
            Button closeButton = new Button("Close");
            closeButton.setOnAction(event -> codeStage.close());

            VBox bottomContainer = new VBox(closeButton);
            bottomContainer.setAlignment(Pos.CENTER);
            bottomContainer.setSpacing(10);
            content.getChildren().add(bottomContainer);
            codeStage.show();

            // Stop animation
            codeStage.setOnCloseRequest(event -> timer.stop());
        });

        Label s4 = new Label("");

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
        this.getChildren().addAll(circuitName, exportButton, s, viewChoose, comboBox, s1, colorChoose, picker, s2, checkbox, s3, popup, s4, status, mini);

        this.setAlignment(Pos.BASELINE_LEFT);
        this.setSpacing(0);
        this.setPadding(new Insets(10, 10, 10, 10));
        this.setMinWidth(200);
    }
}
