package edu.vanier.strawberries.controllers;

import edu.vanier.math.CircuitMath;
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
import java.io.*;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import javax.swing.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * FXML controller class for the primary stage scene.
 *
 * @author frostybee
 */
public class MainAppFXMLController {

    private final static Logger logger = LoggerFactory.getLogger(MainAppFXMLController.class);
    public boolean animationRunning;
    public Component selection,editing;
    public Circuit circuit;
    private double posX,posY;
    private Node[] toMove = new Node[2];
    private Point2D mouseDownLocation,initialBegin,initialEnd;
    public boolean diagramView;
    private String currentTheme;

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
    ColorPicker defaultWireColorPicker, menuColorPicker, lightbulbColorPicker;
    @FXML
    CheckBox polarityCheckBox;
    @FXML
    MenuBar menuBar;
    @FXML
    Text circuitStateText;
    @FXML
    MenuItem menuNew, menuOpen, menuOpenRecent, menuSave, menuSaveAs, menuQuit, menuShowToolbar, menuHideToolbar, menuThemes,
            menuFitToScreen, menuZoomIn, menuZoomOut, menuToggleGrid, menuSelect, menuWire, menuRedWire, menuBlackWire,
            menuDefaultColorWire, menuChooseColorWire, menuResistor, menuSwitch, menuBattery, menuCapacitor, menuLightbulb,
            menuYellow, menuRed, menuGreen, menuBlue, menuColorLightbulb;
    @FXML
    MenuItem lightThemeItem, darkThemeItem, strawThemeItem;
    private DrawingTool drawingTool;
    public DrawingArea drawingArea;

