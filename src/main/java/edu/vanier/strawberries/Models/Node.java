package edu.vanier.strawberries.Models;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Node {
    // COORDINATES
    private double x, y;

    public Node(double x, double y) {
        this.x = x;
        this.y = y;
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

    public static Node copyOf(Node node) {
        return new Node(node.x,node.y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}

