package edu.vanier.strawberries.controllers;

import edu.vanier.strawberries.Models.*;
import edu.vanier.strawberries.Models.DrawingArea;
import edu.vanier.strawberries.ui.MainApp;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

/**
 * FXML controller class for the primary stage scene.
 *
 * @author frostybee
 */
public class MainAppFXMLController {

    private final static Logger logger = LoggerFactory.getLogger(MainAppFXMLController.class);

    public boolean animationRunning;
    public Component selection;
    public Circuit circuit;

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
    Canvas canvas;
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
    Text mouseText;
    @FXML
    MenuItem menuNew, menuOpen, menuSave, menuSaveAs, menuQuit, menuPreferences, menuShowToolbar, menuHideToolbar, menuThemes, menuFitToScreen, menuZoomIn, menuZoomOut, menuToggleGrid,
    menuSelect;
    @FXML
    MenuItem lightThemeItem, darkThemeItem, strawThemeItem;
    private boolean isRunning = false;
    private DrawingTool drawingTool;
    public DrawingArea drawingArea;

    @FXML
    public void initialize() {
        logger.info("Initializing MainAppController...");

        animationRunning = false;
        initUI();
        setUpKeyListeners();
        applyTheme("light-mode.css");
        lightThemeItem.setOnAction(e -> applyTheme("light-mode.css"));
        darkThemeItem.setOnAction(e -> applyTheme("dark-mode.css"));
        strawThemeItem.setOnAction(e -> applyTheme("strawberries-theme.css"));

        circuit = new Circuit();

        // SET UP EVENT LISTENERS
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, this::mousePressed);
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::mouseDragged);
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, this::mouseReleased);
    }

    public void update() {
        if(drawingArea.circuit==null) drawingArea.setCircuit(circuit);
        drawingArea.drawContent();
    }

    private void initUI() {
// 1. BIND DIMENSIONS:
    // Heights
        window.prefHeightProperty().bind(MainApp.stage.heightProperty());
        splitPane.prefHeightProperty().bind(window.heightProperty());
        leftPanel.prefHeightProperty().bind(splitPane.heightProperty());
        canvas.heightProperty().bind(leftPanel.prefHeightProperty());
        //TODO figure out why it doesn't work without this...
//        canvas.prefHeightProperty().addListener(_-> canvas.setMinHeight(canvas.getPrefHeight()));
        rightPanel.prefHeightProperty().bind(splitPane.heightProperty());
        toolbarHBox.setPrefHeight(toolbarHBox.getChildren().getFirst().getLayoutBounds().getHeight());
        toolbarScrollPane.prefViewportHeightProperty().bind(toolbarHBox.heightProperty());
    //Widths
        leftPanelVBox.prefWidthProperty().bind(leftPanel.prefWidthProperty());
        toolbarScrollPane.prefWidthProperty().bind(leftPanelVBox.prefWidthProperty());
        toolbarHBox.prefWidthProperty().bind(toolbarScrollPane.prefWidthProperty());
        canvas.widthProperty().bind(leftPanel.prefWidthProperty());

// 2. OTHER FORMATTING
        toolbarScrollPane.widthProperty().addListener(_-> {
            //TODO make that little space under the buttons disappear when there is no scrollbar...
            //TODO make buttons stretch if they have extra space to do so.
        });

        leftPanel.widthProperty().addListener(_-> leftPanel.setPrefWidth(leftPanel.getWidth()));
// 3. INITIALIZE CLASSES
        // Linking to existing classes
        drawingArea = new DrawingArea(canvas);
        drawingArea.setCircuit(circuit);
        drawingTool = drawingArea.drawingTool;
        edu.vanier.strawberries.MenuBar myMenu = new edu.vanier.strawberries.MenuBar(menuBar);


// 4. SET UP UI ELEMENTS
        // Set button actions
        zoomInBtn.setOnAction(_-> {drawingArea.zoomIn();});
        zoomOutBtn.setOnAction(_-> {drawingArea.zoomOut();});
        addWireBtn.setOnAction(_-> drawingTool.setCurrentAction("place-wire"));
        addResistorBtn.setOnAction(_->drawingTool.setCurrentAction("place-resistor"));
        addBatteryBtn.setOnAction(_->drawingTool.setCurrentAction("place-battery"));
        addCapacitorBtn.setOnAction(_->drawingTool.setCurrentAction("place-capacitor"));
        addSwitchBtn.setOnAction(_->drawingTool.setCurrentAction("place-switch"));
        clearBtn.setOnAction(_-> {
            drawingArea.circuit.print();
            drawingArea.circuit.clear();
            drawingArea.canvas.getGraphicsContext2D().clearRect(0,0,canvas.getWidth(), canvas.getHeight());
            System.out.println("Cleared!");
        });

        defaultWireColorPicker.setValue(Color.BLACK);
        defaultWireColorPicker.setOnAction(_ -> {
            Color pickedColor = defaultWireColorPicker.getValue();
            if(pickedColor==null) pickedColor = Color.BLACK;
            drawingTool.defaultColor = pickedColor;
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
            if (animationRunning) {
                runStopBtn.setText("Stop");


                edu.vanier.math.CircuitMath math = new edu.vanier.math.CircuitMath(drawingArea.circuit);
                math.assignValuesToComponents();

                drawingArea.animateCurrentFlow(true);
            } else {
                runStopBtn.setText("Run");
                drawingArea.animateCurrentFlow(false);
            }
        });
    }

    private void mousePressed(MouseEvent e) {
        if(!Objects.equals(drawingTool.getCurrentAction(),"")) {
            drawingTool.setPencilDown(true);
            Node eventLocation = new Node(drawingArea.snap(e.getX()), drawingArea.snap(e.getY()));
            Node tempEnd = Node.copyOf(eventLocation);
            switch (drawingTool.getCurrentAction()) {
                case "place-wire" -> selection = new Wire(eventLocation, tempEnd, drawingTool.defaultColor, 0, 0);
                case "place-battery" -> selection = new Battery(eventLocation, tempEnd, 12);
                case "place-capacitor" -> selection = new Capacitor(eventLocation, tempEnd, 0, true, false);
                case "place-fuse" -> selection = new Fuse(eventLocation, tempEnd);
                case "place-lightbulb" -> selection = new Lightbulb(eventLocation, tempEnd);
                case "place-resistor" -> selection = new Resistor(eventLocation, tempEnd, 100);
                case "place-switch" -> selection = new Switch(eventLocation, tempEnd, false);
                case "select" -> {
                    circuit.unselectAll();
                    Point2D clickedAt = new Point2D(e.getX(), e.getY());
                    for (LinkedList<Component> list : circuit.arrayList) {
                        for (Component current : list) {
                            if (current.intersects(e.getX(), e.getY(), 100, 100)) {
                                selection = current;
                                current.markAsSelected(true);
                            }
                        }
                    }
                }
                default -> {}
            }
            if (!Objects.equals(drawingTool.getCurrentAction(), "select")) {
                circuit.addComponent(selection);
            }
        }
    }

    private void mouseDragged(MouseEvent e) {
        if (drawingTool.isPencilDown() && selection != null) {
            double nearestX = drawingArea.snap(e.getX());
            double nearestY = drawingArea.snap(e.getY());
            selection.moveNode(selection.end, nearestX, nearestY);
        }
        if(Objects.equals(drawingTool.getCurrentAction(), "select")) {
            //TODO add modifying a node after it's been drawn

        }
    }

    private void mouseReleased(MouseEvent e) {
        if (selection != null) {
            if (drawingTool.isPencilDown()) {
                drawingTool.setPencilDown(false);
                attemptConnection(selection, selection.end);

                // Enable dragging and rotating if it's draggable
                if (selection instanceof Battery battery) {
                    battery.enableDragAndRotate();
                } else if (selection instanceof Switch sw) {
                    sw.enableDragAndRotate(); // Do the same for others
                }
                selection = null;
            }
        }
    }

    private void attemptConnection(Component toCheck, Node node) {
        int srcIndex = circuit.getIndex(toCheck);
        Point2D checkPoint = new Point2D(node.getX(), node.getY());
        ArrayList<Node> connectedNodes = new ArrayList<>();
        connectedNodes.add(node);

        for (LinkedList<Component> currentList : circuit.arrayList) {
            for (Component connectedComponent : currentList) {
                int dstIndex = circuit.getIndex(connectedComponent);

                Point2D componentBegin = new Point2D(connectedComponent.begin.getX(), connectedComponent.begin.getY());
                Point2D componentEnd = new Point2D(connectedComponent.end.getX(), connectedComponent.end.getY());

                if (componentBegin.distance(checkPoint) == 0) {
                    connectedNodes.add(connectedComponent.begin);
                }
                if (componentEnd.distance(checkPoint) == 0) {
                    connectedNodes.add(connectedComponent.end);
                }

                for (int i = 1; i < connectedNodes.size(); i++) {
                    if (!circuit.checkEdge(srcIndex, dstIndex)) circuit.addEdge(srcIndex, dstIndex);
                }
                connectedComponent.draw();
            }
        }

        toCheck.draw();
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

    @FXML
    private void menuSelectBtnPressed() {
        System.out.println("select pressed");
        drawingTool.setCurrentAction("select");
    }

    private void setUpKeyListeners() {
        window.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            switch(event.getCode()) {
                case S,ESCAPE -> {
                    drawingTool.setCurrentAction("select");
                    window.setCursor(Cursor.OPEN_HAND);
                }
                case W -> {
                    drawingTool.setCurrentAction("place-wire");
                }
                case DELETE,BACK_SPACE -> { //TODO deletion not working
                    System.out.println("deleted");
                    circuit.deleteComponent(selection);
                    //delete selected element
                }
                case COMMA -> {
                    System.out.println("ROTATING LEFT");
                    //TODO rotate selected element 90 deg left
                }
                case PERIOD -> {
                    System.out.println("ROTATING RIGHT");
                    //TODO rotate selected element 90 deg right
                }
            }
            System.out.println(event.getCode());
        });
    }

    public void setCursor(Cursor cursor) {
        window.setCursor(cursor);
    }

    private void applyTheme(String cssFile) {
        window.getStylesheets().clear();
        window.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/" + cssFile)).toExternalForm());
    }

}
