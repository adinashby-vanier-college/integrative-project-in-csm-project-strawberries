package edu.vanier.strawberries.tests;

import edu.vanier.math.CircuitMath;
import edu.vanier.strawberries.Models.Battery;
import edu.vanier.strawberries.Models.Circuit;
import edu.vanier.strawberries.Models.Node;
import edu.vanier.strawberries.Models.Resistor;
import javafx.application.Application;
import javafx.stage.Stage;

public class TestMath extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Build test circuit
        Circuit circuit = new Circuit();

        Resistor r1 = new Resistor(new Node(0, 0), new Node(50, 0), 100);
        Resistor r2 = new Resistor(new Node(60, 0), new Node(110, 0), 200);
        Battery battery = new Battery(new Node(-50, 0), new Node(0, 0), 12);

        circuit.addComponent(battery);
        circuit.addComponent(r1);
        circuit.addComponent(r2);

        circuit.addEdge(0, 1);
        circuit.addEdge(1, 2);

        circuit.print();

        CircuitMath math = new CircuitMath(circuit);

        System.out.println("Total Resistance: " + math.getTotalResistance() + " ohms");
        System.out.println("Total Voltage: " + math.getTotalVoltage() + " V");
        System.out.println("Total Current: " + math.getTotalCurrent() + " A");

        // Exit after test
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args); // <-- This properly initializes JavaFX
    }
}