    /**
     * Initializes the application before launching the window.
     */
    @FXML
    public void initialize() {
        logger.info("Initializing MainAppController...");

        animationRunning = false;
        diagramView = true;
        circuit = new Circuit(true);
        initUI();
        setUpKeyListeners();
        applyTheme("light-mode.css");

        // SET UP EVENT LISTENERS
        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, this::mouseMoved);
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, this::mousePressed);
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::mouseDragged);
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, this::mouseReleased);

        if(drawingArea.circuit == null) drawingArea.setCircuit(circuit);
    }

    /**
     * Method called at every frame. Checks on circuit state and updates values when necessary.
     */
    public void update() {
        Point2D mouseAt = new Point2D(posX, posY);
        // Update selection
        if (drawingTool.getCurrentAction().equals("select")) {
            for (LinkedList<Component> list : circuit.arrayList) {
                for (Component current : list) {
                    if (current instanceof Wire wire && checkLineCollision(mouseAt, wire)) {
                        select(current);
                    } else if (!(current instanceof Wire) && checkComponentCollision(mouseAt, current)) {
                        select(current);
                    } else {
                        unselect(current);
                    }
                }
            }
        }
        // electrons get animated
        drawingArea.updateAnimation();

        // Component specific states
        for (LinkedList<Component> list : circuit.arrayList) {
            for (Component component : list) {
                CircuitMath.setVoltageAcross(component);
                if (component instanceof Lightbulb lightbulb) {
                    System.out.println("lightbulb voltage: " + lightbulb.getVoltage());
                    if (lightbulb.getVoltage() >= lightbulb.getMinVoltage()) {
                        lightbulb.turnOn(true);
                    }
                }
            }
        }

        //Update circuit message
        if (circuit.arrayList.isEmpty()) {
            circuitStateText.setText("Empty circuit. A blank canvas!");
        } else if (circuit.isClosed()) {
            circuitStateText.setText("Closed circuit!");
        } else {
            circuitStateText.setText("Open circuit... No current :(");
        }

        // Draw everything
        drawingArea.drawContent();
    }

    /**
     * Initializing the functions of UI elements such as buttons, menus, etc. before launching.
     * @implNote This method should be called in the {@link #initialize()} method
     */
    private void initUI() {
// 1. BIND DIMENSIONS:
        // Heights
        window.prefHeightProperty().bind(MainApp.stage.heightProperty());
        splitPane.prefHeightProperty().bind(window.heightProperty());
        leftPanel.prefHeightProperty().bind(splitPane.heightProperty());
        canvas.heightProperty().bind(leftPanel.prefHeightProperty());
        rightPanel.prefHeightProperty().bind(splitPane.heightProperty());
        toolbarHBox.setPrefHeight(toolbarHBox.getChildren().getFirst().getLayoutBounds().getHeight());
        toolbarScrollPane.prefViewportHeightProperty().bind(toolbarHBox.heightProperty());
        //Widths
        leftPanelVBox.prefWidthProperty().bind(leftPanel.prefWidthProperty());
        toolbarScrollPane.prefWidthProperty().bind(leftPanelVBox.prefWidthProperty());
        toolbarHBox.prefWidthProperty().bind(toolbarScrollPane.prefWidthProperty());
        canvas.widthProperty().bind(leftPanel.prefWidthProperty());

// 2. OTHER FORMATTING
        toolbarScrollPane.widthProperty().addListener(_ -> {
            //TODO make that little space under the buttons disappear when there is no scrollbar...
        });
        leftPanel.widthProperty().addListener(_ -> leftPanel.setPrefWidth(leftPanel.getWidth()));
        window.widthProperty().addListener(_ -> splitPane.setDividerPosition(0, window.getWidth()));

// 3. INITIALIZE CLASSES
        // Linking to existing classes
        drawingArea = new DrawingArea(canvas);
        drawingArea.setCircuit(circuit);
        drawingTool = drawingArea.drawingTool;

// 4. SET UP UI ELEMENTS
        // SET BUTTON ACTIONS
        zoomInBtn.setOnAction(_ -> drawingArea.zoomIn());
        zoomOutBtn.setOnAction(_ -> drawingArea.zoomOut());
        addWireBtn.setOnAction(_ -> drawingTool.setCurrentAction("place-wire"));
        addResistorBtn.setOnAction(_ -> drawingTool.setCurrentAction("place-resistor"));
        addBatteryBtn.setOnAction(_ -> drawingTool.setCurrentAction("place-battery"));
        addCapacitorBtn.setOnAction(_ -> drawingTool.setCurrentAction("place-capacitor"));
        addSwitchBtn.setOnAction(_ -> drawingTool.setCurrentAction("place-switch"));

        // view
        switchView(true);
        MenuItem diagramItem = viewMenuBtn.getItems().get(0);
        MenuItem realisticItem = viewMenuBtn.getItems().get(1);
        diagramItem.setOnAction(_ -> switchView(true));
        realisticItem.setOnAction(_ -> switchView(false));
        viewMenuBtn.setOnAction(_ -> switchView(true));

        // menu buttons
        setUpMenuActions();

        clearBtn.setOnAction(_ -> {
            drawingArea.circuit.print();
            drawingArea.circuit.clear();
            // clear animation as well
            drawingArea.animateCurrentFlow(false);
            runStopBtn.setText("Run");

            drawingArea.canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        });
        defaultWireColorPicker.setValue(Color.BLACK);
        defaultWireColorPicker.setOnAction(_ -> {
            Color pickedColor = defaultWireColorPicker.getValue();
            if (pickedColor == null) {
                pickedColor = Color.BLACK;
            }
            drawingTool.defaultWireColor = pickedColor;
        });

        polarityCheckBox.setOnAction(_ -> {
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

            edu.vanier.math.CircuitMath maths = new edu.vanier.math.CircuitMath(drawingArea.circuit);
            // Create text display
            TextFlow infoText = new TextFlow(
                    new Text("Total Resistance: " + maths.getTotalResistance() + " Ω\nTotal Voltage:" + maths.getTotalVoltage() + " V\nTotal Current:" + maths.getTotalCurrent() + " A\n")
            );
            infoText.setTextAlignment(TextAlignment.LEFT);

            // Graph title
            Label graphTitle = new Label("Kirchhoff's Loop Rule Graph");
            graphTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            // Graph
            // Define axes
            NumberAxis xAxis = new NumberAxis();
            xAxis.setLabel("Resistance (Ω)");

            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Current (A)");

            // Create chart
            LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
            lineChart.setTitle("Current vs Resistance");

            // Series for data
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName("Components");

            // Add data points from circuit
            for (Component c : drawingArea.circuit.toArrayList()) {
                double r = c.getResistance();
                double i = c.getCurrent();
                // Only plot components with valid values
                if (!Double.isNaN(r) && !Double.isNaN(i)) {
                    series.getData().add(new XYChart.Data<>(r, i));
                }
            }

            lineChart.getData().add(series);
            lineChart.setLegendVisible(false);

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

            // Animation
            VBox bottomContainer = new VBox(closeButton);
            bottomContainer.setAlignment(Pos.CENTER);
            bottomContainer.setSpacing(10);
            content.getChildren().add(bottomContainer);
            codeStage.show();
        });
        runStopBtn.setText("Run");
        runStopBtn.setOnAction(_ -> {
            // Check if there's an open switch
            boolean hasOpenSwitch = drawingArea.circuit.toArrayList().stream()
                    .filter(c -> c instanceof edu.vanier.strawberries.Models.Switch)
                    .map(c -> (edu.vanier.strawberries.Models.Switch) c)
                    .anyMatch(s -> !s.isClosed());
            if (!animationRunning && hasOpenSwitch) {
                // shows an error message if the switch is open
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Open Switch Detected");
                alert.setHeaderText(null);
                alert.setContentText("The switch is open!! Close it to allow current to flow");
                alert.showAndWait();
                return; // no animation
            }
            animationRunning = !animationRunning;
            if (animationRunning) {
                runStopBtn.setText("Stop");
                // creates a circuit math object
                edu.vanier.math.CircuitMath math = new edu.vanier.math.CircuitMath(drawingArea.circuit);
                math.assignValuesToComponents();
                drawingArea.animateCurrentFlow(true);
            } else {
                runStopBtn.setText("Run");
                drawingArea.animateCurrentFlow(false);
            }
        });
    }

    /**
     * Method called whenever the mouse moves location on the screen.
     * @implNote Serves update the global variables {@link #posX} and {@link #posY} and the state of the cursor when hovering a circuit element.
     * @param e the MouseEvent fired.
     */
    private void mouseMoved(MouseEvent e) {
        posX = e.getX();
        posY = e.getY();
        if(drawingTool.getCurrentAction().equals("select")) {
            if (selection != null) setCursor(Cursor.OPEN_HAND);
            else setCursor(Cursor.DEFAULT);
        }
    }

    /**
     * Method called whenever the mouse is pressed down.
     * @param e The MouseEvent fired.
     */
    private void mousePressed(MouseEvent e) {
        mouseDownLocation = new Point2D(e.getX(),e.getY());
        if(!Objects.equals(drawingTool.getCurrentAction(),"")) {
            if(selection!=null && !Objects.equals(drawingTool.getCurrentAction(), "select")) unselect(selection);
            if(editing!=null) edit(null);
            drawingTool.setPencilDown(true);
            Node eventLocation = new Node(drawingArea.snap(e.getX()), drawingArea.snap(e.getY()));
            Node tempEnd = Node.copyOf(eventLocation);
            switch (drawingTool.getCurrentAction()) {
                case "place-wire" -> select(new Wire(eventLocation, tempEnd, ((drawingTool.getCurrentColor()==null) ? drawingTool.defaultWireColor : drawingTool.getCurrentColor()), 0, 0));
                case "place-battery" -> select(new Battery(eventLocation, tempEnd, 12, diagramView));
                case "place-capacitor" -> select(new Capacitor(eventLocation, tempEnd, 0, diagramView));
                case "place-fuse" -> select(new Fuse(eventLocation, tempEnd,20, diagramView));
                case "place-lightbulb" -> select(new Lightbulb(eventLocation, tempEnd,(drawingTool.getCurrentColor()==null) ? drawingTool.defaultLightbulbColor : drawingTool.getCurrentColor(),0, diagramView));
                case "place-resistor" -> select(new Resistor(eventLocation, tempEnd, 10, diagramView));
                case "place-switch" -> select(new Switch(eventLocation, tempEnd, false,diagramView));
                case "select" -> {
                    if(selection != null) setCursor(Cursor.CLOSED_HAND);
                    edit(selection);
                    if (selection instanceof Battery battery) {
                        battery.handleEdit(leftPanel);
                    }
                    else if (selection instanceof Resistor resistor) {
                        resistor.handleEdit(leftPanel);
                    }

                    if (selection instanceof Wire wire) {
                        initialBegin = wire.begin.getPosition();
                        initialEnd = wire.end.getPosition();
                    }

                    if (selection instanceof Switch switchObj) {
                        switchObj.toggle();
                    }
                }
                default -> {}
            }
            drawingTool.setCurrentColor(null);
            if (!Objects.equals(drawingTool.getCurrentAction(), "select")) {
                circuit.addComponent(selection);
                if (!(selection instanceof Wire)) {
                    selection.end.setPosition(selection.begin.getX() + selection.display.getWidth(), selection.begin.getY());
                }
            }
        }

        if(editing != null) {
            if(editing instanceof Wire wire) {
                //find which node to move

                //1. Calculate the dist from the point to either node
                double d1 = wire.begin.getPosition().distance(e.getX(), e.getY());
                double d2 = wire.end.getPosition().distance(e.getX(), e.getY());
                double buffer = 0.5; // Accounts for uncertainty due to cursor size

                if (d1 + buffer <= 25) {
                    wire.begin.unlock();
                    toMove[0] = wire.begin;
                    wire.end.lock();
                } else if (d2 + buffer <= 25) {
                    wire.end.unlock();
                    toMove[0] = wire.end;
                    wire.begin.lock();
                } else {
                    toMove[0] = wire.begin;
                    toMove[1] = wire.end;
                }
            }
            else {
                //Images
                if(editing.getAngle()==0 || editing.getAngle()==180) toMove[0] = (editing.begin.getX() < editing.end.getX() ? editing.begin : editing.end);
                else toMove[0] = (editing.begin.getY() < editing.end.getY() ? editing.begin : editing.end);
            }
        }
    }

    /**
     * Method called when the mouse is dragged across the screen
     * @param e The MouseEvent fired
     */
    private void mouseDragged(MouseEvent e) {
        double correctedX = e.getX(), correctedY = e.getY();
        if(e.getX() > canvas.getWidth()) correctedX = canvas.getWidth();
        if(e.getX() < 0) correctedX = 0;
        if(e.getY() > canvas.getHeight()-toolbarScrollPane.getHeight()) correctedY = canvas.getHeight() - toolbarScrollPane.getHeight() - 5;
        if(e.getY() < toolbarScrollPane.getHeight()) correctedY = 0;

        double displacementX = mouseDownLocation.getX() - correctedX;
        double displacementY = mouseDownLocation.getY() - correctedY;

        if (drawingTool.isPencilDown() && selection != null) {
            double nearestX = drawingArea.snap(correctedX);
            double nearestY = drawingArea.snap(correctedY);
            selection.moveNode(selection.end, nearestX, nearestY);
        }

        if(editing != null) {
            if(editing instanceof Wire wire) {
                if(toMove[1]!=null) {
                    //move both (keep length)
                    wire.begin.setPosition(drawingArea.snap(initialBegin.getX()-displacementX),drawingArea.snap(initialBegin.getY()-displacementY));
                    wire.end.setPosition(drawingArea.snap(initialEnd.getX()-displacementX), drawingArea.snap(initialEnd.getY()-displacementY));
                }
                else {
                    Node node = toMove[0];
                    node.setPosition(drawingArea.snap(correctedX), drawingArea.snap(correctedY));
                }
            }
            else {
                Node node = toMove[0];
                node.setPosition(drawingArea.snap(correctedX-editing.display.getWidth()/2),drawingArea.snap(correctedY-editing.display.getHeight()/2));
                editing.updateEnd();
            }
        }
    }

    /**
     * Checks if the user pressed on a given component that is NOT a wire.
     * @param source The Point2D representing the coordinates of where the mouse was pressed.
     * @param component The component to verify.
     * @return true if component was pressed.
     */
    private boolean checkComponentCollision(Point2D source, Component component) {
        boolean vertical = (component.getAngle() == 90 || component.getAngle() == 270);

        double maxX,maxY;
        Point2D minimums = component.getMinimums();
        if(vertical) {
            maxX = minimums.getX() + (component.display.getHeight() / 2);
            maxY = minimums.getY() + component.display.getWidth();
        } else {
            maxX = minimums.getX() + component.display.getWidth();
            maxY = minimums.getY() + component.display.getHeight();
        }

        return ((source.getX() <= maxX && source.getX() >= minimums.getX()) && (source.getY() <= maxY && source.getY() >= minimums.getY()));
    }


    private boolean checkLineCollision(Point2D source, Wire wire) {
        Point2D begin = wire.begin.getPosition(),
                end = wire.end.getPosition();

        //1. Calculate the dist from the point to either node
        double d1 = source.distance(begin);
        double d2 = source.distance(end);

        //2. Verify that both add up to the length of the line wire
        double length = begin.distance(end);
        double buffer = 1; // Accounts for uncertainty due to cursor size

        return (d1+d2 >= length-buffer && d1+d2 <= length+buffer);
    }

    /**
     * Method called when the mouse is released.
     * @implNote Serves to reset necessary values such as {@link #selection}.
     * @param e The MouseEvent fired
     */
    private void mouseReleased(MouseEvent e) {
        toMove = new Node[2];
        if (selection != null) {
            circuit.attemptConnection(selection, null);
            if (drawingTool.isPencilDown()) {
                drawingTool.setPencilDown(false);
                if(selection.getLength()<=0) circuit.deleteComponent(selection);
            }
            if(canvas.getCursor().equals(Cursor.CLOSED_HAND)) setCursor(Cursor.OPEN_HAND);
        }
        else if(editing != null) circuit.attemptConnection(editing, null);

        Circuit.checkForCycle(circuit);
    }

    /**
     * Returns the application to the log-in page
     */
    private void quit() {
        MainApp.switchScene(MainApp.START_SCENE);
    }

    /**
     * Mark a given component as the currently selected one
     * @param component The component to select
     * @implNote It is possible to pass "null" to unselect, but it is preferred to use the method {@link #unselect(Component)}
     */
    public void select(Component component) {
        if(selection!=null) selection.markAsSelected(false);
        component.markAsSelected(true);
        selection = component;
    }

    /**
     * Unselect the given component
     * @param component The component to unselect
     * @implNote The component passed in the function does not have to be the currently selected one. This will be taken into account by the method itself.
     */
    public void unselect(Component component) {
        if(component != null && selection == component) {
            component.markAsSelected(false);
            selection = null;
        }
    }

    /**
     * TODO INSERT JAVADOC HERE
     * @param drawingArea
     * @return
     */
    private static LineChart<Number, Number> getChart(DrawingArea drawingArea) {
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

        // constant Voltage
        edu.vanier.math.CircuitMath maths = new edu.vanier.math.CircuitMath(drawingArea.circuit);

        List<Component> path = maths.getTraversalPath();
        series.getData().add(new XYChart.Data<>(0, maths.getTotalVoltage()));
        int i = 1;
        for (Component c : path) {
            if (c instanceof Battery b) {
                series.getData().add(new XYChart.Data<>(i, b.getPotential()));
            }
            i++;
        }

        lineChart.getData().add(series);
        return lineChart;
    }

    @FXML
    private void menuSelectBtnPressed() {
        drawingTool.setCurrentAction("select");
    }

    /**
     * Initializes the key listeners of the main applications, so that the user can use keyboard shortcuts.
     * @implNote This method should be called in the {@link #initialize()} method.
     */
    private void setUpKeyListeners() {
        window.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            switch(event.getCode()) {
                case S,ESCAPE -> {
                    unselect(selection);
                    drawingTool.setCurrentAction("select");
                    setCursor(Cursor.DEFAULT);
                }
                case W -> drawingTool.setCurrentAction("place-wire");
                case B -> drawingTool.setCurrentAction("place-battery");
                case C -> drawingTool.setCurrentAction("place-capacitor");
                case L -> drawingTool.setCurrentAction("place-lightbulb");
                case T -> drawingTool.setCurrentAction("place-switch");
                case R -> drawingTool.setCurrentAction("place-resistor");
                case DELETE,BACK_SPACE -> circuit.deleteComponent(editing);
                case COMMA -> {
                    if(editing!=null) editing.rotate("left");
                }
                case PERIOD -> {
                    if(editing!=null) editing.rotate("right");
                }
                case P -> {
                    circuit.print();
                }
            }
        });
    }

    /**
     * Modify the appearance of the cursor in the main window.
     * @param cursor The Cursor object to set as the current cursor.
     */
    public void setCursor(Cursor cursor) {
        canvas.setCursor(cursor);
    }

    /**
     * Modify the color scheme of the application
     * @param cssFile The filepath to the css file containing the style to be applied.
     */
    private void applyTheme(String cssFile) {
        window.getStylesheets().clear();
        window.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/" + cssFile)).toExternalForm());
        currentTheme = cssFile.substring(0,cssFile.length()-4);
    }

    /**
     *
     * @return the current applied theme of the application (ex.: "light-mode", "dark-mode", "strawberries")
     */
    public String getTheme() {
        return currentTheme;
    }

    /**
     * Set the currently being edited element
     * @param component The component to be edited.
     * @implNote The <em>editing</em> variable is different from the <em>selection</em> variable.
     */
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

    /**
     * Switch between realistic and diagram views of the components
     * @param diagramView Represents if the program should display a diagram view.
     */
    private void switchView(boolean diagramView) {
        this.diagramView = diagramView;
        viewMenuBtn.setText(diagramView ? "Diagram" : "Realistic");
        drawingArea.switchView(diagramView);
    }

    /**
     * Initializes the methods of menu items
     * @implNote This method should be called in the {@link #initialize()} method
     */
    private void setUpMenuActions() {
        //FILE MENU
        menuNew.setOnAction(_-> {});
        menuOpen.setOnAction(_->{});
        menuOpenRecent.setOnAction(_->{});
        menuSave.setOnAction(_->{});
        String circuitName = circuitNameField.getText();
        menuSaveAs.setOnAction(_->exportToTxt(circuitName));
        menuQuit.setOnAction(_->quit());

        // SETTINGS & VIEW MENU
        menuShowToolbar.setOnAction(_ -> showToolBar(true));
        menuHideToolbar.setOnAction(_ -> showToolBar(false));
        lightThemeItem.setOnAction(_ -> applyTheme("light-mode.css"));
        darkThemeItem.setOnAction(_ -> applyTheme("dark-mode.css"));
        strawThemeItem.setOnAction(_ -> applyTheme("strawberries-theme.css"));
        menuFitToScreen.setOnAction(_ -> {
        });
        menuZoomIn.setOnAction(zoomInBtn.getOnAction());
        menuZoomOut.setOnAction(zoomOutBtn.getOnAction());
        menuToggleGrid.setOnAction(_ -> drawingArea.toggleGrid());
        String username = MainApp.loggedInUsername;
        if (!Objects.equals(username, "")) {
            String finalUsername1 = username;
            exportBtn.setOnAction(_ -> exportToJson(finalUsername1)); // if logged in, export to json file
        } else {
            // System.out.println(e.getMessage());
            exportBtn.setOnAction(_ -> exportToTxt(circuitName)); // if not logged in, export to txt (local)
        }
        //String recentProject = MainApp.signOnLogInController.getRecent();
        //menuOpenRecent.setOnAction(_->openRecent(recentProject));
        String finalUsername = username;
        menuOpenRecent.setOnAction(_ -> {
            importFromJson(finalUsername, drawingArea);
            update();
        });

        // INSERT MENU
        //wire
        menuWire.setOnAction(_ -> drawingTool.setCurrentAction("place-wire"));
        menuRedWire.setOnAction(_ -> drawingTool.setCurrentColor(Color.RED));
        menuBlackWire.setOnAction(_ -> drawingTool.setCurrentColor(Color.BLACK));
        menuColorPicker.setOnAction(_ -> drawingTool.setCurrentColor(menuColorPicker.getValue()));
        //other components
        menuResistor.setOnAction(_ -> drawingTool.setCurrentAction("place-resistor"));
        menuBattery.setOnAction(_ -> drawingTool.setCurrentAction("place-battery"));
        menuSwitch.setOnAction(_ -> drawingTool.setCurrentAction("place-switch"));
        menuCapacitor.setOnAction(_ -> drawingTool.setCurrentAction("place-capacitor"));
        menuLightbulb.setOnAction(_ -> drawingTool.setCurrentAction("place-lightbulb"));
        menuYellow.setOnAction(_ -> drawingTool.setCurrentColor(Color.YELLOW));
        menuRed.setOnAction(_ -> drawingTool.setCurrentColor(Color.RED));
        menuGreen.setOnAction(_ -> drawingTool.setCurrentColor(Color.GREEN));
        menuBlue.setOnAction(_ -> drawingTool.setCurrentColor(Color.BLUE));
        lightbulbColorPicker.setOnAction(_ -> drawingTool.setCurrentColor(lightbulbColorPicker.getValue()));
    }

    /**
     * Toggle the toolbar's visibility on/off
     * @param show true if the toolbar should be shown
     */
    private void showToolBar(boolean show) {
        if (show) {
            leftPanelVBox.getChildren().addFirst(toolbarScrollPane);
        } else {
            leftPanelVBox.getChildren().remove(toolbarScrollPane);
        }
    }

    /**
     * Exports current circuit project to json user data
     * @param username The username of the currently logged-in user
     */
    private void exportToJson(String username) {
        System.out.println("EXPORTING..."+username); // is null!!
        String notFormattedInfo = drawingArea.exportCircuit(circuitNameField.getText());
        String info = notFormattedInfo.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
        File jsonFile = new File("src/main/resources/users.json");

        String content = "";

        // read json
        if (!jsonFile.exists()) {
            JOptionPane.showMessageDialog(null, "JSON file does not exist.");
            return;
        }

        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(jsonFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to read JSON file.");
            return;
        }

        String[] lines = contentBuilder.toString().split("\n");
        StringBuilder updated = new StringBuilder();
        boolean userFound = false;
        boolean insideUser = false;
        boolean recentReplaced = false;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            // Detect start of target user
            if (line.trim().startsWith("\"username\":")) {
                String foundUsername = line.trim().replace("\"username\":", "")
                        .replace("\"", "")
                        .replace(",", "")
                        .trim();
                insideUser = foundUsername.equals(username);
                userFound |= insideUser;
            }

            // Replace first matching "recent" field
            if (insideUser && line.trim().startsWith("\"recent\":") && !recentReplaced) {
                updated.append("      \"recent\": \"").append(info).append("\",\n");
                recentReplaced = true;
                continue;
            }

            // Add new recent if it wasn't there
            if (insideUser && line.trim().equals("}") && !recentReplaced) {
                updated.append("      \"recent\": \"").append(info).append("\"\n");
                recentReplaced = true;
            }

            updated.append(line).append("\n");

            // Exit user block
            if (insideUser && line.trim().equals("}")) {
                insideUser = false;
            }
        }

        if (!userFound) {
            JOptionPane.showMessageDialog(null, "User not found in JSON.");
            return;
        }

        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write(updated.toString());
            JOptionPane.showMessageDialog(null, "Exported circuit to JSON for user: " + username);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to write to JSON file.");
        }
    }

    /**
     * Helper method for formatting json file
     * @param input The text to format
     * @return The formatted text
     */
    private String escape(String input) {
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    /**
     * Helper method to check if there is recent project data in json
     * @param lines
     * @param index
     * @return
     */
    private boolean lineAboveContainsRecent(String[] lines, int index) { // helper method check if there is recent
        for (int i = index - 1; i >= 0; i--) {
            String trimmed = lines[i].trim();
            if (trimmed.startsWith("\"") && trimmed.endsWith(",")) {
                return trimmed.startsWith("\"recent\":");
            } else if (trimmed.equals("{")) {
                break;
            }
        }
        return false;
    }

    /**
     * Exports the circuit as a text file
     * @param circuitName The name of the circuit (given by the user in {@link #circuitNameField})
     */
    private void exportToTxt(String circuitName) {
        String info = drawingArea.exportCircuit(circuitName); // get drawingArea to put in txt
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Circuit as TXT");
        int userSelection = fileChooser.showSaveDialog(null); // file dialog

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                writer.write(info);
                JOptionPane.showMessageDialog(null, "Circuit exported to TXT successfully.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error exporting circuit to TXT.");
            }
        }
    }

    /**
     * Imports the circuit from the json user data to the Drawing Area
     * @param username
     * @param drawingArea
     */
    public void importFromJson(String username, DrawingArea drawingArea) {
        File jsonFile = new File("src/main/resources/users.json");

        if (!jsonFile.exists()) {
            System.out.println("JSON file not found.");
            return;
        }

        String recent = MainApp.recentProject;

        if (recent == null || recent.isEmpty()) {
            System.out.println("No recent project found for user: " + username);
            return;
        }

        String[] lines = recent.split("\n");

        Circuit circuit = new Circuit(true);  // reset the circuit
        ArrayList<Component> components = new ArrayList<>(); // temp component holder

        for (String line : lines) {

            // Remove any prefix before "wire|"
            if (line.matches(".*\\|wire\\|.*")) {
                line = line.substring(line.indexOf("wire|"));
            }

            if (line.contains("wire|")) {
                // wire format: wire|color|x1|y1|x2|y2
                // fix!! wire does not appear
                String[] parts = line.split("\\|");
                if (parts.length == 6) {
                    try {
                        Color color = Color.web(parts[1].trim());
                        double x1 = Double.parseDouble(parts[2]);
                        double y1 = Double.parseDouble(parts[3]);
                        double x2 = Double.parseDouble(parts[4]);
                        double y2 = Double.parseDouble(parts[5]);

                        Node begin = new Node(x1, y1);
                        Node end = new Node(x2, y2);
                        Wire wire = new Wire(begin, end, color, 10, 10);

                        circuit.addComponent(wire);
                    } catch (Exception e) {
                        System.out.println("[ERROR] Failed to parse wire: " + e.getMessage());
                    }
                } else {
                    System.out.println("[DEBUG] Invalid wire format: " + line);
                }
            } else if (line.contains("|")) {
                // component format: type|x|y|angle
                String[] parts = line.split("\\|");
                if (parts.length == 4) {
                    try {
                        String type = parts[0];
                        double x = Double.parseDouble(parts[1]);
                        double y = Double.parseDouble(parts[2]);
                        int angle = (int) Double.parseDouble(parts[3]);

                        Component component = null;
                        Node begin = new Node(x, y);
                        Node end = new Node(x + 50, y);

                        switch (type.toLowerCase()) {
                            case "resistor":
                                component = new Resistor(begin, end, 100.0, false);
                                break;
                            case "battery":
                                component = new Battery(begin, end, 5.0, false);
                                break;
                            case "capacitor":
                                component = new Capacitor(begin, end, 10, true);
                                break;
                            case "lightbulb":
                                component = new Lightbulb(begin, end, Color.web("BLACK"), 10, true);
                                break;
                            case "fuse":
                                component = new Fuse(begin, end, 10, true);
                                break;
                            case "switch":
                                component = new Switch(begin, end, true, true);
                                break;
                            default:
                                System.out.println("[DEBUG] Unknown component type: " + type);
                        }

                        if (component != null) {
                            component.setRotate(angle);
                            circuit.addComponent(component);
                            components.add(component);
                        }
                    } catch (Exception e) {
                        System.out.println("[ERROR] Failed to parse component: " + e.getMessage());
                    }
                } else {
                    System.out.println("[DEBUG] Invalid component format: " + line);
                }
            }
        }

        // Connections
        circuit.connectEntireCircuit();

        // visual
        this.circuit = circuit;
        drawingArea.setCircuit(circuit);
        drawingArea.drawContent();
        circuit.print();
        Circuit.checkForCycle(circuit);

        System.out.println("Circuit imported successfully from JSON.");
        System.out.println(circuit);
    }
}