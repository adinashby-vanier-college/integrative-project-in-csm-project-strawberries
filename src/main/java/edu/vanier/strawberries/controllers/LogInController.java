package edu.vanier.strawberries.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import java.util.Objects;

import static java.awt.SystemColor.window;

public class LogInController {
    @FXML
    private TextField username, password;
    private Button logInBtn, signInBtn;

    private final static Logger logger = LoggerFactory.getLogger(StartScreenFXMLController.class);

    @FXML
    public void initialize() {
        logger.info("Initializing LogInController...");

        styleButton(logInBtn);
        styleButton(signInBtn);
        //initUI();
        //window.getStylesheets().clear();
        //window.getStylesheets().add(Objects.requireNonNull(getClass().getResource("light-mode.css")).toExternalForm());
    }

    private void styleButton(Button btn) {
        btn.setStyle("""
        -fx-background-color: linear-gradient(to bottom, #f4f4f4, #e8e8e8);
        -fx-text-fill: #222222;
        -fx-background-radius: 6;
        -fx-border-radius: 6;
        -fx-border-color: #cccccc;
        -fx-border-width: 1px;
        -fx-cursor: hand;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 1, 0.0, 0, 1);
    """);
    }

    private void encryptPassword(String password) {
        // uses Bouncy Castle
        System.out.println("Generate a 32 byte long encryption key with Argon2id");
    }
}
