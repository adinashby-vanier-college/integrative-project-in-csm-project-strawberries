package com.example.fractal.Components;
import com.example.fractal.Component;
import com.example.fractal.Node;

public class Resistor extends Component {
    public Resistor(Node begin, Node end) {
        super(begin, end);
    }

    @Override
    public void draw() {
        System.out.println("Resistor from " + begin + " to " + end);
    }
}

