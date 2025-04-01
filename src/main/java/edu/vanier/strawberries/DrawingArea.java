package edu.vanier.strawberries;


import edu.vanier.strawberries.Components.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

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
        if(!Objects.equals(drawingTool.getCurrentAction(),"")) {
            drawingTool.setPencilDown(true);
            Node eventLocation = new Node(event.getX(), event.getY());
            Node tempEnd = new Node(event.getX(), event.getY());
            switch (drawingTool.getCurrentAction()) {
                case "place-wire" -> selection = new Wire(eventLocation, tempEnd, drawingTool.defaultColor, 0, 0);
                case "place-battery" -> selection = new Battery(eventLocation,tempEnd, 12);
                case "place-capacitor" -> selection = new Capacitor(eventLocation,tempEnd,0,true,false);
                case "place-fuse" -> selection = new Fuse(eventLocation,tempEnd);
                case "place-lightbulb" -> selection = new Lightbulb(eventLocation,tempEnd);
                case "place-resistor" -> selection = new Resistor(eventLocation,tempEnd,100);
                case "place-switch" -> selection = new Switch(eventLocation,tempEnd,false);
            }
            selection.setX(selection.begin.getX());
            selection.setY(selection.begin.getY());
            pane.getChildren().add(selection);
            circuit.addComponent(selection);
            attemptConnection(selection, selection.begin);
            selection.draw();
        }
    }

    private void mouseDragged(MouseEvent event) {
        if (drawingTool.isPencilDown() && selection != null) {
            selection.moveNode(selection.end, event.getX(), event.getY());
            selection.draw();
        }
    }

    private void mouseReleased(MouseEvent event) {
        if (drawingTool.isPencilDown()) {
            drawingTool.setPencilDown(false);
            attemptConnection(selection, selection.end);
            selection = null;
        }
    }

    private void attemptConnection(Component toCheck, Node node) {
        int srcIndex = circuit.getIndex(toCheck);
        Point2D checkPoint = new Point2D(node.getX(),node.getY());
        ArrayList<Node> connectedNodes = new ArrayList<>();
        connectedNodes.add(node);

        for(LinkedList<Component> currentList:circuit.arrayList) {
            for(Component connectedComponent:currentList) {
                int dstIndex = circuit.getIndex(connectedComponent);

                //Check edge and update drawings
                Point2D componentBegin = new Point2D(connectedComponent.begin.getX(), connectedComponent.begin.getY());
                Point2D componentEnd = new Point2D(connectedComponent.end.getX(), connectedComponent.end.getY());
                if(componentBegin.distance(checkPoint)<=20) {
                    connectedNodes.add(connectedComponent.begin);
                }
                if(componentEnd.distance(checkPoint)<=20) {
                    connectedNodes.add(connectedComponent.end);
                }

                for(int i=1;i<connectedNodes.size();i++) {
                    if (!circuit.checkEdge(srcIndex, dstIndex)) circuit.addEdge(srcIndex, dstIndex);
                    connectedNodes.get(i).setPosition(node.getX(), node.getY());
                }
                connectedComponent.draw();
            }
        }
        toCheck.draw();
    }
}
