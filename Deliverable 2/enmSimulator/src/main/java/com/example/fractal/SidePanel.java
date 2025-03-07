package com.example.fractal;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class SidePanel extends VBox {
    Color pickedColor = Color.BLACK;

    public SidePanel(Main app) {

        // Name of circuit
        TextField circuitName = new TextField("Circuit #1");

        // Export Button
        Button exportButton = new Button("Export");
        exportButton.setOnAction(_ -> System.out.println(circuitName.getText()));
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
        comboBox.setOnAction(_->System.out.println(comboBox.getValue()));
        Label s1 = new Label("");

        // Title
        Label colorChoose = new Label("Default Wire Colour:");
        // Colour chooser
        ColorPicker picker = new ColorPicker();
        picker.setValue(Color.BLACK);
        picker.setOnAction(_ -> {
            pickedColor = picker.getValue();
            if(pickedColor==null) pickedColor = Color.BLACK;
            app.updateDefaultColor();
        });
        picker.setMinHeight(30);
        Label s2 = new Label("");
        s2.setPadding(new Insets(15, 0, 0, 0));

        // Toggle Switch
        CheckBox checkbox = new CheckBox("Show Component Polarity");
        checkbox.setOnAction(_-> {
            if (checkbox.isSelected()) {
                System.out.println("Clicked");
            } else {
                System.out.println("Unclicked");
            }
        });
        Label s3 = new Label("");

        // TODO: Formula info

        // More information button pop up
        Button popup = new Button("More information");
        popup.setOnAction(_ -> {
            Stage codeStage = new Stage();
            codeStage.setTitle("More information");
            codeStage.setHeight(500);
            codeStage.setWidth(600);

            // Create text display
            TextFlow infoText = new TextFlow(
                    new Text("Total Resistance:\nBranch Voltage:\nBranch Current:\n")
            );
            infoText.setTextAlignment(TextAlignment.LEFT);

            // Graph title
            Label graphTitle = new Label("Kirchhoff's Loop Rule Graph");
            graphTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            // Axies
            final NumberAxis xAxis = new NumberAxis();
            final NumberAxis yAxis = new NumberAxis();
            xAxis.setLabel("Loop Position");
            yAxis.setLabel("Potential Difference (V)");
            xAxis.setTickLabelsVisible(false);
            xAxis.setTickMarkVisible(false);
            xAxis.setMinorTickVisible(false);

            // Creating the chart
            final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
            lineChart.setTitle("Potential Difference in Kirchhoff's Loop");
            lineChart.setLegendVisible(false);

            XYChart.Series<Number, Number> series = new XYChart.Series<>();

            series.getData().add(new XYChart.Data<>(0, 0));   // Start at 0V
            series.getData().add(new XYChart.Data<>(1, 10));  // Increase
            series.getData().add(new XYChart.Data<>(2, 10));  // Stay constant
            series.getData().add(new XYChart.Data<>(3, 5));   // Decrease
            series.getData().add(new XYChart.Data<>(4, 5));   // Stay constant
            series.getData().add(new XYChart.Data<>(5, 0));   // Return to 0V

            lineChart.getData().add(series);

            // Close button
            Button closeButton = new Button("Close");
            closeButton.setOnAction(_ -> codeStage.close());

            // VBox layout
            VBox content = new VBox(10, infoText, graphTitle, lineChart, closeButton);
            content.setPadding(new Insets(10));
            content.setAlignment(Pos.CENTER);

            Scene scene = new Scene(content, 600, 500);
            codeStage.setScene(scene);
            codeStage.show();

            // Animation: the real thing coming soon

            VBox bottomContainer = new VBox(closeButton);
            bottomContainer.setAlignment(Pos.CENTER);
            bottomContainer.setSpacing(10);
            content.getChildren().add(bottomContainer);
            codeStage.show();
        });

        Label s4 = new Label("");

        // Run/stop, Reset, Clear
        HBox mini = new HBox();
        Button runStop = new Button("Run/Stop");

        Button reset = new Button("Reset");
        reset.setOnAction(_ -> {
            System.out.println("Resetted");
        });

        Button clear = new Button("Clear");
        clear.setOnAction(_ -> {
            System.out.println("Cleared");
        });

        // Running Status
        Label status = new Label("Click below to run!");
        final boolean[] running = {false};
        runStop.setOnAction(_ -> {
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
