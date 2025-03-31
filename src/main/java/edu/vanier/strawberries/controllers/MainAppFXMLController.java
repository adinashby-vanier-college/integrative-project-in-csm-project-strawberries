package edu.vanier.strawberries.controllers;

import edu.vanier.strawberries.DrawingArea;
import edu.vanier.strawberries.DrawingTool;
import edu.vanier.strawberries.ui.MainApp;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
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

    public boolean animationRunning;

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
    Button exportBtn,moreInformationBtn,runStopBtn,resetBtn,clearBtn;
    @FXML
    Label runStopLabel;
    @FXML
    MenuButton viewMenuBtn;
    @FXML
    ColorPicker defaultWireColorPicker;
    @FXML
    CheckBox polarityCheckBox;
    @FXML
    MenuBar menuBar;
    @FXML
    MenuItem menuNew, menuOpen, menuSave, menuSaveAs, menuQuit, menuPreferences, menuShowToolbar, menuHideToolbar, menuThemes, menuFitToScreen, menuZoomIn, menuZoomOut, menuToggleGrid;
    private boolean isRunning = false;
    private double zoomScale = 1.0;

    @FXML
    public void initialize() {
        logger.info("Initializing MainAppController...");

        drawingAreaPane.setStyle("-fx-background-color: #fff");
        animationRunning = false;
        initUI();
    }

    private void initUI() {
// 1. BIND DIMENSIONS:
    // Heights
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

// 2. OTHER FORMATTING
        toolbarScrollPane.widthProperty().addListener(_-> {
            //TODO make that little space under the buttons disappear when there is no scrollbar...
            //TODO make buttons stretch if they have extra space to do so.
        });

        leftPanel.widthProperty().addListener(_-> {
            leftPanel.setPrefWidth(leftPanel.getWidth());
        });
// 3. INITIALIZE CLASSES
        // Linking to existing classes
        DrawingArea drawingArea = new DrawingArea(drawingAreaPane);
        DrawingTool drawingTool = drawingArea.drawingTool;
        edu.vanier.strawberries.MenuBar myMenu = new edu.vanier.strawberries.MenuBar(menuBar); //TODO fix this... feels like there should be an easier way


// 4. SET UP UI ELEMENTS
        // Set button actions
        addWireBtn.setOnAction(_-> {drawingTool.setCurrentAction("place-wire");});
        addResistorBtn.setOnAction(_->drawingTool.setCurrentAction("place-resistor"));
        addBatteryBtn.setOnAction(_->drawingTool.setCurrentAction("place-battery"));
        addCapacitorBtn.setOnAction(_->drawingTool.setCurrentAction("place-capacitor"));
        addSwitchBtn.setOnAction(_->drawingTool.setCurrentAction("place-switch"));

        defaultWireColorPicker.setValue(Color.BLACK);
        defaultWireColorPicker.setOnAction(_ -> {
            Color pickedColor = defaultWireColorPicker.getValue();
            if(pickedColor==null) pickedColor = Color.BLACK;
            drawingTool.setColor(pickedColor);
        });
        polarityCheckBox.setOnAction(_-> {
            if (polarityCheckBox.isSelected()) {
                System.out.println("Clicked");
            } else {
                System.out.println("Un-clicked");
            }
        });

        moreInformationBtn.setOnAction(_ -> {
            Stage codeStage = new Stage();
            codeStage.setTitle("More information");
            codeStage.setHeight(500);
            codeStage.setWidth(600);

            // Create text display
            TextFlow infoText = new TextFlow(
                    new Text("Total Resistance:\nBranch Voltage:\nBranch Current:\n")
            );
            infoText.setTextAlignment(TextAlignment.LEFT);

            // Graph title
            Label graphTitle = new Label("Kirchhoff's Loop Rule Graph");
            graphTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            // Graph
            final LineChart<Number, Number> lineChart = getChart();

            // Close button
            Button closeButton = new Button("Close");
            closeButton.setOnAction(_ -> codeStage.close());

            // VBox layout
            VBox content = new VBox(10, infoText, graphTitle, lineChart, closeButton);
            content.setPadding(new Insets(10));
            content.setAlignment(Pos.CENTER);

            Scene scene = new Scene(content, 600, 500);
            codeStage.setScene(scene);
            codeStage.show();

            // Animation: the real thing coming soon

            VBox bottomContainer = new VBox(closeButton);
            bottomContainer.setAlignment(Pos.CENTER);
            bottomContainer.setSpacing(10);
            content.getChildren().add(bottomContainer);
            codeStage.show();
        });

        runStopBtn.setText("Run");
        runStopBtn.setOnAction(_ -> {
            animationRunning = !animationRunning;
            if(animationRunning) runStopBtn.setText("Stop");
            else runStopBtn.setText("Run");
        });

    }

    private static LineChart<Number, Number> getChart() {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Loop Position");
        yAxis.setLabel("Potential Difference (V)");
        xAxis.setTickLabelsVisible(false);
        xAxis.setTickMarkVisible(false);
        xAxis.setMinorTickVisible(false);

        // Creating the chart
        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Potential Difference in Kirchhoff's Loop");
        lineChart.setLegendVisible(false);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        series.getData().add(new XYChart.Data<>(0, 0));   // Start at 0V
        series.getData().add(new XYChart.Data<>(1, 10));  // Increase
        series.getData().add(new XYChart.Data<>(2, 10));  // Stay constant
        series.getData().add(new XYChart.Data<>(3, 5));   // Decrease
        series.getData().add(new XYChart.Data<>(4, 5));   // Stay constant
        series.getData().add(new XYChart.Data<>(5, 0));   // Return to 0V

        lineChart.getData().add(series);
        return lineChart;
    }

}
