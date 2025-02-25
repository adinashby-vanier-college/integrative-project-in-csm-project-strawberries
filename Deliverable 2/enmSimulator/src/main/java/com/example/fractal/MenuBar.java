package com.example.fractal;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;

public class MenuBar {
    private String currentTheme;
    private Scene scene;
    private String currentFont;
    private DrawingArea area;; 

    public MenuBar(Scene scene, DrawingArea area) {
        this.scene = scene;
        this.area = area;
        this.currentTheme = "light"; // Default theme
        this.currentFont = "Monospace";
    }

    public javafx.scene.control.MenuBar make() {
    	 javafx.scene.control.MenuBar menuBar = new javafx.scene.control.MenuBar();

        // File Menu
        Menu file = new Menu("File");
        MenuItem about = new MenuItem("About");
        about.setOnAction(e -> {
            Alert aboutDialog = new Alert(Alert.AlertType.INFORMATION);
            aboutDialog.setTitle("About");
            aboutDialog.setHeaderText("Fractal Application");
            aboutDialog.setContentText("This is a simple fractal generation application.\nVersion 1.0");
            aboutDialog.show();
        });

        MenuItem importFile = new MenuItem("Import");
        MenuItem exportFile = new MenuItem("Export");
        MenuItem save = new MenuItem("Save");
        MenuItem saveAs = new MenuItem("Save As");
        file.getItems().addAll(about, importFile, exportFile, save, saveAs);

        // Settings Menu
        Menu settings = new Menu("Settings");
        // Preferences Window
        MenuItem preferences = new MenuItem("Preferences");
        preferences.setOnAction(e -> {
            Stage preferencesStage = new Stage();
            preferencesStage.setTitle("Preferences");

            VBox layout = new VBox(10);
            layout.setPadding(new Insets(10));

            // Elements
            Label label = new Label("Modify your preferences here.");
            Button saveButton = new Button("Save");

            layout.getChildren().addAll(label, saveButton);
            Scene preferencesScene = new Scene(layout, 300, 200);
            preferencesStage.setScene(preferencesScene);
            preferencesStage.show();
        });

        Menu view = new Menu("View");
        MenuItem showToolbar = new MenuItem("Show Toolbar");
        MenuItem hideToolbar = new MenuItem("Hide Toolbar");
        Menu theme = new Menu("Theme");
        MenuItem light = new MenuItem("Light");
        MenuItem dark = new MenuItem("Dark");
        Menu font = new Menu("Font");
        MenuItem comicSans = new MenuItem("Comic Sans");
        MenuItem monospace = new MenuItem("Monospace");
        MenuItem fitToScreen = new MenuItem("Fit to Screen");
        MenuItem zoomIn = new MenuItem("Zoom In");
        MenuItem zoomOut = new MenuItem("Zoom Out");
        MenuItem toggleGrid = new MenuItem("Toggle Grid");
        MenuItem resourcesAndReferences = new MenuItem("Resources and References");

        // Insert Menu
        Menu insert = new Menu("Insert");
        MenuItem wire = new MenuItem("Wire");
        MenuItem resistor = new MenuItem("Resistor");
        MenuItem switchItem = new MenuItem("Switch");
        MenuItem battery = new MenuItem("Battery");
       
        battery.setOnAction(_-> area.drawingTool.setCurrentAction("place-battery"));

        MenuItem voltmeter = new MenuItem("Voltmeter");
        MenuItem ampmeter = new MenuItem("Ammeter");
        Menu lightbulb = new Menu("Lightbulb");
        MenuItem lightbulbYellow = new MenuItem("Yellow");
        MenuItem lightbulbRed = new MenuItem("Red");
        MenuItem lightbulbGreen = new MenuItem("Green");
        MenuItem lightbulbBlue = new MenuItem("Blue");
        MenuItem lightbulbCustom = new MenuItem("Custom");
        lightbulb.getItems().addAll(lightbulbYellow, lightbulbRed, lightbulbGreen, lightbulbBlue, lightbulbCustom);
        battery.setOnAction(e -> {
            area.startPlacingBattery();  
        });


        // Edit Menu
        Menu edit = new Menu("Edit");
        MenuItem copy = new MenuItem("Copy");
        MenuItem paste = new MenuItem("Paste");
        MenuItem duplicate = new MenuItem("Duplicate");
        MenuItem undo = new MenuItem("Undo");
        MenuItem redo = new MenuItem("Redo");

        // Help Menu
        Menu help = new Menu("Help");
        MenuItem howToUse = new MenuItem("How to Use");
        MenuItem faq = new MenuItem("FAQ");

        // Themes
        light.setOnAction(e -> {
            if (!"light".equals(currentTheme)) {
                scene.getStylesheets().remove(Objects.requireNonNull(getClass().getResource("dark-mode.css")).toExternalForm());
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("light-mode.css")).toExternalForm());
                currentTheme = "light";
            }
        });

        dark.setOnAction(e -> {
            if (!"dark".equals(currentTheme)) {
                scene.getStylesheets().remove(Objects.requireNonNull(getClass().getResource("light-mode.css")).toExternalForm());
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("dark-mode.css")).toExternalForm());
                currentTheme = "dark";
            }
        });

        // Fonts
        comicSans.setOnAction(e -> {
            if (!"Comic Sans".equals(currentFont)) {
                scene.getStylesheets().remove(Objects.requireNonNull(getClass().getResource("monospace.css")).toExternalForm());
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("comic-sans.css")).toExternalForm());
                currentFont = "Comic Sans";
            }
        });

        monospace.setOnAction(e -> {
            if (!"Monospace".equals(currentFont)) {
                scene.getStylesheets().remove(Objects.requireNonNull(getClass().getResource("comic-sans.css")).toExternalForm());
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("monospace.css")).toExternalForm());
                currentFont = "Monospace";
            }
        });

        // Help Menu Actions
        howToUse.setOnAction(e -> {
            Alert howToUseDialog = new Alert(Alert.AlertType.INFORMATION);
            howToUseDialog.setTitle("How to Use");
            howToUseDialog.setHeaderText("User Guide");
            howToUseDialog.setContentText("Follow the instructions to get started.\nFor further assistance, consult the FAQ.");
            howToUseDialog.show();
        });

        faq.setOnAction(e -> {
            Alert faqDialog = new Alert(Alert.AlertType.INFORMATION);
            faqDialog.setTitle("FAQ");
            faqDialog.setHeaderText("Frequently Asked Questions");
            faqDialog.setContentText("Check here for answers to common questions.\nIf you need more help, contact support.");
            faqDialog.show();
        });

        // Adding everything together
        theme.getItems().addAll(light, dark);
        font.getItems().addAll(comicSans, monospace);
        view.getItems().addAll(showToolbar, hideToolbar);
        settings.getItems().addAll(preferences, view, theme, font, fitToScreen, zoomIn, zoomOut, toggleGrid, resourcesAndReferences);
        insert.getItems().addAll(wire, resistor, switchItem, battery, voltmeter, ampmeter, lightbulb);
        edit.getItems().addAll(copy, paste, duplicate, undo, redo);
        help.getItems().addAll(howToUse, faq);

        return menuBar;
    }
}
