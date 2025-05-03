package edu.vanier.strawberries.Models;

import edu.vanier.math.CircuitMath;
import edu.vanier.strawberries.controllers.MainAppFXMLController;
import edu.vanier.strawberries.controllers.SignOnLogInController;
import edu.vanier.strawberries.ui.MainApp;
import javafx.animation.Interpolator;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.animation.PathTransition;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Path;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

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

    public DrawingArea(Canvas canvas) {
        this.canvas = canvas;
        gc = canvas.getGraphicsContext2D();
        setZoom(1);
        showGrid = true;
        drawingTool.setCurrentAction("");
    }

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

    public double snap(double pos) {
        double remainder = pos % (squareSize);
        if (remainder == 0)
            return pos;
        else if (remainder <= (double) squareSize / 2)
            return pos - remainder;
        else
            return pos + (squareSize - remainder);
    }

    //make a list to store the electrons to that r going to get animated
    private final List<Electron> animatedElectrons = new ArrayList<>();
    private boolean animateCurrent = false;

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
                output += "wire|" + wire.getColor() + "|" + component.begin.getX() + "|" + component.begin.getY() + "|"  + component.end.getX() + "|"  + component.end.getY() + "\n";
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

    public static void importFromJson(String username, DrawingArea drawingArea) {
        File jsonFile = new File("src/main/resources/users.json");

        if (!jsonFile.exists()) {
            System.out.println("[DEBUG] JSON file not found.");
            return;
        }

        String recent = MainApp.recentProject;

        if (recent == null || recent.isEmpty()) {
            System.out.println("[DEBUG] No recent project found for user: " + username);
            return;
        }

        String[] lines = recent.split("\n");
        System.out.println("[DEBUG] Total lines read: " + lines.length);

        Circuit circuit = new Circuit(true);  // create new circuit
        ArrayList<Component> components = new ArrayList<>(); // temp component holder

        for (String line : lines) {
            System.out.println("[DEBUG] Processing line: " + line);

            // Remove any prefix before "wire|"
            if (line.matches(".*\\|wire\\|.*")) {
                line = line.substring(line.indexOf("wire|"));
                System.out.println("[DEBUG] Trimmed wire line: " + line);
            }

            if (line.startsWith("wire|")) {
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
                        System.out.println("[DEBUG] Added wire: " + wire);
                    } catch (Exception e) {
                        System.out.println("[ERROR] Failed to parse wire: " + e.getMessage());
                    }
                } else {
                    System.out.println("[DEBUG] Invalid wire format: " + line);
                }
            } else if (line.contains("|")) {
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
                            System.out.println("[DEBUG] Added component: " + component + " at (" + x + "," + y + ") angle: " + angle);
                        }
                    } catch (Exception e) {
                        System.out.println("[ERROR] Failed to parse component: " + e.getMessage());
                    }
                } else {
                    System.out.println("[DEBUG] Invalid component format: " + line);
                }
            }
        }

        System.out.println("[DEBUG] Total components added: " + components.size());

        for (int i = 0; i < components.size(); i++) {
            for (int j = i + 1; j < components.size(); j++) {
                Component a = components.get(i);
                Component b = components.get(j);

                if (a.sharesNode(b)) {
                    int indexA = circuit.getIndex(a);
                    int indexB = circuit.getIndex(b);
                    if (indexA != -1 && indexB != -1) {
                        circuit.addEdge(indexA, indexB);
                        System.out.println("[DEBUG] Connected components: " + a + " & " + b);
                    } else {
                        System.out.println("[DEBUG] Could not get indices for connection");
                    }
                } else {
                    System.out.println("[DEBUG] Components not connected: " + a + " &  " + b);
                }
            }
        }

        drawingArea.setCircuit(circuit);
        System.out.println("[DEBUG] Circuit set on drawingArea");
        drawingArea.drawContent();
        System.out.println("[DEBUG] drawContent() called");

        // Extra circuit state debug
        System.out.println("[DEBUG] Components in circuit:");
        for (Component c : circuit.toArrayList()) {
            System.out.println("  - " + c);
        }

        circuit.print(); // optional debug
        circuit.checkForCycle(); // optional debug

        System.out.println("[DEBUG] Circuit import complete.");
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
