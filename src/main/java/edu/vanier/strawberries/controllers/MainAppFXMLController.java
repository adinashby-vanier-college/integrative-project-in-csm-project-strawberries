package edu.vanier.strawberries.controllers;

import edu.vanier.strawberries.ui.MainApp;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FXML controller class for the primary stage scene.
 *
 * @author frostybee
 */
public class MainAppFXMLController {

    private final static Logger logger = LoggerFactory.getLogger(MainAppFXMLController.class);

    //Import FXML variables
    @FXML
    VBox window,leftPanelVBox,rightPanelVBox;
    @FXML
    SplitPane splitPane;
    @FXML
    ScrollPane toolbarScrollPane;
    @FXML
    HBox toolbarHBox;
    @FXML
    Button zoomInBtn,zoomOutBtn,undoBtn,redoBtn,copyBtn,pasteBtn,addWireBtn,addResistorBtn,addCapacitorBtn,addBatteryBtn,addSwitchBtn;
    @FXML
    Pane drawingAreaPane;
    @FXML
    AnchorPane leftPanel, rightPanel;
    @FXML
    TextField circuitNameField;
    @FXML
    Button exportBtn,morInformationBtn,runStopBtn,resetBtn,clearBtn;
    @FXML
    MenuButton viewMenuBtn;
    @FXML
    ColorPicker defaultWireColorPicker;
    @FXML
    CheckBox polarityCheckBox;

    @FXML
    public void initialize() {
        logger.info("Initializing MainAppController...");
        bind();
    }



    private void bind() {
        System.out.println("Binding heights");
        window.prefHeightProperty().bind(MainApp.stage.heightProperty());
        splitPane.prefHeightProperty().bind(window.heightProperty());
        leftPanel.prefHeightProperty().bind(splitPane.heightProperty());
        rightPanel.prefHeightProperty().bind(splitPane.heightProperty());
        System.out.println("Binding widths");
        leftPanelVBox.prefWidthProperty().bind(leftPanel.prefWidthProperty());
        toolbarScrollPane.prefWidthProperty().bind(leftPanelVBox.prefWidthProperty());

    }

}
