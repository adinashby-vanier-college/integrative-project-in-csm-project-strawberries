package com.example.fractal.Components;

import com.example.fractal.Component;
import com.example.fractal.Node;

public class Resistor extends Component {
    private double resistance; // The resistance value 
    private double current;    // The current flowing through the resistor

    // Constructor with resistance value
    public Resistor(Node begin, Node end, double resistance) {
        super(begin, end);
        this.resistance = resistance;
        this.current = 0; // Initial current is 0
    }

    // Getter for resistance
    public double getResistance() {
        return resistance;
    }

    // Setter for resistance
    public void setResistance(double resistance) {
        this.resistance = resistance;
    }

    // Getter for current
    public double getCurrent() {
        return current;
    }


    public void calculateCurrent(double voltage) {
        if (resistance != 0) this.current = voltage / resistance;
        else this.current = 0;
    }

    @Override
    public void draw() {
        System.out.println("Resistor from " + begin + " to " + end + " with resistance: " + resistance + "Ω");
    }
}
