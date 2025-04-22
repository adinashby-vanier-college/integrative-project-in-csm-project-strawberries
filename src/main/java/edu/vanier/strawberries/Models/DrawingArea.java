package edu.vanier.strawberries.Models;


import edu.vanier.strawberries.ui.MainApp;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
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
import java.util.LinkedList;

public class DrawingArea {
    public DrawingTool drawingTool = new DrawingTool();
    private Component selection;
    public Circuit circuit;
    public Canvas canvas;
    public GraphicsContext gc;
    private double zoom;
    private double squareSize;
    private boolean showGrid;

    public DrawingArea(Canvas canvas) {
        this.canvas = canvas;
        gc = canvas.getGraphicsContext2D();
        setZoom(1);
        drawingTool.setCurrentAction("");
    }

    public void drawContent() {
        gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());

        //Draw the grid
        if(showGrid) {
            gc.setStroke(Color.LIGHTGREY);
            gc.setLineWidth(1);
            for (int i = 0; i < canvas.getWidth(); i += (int) (squareSize * zoom)) {
                for (int j = 0; j < canvas.getHeight(); j += (int) (squareSize * zoom)) {
                    gc.strokeRect(i, j, squareSize * zoom, squareSize * zoom);
                }
            }
        }

        //Draw the Components
        //TODO add smth to show when a component is selected (borders or node markers)
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
                    //show editing display
                    gc.setStroke(Color.BLUE);
                    gc.setLineWidth(1.2);
                    gc.strokeRect(component.begin.getX(),component.begin.getY()-(img.getHeight())/2,img.getWidth()*zoom,img.getHeight()*zoom);
                }
                gc.restore();
            }
        }
    }

    public double snap(double pos) {
        double remainder = pos%(squareSize);

        if(remainder == 0)
            return pos;
        else if(remainder <= (double) squareSize/2)
            return pos - remainder;
        else
            return pos + (squareSize - remainder);
    }

    public void animateCurrentFlow(boolean b) {
        for (LinkedList<Component> list : circuit.arrayList) {
            for (Component c : list) {
                if (c instanceof Wire wire) {
                    // TODO: Only animate wires where wire.hasCurrent() returns true
                    // if (!wire.hasCurrent()) continue;

                    Circle currentDot = new Circle(5, Color.RED);
                    gc.fillOval(wire.begin.getX(),wire.begin.getY(),10,10);

                    Line path = new Line(
                        wire.begin.getX(), wire.begin.getY(),
                        wire.end.getX(), wire.end.getY()
                    );

                    PathTransition transition = new PathTransition();
                    transition.setNode(currentDot);
                    transition.setPath(path);
                    transition.setDuration(Duration.seconds(1));
                    transition.setCycleCount(PathTransition.INDEFINITE);
                    transition.setAutoReverse(false);
                    transition.play();

                } else {
                    // TODO: Stop the transition and remove the animated dot
                }
            }
        }
    }

    public void setZoom(double newZoom) {
        this.zoom = newZoom;
        squareSize = 20*newZoom;
    }

    public void zoomOut() {
        if(zoom>0.1) setZoom(zoom-0.1);
    }

    public void zoomIn() {
        if(zoom<2) setZoom(zoom+0.1);
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


