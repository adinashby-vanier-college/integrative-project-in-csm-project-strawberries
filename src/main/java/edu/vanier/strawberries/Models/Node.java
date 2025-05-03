package edu.vanier.strawberries.Models;

import javafx.geometry.Point2D;

public class Node {
    // COORDINATES
    Point2D position;
    private boolean locked,connected;

    public Node(double x, double y) {
        position = new Point2D(x,y);
        connected = false;
    }

    /**
     * Updates the position of the node, only if it is not locked
     * @param newX The new position of x
     * @param newY The new position of y
     */
    public void setPosition(double newX, double newY) {
        if(!locked) {
            position = new Point2D(newX,newY);
        }
    }

    /**
     *
     * @return The 2D coordinates of the node
     */
    public Point2D getPosition() {
        return position;
    }

    /**
     *
     * @return the x coordinate of the node
     */
    public double getX() {
        return position.getX();
    }

    /**
     *
     * @return the y coordinate of the node
     */
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
     * Unlock the node and allow it to change position.
     */
    public void unlock() {
        locked = false;
    }

    /**
     * Marks the node as a connected node
     * @param connected true if the node is connected to another node
     */
    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    /**
     * @return true if the node is connected to another
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Creates a new instance of the {@link Node} class, which has the same properties as the passed node
     * @param node the node to copy
     * @return The new node
     */
    public static Node copyOf(Node node) {
        return new Node(node.getX(), node.getY());
    }

    @Override
    public String toString() {
        return "(" + getX() + ", " + getY() + ")";
    }
}

