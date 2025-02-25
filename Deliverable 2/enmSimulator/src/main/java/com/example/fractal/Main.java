package com.example.fractal;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

//    public static DrawingArea area = new DrawingArea(); // Static so it can be accessed everywhere
    // Button pane init
    SidePanel sidePanel = new SidePanel(this);
    // Circuit pane init
    DrawingArea area = new DrawingArea(this);

    public void start(Stage primaryStage) {
        VBox outerOuterBox = new VBox(); // Outer Vbox
        Scene scene = new Scene(outerOuterBox);

        // Toolbar init
        ToolBar toolbar = new ToolBar(area.drawingTool);

        // Menu bar init
        javafx.scene.control.MenuBar menuBar = new MenuBar(scene,area).make();
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("light-mode.css")).toExternalForm());

        //Vbox2: ToolBar & Drawing Area
        VBox circuitPane = new VBox();
        circuitPane.getChildren().addAll(toolbar, area);

        //Hbox1: Vbox2 & Button pane
        HBox outerbox = new HBox();
        outerbox.getChildren().addAll(circuitPane, sidePanel);

        //Vbox1: Menu bar & Hbox1
        outerOuterBox.getChildren().addAll(menuBar, outerbox);

        // Vbox settings 

        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("strawberryy.jpg"))));
        primaryStage.setScene(scene);
        primaryStage.setTitle("Circuit Generator");
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();
        primaryStage.setAlwaysOnTop(false);
    }

    public SidePanel getSidePanel() {
        return sidePanel;
    }

    public void updateDefaultColor() {
        area.drawingTool.setColor(sidePanel.pickedColor);
    }
}
