package edu.vanier.strawberries;


import edu.vanier.strawberries.Components.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.animation.PathTransition;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import javafx.scene.shape.Circle;
import java.util.Stack;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

public class DrawingArea extends Pane {
    public DrawingTool drawingTool = new DrawingTool();
    private Component selection;
    public Circuit circuit;
    public Pane pane;

    public DrawingArea(Pane pane) {
        setPadding(new Insets(10, 10, 10, 10));
//        setBackground(Background.fill(Color.RED));

        // SET UP EVENT LISTENERS
        pane.addEventHandler(MouseEvent.MOUSE_PRESSED, this::mousePressed);
        pane.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::mouseDragged);
        pane.addEventHandler(MouseEvent.MOUSE_RELEASED, this::mouseReleased);
        this.pane = pane;

        circuit = new Circuit();
        drawingTool.setCurrentAction("");
    }

  private void mousePressed(MouseEvent event) {
    if (!Objects.equals(drawingTool.getCurrentAction(), "")) {
        drawingTool.setPencilDown(true);
        Node eventLocation = new Node(event.getX(), event.getY());
        Node tempEnd = new Node(event.getX(), event.getY());
        switch (drawingTool.getCurrentAction()) {
            case "place-wire" -> selection = new Wire(eventLocation, tempEnd, drawingTool.defaultColor, 0, 0);
            case "place-battery" -> selection = new Battery(eventLocation, tempEnd, 12);
            case "place-capacitor" -> selection = new Capacitor(eventLocation, tempEnd, 0, true, false);
            case "place-fuse" -> selection = new Fuse(eventLocation, tempEnd);
            case "place-lightbulb" -> selection = new Lightbulb(eventLocation, tempEnd);
            case "place-resistor" -> selection = new Resistor(eventLocation, tempEnd, 100);
            case "place-switch" -> selection = new Switch(eventLocation, tempEnd, false);
        }

        selection.setX(selection.begin.getX());
        selection.setY(selection.begin.getY());
        pane.getChildren().add(selection);
        circuit.addComponent(selection);
        attemptConnection(selection, selection.begin);
        selection.draw();
        circuit.print();
    } else {
        
        for (LinkedList<Component> list : circuit.arrayList) {
            Component comp = list.getFirst();

            if (comp.getBoundsInParent().contains(event.getX(), event.getY())) {
                if (comp instanceof Wire wire) {
                    wire.handleEdit(event);
                } else if (comp instanceof Battery battery) {
                    battery.handleEdit(event); 
                } else if (comp instanceof Resistor resistor) {
                 
                    System.out.println("Resistor clicked: V = " + resistor.getVoltage() + ", I = " + resistor.getCurrent());
                }
                break;
            }
        }
    }
}

    private void mouseDragged(MouseEvent event) {
        if (drawingTool.isPencilDown() && selection != null) {
            selection.moveNode(selection.end, event.getX(), event.getY());
            selection.draw();
        }
    }

    private void mouseReleased(MouseEvent event) {
        if (selection != null) {
            if (drawingTool.isPencilDown()) {
                drawingTool.setPencilDown(false);
                attemptConnection(selection, selection.end);

                // Enable dragging and rotating if it's draggable
                if (selection instanceof Battery battery) {
                    battery.enableDragAndRotate();
                } else if (selection instanceof Switch sw) {
                    sw.enableDragAndRotate(); // Do the same for others
                }

                selection = null;
            }
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

            
            if (connectedComponent instanceof Battery battery) {
                battery.snapNearbyNode(node); 
            }
            Point2D componentBegin = new Point2D(connectedComponent.begin.getX(), connectedComponent.begin.getY());
            Point2D componentEnd = new Point2D(connectedComponent.end.getX(), connectedComponent.end.getY());

            if (componentBegin.distance(checkPoint) <= 20) {
                connectedNodes.add(connectedComponent.begin);
            }
            if (componentEnd.distance(checkPoint) <= 20) {
                connectedNodes.add(connectedComponent.end);
            }

            for (int i = 1; i < connectedNodes.size(); i++) {
                if (!circuit.checkEdge(srcIndex, dstIndex)) circuit.addEdge(srcIndex, dstIndex);
                connectedNodes.get(i).setPosition(node.getX(), node.getY());
            }

            connectedComponent.draw();
        }
    }

    toCheck.draw();
}


    private void editHandler(MouseEvent event) {

    }

    public void animateCurrentFlow(boolean b) {
        for (LinkedList<Component> list : circuit.arrayList) {
            for (Component c : list) {
                if (c instanceof Wire wire) {
                    // TODO: Only animate wires where wire.hasCurrent() returns true
                    // if (!wire.hasCurrent()) continue;

                    Circle currentDot = new Circle(5, Color.RED);
                    pane.getChildren().add(currentDot);

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
            }


