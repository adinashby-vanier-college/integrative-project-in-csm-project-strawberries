package edu.vanier.strawberries.Models;

import edu.vanier.math.CircuitMath;
import edu.vanier.strawberries.controllers.MainAppFXMLController;
import edu.vanier.strawberries.controllers.SignOnLogInController;
import edu.vanier.strawberries.ui.MainApp;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.animation.PathTransition;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import javafx.scene.paint.Color;

public class DrawingArea {

    public DrawingTool drawingTool = new DrawingTool();
    private Component selection;
    public Circuit circuit;
    public Canvas canvas;
    public GraphicsContext gc;
    private double zoom;
    private double squareSize;
    private boolean showGrid;

    private final List<PathTransition> activeTransitions = new ArrayList<>();
    // Make a list to store the electrons to that are going to get animated
    private final List<Electron> animatedElectrons = new ArrayList<>();
    private boolean animateCurrent = false;

    public DrawingArea(Canvas canvas) {
        this.canvas = canvas;
        gc = canvas.getGraphicsContext2D();
        setZoom(1);
        showGrid = true;
        drawingTool.setCurrentAction("");
    }

    /**
     * Method called every frame. Draws the elements to be displayed in the
     * canvas, such as circuit components
     */
    public void drawContent() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Draw the grid
        if (showGrid) {
            switch (MainApp.mainAppFXMLController.getTheme()) {
                case "light-mode" ->
                    gc.setStroke(Color.LIGHTGREY);
                case "dark-mode" ->
                    gc.setStroke(Color.BLACK);
                case "strawberries-theme" ->
                    gc.setStroke(Color.PINK);
            }
            gc.setLineWidth(1);
            for (int i = 0; i < canvas.getWidth(); i += (int) (squareSize * zoom)) {
                for (int j = 0; j < canvas.getHeight(); j += (int) (squareSize * zoom)) {
                    gc.strokeRect(i, j, squareSize * zoom, squareSize * zoom);
                }
            }
        }
        if (animateCurrent) {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.setFill(Color.RED);
            for (Electron e : animatedElectrons) {
                Point2D pos = e.getPosition();
                gc.fillOval(pos.getX() - 3, pos.getY() - 3, 10, 10);
            }
        }

