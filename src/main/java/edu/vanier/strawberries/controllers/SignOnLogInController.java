package edu.vanier.strawberries.controllers;


import edu.vanier.strawberries.ui.MainApp;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class SignOnLogInController {
    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private Button signInBtn;
    @FXML
    private ToggleButton newUserBtn;
    @FXML
    private Label statusLabel;

    private static final File USER_INFO = new File("src/main/resources/users.json");

    @FXML
    public void initialize() {
        //styleButton(signInBtn);
        newUserBtn.setOnAction(this::loadSingUpScene);
        signInBtn.setOnAction(_-> {
            String recentProject = Login();
            System.out.println(recentProject);
            // send recentProject to Main Class
        });
    }

    public String getRecent() {
        return Login();
    }

    private String Login() {
        String recentProject = "";
        String username = this.username.getText().trim();
        String password = this.password.getText();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both username and password.");
            return "";
        }

        if (!USER_INFO.exists()) {
            statusLabel.setText("No users registered.");
            return "";
        }

        try { // main, call methods below
            String content = readFile(USER_INFO);
            String hashedInputPassword = hashSHA256(password);

            if (findCheckUser(content, username, hashedInputPassword)) {
                statusLabel.setText("Please enter both username and password.");
                recentProject = findRecent(content, username);
                loadMainScene();
                return recentProject;
            } else {
                statusLabel.setText("Incorrect username or password.");
            }

        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return recentProject;
    }

    private boolean findCheckUser(String content, String username, String hashedPassword) { // check existing user
        int index = 0;
        while ((index = content.indexOf("\"username\":", index)) != -1) {
            int startUser = content.indexOf("\"", index + 11) + 1;
            int endUser = content.indexOf("\"", startUser);
            String foundUsername = content.substring(startUser, endUser);

            if (foundUsername.equals(username)) {
                int passIndex = content.indexOf("\"password\":", endUser);
                int startPass = content.indexOf("\"", passIndex + 11) + 1;
                int endPass = content.indexOf("\"", startPass);
                String storedPassword = content.substring(startPass, endPass);
                return storedPassword.equals(hashedPassword);
            }
            index = endUser;
        }
        return false;
    }

    private static String findRecent(String content, String username) { // return recent project
        int index = 0;
        while ((index = content.indexOf("\"username\":", index)) != -1) {
            int startUser = content.indexOf("\"", index + 11) + 1;
            int endUser = content.indexOf("\"", startUser);
            String foundUsername = content.substring(startUser, endUser);

            if (foundUsername.equals(username)) {
                int recentIndex = content.indexOf("\"recent\":", endUser);
                int startRecent = content.indexOf("\"", recentIndex + 9) +1;
                int endRecent = content.indexOf("\"", startRecent);
                String recentProject = content.substring(startRecent, endRecent);
                return recentProject;
            }
            index = endUser;
        }
        return "";
    }

    public String hashSHA256(String input) throws Exception { // ecrypt inputted password
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
        for (byte b : encodedHash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private String readFile(File file) throws IOException { // json format parser
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    private void loadSingUpScene(Event e) {
        MainApp.switchScene(MainApp.SIGNUP_SCENE);
    }

    private void loadMainScene() {
        MainApp.switchScene(MainApp.MAINAPP_SCENE);
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
}
