package edu.vanier.strawberries.tests;

import edu.vanier.math.CircuitMath;
import edu.vanier.strawberries.*;
import edu.vanier.strawberries.Components.*;

public class TestMath {
    //this is just to check the math.
    public static void main(String[] args) {
        // Create a fake circuit
        Circuit circuit = new Circuit();

        // Add components manually
        Resistor r1 = new Resistor(new Node(0, 0), new Node(50, 0), 100); // 100 ohm
        Resistor r2 = new Resistor(new Node(60, 0), new Node(110, 0), 200); // 200 ohm
        Battery battery = new Battery(new Node(-50, 0), new Node(0, 0), 12); // 12V

        // Add them to the circuit
        circuit.addComponent(battery); // index 0
        circuit.addComponent(r1);      // index 1
        circuit.addComponent(r2);      // index 2

        // Connect them in series
        circuit.addEdge(0, 1); // battery → resistor 1
        circuit.addEdge(1, 2); // resistor 1 → resistor 2

     
        circuit.print();

     
        CircuitMath math = new CircuitMath(circuit);

        System.out.println("Total Resistance: " + math.getTotalResistance() + " ohms");
        System.out.println("Total Voltage: " + math.getTotalVoltage() + " V");
        System.out.println("Total Current: " + math.getTotalCurrent() + " A");
    }
}
