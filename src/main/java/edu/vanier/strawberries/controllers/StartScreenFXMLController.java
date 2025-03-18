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
public class StartScreenFXMLController {

    @FXML
    Button newCircuitBtn = new Button();

    private final static Logger logger = LoggerFactory.getLogger(StartScreenFXMLController.class);

    @FXML
    public void initialize() {
        logger.info("Initializing StartScreenController...");
        newCircuitBtn.setOnAction(this::loadPrimaryScene);
    }

    private void loadPrimaryScene(Event e) {
        MainApp.switchScene(MainApp.MAINAPP_SCENE);
        logger.info("Loaded the main scene...");
    }
}
