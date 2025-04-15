package edu.vanier.strawberries.controllers;

import edu.vanier.strawberries.Models.*;
import edu.vanier.strawberries.Models.DrawingArea;
import edu.vanier.strawberries.ui.MainApp;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
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

    //git commit -m "cleaned up some leftover code from the migration ImageView -> Canvas + polished selection logic"

    private final static Logger logger = LoggerFactory.getLogger(MainAppFXMLController.class);

    public boolean animationRunning;
    public Component selection,editing;
    public Circuit circuit;
    private double posX,posY;
    private Node[] toMove = new Node[2];

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
        lightThemeItem.setOnAction(_ -> applyTheme("light-mode.css"));
        darkThemeItem.setOnAction(_-> applyTheme("dark-mode.css"));
        strawThemeItem.setOnAction(_-> applyTheme("strawberries-theme.css"));

        circuit = new Circuit();

        // SET UP EVENT LISTENERS
        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, this::mouseMoved);
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, this::mousePressed);
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::mouseDragged);
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, this::mouseReleased);

        if(drawingArea.circuit==null) drawingArea.setCircuit(circuit);
    }

    public void update() {
        Point2D mouseAt = new Point2D(posX,posY);
        if(editing == selection && drawingTool.getCurrentAction().equals("select")) {
            for (LinkedList<Component> list : circuit.arrayList) {
                for (Component current : list) {
                    if (current instanceof Wire wire && checkLineCollision(mouseAt, wire)) {
                        select(current);
                    }
                    else if (!(current instanceof Wire) && checkImageCollision(mouseAt, current)) {
                        select(current);
                    }
                    else {
                        unselect(current);
                    }
                }
            }
        }

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
        zoomInBtn.setOnAction(_-> drawingArea.zoomIn());
        zoomOutBtn.setOnAction(_-> drawingArea.zoomOut());
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

    private void mouseMoved(MouseEvent e) {
        posX = e.getX();
        posY = e.getY();
        if(drawingTool.getCurrentAction().equals("select")) {
            if (selection != null) setCursor(Cursor.OPEN_HAND);
            else setCursor(Cursor.DEFAULT);
        }
    }

    private void mousePressed(MouseEvent e) {
        if(!Objects.equals(drawingTool.getCurrentAction(),"")) {
            drawingTool.setPencilDown(true);
            Node eventLocation = new Node(drawingArea.snap(e.getX()), drawingArea.snap(e.getY()));
            Node tempEnd = Node.copyOf(eventLocation);
            switch (drawingTool.getCurrentAction()) {
                case "place-wire" -> select(new Wire(eventLocation, tempEnd, drawingTool.defaultColor, 0, 0));
                case "place-battery" -> select(new Battery(eventLocation, tempEnd, 12));
                case "place-capacitor" -> select(new Capacitor(eventLocation, tempEnd, 0, true, false));
                case "place-fuse" -> select(new Fuse(eventLocation, tempEnd));
                case "place-lightbulb" -> select(new Lightbulb(eventLocation, tempEnd));
                case "place-resistor" -> select(new Resistor(eventLocation, tempEnd, 10));
                case "place-switch" -> select(new Switch(eventLocation, tempEnd, false));
     case "select" -> {
    setCursor(Cursor.CLOSED_HAND);
    edit(selection);
    if (selection instanceof Battery battery) {
       battery.handleEdit(leftPanel);  

    }
}


                default -> {}
            }
            if (!Objects.equals(drawingTool.getCurrentAction(), "select")) {
                circuit.addComponent(selection);
            }
        }

        if(editing != null) {
            if(editing instanceof Wire wire) {
                //find which node to move
                Point2D begin = new Point2D(wire.begin.getX(), wire.begin.getY()),
                        end = new Point2D(wire.end.getX(), wire.end.getY());

                //1. Calculate the dist from the point to either node
                double d1 = begin.distance(e.getX(), e.getY());
                double d2 = end.distance(e.getX(), e.getY());
                double buffer = 0.4; // Accounts for uncertainty due to cursor size

                toMove = new Node[2]; //(re)initializing the array

                if (d1 + buffer <= 20) {
                    toMove[0] = wire.begin;
                } else if (d2 + buffer <= 20) {
                    toMove[0] = wire.end;
                } else {
                    toMove[0] = wire.begin;
                    toMove[1] = wire.end;
                }
            }
            else {
                toMove[0] = editing.begin;
                toMove[1] = null;
            }
        }
    }

    private boolean checkImageCollision(Point2D source, Component component) {
        double minX = Math.min(component.begin.getX(),component.end.getX()),
               minY = Math.min(component.begin.getY(),component.end.getY());
        return (source.getX() <= minX+component.display.getWidth() && source.getY() <= minY+component.display.getHeight());
    }

    private void mouseDragged(MouseEvent e) {
        if (drawingTool.isPencilDown() && selection != null) {
            double nearestX = drawingArea.snap(e.getX());
            double nearestY = drawingArea.snap(e.getY());
            selection.moveNode(selection.end, nearestX, nearestY);
        }

        //TODO testing
        if(editing != null) {
            setCursor(Cursor.CLOSED_HAND);
            //TODO add modifying a node after it's been drawn
            if(editing instanceof Wire wire) {
                for (Node node : toMove) {
                    if (node != null) {
                        node.setPosition(e.getX(), e.getY());
                    }
                }
            }
            else {
                Node node = toMove[0];
                node.setPosition(drawingArea.snap(e.getX()-editing.display.getWidth()/2),drawingArea.snap(e.getY()-editing.display.getHeight()/2));
            }
        }
    }

    private boolean checkLineCollision(Point2D source, Wire wire) {
        Point2D begin = new Point2D(wire.begin.getX(),wire.begin.getY()),
                end = new Point2D(wire.end.getX(),wire.end.getY());

        //1. Calculate the dist from the point to either node
        double d1 = source.distance(begin);
        double d2 = source.distance(end);

        //2. Verify that both add up to the length of the line wire
        double length = begin.distance(end);
        double buffer = 0.4; // Accounts for uncertainty due to cursor size

        return (d1+d2 >= length-buffer && d1+d2 <= length+buffer);
    }


    private void mouseReleased(MouseEvent e) {
        if (selection != null) {
            if (drawingTool.isPencilDown()) {
                drawingTool.setPencilDown(false);
                attemptConnection(selection, selection.end);
            }
            if(canvas.getCursor().equals(Cursor.CLOSED_HAND)) setCursor(Cursor.OPEN_HAND);
        }
    }

    private void select(Component component) {
        if(selection!=null) selection.markAsSelected(false);
        component.markAsSelected(true);
        selection = component;
    }

    private void unselect(Component component) {
        if(component != null && selection == component) {
            component.markAsSelected(false);
            selection = null;
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
            }
        }
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
        drawingTool.setCurrentAction("select");
    }

    private void setUpKeyListeners() {
        window.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            switch(event.getCode()) {
                case S,ESCAPE -> {
                    unselect(selection);
                    drawingTool.setCurrentAction("select");
                    setCursor(Cursor.DEFAULT);
                }
                case W -> drawingTool.setCurrentAction("place-wire");

                case DELETE,BACK_SPACE -> { //TODO deletion not working properly
                    circuit.deleteComponent(editing);
                    //delete selected element
                }
                case COMMA -> {
                    if(editing!=null) {
                        editing.rotate("left");
                    }
                }
                case PERIOD -> {
                    if(editing!=null) editing.rotate("right");
                }
            }
        });
    }

    public void setCursor(Cursor cursor) {
        canvas.setCursor(cursor);
    }

    private void applyTheme(String cssFile) {
        window.getStylesheets().clear();
        window.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/" + cssFile)).toExternalForm());
    }

  private void edit(Component component) {
    if (editing != component) {
        if (component != null) component.setEdit(true);
        if (editing != null) {
            if (component == null) {
                editing.setEdit(false);
                editing = null;
            } else {
                editing.setEdit(false);
                editing = component;
            }
        } else {
            editing = component;
        }

      if (component instanceof Battery battery) {
    battery.handleEdit(leftPanel); 
}

    }


}


}