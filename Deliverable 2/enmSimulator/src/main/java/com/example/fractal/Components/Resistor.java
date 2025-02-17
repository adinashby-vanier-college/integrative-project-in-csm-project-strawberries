package com.example.fractal.Components;
import com.example.fractal.Component;
import com.example.fractal.Node;

public class Resistor extends Component {
    private double resistance;
    public Resistor(Node begin, Node end, double resistance) {
        super(begin, end);
        this.resistance = resistance;
    }

    public double getResistance() {
        return resistance;
    }
    public void setResistance(double resistance) {
        this.resistance = resistance;
    }

    @Override
    public void draw() {
        System.out.println("Resistor from " + begin + " to " + end);
    }
}

