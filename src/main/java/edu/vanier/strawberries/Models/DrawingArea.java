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
     * Method called every frame. Draws the elements to be displayed in the canvas, such as circuit components
     */
    public void drawContent() {
        gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());

        // Draw the grid
        if(showGrid) {
            switch(MainApp.mainAppFXMLController.getTheme()) {
                case "light-mode" -> gc.setStroke(Color.LIGHTGREY);
                case "dark-mode" -> gc.setStroke(Color.BLACK);
                case "strawberries-theme" -> gc.setStroke(Color.PINK);
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
        for(Component component : circuit.toArrayList()) {
            assert component != null;

            if(component instanceof Wire wire) {
                gc.setLineWidth(3);
                gc.setStroke(wire.getColor());
                gc.strokeLine(component.begin.getX(),component.begin.getY(),component.end.getX(),component.end.getY());
                Node[] nodes = {wire.begin,wire.end};
                for(Node node : nodes) {
                    if(node.isConnected()) {
                        gc.setFill(wire.getColor());
                        gc.fillOval(node.getX()-4,node.getY()-4,8,8);
                    }
                }
                if(wire.selected) {
                    gc.setFill(Color.BLACK);
                    gc.fillOval(wire.begin.getX()-5,wire.begin.getY()-5,10,10);
                    gc.fillOval(wire.end.getX()-5,wire.end.getY()-5,10,10);
                }
            }
            else {
                Image img = component.display;
                Rotate rotateTransform = new Rotate(component.getAngle(),component.begin.getX(),component.begin.getY());
                gc.save();
                gc.setTransform(new Affine(rotateTransform));
                gc.drawImage(img,component.begin.getX(),component.begin.getY()-(img.getHeight())/2,img.getWidth()*zoom,img.getHeight()*zoom);

                gc.setFill(Color.RED);
                gc.fillOval(component.end.getX()-4,component.end.getY()-4,8,8);

                if(component.isEdit()) {
                    // SHOW IF THE COMPONENT IS BEING EDITED
                    gc.setStroke(Color.BLUE);
                    gc.setLineWidth(1.2);
                    gc.strokeRect(component.begin.getX(),component.begin.getY()-(img.getHeight())/2,img.getWidth()*zoom,img.getHeight()*zoom);
                }
                gc.restore();

                if(component instanceof Lightbulb lightbulb && lightbulb.isOn()) {
                    //TODO set opacity based on lightbulb's current voltage
                    //TODO not working for rotation
                    gc.setFill(lightbulb.getColor().deriveColor(0,1,1, 0.5));
                    gc.fillOval(lightbulb.getCenter().getX()-20,lightbulb.getCenter().getY()-20,40,40);
                }
            }
        }
    }

    /**
     * Allows the component nodes to snap to the closed grid intersection when dragging
     * @param pos the un-snapped position of the node
     * @return The updated, snapped position relative to the grid.
     */
    public double snap(double pos) {
        double remainder = pos % (squareSize);
        if (remainder == 0)
            return pos;
        else if (remainder <= (double) squareSize / 2)
            return pos - remainder;
        else
            return pos + (squareSize - remainder);
    }

    /**
     *  INSERT JAVADOC COMMENT HERE
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
        String output = "";
        if (circuitName != null) {
            output = circuitName + ".txt\n";
        }
        output = "unnamedCircuit.txt\n";
        // go through each component and add to final output
        for(Component component : circuit.toArrayList()) {
            assert component != null;
            if (component instanceof Wire wire) {
                Node[] nodes = {wire.begin, wire.end};
                for (Node node : nodes) {
                    if (node.isConnected()) output += "1|";
                }
                gc.setLineWidth(3);
                output += "wire|" + wire.getColor() + "|" + component.begin.getX() + "|" + component.begin.getY() + "|"  + component.end.getX() + "|"  + component.end.getY() + "|" + component.getCurrent() + "|"  + component.getVoltage() + "\n";
            } else {
                output += component.getType() + "|" + component.begin.getX() + "|" + component.begin.getY() + "|" + component.getAngle() + "\n";
            }
        }
        // remove duplicate lines
        StringBuilder builder = new StringBuilder();
        for (String line: new LinkedHashSet<String>(Arrays.asList(output.split("\n"))) ) {
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

        if (!(canvas.getParent() instanceof Pane parent)) return;

        if (start) {
            int dotsPerWire = 5;
            double speedFactor;

            CircuitMath circuitMath = new CircuitMath(circuit);
            double totalCurrent = circuitMath.getTotalCurrent(); // get the total current
            double totalResistance = circuitMath.getTotalResistance(); // get the total resistance
            double totalVoltage = circuitMath.getTotalVoltage(); // get the total voltage

            if (!printedValues) {
                System.out.println("Voltage: " + totalVoltage + "V");
                System.out.println("Resistance: " + totalResistance + "Ω");
                System.out.println("Current: " + totalCurrent + "A");
                printedValues = true;
            }

            // Determine animation speed based on total current
            if (totalCurrent < 100) {
                speedFactor = 0.5;
            } else if (totalCurrent < 600) {
                speedFactor = 5.0;
            } else {
                speedFactor = 10.0;
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
        if (!(canvas.getParent() instanceof Pane parent)) return;

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
        if(zoom > 0.1) setZoom(zoom - 0.1);
    }

    public void zoomIn() {
        if(zoom < 2) setZoom(zoom + 0.1);
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
