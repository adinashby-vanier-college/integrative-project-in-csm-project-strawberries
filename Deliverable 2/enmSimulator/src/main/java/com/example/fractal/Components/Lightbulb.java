package com.example.fractal.Components;
import com.example.fractal.Component;
import com.example.fractal.Node;

public class Lightbulb extends Component {
    public Lightbulb(Node begin, Node end) {
        super(begin, end);
    }

    public void draw() {
        // Implement the drawing logic here
        System.out.println("Drawing a Lightbulb ??  ");
    }
}