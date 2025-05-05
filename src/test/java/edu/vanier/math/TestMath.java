package edu.vanier.math;

import edu.vanier.strawberries.Models.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestMath {

    @Test
   // tests the math for resistance in a circuit
    public void testSeriesCircuitCalculation() {
        Circuit circuit = new Circuit(false);

        Battery battery = Battery.createForTest(new Node(0, 0), new Node(50, 0), 12);
        Resistor r1 = Resistor.createForTest(new Node(50, 0), new Node(100, 0), 100);
        Resistor r2 = Resistor.createForTest(new Node(100, 0), new Node(150, 0), 200);

        circuit.addComponent(battery);
        circuit.addComponent(r1);
        circuit.addComponent(r2);

        circuit.addEdge(0, 1);
        circuit.addEdge(1, 2);

        CircuitMath math = new CircuitMath(circuit);

        assertEquals(300.0, math.getTotalResistance(), 0.001);
        assertEquals(12.0, math.getTotalVoltage(), 0.001);
        assertEquals(0.04, math.getTotalCurrent(), 0.001);  // 12V / 300Ω = 0.04A
    }

    @Test
    //tesitn gout the short circuit logic
    public void testShortCircuitDetected() {
        Circuit circuit = new Circuit(false);
      Battery battery = Battery.createForTest(new Node(0, 0), new Node(50, 0), 12);

        circuit.addComponent(battery);

        CircuitMath math = new CircuitMath(circuit);

        assertEquals(0.0, math.getTotalResistance(), 0.001);
        assertEquals(12.0, math.getTotalVoltage(), 0.001);
        assertEquals(0.0, math.getTotalCurrent(), 0.001);
    }
    
    
  
@Test
//testing out if the fuse logic works
public void testFuseBlowsWhenOverloaded() {
    Circuit circuit = new Circuit(false);

    Battery battery = Battery.createForTest(new Node(0, 0), new Node(50, 0), 12);
    Resistor r1 = Resistor.createForTest(new Node(50, 0), new Node(100, 0), 10); // Very low resistance → high current
    Fuse fuse = Fuse.createForTest(new Node(100, 0), new Node(150, 0), 0.5); // Max 0.5 A

    circuit.addComponent(battery);
    circuit.addComponent(r1);
    circuit.addComponent(fuse);

    circuit.addEdge(0, 1);
    circuit.addEdge(1, 2);

    CircuitMath math = new CircuitMath(circuit);
    double current = math.getTotalCurrent();

    fuse.updateState(current);

    assertTrue(fuse.isBlown(), "Fuse should blow with current = " + current);
}

}
