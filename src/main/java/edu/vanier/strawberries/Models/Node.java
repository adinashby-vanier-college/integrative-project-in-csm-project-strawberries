package edu.vanier.strawberries.Models;

import javafx.geometry.Point2D;

public class Node {
    // COORDINATES
    Point2D position;
    private boolean locked;

    public Node(double x, double y) {
        position = new Point2D(x,y);
    }

    public void setPosition(double newX, double newY) {
        if(!locked) {
            position = new Point2D(newX,newY);
        }
    }

    public Point2D getPosition() {
        return position;
    }

    public double getX() {
        return position.getX();
    }

    public double getY() {
        return position.getY();
    }

    /**
     * Locks the node at its current position to avoid accidental movement.
     */
    public void lock() {
        locked = true;
    }

    /**
     * Unlock the node and allow it to change position again.
     */
    public void unlock() {
        locked = false;
    }

    public static Node copyOf(Node node) {
        return new Node(node.getX(), node.getY());
    }

    @Override
    public String toString() {
        return "(" + getX() + ", " + getY() + ")";
    }
}

