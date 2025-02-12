package com.example.fractal.Components;
import com.example.fractal.Component;
import com.example.fractal.Node;

public class Capacitor extends Component {
    public Capacitor(Node begin, Node end) {
        super(begin, end);
    }

    @Override
    public void draw() {
        System.out.println("Capacitor from " + begin + " to " + end);
    }
}