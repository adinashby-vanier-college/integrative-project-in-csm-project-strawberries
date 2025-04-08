package edu.vanier.strawberries.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.Properties;

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

    private static String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] hash = md.digest(password.getBytes());
            return (Base64.getEncoder().encodeToString(hash));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //TODO: all of this below
    //TODO: fix log in scene

    private void displayInfo () {
        // parse through json file
        // display it in table view in scene for TESTING PURPOSES ONLY
        // call this function after the other functions below
    }

    private void logIn(String username,String password) {
        // parse through json file using gson: https://stackoverflow.com/questions/73418013/how-can-i-read-a-json-file-and-display-it-to-a-tableview-in-javafx
        // display it in scene for better view
        // check if input username is in json file: if yes, check password match by encrypting password and comparing
        // if not: print username does not exist
    }

    private void signIn(String username, String password) {
        // parse through json file
        //
    }
}
