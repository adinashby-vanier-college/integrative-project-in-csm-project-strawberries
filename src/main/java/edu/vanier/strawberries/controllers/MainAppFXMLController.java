package edu.vanier.strawberries.controllers;

import edu.vanier.strawberries.DrawingArea;
import edu.vanier.strawberries.DrawingTool;
import edu.vanier.strawberries.MenuBar;
import edu.vanier.strawberries.ui.MainApp;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

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

        drawingAreaPane.setStyle("-fx-background-color: #fff");
        bind();

        // Linking to existing classes
        DrawingArea drawingArea = new DrawingArea(drawingAreaPane);
        DrawingTool drawingTool = drawingArea.drawingTool;

        // Set button actions
        addWireBtn.setOnAction(_-> {drawingTool.setCurrentAction("place-wire");});
        addResistorBtn.setOnAction(_->drawingTool.setCurrentAction("place-resistor"));
        addBatteryBtn.setOnAction(_->drawingTool.setCurrentAction("place-battery"));
        addCapacitorBtn.setOnAction(_->drawingTool.setCurrentAction("place-capacitor"));
        addSwitchBtn.setOnAction(_->drawingTool.setCurrentAction("place-switch"));

    }

    private void bind() {
        //Heights
        window.prefHeightProperty().bind(MainApp.stage.heightProperty());
        splitPane.prefHeightProperty().bind(window.heightProperty());
        leftPanel.prefHeightProperty().bind(splitPane.heightProperty());
        drawingAreaPane.prefHeightProperty().bind(leftPanel.prefHeightProperty());
        //TODO figure out why it doesn't work without this...
        drawingAreaPane.prefHeightProperty().addListener(_-> {
            drawingAreaPane.setMinHeight(drawingAreaPane.getPrefHeight());
        });
        rightPanel.prefHeightProperty().bind(splitPane.heightProperty());
        toolbarHBox.setPrefHeight(toolbarHBox.getChildren().getFirst().getLayoutBounds().getHeight());
        toolbarScrollPane.prefViewportHeightProperty().bind(toolbarHBox.heightProperty());
        //Widths
        leftPanelVBox.prefWidthProperty().bind(leftPanel.prefWidthProperty());
        toolbarScrollPane.prefWidthProperty().bind(leftPanelVBox.prefWidthProperty());
        toolbarHBox.prefWidthProperty().bind(toolbarScrollPane.prefWidthProperty());

        //Other Formatting
        toolbarScrollPane.widthProperty().addListener(_-> {
            //TODO make that little space under the buttons disappear when there is no scrollbar...
            //TODO make buttons stretch if they have extra space to do so.
        });

        leftPanel.widthProperty().addListener(_-> {
            leftPanel.setPrefWidth(leftPanel.getWidth());
        });
    }

}
