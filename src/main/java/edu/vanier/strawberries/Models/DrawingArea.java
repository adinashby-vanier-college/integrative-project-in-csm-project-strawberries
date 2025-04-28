package edu.vanier.strawberries.Models;

import edu.vanier.strawberries.ui.MainApp;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.animation.PathTransition;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
            gc.setStroke(Color.LIGHTGREY);
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
                    if(node.isConnected()) gc.fillOval(node.getX()-4,node.getY()-4,8,8);
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

        public Electron(Wire wire) {
            this.wire = wire;
            this.progress = 0;
        }

        public void update() {
            progress += 0.01; // Adjust speed here
            if (progress > 1.0) {
                progress = 0; // Loop the electron back to start
            }
        }

        public Point2D getPosition() { //get wire position
            double x = wire.begin.getX() + (wire.end.getX() - wire.begin.getX()) * progress;
            double y = wire.begin.getY() + (wire.end.getY() - wire.begin.getY()) * progress;
            return new Point2D(x, y);
        }
    }


    public void animateCurrentFlow(boolean start) {
        animateCurrent = start;
        animatedElectrons.clear();  // Reset electrons when toggled

        if (start) {
            int dotsPerWire = 5;

            for (LinkedList<Component> list : circuit.arrayList) {
                for (Component c : list) {
                    if (c instanceof Wire wire) {
                        for (int i = 0; i < dotsPerWire; i++) {
                            Electron e = new Electron(wire);
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

    public void updateAnimation() { // update the electrons animation
        if (animateCurrent) {
            for (Electron e : animatedElectrons) {
                e.update();
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
