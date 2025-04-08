package edu.vanier.strawberries.Models;


import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.animation.PathTransition;
import javafx.scene.shape.Line;
import javafx.scene.transform.Transform;
import javafx.util.Duration;
import javafx.scene.shape.Circle;

import java.util.LinkedList;

public class DrawingArea {
    public DrawingTool drawingTool = new DrawingTool();
    private Component selection;
    public Circuit circuit;
    public Canvas canvas;
    public GraphicsContext gc;
    private double zoom;
    private double squareSize;

    public DrawingArea(Canvas canvas) {
        this.canvas = canvas;
        gc = canvas.getGraphicsContext2D();
        setZoom(1);

        drawingTool.setCurrentAction("");
    }

    public void drawContent() {
        gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());

        gc.fillRect(0,0,squareSize,squareSize);

        //Draw the grid ---- WORKS OK BUT VERY NON-OPTIMAL
        gc.setStroke(Color.LIGHTGREY);
        gc.setLineWidth(1);
        for(int i=0;i<canvas.getWidth();i+= (int) (squareSize*zoom)) {
            for(int j=0;j<canvas.getHeight();j+= (int) (squareSize*zoom)) {
                gc.strokeRect(i,j,squareSize*zoom,squareSize*zoom);
            }
        }

        //Draw the Components
        for(Component component : circuit.toArrayList()) {
            if(component instanceof Wire) {
                gc.setLineWidth(3);
                gc.setStroke(((Wire) component).getColor());
                gc.strokeLine(component.begin.getX(),component.begin.getY(),component.end.getX(),component.end.getY());
            }
            else {
                if(component.display.getTransforms()!=null) {
                    Transform transform = component.display.getTransforms().getLast();
                    double minX = Math.min(component.begin.getX(),component.end.getX());
                    double minY = Math.min(component.begin.getY(),component.end.getY());
                    gc.setTransform(transform.getMxx(), transform.getMyx(), transform.getMxy(), transform.getMyy(), transform.getMxz(),transform.getMyz());
                    gc.drawImage(component.display.getImage(),minX,minY);
                }
            }
        }
    }

    //TODO fix this function
    public double snap(double pos) {
        double remainder = pos%(squareSize);
        if(remainder< (double) squareSize/2) return pos-remainder;
        else return pos+remainder;
    }

    private void unselectAll() {
        for(LinkedList<Component> ll : circuit.arrayList) {
            for(Component current : ll) {
                current.markAsSelected(false);
                current.draw();
            }
        }
    }

    public void animateCurrentFlow(boolean b) {
        for (LinkedList<Component> list : circuit.arrayList) {
            for (Component c : list) {
                if (c instanceof Wire wire) {
                    // TODO: Only animate wires where wire.hasCurrent() returns true
                    // if (!wire.hasCurrent()) continue;

                    Circle currentDot = new Circle(5, Color.RED);
//                    canvas.getGraphicsContext2D().fillOval();
//                    canvas.getChildren().add(currentDot);

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

    public void setCircuit(Circuit circuit) {
        this.circuit = circuit;
    }
}


