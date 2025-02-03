package com.example.fractal;

public class Node {
    // COORDINATES
    double x, y;

    public Node(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setPosition(double newX, double newY) {
        this.x = newX;
        this.y = newY;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}

