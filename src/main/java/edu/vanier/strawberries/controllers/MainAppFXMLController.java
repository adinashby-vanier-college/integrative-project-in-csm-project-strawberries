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

import java.io.*;

import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static edu.vanier.strawberries.controllers.SignOnLogInController.findRecent;

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

        if(drawingArea.circuit==null) drawingArea.setCircuit(circuit);
    }

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
        for(LinkedList<Component> list : circuit.arrayList) {
            for(Component component : list) {
                CircuitMath.setVoltageAcross(component);
                if(component instanceof Lightbulb lightbulb) {
                    System.out.println("lightbulb voltage: "+lightbulb.getVoltage());
                    if(lightbulb.getVoltage() >= lightbulb.getMinVoltage()) lightbulb.turnOn(true);
                }
            }
        }

        //Update circuit message
        if(circuit.arrayList.isEmpty()) circuitStateText.setText("Empty circuit. A blank canvas!");
        else if(circuit.isClosed()) circuitStateText.setText("Closed circuit!");
        else circuitStateText.setText("Open circuit... No current :(");

        // Draw everything
        drawingArea.drawContent();
    }

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
        drawingArea.switchView(true);
        MenuItem diagramItem = viewMenuBtn.getItems().get(0);
        MenuItem realisticItem = viewMenuBtn.getItems().get(1);
        diagramItem.setOnAction(e -> drawingArea.switchView(true));
        realisticItem.setOnAction(e -> drawingArea.switchView(false));
        viewMenuBtn.setOnAction(e -> drawingArea.switchView(true));

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
            if (pickedColor == null) pickedColor = Color.BLACK;
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
            final LineChart<Number, Number> lineChart = getChart(drawingArea);

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

    private void mouseMoved(MouseEvent e) {
        posX = e.getX();
        posY = e.getY();
        if(drawingTool.getCurrentAction().equals("select")) {
            if (selection != null) setCursor(Cursor.OPEN_HAND);
            else setCursor(Cursor.DEFAULT);
        }
    }

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

                   if(selection instanceof Wire wire) {
                       initialBegin = wire.begin.getPosition();
                       initialEnd = wire.end.getPosition();
                   }
                   if(selection instanceof Switch switchObj) { // switch is a reserved word
                       switchObj.toggle();
                    }
                }
                default -> {}
            }
            drawingTool.setCurrentColor(null);
            if (!Objects.equals(drawingTool.getCurrentAction(), "select")) {
                circuit.addComponent(selection);
                if(!(selection instanceof Wire)) {
                    selection.end.setPosition(selection.begin.getX()+selection.display.getWidth(),selection.begin.getY());
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
            else { //Images
                if(editing.getAngle()==0 || editing.getAngle()==180) toMove[0] = (editing.begin.getX() < editing.end.getX() ? editing.begin : editing.end);
                else toMove[0] = (editing.begin.getY() < editing.end.getY() ? editing.begin : editing.end);
            }
        }
    }

    private boolean checkComponentCollision(Point2D source, Component component) {
        boolean vertical = (component.getAngle()==90 || component.getAngle()==270);

        double maxX,maxY;
        Point2D minimums = component.getMinimums();
        if(vertical) {
            maxX = minimums.getX() + (component.display.getHeight() / 2);
            maxY = minimums.getY() + component.display.getWidth();
        }
        else {
            maxX = minimums.getX() + component.display.getWidth();
            maxY = minimums.getY() + component.display.getHeight();
        }

        return ((source.getX() <= maxX && source.getX() >= minimums.getX()) && (source.getY() <= maxY && source.getY() >= minimums.getY()));
    }

    private void quit() {
        MainApp.switchScene(MainApp.START_SCENE);
    }

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

        //TODO testing
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
            }
        }
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


    private void mouseReleased(MouseEvent e) {
        toMove = new Node[2];
        if (selection != null) {
            attemptConnection(selection, selection.begin);
            if (drawingTool.isPencilDown()) {
                drawingTool.setPencilDown(false);
                if(selection.getLength()<=0) circuit.deleteComponent(selection);
            }
            if(canvas.getCursor().equals(Cursor.CLOSED_HAND)) setCursor(Cursor.OPEN_HAND);
        }
        else if(editing != null) attemptConnection(editing, editing.begin);

        circuit.checkForCycle();
    }

    public void select(Component component) {
        if(selection!=null) selection.markAsSelected(false);
        component.markAsSelected(true);
        selection = component;
    }

    public void unselect(Component component) {
        if(component != null && selection == component) {
            component.markAsSelected(false);
            selection = null;
        }
    }

    private void attemptConnection(Component toCheck, Node node) {
        int srcIndex = circuit.getIndex(toCheck);
        Point2D checkPoint = node.getPosition();
        ArrayList<Integer> connectedComponents = new ArrayList<>(), disconnectedComponents = new ArrayList<>();

        // CHECK FOR CONNECTION
        for (LinkedList<Component> currentList : circuit.arrayList) {
            for (Component connectedComponent : currentList) {
                if(connectedComponent != toCheck) {
                    int dstIndex = circuit.getIndex(connectedComponent);

                    Point2D componentBegin = connectedComponent.begin.getPosition();
                    Point2D componentEnd = connectedComponent.end.getPosition();

                    if ((componentBegin.distance(checkPoint) <= 1) || (componentEnd.distance(checkPoint) <= 1)) {
                        if(!connectedComponents.contains(dstIndex)) connectedComponents.add(dstIndex);
                        if(!node.isConnected()) node.setConnected(true);
                    }
                }
            }
        }
        for (int compIndex : connectedComponents) circuit.addEdge(srcIndex, compIndex);

        // CHECK FOR DISCONNECTION
        Point2D checkBegin = toCheck.begin.getPosition(),
                checkEnd = toCheck.end.getPosition();
        for(Component connected : circuit.arrayList.get(srcIndex)) {
            if(connected != toCheck) {
                Point2D compBegin = connected.begin.getPosition(),
                        compEnd = connected.end.getPosition();
                if ((checkBegin.distance(compBegin) > 1 && checkBegin.distance(compEnd) > 1) && (checkEnd.distance(compBegin) > 1 && checkEnd.distance(compEnd) > 1)) {
                    circuit.removeEdge(srcIndex, circuit.getIndex(connected));
                }
            }
        }

        if(node == toCheck.begin) attemptConnection(toCheck, toCheck.end);
    }

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

    public void setCursor(Cursor cursor) {
        canvas.setCursor(cursor);
    }

    private void applyTheme(String cssFile) {
        window.getStylesheets().clear();
        window.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/" + cssFile)).toExternalForm());
        currentTheme = cssFile.substring(0,cssFile.length()-4);
    }

    public String getTheme() {
        return currentTheme;
    }

    private void openRecent(String project) {
        // TODO: open project
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

  private void setUpMenuActions() {
  //FILE MENU
      menuNew.setOnAction(_-> {});
      menuOpen.setOnAction(_->{});
      menuOpenRecent.setOnAction(_->{});
      menuSave.setOnAction(_->{});
      menuSaveAs.setOnAction(_->exportToTxt());
      menuQuit.setOnAction(_->quit());

  // SETTINGS & VIEW MENU
      menuShowToolbar.setOnAction(_-> showToolBar(true));
      menuHideToolbar.setOnAction(_-> showToolBar(false));
      lightThemeItem.setOnAction(_ -> applyTheme("light-mode.css"));
      darkThemeItem.setOnAction(_-> applyTheme("dark-mode.css"));
      strawThemeItem.setOnAction(_-> applyTheme("strawberries-theme.css"));
      menuFitToScreen.setOnAction(_->{});
      menuZoomIn.setOnAction(zoomInBtn.getOnAction());
      menuZoomOut.setOnAction(zoomOutBtn.getOnAction());
      menuToggleGrid.setOnAction(_->drawingArea.toggleGrid());
      String username = MainApp.signOnLogInController.getUsername();
      exportBtn.setOnAction(_->exportToJson(username));
      String recentProject = MainApp.signOnLogInController.getRecent();
      //menuOpenRecent.setOnAction(_->openRecent(recentProject));
      menuOpenRecent.setOnAction(_-> {
          DrawingArea.importFromJson(username, drawingArea);
      });

      // INSERT MENU
      //wire
      menuWire.setOnAction(_-> drawingTool.setCurrentAction("place-wire"));
      menuRedWire.setOnAction(_-> drawingTool.setCurrentColor(Color.RED));
      menuBlackWire.setOnAction(_-> drawingTool.setCurrentColor(Color.BLACK));
      menuColorPicker.setOnAction(_-> drawingTool.setCurrentColor(menuColorPicker.getValue()));
      //other components
      menuResistor.setOnAction(_-> drawingTool.setCurrentAction("place-resistor"));
      menuBattery.setOnAction(_-> drawingTool.setCurrentAction("place-battery"));
      menuSwitch.setOnAction(_-> drawingTool.setCurrentAction("place-switch"));
      menuCapacitor.setOnAction(_-> drawingTool.setCurrentAction("place-capacitor"));
      menuLightbulb.setOnAction(_-> drawingTool.setCurrentAction("place-lightbulb"));
      menuYellow.setOnAction(_-> drawingTool.setCurrentColor(Color.YELLOW));
      menuRed.setOnAction(_-> drawingTool.setCurrentColor(Color.RED));
      menuGreen.setOnAction(_-> drawingTool.setCurrentColor(Color.GREEN));
      menuBlue.setOnAction(_-> drawingTool.setCurrentColor(Color.BLUE));
      lightbulbColorPicker.setOnAction(_-> drawingTool.setCurrentColor(lightbulbColorPicker.getValue()));
  }

  private void showToolBar(boolean show) {
        if(show) leftPanelVBox.getChildren().addFirst(toolbarScrollPane);
        else leftPanelVBox.getChildren().remove(toolbarScrollPane);
  }

    private void exportToJson(String username) {
        String info = drawingArea.exportCircuit(circuitNameField.getText());
        File jsonFile = new File("users.json");

        String content = "";

        // read json
        if (jsonFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(jsonFile))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                content = sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to read JSON file.");
                return;
            }
        } else {
            JOptionPane.showMessageDialog(null, "JSON file does not exist.");
            return;
        }

        // look for user, replace recent
        String[] lines = content.split("\n");
        StringBuilder updated = new StringBuilder();
        boolean userFound = false;
        boolean insideTargetUser = false;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            if (line.trim().startsWith("\"username\":") && line.contains("\"" + username + "\"")) {
                userFound = true;
                insideTargetUser = true;
                updated.append(line).append("\n");
                continue;
            }

            if (insideTargetUser && line.trim().startsWith("\"recent\":")) {
                // replace the recent field
                updated.append("      \"recent\": \"").append(escape(info.trim())).append("\",\n");
                continue;
            }

            // insert recent if it wasn't already there
            if (insideTargetUser && line.trim().startsWith("}")) {
                if (!content.contains("\"recent\"") || !lineAboveContainsRecent(lines, i)) {
                    updated.append("      \"recent\": \"").append(escape(info.trim())).append("\"\n");
                }
                insideTargetUser = false;
            }

            updated.append(line).append("\n");
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

    private String escape(String input) { // helper method for formatting
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }

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

  private void exportToTxt() {
        String info = drawingArea.exportCircuit(circuitNameField.getText()); // get drawingArea to put in txt
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setDialogTitle("Save Circuit as TXT");
      int userSelection = fileChooser.showSaveDialog(null); // file dialog

      if (userSelection == JFileChooser.APPROVE_OPTION) {
          File fileToSave = fileChooser.getSelectedFile();

          try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
              writer.write(info);
              JOptionPane.showMessageDialog(null, "Circuit exported to TXT successfully.");
          } catch (IOException e) {
              e.printStackTrace();
              JOptionPane.showMessageDialog(null, "Error exporting circuit to TXT.");
          }
      }
  }
}