        // Draw the Components
        for (Component component : circuit.toArrayList()) {
            assert component != null;

            if (component instanceof Wire wire) {
                gc.setLineWidth(3);
                gc.setStroke(wire.getColor());
                gc.strokeLine(component.begin.getX(), component.begin.getY(), component.end.getX(), component.end.getY());
                Node[] nodes = {wire.begin, wire.end};
                for (Node node : nodes) {
                    if (node.isConnected()) {
                        gc.setFill(wire.getColor());
                        gc.fillOval(node.getX() - 4, node.getY() - 4, 8, 8);
                    }
                }
                if (wire.selected) {
                    gc.setFill(Color.BLACK);
                    gc.fillOval(wire.begin.getX() - 5, wire.begin.getY() - 5, 10, 10);
                    gc.fillOval(wire.end.getX() - 5, wire.end.getY() - 5, 10, 10);
                }
            } else {
                Image img = component.display;
                Rotate rotateTransform = new Rotate(component.getAngle(), component.begin.getX(), component.begin.getY());
                gc.save();
                gc.setTransform(new Affine(rotateTransform));
                gc.drawImage(img, component.begin.getX(), component.begin.getY() - (img.getHeight()) / 2, img.getWidth() * zoom, img.getHeight() * zoom);

                if (component.isEdit()) {
                    // SHOW IF THE COMPONENT IS BEING EDITED
                    gc.setStroke(Color.BLUE);
                    gc.setLineWidth(1.2);
                    gc.strokeRect(component.begin.getX(), component.begin.getY() - (img.getHeight()) / 2, img.getWidth() * zoom, img.getHeight() * zoom);
                }
                gc.restore();

                if (component instanceof Lightbulb lightbulb && lightbulb.isOn()) {
                    double haloX = lightbulb.getCenter().getX() - 20;
                    double haloY = lightbulb.getCenter().getY() - 20;

                    if (!lightbulb.diagramView) {
                        haloY -= 50;
                    }

                    gc.setFill(lightbulb.getColor().deriveColor(0, 1, 1, 0.5));
                    gc.fillOval(haloX, haloY, 40, 40);
                }

            }
        }
    }

    /**
     * Allows the component nodes to snap to the closed grid intersection when
     * dragging
     *
     * @param pos the un-snapped position of the node
     * @return The updated, snapped position relative to the grid.
     */
    public double snap(double pos) {
        double remainder = pos % (squareSize);
        if (remainder == 0) {
            return pos;
        } else if (remainder <= (double) squareSize / 2) {
            return pos - remainder;
        } else {
            return pos + (squareSize - remainder);
        }
    }

    /**
     * INSERT JAVADOC COMMENT HERE
     */
    private static class Electron {

        private final Wire wire;
        private double progress;
        private final double speedFactor;

        public Electron(Wire wire, double speedFactor) {
            this.wire = wire;
            this.speedFactor = speedFactor;
            this.progress = 0;
        }

        public void update(double current) {
            double dynamicSpeedFactor = speedFactor * (current / 1000);  // Normalize speed based on current

            progress += 0.01 * dynamicSpeedFactor;
            if (progress > 1.0) {
                progress = 0;
            }
        }

        public Point2D getPosition() {
            double x = wire.begin.getX() + (wire.end.getX() - wire.begin.getX()) * progress;
            double y = wire.begin.getY() + (wire.end.getY() - wire.begin.getY()) * progress;
            return new Point2D(x, y);
        }
    }

    public String exportCircuit(String circuitName) {
        StringBuilder output;
        if (circuitName != null) {
            output = new StringBuilder(circuitName + ".txt\n");
        } else {
            output = new StringBuilder("unnamedCircuit.txt\n");
        }
        // go through each component and add to final output
        for (Component component : circuit.toArrayList()) {
            assert component != null;
            if (component instanceof Wire wire) {
                Node[] nodes = {wire.begin, wire.end};
                for (Node node : nodes) {
                    if (node.isConnected()) {
                        output.append("1|");
                    }
                }
                gc.setLineWidth(3);
                output.append("wire|").append(wire.getColor()).append("|").append(component.begin.getX()).append("|").append(component.begin.getY()).append("|").append(component.end.getX()).append("|").append(component.end.getY()).append("|").append(component.getCurrent()).append("|").append(component.getVoltage()).append("\n");
            } else {
                switch (component) {
                    case Resistor resistor ->
                        output.append(component.getType()).append("|").append(component.begin.getX()).append("|").append(component.begin.getY()).append("|").append(component.getAngle()).append("|").append(resistor.getResistance()).append("\n");
                    case Battery battery ->
                        output.append(component.getType()).append("|").append(component.begin.getX()).append("|").append(component.begin.getY()).append("|").append(component.getAngle()).append("|").append(battery.getPotential()).append("\n");
                    case Capacitor capacitor ->
                        output.append(component.getType()).append("|").append(component.begin.getX()).append("|").append(component.begin.getY()).append("|").append(component.getAngle()).append("|").append(capacitor.getStoredEnergy()).append("\n");
                    case Lightbulb lightbulb ->
                        output.append(component.getType()).append("|").append(component.begin.getX()).append("|").append(component.begin.getY()).append("|").append(component.getAngle()).append("|").append(lightbulb.getResistance()).append("\n");
                    case Fuse fuse ->
                        output.append(component.getType()).append("|").append(component.begin.getX()).append("|").append(component.begin.getY()).append("|").append(component.getAngle()).append("|").append(fuse.getMaxCurrent()).append("\n");
                    case Switch switchh ->
                        output.append(component.getType()).append("|").append(component.begin.getX()).append("|").append(component.begin.getY()).append("|").append(component.getAngle()).append("|").append(switchh.getCurrent()).append("\n"); // curent not used, but it is here to not break formatting
                    default -> {
                    }
                }
            }
        }
        // remove duplicate lines
        StringBuilder builder = new StringBuilder();
        for (String line : new LinkedHashSet<String>(Arrays.asList(output.toString().split("\n")))) {
            builder.append(line).append("\n");
        }
        String result = builder.toString();
        System.out.println(result);
        return result;
    }

    boolean printedValues = false;

    public void animateCurrentFlow(boolean start) {
        animateCurrent = start;
        activeTransitions.clear();
        animatedElectrons.clear();

        if (!(canvas.getParent() instanceof Pane parent)) {
            return;
        }

        if (start) {
            int dotsPerWire = 5;
            double speedFactor;

            CircuitMath circuitMath = new CircuitMath(circuit);
            double totalCurrent = circuitMath.getTotalCurrent(); // get total current
            double totalResistance = circuitMath.getTotalResistance();
            double totalVoltage = circuitMath.getTotalVoltage();

            if (!printedValues) {
                System.out.println("Voltage: " + totalVoltage + "V");
                System.out.println("Resistance: " + totalResistance + "Ω");
                System.out.println("Current: " + totalCurrent + "A");
                printedValues = true;
            }

            for (LinkedList<Component> list : circuit.arrayList) {
                for (Component c : list) {
                    if (c instanceof Fuse fuse) {
                        if (totalCurrent > fuse.getMaxCurrent()) {
                            fuse.updateState(totalCurrent);
                            animateCurrent = false;

                            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                            alert.setTitle("Fuse Blown");
                            alert.setHeaderText("Current exceeded fuse limit!");
                            alert.setContentText("Current: " + totalCurrent + " A\nLimit: " + fuse.getMaxCurrent() + " A\nThe fuse has blown.");
                            alert.showAndWait();

                            return;
                        }
                    }
                }
            }

            if (totalCurrent < 0.5) {
                speedFactor = 500;
            } else if (totalCurrent < 2 && totalCurrent > 0.5) {
                speedFactor = 1000.0;
            } else {
                speedFactor = 10000.0;
            }

            for (LinkedList<Component> list : circuit.arrayList) {
                for (Component c : list) {
                    if (c instanceof Wire wire) {
                        for (int i = 0; i < dotsPerWire; i++) {
                            Electron e = new Electron(wire, speedFactor);
                            e.progress = i / (double) dotsPerWire;
                            animatedElectrons.add(e);
                        }
                    }
                }
            }
        }
    }

    public void stopElectronAnimation() {
        if (!(canvas.getParent() instanceof Pane parent)) {
            return;
        }

        for (PathTransition transition : activeTransitions) {
            transition.stop();
            parent.getChildren().remove(transition.getNode()); // remove the moving dot
        }
        activeTransitions.clear(); // Clear the active transitions
    }

    public void setZoom(double newZoom) {
        this.zoom = newZoom;
        squareSize = 20 * newZoom; // Adjust square size based on zoom
    }

    public void zoomOut() {
        if (zoom > 0.1) {
            setZoom(zoom - 0.1);
        }
    }

    public void zoomIn() {
        if (zoom < 2) {
            setZoom(zoom + 0.1);
        }
    }

    public double getZoom() {
        return zoom;
    }

    public void toggleGrid() {
        showGrid = !showGrid;
    }

    public void setCircuit(Circuit circuit) {
        this.circuit = circuit;
    }

    public void updateAnimation() {
        if (animateCurrent) {
            // Get the total current from CircuitMath
            CircuitMath circuitMath = new CircuitMath(circuit);
            double totalCurrent = circuitMath.getTotalCurrent();  // Get the total current

            // Update all electrons with the current speed factor
            for (Electron e : animatedElectrons) {
                e.update(totalCurrent);  // Pass the current to the update method
            }
        }
    }

    public void switchView(boolean isDiagram) {
        for (Component component : circuit.toArrayList()) {
            System.out.println(component.display);
            component.switchDisplay(isDiagram);
        }
    }
}
