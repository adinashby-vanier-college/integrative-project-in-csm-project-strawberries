package com.example.fractal;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import java.util.Objects;

public class MenuBar {
    private String currentTheme;
    private Scene scene;
    private String currentFont;

    public MenuBar(Scene scene) {
        this.scene = scene;
        this.currentTheme = "light"; // Default theme
        this.currentFont = "Monospace";
    }

    public javafx.scene.control.MenuBar make() {
        javafx.scene.control.MenuBar menuBar = new javafx.scene.control.MenuBar();

        Menu file = new Menu("File");
        MenuItem quit = new MenuItem("Quit");
        quit.setOnAction(e -> System.exit(0));
        MenuItem newWindow = new MenuItem("New Window");
        newWindow.setOnAction(e -> (new Main()).start(new Stage()));
        file.getItems().addAll(newWindow, quit);

        Menu options = new Menu("Options");
        Menu font = new Menu("Font");
        MenuItem comicSans = new MenuItem("Comic Sans");
        MenuItem monospace = new MenuItem("Monospace");
        Menu theme = new Menu("Change Theme");
        MenuItem light = new MenuItem("Light");
        MenuItem dark = new MenuItem("Dark");

        // LIGHT THEME
        light.setOnAction(e -> {
            if (!"light".equals(currentTheme)) { // Only switch if the current theme isn't light
                scene.getStylesheets().remove(Objects.requireNonNull(getClass().getResource("dark-mode.css")).toExternalForm()); // Remove dark theme
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("light-mode.css")).toExternalForm()); // Add light theme
                currentTheme = "light"; // Update the current theme
            }
        });

        // DARK THEME
        dark.setOnAction(e -> {
            if (!"dark".equals(currentTheme)) { // Only switch if the current theme isn't dark
                scene.getStylesheets().remove(Objects.requireNonNull(getClass().getResource("light-mode.css")).toExternalForm()); // Remove light theme
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("dark-mode.css")).toExternalForm()); // Add dark theme
                currentTheme = "dark"; // Update the current theme
            }
        });

        // COMIC SANS FONT
        comicSans.setOnAction(e -> {
            if (!"Comic Sans".equals(currentFont)) {
                scene.getStylesheets().remove(Objects.requireNonNull(getClass().getResource("monospace.css")).toExternalForm());
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("comic-sans.css")).toExternalForm());
                currentFont = "Comic Sans"; // Update the current theme
            }
        });

        // MONOSPACE FONT
        monospace.setOnAction(e -> {
            if (!"Monospace".equals(currentFont)) {
                scene.getStylesheets().remove(Objects.requireNonNull(getClass().getResource("comic-sans.css")).toExternalForm());
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("monospace.css")).toExternalForm());
                currentFont = "Monospace";
            }
        });

        theme.getItems().addAll(light, dark);
        font.getItems().addAll(comicSans, monospace);

        options.getItems().addAll(theme, font);

        // FEEDBACK IN MENU
        Menu help = new Menu("Help");
        MenuItem sendFeedback = new MenuItem("Send Feedback");
        sendFeedback.setOnAction(e -> {
            Alert codeDialog = new Alert(Alert.AlertType.INFORMATION);
            codeDialog.setTitle("Send Feedback");
            codeDialog.setHeight(350);
            codeDialog.setWidth(500);
            codeDialog.setHeaderText("We sincerely apologize.");
            codeDialog.setContentText(
                    "We currently do not accept feedback."
            );
            codeDialog.show();
        });

        // MENU GUIDE
        MenuItem guide = new MenuItem("Guide");
        guide.setOnAction(e -> {
            Alert codeDialog = new Alert(Alert.AlertType.INFORMATION);
            codeDialog.setTitle("Simple Guide");
            codeDialog.setHeight(350);
            codeDialog.setWidth(500);
            codeDialog.setHeaderText("Lorem Ipsum");
            codeDialog.setContentText(
                    "Hi guys\n"
            );
            codeDialog.show();
        });
        help.getItems().addAll(guide, sendFeedback);

        menuBar.getMenus().addAll(file, options, help);
        return menuBar;
    }
}
