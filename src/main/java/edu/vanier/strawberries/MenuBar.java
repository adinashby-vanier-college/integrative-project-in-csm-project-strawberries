package edu.vanier.strawberries;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;

public class MenuBar {
    private String currentTheme;
    private String currentFont;
    javafx.scene.control.MenuBar menuBar;

    public MenuBar(javafx.scene.control.MenuBar menuBar) {
        this.currentTheme = "light"; // Default theme
        this.currentFont = "Monospace";
        this.menuBar = menuBar;
    }

    public javafx.scene.control.MenuBar make() {
//        about.setOnAction(e -> {
//            Alert aboutDialog = new Alert(Alert.AlertType.INFORMATION);
//            aboutDialog.setTitle("About");
//            aboutDialog.setHeaderText("Fractal Application");
//            aboutDialog.setContentText("This is a simple fractal generation application.\nVersion 1.0");
//            aboutDialog.show();
//        });

//        preferences.setOnAction(e -> {
//            Stage preferencesStage = new Stage();
//            preferencesStage.setTitle("Preferences");
//
//            VBox layout = new VBox(10);
//            layout.setPadding(new Insets(10));
//
//            // Elements
//            Label label = new Label("Modify your preferences here.");
//            Button saveButton = new Button("Save");
//
//            layout.getChildren().addAll(label, saveButton);
//            Scene preferencesScene = new Scene(layout, 300, 200);
//            preferencesStage.setScene(preferencesScene);
//            preferencesStage.show();
//        });

//        // Themes
//        light.setOnAction(e -> {
//            if (!"light".equals(currentTheme)) {
//                scene.getStylesheets().remove(Objects.requireNonNull(getClass().getResource("dark-mode.css")).toExternalForm());
//                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("light-mode.css")).toExternalForm());
//                currentTheme = "light";
//            }
//        });
//
//        dark.setOnAction(e -> {
//            if (!"dark".equals(currentTheme)) {
//                scene.getStylesheets().remove(Objects.requireNonNull(getClass().getResource("light-mode.css")).toExternalForm());
//                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("dark-mode.css")).toExternalForm());
//                currentTheme = "dark";
//            }
//        });

//        // Fonts
//        comicSans.setOnAction(e -> {
//            if (!"Comic Sans".equals(currentFont)) {
//                scene.getStylesheets().remove(Objects.requireNonNull(getClass().getResource("monospace.css")).toExternalForm());
//                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("comic-sans.css")).toExternalForm());
//                currentFont = "Comic Sans";
//            }
//        });
//
//        monospace.setOnAction(e -> {
//            if (!"Monospace".equals(currentFont)) {
//                scene.getStylesheets().remove(Objects.requireNonNull(getClass().getResource("comic-sans.css")).toExternalForm());
//                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("monospace.css")).toExternalForm());
//                currentFont = "Monospace";
//            }
//        });
//
//        // Help Menu Actions
//        howToUse.setOnAction(e -> {
//            Alert howToUseDialog = new Alert(Alert.AlertType.INFORMATION);
//            howToUseDialog.setTitle("How to Use");
//            howToUseDialog.setHeaderText("User Guide");
//            howToUseDialog.setContentText("Follow the instructions to get started.\nFor further assistance, consult the FAQ.");
//            howToUseDialog.show();
//        });
//
//        faq.setOnAction(e -> {
//            Alert faqDialog = new Alert(Alert.AlertType.INFORMATION);
//            faqDialog.setTitle("FAQ");
//            faqDialog.setHeaderText("Frequently Asked Questions");
//            faqDialog.setContentText("Check here for answers to common questions.\nIf you need more help, contact support.");
//            faqDialog.show();
//        });

        return menuBar;
    }
}
