package edu.vanier.strawberries.controllers;

import edu.vanier.strawberries.ui.MainApp;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

import java.io.File;

public class SignUpController {
    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private Button SignUpBtn;
    @FXML
    private ToggleButton oldUserBtn;
    @FXML
    private Label statusLabel;

    private static final File USER_INFO = new File("src/main/resources/users.json");

    @FXML
    public void initialize() {
        //styleButton(logInBtn);

        oldUserBtn.setOnAction(this::loadSingOnScene);
        SignUpBtn.setOnAction(_-> signUp());
    }

    /**
     * Collect user information and create an account
     */
    private void signUp() {
        String username = this.username.getText().trim();
        String password = this.password.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Username and password cannot be empty.");
            return;
        }

        try {
            String hashedPassword = hashSHA256(password);
            String recoveryCode = generateRecoveryCode();

            // Read existing file
            String content = "";
            if (USER_INFO.exists()) {
                content = readFile(USER_INFO);
                if (userExists(content, username)) {
                    statusLabel.setText("Username already exists.");
                    return;
                }
            }

            String newUserBlock = String.format( // json format
                    """
                        {
                          "username": "%s",
                          "password": "%s",
                          "recoveryCode": "%s"
                        }\
                    """,
                    username, hashedPassword, recoveryCode
            );

            String updatedContent;

            if (content.isEmpty()) {
                updatedContent = "{\n  \"users\": [\n" + newUserBlock + "\n  ]\n}";
            } else {
                // new user in JSON
                int insertPosition = content.lastIndexOf("]");
                if (content.contains("\"users\": [") && content.contains("{")) {
                    // not first user
                    updatedContent = content.substring(0, insertPosition) +
                            ",\n" + newUserBlock + "\n" +
                            content.substring(insertPosition);
                } else {
                    // first user
                    updatedContent = "{\n  \"users\": [\n" + newUserBlock + "\n  ]\n}";
                }
            }

            try (FileWriter writer = new FileWriter(USER_INFO)) { // write to json file
                writer.write(updatedContent);
            }

            statusLabel.setText("Account created! Recovery code: " + recoveryCode); // print recovery code

        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            System.out.println("Error: " + e.getMessage());
        }

    }

    /**
     * Verifies if the user exists in the records
     * @param content The records of existing accounts
     * @param username The username to create
     * @return True if an account under that username already exists
     */
    private boolean userExists(String content, String username) { // check if user exists
        return content.contains("\"username\": \"" + username + "\"");
    }

    /**
     * Encrypts the user's passwords
     * @param input The original password
     * @return The encrypted password
     */
    private String hashSHA256(String input) throws Exception {
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

    /**
     * Generate a recovery code for the user, in case of a forgotten password
     * @return The recovery code
     */
    private String generateRecoveryCode() {
        SecureRandom random = new SecureRandom();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder(5);
        for (int i = 0; i < 5; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }
        return code.toString();
    }

    /**
     * Parse a given JSON file
     * @param file The JSON file to parse
     * @return a string value for the user information
     */
    private String readFile(File file) throws IOException { //read json
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    private void loadSingOnScene(Event e) {
        MainApp.switchScene(MainApp.LOGIN_SCENE);
    }
}
