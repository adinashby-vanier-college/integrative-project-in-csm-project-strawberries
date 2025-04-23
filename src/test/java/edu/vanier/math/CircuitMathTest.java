package edu.vanier.math;

public class CircuitMathTest {

    @org.junit.Test
    public void testSimpleSeriesCircuit() {
        edu.vanier.strawberries.Models.Node n1 = new edu.vanier.strawberries.Models.Node(0, 0);
        edu.vanier.strawberries.Models.Node n2 = new edu.vanier.strawberries.Models.Node(1, 0);
        edu.vanier.strawberries.Models.Node n3 = new edu.vanier.strawberries.Models.Node(1, 0);
        edu.vanier.strawberries.Models.Node n4 = new edu.vanier.strawberries.Models.Node(2, 0);

        edu.vanier.strawberries.Models.Battery battery =
            new edu.vanier.strawberries.Models.Battery(n1, n2, 12, true);  // 12V

        edu.vanier.strawberries.Models.Resistor resistor =
            new edu.vanier.strawberries.Models.Resistor(n3, n4, 6, true);   // 6Ω

        edu.vanier.strawberries.Models.Circuit circuit = new edu.vanier.strawberries.Models.Circuit();
        circuit.addComponent(battery);  // index 0
        circuit.addComponent(resistor); // index 1
        circuit.addEdge(0, 1);
        circuit.addEdge(1, 0);

        CircuitMath cm = new CircuitMath(circuit);

        org.junit.Assert.assertEquals(6.0, cm.getTotalResistance(), 0.01);
        org.junit.Assert.assertEquals(12.0, cm.getTotalVoltage(), 0.01);
        org.junit.Assert.assertEquals(2.0, cm.getTotalCurrent(), 0.01);  // I = V / R
    }
}
