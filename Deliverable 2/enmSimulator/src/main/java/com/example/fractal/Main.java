package com.example.fractal;

import com.example.fractal.Components.Resistor;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    public void start(Stage primaryStage) {
        VBox outerOuterbox = new VBox(); // Outer Vbox
        Scene scene = new Scene(outerOuterbox);

        // Circuit pane init
        DrawingArea area = new DrawingArea();
        VBox drawArea = area.displayDrawArea();

        // Toolbar init
        ToolBar tools = new ToolBar();
        ScrollPane toolbarr = tools.displayToolBar();

        // Button pane init
        SidePanel displayPane = new SidePanel();
        VBox buttons = displayPane.ButtonsAndGraphs();


        // Menu bar init
        javafx.scene.control.MenuBar menuBar = new MenuBar(scene).make();
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("light-mode.css")).toExternalForm());

        //Vbox2: ToolBar & Drawing Area
        VBox circuitPane = new VBox();
        circuitPane.getChildren().addAll(toolbarr, drawArea);

        //Hbox1: Vbox2 & Button pane
        HBox outerbox = new HBox();
        outerbox.getChildren().addAll(circuitPane, buttons);

        //Vbox1: Menu bar & Hbox1
        outerOuterbox.getChildren().addAll(menuBar, outerbox);

        // Vbox settings 

        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("strawberryy.jpg"))));
        primaryStage.setScene(scene);
        primaryStage.setTitle("Circuit Generator");
        primaryStage.show();
    }
}
