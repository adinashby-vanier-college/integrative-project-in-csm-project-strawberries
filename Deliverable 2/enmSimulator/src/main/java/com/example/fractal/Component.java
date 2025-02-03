package com.example.fractal;

public abstract class Component {
    // begin ->  o-----^v^v^v-----o  <- end
    protected Node begin;
    protected Node end;

    public Component(Node begin, Node end) {
        this.begin = begin;
        this.end = end;
    }

    // MOVE one node (the other is pivot)
    public void moveNode(Node movingNode, double newX, double newY) {
        if (movingNode == begin) {
            begin.setPosition(newX, newY);
        } else if (movingNode == end) {
            end.setPosition(newX, newY);}
    }


    // ABSTRACT methods for each component type
    public abstract String getSymbol(); // SYMBOL
    public abstract void draw(); // SHOW/UPDATE
}

