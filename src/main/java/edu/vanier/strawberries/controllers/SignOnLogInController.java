package edu.vanier.strawberries.controllers;

import edu.vanier.strawberries.ui.MainApp;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FXML controller class for the secondary scene.
 *
 * @author frostybee
 */
public class SignOnLogInController {

    @FXML
    Button newCircuitBtn = new Button();
    @FXML
    Button savedCircuitsBtn = new Button();
    @FXML
    Button logInBtn = new Button();

    private final static Logger logger = LoggerFactory.getLogger(StartScreenFXMLController.class);

    @FXML
    public void initialize() {
        logger.info("Initializing StartScreenController...");

        styleButton(newCircuitBtn);
        styleButton(savedCircuitsBtn);
        styleButton(logInBtn);

        newCircuitBtn.setOnAction(this::loadPrimaryScene);
    }

    private void loadPrimaryScene(Event e) {
        MainApp.switchScene(MainApp.MAINAPP_SCENE);
        logger.info("Loaded the main scene...");
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
