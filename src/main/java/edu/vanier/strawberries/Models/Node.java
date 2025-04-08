package edu.vanier.strawberries.Models;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Node {
    // COORDINATES
    private double x, y;
    private final Circle marker;
    private boolean visible;

    public Node(double x, double y) {
        this.x = x;
        this.y = y;
        marker = new Circle(4, Color.WHITE);
    }

    public void setPosition(double newX, double newY) {
        this.x = newX;
        this.y = newY;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public Circle getMarker() {
        return marker;
    }

    public void setMarkerVisible(boolean show) {
        visible = show;
    }

    public static Node copyOf(Node node) {
        return new Node(node.x,node.y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}

