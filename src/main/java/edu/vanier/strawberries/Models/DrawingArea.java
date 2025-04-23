package edu.vanier.strawberries.Models;

import edu.vanier.strawberries.ui.MainApp;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.animation.PathTransition;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.util.Duration;
import javafx.scene.shape.Circle;

import javax.sound.midi.Transmitter;
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

        // Draw the Components
        for(Component component : circuit.toArrayList()) {
            assert component != null;
            if(component instanceof Wire wire) {
                gc.setLineWidth(3);
                gc.setStroke(wire.getColor());
                gc.strokeLine(component.begin.getX(),component.begin.getY(),component.end.getX(),component.end.getY());

                gc.setFill(Color.RED);
                gc.fillOval(wire.begin.getX()-6,wire.begin.getY()-6,12,12);
                gc.setFill(Color.BLUE);
                gc.fillOval(wire.end.getX()-6,wire.end.getY()-6,12,12);

                if(wire.selected) {
                    gc.setFill(Color.BLACK);
                    gc.fillOval(wire.begin.getX()-4,wire.begin.getY()-4,8,8);
                    gc.fillOval(wire.end.getX()-4,wire.end.getY()-4,8,8);
                }
            }
            else {
                Image img = component.display;
                Rotate rotateTransform = new Rotate(component.getAngle(),component.begin.getX(),component.begin.getY());
                gc.save();
                gc.setTransform(new Affine(rotateTransform));
                gc.drawImage(img,component.begin.getX(),component.begin.getY()-(img.getHeight())/2,img.getWidth()*zoom,img.getHeight()*zoom);

                if(component.isEdit()) {
                    // Show editing display
                    gc.setStroke(Color.BLUE);
                    gc.setLineWidth(1.2);
                    gc.strokeRect(component.begin.getX(),component.begin.getY()-(img.getHeight())/2,img.getWidth()*zoom,img.getHeight()*zoom);
                }
                gc.restore();
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

    public void animateCurrentFlow(boolean start) {
        stopElectronAnimation(); // Stop previous animations before starting a new one

        // Ensure the canvas is inside a Pane (important for animations)
        if (!(canvas.getParent() instanceof Pane parent)) {
            System.err.println("Parent is not a Pane. Cannot animate current.");
            return;
        }

        if (start) {
            // Animation: Draw multiple animated dots along the wire
            for (LinkedList<Component> list : circuit.arrayList) {
                for (Component c : list) {
                    if (c instanceof Wire wire) {
                        // Check if wire has valid coordinates
                        System.out.println("Animating Wire: " + wire.begin + " to " + wire.end);

                        // Draw animated electrons along the wire
                        int dotCount = 5;
                        double durationMillis = 2000; // Duration for full animation

                        Line path = new Line(
                                wire.begin.getX(), wire.begin.getY(),
                                wire.end.getX(), wire.end.getY()
                        );

                        for (int i = 0; i < dotCount; i++) {
                            Circle dot = new Circle(4, Color.RED);
                            parent.getChildren().add(dot);  // Add to the parent container

                            PathTransition transition = new PathTransition();
                            transition.setNode(dot);
                            transition.setPath(path);
                            transition.setDuration(Duration.millis(durationMillis));
                            transition.setDelay(Duration.millis(i * (durationMillis / dotCount)));
                            transition.setCycleCount(PathTransition.INDEFINITE);
                            transition.setAutoReverse(false);
                            transition.play();

                            activeTransitions.add(transition);
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
}
