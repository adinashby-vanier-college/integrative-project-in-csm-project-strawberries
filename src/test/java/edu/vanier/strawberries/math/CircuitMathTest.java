//package edu.vanier.math;
//
//import edu.vanier.strawberries.Models.*;
//import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;
//
//public class CircuitMathTest {
//
//    @Test
//    public void testSeriesCircuitCalculation() {
//        Circuit circuit = new Circuit(false);
//
//        Battery battery = new Battery(new Node(0, 0), new Node(50, 0), 12, false);
//        Resistor r1 = new Resistor(new Node(50, 0), new Node(100, 0), 100, false);
//        Resistor r2 = new Resistor(new Node(100, 0), new Node(150, 0), 200, false);
//
//        circuit.addComponent(battery); // index 0
//        circuit.addComponent(r1);      // index 1
//        circuit.addComponent(r2);      // index 2
//
//        circuit.addEdge(0, 1);
//        circuit.addEdge(1, 2);
//
//        CircuitMath math = new CircuitMath(circuit);
//
//        assertEquals(300.0, math.getTotalResistance(), 0.001);
//        assertEquals(12.0, math.getTotalVoltage(), 0.001);
//        assertEquals(0.04, math.getTotalCurrent(), 0.001);  // 12V / 300Ω = 0.04A
//    }
//
//    @Test
//    public void testShortCircuitDetected() {
//        Circuit circuit = new Circuit(false);
//        Battery battery = new Battery(new Node(0, 0), new Node(50, 0), 12, false);
//        circuit.addComponent(battery);
//        CircuitMath math = new CircuitMath(circuit);
//
//        assertEquals(0.0, math.getTotalResistance(), 0.001);
//        assertEquals(12.0, math.getTotalVoltage(), 0.001);
//        assertEquals(0.0, math.getTotalCurrent(), 0.001); // Current should be zero or error-handled
//    }
//
//    @Test
//    public void testNoVoltage() {
//        Circuit circuit = new Circuit(false);
//        Resistor resistor = new Resistor(new Node(0, 0), new Node(50, 0), 100, false);
//        circuit.addComponent(resistor);
//        CircuitMath math = new CircuitMath(circuit);
//
//        assertEquals(100.0, math.getTotalResistance(), 0.001);
//        assertEquals(0.0, math.getTotalVoltage(), 0.001);
//        assertEquals(0.0, math.getTotalCurrent(), 0.001);
//    }
//}
